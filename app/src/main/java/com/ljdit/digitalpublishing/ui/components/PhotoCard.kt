package com.ljdit.digitalpublishing.ui.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.ljdit.digitalpublishing.R
import com.ljdit.digitalpublishing.model.Photo

@Composable
fun PhotoCard(
    photo: Photo,
    onClick: () -> Unit = {},
    aspectRatio: Float = photo.displayAspectRatio()
) {
    val platformKey = photo.platform?.key?.lowercase()
    val platformIcon = when (platformKey) {
        "facebook" -> R.drawable.ic_facebook
        "instagram" -> R.drawable.ic_instagram
        else -> null
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(aspectRatio)
            .clip(RoundedCornerShape(14.dp))
            .clickable { onClick() }
    ) {
        SubcomposeAsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(photo.imageUrl)
                .crossfade(true)
                .memoryCacheKey(photo.imageUrl)
                .diskCacheKey(photo.imageUrl)
                .diskCachePolicy(CachePolicy.ENABLED)
                .memoryCachePolicy(CachePolicy.ENABLED)
                .networkCachePolicy(CachePolicy.ENABLED)
                .build(),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        ) {
            when (painter.state) {
                is AsyncImagePainter.State.Loading -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEDEFF5))
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = Color(0xFF1F65D6)
                        )
                    }
                }

                is AsyncImagePainter.State.Error -> {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFFEDEFF5))
                    ) {
                        Text(
                            text = "Error",
                            color = Color(0xFF667085),
                            modifier = Modifier.align(Alignment.Center)
                        )
                    }
                }

                else -> {
                    SubcomposeAsyncImageContent()
                }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color.Black.copy(alpha = 0.58f)
                        ),
                        startY = 120f
                    )
                )
        )

        Column(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .fillMaxWidth()
                .padding(10.dp)
                .padding(end = if (platformIcon == null) 0.dp else 42.dp)
        ) {
            photo.producto?.takeIf { it.isNotBlank() }?.let { product ->
                Text(
                    text = product,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Row(verticalAlignment = Alignment.CenterVertically) {
                photo.origen?.takeIf { it.isNotBlank() }?.let { origin ->
                    Text(
                        text = origin.replaceFirstChar { it.titlecase() },
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.86f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                val platformName = photo.platform?.name?.takeIf { it.isNotBlank() }
                if (photo.origen?.isNotBlank() == true && platformName != null) {
                    Spacer(modifier = Modifier.width(6.dp))
                }

                platformName?.let { name ->
                    Text(
                        text = name,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White.copy(alpha = 0.86f),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
        }

        platformIcon?.let { icon ->
            Image(
                painter = painterResource(id = icon),
                contentDescription = platformKey,
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(7.dp)
                    .size(34.dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.72f))
                    .padding(5.dp)
            )
        }
    }
}

private fun Photo.displayAspectRatio(): Float {
    val normalizedFormat = formato?.lowercase()

    return when (normalizedFormat) {
        "horizontal" -> 16f / 9f
        "cuadrado", "square" -> 1f
        "semivertical", "semi_vertical", "semi-vertical" -> 4f / 5f
        "vertical" -> 3f / 4f
        else -> {
            val widthValue = width
            val heightValue = height
            if (widthValue != null && heightValue != null && heightValue > 0) {
                widthValue.toFloat() / heightValue.toFloat()
            } else {
                1f
            }
        }
    }
}
