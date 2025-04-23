package com.team15gijo.chat.infrastructure.utils;

import java.util.regex.Pattern;

public class RegexUtils {
    public static String escapeRegex(String input) {
        if (input == null || input.isEmpty()) {
            return input;
        }
        Pattern specialCharacters = Pattern.compile("[\\.\\*\\+\\?\\^\\$\\{\\}\\[\\]\\\\|\\(\\)]");
        return specialCharacters.matcher(input).replaceAll("\\\\$0");
    }
}
