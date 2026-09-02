package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "inventory_items")
data class InventoryItem(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val sku: String,
    val name: String,
    val category: String, // Frenos, Transmisión, Eléctrico/Baterías, Neumáticos, Motor/Aceite, Suspensión, Accesorios
    val compatibleVehicles: String, // "MOTO,BICICLETA,MONOPATIN,MOTO_ELECTRICA" or "UNIVERSAL"
    val stock: Int,
    val minStock: Int = 3,
    val costPrice: Double,
    val salePrice: Double,
    val unit: String = "Unidad", // Unidad, Par, Litro, Kit
    val description: String = ""
) {
    val isLowStock: Boolean
        get() = stock <= minStock
}
