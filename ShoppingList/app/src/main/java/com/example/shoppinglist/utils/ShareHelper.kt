package com.example.shoppinglist.utils

import android.content.Intent
import com.example.shoppinglist.entitie.ShoppingListItem

object ShareHelper {

    fun shareShopList(shopList: List<ShoppingListItem>, listName: String): Intent {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.apply {
            putExtra(Intent.EXTRA_TEXT, makeShareText(shopList, listName))
        }
        return intent
    }

    private fun makeShareText(shopList: List<ShoppingListItem>, listName: String): String {
        val stringBuilder = StringBuilder()
        stringBuilder.append("<<$listName>>")
        stringBuilder.append("\n")
        var counter = 0
        shopList.forEach {
            stringBuilder.append("${++counter} - ${it.name} (${it.itemInfo})")
            stringBuilder.append("\n")
        }
        return stringBuilder.toString()
    }
}