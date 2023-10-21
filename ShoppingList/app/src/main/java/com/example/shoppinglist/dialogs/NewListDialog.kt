package com.example.shoppinglist.dialogs


import android.content.Context
import android.provider.Settings.Global.getString
import android.view.LayoutInflater
import androidx.appcompat.app.AlertDialog
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.NewListDialogBinding

object NewListDialog {
    fun showDialog(context: Context,listName:String, listener: ListenerDialog) {
        var dialog: AlertDialog? = null
        val builder = AlertDialog.Builder(context)
        val binding = NewListDialogBinding.inflate(LayoutInflater.from(context))
        builder.setView(binding.root)
        binding.apply {
            edListName.setText(listName)
            if(listName.isNotEmpty()){
                btnNewList.setText(R.string.rename)
                tvTitle.text=context.getString(R.string.rename_list)
            }
            btnNewList.setOnClickListener {
                val tempListName = edListName.text.toString()
                if (tempListName.isNotEmpty()) {
                    listener.onClick(tempListName)
                }
                dialog?.dismiss()
            }
        }
        dialog = builder.create()
        dialog.window?.setBackgroundDrawable(null)
        dialog.show()
    }

    interface ListenerDialog {
        fun onClick(name:String)
    }
}