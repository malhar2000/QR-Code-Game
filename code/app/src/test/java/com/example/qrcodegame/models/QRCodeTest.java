package com.example.qrcodegame.models;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Objects;

public class QRCodeTest {

    private QRCode qrCode;

    @BeforeEach
    public void setUp() throws Exception {
        qrCode = new QRCode();
    }

    @AfterEach
    public void tearDown() throws Exception {
    }

    @Test
    public void testGetImageUrl() {
        // Initially
        String url = qrCode.getImageUrl();
        assertTrue(Objects.isNull(url));
        // Set url to something else
        qrCode.setImageUrl("Hello");
        assertEquals("Hello", qrCode.getImageUrl());
    }

    @Test
    public void testGetWorth() {
        // Initially
        int worth = qrCode.getWorth();
        assertTrue(Objects.isNull(worth) || worth == 0);
        // Set url to something else
        qrCode.setWorth(1000);
        assertEquals(1000, qrCode.getWorth());
    }

    @Test
    public void testGetCoordinates() {
        // Initially
        assertEquals(qrCode.getCoordinates().size(), 0);
        // Set things up
        ArrayList<Double> coords = new ArrayList<>();
        coords.add(129.123);
        // Try inserting
        qrCode.setCoordinates(coords);
        assertEquals(qrCode.getCoordinates().size(), 0);
        // Update Coords
        coords.add(123.456);
        qrCode.setCoordinates(coords);
        assertEquals(qrCode.getCoordinates().size(), 2);
        assertTrue(129.123 == qrCode.getCoordinates().get(0));
        assertTrue(123.456 == qrCode.getCoordinates().get(1));
    }

    @Test
    public void testGetPlayers() {
    }
}