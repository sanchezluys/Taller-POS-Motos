package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.models.InventoryItem
import kotlinx.coroutines.flow.Flow

@Dao
interface InventoryDao {
    @Query("SELECT * FROM inventory_items ORDER BY name ASC")
    fun getAllItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE id = :id")
    suspend fun getItemById(id: Long): InventoryItem?

    @Query("SELECT * FROM inventory_items WHERE stock <= minStock ORDER BY stock ASC")
    fun getLowStockItems(): Flow<List<InventoryItem>>

    @Query("SELECT * FROM inventory_items WHERE category = :category ORDER BY name ASC")
    fun getItemsByCategory(category: String): Flow<List<InventoryItem>>

    @Query("""
        SELECT * FROM inventory_items 
        WHERE name LIKE '%' || :query || '%' 
           OR sku LIKE '%' || :query || '%' 
           OR category LIKE '%' || :query || '%'
        ORDER BY name ASC
    """)
    fun searchItems(query: String): Flow<List<InventoryItem>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: InventoryItem): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(items: List<InventoryItem>)

    @Update
    suspend fun update(item: InventoryItem)

    @Delete
    suspend fun delete(item: InventoryItem)

    @Query("UPDATE inventory_items SET stock = stock - :quantity WHERE id = :id AND stock >= :quantity")
    suspend fun reduceStock(id: Long, quantity: Int): Int

    @Query("UPDATE inventory_items SET stock = :newStock WHERE id = :id")
    suspend fun setStock(id: Long, newStock: Int)

    @Query("SELECT COUNT(*) FROM inventory_items")
    suspend fun getCount(): Int
}
