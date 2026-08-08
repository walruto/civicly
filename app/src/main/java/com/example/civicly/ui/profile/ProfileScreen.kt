package com.example.civicly.ui.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Article
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Newspaper
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.civicly.R
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.example.civicly.ui.theme.OnSurfaceVariant
import com.example.civicly.ui.theme.OutlineVariant
import com.example.civicly.ui.theme.SlateNavy

// ponytail: static mock, no VM. Every interactive element carries a wire-me TODO.

private val GENDER_OPTIONS = listOf("Male", "Female", "Non-binary", "Prefer not to say")
private val COUNTY_OPTIONS = listOf(
    "Alameda County",
    "San Francisco County",
    "Contra Costa County",
    "Marin County",
    "San Mateo County",
)

private data class SavedArticle(val title: String, val agoLabel: String)

private val SAVED_ARTICLES = listOf(
    SavedArticle("Alameda Transit Updates: New Express Routes Announced", "2 days ago"),
    SavedArticle("SF Housing Initiatives: New Affordable Units in Mission District", "1 week ago"),
)

private const val USER_NAME = "Marcus Thompson"
private const val USER_LOCATION = "Alameda County Resident"

@Composable
fun ProfileScreen(
    onOpenFeed: () -> Unit = {},
    onOpenSearch: () -> Unit = {},
    onOpenArticle: () -> Unit = {},
) {
    var showEdit by remember { mutableStateOf(false) }
    Box(
        Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 120.dp),
        ) {
            item { ProfileTopBar() }
            item { ProfileHeader(onEdit = { showEdit = true }) }
            item {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    SavedArticlesCard()
                }
            }
        }
        ProfileBottomNav(
            modifier = Modifier.align(Alignment.BottomCenter),
            onOpenFeed = onOpenFeed,
            onOpenSearch = onOpenSearch,
            onOpenArticle = onOpenArticle,
        )
    }
    if (showEdit) EditProfileDialog(onDismiss = { showEdit = false })
}

@Composable
private fun ProfileTopBar() {
    Row(
        Modifier
            .fillMaxWidth()
            .background(Color(0x99F7F9FB))
            .statusBarsPadding()
            .padding(horizontal = 16.dp)
            .height(64.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween,
    ) {
        Surface(
            onClick = { /* TODO: Wire to backend — open location picker */ },
            shape = RoundedCornerShape(8.dp),
            color = Color.Transparent,
            modifier = Modifier.size(40.dp),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(Icons.Filled.LocationOn, contentDescription = "Location", tint = SlateNavy)
            }
        }
        Image(
            painter = painterResource(R.drawable.civicly_logo),
            contentDescription = "Civicly",
            modifier = Modifier.height(36.dp),
        )
        Box(
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(Icons.Filled.Person, contentDescription = "Avatar", tint = OnSurfaceVariant)
        }
    }
}

@Composable
private fun ProfileHeader(onEdit: () -> Unit) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(top = 32.dp, bottom = 48.dp, start = 24.dp, end = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Box(
            Modifier
                .size(128.dp)
                .clip(CircleShape)
                .border(4.dp, MaterialTheme.colorScheme.surface, CircleShape)
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Person,
                contentDescription = null,
                tint = OnSurfaceVariant.copy(alpha = 0.6f),
                modifier = Modifier.size(64.dp),
            )
        }
        Spacer(Modifier.height(24.dp))
        Text(
            USER_NAME,
            style = MaterialTheme.typography.headlineMedium,
            color = SlateNavy,
        )
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                Icons.Filled.Place,
                contentDescription = null,
                tint = OnSurfaceVariant,
                modifier = Modifier.size(16.dp),
            )
            Spacer(Modifier.width(4.dp))
            Text(
                USER_LOCATION,
                style = MaterialTheme.typography.bodyMedium,
                color = OnSurfaceVariant,
            )
        }
        Spacer(Modifier.height(24.dp))
        Button(
            onClick = onEdit,
            shape = CircleShape,
            colors = ButtonDefaults.buttonColors(
                containerColor = SlateNavy,
                contentColor = Color.White,
            ),
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
        ) {
            Text("Edit Profile", style = MaterialTheme.typography.labelLarge)
        }
    }
}

@Composable
private fun SavedArticlesCard() {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = Color.White.copy(alpha = 0.8f),
        border = BorderStroke(1.dp, OutlineVariant.copy(alpha = 0.2f)),
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Column(Modifier.padding(24.dp)) {
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Filled.Bookmarks, contentDescription = null, tint = SlateNavy)
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Saved Articles",
                        style = MaterialTheme.typography.headlineSmall,
                        color = SlateNavy,
                    )
                }
                TextButton(onClick = { /* TODO: Wire to backend — open Saved Articles list */ }) {
                    Text(
                        "View All",
                        style = MaterialTheme.typography.labelMedium,
                        color = SlateNavy,
                    )
                }
            }
            Spacer(Modifier.height(16.dp))
            SAVED_ARTICLES.forEachIndexed { i, article ->
                if (i > 0) Spacer(Modifier.height(16.dp))
                SavedArticleRow(article)
            }
        }
    }
}

@Composable
private fun SavedArticleRow(article: SavedArticle) {
    Row(
        Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .padding(4.dp),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            Modifier
                .size(64.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Filled.Newspaper,
                contentDescription = null,
                tint = OnSurfaceVariant.copy(alpha = 0.5f),
            )
        }
        Spacer(Modifier.width(16.dp))
        Column(Modifier.padding(top = 2.dp)) {
            Text(
                article.title,
                style = MaterialTheme.typography.bodyMedium,
                color = SlateNavy,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
            )
            Spacer(Modifier.height(4.dp))
            Text(
                article.agoLabel,
                style = MaterialTheme.typography.labelSmall,
                color = OnSurfaceVariant,
            )
        }
    }
}

@Composable
private fun ProfileBottomNav(
    modifier: Modifier = Modifier,
    onOpenFeed: () -> Unit,
    onOpenSearch: () -> Unit,
    onOpenArticle: () -> Unit,
) {
    Surface(
        modifier = modifier
            .padding(horizontal = 24.dp, vertical = 16.dp)
            .fillMaxWidth()
            .height(64.dp),
        shape = CircleShape,
        color = Color.White.copy(alpha = 0.85f),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.4f)),
        shadowElevation = 12.dp,
    ) {
        Row(
            Modifier.fillMaxSize().padding(horizontal = 24.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            NavIcon(Icons.Filled.Home, selected = false, onClick = onOpenFeed)
            NavIcon(Icons.Filled.Search, selected = false, onClick = onOpenSearch)
            NavIcon(Icons.AutoMirrored.Filled.Article, selected = false, onClick = onOpenArticle)
            NavIcon(Icons.Filled.Person, selected = true, onClick = {})
        }
    }
}

@Composable
private fun NavIcon(icon: ImageVector, selected: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        color = if (selected) SlateNavy else Color.Transparent,
        contentColor = if (selected) Color.White else SlateNavy.copy(alpha = 0.5f),
        shape = CircleShape,
        shadowElevation = if (selected) 4.dp else 0.dp,
        modifier = Modifier.size(if (selected) 48.dp else 44.dp),
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
            Icon(icon, contentDescription = null)
        }
    }
}

@Composable
private fun EditProfileDialog(onDismiss: () -> Unit) {
    var name by remember { mutableStateOf(USER_NAME) }
    var gender by remember { mutableStateOf(GENDER_OPTIONS.first()) }
    var county by remember { mutableStateOf(COUNTY_OPTIONS.first()) }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false),
    ) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = Color.White.copy(alpha = 0.95f),
            border = BorderStroke(1.dp, Color.White.copy(alpha = 0.5f)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
        ) {
            Column(Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(20.dp)) {
                Row(
                    Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                ) {
                    Text("Edit Profile", style = MaterialTheme.typography.headlineSmall, color = SlateNavy)
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Filled.Close, contentDescription = "Close", tint = OnSurfaceVariant)
                    }
                }
                Column(
                    Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                ) {
                    Box(
                        Modifier
                            .size(96.dp)
                            .clip(CircleShape)
                            .border(2.dp, Color.White, CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                        contentAlignment = Alignment.Center,
                    ) {
                        Icon(
                            Icons.Filled.Person,
                            contentDescription = null,
                            tint = OnSurfaceVariant.copy(alpha = 0.6f),
                            modifier = Modifier.size(48.dp),
                        )
                        Box(
                            Modifier
                                .fillMaxSize()
                                .background(Color.Black.copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center,
                        ) {
                            Icon(Icons.Filled.Edit, contentDescription = "Change picture", tint = Color.White)
                        }
                    }
                    Text(
                        "Edit Profile Picture",
                        style = MaterialTheme.typography.labelSmall,
                        color = OnSurfaceVariant,
                    )
                }
                FieldLabel("Edit Name")
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = SlateNavy,
                        unfocusedBorderColor = OutlineVariant.copy(alpha = 0.4f),
                    ),
                )
                FieldLabel("Edit Gender")
                DropdownField(value = gender, options = GENDER_OPTIONS, onSelect = { gender = it })
                FieldLabel("Edit County")
                DropdownField(value = county, options = COUNTY_OPTIONS, onSelect = { county = it })
                Row(
                    Modifier.fillMaxWidth().padding(top = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                    OutlinedButton(
                        onClick = onDismiss,
                        shape = CircleShape,
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) { Text("Cancel", style = MaterialTheme.typography.labelLarge) }
                    Button(
                        onClick = onDismiss,
                        shape = CircleShape,
                        colors = ButtonDefaults.buttonColors(containerColor = SlateNavy, contentColor = Color.White),
                        modifier = Modifier.weight(1f).height(48.dp),
                    ) { Text("Save Changes", style = MaterialTheme.typography.labelLarge) }
                }
            }
        }
    }
}

@Composable
private fun FieldLabel(text: String) {
    Text(
        text,
        style = MaterialTheme.typography.labelMedium,
        color = OnSurfaceVariant,
        modifier = Modifier.padding(start = 4.dp),
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DropdownField(value: String, options: List<String>, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = it }) {
        OutlinedTextField(
            value = value,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { Icon(Icons.Filled.ArrowDropDown, contentDescription = null) },
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            shape = RoundedCornerShape(12.dp),
            colors = ExposedDropdownMenuDefaults.outlinedTextFieldColors(
                focusedBorderColor = SlateNavy,
                unfocusedBorderColor = OutlineVariant.copy(alpha = 0.4f),
            ),
        )
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}
