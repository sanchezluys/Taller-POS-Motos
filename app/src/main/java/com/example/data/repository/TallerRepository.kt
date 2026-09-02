package com.example.data.repository

import com.example.data.local.InventoryDao
import com.example.data.local.ServiceOrderDao
import com.example.data.models.InventoryItem
import com.example.data.models.OrderItem
import com.example.data.models.OrderStatus
import com.example.data.models.OrderWithItems
import com.example.data.models.ServiceOrder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class TallerRepository(
    private val inventoryDao: InventoryDao,
    private val serviceOrderDao: ServiceOrderDao
) {
    // Inventory
    val allInventoryItems: Flow<List<InventoryItem>> = inventoryDao.getAllItems()
    val lowStockItems: Flow<List<InventoryItem>> = inventoryDao.getLowStockItems()

    fun searchInventory(query: String): Flow<List<InventoryItem>> {
        return if (query.isBlank()) {
            inventoryDao.getAllItems()
        } else {
            inventoryDao.searchItems(query.trim())
        }
    }

    fun getInventoryByCategory(category: String): Flow<List<InventoryItem>> {
        return if (category == "Todos") {
            inventoryDao.getAllItems()
        } else {
            inventoryDao.getItemsByCategory(category)
        }
    }

    suspend fun saveInventoryItem(item: InventoryItem): Long {
        return if (item.id == 0L) {
            inventoryDao.insert(item)
        } else {
            inventoryDao.update(item)
            item.id
        }
    }

    suspend fun deleteInventoryItem(item: InventoryItem) {
        inventoryDao.delete(item)
    }

    suspend fun updateStock(itemId: Long, newStock: Int) {
        inventoryDao.setStock(itemId, newStock.coerceAtLeast(0))
    }

    suspend fun adjustStock(itemId: Long, delta: Int) {
        val item = inventoryDao.getItemById(itemId)
        if (item != null) {
            val newStock = (item.stock + delta).coerceAtLeast(0)
            inventoryDao.setStock(itemId, newStock)
        }
    }

    // Orders & POS
    val allOrders: Flow<List<OrderWithItems>> = serviceOrderDao.getAllOrders()

    fun searchOrders(query: String): Flow<List<OrderWithItems>> {
        return if (query.isBlank()) {
            serviceOrderDao.getAllOrders()
        } else {
            serviceOrderDao.searchOrders(query.trim())
        }
    }

    fun getOrdersByStatus(status: String): Flow<List<OrderWithItems>> {
        return if (status == "TODOS") {
            serviceOrderDao.getAllOrders()
        } else {
            serviceOrderDao.getOrdersByStatus(status)
        }
    }

    fun getOrderById(orderId: Long): Flow<OrderWithItems?> {
        return serviceOrderDao.getOrderById(orderId)
    }

    fun getVehicleHistory(identifier: String): Flow<List<OrderWithItems>> {
        return serviceOrderDao.getHistoryForVehicle(identifier.trim())
    }

    suspend fun generateNextOrderNumber(): String {
        val count = serviceOrderDao.getOrderCount() + 1
        return "OT-${1000 + count}"
    }

    suspend fun createServiceOrder(
        order: ServiceOrder,
        items: List<OrderItem>,
        reduceStockNow: Boolean = true
    ): Long {
        val orderId = serviceOrderDao.insertOrder(order)
        val itemsWithOrderId = items.map { it.copy(orderId = orderId) }
        serviceOrderDao.insertOrderItems(itemsWithOrderId)

        if (reduceStockNow) {
            items.forEach { item ->
                item.inventoryItemId?.let { invId ->
                    inventoryDao.reduceStock(invId, item.quantity)
                }
            }
        }
        return orderId
    }

    suspend fun updateOrderStatus(order: ServiceOrder, newStatus: OrderStatus, isPaid: Boolean = order.isPaid) {
        val completedAt = if (newStatus == OrderStatus.ENTREGADO && order.completedAt == null) {
            System.currentTimeMillis()
        } else order.completedAt

        val updatedOrder = order.copy(
            status = newStatus.name,
            isPaid = if (newStatus == OrderStatus.ENTREGADO) true else isPaid,
            completedAt = completedAt
        )
        serviceOrderDao.updateOrder(updatedOrder)
    }

    suspend fun updateOrderDetails(
        order: ServiceOrder,
        items: List<OrderItem>
    ) {
        serviceOrderDao.updateOrder(order)
        serviceOrderDao.deleteOrderItems(order.id)
        val itemsWithOrderId = items.map { it.copy(orderId = order.id) }
        serviceOrderDao.insertOrderItems(itemsWithOrderId)
    }

    suspend fun deleteOrder(order: ServiceOrder) {
        serviceOrderDao.deleteOrder(order)
    }
}
