package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.data.models.InventoryItem
import com.example.data.models.OrderItem
import com.example.data.models.OrderStatus
import com.example.data.models.PaymentMethod
import com.example.data.models.ServiceOrder
import com.example.data.models.VehicleType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(
    entities = [InventoryItem::class, ServiceOrder::class, OrderItem::class],
    version = 1,
    exportSchema = false
)
abstract class TallerDatabase : RoomDatabase() {
    abstract fun inventoryDao(): InventoryDao
    abstract fun serviceOrderDao(): ServiceOrderDao

    companion object {
        @Volatile
        private var INSTANCE: TallerDatabase? = null

        fun getInstance(context: Context): TallerDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    TallerDatabase::class.java,
                    "taller_pos_database.db"
                )
                .addCallback(object : Callback() {
                    override fun onCreate(db: SupportSQLiteDatabase) {
                        super.onCreate(db)
                        // Seed initial realistic inventory and demo orders in background
                        CoroutineScope(Dispatchers.IO).launch {
                            INSTANCE?.let { database ->
                                seedInitialData(database)
                            }
                        }
                    }
                })
                .build()
                INSTANCE = instance
                instance
            }
        }

        private suspend fun seedInitialData(db: TallerDatabase) {
            val initialInventory = listOf(
                // Repuestos para Motos
                InventoryItem(
                    sku = "MOT-ACE-10W40",
                    name = "Aceite Sintético 4T 10W40 (1L)",
                    category = "Motor / Aceites",
                    compatibleVehicles = "MOTO",
                    stock = 14,
                    minStock = 4,
                    costPrice = 8.50,
                    salePrice = 14.00,
                    unit = "Litro",
                    description = "Aceite de alto rendimiento para motores de 4 tiempos"
                ),
                InventoryItem(
                    sku = "MOT-PAS-DEL",
                    name = "Pastillas de Freno Delanteras (Moto 125-250cc)",
                    category = "Frenos",
                    compatibleVehicles = "MOTO",
                    stock = 8,
                    minStock = 3,
                    costPrice = 6.00,
                    salePrice = 12.50,
                    unit = "Par",
                    description = "Compuesto semimetálico de alta fricción"
                ),
                InventoryItem(
                    sku = "MOT-BUJ-CPR8",
                    name = "Bujía NGK Resistencia Iridium",
                    category = "Motor / Aceites",
                    compatibleVehicles = "MOTO",
                    stock = 12,
                    minStock = 4,
                    costPrice = 3.80,
                    salePrice = 8.00,
                    unit = "Unidad",
                    description = "Bujía de encendido de larga duración"
                ),
                InventoryItem(
                    sku = "MOT-CAD-428",
                    name = "Cadena Reforzada 428H x 120L",
                    category = "Transmisión",
                    compatibleVehicles = "MOTO",
                    stock = 5,
                    minStock = 2,
                    costPrice = 12.00,
                    salePrice = 22.00,
                    unit = "Unidad",
                    description = "Cadena de tracción con eslabones reforzados"
                ),

                // Repuestos para Monopatines Eléctricos (Scooters)
                InventoryItem(
                    sku = "SCO-NEU-85X2",
                    name = "Neumático Macizo Antipinchazos 8.5\" (Xiaomi)",
                    category = "Neumáticos",
                    compatibleVehicles = "MONOPATIN",
                    stock = 6,
                    minStock = 2,
                    costPrice = 9.00,
                    salePrice = 18.00,
                    unit = "Unidad",
                    description = "Rueda sólida sin cámara de aire, patrón panal"
                ),
                InventoryItem(
                    sku = "SCO-CAM-85",
                    name = "Cámara de Aire 8.5\" con Válvula Curva",
                    category = "Neumáticos",
                    compatibleVehicles = "MONOPATIN",
                    stock = 15,
                    minStock = 5,
                    costPrice = 2.50,
                    salePrice = 6.50,
                    unit = "Unidad",
                    description = "Cámara de butilo reforzada para scooter"
                ),
                InventoryItem(
                    sku = "SCO-PAS-DISC",
                    name = "Pastillas Freno de Disco Scooter (Redondas/Cuadradas)",
                    category = "Frenos",
                    compatibleVehicles = "MONOPATIN,MOTO_ELECTRICA",
                    stock = 10,
                    minStock = 3,
                    costPrice = 2.00,
                    salePrice = 6.00,
                    unit = "Par",
                    description = "Pastillas de freno mecánicas e hidráulicas compactas"
                ),
                InventoryItem(
                    sku = "SCO-ACE-THROT",
                    name = "Acelerador de Gatillo Digital 36V/48V",
                    category = "Eléctrico / Baterías",
                    compatibleVehicles = "MONOPATIN",
                    stock = 4,
                    minStock = 2,
                    costPrice = 7.50,
                    salePrice = 15.00,
                    unit = "Unidad",
                    description = "Sensor Hall con conector impermeable de 3 pines"
                ),

                // Repuestos para Bicicletas
                InventoryItem(
                    sku = "BIC-CAD-9V",
                    name = "Cadena 9 Velocidades KMC con Eslabón Rápido",
                    category = "Transmisión",
                    compatibleVehicles = "BICICLETA",
                    stock = 7,
                    minStock = 3,
                    costPrice = 9.00,
                    salePrice = 16.50,
                    unit = "Unidad",
                    description = "Cadena niquelada resistente a la corrosión"
                ),
                InventoryItem(
                    sku = "BIC-CAM-29",
                    name = "Cámara MTB 29 x 2.10/2.35 Válvula Presta",
                    category = "Neumáticos",
                    compatibleVehicles = "BICICLETA",
                    stock = 12,
                    minStock = 4,
                    costPrice = 3.00,
                    salePrice = 7.00,
                    unit = "Unidad",
                    description = "Cámara con goma de alta elasticidad"
                ),
                InventoryItem(
                    sku = "BIC-PAS-HYD",
                    name = "Pastillas Freno Hidráulico Shimano B01S / B05S",
                    category = "Frenos",
                    compatibleVehicles = "BICICLETA,MONOPATIN",
                    stock = 9,
                    minStock = 3,
                    costPrice = 4.00,
                    salePrice = 9.50,
                    unit = "Par",
                    description = "Resina de bajo ruido y excelente modulación"
                ),
                InventoryItem(
                    sku = "BIC-CAB-CAM",
                    name = "Kit de Guaya y Funda de Cambios / Frenos",
                    category = "Transmisión",
                    compatibleVehicles = "BICICLETA,MOTO",
                    stock = 18,
                    minStock = 5,
                    costPrice = 1.80,
                    salePrice = 4.50,
                    unit = "Kit",
                    description = "Cable de acero inoxidable con funda teflonada"
                ),

                // Repuestos para Motos Eléctricas
                InventoryItem(
                    sku = "ELC-CON-60V",
                    name = "Controlador Brushless 60V/72V 1500W Smart",
                    category = "Eléctrico / Baterías",
                    compatibleVehicles = "MOTO_ELECTRICA",
                    stock = 2,
                    minStock = 1,
                    costPrice = 35.00,
                    salePrice = 65.00,
                    unit = "Unidad",
                    description = "Controlador inteligente con frenado regenerativo"
                ),
                InventoryItem(
                    sku = "ELC-CON-CHARG",
                    name = "Cargador Rápido 60V 5A Li-ion / Plomo",
                    category = "Eléctrico / Baterías",
                    compatibleVehicles = "MOTO_ELECTRICA",
                    stock = 3,
                    minStock = 1,
                    costPrice = 22.00,
                    salePrice = 42.00,
                    unit = "Unidad",
                    description = "Cargador con ventilador inteligente y corte automático"
                ),
                InventoryItem(
                    sku = "UNI-LUB-CHAIN",
                    name = "Lubricante Sintético de Cadena con PTFE (400ml)",
                    category = "Accesorios",
                    compatibleVehicles = "MOTO,BICICLETA,MONOPATIN,MOTO_ELECTRICA",
                    stock = 11,
                    minStock = 3,
                    costPrice = 4.50,
                    salePrice = 9.00,
                    unit = "Unidad",
                    description = "Spray lubricante repelente al agua y polvo"
                )
            )

            db.inventoryDao().insertAll(initialInventory)

            // Seed Sample Initial Orders
            val order1 = ServiceOrder(
                orderNumber = "OT-1001",
                clientName = "Carlos Mendoza",
                clientPhone = "+34 611 234 567",
                vehicleType = VehicleType.MOTO.name,
                vehicleBrandModel = "Yamaha FZ 2.0 150cc",
                vehiclePlateOrSerial = "ABC-458",
                vehicleColor = "Azul / Negro",
                vehicleMetric = "24,500 km",
                issueReported = "Mantenimiento general, cambio de aceite y frenos traseros chillan",
                diagnosisNotes = "Se realizó cambio de aceite 10W40, limpieza de carburador y cambio de pastillas de freno.",
                status = OrderStatus.LISTO.name,
                laborDescription = "Servicio de Mantenimiento Preventivo Mayor",
                laborCost = 25.00,
                discount = 0.0,
                totalAmount = 51.50,
                paymentMethod = PaymentMethod.EFECTIVO.name,
                isPaid = false,
                createdAt = System.currentTimeMillis() - 86400000L * 2
            )
            val orderId1 = db.serviceOrderDao().insertOrder(order1)
            db.serviceOrderDao().insertOrderItems(listOf(
                OrderItem(orderId = orderId1, itemName = "Aceite Sintético 4T 10W40 (1L)", quantity = 1, unitPrice = 14.00, subtotal = 14.00),
                OrderItem(orderId = orderId1, itemName = "Pastillas de Freno Delanteras (Moto)", quantity = 1, unitPrice = 12.50, subtotal = 12.50)
            ))

            val order2 = ServiceOrder(
                orderNumber = "OT-1002",
                clientName = "Lucía Gómez",
                clientPhone = "+34 622 876 543",
                vehicleType = VehicleType.MONOPATIN.name,
                vehicleBrandModel = "Xiaomi Mi Pro 2",
                vehiclePlateOrSerial = "SN-XP2-89210",
                vehicleColor = "Negro",
                vehicleMetric = "36V (82% Salud)",
                issueReported = "Rueda trasera pinchada, holgura en el mástil de plegado",
                diagnosisNotes = "Instalación de rueda maciza antipinchazos y ajuste de holgura con almohadilla de goma.",
                status = OrderStatus.EN_PROCESO.name,
                laborDescription = "Montaje rueda scooter + Calibración mástil",
                laborCost = 15.00,
                discount = 0.0,
                totalAmount = 33.00,
                paymentMethod = PaymentMethod.TARJETA.name,
                isPaid = false,
                createdAt = System.currentTimeMillis() - 86400000L
            )
            val orderId2 = db.serviceOrderDao().insertOrder(order2)
            db.serviceOrderDao().insertOrderItems(listOf(
                OrderItem(orderId = orderId2, itemName = "Neumático Macizo Antipinchazos 8.5\"", quantity = 1, unitPrice = 18.00, subtotal = 18.00)
            ))

            val order3 = ServiceOrder(
                orderNumber = "OT-1003",
                clientName = "Andrés Rivas",
                clientPhone = "+34 633 456 789",
                vehicleType = VehicleType.BICICLETA.name,
                vehicleBrandModel = "Trek Marlin 7 MTB",
                vehiclePlateOrSerial = "TRK-98214-B",
                vehicleColor = "Rojo / Gris",
                vehicleMetric = "650 km estimados",
                issueReported = "Cadena salta en piñones pequeños, purga de frenos hidráulicos",
                diagnosisNotes = "Cadena con elongación del 0.8%. Se reemplaza cadena KMC 9V y se purgan frenos Shimano.",
                status = OrderStatus.ENTREGADO.name,
                laborDescription = "Ajuste de transmisión + Purga de frenos",
                laborCost = 20.00,
                discount = 2.00,
                totalAmount = 34.50,
                paymentMethod = PaymentMethod.TRANSFERENCIA.name,
                isPaid = true,
                createdAt = System.currentTimeMillis() - 86400000L * 4,
                completedAt = System.currentTimeMillis() - 86400000L * 3
            )
            val orderId3 = db.serviceOrderDao().insertOrder(order3)
            db.serviceOrderDao().insertOrderItems(listOf(
                OrderItem(orderId = orderId3, itemName = "Cadena 9 Velocidades KMC", quantity = 1, unitPrice = 16.50, subtotal = 16.50)
            ))
        }
    }
}
