package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Build
import androidx.compose.material.icons.outlined.History
import androidx.compose.material.icons.outlined.Inventory2
import androidx.compose.material.icons.outlined.PointOfSale
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.FilledTonalIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.HistoryScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.LOGO_PRESETS
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.PosScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.AmberSecondary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.TealPrimary
import com.example.ui.viewmodel.TallerViewModel

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()
    setContent {
      val viewModel: TallerViewModel = viewModel()
      val settings by viewModel.workshopSettings.collectAsStateWithLifecycle()

      DisposableEffect(settings.isDarkMode) {
        enableEdgeToEdge(
          statusBarStyle = SystemBarStyle.auto(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT,
          ) { settings.isDarkMode },
          navigationBarStyle = SystemBarStyle.auto(
            android.graphics.Color.TRANSPARENT,
            android.graphics.Color.TRANSPARENT,
          ) { settings.isDarkMode }
        )
        onDispose {}
      }

      MyApplicationTheme(darkTheme = settings.isDarkMode) {
        TallerApp(viewModel = viewModel)
      }
    }
  }
}

data class NavigationTabItem(
  val title: String,
  val selectedIcon: ImageVector,
  val unselectedIcon: ImageVector,
  val tag: String
)

@Composable
fun TallerApp(
  viewModel: TallerViewModel
) {
  val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
  val lowStockCount by viewModel.lowStockCount.collectAsStateWithLifecycle()
  val orders by viewModel.ordersList.collectAsStateWithLifecycle()
  val settings by viewModel.workshopSettings.collectAsStateWithLifecycle()
  val isDarkMode = settings.isDarkMode

  val activeOrdersCount = orders.count { it.order.status != "ENTREGADO" && it.order.status != "CANCELADO" }
  val logoPreset = LOGO_PRESETS.firstOrNull { it.id == settings.logoIconName } ?: LOGO_PRESETS.first()

  val tabs = listOf(
    NavigationTabItem("Taller", Icons.Filled.Build, Icons.Outlined.Build, "tab_taller"),
    NavigationTabItem("POS / Nueva", Icons.Filled.PointOfSale, Icons.Outlined.PointOfSale, "tab_pos"),
    NavigationTabItem("Inventario", Icons.Filled.Inventory2, Icons.Outlined.Inventory2, "tab_inventory"),
    NavigationTabItem("Historial", Icons.Filled.History, Icons.Outlined.History, "tab_history"),
    NavigationTabItem("Ajustes", Icons.Filled.Settings, Icons.Outlined.Settings, "tab_settings")
  )

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = MaterialTheme.colorScheme.background,
    topBar = {
      Surface(
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
      ) {
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          // Left: Workshop Branding & Active Tab Indicator
          Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
          ) {
            Surface(
              shape = CircleShape,
              color = MaterialTheme.colorScheme.primaryContainer,
              modifier = Modifier.size(36.dp)
            ) {
              Box(contentAlignment = Alignment.Center) {
                Icon(
                  imageVector = logoPreset.icon,
                  contentDescription = null,
                  tint = MaterialTheme.colorScheme.primary,
                  modifier = Modifier.size(20.dp)
                )
              }
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
              Text(
                text = settings.workshopName,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
              )
              Text(
                text = tabs[selectedTab].title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.width(8.dp))

          // Right: Dark / Light Mode Toggle Button!
          FilledTonalIconButton(
            onClick = { viewModel.toggleDarkMode() },
            modifier = Modifier
              .testTag("dark_mode_toggle_button")
              .size(40.dp),
            colors = IconButtonDefaults.filledTonalIconButtonColors(
              containerColor = if (isDarkMode) Color(0xFF1E293B) else Color(0xFFE2E8F0),
              contentColor = if (isDarkMode) Color(0xFFFBBF24) else Color(0xFF0F766E)
            )
          ) {
            Icon(
              imageVector = if (isDarkMode) Icons.Filled.DarkMode else Icons.Filled.LightMode,
              contentDescription = if (isDarkMode) "Deshabilitar modo oscuro (Modo Claro)" else "Habilitar modo oscuro",
              modifier = Modifier.size(20.dp)
            )
          }
        }
      }
    },
    bottomBar = {
      NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
        modifier = Modifier.background(MaterialTheme.colorScheme.surface)
      ) {
        tabs.forEachIndexed { index, tab ->
          val isSelected = selectedTab == index
          NavigationBarItem(
            selected = isSelected,
            onClick = { viewModel.selectTab(index) },
            icon = {
              if (index == 0 && activeOrdersCount > 0) {
                BadgedBox(
                  badge = {
                    Badge(
                      containerColor = TealPrimary,
                      contentColor = Color(0xFF00382E)
                    ) {
                      Text("$activeOrdersCount")
                    }
                  }
                ) {
                  Icon(
                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.title
                  )
                }
              } else if (index == 2 && lowStockCount > 0) {
                BadgedBox(
                  badge = {
                    Badge(
                      containerColor = AmberSecondary,
                      contentColor = Color.Black
                    ) {
                      Text("$lowStockCount")
                    }
                  }
                ) {
                  Icon(
                    imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                    contentDescription = tab.title
                  )
                }
              } else {
                Icon(
                  imageVector = if (isSelected) tab.selectedIcon else tab.unselectedIcon,
                  contentDescription = tab.title
                )
              }
            },
            label = {
              Text(
                text = tab.title,
                fontSize = 11.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = MaterialTheme.colorScheme.onPrimary,
              selectedTextColor = MaterialTheme.colorScheme.primary,
              indicatorColor = MaterialTheme.colorScheme.primary,
              unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
              unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
            ),
            modifier = Modifier.testTag(tab.tag)
          )
        }
      }
    }
  ) { innerPadding ->
    Box(
      modifier = Modifier
        .fillMaxSize()
        .padding(innerPadding)
    ) {
      when (selectedTab) {
        0 -> OrdersScreen(
          viewModel = viewModel,
          onNavigateToPos = { viewModel.selectTab(1) }
        )
        1 -> PosScreen(viewModel = viewModel)
        2 -> InventoryScreen(viewModel = viewModel)
        3 -> HistoryScreen(viewModel = viewModel)
        4 -> SettingsScreen(viewModel = viewModel)
      }
    }
  }
}
