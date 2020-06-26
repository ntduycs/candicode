package vn.candicode.util;

import com.google.common.collect.Lists;

import java.util.List;

public class LanguageUtils {
    private static final List<String> scriptingLanguage = Lists.newArrayList("python", "js");

    public static boolean requireCompile(String language) {
        return !scriptingLanguage.contains(language.toUpperCase());
    }
}
