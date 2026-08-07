package com.crome.forecastpoint.util

import android.util.Base64
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale

/**
 * Builds a [radar.weather.gov](https://radar.weather.gov) bookmark URL that opens the
 * "Weather for a location" view centered on the given coordinates.
 *
 * RIDGE2 stores map state as `?settings=v1_<base64(json)>` where center/location are
 * GeoJSON-style `[longitude, latitude]`.
 */
object RadarUrl {
    private const val BASE = "https://radar.weather.gov/"

    fun forCoordinates(latitude: Double, longitude: Double, zoom: Int = 8): String {
        val lon = roundCoord(longitude)
        val lat = roundCoord(latitude)
        val center = JSONArray().put(lon).put(lat)

        val agenda = JSONObject()
            .put("id", "weather")
            .put("center", center)
            .put("location", JSONArray().put(lon).put(lat))
            .put("zoom", zoom)

        val opacity = JSONObject()
            .put("contour", 0.5)
            .put("cross", 1)
            .put("single", 0.6)
            .put("rfc", 0.5)
            .put("sat", 0.5)
            .put("rtsa", 0.5)

        val settings = JSONObject()
            .put("agenda", agenda)
            .put("animating", false)
            .put("base", "standard")
            .put("artcc", false)
            .put("county", false)
            .put("cwa", false)
            .put("rfc", false)
            .put("state", false)
            .put("menu", true)
            .put("shortFusedOnly", false)
            .put("opacity", opacity)
            .put("layer", "bref_qcd")

        val json = settings.toString()
        val encoded = Base64.encodeToString(
            json.toByteArray(Charsets.UTF_8),
            Base64.NO_WRAP,
        )
        return BASE + "?settings=v1_" + encoded
    }

    fun generic(): String = BASE

    private fun roundCoord(value: Double): Double =
        String.format(Locale.US, "%.4f", value).toDouble()
}
