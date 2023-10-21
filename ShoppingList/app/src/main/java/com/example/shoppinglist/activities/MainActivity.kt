package com.example.shoppinglist.activities

import ThemeManager
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ActivityMainBinding
import com.example.shoppinglist.fragments.FragmentManager
import com.example.shoppinglist.fragments.NoteFragment
import com.example.shoppinglist.fragments.ShopListNamesFragment

class MainActivity : AppCompatActivity(){
    private lateinit var binding: ActivityMainBinding
    private lateinit var defPref: SharedPreferences
    private var currentMenuItemId=R.id.note
    private var currentTheme=""

    override fun onCreate(savedInstanceState: Bundle?) {
        defPref=PreferenceManager.getDefaultSharedPreferences(this)
        currentTheme=defPref.getString("chose_theme","green").toString()
        setTheme(ThemeManager.getSelectedTheme(defPref))
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        bottomItemSelectedListener()
    }

    private fun bottomItemSelectedListener(){
        binding.btnView.setOnItemSelectedListener {
            when(it.itemId){
                R.id.settings->{
                    startActivity(Intent(this,SettingsActivity::class.java))
                    supportActionBar?.title = getString(R.string.settings)
                    true
                }
                R.id.note->{
                   FragmentManager.setFragment(NoteFragment.newInstance(),this)
                    supportActionBar?.title = getString(R.string.note)
                    currentMenuItemId=R.id.note
                    true
                }
                R.id.shop_list->{
                    FragmentManager.setFragment(ShopListNamesFragment.newInstance(),this)
                    supportActionBar?.title = getString(R.string.shopping_list)
                    currentMenuItemId=R.id.shop_list
                    true
                }
                R.id.new_item->{
                        FragmentManager.currentFrag?.onClickNew()
                    true
                }
                else -> {true}
            }
        }
    }

    override fun onResume() {
        binding.btnView.selectedItemId=currentMenuItemId
        if(defPref.getString("chose_theme","green")!=currentTheme) recreate()
        super.onResume()
    }

    private fun toastShow(text:String){
        Toast.makeText(this, text, Toast.LENGTH_SHORT).show()
    }
}