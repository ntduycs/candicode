package vn.candicode.utils;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class RegexUtils {
    private static final String FLOAT = "[+-]?\\d+(.\\d+)?";

    private static final String FLOAT_LIST = "\\[([+-]?\\d+(.\\d+)?)?(,[+-]?\\d+(.\\d+)?)*\\]";

    private static final String INTEGER = "[+-]?\\d+";

    private static final String INTEGER_LIST = "\\[(\\d+)?(,\\d+)*\\]";

    private static final String STRING = "[\\w\\r\\n\\s\\0]+";

    private static final String STRING_LIST = "\\[[\\w\\r\\n\\s.,?-]*(,[\\w\\r\\n\\s.,?-]*)*\\]";

    private static final String BOOLEAN = "[0-1]";

    private static final String BOOLEAN_LIST = "\\[[0-1]?(,[0-1])*\\]";

    public static String generateRegex(List<String> types) throws IllegalArgumentException {
        StringBuilder sb = new StringBuilder();

        sb.append("^");

        for (int i = 0, typesSize = types.size(); i < typesSize; i++) {
            if (i != 0) {
                sb.append("\\|");
            }

            String type = types.get(i);
            sb.append(generateRegexByType(type));
        }

        sb.append("$");

        return sb.toString();
    }

    public static List<String> resolveRegex(String regex) throws IllegalArgumentException {
        String[] subRegexps = regex
            .substring(1, regex.length() - 1)
            .replace("\\|", "|")
            .split("\\|");
        return Arrays.stream(subRegexps).map(RegexUtils::resolveRegexTemplate).collect(Collectors.toList());
    }

    private static String resolveRegexTemplate(String regex) {
        switch (regex) {
            case INTEGER:
                return "integer";
            case INTEGER_LIST:
                return "integer_list";
            case FLOAT:
                return "float";
            case FLOAT_LIST:
                return "float_list";
            case STRING:
                return "string";
            case STRING_LIST:
                return "string_list";
            case BOOLEAN:
                return "boolean";
            case BOOLEAN_LIST:
                return "boolean_list";
            default:
                throw new IllegalArgumentException("Given string does not match any regex template");
        }
    }

    private static String generateRegexByType(String type) {
        switch (type) {
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
                throw new IllegalArgumentException("Regex template not found");
        }
    }
}
