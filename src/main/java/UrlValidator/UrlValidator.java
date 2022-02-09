package UrlValidator;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
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
        // TODO
        return true;
    }

    private UrlElement urlParser(String url){
        // TODO
        UrlElement parsed = new UrlElement();
        return parsed;
    }

    private String urlNormalizer(String url){
        // TODO
        return url;
    }
}

class UrlElement{
    String scheme;
    String port;
    String query;
    String path;
    String fragment;

    UrlElement(){
        // default
    }
}
