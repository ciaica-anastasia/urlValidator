package UrlValidator;

import org.jetbrains.annotations.NotNull;
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

    public boolean isValid(String url){
        UrlElement element = urlParser(urlNormalizer(url));
        return element.HostIsValid() && element.SchemeIsValid() && element.PortIsValid()
                && element.QueryIsValid() && element.PathIsValid() && element.FragmentIsValid();
    }

    private UrlElement urlParser(String url){
        return new UrlParser().parse(url);
    }

    private String urlNormalizer(String url){
        // TODO
        return url;
    }
}



class UrlElement{
    String host;
    String scheme;
    String port;
    String query;
    String path;
    String fragment;

    UrlElement(){
        // default
    }

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

    boolean SchemeIsValid(){
        //TODO
        return false;
    }
    boolean PortIsValid(){
        //TODO
        return false;
    }
    boolean QueryIsValid(){
        //TODO
        return false;
    }
    boolean PathIsValid(){
        //TODO
        return false;
    }
    boolean FragmentIsValid(){
        //TODO
        return false;
    }

}
