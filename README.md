# Fremont Wellness Watch Face (Galaxy Watch 7 / Wear OS 5)

Custom high-density watch face designed for Robert's workday at the Fremont Academy Wellness Center. Built using Google & Samsung's official **Watch Face Format (WFF)** standard for maximum battery efficiency, OLED burn-in protection, and full complication customization.

---

## Features

- **8 Multi-Type Complication Slots**: Curated layout for school schedules, biometrics, campus walk tracking, outdoor weather, calendar, and quick wellness actions.
- **Dedicated School Bell Period Pod (Slot 1)**: Seamlessly integrates with the companion `school-period-complication` app to display active periods (`P1`, `P2`, `Lunch`, etc.) and countdown timers.
- **Fremont Academy Crest & Colors**: Forest Green (`#0E4D2D`), Aztec Gold (`#FFC72C`), Warm Cream (`#FFF8E7`), Tactical Slate, and Midnight Teal palettes.
- **OLED Always-On Display (AOD)**: Optimized low-power mode with <5% pixel ratio to preserve Galaxy Watch 7 battery throughout long workdays.
- **Full Wear OS 5 Compliance**: Uses declarative Watch Face Format XML without background runtime battery drain.

---

## Complication Slot Layout

```
                       [ SLOT 1: Bell Schedule & Countdown ]
                               (School Period App)
               [ SLOT 7: Weather ]              [ SLOT 2: Heart Rate / Stress ]
           (Temp & Conditions)                      (Wellness Metric)
                        \                                /
   [ SLOT 6: Quick Action ]       [ FREMONT LOGO ]       [ SLOT 3: Next Event ]
  (Wellness Form / Water)     [ 10:42 AM | MON AUG 30 ]     (Calendar / Meeting)
                        /                                \
           [ SLOT 5: Steps / Active ]        [ SLOT 4: Watch Battery ]
              (Campus Activity)                   (Percentage & Ring)
                       [ SLOT 8: Sub-Dial / Notification Dot ]
```

| Slot ID | Position | Recommended Provider | Supported Types |
| :--- | :--- | :--- | :--- |
| **Slot 1** | Top Center (Curved Pod) | School Period Complication (`com.shieldrj.schoolperiod`) | `SHORT_TEXT`, `RANGED_VALUE`, `LONG_TEXT` |
| **Slot 2** | Top Right | Samsung Health Heart Rate / Stress | `SHORT_TEXT`, `RANGED_VALUE` |
| **Slot 3** | Center Right | Google / Samsung Calendar (Next Event) | `SHORT_TEXT` |
| **Slot 4** | Bottom Right | Watch Battery Level | `SHORT_TEXT`, `RANGED_VALUE` |
| **Slot 5** | Bottom Left | Samsung Health Daily Steps / Active Time | `SHORT_TEXT`, `GOAL_PROGRESS` |
| **Slot 6** | Center Left | Quick Shortcut (Sign-In Form, Voice, Timer) | `SHORT_TEXT`, `SMALL_IMAGE` |
| **Slot 7** | Top Left | Weather & UV Index | `SHORT_TEXT`, `RANGED_VALUE` |
| **Slot 8** | Bottom Center | Unread Notification Dot / Hydration Tracker | `SHORT_TEXT`, `SMALL_IMAGE` |

---

## How to Set Up Auto-Switch at Work (Modes & Routines)

To have your Galaxy Watch 7 automatically switch to this watch face when you arrive at school:

1. On your Galaxy Phone, open **Settings** > **Modes and Routines**.
2. Tap **Modes** > **Work** (or create a custom "Fremont School" routine under the **Routines** tab).
3. Under **Turn on automatically**, add a condition:
   - **Place (Location)**: Select Fremont Academy / Wellness Center on the map (e.g. 100m radius).
   - *Optional additional condition*: **Time period** (Mon–Fri 7:30 AM – 4:00 PM) or **Wi-Fi network** (School Wi-Fi).
4. Under **Other actions** (or **Watch Face** section):
   - Tap **Change watch face**.
   - Select **Fremont Wellness Watch Face**.
5. Save the mode/routine.

Now, whenever you arrive at work, your Galaxy Watch 7 will automatically switch to your Fremont Wellness Watch Face and switch back when you leave!

---

## Building and Installing to Galaxy Watch 7

### Prerequisites
- Android SDK installed (`minSdk 33`, `compileSdk 35`)
- Java 21+
- Galaxy Watch 7 with **Developer Options** and **Wireless Debugging** enabled.

### Run Tests Locally
```powershell
.\gradlew.bat test
```

### Build Debug APK
```powershell
.\gradlew.bat assembleDebug
```

### Install onto Galaxy Watch 7 via ADB
1. Connect to your watch over Wi-Fi:
   ```powershell
   adb connect <WATCH_IP>:<DEBUG_PORT>
   ```
2. Install the watch face APK:
   ```powershell
   adb install -r app\build\outputs\apk\debug\app-debug.apk
   ```
3. Long-press on your watch screen, tap **Add watch face**, and select **Fremont Wellness Watch Face**.

