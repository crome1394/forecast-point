package com.crome.forecastpoint.ui.components

import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.crome.forecastpoint.util.IconMapper

/**
 * NWS weather icon loaded asynchronously via Coil (off main thread, memory-cached).
 * Bundled assets avoid weather.gov 403 issues on CalyxOS.
 */
@Composable
fun NwsIcon(
    iconCode: String?,
    modifier: Modifier = Modifier,
    size: Dp = 48.dp,
) {
    val context = LocalContext.current
    val code = remember(iconCode) { IconMapper.resolveCode(context, iconCode) }
    val px = (size.value * 2.5f).toInt().coerceIn(64, 192)
    val request = remember(code, px) {
        ImageRequest.Builder(context)
            .data("file:///android_asset/nws_icons/$code.png")
            .size(px)
            .crossfade(false)
            .build()
    }
    AsyncImage(
        model = request,
        contentDescription = code,
        contentScale = ContentScale.Fit,
        modifier = modifier.size(size),
    )
}
