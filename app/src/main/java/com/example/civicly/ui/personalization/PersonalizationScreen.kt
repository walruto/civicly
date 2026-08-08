package com.example.civicly.ui.personalization

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.civicly.R
import com.example.civicly.ui.district.DistrictSelectionSheet
import com.example.civicly.ui.theme.DeepCobalt
import com.example.civicly.ui.theme.OnSurfaceVariant
import com.example.civicly.ui.theme.OutlineVariant
import com.example.civicly.ui.theme.SlateNavyDeep

private val GENDER_OPTIONS = listOf("Female", "Male", "Non-binary", "Prefer not to say")
private val FIELD_BG = Color(0xFFF1F5F9)
private val CANVAS_BG = Color(0xFFF8FAFC)

@Composable
fun PersonalizationScreen(
    initialName: String = "",
    initialDistrict: String = "98104",
    onContinue: (name: String, district: String) -> Unit = { _, _ -> },
    onSkip: () -> Unit = {},
) {
    var name by remember { mutableStateOf(initialName) }
    var gender by remember { mutableStateOf("Male") }
    var district by remember { mutableStateOf(initialDistrict) }
    var showDistrictSheet by remember { mutableStateOf(false) }

    Box(
        Modifier
            .fillMaxSize()
            .background(CANVAS_BG),
    ) {
        Box(
            Modifier
                .align(Alignment.TopEnd)
                .size(384.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(DeepCobalt.copy(alpha = 0.10f), Color.Transparent),
                    ),
                ),
        )
        Box(
            Modifier
                .align(Alignment.BottomStart)
                .size(320.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(SlateNavyDeep.copy(alpha = 0.05f), Color.Transparent),
                    ),
                ),
        )

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 32.dp),
        ) {
            item {
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Image(
                        painter = painterResource(R.drawable.civicly_logo),
                        contentDescription = "Civicly",
                        modifier = Modifier.height(56.dp),
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Let's personalize your local experience.",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnSurfaceVariant,
                        textAlign = TextAlign.Center,
                    )
                }
                Spacer(Modifier.height(48.dp))
            }
            item {
                GlassFormCard {
                    NameSection(name) { name = it }
                    SectionDivider()
                    GenderSection(gender) { gender = it }
                    SectionDivider()
                    DistrictSection(district, onTap = { showDistrictSheet = true })
                }
                Spacer(Modifier.height(40.dp))
            }
            item {
                Button(
                    onClick = { onContinue(name, district) },
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
                        "Continue",
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    )
                    Spacer(Modifier.size(8.dp))
                    Icon(Icons.Filled.ArrowForward, contentDescription = null, modifier = Modifier.size(18.dp))
                }
                Spacer(Modifier.height(16.dp))
                Row(
                    Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                ) {
                    TextButton(onClick = onSkip) {
                        Text(
                            "Skip for now",
                            style = MaterialTheme.typography.labelLarge,
                            color = OnSurfaceVariant,
                        )
                    }
                }
            }
        }
    }

    if (showDistrictSheet) {
        DistrictSelectionSheet(
            onDismiss = { showDistrictSheet = false },
            onConfirm = { chosen -> district = chosen },
        )
    }
}

@Composable
private fun GlassFormCard(content: @Composable () -> Unit) {
    Surface(
        shape = RoundedCornerShape(24.dp),
        color = Color.White.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
        shadowElevation = 8.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Box {
            Box(
                Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Brush.horizontalGradient(colors = listOf(DeepCobalt, SlateNavyDeep))),
            )
            Column(
                Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(32.dp),
            ) {
                content()
            }
        }
    }
}

@Composable
private fun SectionDivider() {
    HorizontalDivider(color = OutlineVariant.copy(alpha = 0.2f))
}

@Composable
private fun QuestionHeader(icon: ImageVector, title: String) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, tint = DeepCobalt, modifier = Modifier.size(20.dp))
        Spacer(Modifier.size(8.dp))
        Text(title, style = MaterialTheme.typography.headlineSmall, color = SlateNavyDeep)
    }
}

@Composable
private fun NameSection(value: String, onChange: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        QuestionHeader(Icons.Filled.Person, "What is your name?")
        OutlinedTextField(
            value = value,
            onValueChange = onChange,
            placeholder = { Text("Full Name") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = fieldColors(),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
        )
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun GenderSection(selected: String, onSelect: (String) -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        QuestionHeader(Icons.Filled.Person, "What is your gender?")
        Text(
            "This helps us tailor demographic data in civic polls.",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
        )
        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            GENDER_OPTIONS.forEach { option ->
                GenderChip(option, selected == option) { onSelect(option) }
            }
        }
    }
}

@Composable
private fun GenderChip(label: String, active: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = if (active) SlateNavyDeep else Color.White,
        contentColor = if (active) Color.White else SlateNavyDeep,
        border = BorderStroke(
            1.dp,
            if (active) SlateNavyDeep else OutlineVariant.copy(alpha = 0.5f),
        ),
    ) {
        Text(
            label,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp),
        )
    }
}

@Composable
private fun DistrictSection(value: String, onTap: () -> Unit) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        QuestionHeader(Icons.Filled.LocationOn, "Find your district")
        Text(
            "Enter your address or zip code to connect with your local representatives.",
            style = MaterialTheme.typography.bodySmall,
            color = OnSurfaceVariant,
        )
        Surface(
            onClick = onTap,
            shape = RoundedCornerShape(12.dp),
            color = FIELD_BG,
            border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.3f)),
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(
                Modifier.padding(horizontal = 16.dp, vertical = 16.dp),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Icon(Icons.Filled.Search, contentDescription = null, tint = OnSurfaceVariant)
                Spacer(Modifier.size(12.dp))
                Text(
                    value.ifEmpty { "Enter address or zip code..." },
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (value.isEmpty()) OnSurfaceVariant else SlateNavyDeep,
                )
            }
        }
    }
}

@Composable
private fun fieldColors() = OutlinedTextFieldDefaults.colors(
    focusedContainerColor = FIELD_BG,
    unfocusedContainerColor = FIELD_BG,
    focusedBorderColor = DeepCobalt,
    unfocusedBorderColor = OutlineVariant.copy(alpha = 0.3f),
)
