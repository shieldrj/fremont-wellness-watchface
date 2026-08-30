package com.shieldrj.wellnesswatchface

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File
import javax.xml.parsers.DocumentBuilderFactory

class WatchFaceConfigTest {

    @Test
    fun testWatchFaceXmlIntegrity() {
        val watchFaceFile = File("src/main/res/raw/watchface.xml")
        assertTrue("watchface.xml should exist", watchFaceFile.exists())

        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(watchFaceFile)

        val root = doc.documentElement
        assertEquals("Root tag must be WatchFace", "WatchFace", root.tagName)
        assertEquals("Canvas width should be 450", "450", root.getAttribute("width"))
        assertEquals("Canvas height should be 450", "450", root.getAttribute("height"))
    }

    @Test
    fun testComplicationSlotsConfiguration() {
        val watchFaceFile = File("src/main/res/raw/watchface.xml")
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(watchFaceFile)

        val slots = doc.getElementsByTagName("ComplicationSlot")
        assertEquals("Must configure exactly 8 complication slots", 8, slots.length)

        val slotIds = mutableSetOf<Int>()
        for (i in 0 until slots.length) {
            val node = slots.item(i)
            val attrs = node.attributes
            val slotIdStr = attrs.getNamedItem("slotId")?.nodeValue
            assertNotNull("Slot ID must not be null", slotIdStr)
            val slotId = slotIdStr!!.toInt()
            assertTrue("Slot ID must be between 1 and 8", slotId in 1..8)
            assertTrue("Slot ID must be unique", slotIds.add(slotId))

            val x = attrs.getNamedItem("x")?.nodeValue?.toInt() ?: -1
            val y = attrs.getNamedItem("y")?.nodeValue?.toInt() ?: -1
            val width = attrs.getNamedItem("width")?.nodeValue?.toInt() ?: -1
            val height = attrs.getNamedItem("height")?.nodeValue?.toInt() ?: -1

            assertTrue("Slot $slotId x ($x) must be within dial (0..450)", x in 0..450)
            assertTrue("Slot $slotId y ($y) must be within dial (0..450)", y in 0..450)
            assertTrue("Slot $slotId (x+w) must not exceed canvas", (x + width) <= 450)
            assertTrue("Slot $slotId (y+h) must not exceed canvas", (y + height) <= 450)
        }
    }

    @Test
    fun testUserColorThemes() {
        val watchFaceFile = File("src/main/res/raw/watchface.xml")
        val factory = DocumentBuilderFactory.newInstance()
        val builder = factory.newDocumentBuilder()
        val doc = builder.parse(watchFaceFile)

        val colorOptions = doc.getElementsByTagName("ColorOption")
        assertTrue("Must have at least 3 color theme options", colorOptions.length >= 3)

        val displayNames = (0 until colorOptions.length).map {
            colorOptions.item(it).attributes.getNamedItem("displayName")?.nodeValue
        }

        assertTrue("Must include theme_fremont_green", displayNames.contains("theme_fremont_green"))
        assertTrue("Must include theme_tactical", displayNames.contains("theme_tactical"))
        assertTrue("Must include theme_midnight", displayNames.contains("theme_midnight"))
    }

    @Test
    fun testWatchFaceInfoAndShapesXml() {
        val infoFile = File("src/main/res/xml/watch_face_info.xml")
        assertTrue("watch_face_info.xml should exist", infoFile.exists())

        val previewFile = File("src/main/res/drawable/preview.png")
        assertTrue("preview.png should exist", previewFile.exists())
    }

    // --- Checks that mirror what the Watch Face Format runtime itself enforces. ---

    /**
     * The runtime only offers the Customize button when watch_face_info.xml opts in.
     * Editable defaults to false, and without it neither the colour theme nor any
     * complication slot can be changed on the watch.
     */
    @Test
    fun testWatchFaceIsMarkedEditable() {
        val info = File("src/main/res/xml/watch_face_info.xml").readText()
        assertTrue(
            "watch_face_info.xml must declare Editable value=\"true\" or the watch " +
                "hides the Customize button",
            Regex("""<Editable\s+value="true"""").containsMatchIn(info)
        )
    }

    /**
     * Complication data sources are plain strings, so a typo such as COMPLICATION.ICON
     * parses cleanly and then silently renders nothing.
     */
    @Test
    fun testComplicationExpressionsAreRecognised() {
        val valid = setOf(
            "TEXT", "TITLE",
            "MONOCHROMATIC_IMAGE", "MONOCHROMATIC_IMAGE_AMBIENT",
            "SMALL_IMAGE", "SMALL_IMAGE_AMBIENT", "IMAGE_STYLE", "PHOTO_IMAGE",
            "RANGED_VALUE_MIN", "RANGED_VALUE_MAX", "RANGED_VALUE_VALUE",
            "RANGED_VALUE_COLORS", "RANGED_VALUE_COLORS_INTERPOLATE",
            "GOAL_PROGRESS_VALUE", "GOAL_PROGRESS_TARGET_VALUE",
            "GOAL_PROGRESS_COLORS", "GOAL_PROGRESS_COLORS_INTERPOLATE",
            "WEIGHTED_ELEMENTS_COLORS", "WEIGHTED_ELEMENTS_WEIGHTS",
            "WEIGHTED_ELEMENTS_BACKGROUND_COLOR"
        )
        val used = Regex("""\[COMPLICATION\.([A-Z_]+)]""")
            .findAll(File("src/main/res/raw/watchface.xml").readText())
            .map { it.groupValues[1] }
            .toSet()
        assertTrue("Expected the watch face to use complication data", used.isNotEmpty())
        assertEquals("Unknown COMPLICATION.* data sources", emptySet<String>(), used - valid)
    }

    /**
     * The whole watch face is validated against the format version declared in the
     * manifest. Using a newer feature than that version fails validation outright.
     */
    @Test
    fun testDeclaredFormatVersionCoversFeaturesUsed() {
        val manifest = File("src/main/AndroidManifest.xml").readText()
        val declared = Regex(
            """com\.google\.wear\.watchface\.format\.version"\s*android:value="(\d+)""""
        ).find(manifest)?.groupValues?.get(1)?.toInt()
        assertNotNull("Manifest must declare com.google.wear.watchface.format.version", declared)

        val watchFace = File("src/main/res/raw/watchface.xml").readText()
        // GOAL_PROGRESS and WEIGHTED_ELEMENTS arrived in Watch Face Format version 2.
        val needsV2 = watchFace.contains("GOAL_PROGRESS") || watchFace.contains("WEIGHTED_ELEMENTS")
        if (needsV2) {
            assertTrue(
                "watchface.xml uses a version 2 feature but the manifest declares " +
                    "format version $declared",
                declared!! >= 2
            )
        }
    }

    /**
     * A Watch Face Format bundle must be resource-only. Any code dependency ends up in
     * classes.dex and contradicts android:hasCode="false" in the manifest.
     */
    @Test
    fun testBundleShipsNoCode() {
        assertTrue(
            "AndroidManifest.xml must set android:hasCode to false",
            File("src/main/AndroidManifest.xml").readText().contains("android:hasCode=\"false\"")
        )

        val shipping = File("build.gradle.kts").readLines()
            .map { it.trim() }
            .filter { it.startsWith("implementation(") || it.startsWith("api(") }
        assertEquals(
            "A watch face bundle must ship no code dependencies",
            emptyList<String>(),
            shipping
        )

        val mainSources = File("src/main/java")
        assertTrue(
            "src/main/java must stay empty - watch face bundles cannot contain code",
            !mainSources.exists() || mainSources.walkTopDown().none { it.isFile }
        )
    }
}
