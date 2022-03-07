package urlValidator;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestHostname {

    @Test
    public void whitelistHostnames() {
        Predicate<String> whitelistHostnames = host ->
                Arrays.asList("game.my.srv", "example.com").contains(host);
        UrlValidator validator = new Options().setNewWhosts(whitelistHostnames).build();
        assertTrue(validator.isValid("http://game.my.srv:19100"));
    }

    @Test
    public void blacklistHostnames() {
        Predicate<String> blacklistHostnames = host ->
                !Arrays.asList("game.my.srv", "maryanna.com").contains(host);
        UrlValidator validator = new Options().setNewBhosts(blacklistHostnames).build();
        assertFalse(validator.isValid("http://game.my.srv:19100"));
    }

    @Test
    public void consecutiveDotsNotAllowed() {
        UrlValidator validator = new Options().build();
        assertFalse(validator.isValid("http://.game..my.srv:19100"));
    }

    @Test
    public void invalidIPv4() {
        UrlValidator validator = new Options().build();
        // there are less than 1 or more than 4 labels
        assertFalse(validator.isValid("http://:19100"));
        assertFalse(validator.isValid("http://256.256.257.258.999:19100"));

        // octal IPv4
        // get rid of leading zeros in octal IPv4
        // label longer than 12
        assertFalse(validator.isValid("http://000300.0301.0302.0377777777777:19100"));
        // label bigger than 32 bits
        assertFalse(validator.isValid("http://0300.0301.0302.040000000000:19100"));
        // only numbers in the range 0-255 are allowed
        assertFalse(validator.isValid("http://0400.0401.0402.01747:19100"));

        // hexadecimal IPv4
        // get rid of leading zeros in hexadecimal IPv4
        // label longer than 8
        assertFalse(validator.isValid("http://000C0.0C1.0C2.0x7FFFFFFFF:19100"));
        // label bigger than 32 bits
        assertFalse(validator.isValid("http://C0.C1.C2.0X100000000:19100"));
        // only numbers in the range 0-255 are allowed
        assertFalse(validator.isValid("http://3C0.3C1.3C2.3E7:19100"));

        // decimal IPv4
        // get rid of leading zeros in octal IPv4
        // label longer than 12
        assertFalse(validator.isValid("http://000192.193.194.34359738367:19100"));
        // label bigger than 32 bits
        assertFalse(validator.isValid("http://192.193.194.4294967296:19100"));
        // only numbers in the range 0-255 are allowed
        assertFalse(validator.isValid("http://256.257.258.999:19100"));

        // x.x.x with 8.8.16 bits.
        assertFalse(validator.isValid("http://256.257.65536:19100"));
        // x.x with 8.24 bits.
        assertFalse(validator.isValid("http://256.16777216:19100"));

        // IPv4 addresses with special purpose?
        assertFalse(validator.isValid("http://127.0.0.1:19100"));
    }

    @Test
    public void validIPv4() {
        UrlValidator validator = new Options().build();
        // x.x.x with 8.8.16 bits.
        assertTrue(validator.isValid("http://120.144.43981:19100"));
        // x.x with 8.24 bits.
        assertTrue(validator.isValid("http://120.9481165:19100"));
        // x with 32 bits.
        assertTrue(validator.isValid("http://2022747085:19100"));
    }

    @Test
    public void IPv6() {
        UrlValidator validator = new Options().build();
        // IPv4 mapped addresses, IPv4 translated addresses, IPv4/IPv6 translation address space (6to4)
        assertFalse(validator.isValid("http://[::FFFF:172.16.17.18]:19100"));

        // compressed form
        assertTrue(validator.isValid("http://[2001:db8::1:0]:19100"));

        // label bigger than 0xffff
        assertFalse(validator.isValid("http://[10040:db8::1:0]:19100"));

        // IPv6 addresses with special purpose?
        assertFalse(validator.isValid("http://[0000:0000:0000:0000:0000:00000:0000:0001]:19100"));
    }

    @Test
    public void symbolicHostnames() {
        UrlValidator validator = new Options().build();
        // less than two labels
        // assertFalse(validator.isValid("http://.com:19100"));

        // the top-level domain name must not contain a hyphen
        // or digit unless it is an IDN
        // assertFalse(validator.isValid("http://example.c--o1m:19100"));

        // a tld label must be at least two characters long and may be as long as 63 characters
        //assertFalse(validator.isValid("http://example.c:19100"));
        //assertFalse(validator.isValid("http://example.coooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooom:19100"));

        // special purpose and reserved top-level domain?
        //assertFalse(validator.isValid("http://company.corp:19100"));
        assertFalse(validator.isValid("http://example.com:19100"));
    }
}
