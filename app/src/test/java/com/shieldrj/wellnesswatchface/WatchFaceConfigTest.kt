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
        assertEquals("Clip shape should be CIRCLE", "CIRCLE", root.getAttribute("clipShape"))
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

        val optionIds = (0 until colorOptions.length).map {
            colorOptions.item(it).attributes.getNamedItem("id")?.nodeValue
        }

        assertTrue("Must include fremont_green theme", optionIds.contains("fremont_green"))
        assertTrue("Must include tactical_slate theme", optionIds.contains("tactical_slate"))
        assertTrue("Must include midnight_teal theme", optionIds.contains("midnight_teal"))
    }
}

