# Fremont Wellness Watch Face — Development & Deployment Rules

## MANDATORY: Auto-Install to Watch on Every Edit

Whenever code, layout, or resources in this repo are edited or updated:
1. Run local tests: .\gradlew.bat test
2. Build debug APK: .\gradlew.bat assembleDebug
3. If an ADB device is connected (db devices), automatically install the APK to Robert's Galaxy Watch 7:
   db install -r app\build\outputs\apk\debug\app-debug.apk
4. Follow the standard git shipping workflow (branch, PR, squash merge, pull main).

## Communication Style for Robert
- Robert runs a school wellness center. He is not a developer.
- Keep explanations plain, short, and in active voice.
