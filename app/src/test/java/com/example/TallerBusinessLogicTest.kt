package com.example

import com.example.data.models.InventoryItem
import com.example.data.models.OrderItem
import com.example.data.models.OrderStatus
import com.example.data.models.PaymentMethod
import com.example.data.models.ServiceOrder
import com.example.data.models.ThousandsSeparator
import com.example.data.models.VehicleType
import com.example.data.models.WorkshopCurrency
import com.example.data.models.WorkshopSettings
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * QA Unit tests for Taller POS business logic, calculations, formatting and data rules.
 */
class TallerBusinessLogicTest {

    @Test
    fun testPriceFormatting_USD_withComma() {
        val settings = WorkshopSettings(
            currency = WorkshopCurrency.USD,
            thousandsSeparator = ThousandsSeparator.COMMA,
            showDecimals = true
        )
        val formatted = settings.formatPrice(1250.50)
        assertEquals("$ 1,250.50", formatted)
    }

    @Test
    fun testPriceFormatting_EUR_withDot() {
        val settings = WorkshopSettings(
            currency = WorkshopCurrency.EUR,
            thousandsSeparator = ThousandsSeparator.DOT,
            showDecimals = true
        )
        val formatted = settings.formatPrice(1250.50)
        assertEquals("1.250,50 €", formatted)
    }

    @Test
    fun testPriceFormatting_NoDecimals() {
        val settings = WorkshopSettings(
            currency = WorkshopCurrency.USD,
            thousandsSeparator = ThousandsSeparator.COMMA,
            showDecimals = false
        )
        val formatted = settings.formatPrice(1250.00)
        assertEquals("$ 1,250", formatted)
    }

    @Test
    fun testPriceFormatting_CustomCurrencySymbol() {
        val settings = WorkshopSettings(
            currency = WorkshopCurrency.USD,
            customCurrencySymbol = "S/.",
            thousandsSeparator = ThousandsSeparator.COMMA,
            showDecimals = true
        )
        val formatted = settings.formatPrice(85.00)
        assertEquals("S/. 85.00", formatted)
    }

    @Test
    fun testPriceFormatting_NegativeNumber() {
        val settings = WorkshopSettings(
            currency = WorkshopCurrency.USD,
            thousandsSeparator = ThousandsSeparator.COMMA,
            showDecimals = true
        )
        val formatted = settings.formatPrice(-25.50)
        assertEquals("-$ 25.50", formatted)
    }

    @Test
    fun testServiceOrderCalculations() {
        val items = listOf(
            OrderItem(orderId = 1L, itemName = "Aceite 10W40", quantity = 2, unitPrice = 12.00, subtotal = 24.00),
            OrderItem(orderId = 1L, itemName = "Bujía NGK", quantity = 1, unitPrice = 8.50, subtotal = 8.50)
        )
        val partsTotal = items.sumOf { it.subtotal }
        val laborCost = 25.00
        val discount = 5.00
        val computedTotal = partsTotal + laborCost - discount

        assertEquals(32.50, partsTotal, 0.001)
        assertEquals(52.50, computedTotal, 0.001)

        val order = ServiceOrder(
            id = 1L,
            orderNumber = "OT-1001",
            clientName = "Juan Pérez",
            clientPhone = "+34 600 000 000",
            vehicleType = VehicleType.MOTO.name,
            vehicleBrandModel = "Yamaha FZ 150",
            vehiclePlateOrSerial = "1234-XYZ",
            issueReported = "Revisión general",
            laborCost = laborCost,
            discount = discount,
            totalAmount = computedTotal,
            status = OrderStatus.RECIBIDO.name,
            paymentMethod = PaymentMethod.EFECTIVO.name,
            isPaid = false
        )

        assertEquals("OT-1001", order.orderNumber)
        assertEquals(52.50, order.totalAmount, 0.001)
        assertFalse(order.isPaid)
        assertEquals(OrderStatus.RECIBIDO.name, order.status)
    }

    @Test
    fun testInventoryStockAndMarginRules() {
        val item = InventoryItem(
            id = 10L,
            sku = "TEST-OIL-01",
            name = "Aceite Motor",
            category = "Motor / Aceites",
            compatibleVehicles = "MOTO",
            stock = 2,
            minStock = 4,
            costPrice = 8.00,
            salePrice = 15.00,
            unit = "Litro"
        )

        // Low stock rule
        val isLowStock = item.stock <= item.minStock
        assertTrue("Item should be identified as low stock", isLowStock)

        // Profit margin calculation
        val unitProfit = item.salePrice - item.costPrice
        val marginPercentage = (unitProfit / item.salePrice) * 100.0
        assertEquals(7.00, unitProfit, 0.001)
        assertEquals(46.67, marginPercentage, 0.01)
    }

    @Test
    fun testOrderStatusCycle() {
        val allStatuses = OrderStatus.entries.map { it.name }
        assertTrue(allStatuses.contains("RECIBIDO"))
        assertTrue(allStatuses.contains("EN_PROCESO"))
        assertTrue(allStatuses.contains("LISTO"))
        assertTrue(allStatuses.contains("ENTREGADO"))
        assertTrue(allStatuses.contains("CANCELADO"))
        assertEquals(5, allStatuses.size)
    }

    @Test
    fun testVehicleTypeAttributes() {
        val moto = VehicleType.MOTO
        assertEquals("Motocicleta (Combustión)", moto.displayName)
        assertEquals("Moto", moto.shortName)

        val monopatin = VehicleType.MONOPATIN
        assertEquals("Monopatín Eléctrico / Scooter", monopatin.displayName)
        assertEquals("Scooter", monopatin.shortName)

        val bici = VehicleType.BICICLETA
        assertEquals("Bicicleta (Ruta / MTB / Urbana)", bici.displayName)
        assertEquals("Bici", bici.shortName)

        val elect = VehicleType.MOTO_ELECTRICA
        assertEquals("Moto Eléctrica / Ciclomotor", elect.displayName)
        assertEquals("Moto Eléctrica", elect.shortName)
    }

    @Test
    fun testDarkModeSettingDefault() {
        val settings = WorkshopSettings()
        assertTrue(settings.isDarkMode)
    }
}
