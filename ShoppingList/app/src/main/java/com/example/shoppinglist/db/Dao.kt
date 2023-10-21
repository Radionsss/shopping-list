package com.example.shoppinglist.db

import  androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.shoppinglist.constlist.ListConstName
import com.example.shoppinglist.entitie.LibraryItem
import com.example.shoppinglist.entitie.NoteItem
import com.example.shoppinglist.entitie.ShoppingListItem
import com.example.shoppinglist.entitie.ShoppingListName
import kotlinx.coroutines.flow.Flow

@androidx.room.Dao //Data Access Object |Объект доступа к данным
interface Dao {
    @Query("SELECT * FROM ${ListConstName.NOTE_ITEM}")
    fun getAllNotes(): Flow<List<NoteItem>>

    @Query("DELETE FROM ${ListConstName.NOTE_ITEM} WHERE id IS :id")
    suspend fun deleteNotes(id: Int)

    @Insert//Вставлять
    suspend fun insertNote(note: NoteItem)

    @Update
    suspend fun updateNote(note: NoteItem)

    //ShopList
    @Query("SELECT * FROM ${ListConstName.SHOPPING_LIST_NAMES}")
    fun getAllShoppingListNames(): Flow<List<ShoppingListName>>

    @Query("DELETE FROM ${ListConstName.SHOPPING_LIST_NAMES} WHERE id IS :id")
    suspend fun deleteShopListName(id: Int)

    @Insert
    suspend fun insertShopListName(shopList: ShoppingListName)

    @Update
    suspend fun updateShopListName(shopListName: ShoppingListName)

    //ShopItem
    @Query("SELECT * FROM ${ListConstName.SHOPPING_LIST_ITEM} WHERE listId LIKE :listId")
    fun getAllShopListItems(listId: Int): Flow<List<ShoppingListItem>>

    @Insert
    suspend fun insertItem(shopListItem: ShoppingListItem)

    @Update
    suspend fun updateItem(shopListItem: ShoppingListItem)

    @Query("DELETE FROM ${ListConstName.SHOPPING_LIST_ITEM} WHERE listId LIKE :listId")
    suspend fun deleteShopListItem(listId: Int)

    //ShopLibrary
    @Query("SELECT * FROM ${ListConstName.LIBRARY_ITEM} WHERE name LIKE :name")
    suspend fun getAllLibraryItems(name: String): List<LibraryItem>

    @Insert
    suspend fun insertLibraryItem(libraryItem: LibraryItem)
}