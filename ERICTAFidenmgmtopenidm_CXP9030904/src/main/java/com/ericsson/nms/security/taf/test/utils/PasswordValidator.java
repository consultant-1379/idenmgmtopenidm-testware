package com.ericsson.nms.security.taf.test.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class PasswordValidator {

    public static boolean validate(String str, String[] regexes) {

        Matcher matcher;
        Pattern pattern;

        for (String regex : regexes) {
            pattern = Pattern.compile(regex);
            matcher = pattern.matcher(str);
            if (!matcher.matches()) {
                return false;
            }
        }
        return true;
    }

}
