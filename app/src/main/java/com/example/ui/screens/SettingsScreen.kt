package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Badge
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CurrencyExchange
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.DirectionsBike
import androidx.compose.material.icons.filled.ElectricBike
import androidx.compose.material.icons.filled.ElectricMoped
import androidx.compose.material.icons.filled.ElectricScooter
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Notes
import androidx.compose.material.icons.filled.Numbers
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Store
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.RadioButton
import androidx.compose.material3.RadioButtonDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.models.ThousandsSeparator
import com.example.data.models.WorkshopCurrency
import com.example.data.models.WorkshopSettings
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.SkyTertiary
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WorkshopBorder
import com.example.ui.theme.WorkshopSlateBg
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant
import com.example.ui.viewmodel.TallerViewModel
import kotlinx.coroutines.launch

data class LogoPreset(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color
)

val LOGO_PRESETS = listOf(
    LogoPreset("WRENCH", "Herramientas", Icons.Filled.Build, TealPrimary),
    LogoPreset("MOTO", "Moto", Icons.Filled.ElectricMoped, AmberSecondary),
    LogoPreset("BICI", "Bicicleta", Icons.Filled.DirectionsBike, SkyTertiary),
    LogoPreset("SCOOTER", "Scooter", Icons.Filled.ElectricScooter, GreenSuccess),
    LogoPreset("EBIKE", "E-Bike", Icons.Filled.ElectricBike, TealPrimary),
    LogoPreset("BOLT", "Eléctrico", Icons.Filled.Bolt, Color(0xFFFBBF24)),
    LogoPreset("SPEED", "Velocidad", Icons.Filled.Speed, Color(0xFFF43F5E)),
    LogoPreset("STORE", "Taller/Local", Icons.Filled.Store, Color(0xFFA855F7)),
    LogoPreset("SHIELD", "Garantía Pro", Icons.Filled.Security, Color(0xFF38BDF8))
)

@Composable
fun SettingsScreen(
    viewModel: TallerViewModel,
    modifier: Modifier = Modifier
) {
    val currentSettings by viewModel.workshopSettings.collectAsStateWithLifecycle()

    var workshopName by remember(currentSettings) { mutableStateOf(currentSettings.workshopName) }
    var tagline by remember(currentSettings) { mutableStateOf(currentSettings.tagline) }
    var address by remember(currentSettings) { mutableStateOf(currentSettings.address) }
    var phone by remember(currentSettings) { mutableStateOf(currentSettings.phone) }
    var taxId by remember(currentSettings) { mutableStateOf(currentSettings.taxId) }
    var logoIconName by remember(currentSettings) { mutableStateOf(currentSettings.logoIconName) }
    var selectedCurrency by remember(currentSettings) { mutableStateOf(currentSettings.currency) }
    var customCurrencySymbol by remember(currentSettings) { mutableStateOf(currentSettings.customCurrencySymbol) }
    var thousandsSeparator by remember(currentSettings) { mutableStateOf(currentSettings.thousandsSeparator) }
    var showDecimals by remember(currentSettings) { mutableStateOf(currentSettings.showDecimals) }
    var receiptFooterNote by remember(currentSettings) { mutableStateOf(currentSettings.receiptFooterNote) }

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    var showSavedMessage by remember { mutableStateOf(false) }

    // Live preview settings
    val previewSettings = remember(
        workshopName, tagline, address, phone, taxId, logoIconName,
        selectedCurrency, customCurrencySymbol, thousandsSeparator, showDecimals, receiptFooterNote
    ) {
        WorkshopSettings(
            workshopName = workshopName,
            tagline = tagline,
            address = address,
            phone = phone,
            taxId = taxId,
            logoIconName = logoIconName,
            currency = selectedCurrency,
            customCurrencySymbol = customCurrencySymbol,
            thousandsSeparator = thousandsSeparator,
            showDecimals = showDecimals,
            receiptFooterNote = receiptFooterNote
        )
    }

    LaunchedEffect(showSavedMessage) {
        if (showSavedMessage) {
            kotlinx.coroutines.delay(2500)
            showSavedMessage = false
        }
    }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(top = 12.dp, bottom = 90.dp)
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
                            text = "Configuración del Taller",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Text(
                            text = "Personaliza datos de negocio, tickets, moneda y formato numérico",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            // Live Format Preview Card
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFF0F172A)),
                    border = BorderStroke(1.5.dp, TealPrimary)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                val currentLogo = LOGO_PRESETS.firstOrNull { it.id == logoIconName } ?: LOGO_PRESETS.first()
                                Surface(
                                    shape = CircleShape,
                                    color = currentLogo.color.copy(alpha = 0.2f),
                                    border = BorderStroke(1.dp, currentLogo.color),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = currentLogo.icon,
                                            contentDescription = null,
                                            tint = currentLogo.color,
                                            modifier = Modifier.size(20.dp)
                                        )
                                    }
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = workshopName.ifBlank { "Nombre del Taller" },
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        color = Color.White
                                    )
                                    if (taxId.isNotBlank()) {
                                        Text(
                                            text = "ID/RUT/NIT: $taxId",
                                            fontSize = 11.sp,
                                            color = TealPrimary
                                        )
                                    }
                                }
                            }

                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = TealPrimary.copy(alpha = 0.15f),
                                border = BorderStroke(1.dp, TealPrimary.copy(alpha = 0.5f))
                            ) {
                                Text(
                                    text = "VISTA PREVIA",
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = TealPrimary,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = Color(0xFF334155))
                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "Ejemplos con formato actual:",
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF94A3B8)
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Column {
                                Text(text = "Servicio menor:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = previewSettings.formatPrice(45.0),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = AmberSecondary,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                            Column(horizontalAlignment = Alignment.End) {
                                Text(text = "Reparación completa:", fontSize = 11.sp, color = Color(0xFF94A3B8))
                                Text(
                                    text = previewSettings.formatPrice(1250350.50),
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = GreenSuccess,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }
                    }
                }
            }

            // Section 1: Workshop Profile
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                    border = BorderStroke(1.dp, WorkshopBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Store, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "DATOS DEL TALLER / NEGOCIO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                letterSpacing = 0.8.sp
                            )
                        }

                        // Logo / Icon Selector
                        Text(
                            text = "Logotipo / Emblema para tickets y encabezados:",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            contentPadding = PaddingValues(vertical = 4.dp)
                        ) {
                            items(LOGO_PRESETS, key = { it.id }) { preset ->
                                val isSelected = logoIconName == preset.id
                                Surface(
                                    shape = RoundedCornerShape(12.dp),
                                    color = if (isSelected) preset.color.copy(alpha = 0.25f) else WorkshopSurfaceVariant,
                                    border = BorderStroke(if (isSelected) 2.dp else 1.dp, if (isSelected) preset.color else WorkshopBorder),
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(12.dp))
                                        .clickable { logoIconName = preset.id }
                                        .testTag("logo_preset_${preset.id}")
                                ) {
                                    Row(
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = preset.icon,
                                            contentDescription = preset.name,
                                            tint = if (isSelected) preset.color else MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Text(
                                            text = preset.name,
                                            fontSize = 12.sp,
                                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                            color = if (isSelected) preset.color else MaterialTheme.colorScheme.onSurface
                                        )
                                    }
                                }
                            }
                        }

                        // Nombre del taller
                        OutlinedTextField(
                            value = workshopName,
                            onValueChange = { workshopName = it },
                            label = { Text("Nombre del Taller *") },
                            leadingIcon = { Icon(Icons.Filled.Store, contentDescription = null, tint = TealPrimary) },
                            modifier = Modifier.fillMaxWidth().testTag("settings_workshop_name"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = WorkshopBorder,
                                focusedContainerColor = WorkshopSurfaceVariant,
                                unfocusedContainerColor = WorkshopSurfaceVariant
                            )
                        )

                        // Slogan / Especialidades
                        OutlinedTextField(
                            value = tagline,
                            onValueChange = { tagline = it },
                            label = { Text("Subtítulo / Especialidades") },
                            placeholder = { Text("Motos • Bicis • Scooters • E-Bikes") },
                            leadingIcon = { Icon(Icons.Filled.Notes, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant) },
                            modifier = Modifier.fillMaxWidth().testTag("settings_workshop_tagline"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = WorkshopBorder,
                                focusedContainerColor = WorkshopSurfaceVariant,
                                unfocusedContainerColor = WorkshopSurfaceVariant
                            )
                        )

                        // Dirección
                        OutlinedTextField(
                            value = address,
                            onValueChange = { address = it },
                            label = { Text("Dirección del Taller") },
                            placeholder = { Text("Av. Principal #123, Local B") },
                            leadingIcon = { Icon(Icons.Filled.LocationOn, contentDescription = null, tint = AmberSecondary) },
                            modifier = Modifier.fillMaxWidth().testTag("settings_workshop_address"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = WorkshopBorder,
                                focusedContainerColor = WorkshopSurfaceVariant,
                                unfocusedContainerColor = WorkshopSurfaceVariant
                            )
                        )

                        // Teléfono / WhatsApp
                        OutlinedTextField(
                            value = phone,
                            onValueChange = { phone = it },
                            label = { Text("Teléfono / WhatsApp de Contacto") },
                            placeholder = { Text("+54 9 11 1234-5678") },
                            leadingIcon = { Icon(Icons.Filled.Phone, contentDescription = null, tint = GreenSuccess) },
                            modifier = Modifier.fillMaxWidth().testTag("settings_workshop_phone"),
                            singleLine = true,
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealPrimary,
                                unfocusedBorderColor = WorkshopBorder,
                                focusedContainerColor = WorkshopSurfaceVariant,
                                unfocusedContainerColor = WorkshopSurfaceVariant
                            )
                        )

                        // RUT / NIT / RUC / CUIT (Opcional)
                        OutlinedTextField(
                            value = taxId,
                            onValueChange = { taxId = it },
                            label = { Text("RUT / NIT / RUC / CUIT (Identificación Fiscal - Opcional)") },
                            placeholder = { Text("Ej: 900.123.456-7 ó 20-12345678-9") },
                            leadingIcon = { Icon(Icons.Filled.Badge, contentDescription = null, tint = SkyTertiary) },
                            modifier = Modifier.fillMaxWidth().testTag("settings_workshop_taxid"),
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

            // Section 2: Moneda (Currency)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                    border = BorderStroke(1.dp, WorkshopBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.CurrencyExchange, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "MONEDA Y SÍMBOLO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Text(
                            text = "Selecciona la moneda principal de cobro:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Currency Selector Chips
                        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            val currenciesRow1 = listOf(WorkshopCurrency.USD, WorkshopCurrency.SOL, WorkshopCurrency.COL)
                            val currenciesRow2 = listOf(WorkshopCurrency.ARS, WorkshopCurrency.MXN, WorkshopCurrency.EUR, WorkshopCurrency.CLP)

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                currenciesRow1.forEach { currency ->
                                    val isSelected = selectedCurrency == currency
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) TealPrimary.copy(alpha = 0.2f) else WorkshopSurfaceVariant,
                                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) TealPrimary else WorkshopBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                selectedCurrency = currency
                                                showDecimals = currency.defaultDecimals
                                            }
                                            .testTag("currency_${currency.name}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 8.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = currency.symbol,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = currency.name,
                                                fontSize = 11.sp,
                                                color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }

                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                currenciesRow2.forEach { currency ->
                                    val isSelected = selectedCurrency == currency
                                    Surface(
                                        shape = RoundedCornerShape(10.dp),
                                        color = if (isSelected) TealPrimary.copy(alpha = 0.2f) else WorkshopSurfaceVariant,
                                        border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) TealPrimary else WorkshopBorder),
                                        modifier = Modifier
                                            .weight(1f)
                                            .clip(RoundedCornerShape(10.dp))
                                            .clickable {
                                                selectedCurrency = currency
                                                showDecimals = currency.defaultDecimals
                                            }
                                            .testTag("currency_${currency.name}")
                                    ) {
                                        Column(
                                            modifier = Modifier.padding(vertical = 10.dp, horizontal = 4.dp),
                                            horizontalAlignment = Alignment.CenterHorizontally
                                        ) {
                                            Text(
                                                text = currency.symbol,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 15.sp,
                                                color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = currency.name,
                                                fontSize = 10.sp,
                                                color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Símbolo personalizado opcional
                        OutlinedTextField(
                            value = customCurrencySymbol,
                            onValueChange = { customCurrencySymbol = it },
                            label = { Text("Símbolo personalizado (Opcional)") },
                            placeholder = { Text("Ej: $ COP, S/., Bs., €") },
                            leadingIcon = { Icon(Icons.Filled.AttachMoney, contentDescription = null, tint = AmberSecondary) },
                            modifier = Modifier.fillMaxWidth().testTag("settings_custom_symbol"),
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

            // Section 3: Separación de Miles y Decimales
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                    border = BorderStroke(1.dp, WorkshopBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.Numbers, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "SEPARADOR DE MILES Y FORMATO NUMÉRICO",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                letterSpacing = 0.8.sp
                            )
                        }

                        Text(
                            text = "Elige cómo deseas visualizar los importes y miles:",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        // Radio options for ThousandsSeparator
                        ThousandsSeparator.entries.forEach { option ->
                            val isSelected = thousandsSeparator == option
                            Surface(
                                shape = RoundedCornerShape(10.dp),
                                color = if (isSelected) TealPrimary.copy(alpha = 0.12f) else WorkshopSurfaceVariant,
                                border = BorderStroke(if (isSelected) 1.5.dp else 1.dp, if (isSelected) TealPrimary else WorkshopBorder),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(10.dp))
                                    .clickable { thousandsSeparator = option }
                                    .testTag("separator_${option.name}")
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(horizontal = 12.dp, vertical = 10.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { thousandsSeparator = option },
                                            colors = RadioButtonDefaults.colors(selectedColor = TealPrimary)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Column {
                                            Text(
                                                text = option.label,
                                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                                fontSize = 13.sp,
                                                color = MaterialTheme.colorScheme.onSurface
                                            )
                                            Text(
                                                text = "Ejemplo: ${previewSettings.copy(thousandsSeparator = option).formatPrice(1250000.50)}",
                                                fontSize = 11.sp,
                                                color = if (isSelected) TealPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                fontFamily = FontFamily.Monospace
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        HorizontalDivider(color = WorkshopBorder, modifier = Modifier.padding(vertical = 4.dp))

                        // Switch Decimals
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Mostrar centavos / decimales",
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 13.sp,
                                    color = MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (showDecimals) "Ej: .50 / ,50 activado" else "Solo números enteros (común en COP, CLP)",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            Switch(
                                checked = showDecimals,
                                onCheckedChange = { showDecimals = it },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = TealPrimary,
                                    checkedTrackColor = TealPrimary.copy(alpha = 0.3f)
                                ),
                                modifier = Modifier.testTag("settings_decimals_switch")
                            )
                        }
                    }
                }
            }

            // Section 4: Pie de Comprobante / Garantía
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = WorkshopSurface),
                    border = BorderStroke(1.dp, WorkshopBorder)
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(imageVector = Icons.Filled.ReceiptLong, contentDescription = null, tint = TealPrimary, modifier = Modifier.size(20.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "PIE DE PÁGINA Y GARANTÍA EN TICKETS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealPrimary,
                                letterSpacing = 0.8.sp
                            )
                        }

                        OutlinedTextField(
                            value = receiptFooterNote,
                            onValueChange = { receiptFooterNote = it },
                            label = { Text("Términos de garantía o agradecimiento") },
                            placeholder = { Text("Ej: Garantía de 30 días en mano de obra. ¡Gracias por su preferencia!") },
                            modifier = Modifier.fillMaxWidth().testTag("settings_footer_note"),
                            minLines = 2,
                            maxLines = 4,
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

            // Section 5: Apariencia y Tema (Modo Oscuro / Claro)
            item {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.4f))
                ) {
                    Column(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = if (currentSettings.isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
                                    contentDescription = null,
                                    tint = if (currentSettings.isDarkMode) AmberSecondary else TealPrimary,
                                    modifier = Modifier.size(22.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Column {
                                    Text(
                                        text = "TEMA DE LA APLICACIÓN",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = TealPrimary,
                                        letterSpacing = 0.8.sp
                                    )
                                    Text(
                                        text = if (currentSettings.isDarkMode) "Modo Oscuro activado (Alto contraste)" else "Modo Claro activado",
                                        fontSize = 12.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }

                            Switch(
                                checked = currentSettings.isDarkMode,
                                onCheckedChange = { viewModel.toggleDarkMode() },
                                modifier = Modifier.testTag("settings_dark_mode_switch"),
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = Color(0xFF00382E),
                                    checkedTrackColor = TealPrimary,
                                    uncheckedThumbColor = Color(0xFF475569),
                                    uncheckedTrackColor = Color(0xFFCBD5E1)
                                )
                            )
                        }
                    }
                }
            }

            // Save Button & Status
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = {
                            val newSettings = WorkshopSettings(
                                workshopName = workshopName.trim().ifBlank { "Taller Pro Multi-Vehículos" },
                                tagline = tagline.trim(),
                                address = address.trim(),
                                phone = phone.trim(),
                                taxId = taxId.trim(),
                                logoIconName = logoIconName,
                                currency = selectedCurrency,
                                customCurrencySymbol = customCurrencySymbol.trim(),
                                thousandsSeparator = thousandsSeparator,
                                showDecimals = showDecimals,
                                receiptFooterNote = receiptFooterNote.trim(),
                                isDarkMode = currentSettings.isDarkMode
                            )
                            viewModel.updateWorkshopSettings(newSettings)
                            showSavedMessage = true
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(52.dp)
                            .testTag("save_settings_button"),
                        shape = RoundedCornerShape(12.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = TealPrimary)
                    ) {
                        Icon(imageVector = Icons.Filled.Save, contentDescription = null, tint = Color(0xFF00382E))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Guardar Configuración",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF00382E)
                        )
                    }

                    AnimatedVisibility(visible = showSavedMessage) {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = GreenSuccess.copy(alpha = 0.15f),
                            border = BorderStroke(1.dp, GreenSuccess),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.CheckCircle,
                                    contentDescription = null,
                                    tint = GreenSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = "¡Configuración y formato guardados correctamente!",
                                    color = GreenSuccess,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
