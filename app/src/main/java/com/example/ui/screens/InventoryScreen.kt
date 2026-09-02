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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.WarningAmber
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.InventoryItem
import com.example.data.models.VehicleType
import com.example.data.models.WorkshopSettings
import com.example.ui.components.INVENTORY_CATEGORIES
import com.example.ui.components.InventoryDialog
import com.example.ui.components.StockBadge
import com.example.ui.components.getVehicleColor
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WorkshopBorder
import com.example.ui.theme.WorkshopSlateBg
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant
import com.example.ui.viewmodel.TallerViewModel
import java.util.Locale

@Composable
fun InventoryScreen(
    viewModel: TallerViewModel,
    modifier: Modifier = Modifier
) {
    val items by viewModel.inventoryList.collectAsStateWithLifecycle()
    val searchQuery by viewModel.inventorySearchQuery.collectAsStateWithLifecycle()
    val selectedCategory by viewModel.inventoryCategoryFilter.collectAsStateWithLifecycle()
    val lowStockOnly by viewModel.inventoryLowStockOnly.collectAsStateWithLifecycle()
    val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
    val settings by viewModel.workshopSettings.collectAsStateWithLifecycle()

    var showAddEditDialog by remember { mutableStateOf(false) }
    var itemToEdit by remember { mutableStateOf<InventoryItem?>(null) }

    if (showAddEditDialog) {
        InventoryDialog(
            itemToEdit = itemToEdit,
            onDismiss = {
                showAddEditDialog = false
                itemToEdit = null
            },
            onSave = { savedItem ->
                viewModel.saveInventoryItem(savedItem)
                showAddEditDialog = false
                itemToEdit = null
            },
            onDelete = { deletedItem ->
                viewModel.deleteInventoryItem(deletedItem)
                showAddEditDialog = false
                itemToEdit = null
            }
        )
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
                        text = "Inventario & Repuestos",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "${items.size} artículos registrados",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                if (lowStockCount > 0) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { viewModel.toggleInventoryLowStockOnly() },
                        shape = RoundedCornerShape(10.dp),
                        color = if (lowStockOnly) AmberSecondary else AmberSecondary.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, AmberSecondary)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.WarningAmber,
                                contentDescription = null,
                                tint = if (lowStockOnly) Color.Black else AmberSecondary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "$lowStockCount bajo stock",
                                color = if (lowStockOnly) Color.Black else AmberSecondary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.setInventorySearchQuery(it) },
                placeholder = { Text("Buscar por nombre, SKU o categoría...") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.setInventorySearchQuery("") }) {
                            Icon(imageVector = Icons.Filled.Clear, contentDescription = "Limpiar")
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("inventory_search_input"),
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

            // Category filter chips
            val allCategories = listOf("Todos") + INVENTORY_CATEGORIES
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(horizontal = 2.dp)
            ) {
                items(allCategories) { cat ->
                    val isSelected = selectedCategory == cat
                    FilterChip(
                        selected = isSelected,
                        onClick = { viewModel.setInventoryCategoryFilter(cat) },
                        label = { Text(cat, fontSize = 12.sp, fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal) },
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

            Spacer(modifier = Modifier.height(10.dp))

            // Inventory List
            if (items.isEmpty()) {
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
                            imageVector = Icons.Filled.Inventory2,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (searchQuery.isNotBlank() || selectedCategory != "Todos" || lowStockOnly)
                                "No se encontraron repuestos con esos filtros"
                            else "No hay repuestos registrados en el inventario",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = {
                                itemToEdit = null
                                showAddEditDialog = true
                            },
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
                            Text("Agregar Primer Repuesto", color = Color(0xFF00382E), fontWeight = FontWeight.Bold)
                        }
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 80.dp)
                ) {
                    items(items, key = { it.id }) { item ->
                        InventoryItemCard(
                            item = item,
                            settings = settings,
                            onClick = {
                                itemToEdit = item
                                showAddEditDialog = true
                            },
                            onAdjustStock = { delta ->
                                viewModel.adjustStock(item.id, delta)
                            }
                        )
                    }
                }
            }
        }

        // FAB to add new part
        FloatingActionButton(
            onClick = {
                itemToEdit = null
                showAddEditDialog = true
            },
            containerColor = TealPrimary,
            contentColor = Color(0xFF00382E),
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(bottom = 16.dp, end = 16.dp)
                .testTag("fab_add_inventory_item")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(imageVector = Icons.Filled.Add, contentDescription = "Nuevo Repuesto")
                Spacer(modifier = Modifier.width(6.dp))
                Text("Nuevo Repuesto", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun InventoryItemCard(
    item: InventoryItem,
    settings: WorkshopSettings = WorkshopSettings(),
    onClick: () -> Unit,
    onAdjustStock: (Int) -> Unit
) {
    val margin = if (item.salePrice > 0) ((item.salePrice - item.costPrice) / item.salePrice) * 100 else 0.0

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(14.dp))
            .clickable(onClick = onClick)
            .testTag("inventory_item_${item.sku}"),
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (item.isLowStock) AmberSecondary.copy(alpha = 0.5f) else WorkshopBorder
        )
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            // Top: SKU, Category, Stock Badge
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = WorkshopSurfaceVariant
                    ) {
                        Text(
                            text = item.sku,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }

                    Text(
                        text = item.category,
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                StockBadge(stock = item.stock, minStock = item.minStock, unit = item.unit)
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Name
            Text(
                text = item.name,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )

            if (item.description.isNotBlank()) {
                Text(
                    text = item.description,
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }

            // Compatible Vehicles Icons
            val compatibleTypes = item.compatibleVehicles.split(",").mapNotNull { raw ->
                VehicleType.entries.find { it.name.equals(raw.trim(), ignoreCase = true) }
            }
            if (compatibleTypes.isNotEmpty()) {
                Row(
                    modifier = Modifier.padding(top = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = "Aplica a:", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    compatibleTypes.forEach { vType ->
                        val vColor = getVehicleColor(vType)
                        Icon(
                            imageVector = vType.icon,
                            contentDescription = vType.shortName,
                            tint = vColor,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }
            }

            HorizontalDivider(modifier = Modifier.padding(vertical = 10.dp), color = WorkshopBorder)

            // Bottom row: Pricing & Quick Restock buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Price & Margin
                Column {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        Text(
                            text = settings.formatPrice(item.salePrice),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                        Text(
                            text = "Costo: ${settings.formatPrice(item.costPrice)}",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(bottom = 2.dp)
                        )
                    }
                    Text(
                        text = "Margen: ${String.format(Locale.US, "%.0f", margin)}%",
                        fontSize = 11.sp,
                        color = if (margin > 0) GreenSuccess else AmberSecondary,
                        fontWeight = FontWeight.Medium
                    )
                }

                // Quick Stock Adjustment Buttons
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onAdjustStock(-1) },
                        shape = RoundedCornerShape(6.dp),
                        color = WorkshopSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
                    ) {
                        Text(
                            text = "-1",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onAdjustStock(1) },
                        shape = RoundedCornerShape(6.dp),
                        color = WorkshopSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
                    ) {
                        Text(
                            text = "+1",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }

                    Surface(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .clickable { onAdjustStock(5) },
                        shape = RoundedCornerShape(6.dp),
                        color = WorkshopSurfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
                    ) {
                        Text(
                            text = "+5",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = GreenSuccess,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }
        }
    }
}
