package com.example.data.models

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.TwoWheeler
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

enum class VehicleType(
    val id: String,
    val displayName: String,
    val shortName: String,
    val measurementLabel: String,
    val identifierLabel: String,
    val defaultBrandPlaceholder: String
) {
    MOTO(
        id = "MOTO",
        displayName = "Motocicleta (Combustión)",
        shortName = "Moto",
        measurementLabel = "Kilometraje (km)",
        identifierLabel = "Placa / Patente",
        defaultBrandPlaceholder = "Ej. Honda CB190, Yamaha FZ..."
    ),
    BICICLETA(
        id = "BICICLETA",
        displayName = "Bicicleta (Ruta / MTB / Urbana)",
        shortName = "Bici",
        measurementLabel = "Uso / Horas aprox.",
        identifierLabel = "Nº Serie de Cuadro",
        defaultBrandPlaceholder = "Ej. Trek Marlin, Specialized, Giant..."
    ),
    MONOPATIN(
        id = "MONOPATIN",
        displayName = "Monopatín Eléctrico / Scooter",
        shortName = "Scooter",
        measurementLabel = "Ciclos / Voltaje Batería",
        identifierLabel = "Nº Serie / Identificador",
        defaultBrandPlaceholder = "Ej. Xiaomi Pro 2, Ninebot Max, Dualtron..."
    ),
    MOTO_ELECTRICA(
        id = "MOTO_ELECTRICA",
        displayName = "Moto Eléctrica / Ciclomotor",
        shortName = "Moto Eléctrica",
        measurementLabel = "Km / Batería (%)",
        identifierLabel = "Placa / Nº Serie",
        defaultBrandPlaceholder = "Ej. NIU NQi, Super Soco, Sunra..."
    );

    val icon: ImageVector
        get() = when (this) {
            MOTO -> Icons.Filled.TwoWheeler
            BICICLETA -> Icons.Filled.DirectionsBike
            MONOPATIN -> Icons.Filled.ElectricScooter
            MOTO_ELECTRICA -> Icons.Filled.ElectricBike
        }

    companion object {
        fun fromString(value: String?): VehicleType {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.id.equals(value, ignoreCase = true) } ?: MOTO
        }
    }
}

enum class OrderStatus(
    val label: String,
    val color: Long,
    val description: String
) {
    RECIBIDO("Recibido", 0xFF3B82F6, "Vehículo ingresado al taller, pendiente de revisión"),
    EN_PROCESO("En Reparación", 0xFFF59E0B, "Técnico trabajando activamente en el servicio"),
    LISTO("Listo para Entrega", 0xFF10B981, "Reparación finalizada y verificada"),
    ENTREGADO("Entregado / Pagado", 0xFF64748B, "Entregado al cliente con cobro realizado"),
    CANCELADO("Cancelado", 0xFFEF4444, "Orden cancelada o devuelta");

    companion object {
        fun fromString(value: String?): OrderStatus {
            return entries.find { it.name.equals(value, ignoreCase = true) } ?: RECIBIDO
        }
    }
}

enum class PaymentMethod(val label: String) {
    EFECTIVO("Efectivo"),
    TARJETA("Tarjeta Débito/Crédito"),
    TRANSFERENCIA("Transferencia / Pago Móvil");

    companion object {
        fun fromString(value: String?): PaymentMethod {
            return entries.find { it.name.equals(value, ignoreCase = true) || it.label.equals(value, ignoreCase = true) } ?: EFECTIVO
        }
    }
}
