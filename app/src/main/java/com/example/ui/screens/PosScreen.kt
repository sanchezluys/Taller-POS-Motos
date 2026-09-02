package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payment
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.InventoryItem
import com.example.data.models.PaymentMethod
import com.example.data.models.VehicleType
import com.example.data.models.WorkshopSettings
import com.example.ui.components.StockBadge
import com.example.ui.components.VehicleTypeSelector
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedError
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WorkshopBorder
import com.example.ui.theme.WorkshopSlateBg
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant
import com.example.ui.viewmodel.PosCartItem
import com.example.ui.viewmodel.TallerViewModel
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PosScreen(
    viewModel: TallerViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val formState by viewModel.posForm.collectAsStateWithLifecycle()
    val inventoryItems by viewModel.inventoryList.collectAsStateWithLifecycle()
    val settings by viewModel.workshopSettings.collectAsStateWithLifecycle()

    var showPartPickerSheet by remember { mutableStateOf(false) }
    var partPickerSearch by remember { mutableStateOf("") }
    var partPickerCategory by remember { mutableStateOf("Todos") }

    var showCustomItemDialog by remember { mutableStateOf(false) }
    var customItemName by remember { mutableStateOf("") }
    var customItemPrice by remember { mutableStateOf("") }

    // Inventory Picker Sheet
    if (showPartPickerSheet) {
        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        ModalBottomSheet(
            onDismissRequest = { showPartPickerSheet = false },
            sheetState = sheetState,
            containerColor = WorkshopSurface
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Seleccionar Repuesto del Inventario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    IconButton(onClick = { showPartPickerSheet = false }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Cerrar")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = partPickerSearch,
                    onValueChange = { partPickerSearch = it },
                    placeholder = { Text("Buscar repuesto por nombre o SKU...") },
                    leadingIcon = { Icon(Icons.Filled.Search, contentDescription = null) },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                val filteredItems = inventoryItems.filter { item ->
                    val matchQuery = partPickerSearch.isBlank() ||
                            item.name.contains(partPickerSearch, ignoreCase = true) ||
                            item.sku.contains(partPickerSearch, ignoreCase = true)
                    matchQuery
                }

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(380.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(filteredItems) { item ->
                        Surface(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(10.dp))
                                .clickable {
                                    viewModel.addItemToCart(item)
                                    showPartPickerSheet = false
                                    Toast.makeText(context, "${item.name} agregado", Toast.LENGTH_SHORT).show()
                                },
                            shape = RoundedCornerShape(10.dp),
                            color = WorkshopSurfaceVariant,
                            border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(12.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = item.name,
                                        fontWeight = FontWeight.SemiBold,
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurface
                                    )
                                    Row(
                                        modifier = Modifier.padding(top = 2.dp),
                                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Text(text = item.category, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        StockBadge(stock = item.stock, minStock = item.minStock, unit = item.unit)
                                    }
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = settings.formatPrice(item.salePrice),
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = TealPrimary
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Icon(
                                        imageVector = Icons.Filled.AddShoppingCart,
                                        contentDescription = "Agregar",
                                        tint = TealPrimary,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(horizontal = 16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
        contentPadding = PaddingValues(top = 8.dp, bottom = 80.dp)
    ) {
        // Title Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Punto POS / Recepción",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "Ingreso a taller o venta directa",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                OutlinedButton(
                    onClick = { viewModel.resetPosForm() },
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("Limpiar", fontSize = 12.sp)
                }
            }
        }

        // 1. Vehicle Type Selector Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    VehicleTypeSelector(
                        selectedType = formState.vehicleType,
                        onTypeSelected = { selected ->
                            viewModel.updatePosVehicle(
                                type = selected,
                                brandModel = formState.vehicleBrandModel,
                                plateOrSerial = formState.vehiclePlateOrSerial,
                                color = formState.vehicleColor,
                                metric = formState.vehicleMetric
                            )
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Vehicle Brand/Model & Plate/Serial Row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.vehicleBrandModel,
                            onValueChange = {
                                viewModel.updatePosVehicle(
                                    type = formState.vehicleType,
                                    brandModel = it,
                                    plateOrSerial = formState.vehiclePlateOrSerial,
                                    color = formState.vehicleColor,
                                    metric = formState.vehicleMetric
                                )
                            },
                            label = { Text("Marca / Modelo *") },
                            placeholder = { Text(formState.vehicleType.defaultBrandPlaceholder) },
                            modifier = Modifier.weight(1.2f).testTag("pos_vehicle_model_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = formState.vehiclePlateOrSerial,
                            onValueChange = { input ->
                                val clean = input.uppercase()
                                viewModel.updatePosVehicle(
                                    type = formState.vehicleType,
                                    brandModel = formState.vehicleBrandModel,
                                    plateOrSerial = clean,
                                    color = formState.vehicleColor,
                                    metric = formState.vehicleMetric
                                )
                                // Check if recognized
                                val prev = viewModel.findPreviousVehicleRecord(clean)
                                if (prev != null && formState.clientName.isBlank()) {
                                    viewModel.updatePosClient(prev.order.clientName, prev.order.clientPhone)
                                }
                            },
                            label = { Text(formState.vehicleType.identifierLabel) },
                            placeholder = { Text("ABC-123") },
                            modifier = Modifier.weight(1f).testTag("pos_vehicle_plate_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Metric (Km/Battery) & Color
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.vehicleMetric,
                            onValueChange = {
                                viewModel.updatePosVehicle(
                                    type = formState.vehicleType,
                                    brandModel = formState.vehicleBrandModel,
                                    plateOrSerial = formState.vehiclePlateOrSerial,
                                    color = formState.vehicleColor,
                                    metric = it
                                )
                            },
                            label = { Text(formState.vehicleType.measurementLabel) },
                            placeholder = { Text("Ej. 12,500 km") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = formState.vehicleColor,
                            onValueChange = {
                                viewModel.updatePosVehicle(
                                    type = formState.vehicleType,
                                    brandModel = formState.vehicleBrandModel,
                                    plateOrSerial = formState.vehiclePlateOrSerial,
                                    color = it,
                                    metric = formState.vehicleMetric
                                )
                            },
                            label = { Text("Color") },
                            placeholder = { Text("Negro / Rojo") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // 2. Client Details Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DATOS DEL CLIENTE",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        letterSpacing = 1.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.clientName,
                            onValueChange = { viewModel.updatePosClient(it, formState.clientPhone, formState.clientEmail) },
                            label = { Text("Nombre del Cliente *") },
                            placeholder = { Text("Juan Pérez") },
                            modifier = Modifier.weight(1.2f).testTag("pos_client_name_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = formState.clientPhone,
                            onValueChange = { viewModel.updatePosClient(formState.clientName, it, formState.clientEmail) },
                            label = { Text("Teléfono / WhatsApp") },
                            placeholder = { Text("+54 9 11 ...") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                            modifier = Modifier.weight(1f).testTag("pos_client_phone_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }
        }

        // 3. Issue / Reason for Service & Diagnosis
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MOTIVO DE INGRESO / FALLA REPORTADA",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = AmberSecondary,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = formState.issueReported,
                        onValueChange = { viewModel.updatePosIssueAndDiagnosis(it, formState.diagnosisNotes) },
                        placeholder = { Text("Ej. Mantenimiento general, cambio de aceite, freno flojo, neumático pinchado...") },
                        modifier = Modifier.fillMaxWidth().testTag("pos_issue_input"),
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedTextField(
                        value = formState.diagnosisNotes,
                        onValueChange = { viewModel.updatePosIssueAndDiagnosis(formState.issueReported, it) },
                        label = { Text("Diagnóstico Técnico Inicial (Opcional)") },
                        placeholder = { Text("Notas técnicas o piezas a revisar") },
                        modifier = Modifier.fillMaxWidth(),
                        maxLines = 2,
                        shape = RoundedCornerShape(10.dp)
                    )
                }
            }
        }

        // 4. Parts / Repuestos Cart
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "REPUESTOS E INSUMOS",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary,
                            letterSpacing = 0.8.sp
                        )

                        Button(
                            onClick = { showPartPickerSheet = true },
                            shape = RoundedCornerShape(8.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = TealPrimary),
                            contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                            modifier = Modifier.testTag("pos_add_part_button")
                        ) {
                            Icon(Icons.Filled.Add, contentDescription = null, tint = Color(0xFF00382E), modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Agregar Repuesto", color = Color(0xFF00382E), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    if (formState.cartItems.isEmpty()) {
                        Surface(
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(8.dp),
                            color = WorkshopSurfaceVariant
                        ) {
                            Text(
                                text = "No se han agregado repuestos aún (opcional si es solo mano de obra).",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                modifier = Modifier.padding(12.dp)
                            )
                        }
                    } else {
                        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                            formState.cartItems.forEachIndexed { index, cartItem ->
                                Surface(
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(8.dp),
                                    color = WorkshopSurfaceVariant,
                                    border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
                                ) {
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(8.dp),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = cartItem.name,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "${settings.formatPrice(cartItem.unitPrice)} c/u",
                                                fontSize = 11.sp,
                                                color = MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }

                                        // Qty controls
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                                        ) {
                                            IconButton(
                                                onClick = { viewModel.updateCartItemQuantity(index, cartItem.quantity - 1) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Filled.Remove, contentDescription = "Menos", modifier = Modifier.size(16.dp))
                                            }

                                            Text(
                                                text = "${cartItem.quantity}",
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )

                                            IconButton(
                                                onClick = { viewModel.updateCartItemQuantity(index, cartItem.quantity + 1) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(Icons.Filled.Add, contentDescription = "Más", modifier = Modifier.size(16.dp))
                                            }

                                            Text(
                                                text = settings.formatPrice(cartItem.subtotal),
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp,
                                                color = TealPrimary,
                                                modifier = Modifier.padding(horizontal = 6.dp)
                                            )

                                            IconButton(
                                                onClick = { viewModel.removeCartItem(index) },
                                                modifier = Modifier.size(28.dp)
                                            ) {
                                                Icon(
                                                    Icons.Filled.Delete,
                                                    contentDescription = "Eliminar",
                                                    tint = RedError,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // 5. Labor & Pricing & Payment Method
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                border = androidx.compose.foundation.BorderStroke(1.dp, WorkshopBorder)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "MANO DE OBRA Y COBRO",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = TealPrimary,
                        letterSpacing = 0.8.sp
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.laborDescription,
                            onValueChange = {
                                viewModel.updatePosPricing(
                                    laborDescription = it,
                                    laborCost = formState.laborCostText,
                                    discount = formState.discountText,
                                    paymentMethod = formState.paymentMethod
                                )
                            },
                            label = { Text("Descripción de Servicio") },
                            modifier = Modifier.weight(1.3f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        OutlinedTextField(
                            value = formState.laborCostText,
                            onValueChange = {
                                viewModel.updatePosPricing(
                                    laborDescription = formState.laborDescription,
                                    laborCost = it,
                                    discount = formState.discountText,
                                    paymentMethod = formState.paymentMethod
                                )
                            },
                            label = { Text("Mano de Obra ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f).testTag("pos_labor_cost_input"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = formState.discountText,
                            onValueChange = {
                                viewModel.updatePosPricing(
                                    laborDescription = formState.laborDescription,
                                    laborCost = formState.laborCostText,
                                    discount = it,
                                    paymentMethod = formState.paymentMethod
                                )
                            },
                            label = { Text("Descuento ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp)
                        )

                        // Payment method selector
                        Column(modifier = Modifier.weight(1.3f)) {
                            Text(
                                text = "Método de Pago",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                PaymentMethod.entries.forEach { pm ->
                                    val isSelected = formState.paymentMethod == pm
                                    Surface(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(6.dp))
                                            .clickable {
                                                viewModel.updatePosPricing(
                                                    laborDescription = formState.laborDescription,
                                                    laborCost = formState.laborCostText,
                                                    discount = formState.discountText,
                                                    paymentMethod = pm
                                                )
                                            },
                                        shape = RoundedCornerShape(6.dp),
                                        color = if (isSelected) TealPrimary.copy(alpha = 0.2f) else WorkshopSurfaceVariant,
                                        border = androidx.compose.foundation.BorderStroke(1.dp, if (isSelected) TealPrimary else WorkshopBorder)
                                    ) {
                                        Text(
                                            text = when (pm) {
                                                PaymentMethod.EFECTIVO -> "Efectivo"
                                                PaymentMethod.TARJETA -> "Tarjeta"
                                                PaymentMethod.TRANSFERENCIA -> "Transf."
                                            },
                                            fontSize = 11.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 6.dp)
                                        )
                                    }
                                }
                            }
                        }
                    }

                    HorizontalDivider(modifier = Modifier.padding(vertical = 12.dp), color = WorkshopBorder)

                    // Calculation breakdown
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Mano de Obra:", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = settings.formatPrice(formState.laborCost), fontSize = 13.sp)
                    }
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(text = "Repuestos (${formState.cartItems.sumOf { it.quantity }} items):", fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Text(text = settings.formatPrice(formState.partsSubtotal), fontSize = 13.sp)
                    }
                    if (formState.discount > 0) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(text = "Descuento:", fontSize = 13.sp, color = AmberSecondary)
                            Text(text = "-${settings.formatPrice(formState.discount)}", fontSize = 13.sp, color = AmberSecondary)
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(text = "TOTAL:", fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurface)
                        Text(
                            text = settings.formatPrice(formState.totalAmount),
                            fontSize = 24.sp,
                            fontWeight = FontWeight.Bold,
                            color = TealPrimary
                        )
                    }
                }
            }
        }

        // 6. Action Buttons: Receive in Workshop vs Instant POS Checkout
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                // Reception button (Recibido en Taller)
                OutlinedButton(
                    onClick = {
                        if (formState.isValid) {
                            viewModel.submitOrder(directCheckout = false) { createdOrder ->
                                Toast.makeText(context, "Orden ${createdOrder.order.orderNumber} ingresada al taller", Toast.LENGTH_LONG).show()
                            }
                        } else {
                            Toast.makeText(context, "Por favor ingresa nombre del cliente y marca/modelo", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1f)
                        .height(52.dp)
                        .testTag("pos_submit_workshop_button"),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Filled.ReceiptLong,
                        contentDescription = null,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Ingresar a Taller", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }

                // Direct POS Checkout (Cobrar y Facturar)
                Button(
                    onClick = {
                        if (formState.isValid) {
                            viewModel.submitOrder(directCheckout = true) { createdOrder ->
                                Toast.makeText(context, "Venta completada y cobrada ✓", Toast.LENGTH_SHORT).show()
                                viewModel.showReceipt(createdOrder)
                            }
                        } else {
                            Toast.makeText(context, "Por favor ingresa nombre del cliente y marca/modelo", Toast.LENGTH_SHORT).show()
                        }
                    },
                    modifier = Modifier
                        .weight(1.3f)
                        .height(52.dp)
                        .testTag("pos_submit_checkout_button"),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                ) {
                    Icon(
                        imageVector = Icons.Filled.PointOfSale,
                        contentDescription = null,
                        tint = Color(0xFF00382E),
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cobrar y Facturar", color = Color(0xFF00382E), fontSize = 14.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
