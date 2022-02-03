package UrlValidator;
import org.jetbrains.annotations.NotNull;

/*
    Мы стремимся сделать максимально возможную кастомизацию. Идея следующая:
        Если enableAllPorts == true, то пропускаем абсолютно все порты
        Если enableAllPorts == false, то пропускаем только порты из массива ports
        По аналогии для остальных(logins, passwords...)
 */
public class UrlValidator {
    private final String[] schemes;
    private final  String[] logins;
    private final String[] passwords;
    private final String[] hosts;
    private final String[] ports;
    private final String[] paths;
    private final String[] queries;
    private final String[] fragments;

    private final boolean enableAllLogins;
    private final boolean enableAllPasswords;
    private final boolean enableAllHosts;
    private final boolean enableAllPorts;
    private final boolean enableAllPaths;
    private final boolean enableAllQueries;
    private final boolean enableAllFragments;

    public UrlValidator(){
        schemes = null;
        logins = null;
        passwords = null;
        hosts = null;
        ports = null;
        paths = null;
        queries = null;
        fragments = null;
        enableAllLogins = false;
        enableAllPasswords = false;
        enableAllHosts = false;
        enableAllPorts = false;
        enableAllPaths = false;
        enableAllQueries = false;
        enableAllFragments = false;
    }

    public UrlValidator(@NotNull Options userOptions){
        this.schemes = userOptions.schemes;
        this.logins = userOptions.logins;
        this.passwords = userOptions.passwords;
        this.hosts = userOptions.hosts;
        this.ports = userOptions.ports;
        this.paths = userOptions.paths;
        this.queries = userOptions.queries;
        this.fragments = userOptions.fragments;
        this.enableAllLogins = userOptions.enableAllLogins;
        this.enableAllPasswords = userOptions.enableAllPasswords;
        this.enableAllHosts = userOptions.enableAllHosts;
        this.enableAllPorts = userOptions.enableAllPorts;
        this.enableAllPaths = userOptions.enableAllPaths;
        this.enableAllQueries = userOptions.enableAllQueries;
        this.enableAllFragments = userOptions.enableAllFragments;
    }

    public static Options newOptions(){
        return new UrlValidator().new Options();
    }

    /*
        Весь этот цирк для того, чтобы после вызова build объект UrlValidator был immutable
        Мне не нравится, что очень много дублирования кода.
        Если есть способ сделать это изящнее, то предлагайте.
     */
    public class Options{
        // Патерн Builder

        private String[] schemes = null;
        private String[] logins = null;
        private String[] passwords = null;
        private String[] hosts = null;
        private String[] ports = null;
        private String[] paths = null;
        private String[] queries = null;
        private String[] fragments = null;

        private boolean enableAllLogins = true;
        private boolean enableAllPasswords = true;
        private boolean enableAllHosts = true;
        private boolean enableAllPorts = true;
        private boolean enableAllPaths = true;
        private boolean enableAllQueries = true;
        private boolean enableAllFragments = true;

        private Options(){
            // private constructor
        }

        public Options setNewSchemes(String[] schemes){
            this.schemes = schemes;
            return this;
        }

        public Options setNewLogins(String[] logins){
            this.logins = logins;
            return this;
        }

        public Options setNewPasswords(String[] passwords){
            this.passwords = passwords;
            return this;
        }

        public Options setNewHosts(String[] hosts){
            this.hosts = hosts;
            return this;
        }

        public Options setNewPorts(String[] ports){
            this.ports = ports;
            return this;
        }

        public Options setNewPaths(String[] paths){
            this.paths = paths;
            return this;
        }

        public Options setNewQueries(String[] queries){
            this.queries = queries;
            return this;
        }

        public Options setNewFragments(String[] fragments){
            this.fragments = fragments;
            return this;
        }

        public Options allowAllLogins(boolean decision){
            this.enableAllLogins = decision;
            return this;
        }

        public Options allowAllPasswords(boolean decision){
            this.enableAllPasswords = decision;
            return this;
        }

        public Options allowAllHosts(boolean decision){
            this.enableAllHosts = decision;
            return this;
        }

        public Options allowAllPorts(boolean decision){
            this.enableAllPorts = decision;
            return this;
        }

        public Options allowAllPaths(boolean decision){
            this.enableAllPaths = decision;
            return this;
        }

        public Options allowAllQueries(boolean decision){
            this.enableAllQueries = decision;
            return this;
        }

        public Options allowAllFragments(boolean decision){
            this.enableAllFragments = decision;
            return this;
        }

        public UrlValidator build(){
            return new UrlValidator(this);
        }

    }

    public boolean isValid(String url){
        // TODO
        return true;
    }
    private UrlElement[] urlParser(String url){
        // TODO
        UrlElement[] parsed = new UrlElement[3];
        return parsed;
    }
    private String urlNormalizer(String url){
        // TODO
        return url;
    }
}

interface UrlElement{
    String data = null;
}

/*

class Scheme implements UrlElement{

}

class Port implements UrlElement{

}

class Query implements UrlElement{

}

class Path implements UrlElement{

}

class Fragment implements UrlElement{

}

 */