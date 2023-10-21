package com.example.shoppinglist.activities


import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Spannable
import android.text.style.ForegroundColorSpan
import android.text.style.StyleSpan
import android.view.ActionMode
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import androidx.core.content.ContextCompat
import androidx.preference.PreferenceManager
import com.example.shoppinglist.R
import com.example.shoppinglist.databinding.ActivityNewNoteBinding
import com.example.shoppinglist.entitie.NoteItem
import com.example.shoppinglist.fragments.NoteFragment
import com.example.shoppinglist.utils.ColorManager
import com.example.shoppinglist.utils.HtmlManager
import com.example.shoppinglist.utils.MyTouchListener
import com.example.shoppinglist.utils.TimeManager
import java.util.*

class NewNoteActivity : AppCompatActivity() {
    private lateinit var binding: ActivityNewNoteBinding
    private lateinit var defPref: SharedPreferences
    private var note: NoteItem? = null
    private var pref:SharedPreferences?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityNewNoteBinding.inflate(layoutInflater)
        defPref=PreferenceManager.getDefaultSharedPreferences(this)
        setTheme(getSelectedTheme())
        setContentView(binding.root)
        actionBarSetting()
        getNote()
        init()
        setTextSize()
        onClickColor()
        actionMenuCallback()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.top_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun init() {
        binding.colorPicker.setOnTouchListener(MyTouchListener())
        pref=PreferenceManager.getDefaultSharedPreferences(this)
    }

    private fun getNote() {
        val serializableNote = intent.getSerializableExtra(NoteFragment.NEW_NOTE_KEY)
        if (serializableNote != null) {
            note = serializableNote as NoteItem
            fillNote()
        }
    }

    private fun fillNote() = with(binding) {
        edTitle.setText(note?.title)
        edDescription.setText(HtmlManager.getFromHtml(note?.content!!).trim())
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_save_note) {
            sendDataOnMainAct()
        } else if (item.itemId == android.R.id.home) {
            finish()
        } else if (item.itemId == R.id.id_bold) {
            setBoldForSelectedText()
        } else if (item.itemId == R.id.id_color_picker) {
            if (binding.colorPicker.isShown) {
                ColorManager.closeColorPicker(binding,this)
            } else {
                ColorManager.openColorPicker(binding,this)
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setBoldForSelectedText() = with(binding) {
        val startPos = edDescription.selectionStart
        val endPos = edDescription.selectionEnd

        val styles = edDescription.text.getSpans(startPos, endPos, StyleSpan::class.java)
        var boldStyle: StyleSpan? = null
        if (styles.isEmpty()) boldStyle = StyleSpan(Typeface.BOLD)
        if (styles.isNotEmpty()) {
            edDescription.text.removeSpan(styles[0])
        } else {
            boldStyle = StyleSpan(Typeface.BOLD)
        }

        edDescription.text.setSpan(boldStyle, startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        edDescription.text.trim()
        edDescription.setSelection(startPos)
    }

    private fun onClickColor()=with(binding){
        ibRed.setOnClickListener{setColorForSelectedText(R.color.red_picker)}
        ibGreen.setOnClickListener{setColorForSelectedText(R.color.green_picker)}
        ibOrange.setOnClickListener{setColorForSelectedText(R.color.orange_picker)}
        ibYellow.setOnClickListener{setColorForSelectedText(R.color.yellow_picker)}
        ibBlue.setOnClickListener{setColorForSelectedText(R.color.blue_picker)}
        ibViolet.setOnClickListener{setColorForSelectedText(R.color.violet_picker)}
    }

    @SuppressLint("SuspiciousIndentation")
    private fun setColorForSelectedText(colorId: Int) = with(binding) {
        val startPos = edDescription.selectionStart
        val endPos = edDescription.selectionEnd

        val styles = edDescription.text.getSpans(startPos, endPos, ForegroundColorSpan::class.java)

        if (styles.isNotEmpty()) edDescription.text.removeSpan(styles[0])

            edDescription.text.setSpan(
                ForegroundColorSpan(
                    ContextCompat.getColor(
                        this@NewNoteActivity,
                        colorId)),
                startPos, endPos, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
            edDescription.text.trim()
            edDescription.setSelection(startPos)
    }

    private fun sendDataOnMainAct() {
        var editState = "new"
        val tempNote: NoteItem? = if (note == null) {
            createNewNote()
        } else {
            editState = "update"
            updateNote()
        }
        val i = Intent().apply {
            putExtra(NoteFragment.NEW_NOTE_KEY, tempNote)
            putExtra(NoteFragment.EDIT_STATE_KEY, editState)
        }
        setResult(RESULT_OK, i)
        finish()
    }

    private fun updateNote(): NoteItem? = with(binding) {
        return note?.copy(
            title = edTitle.text.toString(),
            content = HtmlManager.toHtml(edDescription.text)
        )
    }

    private fun createNewNote(): NoteItem = with(binding) {
        return NoteItem(
            null,
            edTitle.text.toString(),
            HtmlManager.toHtml(edDescription.text),
            TimeManager.getTime(),
            ""
        )
    }

    private fun actionBarSetting() {
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    private fun actionMenuCallback(){
        val actionCallback=object :ActionMode.Callback{
            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                menu?.clear()
                return true
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean { return true }
            override fun onDestroyActionMode(mode: ActionMode?) {}
        }
        binding.edDescription.customSelectionActionModeCallback=actionCallback
    }

    private fun setTextSize()= with(binding){
        edTitle.setTextSize(pref?.getString("title_size","16"))
        edDescription.setTextSize(pref?.getString("content_size","14"))
    }


    private fun EditText.setTextSize(size:String?){
        if(size != null)  this.textSize=size.toFloat()
    }

    private fun getSelectedTheme():Int{
        return when (defPref.getString("chose_theme","green")){
            "green"-> R.style.Theme_ShoppingListGreen
            "red" -> R.style.Theme_ShoppingListRed
            else -> {
                R.style.Theme_ShoppingListBlue}
        }
    }
}