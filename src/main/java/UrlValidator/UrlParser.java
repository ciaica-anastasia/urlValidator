import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UrlParser {
    List<String> uses_params = Arrays.asList(" ", "ftp", "hdl", "prosper", "http", "imap",
            "https", "shttp", "rtsp", "rtspu", "sip", "sips",
            "mms", "sftp", "tel");

    // Characters valid in scheme names
    String scheme_chars = ("abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789+-.");

    //Unsafe bytes to be removed per WHATWG spec
    List<String> unsafeUrlBytesToRemove = Arrays.asList("\t", "\r", "\n");

    List<String> SplitNetcol(String url, int start) {
        int cut = url.length(); //position of end of domain part of url, default is end
        char[] delimeters = new char[]{'/', '?', '#'};
        for (char delimeter : delimeters) { // look for delimiters; the order is NOT important
            int firstCut = url.substring(start).indexOf(delimeter);
            if (firstCut >= 0) {
                cut = Math.min(cut, firstCut + start);
            }
        }
        return Arrays.asList(url.substring(start, cut), url.substring(cut));
    }

    List<String> UrlSplit(String url, String scheme) {
        for (String b : unsafeUrlBytesToRemove) {
            url = url.replace(b, "");
            scheme = scheme.replace(b, "");
        }
        String netloc = "", query = "", fragment = "";
        int i = url.indexOf(":");
        char[] charUrl = url.toCharArray();
        if (i > 0) {
            if (url.substring(0, i).equals("http")) {
                scheme = "http";
                url = url.substring(i + 1);
                if (url.substring(0, 2).equals("//")) {
                    netloc = SplitNetcol(url, 2).get(0);
                    url = SplitNetcol(url, 2).get(1);
                    //ТУТ ЕСТЬ ПРОВЕРКА На [, не знаю, нужна ли
                }
                if (url.contains("#")) {
                    int indexCut = url.indexOf("#");
                    String oldUrl = url;
                    url = url.substring(0, indexCut);
                    fragment = oldUrl.substring(indexCut + 1);
                }
                if (url.contains("?")) {
                    int indexCut = url.indexOf("?");
                    String oldUrl = url;
                    url = url.substring(0, indexCut);
                    query = oldUrl.substring(indexCut + 1);
                }
                // Тут может быть проверка netloc
                return Arrays.asList(scheme, netloc, url, query, fragment);
            }
            boolean flag = false;
            for (int j = 0; j < i; ++j) {
                char c = charUrl[j];
                if (!scheme_chars.contains(Character.toString(c))) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                // make sure "url" is not actually a port number (in which case
                // "scheme" is really part of the path)
                char[] rest = url.substring(i + 1).toCharArray();
                boolean flagAnother = false;
                for (char c : rest) {
                    String numbers = "0123456789";
                    if (!numbers.contains(Character.toString(c))) {
                        flagAnother = true;
                        break;
                    }
                }
                if (flagAnother) {
                    scheme = url.substring(0, i);
                    url = url.substring(i + 1);
                }
            }
        }
        if (url.substring(0, 2).equals("//")) {
            netloc = SplitNetcol(url, 2).get(0);
            url = SplitNetcol(url, 2).get(1);
            //ТУТ ЕСТЬ ПРОВЕРКА На [, не знаю, нужна ли
        }
        if (url.contains("#")) {
            int indexCut = url.indexOf("#");
            String oldUrl = url;
            url = url.substring(0, indexCut);
            fragment = oldUrl.substring(indexCut + 1);
        }
        if (url.contains("?")) {
            int indexCut = url.indexOf("?");
            String oldUrl = url;
            url = url.substring(0, indexCut);
            query = oldUrl.substring(indexCut + 1);
        }
        // Тут может быть проверка netloc
        return Arrays.asList(scheme, netloc, url, query, fragment);
    }

    List<String> HostInfo(String netloc) {
        String hostname = netloc, port = "";
        int index = netloc.indexOf("[");
        if (index != -1) {
            String haveOpenBr = netloc.substring(0, index);
            String bracketEnd = netloc.substring(index + 1);
            int indexEnd = bracketEnd.indexOf("]");
            hostname = bracketEnd.substring(0, indexEnd);
            port = bracketEnd.substring(indexEnd);
            int indexPort = port.indexOf(":");
            port = port.substring(indexPort + 1);
        } else {
            int indexPort = netloc.indexOf(":");
            if (indexPort != -1) {
                hostname = netloc.substring(0, indexPort);
                port = netloc.substring(indexPort + 1);
            }
        }
        return Arrays.asList(hostname, port);
    }

    UrlElement urlParser(String url, String scheme) {
        List<String> splitResult = UrlSplit(url, scheme);
        scheme = splitResult.get(0);
        String netloc = splitResult.get(1);
        url = splitResult.get(2);
        String query = splitResult.get(3);
        String fragment = splitResult.get(4);

        //Теперь надо отделить hostname и port
        List<String> parsedHost = HostInfo(netloc);
        String host = parsedHost.get(0);
        String port = parsedHost.get(1);
        return new UrlElement(host, scheme, port, query, url, fragment);
    }
}
