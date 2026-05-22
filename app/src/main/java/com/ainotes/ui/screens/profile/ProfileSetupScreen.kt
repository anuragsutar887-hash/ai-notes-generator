package com.ainotes.ui.screens.profile

import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ainotes.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileSetupScreen(
    onNavigateToHome: () -> Unit,
    viewModel: ProfileViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val scrollState = rememberScrollState()

    LaunchedEffect(uiState.saveSuccess) {
        if (uiState.saveSuccess) {
            viewModel.resetSuccess()
            onNavigateToHome()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getAppBackgroundGradient()),
        contentAlignment = Alignment.Center
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(color = Primary)
        } else {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(24.dp))

                // Page Title
                Text(
                    text = "Complete Your Profile",
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        brush = Brush.horizontalGradient(
                            colors = listOf(Primary, Secondary)
                        )
                    )
                )

                Text(
                    text = "Tell us a bit about your studies so we can customize your study notes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 12.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

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
                            placeholder = { Text("Enter your full name") },
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

                        // Email Address (read-only for Google Auth, editable for Email Auth but disabled to match user account)
                        OutlinedTextField(
                            value = uiState.email,
                            onValueChange = { viewModel.onEmailChanged(it) },
                            label = { Text("Email Address") },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = Secondary) },
                            singleLine = true,
                            enabled = uiState.email.isEmpty(), // Lock it if pre-filled
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

                                // FlowRow or simple Row Grid
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

                        Spacer(modifier = Modifier.height(8.dp))

                        // Submit Button
                        if (uiState.isSaving) {
                            Box(
                                modifier = Modifier.fillMaxWidth(),
                                contentAlignment = Alignment.Center
                            ) {
                                CircularProgressIndicator(color = Primary)
                            }
                        } else {
                            Button(
                                onClick = { viewModel.saveProfile() },
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = Primary)
                            ) {
                                Text(
                                    text = "Complete Setup",
                                    style = MaterialTheme.typography.bodyLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
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
