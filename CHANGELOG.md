# Changelog

All notable changes to **Forecast Point** are documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.1.0/),
and this project aims to follow [Semantic Versioning](https://semver.org/).

## [1.0.0] — 2026-08-06

Initial public release under the name **Forecast Point**
(`com.crome.forecastpoint`).

### Added

- NWS point forecast and current conditions UI (dark theme)
- Expandable current conditions; hazards/alerts when active
- Multi-day forecast cards and hourly tables (temperature color scale, precip, wind, tides, conditions)
- Horizontal swipe between hourly tabs
- Home-screen widget with NWS picture icons, resizable layout, period temp or high/low mode
- City search (Nominatim) with proper place naming
- Map location picker (osmdroid, light basemap), search FAB, pin + confirm chip
- GPS center on map open (with permission)
- Radar deep-link centered on active location
- Favorites: add, rename, remove
- Settings: auto-update, intervals, title bar position, widget high/low, map search position
- System back gesture returns from nested screens to main forecast
- Bundled NWS forecast icons for CalyxOS / reliable rendering

### Credits

- Weather data: U.S. National Weather Service / NOAA  
- Map & geocoding: OpenStreetMap, Nominatim, CARTO, osmdroid  
- UX inspiration: commercial “NOAA Weather & Tides” (Pandamonium Software)—independent reimplementation  

### Notes

- Renamed from internal working title “NOAA Forecast” to **Forecast Point** to
  distinguish this community project from official NOAA products and the
  commercial Play Store app.
