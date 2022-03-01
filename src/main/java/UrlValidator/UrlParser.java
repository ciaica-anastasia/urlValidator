package UrlValidator;

import java.util.List;
import java.util.Arrays;

import org.jetbrains.annotations.NotNull;

public final class UrlParser {

    //Unsafe bytes to be removed per WHATWG spec
    private final List<String> unsafeUrlBytesToRemove = Arrays.asList("\t", "\r", "\n");

    @NotNull HostPort splitNetcol(@NotNull String url) {
        int start = 2;
        int cut = url.length(); //position of end of domain part of url, default is end
        char[] delimiters = {'/', '?', '#'};
        for (char delimiter : delimiters) { // look for delimiters; the order is NOT important
            int firstCut = url.substring(start).indexOf(delimiter);
            if (firstCut >= 0) {
                cut = Math.min(cut, firstCut + start);
            }
        }
        String a = url.substring(start, cut);
        String b = url.substring(cut);
        return new HostPort(url.substring(start, cut), url.substring(cut));
    }

    static class UrlSplitted {
        private final
        String scheme;
        String netloc;
        String url;
        String query;
        String fragment;

        public UrlSplitted(String newScheme, String newNetloc, String newUrl, String newQuery, String newFragment) {
            scheme = newScheme;
            netloc = newNetloc;
            url = newUrl;
            query = newQuery;
            fragment = newFragment;
        }
    }

    static class HostPort {
        String hostname;
        String port;

        HostPort(String newHostname, String newPort) {
            hostname = newHostname;
            port = newPort;
        }
    }

    private UrlSplitted urlSplit(@NotNull String url) {
        String newUrl = url;
        String newScheme = "";
        for (String b : unsafeUrlBytesToRemove) {
            newUrl = url.replace(b, "");
            newScheme = newScheme.replace(b, "");
        }
        String netloc = "", query = "", fragment = "";
        int i = newUrl.indexOf(":");
        char[] charUrl = newUrl.toCharArray();
        if (i > 0) {
            if (newUrl.substring(0, i).equals("http")) {
                newScheme = "http";
                newUrl = newUrl.substring(i + 1);
                return getUrlSplitted(newUrl, newScheme, netloc, query, fragment);
            }
            boolean flag = false;
            for (int j = 0; j < i; ++j) {
                char c = charUrl[j];
                // Characters valid in scheme names
                String schemeChars = ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+-.");
                if (!schemeChars.contains(Character.toString(c))) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                // make sure "url" is not actually a port number (in which case
                // "scheme" is really part of the path)
                char[] rest = newUrl.substring(i + 1).toCharArray();
                boolean flagAnother = false;
                for (char c : rest) {
                    String numbers = "0123456789";
                    if (!numbers.contains(Character.toString(c))) {
                        flagAnother = true;
                        break;
                    }
                }
                if (flagAnother) {
                    newScheme = newUrl.substring(0, i);
                    newUrl = newUrl.substring(i + 1);
                }
            }
        }
        return getUrlSplitted(newUrl, newScheme, netloc, query, fragment);
    }

    private @NotNull UrlParser.UrlSplitted getUrlSplitted(@NotNull String url, @NotNull String newScheme, String netloc, String query, String fragment) {
        String newUrl = url;
        if (newUrl.startsWith("//")) {
            netloc = splitNetcol(newUrl).hostname;
            newUrl = splitNetcol(newUrl).port;
        }
        if (newUrl.contains("#")) {
            int indexCut = newUrl.indexOf('#');
            String oldUrl = newUrl;
            newUrl = newUrl.substring(0, indexCut);
            fragment = oldUrl.substring(indexCut + 1);
        }
        if (newUrl.contains("?")) {
            int indexCut = newUrl.indexOf('?');
            String oldUrl = newUrl;
            newUrl = newUrl.substring(0, indexCut);
            query = oldUrl.substring(indexCut + 1);
        }
        return new UrlSplitted(newScheme, netloc, newUrl, query, fragment);
    }

    @NotNull HostPort hostInfo(@NotNull String netloc) {
        String hostname = netloc, port = "";
        int index = netloc.indexOf('[');
        if (index != -1) {
            String bracketEnd = netloc.substring(index + 1);
            int indexEnd = bracketEnd.indexOf(']');
            hostname = bracketEnd.substring(0, indexEnd);
            port = bracketEnd.substring(indexEnd);
            int indexPort = port.indexOf(':');
            port = port.substring(indexPort + 1);
        } else {
            int indexPort = netloc.indexOf(':');
            if (indexPort != -1) {
                hostname = netloc.substring(0, indexPort);
                port = netloc.substring(indexPort + 1);
            }
        }
        return new HostPort(hostname, port);
    }

    public @NotNull UrlElement parse(@NotNull String urlAddress) {
        UrlSplitted splitResult = urlSplit(urlAddress);

        //Теперь надо отделить hostname и port
        HostPort parsedHost = hostInfo(splitResult.netloc);
        return new UrlElement(parsedHost.hostname, splitResult.scheme, parsedHost.port, splitResult.query, splitResult.url, splitResult.fragment);
    }
}
