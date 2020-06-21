package vn.candicode.util;

import com.google.common.collect.Lists;

import java.util.List;

public class RegexUtils {
    private static final String FLOAT = "[+-]?\\d+(.\\d+)?";

    private static final String FLOAT_LIST = "\\[([+-]?\\d+(.\\d+)?)?(,\\s?[+-]?\\d+(.\\d+)?)*\\]";

    private static final String INTEGER = "[+-]?\\d+";

    private static final String INTEGER_LIST = "\\[(\\d+)?(,\\s?\\d+)*\\]";

    private static final String STRING = "[\\w\\r\\n\\s\\0]+";

    private static final String STRING_LIST = "\\[[\\w\\r\\n\\s.,?-]*(,\\s?[\\w\\r\\n\\s.,?-]*)*\\]";

    private static final String BOOLEAN = "[0-1]";

    private static final String BOOLEAN_LIST = "\\[[0-1]?(,\\s?[0-1])*\\]";

    private RegexUtils() {
    }

    /**
     * If any <code>identifier</code> not match any template, ignore it
     *
     * @param identifiers
     * @return the string combined from multiple templates based on <code>identifiers</code>
     * @throws IllegalArgumentException if no template matches <code>identifier</code>
     */
    public static String genRegex(List<String> identifiers) {
        StringBuilder sb = new StringBuilder();

        sb.append("^");

        final int size = identifiers.size();
        for (int i = 0; i < size; i++) {
            if (i != 0) sb.append("\\|");

            sb.append(genRegex(identifiers.get(i)));
        }

        sb.append("$");

        return sb.toString();
    }

    /**
     * @param identifier
     * @return regex that match the <code>identifier</code>
     * @throws IllegalArgumentException if no template matches <code>identifier</code>
     */
    private static String genRegex(String identifier) {
        switch (identifier) {
            case "float":
                return FLOAT;
            case "float_list":
                return FLOAT_LIST;
            case "integer":
                return INTEGER;
            case "integer_list":
                return INTEGER_LIST;
            case "string":
                return STRING;
            case "string_list":
                return STRING_LIST;
            case "boolean":
                return BOOLEAN;
            case "boolean_list":
                return BOOLEAN_LIST;
            default:
                throw new IllegalArgumentException("Regular expression template not found");
        }
    }

    public static List<String> resolveRegex(String regex) {
        String[] subRegexps = regex
            .substring(1, regex.length() - 1)
            .replace("\\|", "|")
            .split("\\|");

        return Lists.newArrayList(subRegexps);
    }

    
}
