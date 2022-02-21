package UrlValidator;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

// @NotNull надо либо использовать повсеместно, либо не использовать совсем
public final class UrlValidator {
    private final Predicate<String> schemes;
    private final Predicate<String> logins;
    private final Predicate<String> passwords;
    private final Predicate<String> hosts;
    private final Predicate<String> ports;
    private final Predicate<String> paths;
    private final Predicate<String> queries;
    private final Predicate<String> fragments;

    public UrlValidator(@NotNull Options userOptions) {
        this.schemes = userOptions.schemes;
        this.logins = userOptions.logins;
        this.passwords = userOptions.passwords;
        this.hosts = userOptions.hosts;
        this.ports = userOptions.ports;
        this.paths = userOptions.paths;
        this.queries = userOptions.queries;
        this.fragments = userOptions.fragments;
    }

    public boolean isValid(@NotNull String url) {
        UrlElement element = urlParser(urlNormalizer(url));
        return element.hostIsValid() && element.schemeIsValid() && element.portIsValid()
                && element.queryIsValid() && element.pathIsValid() && element.fragmentIsValid();
    }

    private @NotNull UrlElement urlParser(@NotNull String url) {
        return new UrlParser().parse(url);
    }

    private String urlNormalizer(@NotNull String url) {
        // TODO
        return url;
    }
}


@NoArgsConstructor
final class UrlElement {
    String host;
    String scheme;
    String port;
    String query;
    String path;
    String fragment;

    private static final String SUB_DELIMITS = "!$&'()*+,;=";
    private static final String NOT_RESERVED = "-._~";
    private static final long MIN_PORT = 0;
    private static final long MAX_PORT = 65535;

    public UrlElement(String host, String scheme, String port, String query, String url, String fragment) {
        this.host = host;
        this.scheme = scheme;
        this.port = port;
        this.query = query;
        this.path = url;
        this.fragment = fragment;
    }

    // Можете написать, как вам будет удобнее, это сделала, чтобы вызывать в isValid
    boolean hostIsValid() {
        //TODO
        return true;
    }

    private boolean basicCheck(@NotNull String paramForCheck, @NotNull String allowedSymbols) {
        if (paramForCheck.isEmpty()) {
            return false;
        }
        Set<Character> symbols = allowedSymbols.chars()
                .mapToObj(e -> (char) e).collect(Collectors.toSet());
        for (Character curChar : paramForCheck.toCharArray()) {
            if (!(Character.isAlphabetic(curChar) || symbols.contains(curChar))) {
                return false;
            }
        }
        return true;
    }

    // https://habr.com/ru/post/232385/
    boolean schemeIsValid() {
        return basicCheck(scheme, "+-.") && Character.isLetter(scheme.charAt(0));
    }

    boolean pathIsValid() {
        return basicCheck(path, ":@%" + SUB_DELIMITS + NOT_RESERVED);
    }

    boolean queryIsValid() {
        return basicCheck(query, ":@/?%" + SUB_DELIMITS + NOT_RESERVED);
    }

    boolean fragmentIsValid() {
        return true;
    }

    boolean portIsValid() {
        if ((port == null) || (port.isEmpty())) {
            return false;
        }
        for (Character curChar : port.toCharArray()) {
            if (!Character.isDigit(curChar)) {
                return false;
            }
        }
        final long portId = Long.parseLong(port);
        return (portId >= MIN_PORT) && (portId <= MAX_PORT);
    }

}
