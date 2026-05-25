package com.ainotes.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ainotes.data.local.ThemePreferences
import com.ainotes.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onNavigateToLogin: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val currentThemeMode by viewModel.themeMode.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    // Detect save success to pop back
    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.resetSuccess()
            onBack()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getAppBackgroundGradient())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 24.dp)
        ) {
            // ── Top Bar ─────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier
                        .size(44.dp)
                        .background(MaterialTheme.colorScheme.surfaceVariant, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
                
                Text(
                    text = "Profile Settings",
                    style = MaterialTheme.typography.titleLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                )

                // Placeholder to keep balance
                Spacer(modifier = Modifier.size(44.dp))
            }

            if (uiState.isLoading) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = Primary)
                }
            } else {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .padding(horizontal = 24.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Spacer(modifier = Modifier.height(8.dp))

                    // Stylized Profile Avatar Sphere
                    Box(
                        modifier = Modifier
                            .size(80.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(Primary.copy(alpha = 0.35f), Color.Transparent)
                                )
                            )
                            .background(MaterialTheme.colorScheme.surface)
                            .border(BorderStroke(1.dp, MaterialTheme.colorScheme.outline), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = if (uiState.name.isNotEmpty()) uiState.name.take(1).uppercase() else "👤",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Bold,
                            color = Primary
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    // Glassmorphic Card
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(28.dp),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(20.dp)
                        ) {
                            // Full Name Input
                            OutlinedTextField(
                                value = uiState.name,
                                onValueChange = { viewModel.onNameChanged(it) },
                                label = { Text("Full Name") },
                                leadingIcon = { Icon(Icons.Default.Person, null, tint = Primary) },
                                singleLine = true,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Primary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = Primary,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                )
                            )

                            // Email Address (read-only)
                            OutlinedTextField(
                                value = uiState.email,
                                onValueChange = {},
                                label = { Text("Email Address") },
                                leadingIcon = { Icon(Icons.Default.Email, null, tint = Secondary) },
                                singleLine = true,
                                enabled = false,
                                modifier = Modifier.fillMaxWidth(),
                                colors = TextFieldDefaults.outlinedTextFieldColors(
                                    focusedBorderColor = Secondary,
                                    unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                    focusedLabelColor = Secondary,
                                    focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
                                    disabledTextColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                                    disabledBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f)
                                )
                            )

                            // Education Level Selection
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "Are you in School or College?",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                    val levels = listOf("School", "College")
                                    levels.forEach { level ->
                                        val isSelected = uiState.educationLevel == level
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(50.dp)
                                                .clip(RoundedCornerShape(14.dp))
                                                .background(if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant)
                                                .border(
                                                    BorderStroke(
                                                        if (isSelected) 2.dp else 1.dp,
                                                        if (isSelected) Primary else MaterialTheme.colorScheme.outline
                                                    ),
                                                    RoundedCornerShape(14.dp)
                                                )
                                                .clickable { viewModel.onEducationLevelChanged(level) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = level,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = if (isSelected) OnPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Class Level Selection based on School vs College
                            AnimatedVisibility(
                                visible = uiState.educationLevel.isNotEmpty(),
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                Column(modifier = Modifier.fillMaxWidth()) {
                                    Text(
                                        text = "Select your Class / Year:",
                                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                        color = MaterialTheme.colorScheme.onSurface,
                                        modifier = Modifier.padding(bottom = 8.dp)
                                    )

                                    val options = if (uiState.educationLevel == "School") {
                                        listOf("9th", "10th", "11th", "12th")
                                    } else {
                                        listOf("FY", "SY", "TY", "Final Year")
                                    }

                                    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            options.take(2).forEach { option ->
                                                val isSelected = uiState.classLevel == option
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(44.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(if (isSelected) Secondary else MaterialTheme.colorScheme.surfaceVariant)
                                                        .border(
                                                            BorderStroke(
                                                                if (isSelected) 2.dp else 1.dp,
                                                                if (isSelected) Secondary else MaterialTheme.colorScheme.outline
                                                            ),
                                                            RoundedCornerShape(10.dp)
                                                        )
                                                        .clickable { viewModel.onClassLevelChanged(option) },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = option,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) OnSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                            options.takeLast(2).forEach { option ->
                                                val isSelected = uiState.classLevel == option
                                                Box(
                                                    modifier = Modifier
                                                        .weight(1f)
                                                        .height(44.dp)
                                                        .clip(RoundedCornerShape(10.dp))
                                                        .background(if (isSelected) Secondary else MaterialTheme.colorScheme.surfaceVariant)
                                                        .border(
                                                            BorderStroke(
                                                                if (isSelected) 2.dp else 1.dp,
                                                                if (isSelected) Secondary else MaterialTheme.colorScheme.outline
                                                            ),
                                                            RoundedCornerShape(10.dp)
                                                        )
                                                        .clickable { viewModel.onClassLevelChanged(option) },
                                                    contentAlignment = Alignment.Center
                                                ) {
                                                    Text(
                                                        text = option,
                                                        style = MaterialTheme.typography.bodyMedium.copy(
                                                            fontWeight = FontWeight.Bold,
                                                            color = if (isSelected) OnSecondary else MaterialTheme.colorScheme.onSurfaceVariant
                                                        )
                                                    )
                                                }
                                            }
                                        }
                                    }
                                }
                            }

                            // Degree Field for College Users
                            AnimatedVisibility(
                                visible = uiState.educationLevel == "College",
                                enter = fadeIn() + expandVertically(),
                                exit = fadeOut() + shrinkVertically()
                            ) {
                                OutlinedTextField(
                                    value = uiState.degree,
                                    onValueChange = { viewModel.onDegreeChanged(it) },
                                    label = { Text("Degree / Program") },
                                    placeholder = { Text("e.g. B.Tech, B.Sc, BCA") },
                                    leadingIcon = { Icon(Icons.Default.School, null, tint = Tertiary) },
                                    singleLine = true,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = TextFieldDefaults.outlinedTextFieldColors(
                                        focusedBorderColor = Tertiary,
                                        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                        focusedLabelColor = Tertiary,
                                        focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                    )
                                )
                            }

                            // ── API Key Settings ─────────────────────────────────────────
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "🔑 Gemini AI API Key",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 4.dp)
                                )
                                Text(
                                    text = if (uiState.hasUserApiKey)
                                        "✅ You have a personal API key saved — no rate limits!"
                                    else
                                        "The app uses a shared API key which may hit rate limits when multiple people use it. Add your own FREE key from aistudio.google.com/apikey to fix this permanently.",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = if (uiState.hasUserApiKey) 
                                        Color(0xFF27AE60) 
                                    else 
                                        MaterialTheme.colorScheme.onSurface.copy(alpha = 0.65f),
                                    modifier = Modifier.padding(bottom = 12.dp)
                                )

                                if (!uiState.hasUserApiKey) {
                                    var keyVisible by remember { mutableStateOf(false) }
                                    OutlinedTextField(
                                        value = uiState.apiKeyInput,
                                        onValueChange = { viewModel.onApiKeyInputChanged(it) },
                                        label = { Text("Your Gemini API Key") },
                                        placeholder = { Text("AIzaSy...") },
                                        leadingIcon = { Icon(Icons.Default.VpnKey, null, tint = Primary) },
                                        trailingIcon = {
                                            IconButton(onClick = { keyVisible = !keyVisible }) {
                                                Icon(
                                                    imageVector = if (keyVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                                    contentDescription = if (keyVisible) "Hide" else "Show",
                                                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                                                )
                                            }
                                        },
                                        visualTransformation = if (keyVisible) VisualTransformation.None else PasswordVisualTransformation(),
                                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                                        singleLine = true,
                                        modifier = Modifier.fillMaxWidth(),
                                        colors = TextFieldDefaults.outlinedTextFieldColors(
                                            focusedBorderColor = Primary,
                                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                                            focusedLabelColor = Primary,
                                            focusedTextColor = MaterialTheme.colorScheme.onSurface,
                                            unfocusedTextColor = MaterialTheme.colorScheme.onSurface
                                        )
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Button(
                                        onClick = { viewModel.saveApiKey() },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(44.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                                    ) {
                                        Icon(Icons.Default.Save, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Save API Key",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }
                                } else {
                                    Button(
                                        onClick = { viewModel.clearApiKey() },
                                        shape = RoundedCornerShape(10.dp),
                                        modifier = Modifier.fillMaxWidth().height(44.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = MaterialTheme.colorScheme.onSurface
                                        ),
                                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                                    ) {
                                        Icon(Icons.Default.Delete, contentDescription = null, modifier = Modifier.size(16.dp))
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            "Remove My API Key",
                                            style = MaterialTheme.typography.bodyMedium.copy(
                                                fontWeight = FontWeight.Bold
                                            )
                                        )
                                    }
                                }

                                uiState.apiKeyMessage?.let { msg ->
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = msg,
                                        style = MaterialTheme.typography.bodySmall,
                                        color = if (msg.startsWith("✅")) Color(0xFF27AE60) else ErrorColor,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }

                            // App Theme Settings Selection
                            Column(modifier = Modifier.fillMaxWidth()) {
                                Text(
                                    text = "App Theme Settings",
                                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                    color = MaterialTheme.colorScheme.onSurface,
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    val themes = listOf(
                                        Triple(ThemePreferences.THEME_LIGHT, "Light", Icons.Default.LightMode),
                                        Triple(ThemePreferences.THEME_DARK, "Dark", Icons.Default.DarkMode),
                                        Triple(ThemePreferences.THEME_SYSTEM, "System", Icons.Default.Settings)
                                    )
                                    themes.forEach { (mode, label, icon) ->
                                        val isSelected = currentThemeMode == mode
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(46.dp)
                                                .clip(RoundedCornerShape(12.dp))
                                                .background(if (isSelected) Primary else MaterialTheme.colorScheme.surfaceVariant)
                                                .border(
                                                    BorderStroke(
                                                        if (isSelected) 2.dp else 1.dp,
                                                        if (isSelected) Primary else MaterialTheme.colorScheme.outline
                                                    ),
                                                    RoundedCornerShape(12.dp)
                                                )
                                                .clickable { viewModel.setThemeMode(mode) },
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Icon(
                                                    imageVector = icon,
                                                    contentDescription = label,
                                                    tint = if (isSelected) OnPrimary else MaterialTheme.colorScheme.onSurfaceVariant,
                                                    modifier = Modifier.size(16.dp)
                                                )
                                                Spacer(modifier = Modifier.width(6.dp))
                                                Text(
                                                    text = label,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = if (isSelected) OnPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                                                    )
                                                )
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(4.dp))

                            // Action buttons: Save Changes & Sign Out
                            if (uiState.isSaving) {
                                Box(
                                    modifier = Modifier.fillMaxWidth(),
                                    contentAlignment = Alignment.Center
                                ) {
                                    CircularProgressIndicator(color = Primary)
                                }
                            } else {
                                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                                    // Save Button
                                    Button(
                                        onClick = { viewModel.saveProfile() },
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        colors = ButtonDefaults.buttonColors(containerColor = Primary)
                                    ) {
                                        Text(
                                            text = "Save Changes",
                                            style = MaterialTheme.typography.bodyLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = Color.White
                                            )
                                        )
                                    }

                                    // Sign Out Button
                                    Button(
                                        onClick = {
                                            viewModel.signOut()
                                            onNavigateToLogin()
                                        },
                                        shape = RoundedCornerShape(14.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(50.dp),
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.Transparent,
                                            contentColor = ErrorColor
                                        ),
                                        border = BorderStroke(1.dp, ErrorColor.copy(alpha = 0.5f))
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.ExitToApp,
                                                contentDescription = "Sign Out"
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(
                                                text = "Sign Out",
                                                style = MaterialTheme.typography.bodyLarge.copy(
                                                    fontWeight = FontWeight.Bold
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Error Reporting
                            uiState.error?.let { err ->
                                Text(
                                    text = err,
                                    color = ErrorColor,
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    textAlign = TextAlign.Center,
                                    modifier = Modifier.fillMaxWidth()
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                }
            }
        }
    }
}
