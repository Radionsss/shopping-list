package com.example.shoppinglist.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ShopListItemBinding
import com.example.shoppinglist.databinding.ShopListLibraryItemBinding
import com.example.shoppinglist.entitie.ShoppingListItem

class ShopListItemAdapter(private val listener: ShopListItemListener) :
    ListAdapter<ShoppingListItem, ShopListItemAdapter.ItemHolder>(ItemComparator()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return if (viewType == 0)
            ItemHolder.createShopItem(parent)
        else
            ItemHolder.createLibraryItem(parent)
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        if (getItem(position).itemType == 0) {
            holder.setItemData(getItem(position), listener)
        } else {
            holder.setLibraryData(getItem(position), listener)
        }
    }

    override fun getItemViewType(position: Int): Int {
        return getItem(position).itemType
    }

    class ItemHolder(val view: View) : RecyclerView.ViewHolder(view) {

        fun setItemData(shoppingListItem: ShoppingListItem, listener: ShopListItemListener) {
            val binding=ShopListItemBinding.bind(view)
            binding.apply {
                tvName.text=shoppingListItem.name
                tvInfo.text=shoppingListItem.itemInfo

                tvInfo.visibility=tvInfoState(shoppingListItem)
                chItem.isChecked=shoppingListItem.itemChecked
                setPaintFlagAndColor(binding)
                chItem.setOnClickListener{
                    listener.onClickItem(shoppingListItem.copy(itemChecked = chItem.isChecked),CHECK_BOX)
                }
                imEdit.setOnClickListener {
                    listener.onClickItem(shoppingListItem, EDIT)
                }
            }
        }

        private fun setPaintFlagAndColor(binding: ShopListItemBinding)= with(binding){
            if(chItem.isChecked) {
                tvName.paintFlags=Paint.STRIKE_THRU_TEXT_FLAG
                tvInfo.paintFlags=Paint.STRIKE_THRU_TEXT_FLAG
                tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey_dark))
                tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey_back))
            }else{
                tvName.paintFlags=Paint.ANTI_ALIAS_FLAG
                tvInfo.paintFlags=Paint.ANTI_ALIAS_FLAG
                tvName.setTextColor(ContextCompat.getColor(binding.root.context, R.color.black))
                tvInfo.setTextColor(ContextCompat.getColor(binding.root.context, R.color.grey_back))
            }
        }

        private fun tvInfoState(shoppingListItem: ShoppingListItem):Int{
            return if(shoppingListItem.itemInfo.isNullOrEmpty()){
                View.GONE
            }else{
                View.VISIBLE
            }
        }

        fun setLibraryData(shoppingListItem: ShoppingListItem, listener: ShopListItemListener) {
            val binding=ShopListLibraryItemBinding.bind(view)
            binding.apply {
                tvNameLibrary.text=shoppingListItem.name
            }
        }

        companion object {
            fun createShopItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shop_list_item, parent, false)
                )
            }

            fun createLibraryItem(parent: ViewGroup): ItemHolder {
                return ItemHolder(
                    LayoutInflater.from(parent.context)
                        .inflate(R.layout.shop_list_library_item, parent, false)
                )
            }
        }
    }

    class ItemComparator : DiffUtil.ItemCallback<ShoppingListItem>() {
        override fun areItemsTheSame(
            oldItem: ShoppingListItem,
            newItem: ShoppingListItem
        ): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(
            oldItem: ShoppingListItem,
            newItem: ShoppingListItem
        ): Boolean {
            return oldItem == newItem
        }
    }

    interface ShopListItemListener {
        fun onClickItem(shopListItem: ShoppingListItem,state:Int)
    }

    companion object{
        const val EDIT=0
        const val CHECK_BOX=1
    }
}