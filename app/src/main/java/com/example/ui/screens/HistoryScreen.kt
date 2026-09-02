package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.OrderStatus
import com.example.data.models.OrderWithItems
import com.example.data.models.PaymentMethod
import com.example.data.models.WorkshopSettings
import com.example.ui.components.OrderDetailDialog
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.VehicleBadge
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.SkyTertiary
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WorkshopBorder
import com.example.ui.theme.WorkshopSlateBg
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant
import com.example.ui.viewmodel.TallerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun HistoryScreen(
    viewModel: TallerViewModel,
    modifier: Modifier = Modifier
) {
    val historyList by viewModel.vehicleHistoryList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.historyVehicleQuery.collectAsStateWithLifecycle()

    val selectedOrder by viewModel.selectedOrder.collectAsStateWithLifecycle()
    val showReceiptDialog by viewModel.showReceiptDialog.collectAsStateWithLifecycle()
    val settings by viewModel.workshopSettings.collectAsStateWithLifecycle()

    // Dialogs
    selectedOrder?.let { order ->
        if (showReceiptDialog) {
            ReceiptDialog(
                orderWithItems = order,
                settings = settings,
                onDismiss = { viewModel.closeReceiptDialog() }
            )
        } else {
            OrderDetailDialog(
                orderWithItems = order,
                settings = settings,
                onDismiss = { viewModel.closeOrderDetail() },
                onAdvanceStatus = { viewModel.advanceOrderStatus(order) },
                onSetStatus = { newStatus -> viewModel.setSpecificOrderStatus(order, newStatus) },
                onShowReceipt = { viewModel.showReceipt(order) },
                onDeleteOrder = { viewModel.deleteOrder(order) }
            )
        }
    }

    // Calculate revenue stats
    val totalRevenue = historyList.sumOf { it.order.totalAmount }
    val paidRevenue = historyList.filter { it.order.isPaid || it.order.status == OrderStatus.ENTREGADO.name }.sumOf { it.order.totalAmount }
    val cashRevenue = historyList.filter { it.order.paymentMethod == PaymentMethod.EFECTIVO.name }.sumOf { it.order.totalAmount }
    val cardRevenue = historyList.filter { it.order.paymentMethod == PaymentMethod.TARJETA.name }.sumOf { it.order.totalAmount }
    val transferRevenue = historyList.filter { it.order.paymentMethod == PaymentMethod.TRANSFERENCIA.name }.sumOf { it.order.totalAmount }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
            contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
        ) {
            // Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Caja e Historial",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Historial clínico por vehículo y balance de caja",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Summary Financial Cards
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Total Paid
                    Card(
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.4f))
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "TOTAL COBRADO",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary,
                                    letterSpacing = 0.8.sp
                                )
                                Icon(
                                    imageVector = Icons.Filled.TrendingUp,
                                    contentDescription = null,
                                    tint = TealPrimary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = settings.formatPrice(paidRevenue),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = "${historyList.size} servicios registrados",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // Cash & Cards Breakdown Card
                    Card(
                        modifier = Modifier.weight(1.2f),
                        shape = RoundedCornerShape(14.dp),
                        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
                    ) {
                        Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                            Text(
                                text = "MÉTODOS DE PAGO",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                letterSpacing = 0.8.sp
                            )
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Efectivo:", fontSize = 11.sp, color = GreenSuccess)
                                Text(text = settings.formatPrice(cashRevenue), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Tarjeta:", fontSize = 11.sp, color = SkyTertiary)
                                Text(text = settings.formatPrice(cardRevenue), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text(text = "Transferencia:", fontSize = 11.sp, color = AmberSecondary)
                                Text(text = settings.formatPrice(transferRevenue), fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }

            // Search Bar for Vehicle History (Plate, VIN, Brand, Client)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Text(
                            text = "HISTORIAL POR VEHÍCULO / CLIENTE",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { viewModel.setHistoryVehicleQuery(it) },
                            placeholder = { Text("Escribe placa, ID serial, modelo o cliente...") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Filled.Search,
                                    contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            trailingIcon = {
                                if (searchQuery.isNotEmpty()) {
                                    IconButton(onClick = { viewModel.setHistoryVehicleQuery("") }) {
                                        Icon(imageVector = Icons.Filled.Clear, contentDescription = "Limpiar")
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("history_search_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = WorkshopBorder,
                                focusedContainerColor = WorkshopSurfaceVariant,
                                unfocusedContainerColor = WorkshopSurfaceVariant
                            )
                        )
                    }
                }
            }

            // List of historic orders
            if (historyList.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Filled.History,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                            Text(
                                text = if (searchQuery.isNotBlank()) "No hay registros coincidentes" else "No hay historial registrado",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else {
                items(historyList, key = { it.order.id }) { orderWithItems ->
                    HistoricOrderCard(
                        orderWithItems = orderWithItems,
                        settings = settings,
                        onClick = { viewModel.openOrderDetail(orderWithItems) },
                        onShowReceipt = { viewModel.showReceipt(orderWithItems) }
                    )
                }
            }
        }
    }
}

@Composable
fun HistoricOrderCard(
    orderWithItems: OrderWithItems,
    settings: WorkshopSettings = WorkshopSettings(),
    onClick: () -> Unit,
    onShowReceipt: () -> Unit
) {
    val order = orderWithItems.order
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(order.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("history_order_${order.orderNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Row 1: Vehicle Badge, Plate/Serial, Order Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VehicleBadge(vehicleType = orderWithItems.vehicleEnum)
                    Spacer(modifier = Modifier.width(8.dp))
                    if (order.vehiclePlateOrSerial.isNotBlank()) {
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = WorkshopSurfaceVariant
                        ) {
                            Text(
                                text = order.vehiclePlateOrSerial,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(6.dp))
                    }
                    Text(
                        text = order.orderNumber,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OrderStatusBadge(status = orderWithItems.statusEnum)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vehicle model & Client
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.vehicleBrandModel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = dateStr,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Row(modifier = Modifier.padding(top = 2.dp)) {
                Text(
                    text = "Cliente: ${order.clientName}",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (order.vehicleMetric.isNotBlank()) {
                    Text(text = " • ${order.vehicleMetric}", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }

            if (order.issueReported.isNotBlank()) {
                Text(
                    text = "Trabajo: ${order.issueReported}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WorkshopBorder)

            // Bottom Total & Ticket action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = settings.formatPrice(order.totalAmount),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                    Text(
                        text = "(${order.paymentMethod})",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onShowReceipt, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Filled.Receipt,
                        contentDescription = "Ver Comprobante",
                        tint = TealPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}
