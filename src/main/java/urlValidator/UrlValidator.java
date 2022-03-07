package urlValidator;

import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import java.util.*;

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
        UrlElement element = urlParser(url);
        return element.hostnameIsValid(this.hosts) && element.schemeIsValid() && element.portIsValid()
                && element.queryIsValid() && element.pathIsValid() && element.fragmentIsValid();
    }

    private @NotNull UrlElement urlParser(@NotNull String url) {
        return new UrlParser().parse(url);
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
    private static final String IPV6_REGEX = "(^[0-9a-fA-F:]+$)(.*1{2,}.*)";
    private static final List<String> specialPurposeTlds = Arrays.asList(
            "example", "test", "localhost", "invalid",
            "local", "onion", "home", "corp", "arpa", "int");

    public UrlElement(String host, String scheme, String port, String query, String url, String fragment) {
        this.host = host;
        this.scheme = scheme;
        this.port = port;
        this.query = query;
        this.path = url;
        this.fragment = fragment;
    }

    boolean hostnameIsValid(@NotNull Predicate<String> hosts) {

        // check if hostname matches given predicate
        if (hosts.test(host)) {
            return true;
        }

        // chomp leading dot
        if (host.startsWith(".")) {
            host = host.substring(1);
        }

        // split the hostname into its individual labels first
        ArrayList<String> labels = new ArrayList<>(Arrays.asList(host.split("\\.")));

        // consecutive dots are not allowed
        for (String label : labels) {
            if ("".equals(label)) {
                return false;
            }
        }

        // numerical IPv4 addresses
        ArrayList<String> octets = parseIPv4(labels);
        boolean isIP = false;
        if (!octets.isEmpty()) {
            isIP = true;
            List<Integer> integerOctets = octets.stream().map(octet -> Integer.parseInt(octet, 10)).collect(Collectors.toList());

            // IPv4 addresses with special purpose?
            if (integerOctets.get(0) == 127 // loopback 127.x.x.x
                    // private IP ranges 192.168.x.x, 172.16-31.x.x, 10.x.x.x
                    || integerOctets.get(0) == 10
                    || (integerOctets.get(0) == 172 && integerOctets.get(1) >= 16 && integerOctets.get(1) <= 31)
                    || (integerOctets.get(0) == 192 && integerOctets.get(1) == 168)
                    // carrier-grade NAT deployment
                    || (integerOctets.get(0) == 100 && integerOctets.get(1) >= 64 && integerOctets.get(1) <= 127)
                    // link-local addresses
                    || (integerOctets.get(0) == 169 && integerOctets.get(1) == 254)) {
                return false;
            }

            host = String.join(".", octets);
        } else if (host.matches(IPV6_REGEX)) {
            // expand the IPv6 address
            ArrayList<String> groups = new ArrayList<>(Arrays.asList(host.split("\\:")));
            if (groups.size() < 8) {
                for (int i = 0; i < groups.size(); ++i) {
                    if ("".equals(groups.get(i))) {
                        groups.set(i, "0");
                        int missing = 7 - groups.size();
                        for (int j = 0; j <= missing; ++j) {
                            groups.add(j, "0");
                        }
                        break;
                    }
                }
            }

            // check it
            if (groups.stream().filter(group -> group.matches("^[0-9a-f]+$")).count() == 8) {
                List<Integer> igroups = groups.stream().map(group -> Integer.parseInt(group, 16)).collect(Collectors.toList());
                int max = Collections.max(igroups);

                if (max <= 0xffff) {
                    isIP = true;
                    String norm = groups.stream().map(group -> String.format("%-" + 4 + "s", group).replace(' ', '0')).collect(Collectors.joining(":"));
                    if (max == 0 // the unspecified address
                            // loopback
                            || norm.equals("0000:0000:0000:0000:0000:00000:0000:0001")
                            // IPv4 mapped addresses
                            || norm.matches("^0000:0000:0000:0000:0000:ffff")
                            // IPv4 translated addresses
                            || norm.matches("^0000:0000:0000:0000:ffff:0000")
                            // IPv4/IPv6 address translation
                            || norm.matches("^0064:ff9b:0000:0000:0000:0000")
                            // IPv4 compatible
                            || norm.matches("^0000:0000:0000:0000:0000:0000")
                            // discard prefix
                            || norm.matches("^0100")
                            // teredo tunneling, ORCHIDv2, documentation, 6to4
                            || norm.matches("^200[12]")
                            // private networks
                            || norm.matches("^f[cd]")
                            // link-local
                            || norm.matches("^fe[89ab]")
                            // multicast
                            || norm.matches("^ff")) {
                        return false;
                    }
                }
                // Extra: compress the address into its canonical form
            }
        }
        if (isIP) {
            return false;
        } else {
            // only fully-qualified hostnames are allowed
            // (i.e. they must have at least 2 labels)
            if (labels.size() < 2) {
                return false;
            }

            // the top-level domain name must not contain a hyphen
            // or digit unless it is an IDN
            String tld = labels.get(labels.size() - 1);
            // TODO
            //top_level_domains check, else false

            if (!tld.startsWith("xn--") && tld.matches("[-0-9]")
                    // a tld label must be at least two characters long and may be as long as 63 characters
                    || tld.length() < 2 || tld.length() > 63) {
                return false;
            }

            // special purpose and reserved top-level domain?
            // we also disallow .arpa and .int,
            // as well as .home and .corp (not registered but recommended for private use)
            if (specialPurposeTlds.contains(tld)
                    // RFC2606 second-level domain?
                    || (labels.get(labels.size() - 2).equals("example") && Arrays.asList("com", "net", "org").contains(tld))) {
                return false;
            }

            // leading hyphens or digits, and trailing hyphens are not allowed
            for (String label : labels) {
                if (label.matches("^[-0-9]") || label.matches("-$")) {
                    return false;
                }
            }

            // Unicode
            // we allow all Unicode characters except the forbidden ones in the US-ASCII range
            // i.e. we check that only alphabetic characters (a-z), decimal digits (0-9),
            // the hyphen (-), and the dot (.) or characters outside the range of US-ASCII are contained in the hostname.
            return !host.matches("[\\x00-\\x2c\\x2f\\x3a-\\x60\\x7b-\\x7f]");
        }
    }

    private @NotNull ArrayList<String> parseIPv4(ArrayList<String> labels) {
        return new UrlNormalizer().parseIPv4(labels);
    }

    private boolean basicCheck(@NotNull String paramForCheck, @NotNull String allowedSymbols) {
        if (paramForCheck.isEmpty()) {
            return true; // for testing purposes only
        }
        Set<Character> symbols = allowedSymbols.chars()
                .mapToObj(e -> (char) e).collect(Collectors.toSet());
        for (Character curChar : paramForCheck.toCharArray()) {
            if (!(Character.isAlphabetic(curChar) || Character.isDigit(curChar) || symbols.contains(curChar))) {
                return false;
            }
        }
        return true;
    }

    // https://habr.com/ru/post/232385/
    boolean schemeIsValid() {
        if ((scheme == null) || ("".equals(scheme))) {
            return true;
        }
        return basicCheck(scheme, "+-.") && Character.isLetter(scheme.charAt(0));
    }

    boolean pathIsValid() {
        if (path == null) {
            return true;
        }
        return basicCheck(path, ":@%" + SUB_DELIMITS + NOT_RESERVED);
    }

    boolean queryIsValid() {
        if (query == null) {
            return true;
        }
        return basicCheck(query, ":@/?%" + SUB_DELIMITS + NOT_RESERVED);
    }

    boolean fragmentIsValid() {
        return true;
    }

    boolean portIsValid() {
        if ((port == null) || (port.isEmpty())) {
            return true;
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
