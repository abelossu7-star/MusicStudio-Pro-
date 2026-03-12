package com.musicstudio.pro

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.musicstudio.pro.navigation.AppNavHost
import com.musicstudio.pro.ui.theme.MusicStudioProTheme

@dagger.hilt.android.AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MusicStudioProTheme {
                AppNavHost()
            }
        }
    }
}
