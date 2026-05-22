@file:OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)

package com.ainotes.ui.screens.results

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.material3.ExperimentalMaterial3Api

import android.content.Intent
import android.os.Environment
import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ainotes.data.model.*
import com.ainotes.ui.screens.home.GlassCard
import com.ainotes.ui.theme.*
import kotlinx.coroutines.launch
import java.io.File

// ─── Screen Entry Point ───────────────────────────────────────────────────────

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ResultsScreen(
    sessionId: String,
    onBack: () -> Unit,
    viewModel: ResultsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val clipboard = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(sessionId) {
        viewModel.loadSession(sessionId)
    }

    val session = uiState.session
    val notes = session?.notes

    // Helper to build full text content
    fun buildAllText() = buildString {
        appendLine(session?.title ?: "")
        appendLine()
        notes?.keyPoints?.forEach { kp ->
            appendLine("## ${kp.topic}")
            kp.points.forEach { appendLine("• $it") }
            appendLine()
        }
        notes?.flashcards?.forEach { fc ->
            appendLine("Q: ${fc.front}")
            appendLine("A: ${fc.back}")
            appendLine()
        }
        notes?.formulae?.forEach { f ->
            appendLine("${f.name}: ${f.formula}")
            appendLine(f.explanation)
            appendLine()
        }
        notes?.examQuestions?.forEach { q ->
            appendLine("Q${q.question}")
            appendLine("A: ${q.answer}")
            appendLine()
        }
        notes?.customResult?.let { appendLine(it) }
    }

    // Button action states
    var isCopied by remember { mutableStateOf(false) }
    val isSaved = session?.isSaved ?: false

    val tabs = buildList {
        if ((notes?.keyPoints?.size ?: 0) > 0) add(ResultTab.KEY_POINTS)
        if ((notes?.flashcards?.size ?: 0) > 0) add(ResultTab.FLASHCARDS)
        if ((notes?.formulae?.size ?: 0) > 0) add(ResultTab.FORMULAE)
        if ((notes?.examQuestions?.size ?: 0) > 0) add(ResultTab.QUESTIONS)
        if ((notes?.customResult?.length ?: 0) > 0) add(ResultTab.CUSTOM)
    }

    val pagerState = rememberPagerState { tabs.size.coerceAtLeast(1) }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(getAppBackgroundGradient())
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Top Bar ──────────────────────────────────────────────────────
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .statusBarsPadding()
                    .padding(horizontal = 8.dp, vertical = 8.dp),
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
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session?.title ?: "Results",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1
                    )
                    if (session != null) {
                        Text(
                            text = "${GenerationMode.valueOf(session.mode).displayName} ${GenerationMode.valueOf(session.mode).emoji}",
                            style = MaterialTheme.typography.bodySmall,
                            color = Primary
                        )
                    }
                }
                // Share button
                IconButton(onClick = {
                    val allText = buildString {
                        notes?.keyPoints?.forEach { kp ->
                            appendLine("## ${kp.topic}")
                            kp.points.forEach { appendLine("• $it") }
                            appendLine()
                        }
                        notes?.flashcards?.forEach { fc ->
                            appendLine("Q: ${fc.front}")
                            appendLine("A: ${fc.back}")
                            appendLine()
                        }
                    }
                    clipboard.setText(AnnotatedString(allText))
                }) {
                    Icon(Icons.Outlined.Share, null, tint = Primary)
                }
            }

            if (uiState.isLoading) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Primary)
                }
                return@Column
            }

            if (tabs.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("😕", fontSize = 48.sp)
                        Spacer(Modifier.height(8.dp))
                        Text("No results found", style = MaterialTheme.typography.titleMedium)
                        Text(
                            "Try a different file or mode",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
                return@Column
            }

            // ── Stat chips row ───────────────────────────────────────────────
            if (notes != null) {
                LazyRow(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (notes.flashcards.isNotEmpty()) item { StatChip("${notes.flashcards.size} Cards", FlashcardColor) }
                    if (notes.keyPoints.isNotEmpty()) item { StatChip("${notes.keyPoints.sumOf { it.points.size }} Points", KeyPointColor) }
                    if (notes.formulae.isNotEmpty()) item { StatChip("${notes.formulae.size} Formulae", FormulaColor) }
                    if (notes.examQuestions.isNotEmpty()) item { StatChip("${notes.examQuestions.size} Qs", QuestionColor) }
                }
            }

            // ── Tab Row ──────────────────────────────────────────────────────
            ScrollableTabRow(
                selectedTabIndex = pagerState.currentPage,
                modifier = Modifier.fillMaxWidth(),
                containerColor = Color.Transparent,
                contentColor = Primary,
                edgePadding = 16.dp,
                divider = {},

            ) {
                tabs.forEachIndexed { index, tab ->
                    Tab(
                        selected = pagerState.currentPage == index,
                        onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                        text = {
                            Text(
                                "${tab.emoji} ${tab.label}",
                                fontWeight = if (pagerState.currentPage == index) FontWeight.Bold else FontWeight.Normal,
                                fontSize = 13.sp
                            )
                        },
                        selectedContentColor = Primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(0.4f))

            // ── Pager Content ────────────────────────────────────────────────
            if (notes != null) {
                HorizontalPager(
                    state = pagerState,
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f)
                ) { page ->
                    when (tabs.getOrNull(page)) {
                        ResultTab.FLASHCARDS -> FlashcardsTab(notes.flashcards)
                        ResultTab.KEY_POINTS -> KeyPointsTab(notes.keyPoints)
                        ResultTab.FORMULAE -> FormulaeTab(notes.formulae)
                        ResultTab.QUESTIONS -> QuestionsTab(notes.examQuestions)
                        ResultTab.CUSTOM -> CustomResultTab(notes.customResult)
                        null -> {}
                    }
                }
            }
        }

        // ── Bottom Action Bar ────────────────────────────────────────────────
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                // Copy button with feedback
                BottomActionButton(
                    icon = if (isCopied) Icons.Filled.Check else Icons.Outlined.ContentCopy,
                    label = if (isCopied) "Copied!" else "Copy",
                    tintColor = if (isCopied) Secondary else Primary
                ) {
                    clipboard.setText(AnnotatedString(buildAllText()))
                    isCopied = true
                }

                // Share button — opens Android share sheet
                BottomActionButton(icon = Icons.Outlined.Share, label = "Share", tintColor = Primary) {
                    val text = buildAllText()
                    val intent = Intent(Intent.ACTION_SEND).apply {
                        type = "text/plain"
                        putExtra(Intent.EXTRA_SUBJECT, session?.title ?: "AI Notes")
                        putExtra(Intent.EXTRA_TEXT, text)
                    }
                    context.startActivity(Intent.createChooser(intent, "Share Notes via"))
                }

                // Download button — writes to Downloads folder
                BottomActionButton(icon = Icons.Outlined.Download, label = "Download", tintColor = Primary) {
                    try {
                        val text = buildAllText()
                        val fileName = "${(session?.title ?: "AINotes").take(30).replace(Regex("[^a-zA-Z0-9 ]"), "")}.txt"
                        val downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                        downloadsDir.mkdirs()
                        val file = File(downloadsDir, fileName)
                        file.writeText(text)
                        Toast.makeText(context, "✅ Saved to Downloads: $fileName", Toast.LENGTH_LONG).show()
                    } catch (e: Exception) {
                        Toast.makeText(context, "❌ Could not save: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }

                // Save / Bookmark button
                BottomActionButton(
                    icon = if (isSaved) Icons.Filled.Bookmark else Icons.Outlined.BookmarkBorder,
                    label = if (isSaved) "Saved" else "Save",
                    tintColor = if (isSaved) Tertiary else Primary
                ) {
                    viewModel.toggleSavedStatus()
                    val nextSavedState = !isSaved
                    if (nextSavedState) {
                        Toast.makeText(context, "⭐ Note bookmarked!", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(context, "Removed from bookmarks", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}

@Composable
fun BottomActionButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tintColor: Color = Primary,
    onClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(44.dp)
                .clip(CircleShape)
                .background(tintColor.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, contentDescription = label, tint = tintColor, modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.height(4.dp))
        Text(label, style = MaterialTheme.typography.labelSmall, color = tintColor)
    }
}

// ─── Tab Definitions ──────────────────────────────────────────────────────────

enum class ResultTab(val label: String, val emoji: String) {
    KEY_POINTS("Notes", "📝"),
    FLASHCARDS("Flashcards", "📇"),
    FORMULAE("Formulae", "🔢"),
    QUESTIONS("Exam Qs", "❓"),
    CUSTOM("Result", "💬")
}

// ─── Key Points Tab ───────────────────────────────────────────────────────────

@Composable
fun KeyPointsTab(keyPoints: List<KeyPoint>) {
    val clipboard = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(keyPoints) { kp ->
            var expanded by remember { mutableStateOf(true) }
            KeyPointCard(kp = kp, expanded = expanded, onToggle = { expanded = !expanded }, clipboard = clipboard)
        }
    }
}

@Composable
fun KeyPointCard(
    kp: KeyPoint,
    expanded: Boolean,
    onToggle: () -> Unit,
    clipboard: androidx.compose.ui.platform.ClipboardManager
) {
    val bulletEmojis = listOf("🌿", "⭐", "🔵", "🟣", "🔶", "💎", "🌀", "🏷️")

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            // Topic header
            Row(
                modifier = Modifier.clickable(onClick = onToggle),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(PrimaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text("📌", fontSize = 16.sp)
                }
                Spacer(Modifier.width(10.dp))
                Text(
                    text = kp.topic,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = Primary,
                    modifier = Modifier.weight(1f)
                )
                Icon(
                    if (expanded) Icons.Filled.ExpandLess else Icons.Filled.ExpandMore,
                    null,
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(20.dp)
                )
            }

            AnimatedVisibility(visible = expanded) {
                Column(modifier = Modifier.padding(top = 14.dp)) {
                    kp.points.forEachIndexed { index, point ->
                        Row(
                            modifier = Modifier.padding(vertical = 5.dp),
                            verticalAlignment = Alignment.Top
                        ) {
                            Text(
                                bulletEmojis.getOrElse(index) { "•" },
                                fontSize = 14.sp,
                                modifier = Modifier.padding(top = 1.dp, end = 10.dp)
                            )
                            Text(
                                text = parseMarkdownToAnnotatedString(point, false, false),
                                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                    Spacer(Modifier.height(8.dp))
                    TextButton(
                        onClick = {
                            val text = "${kp.topic}\n${kp.points.joinToString("\n") { "• $it" }}"
                            clipboard.setText(AnnotatedString(text))
                        },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Outlined.ContentCopy, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Copy", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ─── Flashcards Tab ────────────────────────────────────────────────────────────

@Composable
fun FlashcardsTab(flashcards: List<Flashcard>) {
    val pagerState = rememberPagerState { flashcards.size }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "${pagerState.currentPage + 1} / ${flashcards.size}",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.padding(bottom = 12.dp)
        )

        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            contentPadding = PaddingValues(horizontal = 24.dp),
            pageSpacing = 16.dp
        ) { page ->
            FlipCard(flashcard = flashcards[page])
        }

        Spacer(Modifier.height(16.dp))
        Text(
            "← Swipe to browse →",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(Modifier.height(6.dp))
        Text(
            "Tap card to flip",
            style = MaterialTheme.typography.bodySmall,
            color = Primary,
            fontWeight = FontWeight.SemiBold
        )
        Spacer(Modifier.height(100.dp))
    }
}

fun parseMarkdownToAnnotatedString(text: String, isDark: Boolean, isFront: Boolean): AnnotatedString {
    return buildAnnotatedString {
        val regex = Regex("\\*\\*(.*?)\\*\\*")
        var lastIndex = 0
        val matches = regex.findAll(text)
        for (match in matches) {
            append(text.substring(lastIndex, match.range.first))
            val boldContent = match.groupValues[1]
            val emoji = when {
                boldContent.contains("registration", ignoreCase = true) -> "📝 "
                boldContent.contains("collection", ignoreCase = true) -> "📦 "
                boldContent.contains("target", ignoreCase = true) -> "🎯 "
                boldContent.contains("record", ignoreCase = true) || boldContent.contains("report", ignoreCase = true) -> "📊 "
                boldContent.contains("goal", ignoreCase = true) || boldContent.contains("objective", ignoreCase = true) -> "🏆 "
                boldContent.contains("definition", ignoreCase = true) -> "📖 "
                boldContent.contains("example", ignoreCase = true) -> "💡 "
                boldContent.contains("formula", ignoreCase = true) || boldContent.contains("equation", ignoreCase = true) -> "🔢 "
                boldContent.contains("key", ignoreCase = true) || boldContent.contains("concept", ignoreCase = true) -> "🔑 "
                boldContent.contains("important", ignoreCase = true) || boldContent.contains("note", ignoreCase = true) -> "⚠️ "
                boldContent.contains("benefit", ignoreCase = true) || boldContent.contains("advantage", ignoreCase = true) -> "🌟 "
                boldContent.contains("disadvantage", ignoreCase = true) || boldContent.contains("challenge", ignoreCase = true) -> "⚡ "
                boldContent.contains("process", ignoreCase = true) || boldContent.contains("step", ignoreCase = true) -> "🔄 "
                boldContent.contains("history", ignoreCase = true) || boldContent.contains("date", ignoreCase = true) -> "📅 "
                boldContent.contains("law", ignoreCase = true) || boldContent.contains("rule", ignoreCase = true) -> "⚖️ "
                else -> ""
            }
            if (emoji.isNotEmpty()) {
                val currentText = toAnnotatedString().text
                if (!currentText.endsWith(emoji)) append(emoji)
            }
            withStyle(SpanStyle(
                fontWeight = FontWeight.Bold,
                color = if (isFront) Secondary else { if (isDark) Secondary else Primary }
            )) {
                append(boldContent)
            }
            lastIndex = match.range.last + 1
        }
        if (lastIndex < text.length) append(text.substring(lastIndex))
    }
}

@Composable
fun FlipCard(flashcard: Flashcard) {
    var isFlipped by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(
        targetValue = if (isFlipped) 180f else 0f,
        animationSpec = tween(durationMillis = 400, easing = EaseInOutCubic),
        label = "card_flip"
    )

    val frontScrollState = rememberScrollState()
    val backScrollState = rememberScrollState()
    val isDark = MaterialTheme.colorScheme.background == BackgroundDark

    Box(
        modifier = Modifier
            .fillMaxSize()
            .graphicsLayer {
                rotationY = rotation
                cameraDistance = 12 * density
            }
            .clip(RoundedCornerShape(24.dp))
            .background(
                Brush.linearGradient(
                    colors = if (!isFlipped) {
                        listOf(Primary, PrimaryVariant)
                    } else {
                        if (isDark) listOf(Color(0xFF1E1E40), Secondary.copy(alpha = 0.3f))
                        else listOf(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.colorScheme.surface)
                    }
                )
            )
            .clickable { isFlipped = !isFlipped }
            .padding(28.dp),
        contentAlignment = Alignment.Center
    ) {
        if (rotation <= 90f) {
            Column(
                modifier = Modifier.verticalScroll(frontScrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("❓", fontSize = 32.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = parseMarkdownToAnnotatedString(flashcard.front, isDark = isDark, isFront = true),
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = 24.sp,
                        letterSpacing = 0.3.sp
                    ),
                    color = Color.White,
                    textAlign = TextAlign.Center
                )
                if (flashcard.topic.isNotBlank()) {
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = flashcard.topic,
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.White.copy(alpha = 0.7f),
                        modifier = Modifier
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color.White.copy(alpha = 0.15f))
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    )
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .graphicsLayer { rotationY = 180f }
                    .verticalScroll(backScrollState),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("✅", fontSize = 32.sp)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = parseMarkdownToAnnotatedString(flashcard.back, isDark = isDark, isFront = false),
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontFamily = FontFamily.SansSerif,
                        lineHeight = 26.sp,
                        letterSpacing = 0.3.sp
                    ),
                    color = MaterialTheme.colorScheme.onSurface,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

// ─── Formulae Tab ─────────────────────────────────────────────────────────────

@Composable
fun FormulaeTab(formulae: List<Formula>) {
    val clipboard = LocalClipboardManager.current

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(formulae) { formula ->
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
            ) {
                Column(modifier = Modifier.padding(18.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .background(Color(0xFFFFF3E0)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text("🔢", fontSize = 16.sp)
                        }
                        Spacer(Modifier.width(10.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = formula.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                                color = FormulaColor
                            )
                            if (formula.topic.isNotBlank()) {
                                Text(
                                    formula.topic,
                                    style = MaterialTheme.typography.labelSmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }

                    Spacer(Modifier.height(14.dp))

                    // Chemistry-style formula box
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(14.dp))
                            .background(FormulaBoxBg)
                            .border(1.5.dp, FormulaBoxBorder, RoundedCornerShape(14.dp))
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = formula.formula,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontFamily = FontFamily.Monospace,
                                fontWeight = FontWeight.Bold
                            ),
                            color = FormulaBoxBorder,
                            textAlign = TextAlign.Center
                        )
                    }

                    Spacer(Modifier.height(10.dp))
                    Text(
                        text = formula.explanation,
                        style = MaterialTheme.typography.bodySmall.copy(lineHeight = 20.sp),
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    TextButton(
                        onClick = { clipboard.setText(AnnotatedString("${formula.name}: ${formula.formula}")) },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Icon(Icons.Outlined.ContentCopy, null, modifier = Modifier.size(14.dp))
                        Spacer(Modifier.width(4.dp))
                        Text("Copy", fontSize = 12.sp)
                    }
                }
            }
        }
    }
}

// ─── Exam Questions Tab ───────────────────────────────────────────────────────

@Composable
fun QuestionsTab(questions: List<ExamQuestion>) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 100.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        items(questions.withIndex().toList()) { (index, question) ->
            QuestionCard(index + 1, question)
        }
    }
}

@Composable
fun QuestionCard(number: Int, question: ExamQuestion) {
    var showAnswer by remember { mutableStateOf(false) }
    val typeColor = when (question.type) {
        QuestionType.MCQ -> Color(0xFF9C27B0)
        QuestionType.SHORT_ANSWER -> QuestionColor
        QuestionType.LONG_ANSWER -> FormulaColor
        QuestionType.TRUE_FALSE -> Secondary
    }
    val typeLabel = when (question.type) {
        QuestionType.MCQ -> "MCQ"
        QuestionType.SHORT_ANSWER -> "Short Answer"
        QuestionType.LONG_ANSWER -> "Long Answer"
        QuestionType.TRUE_FALSE -> "True / False"
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(30.dp)
                        .clip(CircleShape)
                        .background(typeColor.copy(0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("$number", fontWeight = FontWeight.Bold, color = typeColor, fontSize = 12.sp)
                }
                Spacer(Modifier.width(10.dp))
                TypeChip(typeLabel, typeColor)
                Spacer(Modifier.width(6.dp))
                TypeChip("${question.marks} marks", MaterialTheme.colorScheme.onSurfaceVariant)
            }

            Spacer(Modifier.height(12.dp))

            Text(
                text = "Q. ${question.question}",
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    lineHeight = 22.sp
                ),
                color = MaterialTheme.colorScheme.onSurface
            )

            if (question.type == QuestionType.MCQ && question.options.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                question.options.forEachIndexed { i, option ->
                    val label = listOf("A", "B", "C", "D").getOrElse(i) { "${i + 1}" }
                    Row(modifier = Modifier.padding(vertical = 2.dp)) {
                        Text("$label. ", fontWeight = FontWeight.Bold, color = typeColor, style = MaterialTheme.typography.bodySmall)
                        Text(option, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurface)
                    }
                }
            }

            Spacer(Modifier.height(10.dp))

            OutlinedButton(
                onClick = { showAnswer = !showAnswer },
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = typeColor),
                border = BorderStroke(1.dp, typeColor.copy(0.5f))
            ) {
                Icon(if (showAnswer) Icons.Filled.VisibilityOff else Icons.Filled.Visibility, null, modifier = Modifier.size(16.dp))
                Spacer(Modifier.width(6.dp))
                Text(if (showAnswer) "Hide Answer" else "Show Answer", fontSize = 13.sp)
            }

            AnimatedVisibility(visible = showAnswer) {
                Column(modifier = Modifier.padding(top = 12.dp)) {
                    HorizontalDivider(color = typeColor.copy(0.3f))
                    Spacer(Modifier.height(10.dp))
                    Text("✅ Answer:", fontWeight = FontWeight.Bold, color = typeColor, style = MaterialTheme.typography.labelMedium)
                    Spacer(Modifier.height(4.dp))
                    Text(
                        question.answer,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

// ─── Custom Result Tab ────────────────────────────────────────────────────────

@Composable
fun CustomResultTab(result: String) {
    val clipboard = LocalClipboardManager.current
    var copied by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            OutlinedButton(
                onClick = {
                    clipboard.setText(AnnotatedString(result))
                    copied = true
                },
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    if (copied) Icons.Filled.Check else Icons.Outlined.ContentCopy,
                    null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(Modifier.width(4.dp))
                Text(if (copied) "Copied!" else "Copy All")
            }
        }
        Spacer(Modifier.height(8.dp))
        GlassCard(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = result,
                style = MaterialTheme.typography.bodyMedium.copy(lineHeight = 22.sp),
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            )
        }
        Spacer(Modifier.navigationBarsPadding())
    }
}

// ─── Helper Composables ───────────────────────────────────────────────────────

@Composable
fun StatChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(color.copy(alpha = 0.12f))
            .border(1.dp, color.copy(0.25f), RoundedCornerShape(20.dp))
            .padding(horizontal = 12.dp, vertical = 5.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun TypeChip(label: String, color: Color) {
    Box(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.10f))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(label, style = MaterialTheme.typography.labelSmall, color = color)
    }
}
