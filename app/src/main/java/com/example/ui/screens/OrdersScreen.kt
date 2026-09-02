package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
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
import com.example.data.models.VehicleType
import com.example.data.models.WorkshopSettings
import com.example.ui.components.OrderDetailDialog
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.ReceiptDialog
import com.example.ui.components.VehicleBadge
import com.example.ui.components.getVehicleColor
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WorkshopBorder
import com.example.ui.theme.WorkshopSlateBg
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant
import com.example.ui.viewmodel.TallerViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

val STATUS_FILTER_OPTIONS = listOf(
    "TODOS" to "Todos",
    "RECIBIDO" to "Recibidos",
    "EN_PROCESO" to "En Reparación",
    "LISTO" to "Listos",
    "ENTREGADO" to "Entregados"
)

@Composable
fun OrdersScreen(
    viewModel: TallerViewModel,
    onNavigateToPos: () -> Unit,
    modifier: Modifier = Modifier
) {
    val orders by viewModel.ordersList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.orderSearchQuery.collectAsStateWithLifecycle()
    val statusFilter by viewModel.orderStatusFilter.collectAsStateWithLifecycle()
    val vehicleFilter by viewModel.orderVehicleFilter.collectAsStateWithLifecycle()

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

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Órdenes de Trabajo",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Seguimiento e historial de reparaciones",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = TealPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TealPrimary.copy(alpha = 0.3f))
                ) {
                    Text(
                        text = "${orders.size} órdenes",
                        color = TealPrimary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setOrderSearchQuery(it) },
                placeholder = { Text("Buscar por placa, serie, cliente, modelo...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setOrderSearchQuery("") }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("orders_search_input"),
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = TealPrimary,
                    unfocusedBorderColor = WorkshopBorder,
                    focusedContainerColor = WorkshopSurface,
                    unfocusedContainerColor = WorkshopSurface
                )
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Status Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(STATUS_FILTER_OPTIONS) { (key, label) ->
                    val isSelected = statusFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setOrderStatusFilter(key) },
                        label = { Text(label, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = TealPrimary,
                            selectedLabelColor = Color(0xFF00382E),
                            containerColor = WorkshopSurfaceVariant,
                            labelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            borderColor = if (isSelected) TealPrimary else WorkshopBorder,
                            selectedBorderColor = TealPrimary,
                            enabled = true,
                            selected = isSelected
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Vehicle Type Filter Chips
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                item {
                    val isAll = vehicleFilter == null
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable { viewModel.setOrderVehicleFilter(null) },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isAll) MaterialTheme.colorScheme.surfaceVariant else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isAll) TealPrimary else WorkshopBorder)
                    ) {
                        Text(
                            text = "Todos los vehículos",
                            fontSize = 11.sp,
                            fontWeight = if (isAll) FontWeight.Bold else FontWeight.Normal,
                            color = if (isAll) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }

                items(VehicleType.entries) { vType ->
                    val isSelected = vehicleFilter == vType
                    val vColor = getVehicleColor(vType)
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .clickable {
                                viewModel.setOrderVehicleFilter(if (isSelected) null else vType)
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) vColor.copy(alpha = 0.2f) else Color.Transparent,
                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) vColor else WorkshopBorder)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = vType.icon,
                                contentDescription = null,
                                tint = if (isSelected) vColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.size(13.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = vType.shortName,
                                fontSize = 11.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                color = if (isSelected) vColor else MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Orders List
            if (orders.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Build,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || statusFilter != "TODOS" || vehicleFilter != null)
                                "No se encontraron órdenes con esos filtros"
                            else "No hay órdenes de servicio activas",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = onNavigateToPos,
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Add,
                                contentDescription = null,
                                tint = Color(0xFF00382E),
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Crear Nueva Orden / Cobro", color = Color(0xFF00382E), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(orders, key = { it.order.id }) { orderWithItems ->
                        OrderCard(
                            orderWithItems = orderWithItems,
                            settings = settings,
                            onClick = { viewModel.openOrderDetail(orderWithItems) },
                            onAdvanceStatus = { viewModel.advanceOrderStatus(orderWithItems) },
                            onShowReceipt = { viewModel.showReceipt(orderWithItems) }
                        )
                    }
                }
            }
        }

        // FAB to create new order / pos sale
        FloatingActionButton(
            onClick = onNavigateToPos,
            containerColor = TealPrimary,
            contentColor = Color(0xFF00382E),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .testTag("fab_new_order")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Nueva Orden")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Nueva Orden", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun OrderCard(
    orderWithItems: OrderWithItems,
    settings: WorkshopSettings = WorkshopSettings(),
    onClick: () -> Unit,
    onAdvanceStatus: () -> Unit,
    onShowReceipt: () -> Unit
) {
    val order = orderWithItems.order
    val dateFormat = SimpleDateFormat("dd MMM, HH:mm", Locale.getDefault())
    val dateStr = dateFormat.format(Date(order.createdAt))

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("order_card_${order.orderNumber}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
        border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top row: Order Number, Vehicle Badge, Status Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    VehicleBadge(vehicleType = orderWithItems.vehicleEnum)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                OrderStatusBadge(status = orderWithItems.statusEnum)
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Vehicle Brand/Model and Plate/Serial
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = order.vehicleBrandModel,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                if (order.vehiclePlateOrSerial.isNotBlank()) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = WorkshopSurfaceVariant
                    ) {
                        Text(
                            text = order.vehiclePlateOrSerial,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }

            // Client & Date
            Row(
                modifier = Modifier.padding(top = 2.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = order.clientName,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Text(text = "•", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(
                    text = dateStr,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Reported Issue preview
            if (order.issueReported.isNotBlank()) {
                Text(
                    text = order.issueReported,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 4.dp)
                )
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WorkshopBorder)

            // Bottom row: Total Amount + Quick Status Action Button
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "TOTAL",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = settings.formatPrice(order.totalAmount),
                        fontSize = 17.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary
                    )
                }

                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = onShowReceipt,
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Receipt,
                            contentDescription = "Comprobante",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    when (orderWithItems.statusEnum) {
                        OrderStatus.RECIBIDO -> {
                            Button(
                                onClick = onAdvanceStatus,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = AmberSecondary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.PlayArrow,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Iniciar", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        OrderStatus.EN_PROCESO -> {
                            Button(
                                onClick = onAdvanceStatus,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = GreenSuccess),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = Color.Black,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("Listo", color = Color.Black, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        OrderStatus.LISTO -> {
                            Button(
                                onClick = onAdvanceStatus,
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text("Cobrar", color = Color(0xFF00382E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                        OrderStatus.ENTREGADO -> {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = GreenSuccess.copy(alpha = 0.15f)
                            ) {
                                Text(
                                    text = "Pagado ✓",
                                    color = GreenSuccess,
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                        OrderStatus.CANCELADO -> {}
                    }
                }
            }
        }
    }
}
