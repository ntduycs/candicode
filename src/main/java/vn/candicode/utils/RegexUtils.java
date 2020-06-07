package vn.candicode.utils;

import java.util.List;

public class RegexUtils {
    private static final String FLOAT = "[+-]?\\d+(.\\d+)?";

    private static final String FLOAT_LIST = "\\[([+-]?\\d+(.\\d+)?)?(,[+-]?\\d+(.\\d+)?)*\\]";

    private static final String INTEGER = "[+-]?\\d+";

    private static final String INTEGER_LIST = "\\[(\\d+)?(,\\d+)*\\]";

    private static final String STRING = "[\\w\\r\\n\\s\\0]+";

    private static final String STRING_LIST = "\\[[\\w\\r\\n\\s.,?-]*(,[\\w\\r\\n\\s.,?-]*)*\\]";

    private static final String BOOLEAN = "[0-1]";

    private static final String BOOLEAN_LIST = "\\[[0-1]?(,[0-1])*\\]";

    public static String generateRegex(List<String> types) {
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
                return null;
        }
    }
}
