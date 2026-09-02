package com.example.ui.components

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.OrderWithItems
import com.example.data.models.WorkshopSettings
import com.example.ui.screens.LOGO_PRESETS
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WorkshopBorder
import com.example.ui.theme.WorkshopSlateBg
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ReceiptDialog(
    orderWithItems: OrderWithItems,
    settings: WorkshopSettings = WorkshopSettings(),
    onDismiss: () -> Unit
) {
    val context = LocalContext.current
    val order = orderWithItems.order
    val items = orderWithItems.items
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(order.createdAt))

    val logoPreset = LOGO_PRESETS.firstOrNull { it.id == settings.logoIconName } ?: LOGO_PRESETS.first()

    fun shareTicketText() {
        val sb = StringBuilder()
        sb.appendLine("=================================")
        sb.appendLine("  ${settings.workshopName.uppercase(Locale.ROOT)}")
        if (settings.tagline.isNotBlank()) sb.appendLine("  ${settings.tagline}")
        if (settings.taxId.isNotBlank()) sb.appendLine("  ID/RUT/NIT: ${settings.taxId}")
        if (settings.address.isNotBlank()) sb.appendLine("  Dir: ${settings.address}")
        if (settings.phone.isNotBlank()) sb.appendLine("  Tel/WhatsApp: ${settings.phone}")
        sb.appendLine("=================================")
        sb.appendLine("ORDEN / TICKET: ${order.orderNumber}")
        sb.appendLine("FECHA: $dateStr")
        sb.appendLine("ESTADO: ${orderWithItems.statusEnum.label}")
        sb.appendLine("---------------------------------")
        sb.appendLine("CLIENTE: ${order.clientName}")
        if (order.clientPhone.isNotBlank()) sb.appendLine("TELÉFONO: ${order.clientPhone}")
        sb.appendLine("VEHÍCULO: ${orderWithItems.vehicleEnum.displayName}")
        sb.appendLine("MODELO: ${order.vehicleBrandModel}")
        if (order.vehiclePlateOrSerial.isNotBlank()) {
            sb.appendLine("ID/PLACA/SERIE: ${order.vehiclePlateOrSerial}")
        }
        if (order.vehicleMetric.isNotBlank()) {
            sb.appendLine("MÉTRICA/KM: ${order.vehicleMetric}")
        }
        sb.appendLine("---------------------------------")
        sb.appendLine("MOTIVO / TRABAJO:")
        sb.appendLine("  ${order.issueReported}")
        if (order.diagnosisNotes.isNotBlank()) {
            sb.appendLine("DIAGNÓSTICO / NOTAS:")
            sb.appendLine("  ${order.diagnosisNotes}")
        }
        sb.appendLine("---------------------------------")
        sb.appendLine("DETALLE DE SERVICIOS Y REPUESTOS:")
        if (order.laborCost > 0 || order.laborDescription.isNotBlank()) {
            sb.appendLine("• ${order.laborDescription}: ${settings.formatPrice(order.laborCost)}")
        }
        items.forEach { item ->
            sb.appendLine("• ${item.quantity}x ${item.itemName} (${settings.formatPrice(item.unitPrice)}): ${settings.formatPrice(item.subtotal)}")
        }
        sb.appendLine("---------------------------------")
        if (order.discount > 0) {
            sb.appendLine("DESCUENTO: -${settings.formatPrice(order.discount)}")
        }
        sb.appendLine("TOTAL A PAGAR: ${settings.formatPrice(order.totalAmount)}")
        sb.appendLine("MÉTODO DE PAGO: ${order.paymentMethod}")
        sb.appendLine("ESTADO DE PAGO: ${if (order.isPaid) "PAGADO ✓" else "PENDIENTE"}")
        sb.appendLine("=================================")
        if (settings.receiptFooterNote.isNotBlank()) {
            sb.appendLine(settings.receiptFooterNote)
        } else {
            sb.appendLine("¡Gracias por confiar en nuestro taller!")
        }

        val sendIntent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_TEXT, sb.toString())
            type = "text/plain"
        }
        val shareIntent = Intent.createChooser(sendIntent, "Compartir Comprobante / Ticket")
        context.startActivity(shareIntent)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.94f)
                .padding(vertical = 24.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Receipt,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Comprobante / Ticket",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.Filled.Close,
                            contentDescription = "Cerrar",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Thermal receipt paper styling container
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .clip(RoundedCornerShape(10.dp))
                        .background(Color(0xFF0F172A))
                        .border(1.dp, Color(0xFF334155), RoundedCornerShape(10.dp))
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp)
                ) {
                    Column(modifier = Modifier.fillMaxWidth()) {
                        // Workshop Logo & Name
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Surface(
                                shape = CircleShape,
                                color = logoPreset.color.copy(alpha = 0.2f),
                                modifier = Modifier.size(28.dp)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Icon(
                                        imageVector = logoPreset.icon,
                                        contentDescription = null,
                                        tint = logoPreset.color,
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = settings.workshopName.uppercase(Locale.ROOT),
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp,
                                color = TealPrimary,
                                textAlign = TextAlign.Center
                            )
                        }

                        if (settings.tagline.isNotBlank()) {
                            Text(
                                text = settings.tagline,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (settings.taxId.isNotBlank()) {
                            Text(
                                text = "ID/RUT/NIT: ${settings.taxId}",
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        if (settings.address.isNotBlank() || settings.phone.isNotBlank()) {
                            Text(
                                text = listOfNotNull(
                                    settings.address.takeIf { it.isNotBlank() },
                                    settings.phone.takeIf { it.isNotBlank() }
                                ).joinToString(" • "),
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF64748B),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        // Info row
                        ReceiptMonospaceRow(left = "TICKET #:", right = order.orderNumber, isBold = true)
                        ReceiptMonospaceRow(left = "FECHA:", right = dateStr)
                        ReceiptMonospaceRow(left = "CLIENTE:", right = order.clientName)
                        if (order.clientPhone.isNotBlank()) {
                            ReceiptMonospaceRow(left = "TELÉFONO:", right = order.clientPhone)
                        }
                        ReceiptMonospaceRow(left = "TIPO:", right = orderWithItems.vehicleEnum.shortName)
                        ReceiptMonospaceRow(left = "VEHÍCULO:", right = order.vehicleBrandModel)
                        if (order.vehiclePlateOrSerial.isNotBlank()) {
                            ReceiptMonospaceRow(left = "PLACA/SERIE:", right = order.vehiclePlateOrSerial)
                        }
                        if (order.vehicleMetric.isNotBlank()) {
                            ReceiptMonospaceRow(left = "MÉTRICA/KM:", right = order.vehicleMetric)
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "DETALLE DE COBRO",
                            fontFamily = FontFamily.Monospace,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            color = AmberSecondary
                        )
                        Spacer(modifier = Modifier.height(4.dp))

                        if (order.laborCost > 0 || order.laborDescription.isNotBlank()) {
                            ReceiptMonospaceRow(
                                left = order.laborDescription.take(24),
                                right = settings.formatPrice(order.laborCost)
                            )
                        }

                        items.forEach { item ->
                            ReceiptMonospaceRow(
                                left = "${item.quantity}x ${item.itemName.take(22)}",
                                right = settings.formatPrice(item.subtotal)
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
                        Spacer(modifier = Modifier.height(8.dp))

                        if (order.discount > 0) {
                            ReceiptMonospaceRow(
                                left = "DESCUENTO:",
                                right = "-${settings.formatPrice(order.discount)}"
                            )
                        }

                        ReceiptMonospaceRow(
                            left = "TOTAL:",
                            right = settings.formatPrice(order.totalAmount),
                            isBold = true,
                            fontSize = 16.sp,
                            color = TealPrimary
                        )

                        Spacer(modifier = Modifier.height(4.dp))
                        ReceiptMonospaceRow(left = "MÉTODO:", right = order.paymentMethod)
                        ReceiptMonospaceRow(
                            left = "ESTADO PAGO:",
                            right = if (order.isPaid) "PAGADO ✓" else "PENDIENTE",
                            color = if (order.isPaid) Color(0xFF10B981) else Color(0xFFF59E0B)
                        )

                        if (settings.receiptFooterNote.isNotBlank()) {
                            Spacer(modifier = Modifier.height(8.dp))
                            HorizontalDivider(color = Color(0xFF334155), thickness = 1.dp)
                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = settings.receiptFooterNote,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 10.sp,
                                color = Color(0xFF94A3B8),
                                modifier = Modifier.fillMaxWidth(),
                                textAlign = TextAlign.Center
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cerrar")
                    }

                    Button(
                        onClick = { shareTicketText() },
                        modifier = Modifier.weight(1.5f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Share,
                            contentDescription = null,
                            tint = Color(0xFF00382E),
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Compartir", color = Color(0xFF00382E), fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun ReceiptMonospaceRow(
    left: String,
    right: String,
    isBold: Boolean = false,
    fontSize: androidx.compose.ui.unit.TextUnit = 12.sp,
    color: Color = Color(0xFFF8FAFC)
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = left,
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Normal,
            color = if (isBold) color else Color(0xFF94A3B8),
            modifier = Modifier.weight(1f, fill = false)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = right,
            fontFamily = FontFamily.Monospace,
            fontSize = fontSize,
            fontWeight = if (isBold) FontWeight.Bold else FontWeight.Medium,
            color = color
        )
    }
}
