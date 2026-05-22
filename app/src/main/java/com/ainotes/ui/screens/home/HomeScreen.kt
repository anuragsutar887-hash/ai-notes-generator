package com.ainotes.ui.screens.home

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ainotes.data.model.GenerationMode
import com.ainotes.ui.theme.*
import java.util.*

// ── Screen Entry Point ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToResults: (String) -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToProfile: () -> Unit,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    // Navigate to results when session is ready
    LaunchedEffect(uiState.lastSessionId) {
        uiState.lastSessionId?.let { sessionId ->
            onNavigateToResults(sessionId)
            viewModel.clearLastSessionId()
        }
    }

    val fileLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenMultipleDocuments()
    ) { uris ->
        if (uris.isNotEmpty()) viewModel.onFilesSelected(uris)
    }

    // Controls which "page" we show: 0=dashboard, 1=create note
    var showCreateNote by remember { mutableStateOf(false) }
    var selectedBottomTab by remember { mutableStateOf(0) } // 0=home, 1=history, 2=profile

    // If currently processing, show the loading screen
    if (uiState.isProcessing) {
        LoadingScreen(uiState = uiState)
        return
    }

    Scaffold(
        containerColor = Color.Transparent,
        bottomBar = {
            if (!showCreateNote) {
                PremiumBottomNav(
                    selectedTab = selectedBottomTab,
                    onTabSelected = { tab ->
                        selectedBottomTab = tab
                        when (tab) {
                            1 -> onNavigateToHistory()
                            2 -> onNavigateToProfile()
                        }
                    },
                    onFabClick = { showCreateNote = true }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(getAppBackgroundGradient())
        ) {
            AnimatedContent(
                targetState = showCreateNote,
                transitionSpec = {
                    slideInVertically { it } + fadeIn() togetherWith
                            slideOutVertically { -it } + fadeOut()
                },
                label = "screen_transition"
            ) { isCreateNote ->
                if (isCreateNote) {
                    CreateNoteScreen(
                        uiState = uiState,
                        onBack = { showCreateNote = false },
                        onFilesSelected = { fileLauncher.launch(arrayOf("application/pdf","image/*","text/plain","*/*")) },
                        onPastedTextChanged = viewModel::onPastedTextChanged,
                        onModeSelected = viewModel::onModeSelected,
                        onCustomQueryChanged = viewModel::onCustomQueryChanged,
                        onGenerate = {
                            showCreateNote = false
                            viewModel.startProcessing()
                        },
                        onSuggestionChip = { topic ->
                            viewModel.onPastedTextChanged(topic)
                        }
                    )
                } else {
                    DashboardScreen(
                        uiState = uiState,
                        onCreateNote = { showCreateNote = true },
                        onHistory = onNavigateToHistory,
                        onProfile = onNavigateToProfile,
                        onSessionClick = onNavigateToResults,
                        innerPadding = innerPadding
                    )
                }
            }

            // Error snackbar
            AnimatedVisibility(
                visible = uiState.error != null,
                enter = slideInVertically { -it } + fadeIn(),
                exit = slideOutVertically { -it } + fadeOut(),
                modifier = Modifier.align(Alignment.TopCenter)
            ) {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                        .statusBarsPadding(),
                    colors = CardDefaults.cardColors(containerColor = ErrorColor),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("⚠️", fontSize = 18.sp)
                        Spacer(Modifier.width(10.dp))
                        Text(
                            uiState.error ?: "",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White,
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(onClick = viewModel::clearError) {
                            Icon(Icons.Filled.Close, null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }
    }
}

// ── Dashboard Screen ──────────────────────────────────────────────────────────

@Composable
fun DashboardScreen(
    uiState: HomeUiState,
    onCreateNote: () -> Unit,
    onHistory: () -> Unit,
    onProfile: () -> Unit,
    onSessionClick: (String) -> Unit,
    innerPadding: PaddingValues
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = innerPadding.calculateBottomPadding()),
        contentPadding = PaddingValues(bottom = 20.dp)
    ) {
        // ── Header ────────────────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    val greeting = remember {
                        val hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)
                        when {
                            hour < 12 -> "Good morning"
                            hour < 17 -> "Good afternoon"
                            else      -> "Good evening"
                        }
                    }
                    Text(
                        greeting,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    val displayName = if (uiState.userName.isNotBlank()) uiState.userName else "there"
                    Text(
                        "Hi $displayName! 👋",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold
                        ),
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                // Profile avatar
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(Brush.linearGradient(listOf(Primary, PrimaryVariant)))
                        .clickable(onClick = onProfile),
                    contentAlignment = Alignment.Center
                ) {
                    val initials = if (uiState.userName.isNotBlank())
                        uiState.userName.take(1).uppercase() else "?"
                    Text(initials, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                }
            }
        }



        // ── Recent Notes Header ────────────────────────────────────────────
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp, vertical = 20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Recent Notes",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground
                )
                TextButton(onClick = onHistory) {
                    Text(
                        "See all",
                        color = Primary,
                        style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            }
        }

        // ── Recent Sessions list (or empty state) ──────────────────────────
        if (uiState.recentSessions.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(vertical = 32.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(80.dp)
                                .clip(CircleShape)
                                .background(PrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("📚", fontSize = 36.sp)
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(
                            "No notes yet",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Spacer(Modifier.height(6.dp))
                        Text(
                            "Tap + to create your first AI note!",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center
                        )
                        Spacer(Modifier.height(20.dp))
                        Button(
                            onClick = onCreateNote,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                            contentPadding = PaddingValues(0.dp),
                            modifier = Modifier.height(44.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .background(
                                        Brush.horizontalGradient(listOf(Primary, PrimaryVariant)),
                                        RoundedCornerShape(14.dp)
                                    )
                                    .padding(horizontal = 24.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    "✨ Create Note",
                                    style = MaterialTheme.typography.labelLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                )
                            }
                        }
                    }
                }
            }
        } else {
            items(uiState.recentSessions) { session ->
                RecentSessionCard(
                    session = session,
                    onClick = { onSessionClick(session.id) },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 4.dp)
                )
            }
        }
    }
}

// ── Recent Session Card for Dashboard ─────────────────────────────────────────

@Composable
fun RecentSessionCard(
    session: com.ainotes.data.model.NoteSession,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val mode = try { GenerationMode.valueOf(session.mode) } catch (e: Exception) { GenerationMode.FLASHCARDS }
    val modeColor = when (mode) {
        GenerationMode.FLASHCARDS    -> FlashcardColor
        GenerationMode.KEY_POINTS    -> KeyPointColor
        GenerationMode.FORMULAE      -> FormulaColor
        GenerationMode.EXAM_QUESTIONS -> QuestionColor
        GenerationMode.CUSTOM        -> CustomColor
    }
    val modeEmoji = when (mode) {
        GenerationMode.FLASHCARDS    -> "📇"
        GenerationMode.KEY_POINTS    -> "📌"
        GenerationMode.FORMULAE      -> "🔢"
        GenerationMode.EXAM_QUESTIONS -> "❓"
        GenerationMode.CUSTOM        -> "💬"
    }
    val dateStr = remember(session.createdAt) {
        java.text.SimpleDateFormat("dd MMM · hh:mm a", java.util.Locale.getDefault())
            .format(java.util.Date(session.createdAt))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Mode icon
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .clip(RoundedCornerShape(12.dp))
                    .background(modeColor.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Text(modeEmoji, fontSize = 20.sp)
            }
            Spacer(Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = session.title,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(4.dp))
                Row(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(6.dp))
                            .background(modeColor.copy(alpha = 0.10f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            mode.displayName,
                            style = MaterialTheme.typography.labelSmall,
                            color = modeColor,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                    Text(
                        dateStr,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Icon(
                Icons.Filled.ChevronRight,
                null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

// ── Create Note Screen ────────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateNoteScreen(
    uiState: HomeUiState,
    onBack: () -> Unit,
    onFilesSelected: () -> Unit,
    onPastedTextChanged: (String) -> Unit,
    onModeSelected: (GenerationMode) -> Unit,
    onCustomQueryChanged: (String) -> Unit,
    onGenerate: () -> Unit,
    onSuggestionChip: (String) -> Unit
) {
    val suggestions = listOf(
        "Photosynthesis", "Machine Learning", "World War II",
        "Thermodynamics", "Cell Division", "Newton's Laws",
        "Organic Chemistry", "Indian History"
    )

    // Map GenerationMode to display info for note type cards
    val noteTypeCards = listOf(
        Triple(GenerationMode.KEY_POINTS, "Study Notes", "📘"),
        Triple(GenerationMode.FLASHCARDS, "Flashcards", "📇"),
        Triple(GenerationMode.EXAM_QUESTIONS, "Exam Prep", "❓"),
        Triple(GenerationMode.FORMULAE, "Formulae", "🔢"),
        Triple(GenerationMode.CUSTOM, "Custom", "💬")
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getAppBackgroundGradient())
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 100.dp)
        ) {
            // Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surface),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Filled.ArrowBack, "Back",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
                Text(
                    "Create Note",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }

            Spacer(Modifier.height(8.dp))

            // ── Main prompt ──────────────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Upload your material",
                    style = MaterialTheme.typography.headlineSmall.copy(
                        fontWeight = FontWeight.ExtraBold,
                        lineHeight = 34.sp
                    ),
                    color = MaterialTheme.colorScheme.onBackground
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    "Upload a PDF, image, or other document to generate notes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Spacer(Modifier.height(20.dp))

            // ── Spacious & Illustrative Upload Panel ────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(onClick = onFilesSelected)
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        val hasFile = uiState.selectedFileNames.isNotEmpty()
                        
                        // Pulsing / glowing illustrative circle icon
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(if (hasFile) Secondary.copy(alpha = 0.12f) else Primary.copy(alpha = 0.12f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = if (hasFile) Icons.Filled.CheckCircle else Icons.Outlined.CloudUpload,
                                contentDescription = null,
                                tint = if (hasFile) Secondary else Primary,
                                modifier = Modifier.size(36.dp)
                            )
                        }
                        
                        Spacer(Modifier.height(16.dp))
                        
                        Text(
                            text = if (hasFile) "Material Uploaded Successfully!" else "Upload your study material",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                        
                        Spacer(Modifier.height(6.dp))
                        
                        Text(
                            text = if (hasFile) 
                                uiState.selectedFileNames.firstOrNull() ?: "File ready"
                            else 
                                "Tap here to browse PDF, images, or document files",
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (hasFile) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            maxLines = 2,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(horizontal = 8.dp)
                        )
                        
                        Spacer(Modifier.height(16.dp))
                        
                        if (hasFile) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Primary.copy(alpha = 0.08f))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Change File",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Primary
                                )
                            }
                        } else {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(10.dp))
                                    .background(Primary.copy(alpha = 0.08f))
                                    .padding(horizontal = 14.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Browse Files",
                                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                                    color = Primary
                                )
                            }
                        }
                    }
                }
            }

            Spacer(Modifier.height(24.dp))

            // ── Note Type Selector ───────────────────────────────────────
            Column(modifier = Modifier.padding(horizontal = 20.dp)) {
                Text(
                    "Note Type",
                    style = MaterialTheme.typography.labelLarge.copy(fontWeight = FontWeight.SemiBold),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(Modifier.height(10.dp))

                // First row: 3 cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    noteTypeCards.take(3).forEach { (mode, label, emoji) ->
                        NoteTypeCard(
                            emoji = emoji,
                            label = label,
                            isSelected = uiState.selectedMode == mode,
                            onClick = { onModeSelected(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
                Spacer(Modifier.height(10.dp))
                // Second row: 2 cards
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    noteTypeCards.drop(3).forEach { (mode, label, emoji) ->
                        NoteTypeCard(
                            emoji = emoji,
                            label = label,
                            isSelected = uiState.selectedMode == mode,
                            onClick = { onModeSelected(mode) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                    // filler to balance the second row if < 3 items
                    if (noteTypeCards.size - 3 < 3) {
                        repeat(3 - (noteTypeCards.size - 3)) {
                            Spacer(Modifier.weight(1f))
                        }
                    }
                }

                // Custom query input
                AnimatedVisibility(visible = uiState.selectedMode == GenerationMode.CUSTOM) {
                    Column {
                        Spacer(Modifier.height(14.dp))
                        OutlinedTextField(
                            value = uiState.customQuery,
                            onValueChange = onCustomQueryChanged,
                            modifier = Modifier.fillMaxWidth(),
                            label = { Text("Your custom query") },
                            placeholder = {
                                Text(
                                    "e.g. Give me 10 MCQs on Thermodynamics",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = Primary,
                                focusedLabelColor = Primary,
                                cursorColor = Primary,
                                unfocusedBorderColor = MaterialTheme.colorScheme.outline
                            ),
                            shape = RoundedCornerShape(14.dp),
                            minLines = 2
                        )
                    }
                }
            }
        }

        // ── Generate Button ──────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, MaterialTheme.colorScheme.background)
                    )
                )
                .padding(horizontal = 20.dp, vertical = 16.dp)
                .navigationBarsPadding()
        ) {
            val canGenerate = uiState.selectedFileNames.isNotEmpty()

            Button(
                onClick = onGenerate,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                shape = RoundedCornerShape(18.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                contentPadding = PaddingValues(0.dp),
                enabled = canGenerate
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            if (canGenerate)
                                Brush.horizontalGradient(listOf(Primary, PrimaryVariant))
                            else
                                Brush.horizontalGradient(listOf(Color.Gray, Color.Gray)),
                            RoundedCornerShape(18.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text("✨", fontSize = 18.sp)
                        Spacer(Modifier.width(8.dp))
                        Text(
                            "Generate ${uiState.selectedMode.displayName}",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }
                }
            }
        }
    }
}

// ── Loading Screen ────────────────────────────────────────────────────────────

@Composable
fun LoadingScreen(uiState: HomeUiState) {
    val inf = rememberInfiniteTransition(label = "loading_anim")

    // Master rotation for dual rings
    val ring1Angle by inf.animateFloat(
        initialValue = 0f, targetValue = 360f,
        animationSpec = infiniteRepeatable(tween(4000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring1"
    )
    val ring2Angle by inf.animateFloat(
        initialValue = 360f, targetValue = 0f,
        animationSpec = infiniteRepeatable(tween(6000, easing = LinearEasing), RepeatMode.Restart),
        label = "ring2"
    )

    // Orb pulse
    val orbScale by inf.animateFloat(
        initialValue = 0.92f, targetValue = 1.08f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "orb_scale"
    )
    val orbAlpha by inf.animateFloat(
        initialValue = 0.55f, targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            tween(1400, easing = EaseInOutSine), RepeatMode.Reverse
        ), label = "orb_alpha"
    )

    // Floating particles Y
    val p1y by inf.animateFloat(
        initialValue = 0f, targetValue = -18f,
        animationSpec = infiniteRepeatable(tween(1700, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "p1"
    )
    val p2y by inf.animateFloat(
        initialValue = 0f, targetValue = 12f,
        animationSpec = infiniteRepeatable(tween(2100, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "p2"
    )
    val p3y by inf.animateFloat(
        initialValue = 0f, targetValue = -14f,
        animationSpec = infiniteRepeatable(tween(1900, easing = EaseInOutSine), RepeatMode.Reverse),
        label = "p3"
    )

    // Dot loader animation
    val dot1Scale by inf.animateFloat(
        initialValue = 0.6f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            tween(600, easing = EaseInOutSine, delayMillis = 0), RepeatMode.Reverse
        ), label = "d1"
    )
    val dot2Scale by inf.animateFloat(
        initialValue = 0.6f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            tween(600, easing = EaseInOutSine, delayMillis = 200), RepeatMode.Reverse
        ), label = "d2"
    )
    val dot3Scale by inf.animateFloat(
        initialValue = 0.6f, targetValue = 1.4f,
        animationSpec = infiniteRepeatable(
            tween(600, easing = EaseInOutSine, delayMillis = 400), RepeatMode.Reverse
        ), label = "d3"
    )

    // Text cycle
    val tipTexts = listOf(
        "Reading your material…",
        "Extracting key concepts…",
        "Structuring notes…",
        "Applying AI magic…",
        "Almost ready…"
    )
    val tipIndex by inf.animateFloat(
        initialValue = 0f, targetValue = tipTexts.size.toFloat(),
        animationSpec = infiniteRepeatable(
            tween(tipTexts.size * 2200, easing = LinearEasing), RepeatMode.Restart
        ), label = "tip"
    )
    val currentTip = tipTexts[(tipIndex.toInt() % tipTexts.size)]
    val progressPercent = (uiState.processingProgress * 100).toInt().coerceIn(0, 100)

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    listOf(
                        Color(0xFF0A0A1E),
                        Color(0xFF12122E),
                        Color(0xFF0A0A1E)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // ── Full-screen background canvas ────────────────────────────────
        androidx.compose.foundation.Canvas(modifier = Modifier.fillMaxSize()) {
            // Large ambient glow blobs
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x336C5CE7), Color(0x006C5CE7)),
                    center = Offset(size.width * 0.85f, size.height * 0.12f),
                    radius = size.width * 0.60f
                ),
                radius = size.width * 0.60f,
                center = Offset(size.width * 0.85f, size.height * 0.12f)
            )
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color(0x2200B894), Color(0x0000B894)),
                    center = Offset(size.width * 0.12f, size.height * 0.82f),
                    radius = size.width * 0.50f
                ),
                radius = size.width * 0.50f,
                center = Offset(size.width * 0.12f, size.height * 0.82f)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 28.dp)
        ) {

            // ── Title ───────────────────────────────────────────────────
            Text(
                "Generating",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = (-0.5).sp
                ),
                color = Color.White
            )
            val generatingText = when (uiState.selectedMode) {
                GenerationMode.KEY_POINTS -> "Your Study Notes"
                GenerationMode.FLASHCARDS -> "Your Flashcards"
                GenerationMode.EXAM_QUESTIONS -> "Your Exam Prep"
                GenerationMode.FORMULAE -> "Your Formulae"
                GenerationMode.CUSTOM -> "Your Custom Notes"
            }
            Text(
                generatingText,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    brush = Brush.horizontalGradient(listOf(Primary, SecondaryLight))
                )
            )

            Spacer(Modifier.height(44.dp))

            // ── Central illustration: Dual rings + glowing orb ──────────
            Box(
                modifier = Modifier.size(240.dp),
                contentAlignment = Alignment.Center
            ) {
                // Outer ring — rotates forward
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .size(230.dp)
                        .graphicsLayer { rotationZ = ring1Angle }
                ) {
                    val r = size.minDimension / 2f
                    val sw = 3.dp.toPx()
                    // Dashed orbit ring
                    drawArc(
                        brush = Brush.sweepGradient(
                            0f to Color(0x00A29BFE),
                            0.4f to Color(0xFFA29BFE),
                            0.7f to Color(0xFF6C5CE7),
                            1f to Color(0x00A29BFE)
                        ),
                        startAngle = -90f, sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(
                            width = sw,
                            pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(
                                floatArrayOf(18f, 12f), 0f
                            )
                        )
                    )
                    // Bright moving dot on outer ring
                    drawCircle(
                        color = Color(0xFFA29BFE),
                        radius = sw * 2.2f,
                        center = Offset(size.width, size.height / 2f)
                    )
                    // Shadow glow behind dot
                    drawCircle(
                        color = Color(0x60A29BFE),
                        radius = sw * 5f,
                        center = Offset(size.width, size.height / 2f)
                    )
                }

                // Inner ring — rotates backward
                androidx.compose.foundation.Canvas(
                    modifier = Modifier
                        .size(170.dp)
                        .graphicsLayer { rotationZ = ring2Angle }
                ) {
                    val sw = 2.dp.toPx()
                    drawArc(
                        brush = Brush.sweepGradient(
                            0f to Color(0x0055EFC4),
                            0.5f to Color(0xFF55EFC4),
                            1f to Color(0x0055EFC4)
                        ),
                        startAngle = -90f, sweepAngle = 360f,
                        useCenter = false,
                        style = androidx.compose.ui.graphics.drawscope.Stroke(sw)
                    )
                    // Bright teal dot on inner ring
                    drawCircle(
                        color = Color(0xFF55EFC4),
                        radius = sw * 3f,
                        center = Offset(size.width, size.height / 2f)
                    )
                    drawCircle(
                        color = Color(0x6055EFC4),
                        radius = sw * 7f,
                        center = Offset(size.width, size.height / 2f)
                    )
                }

                // Pulsing central orb
                Box(
                    modifier = Modifier
                        .size(110.dp)
                        .graphicsLayer { scaleX = orbScale; scaleY = orbScale; alpha = orbAlpha }
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                listOf(
                                    Color(0xFFD6CFFF),
                                    Color(0xFF9B8FFF),
                                    Color(0xFF6C5CE7),
                                    Color(0xFF4834D4)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🧠", fontSize = 46.sp)
                }

                // Floating particles around the orb
                Text(
                    "📝", fontSize = 22.sp,
                    modifier = Modifier
                        .offset(x = (-88).dp, y = (p1y - 10).dp)
                        .graphicsLayer { alpha = 0.9f }
                )
                Text(
                    "✨", fontSize = 18.sp,
                    modifier = Modifier.offset(x = 82.dp, y = (p2y + 8).dp)
                )
                Text(
                    "⚡", fontSize = 16.sp,
                    modifier = Modifier.offset(x = (-60).dp, y = (p3y + 60).dp)
                )
                Text(
                    "🔬", fontSize = 20.sp,
                    modifier = Modifier.offset(x = 66.dp, y = (p1y - 55).dp)
                )
                Text(
                    "💡", fontSize = 14.sp,
                    modifier = Modifier.offset(x = 20.dp, y = (p2y + 85).dp)
                )
            }

            Spacer(Modifier.height(40.dp))

            // ── Progress bar ─────────────────────────────────────────────
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Progress",
                    style = MaterialTheme.typography.labelSmall,
                    color = Color(0xFF8888AA)
                )
                Text(
                    "$progressPercent%",
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.ExtraBold,
                        brush = Brush.horizontalGradient(listOf(Primary, SecondaryLight))
                    )
                )
            }
            Spacer(Modifier.height(8.dp))

            // Custom gradient progress bar
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(10.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color(0xFF2A2A4A))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(uiState.processingProgress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .clip(RoundedCornerShape(8.dp))
                        .background(
                            Brush.horizontalGradient(
                                listOf(Primary, SecondaryLight, Secondary)
                            )
                        )
                )
            }

            Spacer(Modifier.height(22.dp))

            // ── Animated dot loader ──────────────────────────────────────
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf(
                    dot1Scale to Primary,
                    dot2Scale to Color(0xFFA29BFE),
                    dot3Scale to SecondaryLight
                ).forEach { (scale, color) ->
                    Box(
                        modifier = Modifier
                            .size(10.dp)
                            .graphicsLayer { scaleX = scale; scaleY = scale }
                            .clip(CircleShape)
                            .background(color)
                    )
                }
            }
        }
    }
}

// ── Premium Bottom Navigation ─────────────────────────────────────────────────

@Composable
fun PremiumBottomNav(
    selectedTab: Int,
    onTabSelected: (Int) -> Unit,
    onFabClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding()
    ) {
        // Nav bar card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .height(68.dp)
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxSize(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavItem(
                    icon = Icons.Outlined.Home,
                    iconSelected = Icons.Filled.Home,
                    label = "Home",
                    isSelected = selectedTab == 0,
                    onClick = { onTabSelected(0) }
                )
                // FAB placeholder spacer
                Spacer(Modifier.width(56.dp))
                NavItem(
                    icon = Icons.Outlined.LibraryBooks,
                    iconSelected = Icons.Filled.LibraryBooks,
                    label = "My Notes",
                    isSelected = selectedTab == 1,
                    onClick = { onTabSelected(1) }
                )
            }
        }

        // Central FAB
        Box(
            modifier = Modifier
                .size(60.dp)
                .align(Alignment.TopCenter)
                .offset(y = (-16).dp)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(listOf(Primary, PrimaryVariant))
                )
                .clickable(onClick = onFabClick)
                .shadow(12.dp, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(Icons.Filled.Add, "Create Note", tint = Color.White, modifier = Modifier.size(28.dp))
        }
    }
}

@Composable
fun NavItem(
    icon: ImageVector,
    iconSelected: ImageVector,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Icon(
            imageVector = if (isSelected) iconSelected else icon,
            contentDescription = label,
            tint = if (isSelected) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.size(24.dp)
        )
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall,
            color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
        )
    }
}

// ── Reusable Components ───────────────────────────────────────────────────────

@Composable
fun QuickActionCard(
    title: String,
    subtitle: String,
    gradientColors: List<Color>,
    iconBgColor: Color,
    iconContent: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        modifier = modifier
            .height(130.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.linearGradient(gradientColors))
                .padding(16.dp)
        ) {
            Column(verticalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxSize()) {
                Box(
                    modifier = Modifier
                        .size(44.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    iconContent()
                }
                Column {
                    Text(
                        title,
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.ExtraBold),
                        color = OnSurfaceLightColor
                    )
                    Text(
                        subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color(0xFF6B6B8A)
                    )
                }
            }
        }
    }
}

@Composable
fun NoteTypeCard(
    emoji: String,
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(80.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) PrimaryContainer else MaterialTheme.colorScheme.surface
        ),
        border = if (isSelected)
            BorderStroke(2.dp, Primary)
        else
            BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = if (isSelected) 0.dp else 1.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(emoji, fontSize = 24.sp)
            Spacer(Modifier.height(4.dp))
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.SemiBold),
                color = if (isSelected) Primary else MaterialTheme.colorScheme.onSurface,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit
) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        content()
    }
}
