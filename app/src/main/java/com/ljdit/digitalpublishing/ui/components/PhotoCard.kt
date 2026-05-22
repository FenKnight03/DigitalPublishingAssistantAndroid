package com.ljdit.digitalpublishing.ui.components

import android.icu.number.Scale
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import com.ljdit.digitalpublishing.R
import com.ljdit.digitalpublishing.model.Photo
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.ui.platform.LocalContext
import coil.compose.AsyncImagePainter
import coil.compose.SubcomposeAsyncImage
import coil.compose.SubcomposeAsyncImageContent
import coil.request.CachePolicy
import coil.request.ImageRequest

@Composable
fun PhotoCard(
    photo: Photo,
    onClick: () -> Unit = {}
) {

    val platformKey = photo.platform?.key?.lowercase()

    val borderColor = when (platformKey) {

        "facebook" -> Color(0xFF42A5F5)

        "instagram" -> Color(0xFFE91E63)

        else -> Color.LightGray
    }

    val platformIcon = when (platformKey) {

        "facebook" -> R.drawable.ic_facebook

        "instagram" -> R.drawable.ic_instagram

        else -> null
    }

    Card(
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .padding(8.dp)
            .border(
                width = 2.dp,
                color = borderColor,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
    ) {

        Box {

            SubcomposeAsyncImage(

                model = ImageRequest.Builder(
                    LocalContext.current
                )
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

                modifier = Modifier
                    .fillMaxWidth()
                    .height(160.dp)

            ) {

                when (painter.state) {

                    is AsyncImagePainter.State.Loading -> {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {

                            CircularProgressIndicator(
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    is AsyncImagePainter.State.Error -> {

                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {

                            Text(
                                text = "Error",
                                modifier = Modifier
                                    .align(Alignment.Center)
                            )
                        }
                    }

                    else -> {

                        SubcomposeAsyncImageContent()
                    }
                }
            }

            platformIcon?.let {

                Image(
                    painter = painterResource(id = it),
                    contentDescription = platformKey,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(28.dp)
                        .clip(CircleShape)
                )

            }

        }

    }

}