package com.example.shoppinglist.db

import androidx.lifecycle.*
import com.example.shoppinglist.entitie.LibraryItem

import com.example.shoppinglist.entitie.NoteItem
import com.example.shoppinglist.entitie.ShoppingListItem
import com.example.shoppinglist.entitie.ShoppingListName
import kotlinx.coroutines.launch

class MainViewModel(database: MainDatabase) : ViewModel() {
    private val dao = database.getDao()
    val allNotes: LiveData<List<NoteItem>> = dao.getAllNotes().asLiveData()
    val libraryItems= MutableLiveData<List<LibraryItem>>()
    val allShoppingListNames: LiveData<List<ShoppingListName>> = dao.getAllShoppingListNames().asLiveData()

    fun getAllItemsFromList(listId:Int):LiveData<List<ShoppingListItem>>{
        return dao.getAllShopListItems(listId).asLiveData()
    }
    fun insertNote(note: NoteItem) = viewModelScope.launch {
        dao.insertNote(note)
    }

    fun deleteNote(id:Int) = viewModelScope.launch {
        dao.deleteNotes(id)
    }

    fun updateNote(note: NoteItem) = viewModelScope.launch {
        dao.updateNote(note)
    }

    //ShopList
    fun insertShopListName(shopList: ShoppingListName) = viewModelScope.launch {
        dao.insertShopListName(shopList)
        if(!isLibraryItemExist(shopList.name)) dao.insertLibraryItem(LibraryItem(null,shopList.name))
    }

    fun deleteShopList(id:Int,deleteList:Boolean) = viewModelScope.launch {
        if(deleteList) {
            dao.deleteShopListName(id)
            dao.deleteShopListItem(id)
        }else{
            dao.deleteShopListItem(id)
        }
    }

    fun updateShopListName(shopListName: ShoppingListName) = viewModelScope.launch {
        dao.updateShopListName(shopListName)
    }
    //ShopItem

    fun getAllLibraryItems(name: String) = viewModelScope.launch{
        libraryItems.postValue(dao.getAllLibraryItems(name))
    }

    fun insertItem(shopListItem: ShoppingListItem)= viewModelScope.launch {
        dao.insertItem(shopListItem)
    }

    fun updateItem(shopListItem: ShoppingListItem) = viewModelScope.launch {
        dao.updateItem(shopListItem)
    }

    private suspend fun isLibraryItemExist(name:String):Boolean{
        return dao.getAllLibraryItems(name).isNotEmpty()
    }




    class MainViewModelFactory(private val database: MainDatabase) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                @Suppress("UNCHECKED_CAST")
                return MainViewModel(database) as T
            }
            throw IllegalArgumentException("Unknown ViewModelClass")
        }
    }
}