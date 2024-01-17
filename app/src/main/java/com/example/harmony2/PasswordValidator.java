package com.example.harmony2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
//onur
public class PasswordValidator {

    private static final String PASSWORD_PATTERN = "^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=\\S+$).{8,}$";

    private static final Pattern pattern = Pattern.compile(PASSWORD_PATTERN);

    public static boolean validate(final String password) {
        Matcher matcher = pattern.matcher(password);
        return matcher.matches();
    }

    public static boolean matching(final String password, final String confirmPassword) {
        return password.equals(confirmPassword);
    }

}
