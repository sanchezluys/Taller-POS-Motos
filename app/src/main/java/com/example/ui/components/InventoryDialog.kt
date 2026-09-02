package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.data.models.InventoryItem
import com.example.data.models.VehicleType
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedError
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WorkshopBorder
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant
import java.util.Locale

val INVENTORY_CATEGORIES = listOf(
    "Frenos",
    "Transmisión",
    "Eléctrico / Baterías",
    "Neumáticos",
    "Motor / Aceites",
    "Suspensión",
    "Accesorios"
)

val UNIT_OPTIONS = listOf("Unidad", "Par", "Litro", "Kit", "Metro")

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun InventoryDialog(
    itemToEdit: InventoryItem?,
    onDismiss: () -> Unit,
    onSave: (InventoryItem) -> Unit,
    onDelete: ((InventoryItem) -> Unit)? = null
) {
    var sku by remember { mutableStateOf(itemToEdit?.sku ?: "REP-${(1000..9999).random()}") }
    var name by remember { mutableStateOf(itemToEdit?.name ?: "") }
    var category by remember { mutableStateOf(itemToEdit?.category ?: INVENTORY_CATEGORIES.first()) }
    var categoryExpanded by remember { mutableStateOf(false) }

    var unit by remember { mutableStateOf(itemToEdit?.unit ?: "Unidad") }
    var unitExpanded by remember { mutableStateOf(false) }

    var stockText by remember { mutableStateOf(itemToEdit?.stock?.toString() ?: "10") }
    var minStockText by remember { mutableStateOf(itemToEdit?.minStock?.toString() ?: "3") }
    var costPriceText by remember { mutableStateOf(itemToEdit?.costPrice?.let { String.format(Locale.US, "%.2f", it) } ?: "5.00") }
    var salePriceText by remember { mutableStateOf(itemToEdit?.salePrice?.let { String.format(Locale.US, "%.2f", it) } ?: "10.00") }
    var description by remember { mutableStateOf(itemToEdit?.description ?: "") }

    // Multi-select vehicle compatibility
    val initialVehicles = remember {
        itemToEdit?.compatibleVehicles?.split(",")?.map { it.trim() }?.toSet() ?: setOf("MOTO", "BICICLETA", "MONOPATIN", "MOTO_ELECTRICA")
    }
    var selectedVehicles by remember { mutableStateOf(initialVehicles) }
    var showDeleteConfirm by remember { mutableStateOf(false) }

    val cost = costPriceText.toDoubleOrNull() ?: 0.0
    val sale = salePriceText.toDoubleOrNull() ?: 0.0
    val margin = if (sale > 0) ((sale - cost) / sale) * 100 else 0.0

    if (showDeleteConfirm && itemToEdit != null && onDelete != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Eliminar Repuesto") },
            text = { Text("¿Estás seguro de que deseas eliminar '${itemToEdit.name}' del inventario?") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteConfirm = false
                        onDelete(itemToEdit)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RedError)
                ) {
                    Text("Eliminar", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteConfirm = false }) {
                    Text("Cancelar")
                }
            },
            containerColor = WorkshopSurface
        )
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth(0.95f)
                .padding(vertical = 16.dp),
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
            border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Filled.Inventory2,
                            contentDescription = null,
                            tint = TealPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (itemToEdit == null) "Nuevo Repuesto / Insumo" else "Editar Repuesto",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        if (itemToEdit != null && onDelete != null) {
                            IconButton(onClick = { showDeleteConfirm = true }) {
                                Icon(
                                    imageVector = Icons.Filled.Delete,
                                    contentDescription = "Eliminar",
                                    tint = RedError.copy(alpha = 0.8f)
                                )
                            }
                        }
                        IconButton(onClick = onDismiss) {
                            Icon(
                                imageVector = Icons.Filled.Close,
                                contentDescription = "Cerrar",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Scrollable Form Fields
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f, fill = false)
                        .verticalScroll(rememberScrollState()),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // SKU & Category Row
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = sku,
                            onValueChange = { sku = it.uppercase() },
                            label = { Text("Código / SKU") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = categoryExpanded,
                            onExpandedChange = { categoryExpanded = !categoryExpanded },
                            modifier = Modifier.weight(1.3f)
                        ) {
                            OutlinedTextField(
                                value = category,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Categoría") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false },
                                modifier = androidx.compose.ui.Modifier.background(WorkshopSurfaceVariant)
                            ) {
                                INVENTORY_CATEGORIES.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            category = cat
                                            categoryExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Name
                    OutlinedTextField(
                        value = name,
                        onValueChange = { name = it },
                        label = { Text("Nombre del Repuesto / Pieza *") },
                        placeholder = { Text("Ej. Pastillas de freno, Cámara 8.5\", Cadena 9V...") },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp)
                    )

                    // Vehicle Compatibility Selector
                    Column {
                        Text(
                            text = "COMPATIBILIDAD CON VEHÍCULOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            letterSpacing = 0.8.sp
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            VehicleType.entries.forEach { vType ->
                                val isChecked = selectedVehicles.contains(vType.name)
                                val vColor = getVehicleColor(vType)
                                Surface(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(8.dp))
                                        .clickable {
                                            selectedVehicles = if (isChecked) {
                                                selectedVehicles - vType.name
                                            } else {
                                                selectedVehicles + vType.name
                                            }
                                        },
                                    shape = RoundedCornerShape(8.dp),
                                    color = if (isChecked) vColor.copy(alpha = 0.2f) else WorkshopSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(
                                        1.dp,
                                        if (isChecked) vColor else WorkshopBorder
                                    )
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(
                                            imageVector = vType.icon,
                                            contentDescription = null,
                                            tint = if (isChecked) vColor else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Text(
                                            text = vType.shortName,
                                            fontSize = 12.sp,
                                            fontWeight = if (isChecked) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isChecked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // Stock & Min Stock & Unit
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = stockText,
                            onValueChange = { stockText = it },
                            label = { Text("Stock Actual") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = minStockText,
                            onValueChange = { minStockText = it },
                            label = { Text("Mínimo Alerta") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        ExposedDropdownMenuBox(
                            expanded = unitExpanded,
                            onExpandedChange = { unitExpanded = !unitExpanded },
                            modifier = Modifier.weight(1f)
                        ) {
                            OutlinedTextField(
                                value = unit,
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Unidad") },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = unitExpanded) },
                                modifier = Modifier.menuAnchor(),
                                shape = RoundedCornerShape(10.dp)
                            )
                            ExposedDropdownMenu(
                                expanded = unitExpanded,
                                onDismissRequest = { unitExpanded = false },
                                modifier = androidx.compose.ui.Modifier.background(WorkshopSurfaceVariant)
                            ) {
                                UNIT_OPTIONS.forEach { u ->
                                    DropdownMenuItem(
                                        text = { Text(u, color = MaterialTheme.colorScheme.onSurface) },
                                        onClick = {
                                            unit = u
                                            unitExpanded = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // Pricing (Cost & Sale Price & Live Margin %)
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                        OutlinedTextField(
                            value = costPriceText,
                            onValueChange = { costPriceText = it },
                            label = { Text("Precio Costo ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = salePriceText,
                            onValueChange = { salePriceText = it },
                            label = { Text("Precio Venta ($) *") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    // Margin Pill
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Margen de Ganancia:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = if (margin > 0) GreenSuccess.copy(alpha = 0.15f) else AmberSecondary.copy(alpha = 0.15f)
                        ) {
                            Text(
                                text = "${String.format(Locale.US, "%.1f", margin)}% ($${String.format(Locale.US, "%.2f", (sale - cost).coerceAtLeast(0.0))})",
                                color = if (margin > 0) GreenSuccess else AmberSecondary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    // Description
                    OutlinedTextField(
                        value = description,
                        onValueChange = { description = it },
                        label = { Text("Descripción / Especificaciones (Opcional)") },
                        placeholder = { Text("Medidas, marca, compuesto, compatibilidad especial...") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Bottom Buttons
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("Cancelar")
                    }

                    Button(
                        onClick = {
                            if (name.isNotBlank()) {
                                val newItem = InventoryItem(
                                    id = itemToEdit?.id ?: 0L,
                                    sku = sku.ifBlank { "SKU-${(1000..9999).random()}" },
                                    name = name.trim(),
                                    category = category,
                                    compatibleVehicles = if (selectedVehicles.isEmpty()) "UNIVERSAL" else selectedVehicles.joinToString(","),
                                    stock = stockText.toIntOrNull() ?: 0,
                                    minStock = minStockText.toIntOrNull() ?: 3,
                                    costPrice = cost,
                                    salePrice = sale,
                                    unit = unit,
                                    description = description.trim()
                                )
                                onSave(newItem)
                            }
                        },
                        enabled = name.isNotBlank() && sale > 0,
                        modifier = Modifier.weight(1.4f),
                        shape = RoundedCornerShape(10.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Text(
                            text = if (itemToEdit == null) "Guardar Repuesto" else "Actualizar",
                            color = Color(0xFF00382E),
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
        }
    }
}
