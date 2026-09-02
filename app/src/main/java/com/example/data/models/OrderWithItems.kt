package com.example.data.models

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithItems(
    @Embedded val order: ServiceOrder,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItem> = emptyList()
) {
    val vehicleEnum: VehicleType
        get() = VehicleType.fromString(order.vehicleType)

    val statusEnum: OrderStatus
        get() = OrderStatus.fromString(order.status)

    val partsTotal: Double
        get() = items.sumOf { it.subtotal }

    val calculatedTotal: Double
        get() = (order.laborCost + partsTotal - order.discount).coerceAtLeast(0.0)
}
