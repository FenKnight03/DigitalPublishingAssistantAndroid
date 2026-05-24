package com.ljdit.digitalpublishing.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CoordinateSelectionScreen(
    photoId: String?,
    logoId: String?,
    navController: NavController
) {

        Scaffold(
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
                    .windowInsetsPadding(WindowInsets.statusBars)
                    .background(color = White)
            ) {

                Column(
                    modifier = Modifier.padding(20.dp)
                ) {

                    Text("Elegir posición del logo")

                    Spacer(modifier = Modifier.height(20.dp))

                    Button(onClick = {

                        navController.navigate(
                            "preview/$photoId/$logoId/1"
                        )

                    }) {
                        Text("Posición 1")
                    }

                    Button(onClick = {

                        navController.navigate(
                            "preview/$photoId/$logoId/2"
                        )

                    }) {
                        Text("Posición 2")
                    }

                    Button(onClick = {

                        navController.navigate(
                            "preview/$photoId/$logoId/3"
                        )

                    }) {
                        Text("Posición 3")
                    }

                }
            }
        }

}
