package com.ljdit.digitalpublishing

import RootScreen
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.ljdit.digitalpublishing.ui.navigation.AppNavigation
import com.ljdit.digitalpublishing.ui.theme.DigitalPublishingAssistantAndroidTheme
import com.ljdit.digitalpublishing.ui.screens.PhotoGalleryScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge()

        setContent {
            DigitalPublishingAssistantAndroidTheme {
                RootScreen()
            }
        }
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    DigitalPublishingAssistantAndroidTheme {
        Greeting("Android")
    }
}