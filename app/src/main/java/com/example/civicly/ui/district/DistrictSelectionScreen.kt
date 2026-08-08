package com.example.civicly.ui.district

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Apartment
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DirectionsBus
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.MyLocation
import androidx.compose.material.icons.filled.Park
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.civicly.ui.theme.DeepCobalt
import com.example.civicly.ui.theme.OnSurfaceVariant
import com.example.civicly.ui.theme.OutlineVariant
import com.example.civicly.ui.theme.SlateNavyDeep

private data class District(
    val id: String,
    val name: String,
    val subtitle: String,
    val residents: String,
    val distance: String,
    val icon: ImageVector,
)

private val DISTRICTS = listOf(
    District("oakland", "Oakland", "City of Oakland", "Alameda County", "Nearby", Icons.Filled.Apartment),
    District("san-lorenzo", "San Lorenzo", "Unincorporated community", "Alameda County", "Nearby", Icons.Filled.Park),
    District("hayward", "Hayward", "City of Hayward", "Alameda County", "Nearby", Icons.Filled.DirectionsBus),
    District("berkeley", "Berkeley", "City of Berkeley", "Alameda County", "Nearby", Icons.Filled.Apartment),
    District("fremont", "Fremont", "City of Fremont", "Alameda County", "Nearby", Icons.Filled.Park),
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DistrictSelectionSheet(
    onDismiss: () -> Unit,
    onConfirm: (districtName: String) -> Unit,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color(0xFFF7F9FB),
        dragHandle = null,
    ) {
        DistrictSelectionContent(onDismiss = onDismiss, onConfirm = onConfirm)
    }
}

@Composable
private fun DistrictSelectionContent(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit,
) {
    var query by remember { mutableStateOf("") }
    var selectedId by remember { mutableStateOf("oakland") }
    val selected = DISTRICTS.first { it.id == selectedId }

    Box(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 16.dp),
        ) {
            item { BackButton(onDismiss) }
            item {
                Spacer(Modifier.height(16.dp))
                Text(
                    "Find Your District",
                    style = MaterialTheme.typography.displayLarge,
                    color = SlateNavyDeep,
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Enter your address or zip code to connect with your local civic community.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = OnSurfaceVariant,
                )
                Spacer(Modifier.height(24.dp))
            }
            item {
                DistrictSearchBar(value = query, onValueChange = { query = it })
                Spacer(Modifier.height(32.dp))
            }
            item {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        "Nearby Districts",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SlateNavyDeep,
                    )
                    Text(
                        "SUGGESTED",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                    )
                }
                Spacer(Modifier.height(12.dp))
            }
            items(DISTRICTS, key = { it.id }) { district ->
                DistrictCard(
                    district = district,
                    selected = district.id == selectedId,
                    onSelect = { selectedId = district.id },
                )
                Spacer(Modifier.height(12.dp))
            }
            item { Spacer(Modifier.height(96.dp)) }
        }
        // Fixed bottom confirm action
        Surface(
            color = Color.White.copy(alpha = 0.9f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth(),
            shadowElevation = 8.dp,
        ) {
            Box(Modifier.padding(24.dp)) {
                Button(
                    onClick = {
                        onConfirm(selected.name)
                        onDismiss()
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = DeepCobalt,
                        contentColor = Color.White,
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp),
                ) {
                    Text(
                        "Confirm District",
                        style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(Modifier.size(8.dp))
                    Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null)
                }
            }
        }
    }
}

@Composable
private fun BackButton(onClick: () -> Unit) {
    Row(
        Modifier
            .clickable(onClick = onClick)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            Icons.AutoMirrored.Filled.ArrowBack,
            contentDescription = "Back",
            tint = OnSurfaceVariant,
        )
        Spacer(Modifier.size(8.dp))
        Text(
            "BACK",
            style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold),
            color = OnSurfaceVariant,
        )
    }
}

@Composable
private fun DistrictSearchBar(value: String, onValueChange: (String) -> Unit) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = Color.White,
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Filled.Search, contentDescription = null, tint = OnSurfaceVariant)
            Spacer(Modifier.size(12.dp))
            androidx.compose.foundation.text.BasicTextField(
                value = value,
                onValueChange = onValueChange,
                singleLine = true,
                textStyle = MaterialTheme.typography.bodyMedium.copy(color = SlateNavyDeep),
                modifier = Modifier.weight(1f).padding(vertical = 8.dp),
                decorationBox = { inner ->
                    if (value.isEmpty()) {
                        Text(
                            "Enter address or zip code...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = OnSurfaceVariant,
                        )
                    }
                    inner()
                },
            )
            IconButton(
                onClick = {},
                modifier = Modifier.size(40.dp),
            ) {
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = Color(0xFFE6E8EA),
                    modifier = Modifier.size(40.dp),
                ) {
                    Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                        Icon(
                            Icons.Filled.MyLocation,
                            contentDescription = "Use current location",
                            tint = SlateNavyDeep,
                            modifier = Modifier.size(20.dp),
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DistrictCard(district: District, selected: Boolean, onSelect: () -> Unit) {
    val cardColor = if (selected) DeepCobalt.copy(alpha = 0.05f) else Color.White
    val cardBorder = if (selected) DeepCobalt else OutlineVariant.copy(alpha = 0.3f)
    val iconBg = if (selected) DeepCobalt else Color(0xFFE6E8EA)
    val iconTint = if (selected) Color.White else OnSurfaceVariant

    Surface(
        onClick = onSelect,
        shape = RoundedCornerShape(16.dp),
        color = cardColor,
        border = BorderStroke(if (selected) 2.dp else 1.dp, cardBorder),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            Box(
                Modifier
                    .size(48.dp)
                    .background(iconBg, RoundedCornerShape(12.dp)),
                contentAlignment = Alignment.Center,
            ) {
                Icon(district.icon, contentDescription = null, tint = iconTint)
            }
            Spacer(Modifier.size(16.dp))
            Column(Modifier.weight(1f)) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text(
                        district.name,
                        style = MaterialTheme.typography.headlineMedium,
                        color = SlateNavyDeep,
                    )
                    if (selected) {
                        Icon(
                            Icons.Filled.CheckCircle,
                            contentDescription = null,
                            tint = DeepCobalt,
                        )
                    }
                }
                Spacer(Modifier.size(4.dp))
                Text(
                    district.subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = OnSurfaceVariant,
                )
                Spacer(Modifier.size(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    StatChip(Icons.Filled.Group, district.residents)
                    StatChip(Icons.Filled.Map, district.distance)
                }
            }
        }
    }
}

@Composable
private fun StatChip(icon: ImageVector, label: String) {
    Surface(
        shape = CircleShape,
        color = Color.White,
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
    ) {
        Row(
            Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(icon, contentDescription = null, tint = OnSurfaceVariant, modifier = Modifier.size(14.dp))
            Spacer(Modifier.size(4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = OnSurfaceVariant)
        }
    }
}
