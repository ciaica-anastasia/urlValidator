package UrlValidator;

import org.junit.Test;

import static org.junit.Assert.*;

public class UrlElementTest {

    @Test
    public void schemeIsValid() {
        UrlElement tmp = new UrlElement();

        tmp.scheme = null;
        assertTrue(tmp.schemeIsValid());

        tmp.scheme = "";
        assertTrue(tmp.schemeIsValid());

        tmp.scheme = "v";
        assertTrue(tmp.schemeIsValid());

        tmp.scheme = "V+-.";
        assertTrue(tmp.schemeIsValid());

        tmp.scheme = "V123+456.aba-caba";
        assertTrue(tmp.schemeIsValid());

        tmp.scheme = "1V23456";
        assertFalse(tmp.schemeIsValid());

        tmp.scheme = "+V123456";
        assertFalse(tmp.schemeIsValid());

        tmp.scheme = "+12.3-";
        assertFalse(tmp.schemeIsValid());
    }

    @Test
    public void pathIsValid() {
        UrlElement tmp = new UrlElement();

        tmp.path = null;
        assertTrue(tmp.pathIsValid());

        tmp.path = "";
        assertTrue(tmp.pathIsValid());

        tmp.path = "!$&'()*+,;=";
        assertTrue(tmp.pathIsValid());

        tmp.path = "-._~";
        assertTrue(tmp.pathIsValid());

        tmp.path = ":@%";
        assertTrue(tmp.pathIsValid());

        tmp.path = "H!$&'()*+,;=el-._lo1@%-._!$&'()*+,;=";
        assertTrue(tmp.pathIsValid());

        tmp.path = "1234";
        assertTrue(tmp.pathIsValid());

        tmp.path = "1234#";
        assertFalse(tmp.pathIsValid());

        tmp.path = "#Hi";
        assertFalse(tmp.pathIsValid());

        tmp.path = "Hi?";
        assertFalse(tmp.pathIsValid());

        tmp.path = "?Hi";
        assertFalse(tmp.pathIsValid());
    }

    @Test
    public void queryIsValid() {
        UrlElement tmp = new UrlElement();

        tmp.query = null;
        assertTrue(tmp.queryIsValid());

        tmp.query = "";
        assertTrue(tmp.queryIsValid());

        tmp.query = "!$&'()*+,;=";
        assertTrue(tmp.queryIsValid());

        tmp.query = "-._~";
        assertTrue(tmp.queryIsValid());

        tmp.query = ":@%/?";
        assertTrue(tmp.queryIsValid());

        tmp.query = "H?/:@!$&'()*+,;=el-._lo1@%-._?";
        assertTrue(tmp.queryIsValid());

        tmp.query = "1234";
        assertTrue(tmp.queryIsValid());

        tmp.query = "1234#";
        assertFalse(tmp.queryIsValid());

        tmp.query = "#Hi";
        assertFalse(tmp.queryIsValid());
    }

    @Test
    public void fragmentIsValid() {
        UrlElement tmp = new UrlElement();

        tmp.fragment = null;
        assertTrue(tmp.fragmentIsValid());

        tmp.fragment = "";
        assertTrue(tmp.fragmentIsValid());
    }

    @Test
    public void portIsValid() {
        UrlElement tmp = new UrlElement();

        tmp.port = null;
        assertTrue(tmp.portIsValid());

        tmp.port = "";
        assertTrue(tmp.portIsValid());

        tmp.port = "0";
        assertTrue(tmp.portIsValid());

        tmp.port = "65535";
        assertTrue(tmp.portIsValid());

        tmp.port = "31415";
        assertTrue(tmp.portIsValid());

        tmp.port = "27182";
        assertTrue(tmp.portIsValid());

        tmp.port = "65536";
        assertFalse(tmp.portIsValid());

        tmp.port = "65537";
        assertFalse(tmp.portIsValid());

        tmp.port = "65538";
        assertFalse(tmp.portIsValid());

        tmp.port = "-1";
        assertFalse(tmp.portIsValid());

        tmp.port = "one";
        assertFalse(tmp.portIsValid());

        tmp.port = "five";
        assertFalse(tmp.portIsValid());

        tmp.port = "10 thousands";
        assertFalse(tmp.portIsValid());

        tmp.port = "2+3";
        assertFalse(tmp.portIsValid());

        tmp.port = "#2022";
        assertFalse(tmp.portIsValid());

        tmp.port = "!2022";
        assertFalse(tmp.portIsValid());

        tmp.port = "@2022";
        assertFalse(tmp.portIsValid());

        tmp.port = "$2022";
        assertFalse(tmp.portIsValid());

        tmp.port = "%2022";
        assertFalse(tmp.portIsValid());

        tmp.port = "^&*()2022";
        assertFalse(tmp.portIsValid());
    }
}