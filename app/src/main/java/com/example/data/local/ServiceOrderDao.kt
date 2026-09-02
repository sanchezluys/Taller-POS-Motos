package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.example.data.models.OrderItem
import com.example.data.models.OrderWithItems
import com.example.data.models.ServiceOrder
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceOrderDao {
    @Transaction
    @Query("SELECT * FROM service_orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<OrderWithItems>>

    @Transaction
    @Query("SELECT * FROM service_orders WHERE id = :orderId")
    fun getOrderById(orderId: Long): Flow<OrderWithItems?>

    @Transaction
    @Query("SELECT * FROM service_orders WHERE status = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: String): Flow<List<OrderWithItems>>

    @Transaction
    @Query("""
        SELECT * FROM service_orders 
        WHERE vehiclePlateOrSerial LIKE '%' || :query || '%' 
           OR clientName LIKE '%' || :query || '%'
           OR vehicleBrandModel LIKE '%' || :query || '%'
           OR orderNumber LIKE '%' || :query || '%'
        ORDER BY createdAt DESC
    """)
    fun searchOrders(query: String): Flow<List<OrderWithItems>>

    @Transaction
    @Query("""
        SELECT * FROM service_orders 
        WHERE vehiclePlateOrSerial = :identifier AND :identifier != ''
        ORDER BY createdAt DESC
    """)
    fun getHistoryForVehicle(identifier: String): Flow<List<OrderWithItems>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: ServiceOrder): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItems(items: List<OrderItem>)

    @Update
    suspend fun updateOrder(order: ServiceOrder)

    @Delete
    suspend fun deleteOrder(order: ServiceOrder)

    @Query("DELETE FROM order_items WHERE orderId = :orderId")
    suspend fun deleteOrderItems(orderId: Long)

    @Query("SELECT COUNT(*) FROM service_orders")
    suspend fun getOrderCount(): Int

    @Query("SELECT MAX(id) FROM service_orders")
    suspend fun getMaxId(): Long?
}
