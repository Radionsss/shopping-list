package com.example.shoppinglist.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.EditText
import androidx.activity.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.R
import com.example.shoppinglist.adapters.ShopListItemAdapter
import com.example.shoppinglist.databinding.ActivityShopListBinding
import com.example.shoppinglist.db.MainViewModel
import com.example.shoppinglist.dialogs.EditListItemDialog
import com.example.shoppinglist.entitie.ShoppingListItem
import com.example.shoppinglist.entitie.ShoppingListName
import com.example.shoppinglist.utils.ShareHelper

class ShopListActivity : AppCompatActivity(),ShopListItemAdapter.ShopListItemListener {
    private lateinit var binding: ActivityShopListBinding
    private var shopListName: ShoppingListName? = null
    private lateinit var saveItem: MenuItem
    private lateinit var edItem: EditText
    private lateinit var adapter:ShopListItemAdapter
    private lateinit var textWatcher: TextWatcher

    private val mainViewModel: MainViewModel by viewModels {
        MainViewModel.MainViewModelFactory((applicationContext as MainApp).database)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityShopListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        initRcView()
        listItemObserver()
        actionBarSetting()
        textWatcher= textWatcher()
    }

    private fun textWatcher():TextWatcher{
        return object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                Log.d("MyLog","text: $s")
                mainViewModel.getAllLibraryItems("%$s%")
            }

            override fun afterTextChanged(s: Editable?) {}
        }
    }

    private fun init() {
        shopListName = intent.getSerializableExtra(SHOP_LIST_NAME_KEY) as ShoppingListName
    }

    private fun initRcView() = with(binding) {
        rcViewShopItem.layoutManager= LinearLayoutManager(this@ShopListActivity)
        adapter= ShopListItemAdapter(this@ShopListActivity)
        rcViewShopItem.adapter=adapter
    }

    private fun listItemObserver(){
        mainViewModel.getAllItemsFromList(shopListName?.id!!).observe(this){
            adapter.submitList(it)
            binding.tvEmpty.visibility = if(it.isEmpty()){
                View.VISIBLE
            }else{
                View.GONE
            }
        }
    }

    private fun libraryItemObserver() {
        mainViewModel.libraryItems.observe(this) {
            val tempShopList = ArrayList<ShoppingListItem>()
            it.forEach { item ->
                val shopItem = ShoppingListItem(
                    item.id,
                    item.name,
                    "",
                    false,
                    0,
                    1
                )
                tempShopList.add(shopItem)
            }
            adapter.submitList(tempShopList)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.shop_list_menu, menu)
        saveItem = menu?.findItem(R.id.menu_save_item)!!
        val newItem = menu.findItem(R.id.menu_new_item)!!
        edItem = newItem.actionView?.findViewById(R.id.ed_new_shop_item) as EditText
        newItem.setOnActionExpandListener(expandActionView())
        saveItem.isVisible = false
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_save_item -> addNewShopItem()
            android.R.id.home -> finish()
            R.id.menu_delete_list->{
                mainViewModel.deleteShopList(shopListName?.id!!,true)
                finish()
            }
            R.id.menu_clear_list->{
                mainViewModel.deleteShopList(shopListName?.id!!,false)
            }
            R.id.menu_share_list->{
                startActivity(Intent.createChooser(ShareHelper.shareShopList(adapter.currentList,shopListName?.name!!),"Share by"))
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun addNewShopItem(){
        if (edItem.text.toString().isEmpty())return
        val item= ShoppingListItem(
            null,
            edItem.text.toString(),
            "",
            false,
            shopListName?.id!!,
            0
        )
        edItem.setText("")
        mainViewModel.insertItem(item)
    }

       override fun onClickItem(shopListItem: ShoppingListItem,state:Int) {
           when (state) {
               ShopListItemAdapter.EDIT->showEditDialog(shopListItem)
               ShopListItemAdapter.CHECK_BOX -> mainViewModel.updateItem(shopListItem)
           }
       }

    private fun showEditDialog(shopListItem: ShoppingListItem){
        EditListItemDialog.showDialog(this,shopListItem,object :EditListItemDialog.ListenerDialog{
            override fun onClick(item: ShoppingListItem) {
                mainViewModel.updateItem(item)
            }

        })
    }


    private fun expandActionView(): MenuItem.OnActionExpandListener {
        return object : MenuItem.OnActionExpandListener {
            override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                saveItem.isVisible = true
                edItem.addTextChangedListener(textWatcher)
                libraryItemObserver()
                mainViewModel.getAllItemsFromList(shopListName?.id!!).removeObservers(this@ShopListActivity)
                mainViewModel.getAllLibraryItems("%%")
                return true
            }

            override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                saveItem.isVisible = false
                edItem.removeTextChangedListener(textWatcher)
                invalidateOptionsMenu()
                mainViewModel.libraryItems.removeObservers (this@ShopListActivity)
                edItem.setText("")
                listItemObserver()
                return true
            }
        }
    }

    private fun actionBarSetting() {
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    companion object {
        const val SHOP_LIST_NAME_KEY = "shopList_key"
    }
}