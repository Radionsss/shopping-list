package com.example.shoppinglist.fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.shoppinglist.activities.MainApp
import com.example.shoppinglist.activities.ShopListActivity
import com.example.shoppinglist.adapters.ShopListNameAdapter
import com.example.shoppinglist.databinding.FragmentShopListNamesBinding
import com.example.shoppinglist.db.MainViewModel
import com.example.shoppinglist.dialogs.DeleteDialog
import com.example.shoppinglist.dialogs.NewListDialog
import com.example.shoppinglist.entitie.ShoppingListName
import com.example.shoppinglist.utils.TimeManager
import java.text.SimpleDateFormat
import java.util.*


class ShopListNamesFragment : BaseFragment(), ShopListNameAdapter.ShopListNameListener{
    private lateinit var binding: FragmentShopListNamesBinding
    private lateinit var adapter: ShopListNameAdapter

    private val mainViewModel: MainViewModel by activityViewModels {
        MainViewModel.MainViewModelFactory((context?.applicationContext as MainApp).database)
    }


    override fun onClickNew() {
        NewListDialog.showDialog(activity as AppCompatActivity,"",
            object : NewListDialog.ListenerDialog {
                override fun onClick(name: String) {
                    val shoppingListName = ShoppingListName(
                        null,
                        name,
                        TimeManager.getTime(),
                        0,
                        0,
                        ""
                    )
                    mainViewModel.insertShopListName(shoppingListName)
                }
            })
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRcView()
        observe()
    }

    private fun initRcView() = with(binding) {
        rcViewShopList.layoutManager= LinearLayoutManager(activity)
        adapter= ShopListNameAdapter(this@ShopListNamesFragment)
        rcViewShopList.adapter=adapter
    }

    private fun observe() {
        mainViewModel.allShoppingListNames.observe(viewLifecycleOwner) {
            adapter.submitList(it)
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = ShopListNamesFragment()
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentShopListNamesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun deleteItem(id: Int) {
        DeleteDialog.showDialog(context as AppCompatActivity ,object :DeleteDialog.ListenerDialog{
            override fun onClick() {
                mainViewModel.deleteShopList(id,true)
            }
        } )
    }

    override fun editItem(shopListName: ShoppingListName) {
        NewListDialog.showDialog(activity as AppCompatActivity,shopListName.name,
            object : NewListDialog.ListenerDialog {
                override fun onClick(name: String) {
                    mainViewModel.updateShopListName(shopListName.copy(name = name))
                }
            })
    }

    override fun onClickItem(shopListName: ShoppingListName) {
        val intent=Intent(activity,ShopListActivity::class.java).apply {
            putExtra(ShopListActivity.SHOP_LIST_NAME_KEY,shopListName)
        }
        startActivity(intent)
    }
}