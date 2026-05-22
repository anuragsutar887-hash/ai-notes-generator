package com.ainotes.ui.screens.profile

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.Canvas
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.hilt.navigation.compose.hiltViewModel
import com.ainotes.ui.theme.*
import kotlinx.coroutines.flow.collectLatest
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun SplashScreen(
    onNavigateToLogin: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToProfileSetup: () -> Unit,
    viewModel: SplashViewModel = hiltViewModel()
) {
    // Floating particle animation
    val infiniteTransition = rememberInfiniteTransition(label = "float")
    val floatY by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 16f,
        animationSpec = infiniteRepeatable(
            animation = tween(2200, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "floatY"
    )
    val rotateAngle by infiniteTransition.animateFloat(
        initialValue = -4f,
        targetValue = 4f,
        animationSpec = infiniteRepeatable(
            animation = tween(2600, easing = EaseInOutSine),
            repeatMode = RepeatMode.Reverse
        ),
        label = "rotate"
    )

    LaunchedEffect(key1 = true) {
        viewModel.eventFlow.collectLatest { event ->
            when (event) {
                is SplashEvent.NavigateToLogin -> onNavigateToLogin()
                is SplashEvent.NavigateToHome -> onNavigateToHome()
                is SplashEvent.NavigateToProfileSetup -> onNavigateToProfileSetup()
            }
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFFF7F6FF),
                        Color(0xFFEFECFF),
                        Color(0xFFFFFFFF)
                    )
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        // Background decorative circles
        Canvas(modifier = Modifier.fillMaxSize()) {
            // Large soft violet blob top-right
            drawCircle(
                color = Color(0x1A6C5CE7),
                radius = size.width * 0.55f,
                center = Offset(size.width * 0.85f, size.height * 0.08f)
            )
            // Smaller teal blob bottom-left
            drawCircle(
                color = Color(0x1200B894),
                radius = size.width * 0.40f,
                center = Offset(size.width * 0.10f, size.height * 0.88f)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            Spacer(Modifier.weight(1f))

            // ── 3D-styled Illustration ──────────────────────────────────────
            Box(
                modifier = Modifier
                    .size(220.dp)
                    .offset(y = (-floatY).dp),
                contentAlignment = Alignment.Center
            ) {
                // Glow ring behind illustration
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Primary.copy(alpha = 0.18f),
                                    Primary.copy(alpha = 0.04f),
                                    Color.Transparent
                                )
                            )
                        )
                )

                // Main illustration canvas
                Canvas(modifier = Modifier.size(170.dp)) {
                    drawNoteBookIllustration(this)
                }

                // Floating sparkle particles around illustration
                FloatingParticle(size = 10.dp, color = Primary, xOffset = (-60).dp, yOffset = (-55).dp, phase = 0f, transition = infiniteTransition)
                FloatingParticle(size = 7.dp, color = Secondary, xOffset = 62.dp, yOffset = (-40).dp, phase = 0.3f, transition = infiniteTransition)
                FloatingParticle(size = 8.dp, color = Tertiary, xOffset = (-50).dp, yOffset = 55.dp, phase = 0.6f, transition = infiniteTransition)
                FloatingParticle(size = 6.dp, color = Primary.copy(alpha = 0.6f), xOffset = 58.dp, yOffset = 52.dp, phase = 0.9f, transition = infiniteTransition)
            }

            Spacer(Modifier.height(40.dp))

            // ── Brand Typography ────────────────────────────────────────────
            Text(
                text = "AI Notes",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 44.sp,
                    letterSpacing = (-1.5).sp,
                    color = OnSurfaceLightColor
                )
            )

            Text(
                text = "Generator",
                style = MaterialTheme.typography.displayMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 44.sp,
                    letterSpacing = (-1.5).sp,
                    brush = Brush.horizontalGradient(
                        colors = listOf(Primary, PrimaryVariant)
                    )
                )
            )

            val isOnboardingCompleted = viewModel.isOnboardingCompleted

            if (!isOnboardingCompleted) {
                Spacer(Modifier.height(16.dp))

                Text(
                    text = "Create smart study notes in seconds\nwith the power of AI ✨",
                    style = MaterialTheme.typography.bodyLarge.copy(
                        lineHeight = 26.sp,
                        color = Color(0xFF6B6B8A)
                    ),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.weight(1f))

                // ── Get Started Button ──────────────────────────────────────────
                Button(
                    onClick = { viewModel.completeOnboarding() },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(58.dp),
                    shape = RoundedCornerShape(30.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                    contentPadding = PaddingValues(0.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.horizontalGradient(listOf(Primary, PrimaryVariant)),
                                RoundedCornerShape(30.dp)
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                "Get Started",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 17.sp
                                )
                            )
                            Spacer(Modifier.width(10.dp))
                            Text("→", fontSize = 18.sp, color = Color.White)
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                Text(
                    "By continuing, you agree to our Terms & Privacy Policy",
                    style = MaterialTheme.typography.bodySmall,
                    color = Color(0xFFAAAAAA),
                    textAlign = TextAlign.Center
                )

                Spacer(Modifier.height(32.dp))
            } else {
                // Splash/Loading mode: Display a beautiful subtle progress indicator
                Spacer(Modifier.height(44.dp))
                CircularProgressIndicator(
                    color = Primary,
                    strokeWidth = 3.dp,
                    modifier = Modifier.size(28.dp)
                )
                Spacer(Modifier.weight(1f))
                Spacer(Modifier.height(32.dp))
            }
        }
    }
}

// Draws the notebook / document 3D-styled illustration
fun drawNoteBookIllustration(scope: DrawScope) {
    val w = scope.size.width
    val h = scope.size.height
    val cx = w / 2f
    val cy = h / 2f

    // Shadow
    scope.drawRoundRect(
        color = Color(0x226C5CE7),
        topLeft = Offset(cx - w * 0.28f + 6f, cy - h * 0.35f + 8f),
        size = Size(w * 0.56f, h * 0.70f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
    )

    // Notebook body
    scope.drawRoundRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFFFFF), Color(0xFFF0EEFF)),
            start = Offset(cx - w * 0.28f, cy - h * 0.35f),
            end = Offset(cx + w * 0.28f, cy + h * 0.35f)
        ),
        topLeft = Offset(cx - w * 0.28f, cy - h * 0.35f),
        size = Size(w * 0.56f, h * 0.70f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
    )

    // Left accent stripe
    scope.drawRoundRect(
        brush = Brush.verticalGradient(
            colors = listOf(Color(0xFF6C5CE7), Color(0xFF4834D4)),
            startY = cy - h * 0.35f,
            endY = cy + h * 0.35f
        ),
        topLeft = Offset(cx - w * 0.28f, cy - h * 0.35f),
        size = Size(w * 0.09f, h * 0.70f),
        cornerRadius = androidx.compose.ui.geometry.CornerRadius(24f, 24f)
    )

    // Lines on notebook
    val lineColor = Color(0xFFDDD8FF)
    val lineStart = cx - w * 0.10f
    val lineEnd = cx + w * 0.24f
    listOf(-0.16f, -0.06f, 0.04f, 0.14f, 0.24f).forEach { offset ->
        scope.drawLine(
            color = lineColor,
            start = Offset(lineStart, cy + h * offset),
            end = Offset(lineEnd, cy + h * offset),
            strokeWidth = 2.5f
        )
    }

    // Pencil body (diagonal overlay top-right)
    val pencilPivot = Offset(cx + w * 0.30f, cy - h * 0.30f)
    val angle = Math.toRadians(-38.0)
    val pLen = h * 0.38f
    val pW = w * 0.045f

    fun rotated(px: Float, py: Float): Offset {
        val rx = (px * cos(angle) - py * sin(angle)).toFloat()
        val ry = (px * sin(angle) + py * cos(angle)).toFloat()
        return Offset(pencilPivot.x + rx, pencilPivot.y + ry)
    }

    // Pencil shaft
    scope.drawRect(
        brush = Brush.linearGradient(
            colors = listOf(Color(0xFFFFD166), Color(0xFFFFA500)),
            start = pencilPivot,
            end = Offset(pencilPivot.x + pLen, pencilPivot.y)
        ),
        topLeft = Offset(pencilPivot.x, pencilPivot.y - pW / 2),
        size = Size(pLen, pW)
    )

    // Pencil tip
    scope.drawCircle(
        color = Color(0xFF333333),
        radius = pW * 0.45f,
        center = Offset(pencilPivot.x + pLen, pencilPivot.y)
    )

    // Pencil eraser end
    scope.drawRect(
        color = Color(0xFFFF8FA3),
        topLeft = Offset(pencilPivot.x - pW * 1.2f, pencilPivot.y - pW / 2),
        size = Size(pW * 1.2f, pW)
    )
}

@Composable
fun FloatingParticle(
    size: Dp,
    color: Color,
    xOffset: Dp,
    yOffset: Dp,
    phase: Float,
    transition: InfiniteTransition
) {
    val floatAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            animation = tween(
                durationMillis = (1800 + (phase * 600).toInt()),
                easing = EaseInOutSine
            ),
            repeatMode = RepeatMode.Reverse
        ),
        label = "particle_$phase"
    )
    Box(
        modifier = Modifier
            .offset(x = xOffset, y = yOffset + floatAnim.dp)
            .size(size)
            .clip(CircleShape)
            .background(color)
    )
}
