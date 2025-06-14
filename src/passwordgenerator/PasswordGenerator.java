package passwordgenerator;

import java.util.Random;

public class PasswordGenerator {
    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SPECIAL = "!@#$%^&*()-_=+[]{}|;:,.<>?/";

    public static String generate(int length, boolean useUpper, boolean useLower, boolean useDigits, boolean useSpecial) {
        StringBuilder pool = new StringBuilder();
        if (useUpper) pool.append(UPPER);
        if (useLower) pool.append(LOWER);
        if (useDigits) pool.append(DIGITS);
        if (useSpecial) pool.append(SPECIAL);

        if (pool.length() == 0 || length <= 0) return "";

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            int index = random.nextInt(pool.length());
            password.append(pool.charAt(index));
        }

        return password.toString();
    }

    public static String evaluateStrength(String password, boolean upper, boolean lower, boolean digits, boolean special) {
        int score = 0;

        if (password.length() >= 8) score++;
        if (password.length() >= 12) score++;
        if (upper) score++;
        if (lower) score++;
        if (digits) score++;
        if (special) score++;

        if (score <= 2) return "Weak";
        else if (score <= 4) return "Medium";
        else if (score == 5) return "Strong";
        else return "Very Strong";
    }
}