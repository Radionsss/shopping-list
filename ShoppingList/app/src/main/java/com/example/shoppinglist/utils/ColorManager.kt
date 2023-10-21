package com.example.shoppinglist.utils

import android.content.Context
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ActivityNewNoteBinding

object ColorManager {
     fun openColorPicker(binding:ActivityNewNoteBinding,context: Context) {
        binding.colorPicker.visibility = View.VISIBLE
        val openAnim = AnimationUtils.loadAnimation(context, R.anim.open_color_picker)
        binding.colorPicker.startAnimation(openAnim)
    }

     fun closeColorPicker(binding:ActivityNewNoteBinding,context: Context) {
        val closeAnim = AnimationUtils.loadAnimation(context, R.anim.close_color_picker)
        closeAnim.setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {

            }

            override fun onAnimationEnd(animation: Animation?) {
                binding.colorPicker.visibility = View.GONE
            }

            override fun onAnimationRepeat(animation: Animation?) {

            }

        })
        binding.colorPicker.startAnimation(closeAnim)
    }
}