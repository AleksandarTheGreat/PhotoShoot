package com.finki.courses.Activities;

import junit.framework.TestCase;

import org.junit.Test;

public class MainActivityTest extends TestCase {

    @Test
    public void assertTest(){
        int value = 25;
        assertTrue("Value should be less than 50", value < 50);
    }

}