package com.crome.forecastpoint.util

import java.util.Calendar
import java.util.Locale
import java.util.TimeZone
import kotlin.math.PI
import kotlin.math.acos
import kotlin.math.asin
import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.floor
import kotlin.math.sin
import kotlin.math.tan

/** Approximate sunrise/sunset local times (no external dependency). */
object SunCalculator {
    data class SunTimes(val sunrise: String, val sunset: String)

    fun times(
        latitude: Double,
        longitude: Double,
        timeZone: TimeZone = TimeZone.getDefault(),
        day: Calendar = Calendar.getInstance(timeZone),
    ): SunTimes {
        val year = day.get(Calendar.YEAR)
        val month = day.get(Calendar.MONTH) + 1
        val dayOfMonth = day.get(Calendar.DAY_OF_MONTH)

        val n = dayOfYear(year, month, dayOfMonth)
        val lngHour = longitude / 15.0

        fun calc(isRise: Boolean): Double {
            val t = n + ((if (isRise) 6.0 else 18.0) - lngHour) / 24.0
            val m = (0.9856 * t) - 3.289
            var l = m + (1.916 * sin(rad(m))) + (0.020 * sin(rad(2 * m))) + 282.634
            l = normalize360(l)
            var ra = deg(atan(0.91764 * tan(rad(l))))
            ra = normalize360(ra)
            val lQuad = floor(l / 90.0) * 90.0
            val raQuad = floor(ra / 90.0) * 90.0
            ra = ra + (lQuad - raQuad)
            ra /= 15.0
            val sinDec = 0.39782 * sin(rad(l))
            val cosDec = cos(asin(sinDec))
            val cosH = (cos(rad(90.833)) - (sinDec * sin(rad(latitude)))) /
                (cosDec * cos(rad(latitude)))
            if (cosH > 1) return Double.NaN // polar night
            if (cosH < -1) return Double.NaN // polar day
            var h = if (isRise) 360.0 - deg(acos(cosH)) else deg(acos(cosH))
            h /= 15.0
            val tLocal = h + ra - (0.06571 * t) - 6.622
            var ut = tLocal - lngHour
            ut = normalize24(ut)
            // Convert UT to local via timezone offset for this day
            val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply {
                set(Calendar.YEAR, year)
                set(Calendar.MONTH, month - 1)
                set(Calendar.DAY_OF_MONTH, dayOfMonth)
                set(Calendar.HOUR_OF_DAY, floor(ut).toInt())
                set(Calendar.MINUTE, ((ut - floor(ut)) * 60).toInt())
                set(Calendar.SECOND, 0)
            }
            val local = Calendar.getInstance(timeZone).apply { timeInMillis = cal.timeInMillis }
            return local.get(Calendar.HOUR_OF_DAY) + local.get(Calendar.MINUTE) / 60.0
        }

        val rise = calc(true)
        val set = calc(false)
        return SunTimes(
            sunrise = formatTime(rise),
            sunset = formatTime(set),
        )
    }

    private fun formatTime(hours: Double): String {
        if (hours.isNaN()) return "—"
        val h24 = normalize24(hours)
        val h = floor(h24).toInt()
        val m = ((h24 - h) * 60).toInt().coerceIn(0, 59)
        val ampm = if (h < 12) "AM" else "PM"
        val h12 = when {
            h == 0 -> 12
            h > 12 -> h - 12
            else -> h
        }
        return String.format(Locale.US, "%d:%02d %s", h12, m, ampm)
    }

    private fun dayOfYear(year: Int, month: Int, day: Int): Int {
        val n1 = floor(275.0 * month / 9.0).toInt()
        val n2 = floor((month + 9.0) / 12.0).toInt()
        val n3 = 1 + floor((year - 4.0 * floor(year / 4.0) + 2.0) / 3.0).toInt()
        return n1 - (n2 * n3) + day - 30
    }

    private fun rad(d: Double) = d * PI / 180.0
    private fun deg(r: Double) = r * 180.0 / PI
    private fun normalize360(v: Double): Double {
        var x = v % 360.0
        if (x < 0) x += 360.0
        return x
    }

    private fun normalize24(v: Double): Double {
        var x = v % 24.0
        if (x < 0) x += 24.0
        return x
    }
}
