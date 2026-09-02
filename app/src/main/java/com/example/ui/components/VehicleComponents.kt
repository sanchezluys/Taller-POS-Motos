package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.HourglassTop
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.OrderStatus
import com.example.data.models.VehicleType
import com.example.ui.theme.ColorBici
import com.example.ui.theme.ColorMoto
import com.example.ui.theme.ColorMotoElectrica
import com.example.ui.theme.ColorScooter
import com.example.ui.theme.GreenContainer
import com.example.ui.theme.GreenSuccess
import com.example.ui.theme.RedContainer
import com.example.ui.theme.RedError
import com.example.ui.theme.TealPrimary
import com.example.ui.theme.WorkshopBorder
import com.example.ui.theme.WorkshopSurface
import com.example.ui.theme.WorkshopSurfaceVariant

fun getVehicleColor(type: VehicleType): Color {
    return when (type) {
        VehicleType.MOTO -> ColorMoto
        VehicleType.BICICLETA -> ColorBici
        VehicleType.MONOPATIN -> ColorScooter
        VehicleType.MOTO_ELECTRICA -> ColorMotoElectrica
    }
}

@Composable
fun VehicleBadge(
    vehicleType: VehicleType,
    modifier: Modifier = Modifier,
    showLabel: Boolean = true
) {
    val color = getVehicleColor(vehicleType)
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(8.dp),
        color = color.copy(alpha = 0.15f),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.4f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = vehicleType.icon,
                contentDescription = vehicleType.displayName,
                tint = color,
                modifier = Modifier.size(16.dp)
            )
            if (showLabel) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = vehicleType.shortName,
                    color = color,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun VehicleTypeSelector(
    selectedType: VehicleType,
    onTypeSelected: (VehicleType) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(
            text = "TIPO DE VEHÍCULO",
            style = MaterialTheme.typography.labelSmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.size(6.dp))
        FlowRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            VehicleType.entries.forEach { type ->
                val isSelected = selectedType == type
                val vehicleColor = getVehicleColor(type)
                Surface(
                    modifier = Modifier
                        .clip(RoundedCornerShape(12.dp))
                        .clickable { onTypeSelected(type) },
                    shape = RoundedCornerShape(12.dp),
                    color = if (isSelected) vehicleColor.copy(alpha = 0.2f) else WorkshopSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(
                        width = if (isSelected) 1.5.dp else 1.dp,
                        color = if (isSelected) vehicleColor else WorkshopBorder
                    )
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = type.icon,
                            contentDescription = null,
                            tint = if (isSelected) vehicleColor else MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = type.shortName,
                            color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun OrderStatusBadge(
    status: OrderStatus,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, icon) = when (status) {
        OrderStatus.RECIBIDO -> Triple(Color(0xFF1E3A8A), Color(0xFF93C5FD), Icons.Filled.HourglassTop)
        OrderStatus.EN_PROCESO -> Triple(Color(0xFF78350F), Color(0xFFFDE68A), Icons.Filled.PlayArrow)
        OrderStatus.LISTO -> Triple(GreenContainer, Color(0xFF6EE7B7), Icons.Filled.CheckCircle)
        OrderStatus.ENTREGADO -> Triple(Color(0xFF334155), Color(0xFFCBD5E1), Icons.Filled.CheckCircle)
        OrderStatus.CANCELADO -> Triple(RedContainer, Color(0xFFFCA5A5), Icons.Filled.ErrorOutline)
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 3.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(12.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = status.label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun StockBadge(
    stock: Int,
    minStock: Int,
    unit: String = "unid",
    modifier: Modifier = Modifier
) {
    val isLow = stock <= minStock
    val isZero = stock <= 0
    val (bgColor, textColor, label) = when {
        isZero -> Triple(RedContainer, RedError, "Agotado (0 $unit)")
        isLow -> Triple(Color(0xFF451A03), Color(0xFFF59E0B), "Bajo stock: $stock $unit")
        else -> Triple(GreenContainer, GreenSuccess, "$stock $unit")
    }

    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(6.dp),
        color = bgColor,
        border = androidx.compose.foundation.BorderStroke(1.dp, textColor.copy(alpha = 0.3f))
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (isLow) {
                Icon(
                    imageVector = Icons.Filled.WarningAmber,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(11.dp)
                )
                Spacer(modifier = Modifier.width(3.dp))
            }
            Text(
                text = label,
                color = textColor,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}
