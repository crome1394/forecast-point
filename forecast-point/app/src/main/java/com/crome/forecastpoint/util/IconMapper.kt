package com.crome.forecastpoint.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import java.io.IOException
import java.util.Locale
import java.util.concurrent.ConcurrentHashMap

/**
 * Maps NWS icon URLs / codes onto bundled PNGs under assets/nws_icons/.
 * Bundling avoids weather.gov Akamai 403s that break stock Android UAs on Calyx.
 */
object IconMapper {
    private const val MAX_CACHE = 48

    private val cache = object : LinkedHashMap<String, Bitmap>(32, 0.75f, true) {
        override fun removeEldestEntry(eldest: MutableMap.MutableEntry<String, Bitmap>?): Boolean {
            if (size <= MAX_CACHE) return false
            eldest?.value?.recycle()
            return true
        }
    }
    private val cacheLock = Any()
    private val available = ConcurrentHashMap<String, Boolean>()

    /** Extract a base icon code from a MapClick iconLink or Weatherimage value. */
    fun codeFrom(raw: String?): String {
        if (raw.isNullOrBlank()) return "skc"
        var s = raw.trim()

        if (s.contains("DualImage.php", ignoreCase = true)) {
            val iParam = Regex("[?&]i=([^&]+)").find(s)?.groupValues?.getOrNull(1)
            if (!iParam.isNullOrBlank()) s = iParam
        }

        if (s.contains('/')) {
            s = s.substringAfterLast('/')
        }
        s = s.substringBefore('?')
        s = s.removeSuffix(".png").removeSuffix(".PNG")
        s = s.replace(Regex("\\d+$"), "")

        s = when (s.lowercase(Locale.US)) {
            "nfg", "fg" -> "nbkn"
            "smoke" -> "fu"
            "nsmoke" -> "nfu"
            "haze" -> "fu"
            "rain" -> "ra"
            "nrain" -> "nra"
            "snow" -> "sn"
            "nsnow" -> "sn"
            "tstorms", "ntstorms" -> "tsra"
            else -> s.lowercase(Locale.US)
        }

        return s.ifBlank { "skc" }
    }

    fun hasAsset(context: Context, code: String): Boolean {
        val key = code.lowercase(Locale.US)
        available[key]?.let { return it }
        val ok = try {
            context.assets.open("nws_icons/$key.png").close()
            true
        } catch (_: IOException) {
            false
        }
        available[key] = ok
        return ok
    }

    fun resolveCode(context: Context, raw: String?): String {
        val primary = codeFrom(raw)
        if (hasAsset(context, primary)) return primary

        val candidates = buildList {
            add(primary)
            if (primary.startsWith("n") && primary.length > 1) add(primary.drop(1))
            if (primary.contains("tsra")) add(if (primary.startsWith("n")) "ntsra" else "tsra")
            if (primary.contains("shra")) add(if (primary.startsWith("n")) "nshra" else "shra")
            if (primary.contains("ra")) add(if (primary.startsWith("n")) "nra" else "ra")
            add("few")
            add("skc")
        }
        return candidates.firstOrNull { hasAsset(context, it) } ?: "skc"
    }

    /**
     * For widgets / RemoteViews only. UI should use Coil [AsyncImage] via [NwsIcon].
     * Uses RGB_565 for smaller memory when possible and an LRU of decoded bitmaps.
     */
    fun loadBitmap(context: Context, raw: String?, maxPx: Int = 96): Bitmap? {
        val code = resolveCode(context, raw)
        val cacheKey = "$code@$maxPx"
        synchronized(cacheLock) {
            cache[cacheKey]?.let { return it }
        }

        return try {
            context.assets.open("nws_icons/$code.png").use { stream ->
                val opts = BitmapFactory.Options().apply {
                    inPreferredConfig = Bitmap.Config.RGB_565
                    inSampleSize = 1
                }
                val decoded = BitmapFactory.decodeStream(stream, null, opts) ?: return null
                val scaled = if (decoded.width > maxPx || decoded.height > maxPx) {
                    val scale = maxPx.toFloat() / maxOf(decoded.width, decoded.height)
                    Bitmap.createScaledBitmap(
                        decoded,
                        (decoded.width * scale).toInt().coerceAtLeast(1),
                        (decoded.height * scale).toInt().coerceAtLeast(1),
                        true,
                    ).also { if (it !== decoded) decoded.recycle() }
                } else {
                    decoded
                }
                synchronized(cacheLock) {
                    cache[cacheKey] = scaled
                }
                scaled
            }
        } catch (_: Exception) {
            null
        }
    }

    fun clearCache() {
        synchronized(cacheLock) {
            cache.values.forEach { it.recycle() }
            cache.clear()
        }
    }
}
