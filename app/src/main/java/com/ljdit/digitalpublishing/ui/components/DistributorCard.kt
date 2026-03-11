package com.ljdit.digitalpublishing.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ljdit.digitalpublishing.model.Distributor

@Composable
fun DistributorCard(
    distributor: Distributor,
    onClick: () -> Unit
) {

    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {

        Image(
            painter = rememberAsyncImagePainter(distributor.logoUrl),
            contentDescription = distributor.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp),
            contentScale = ContentScale.Fit
        )

    }
}