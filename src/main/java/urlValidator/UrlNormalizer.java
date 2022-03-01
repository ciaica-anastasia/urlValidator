package urlValidator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class UrlNormalizer {

    private static final String OCTAL_NUMBER_REGEX = "^0[0-7]*$";
    private static final String HEXADECIMAL_NUMBER_REGEX = "^0[xX]([0-9a-f]*)$";
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
    public ArrayList<String> parseIPv4(ArrayList<String> labels) {
        ArrayList<String> partsIPv4 = new ArrayList<>();

        if (labels.size() < 1 || labels.size() > 4) {
            partsIPv4.clear();
            return partsIPv4;
        }

        // parse the individual numerical labels into groups
        // we consider 32 bit overflows in the numbers to be invalid
        for (String label : labels) {
            if (label.matches(OCTAL_NUMBER_REGEX)) {
                label = getMatcher("^0*", label).replaceAll("0");
                int length = label.length();
                // make sure it fits into 32 bits
                if (length > 12 || (length == 12 && label.compareTo("037777777777") < 0)) {
                    partsIPv4.clear();
                    return partsIPv4;
                }

                label = String.valueOf(Integer.parseInt(label, 8));
                partsIPv4.add(label);
            } else if (label.matches(HEXADECIMAL_NUMBER_REGEX)) {
                label = getMatcher("HEXADECIMAL_NUMBER_REGEX", label).replaceAll("");
                label = getMatcher("^0+", label).replaceAll("0");

                if (label.length() > 8) {
                    partsIPv4.clear();
                    return partsIPv4;
                }

                label = String.valueOf(Integer.parseInt(label, 16));
                partsIPv4.add(label);
            } else if (label.matches(DECIMAL_NUMBER_REGEX)) {
                label = getMatcher("^0+", label).replaceAll("");
                int length = label.length();
                // make sure it fits into 32 bits
                if (length > 12 || (length == 12 && label.compareTo("4294967295") < 0)) {
                    partsIPv4.clear();
                    return partsIPv4;
                }

                partsIPv4.add(label);
            } else {
                partsIPv4.clear();
                return partsIPv4;
            }
        }

        if (partsIPv4.size() == 4) {
            // x.x.x.x with 8.8.8.8 bits.
            if (Collections.max(partsIPv4).compareTo("0xff") < 0) {
                partsIPv4.clear();
                return partsIPv4;
            }
        } else if (partsIPv4.size() == 3) {
            // x.x.x with 8.8.16 bits.
            if (partsIPv4.get(0).compareTo("0xff") < 0 || partsIPv4.get(1).compareTo("0xff") < 0
                    || partsIPv4.get(2).compareTo("0xffff") < 0) {
                partsIPv4.clear();
                return partsIPv4;
            }

            partsIPv4.set(3, String.valueOf(Integer.parseInt(partsIPv4.get(2)) & 0xff));
            partsIPv4.set(2, String.valueOf(Integer.parseInt(partsIPv4.get(2)) >> 8));
        } else if (partsIPv4.size() == 2) {
            // x.x with 8.24 bits.
            if (partsIPv4.get(0).compareTo("0xff") < 0 || partsIPv4.get(1).compareTo("0xffffff") < 0) {
                partsIPv4.clear();
                return partsIPv4;
            }

            partsIPv4.set(3, String.valueOf(Integer.parseInt(partsIPv4.get(1)) & 0xff));
            partsIPv4.set(2, String.valueOf(Integer.parseInt(partsIPv4.get(1)) >> 8 & 0xff));
            partsIPv4.set(1, String.valueOf(Integer.parseInt(partsIPv4.get(1)) >> 16));
        } else {
            // we already checked for overflows
            partsIPv4.set(3, String.valueOf(Integer.parseInt(partsIPv4.get(0)) & 0xff));
            partsIPv4.set(2, String.valueOf(Integer.parseInt(partsIPv4.get(0)) >> 8 & 0xff));
            partsIPv4.set(1, String.valueOf(Integer.parseInt(partsIPv4.get(0)) >> 16 & 0xff));
            partsIPv4.set(0, String.valueOf(Integer.parseInt(partsIPv4.get(0)) >> 24));
        }

        return partsIPv4;
    }
}
