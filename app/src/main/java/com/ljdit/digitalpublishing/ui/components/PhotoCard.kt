package com.ljdit.digitalpublishing.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ljdit.digitalpublishing.model.Photo

@Composable
fun PhotoCard(
    photo: Photo,
    onClick: () -> Unit = {}
) {

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .clickable { onClick() }
    ) {

        Image(
            painter = rememberAsyncImagePainter(photo.imageUrl),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth()
                .height(160.dp),
            contentScale = ContentScale.Crop
        )

    }

}