package com.example.qrcodegame.models;



import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Objects;

public class UserTest {

    private User user;

    @BeforeEach
    public void setUp() throws Exception {
        this.user = new User();
    }

    @AfterEach
    public void tearDown() throws Exception {

    }

    @Test
    public void userNameTest(){
        String name = user.getUsername();
        assertNull(name);

        user.setUsername("Madara Uchiha");
        assertEquals("Madara Uchiha", user.getUsername());
    }

    @Test
    public void testTotalScore() {
        int score = user.getTotalScore();
        assertEquals(0, score);

        user.setTotalScore(100);
        assertEquals(100, user.getTotalScore());


    }

    @Test
    public void testUserQRCodes(){
        ArrayList<String> codes = user.getCollectedCodes();
        assertEquals(0, codes.size());

        codes.add("AddQrcode1");
        codes.add("AddQrcode2");
        user.setCollectedCodes(codes);
        assertEquals(2, codes.size());
    }

    @Test
    public void userEmailTest(){
        String email = user.getEmail();
        assertNull(email);

        user.setEmail("mu@ualberta.ca");
        assertEquals("mu@ualberta.ca", user.getEmail());
    }

}
