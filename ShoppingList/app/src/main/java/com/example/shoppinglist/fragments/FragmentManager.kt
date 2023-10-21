package com.example.shoppinglist.fragments

import androidx.appcompat.app.AppCompatActivity
import com.example.shoppinglist.R

object FragmentManager {
    var currentFrag: BaseFragment? = null

    fun setFragment(newFragment: BaseFragment, activity: AppCompatActivity) {
        val transaction = activity.supportFragmentManager.beginTransaction()
        transaction.replace(R.id.placeHolder, newFragment)
        transaction.commit()
        currentFrag=newFragment
    }
}