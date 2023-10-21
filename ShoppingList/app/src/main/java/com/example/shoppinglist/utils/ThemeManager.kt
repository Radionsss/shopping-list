import android.content.SharedPreferences
import com.example.shoppinglist.R

object ThemeManager {
     fun getSelectedTheme(defPref: SharedPreferences):Int{
        return when (defPref.getString("chose_theme","green")){
            "green"-> R.style.Theme_ShoppingListGreen
            "red" -> R.style.Theme_ShoppingListRed
            else -> {
                R.style.Theme_ShoppingListBlue}
        }
    }
}