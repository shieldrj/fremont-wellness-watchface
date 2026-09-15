# Fremont Wellness Watch Face — Development & Deployment Rules

## MANDATORY: Auto-Install to the Watch on Every Edit

Whenever code, layout, or resources in this repo are edited or updated, run `.\deploy.ps1`. It
runs the tests, builds the debug APK, finds the watch, installs, and reports the version it read
back *off the watch*. Then follow the standard shipping workflow (branch, PR, squash merge, pull
main). Say plainly whether the build reached the watch: if the script could not reach it, it
installed nothing, and the change is not on Robert's wrist.

**Never install with a bare `adb install`, and never on the test "is a device connected".** That
was the rule here until 2026-09-14, and it was wrong in two ways.

- **Robert's phone is usually connected to adb as well.** `com.shieldrj.wellnesswatchface.debug`
  was found installed on the *phone*, where a watch face does nothing at all, while the watch
  went without the build. `deploy.ps1` reads `ro.build.characteristics` off the device and
  refuses to install on anything that is not a watch. **That check must stay.**
- **The watch has no stable address.** Its IP, its randomized Wi-Fi MAC (a different one per
  radio band) and its wireless-debugging port all change — the port whenever adbd restarts,
  which includes any Wi-Fi reconnect. The hardware serial `RFAX60DJKDZ` is the only fixed
  identifier, so the script rediscovers the watch on every run.

The adb *transport name* is not an identifier either. Over mDNS the watch is listed as
`adb-RFAX60DJKDZ-vuXso4._adb-tls-connect._tcp`; the same watch reached by
`adb connect <ip>:<port>` is listed as `192.168.1.128:35417`, with the serial nowhere in the
line. The script asks each attached device for `ro.serialno` rather than matching the name.

**Installing a watch face does not select it.** The script says so when the new build is not the
face currently on screen, because otherwise the install succeeds and Robert sees no change and
has no way to tell which happened.

**If the watch stops accepting the connection, the pairing was cleared.** Toggling wireless
debugging on the watch makes it forget this laptop. An open TCP port with a failed handshake is
the signature; a *timeout* instead means the watch is dozing or out of range, and a *refused*
port means a stale mDNS record. Only Robert can re-pair: *Settings → Developer options →
Wireless debugging → Pair new device* shows a 6-digit code that exists nowhere but the watch
screen, and then `adb pair <ip>:<port> <code>`.

## Slot 3 comes from another repo

The school period shown in slot 3 is published by `school-period-complication`, which shares
this watch and carries the same deploy script. Its bell schedule is a hand-copied subset and is
known to be stale — the "Bell Schedule" tab of the Wellness Center spreadsheet is the source of
truth. Do not fix a wrong period by editing this repo.

## Communication Style for Robert

- Robert runs a school wellness center. He is not a developer.
- Keep explanations plain, short, and in active voice.
- Before giving a command, say what it does to his machine and what it changes.
