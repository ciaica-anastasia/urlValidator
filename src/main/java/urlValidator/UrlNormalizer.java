package urlValidator;

import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public final class UrlNormalizer {

    private static final String OCTAL_NUMBER_REGEX = "^0[0-7]*$";
    private static final String HEXADECIMAL_NUMBER_REGEX = "^(0+[xX])?([0-9a-fA-F]+)$";
    private static final String DECIMAL_NUMBER_REGEX = "^[1-9][0-9]*";

    // checks whether a value matches a regex
//    static boolean regexMatch(String regex, String value) {
//        Matcher m = getMatcher(regex, value);
//
//        if (m.find() && m.group().equals(value)) {
//            return true;
//        } else return false;
//    }

    private static Matcher getMatcher(String regex, String value) {
        Pattern p = Pattern.compile(regex);
        return p.matcher(value);
    }

    // parses an IPv4 decimal address (already split up at the dots)
    // into four integers in the range of 0-255 or returns false
    public Pair<ArrayList<String>, Boolean> parseIPv4(ArrayList<String> labels, Boolean isSymbHost) {
        ArrayList<String> partsIPv4 = new ArrayList<>();

        if (labels.size() < 1 || labels.size() > 4) {
            return new Pair<>(partsIPv4, isSymbHost);
        }

        // parse the individual numerical labels into groups
        // we consider 32 bit overflows in the numbers to be invalid
        for (String label : labels) {
            if (label.matches(OCTAL_NUMBER_REGEX)) {
                label = getMatcher("^0*", label).replaceAll("0");
                int length = label.length();
                // make sure it fits into 32 bits. maxValue = 037777777777
                if (length > 12 || (length == 12 && Long.parseLong(label, 8) > 037777777777)) {
                    partsIPv4.clear();
                    return new Pair<>(partsIPv4, isSymbHost);
                }
                // transform to decimal
                label = String.valueOf(Integer.parseInt(label, 8));
                partsIPv4.add(label);
            } else if (label.matches(DECIMAL_NUMBER_REGEX)) {
                label = getMatcher("^0+", label).replaceAll("");
                int length = label.length();
                // make sure it fits into 32 bits
                if (length > 10 || (length == 10 && Long.parseLong(label) > 4294967295L)) {
                    partsIPv4.clear();
                    return new Pair<>(partsIPv4, isSymbHost);
                }

                partsIPv4.add(label);
            } else if (label.matches(HEXADECIMAL_NUMBER_REGEX)) {
                label = getMatcher("^0+([xX]?)", label).replaceAll("");

                if (label.length() > 8) {
                    partsIPv4.clear();
                    return new Pair<>(partsIPv4, isSymbHost);
                }

                label = String.valueOf(Long.parseLong(label, 16));
                partsIPv4.add(label);
            } else if (label.contains(":")) {
                isSymbHost = false;
                partsIPv4.clear();
                return new Pair<>(partsIPv4, isSymbHost);
            } else {
                isSymbHost = true;
                partsIPv4.clear();
                return new Pair<>(partsIPv4, isSymbHost);
            }
        }

        List<Integer> ipartsIPv4 = partsIPv4.stream().map(Integer::parseInt).collect(Collectors.toList());
        if (ipartsIPv4.size() == 4) {
            // x.x.x.x with 8.8.8.8 bits. 0xff == 255
            if (Collections.max(ipartsIPv4) > 0xff) {
                partsIPv4.clear();
                return new Pair<>(partsIPv4, isSymbHost);
            }
        } else if (ipartsIPv4.size() == 3) {
            // x.x.x with 8.8.16 bits. 0xff == 255
            if (ipartsIPv4.get(0) > 0xff || ipartsIPv4.get(1) > 0xff
                    || ipartsIPv4.get(2) > 0xffff) {
                partsIPv4.clear();
                return new Pair<>(partsIPv4, isSymbHost);
            }

            partsIPv4.add(3, String.valueOf(ipartsIPv4.get(2) & 0xff));
            partsIPv4.set(2, String.valueOf(ipartsIPv4.get(2) >> 8));
        } else if (ipartsIPv4.size() == 2) {
            // x.x with 8.24 bits.
            if (ipartsIPv4.get(0) > 0xff || ipartsIPv4.get(1) > 0xffffff) {
                partsIPv4.clear();
                return new Pair<>(partsIPv4, isSymbHost);
            }

            partsIPv4.add(2, String.valueOf(ipartsIPv4.get(1) >> 8 & 0xff));
            partsIPv4.add(3, String.valueOf(ipartsIPv4.get(1) & 0xff));
            partsIPv4.set(1, String.valueOf(ipartsIPv4.get(1) >> 16));
        } else {
            // we already checked for overflows
            partsIPv4.add(1, String.valueOf(ipartsIPv4.get(0) >> 16 & 0xff));
            partsIPv4.add(2, String.valueOf(ipartsIPv4.get(0) >> 8 & 0xff));
            partsIPv4.add(3, String.valueOf(ipartsIPv4.get(0) & 0xff));
            partsIPv4.set(0, String.valueOf(ipartsIPv4.get(0) >> 24));
        }

        return new Pair<>(partsIPv4, isSymbHost);
    }
}
