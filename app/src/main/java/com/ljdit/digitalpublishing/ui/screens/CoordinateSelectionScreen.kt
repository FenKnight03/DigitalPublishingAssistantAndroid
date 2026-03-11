package com.ljdit.digitalpublishing.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun CoordinateSelectionScreen(
    photoId: String?,
    distributorId: String?,
    navController: NavController
) {

    Column(
        modifier = Modifier.padding(20.dp)
    ) {

        Text("Elegir posición del logo")

        Spacer(modifier = Modifier.height(20.dp))

        Button(onClick = {

            navController.navigate(
                "preview/$photoId/$distributorId/1"
            )

        }) {
            Text("Posición 1")
        }

        Button(onClick = {

            navController.navigate(
                "preview/$photoId/$distributorId/2"
            )

        }) {
            Text("Posición 2")
        }

        Button(onClick = {

            navController.navigate(
                "preview/$photoId/$distributorId/3"
            )

        }) {
            Text("Posición 3")
        }

    }

}