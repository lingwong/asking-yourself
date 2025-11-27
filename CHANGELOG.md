# Changelog

# Changelog

## v0.3.1

### Fixes
- Adaptive icon foreground scaled to 72% to avoid mask cropping
- CI workflow syntax corrected; push and tag triggers fixed

### Technical
- Version bump: `versionCode=31`, `versionName=0.3.1`

## v0.3.0 (Initial release)

### Features
- Daily review and reflection flows with multiple screens
- Flash questions and custom questions management
- OCR (ML Kit Chinese) for text recognition
- Reminders and notifications with boot recovery
- PDF export and share via FileProvider
- Statistics and charts, history and favorites
- Local persistence using Room (DAOs and entities)

### Permissions
- POST_NOTIFICATIONS
- RECORD_AUDIO
- RECEIVE_BOOT_COMPLETED

### Technical
- Kotlin + Jetpack Compose, Compose BOM 2024.10.00
- Target SDK 34, Min SDK 24
- Kotlin compiler extension 1.5.14, JVM target 17
