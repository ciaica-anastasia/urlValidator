package UrlValidator;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.HashSet;
import java.util.Set;
import java.util.function.Predicate;

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

    public UrlValidator(@NotNull Options userOptions){
        this.schemes = userOptions.schemes;
        this.logins = userOptions.logins;
        this.passwords = userOptions.passwords;
        this.hosts = userOptions.hosts;
        this.ports = userOptions.ports;
        this.paths = userOptions.paths;
        this.queries = userOptions.queries;
        this.fragments = userOptions.fragments;
    }

    public boolean isValid(@NotNull String url){
        UrlElement element = urlParser(urlNormalizer(url));
        return element.HostIsValid() && element.SchemeIsValid() && element.PortIsValid()
                && element.QueryIsValid() && element.PathIsValid() && element.FragmentIsValid();
    }

    private UrlElement urlParser(@NotNull String url){
        return new UrlParser().Parse(url);
    }

    private String urlNormalizer(@NotNull String url){
        // TODO
        return url;
    }
}


@NoArgsConstructor
class UrlElement{
    String host;
    String scheme;
    String port;
    String query;
    String path;
    String fragment;

    private static final String subDelimits = "!$&'()*+,;=";
    private static final String notReserved = "-._~";
    private static final long minPort = 0;
    private static final long maxPort = 65535;

    public UrlElement(String host, String scheme, String port, String query, String url, String fragment) {
        this.host = host;
        this.scheme = scheme;
        this.port = port;
        this.query = query;
        this.path = url;
        this.fragment = fragment;
    }

    // Можете написать, как вам будет удобнее, это сделала, чтобы вызывать в IsValid
    boolean HostIsValid(){
        //TODO
        return false;
    }

    private boolean BasicCheck(@NotNull String paramForCheck, @NotNull String allowedSymbols){
        Set<Character> symbolsSet = new HashSet<>();
        for(Character curChar : allowedSymbols.toCharArray()){
            symbolsSet.add(curChar);
        }
        if( paramForCheck.length() == 0 ){
            return false;
        }
        for(Character curChar : paramForCheck.toCharArray()){
            if( !(Character.isAlphabetic(curChar) || symbolsSet.contains(curChar)) ){
                return false;
            }
        }
        return true;
    }

    // https://habr.com/ru/post/232385/
    boolean SchemeIsValid(){
        return BasicCheck(scheme, "+-.") && Character.isLetter(scheme.charAt(0));
    }

    boolean PathIsValid(){
        return BasicCheck(path, ":@%" + subDelimits + notReserved);
    }

    boolean QueryIsValid(){
        return BasicCheck(query, ":@/?%" + subDelimits + notReserved);
    }

    boolean FragmentIsValid(){
        return true;
    }

    boolean PortIsValid(){
        if( (port == null) || (port.length() == 0) ){
            return false;
        }
        for(Character curChar: port.toCharArray()){
            if( !Character.isDigit(curChar) ){
                return false;
            }
        }
        long portID = Long.parseLong(port);
        return (portID >= minPort) && (portID <= maxPort);
    }

}
