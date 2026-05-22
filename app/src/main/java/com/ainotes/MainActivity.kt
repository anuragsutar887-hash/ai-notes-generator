package com.ainotes

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.ainotes.data.local.ThemePreferences
import com.ainotes.ui.navigation.AiNotesNavGraph
import com.ainotes.ui.theme.AiNotesTheme
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    lateinit var themePreferences: ThemePreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val themeMode by themePreferences.themeModeFlow.collectAsState()
            val isDarkTheme = when (themeMode) {
                ThemePreferences.THEME_LIGHT -> false
                ThemePreferences.THEME_DARK -> true
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            val composeLifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
            androidx.compose.runtime.CompositionLocalProvider(
                androidx.lifecycle.compose.LocalLifecycleOwner provides composeLifecycleOwner
            ) {
                AiNotesTheme(darkTheme = isDarkTheme) {
                    Surface(
                        modifier = Modifier.fillMaxSize(),
                        color = MaterialTheme.colorScheme.background
                    ) {
                        AiNotesNavGraph()
                    }
                }
            }
        }
    }
}
