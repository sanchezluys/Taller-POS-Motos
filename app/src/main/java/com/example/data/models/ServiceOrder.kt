package com.example.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "service_orders")
data class ServiceOrder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String, // e.g. "OT-1001"
    val clientName: String,
    val clientPhone: String,
    val clientEmail: String = "",
    val vehicleType: String, // "MOTO", "BICICLETA", "MONOPATIN", "MOTO_ELECTRICA"
    val vehicleBrandModel: String,
    val vehiclePlateOrSerial: String,
    val vehicleColor: String = "",
    val vehicleMetric: String = "", // Mileage or battery charge/cycles
    val issueReported: String, // Falla reportada / Motivo de ingreso
    val diagnosisNotes: String = "", // Diagnóstico técnico / Solución aplicada
    val status: String = OrderStatus.RECIBIDO.name,
    val laborDescription: String = "Mano de obra y servicio",
    val laborCost: Double = 0.0,
    val discount: Double = 0.0,
    val totalAmount: Double = 0.0,
    val paymentMethod: String = PaymentMethod.EFECTIVO.name,
    val isPaid: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val completedAt: Long? = null
)
