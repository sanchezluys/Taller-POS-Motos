package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.TallerDatabase
import com.example.data.models.InventoryItem
import com.example.data.models.OrderItem
import com.example.data.models.OrderStatus
import com.example.data.models.OrderWithItems
import com.example.data.models.PaymentMethod
import com.example.data.models.ServiceOrder
import com.example.data.models.VehicleType
import com.example.data.models.WorkshopSettings
import com.example.data.repository.SettingsRepository
import com.example.data.repository.TallerRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class PosCartItem(
    val inventoryItem: InventoryItem? = null,
    val name: String,
    val unitPrice: Double,
    val quantity: Int = 1
) {
    val subtotal: Double get() = unitPrice * quantity
}

data class PosFormState(
    val clientName: String = "",
    val clientPhone: String = "",
    val clientEmail: String = "",
    val vehicleType: VehicleType = VehicleType.MOTO,
    val vehicleBrandModel: String = "",
    val vehiclePlateOrSerial: String = "",
    val vehicleColor: String = "",
    val vehicleMetric: String = "",
    val issueReported: String = "",
    val diagnosisNotes: String = "",
    val laborDescription: String = "Mantenimiento / Mano de obra",
    val laborCostText: String = "0.00",
    val discountText: String = "0.00",
    val paymentMethod: PaymentMethod = PaymentMethod.EFECTIVO,
    val cartItems: List<PosCartItem> = emptyList()
) {
    val laborCost: Double get() = laborCostText.toDoubleOrNull() ?: 0.0
    val discount: Double get() = discountText.toDoubleOrNull() ?: 0.0
    val partsSubtotal: Double get() = cartItems.sumOf { it.subtotal }
    val totalAmount: Double get() = (laborCost + partsSubtotal - discount).coerceAtLeast(0.0)
    val isValid: Boolean get() = clientName.isNotBlank() && vehicleBrandModel.isNotBlank()
}

class TallerViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: TallerRepository
    private val settingsRepository: SettingsRepository

    init {
        val db = TallerDatabase.getInstance(application)
        repository = TallerRepository(db.inventoryDao(), db.serviceOrderDao())
        settingsRepository = SettingsRepository(application)
    }

    // Workshop Settings
    val workshopSettings: StateFlow<WorkshopSettings> = settingsRepository.settings

    fun updateWorkshopSettings(newSettings: WorkshopSettings) {
        settingsRepository.saveSettings(newSettings)
    }

    fun toggleDarkMode() {
        val current = workshopSettings.value
        updateWorkshopSettings(current.copy(isDarkMode = !current.isDarkMode))
    }

    fun formatPrice(amount: Double): String {
        return workshopSettings.value.formatPrice(amount)
    }

    // Active bottom navigation tab: 0 = Taller/Órdenes, 1 = POS / Nueva Orden, 2 = Inventario, 3 = Caja & Historial, 4 = Configuración
    private val _selectedTab = MutableStateFlow(0)
    val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

    fun selectTab(tabIndex: Int) {
        _selectedTab.value = tabIndex
    }

    // Orders Filter State
    private val _orderSearchQuery = MutableStateFlow("")
    val orderSearchQuery = _orderSearchQuery.asStateFlow()

    private val _orderStatusFilter = MutableStateFlow("TODOS")
    val orderStatusFilter = _orderStatusFilter.asStateFlow()

    private val _orderVehicleFilter = MutableStateFlow<VehicleType?>(null)
    val orderVehicleFilter = _orderVehicleFilter.asStateFlow()

    val ordersList: StateFlow<List<OrderWithItems>> = combine(
        repository.allOrders,
        _orderSearchQuery,
        _orderStatusFilter,
        _orderVehicleFilter
    ) { orders, query, status, vehicleType ->
        orders.filter { orderWithItems ->
            val o = orderWithItems.order
            val matchesQuery = query.isBlank() ||
                    o.clientName.contains(query, ignoreCase = true) ||
                    o.clientPhone.contains(query, ignoreCase = true) ||
                    o.vehiclePlateOrSerial.contains(query, ignoreCase = true) ||
                    o.vehicleBrandModel.contains(query, ignoreCase = true) ||
                    o.orderNumber.contains(query, ignoreCase = true) ||
                    o.issueReported.contains(query, ignoreCase = true)

            val matchesStatus = status == "TODOS" || o.status.equals(status, ignoreCase = true)
            val matchesVehicle = vehicleType == null || o.vehicleType.equals(vehicleType.name, ignoreCase = true)

            matchesQuery && matchesStatus && matchesVehicle
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Selected order for detailed view / receipt
    private val _selectedOrder = MutableStateFlow<OrderWithItems?>(null)
    val selectedOrder: StateFlow<OrderWithItems?> = _selectedOrder.asStateFlow()

    private val _showReceiptDialog = MutableStateFlow(false)
    val showReceiptDialog: StateFlow<Boolean> = _showReceiptDialog.asStateFlow()

    fun openOrderDetail(order: OrderWithItems) {
        _selectedOrder.value = order
    }

    fun closeOrderDetail() {
        _selectedOrder.value = null
    }

    fun showReceipt(order: OrderWithItems) {
        _selectedOrder.value = order
        _showReceiptDialog.value = true
    }

    fun closeReceiptDialog() {
        _showReceiptDialog.value = false
    }

    fun setOrderSearchQuery(query: String) {
        _orderSearchQuery.value = query
    }

    fun setOrderStatusFilter(status: String) {
        _orderStatusFilter.value = status
    }

    fun setOrderVehicleFilter(type: VehicleType?) {
        _orderVehicleFilter.value = type
    }

    // Advance order status
    fun advanceOrderStatus(orderWithItems: OrderWithItems) {
        viewModelScope.launch {
            val current = orderWithItems.statusEnum
            val nextStatus = when (current) {
                OrderStatus.RECIBIDO -> OrderStatus.EN_PROCESO
                OrderStatus.EN_PROCESO -> OrderStatus.LISTO
                OrderStatus.LISTO -> OrderStatus.ENTREGADO
                OrderStatus.ENTREGADO -> OrderStatus.ENTREGADO
                OrderStatus.CANCELADO -> OrderStatus.RECIBIDO
            }
            repository.updateOrderStatus(orderWithItems.order, nextStatus)
            // Update selected order in state if opened
            if (_selectedOrder.value?.order?.id == orderWithItems.order.id) {
                _selectedOrder.value = _selectedOrder.value?.copy(
                    order = orderWithItems.order.copy(status = nextStatus.name)
                )
            }
        }
    }

    fun setSpecificOrderStatus(orderWithItems: OrderWithItems, newStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderWithItems.order, newStatus)
            if (_selectedOrder.value?.order?.id == orderWithItems.order.id) {
                _selectedOrder.value = _selectedOrder.value?.copy(
                    order = orderWithItems.order.copy(status = newStatus.name)
                )
            }
        }
    }

    fun deleteOrder(orderWithItems: OrderWithItems) {
        viewModelScope.launch {
            repository.deleteOrder(orderWithItems.order)
            if (_selectedOrder.value?.order?.id == orderWithItems.order.id) {
                _selectedOrder.value = null
            }
        }
    }

    // POS & New Work Order Form State
    private val _posForm = MutableStateFlow(PosFormState())
    val posForm: StateFlow<PosFormState> = _posForm.asStateFlow()

    fun updatePosClient(name: String, phone: String, email: String = "") {
        _posForm.value = _posForm.value.copy(clientName = name, clientPhone = phone, clientEmail = email)
    }

    fun updatePosVehicle(
        type: VehicleType,
        brandModel: String,
        plateOrSerial: String,
        color: String,
        metric: String
    ) {
        _posForm.value = _posForm.value.copy(
            vehicleType = type,
            vehicleBrandModel = brandModel,
            vehiclePlateOrSerial = plateOrSerial,
            vehicleColor = color,
            vehicleMetric = metric
        )
    }

    fun updatePosIssueAndDiagnosis(issue: String, diagnosis: String) {
        _posForm.value = _posForm.value.copy(issueReported = issue, diagnosisNotes = diagnosis)
    }

    fun updatePosPricing(laborDescription: String, laborCost: String, discount: String, paymentMethod: PaymentMethod) {
        _posForm.value = _posForm.value.copy(
            laborDescription = laborDescription,
            laborCostText = laborCost,
            discountText = discount,
            paymentMethod = paymentMethod
        )
    }

    fun addItemToCart(inventoryItem: InventoryItem) {
        val currentItems = _posForm.value.cartItems.toMutableList()
        val index = currentItems.indexOfFirst { it.inventoryItem?.id == inventoryItem.id }
        if (index >= 0) {
            val existing = currentItems[index]
            currentItems[index] = existing.copy(quantity = existing.quantity + 1)
        } else {
            currentItems.add(
                PosCartItem(
                    inventoryItem = inventoryItem,
                    name = inventoryItem.name,
                    unitPrice = inventoryItem.salePrice,
                    quantity = 1
                )
            )
        }
        _posForm.value = _posForm.value.copy(cartItems = currentItems)
    }

    fun addCustomItemToCart(name: String, price: Double, qty: Int = 1) {
        if (name.isBlank() || price <= 0) return
        val currentItems = _posForm.value.cartItems.toMutableList()
        currentItems.add(
            PosCartItem(
                inventoryItem = null,
                name = name.trim(),
                unitPrice = price,
                quantity = qty
            )
        )
        _posForm.value = _posForm.value.copy(cartItems = currentItems)
    }

    fun updateCartItemQuantity(index: Int, newQuantity: Int) {
        val currentItems = _posForm.value.cartItems.toMutableList()
        if (index in currentItems.indices) {
            if (newQuantity <= 0) {
                currentItems.removeAt(index)
            } else {
                currentItems[index] = currentItems[index].copy(quantity = newQuantity)
            }
            _posForm.value = _posForm.value.copy(cartItems = currentItems)
        }
    }

    fun removeCartItem(index: Int) {
        val currentItems = _posForm.value.cartItems.toMutableList()
        if (index in currentItems.indices) {
            currentItems.removeAt(index)
            _posForm.value = _posForm.value.copy(cartItems = currentItems)
        }
    }

    fun resetPosForm() {
        _posForm.value = PosFormState()
    }

    // Create Order or Direct POS Checkout
    fun submitOrder(directCheckout: Boolean = false, onSuccess: (OrderWithItems) -> Unit) {
        val form = _posForm.value
        if (!form.isValid) return

        viewModelScope.launch {
            val orderNumber = repository.generateNextOrderNumber()
            val initialStatus = if (directCheckout) OrderStatus.ENTREGADO else OrderStatus.RECIBIDO
            val serviceOrder = ServiceOrder(
                orderNumber = orderNumber,
                clientName = form.clientName.trim(),
                clientPhone = form.clientPhone.trim(),
                clientEmail = form.clientEmail.trim(),
                vehicleType = form.vehicleType.name,
                vehicleBrandModel = form.vehicleBrandModel.trim(),
                vehiclePlateOrSerial = form.vehiclePlateOrSerial.trim().uppercase(),
                vehicleColor = form.vehicleColor.trim(),
                vehicleMetric = form.vehicleMetric.trim(),
                issueReported = form.issueReported.trim().ifBlank { "Servicio General" },
                diagnosisNotes = form.diagnosisNotes.trim(),
                status = initialStatus.name,
                laborDescription = form.laborDescription.trim(),
                laborCost = form.laborCost,
                discount = form.discount,
                totalAmount = form.totalAmount,
                paymentMethod = form.paymentMethod.name,
                isPaid = directCheckout,
                createdAt = System.currentTimeMillis(),
                completedAt = if (directCheckout) System.currentTimeMillis() else null
            )

            val orderItems = form.cartItems.map { cartItem ->
                OrderItem(
                    orderId = 0,
                    inventoryItemId = cartItem.inventoryItem?.id,
                    itemName = cartItem.name,
                    quantity = cartItem.quantity,
                    unitPrice = cartItem.unitPrice,
                    subtotal = cartItem.subtotal
                )
            }

            val newId = repository.createServiceOrder(serviceOrder, orderItems, reduceStockNow = true)
            val createdOrder = OrderWithItems(
                order = serviceOrder.copy(id = newId),
                items = orderItems.map { it.copy(orderId = newId) }
            )
            resetPosForm()
            _selectedTab.value = 0
            onSuccess(createdOrder)
        }
    }

    // Inventory State
    private val _inventorySearchQuery = MutableStateFlow("")
    val inventorySearchQuery = _inventorySearchQuery.asStateFlow()

    private val _inventoryCategoryFilter = MutableStateFlow("Todos")
    val inventoryCategoryFilter = _inventoryCategoryFilter.asStateFlow()

    private val _inventoryLowStockOnly = MutableStateFlow(false)
    val inventoryLowStockOnly = _inventoryLowStockOnly.asStateFlow()

    val inventoryList: StateFlow<List<InventoryItem>> = combine(
        repository.allInventoryItems,
        _inventorySearchQuery,
        _inventoryCategoryFilter,
        _inventoryLowStockOnly
    ) { items, query, category, lowStockOnly ->
        items.filter { item ->
            val matchesQuery = query.isBlank() ||
                    item.name.contains(query, ignoreCase = true) ||
                    item.sku.contains(query, ignoreCase = true) ||
                    item.category.contains(query, ignoreCase = true) ||
                    item.description.contains(query, ignoreCase = true)

            val matchesCategory = category == "Todos" || item.category.equals(category, ignoreCase = true)
            val matchesLowStock = !lowStockOnly || item.isLowStock

            matchesQuery && matchesCategory && matchesLowStock
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val lowStockCount: StateFlow<Int> = repository.lowStockItems.combine(MutableStateFlow(Unit)) { items, _ ->
        items.size
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    fun setInventorySearchQuery(query: String) {
        _inventorySearchQuery.value = query
    }

    fun setInventoryCategoryFilter(category: String) {
        _inventoryCategoryFilter.value = category
    }

    fun toggleInventoryLowStockOnly() {
        _inventoryLowStockOnly.value = !_inventoryLowStockOnly.value
    }

    fun saveInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.saveInventoryItem(item)
        }
    }

    fun deleteInventoryItem(item: InventoryItem) {
        viewModelScope.launch {
            repository.deleteInventoryItem(item)
        }
    }

    fun adjustStock(itemId: Long, delta: Int) {
        viewModelScope.launch {
            repository.adjustStock(itemId, delta)
        }
    }

    // Vehicle History Search State
    private val _historyVehicleQuery = MutableStateFlow("")
    val historyVehicleQuery = _historyVehicleQuery.asStateFlow()

    val vehicleHistoryList: StateFlow<List<OrderWithItems>> = combine(
        repository.allOrders,
        _historyVehicleQuery
    ) { orders, query ->
        if (query.isBlank()) {
            orders
        } else {
            orders.filter {
                it.order.vehiclePlateOrSerial.contains(query.trim(), ignoreCase = true) ||
                it.order.clientName.contains(query.trim(), ignoreCase = true) ||
                it.order.vehicleBrandModel.contains(query.trim(), ignoreCase = true)
            }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setHistoryVehicleQuery(query: String) {
        _historyVehicleQuery.value = query
    }

    // Quick lookup for pos form autofill if vehicle plate is recognized
    fun findPreviousVehicleRecord(identifier: String): OrderWithItems? {
        if (identifier.isBlank()) return null
        return ordersList.value.firstOrNull {
            it.order.vehiclePlateOrSerial.equals(identifier.trim(), ignoreCase = true)
        }
    }
}
