# NOAA Weather & Tides icon sources

App package: `pandamonium.noaaweather` (Flutter)

## US locations (NWS MapClick) — the colorful icons you want
Source: `https://forecast.weather.gov/newimages/medium/{code}.png`
Also DualImage composites: `https://forecast.weather.gov/DualImage.php?i=...&j=...`

These match the classic NWS picture icons (sun on blue sky, orange windsock, moon/clouds at night).

## International (PirateWeather) — monochrome line icons
Source: `https://noaa-weather.firebaseapp.com/images/forecastio-{condition}.png`
Conditions: clear-day, clear-night, rain, snow, sleet, wind, fog, cloudy, partly-cloudy-day, partly-cloudy-night

## Known issue: weather.gov blocks some Android UAs
Akamai returns HTTP 403 for full Dalvik User-Agents like:
`Dalvik/2.1.0 (Linux; U; Android 14; Pixel 6 Build/...)`

Dart/Flutter HTTP clients work. Firebase icons always work.
