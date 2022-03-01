package urlValidator;

import org.junit.Test;

import java.util.Arrays;
import java.util.function.Predicate;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class TestHostname {

    @Test
    public void whitelistHostnames() { // ок!
        // принимаем все валидные(по стандартам) hostnames и наши личные
        Predicate<String> whitelistHostnames = host ->
                Arrays.asList("game.my.srv", "example.com").contains(host);
        UrlValidator validator = new Options().setNewHosts(whitelistHostnames).build();
        assertTrue(validator.isValid("http://game.my.srv:19100"));
    }

    @Test
    public void blacklistHostnames() { // не ок!
        // что происходит: принимаем все, которые не совпадают с теми, которые blacklist
        // хочу, чтобы если попали в blacklist (т.е. true) -> сразу возврат false,
        // если нет, то прошли все проверки по стандартам
        Predicate<String> blacklistHostnames = host ->
                !Arrays.asList("game.my.srv", "maryanna.com").contains(host);
        UrlValidator validator = new Options().setNewHosts(blacklistHostnames).build();
        assertFalse(validator.isValid("http://game.my.srv:19100"));
    }
}
