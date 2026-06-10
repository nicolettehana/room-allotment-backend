package sad.sras.config;

import com.google.code.kaptcha.text.TextProducer;

import java.security.SecureRandom;

public class SecureCaptchaTextProducer implements TextProducer {

    private static final String UPPER = "ABCDEFGHJKLMNPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijkmnpqrstuvwxyz";
    private static final String NUM = "23456789";
    private static final String ALL = UPPER + LOWER + NUM;

    private static final int LENGTH = 6;

    private final SecureRandom random = new SecureRandom();

    @Override
    public String getText() {

        StringBuilder captcha = new StringBuilder();

        captcha.append(UPPER.charAt(random.nextInt(UPPER.length())));
        captcha.append(LOWER.charAt(random.nextInt(LOWER.length())));
        captcha.append(NUM.charAt(random.nextInt(NUM.length())));

        for (int i = 3; i < LENGTH; i++) {
            captcha.append(ALL.charAt(random.nextInt(ALL.length())));
        }

        return shuffle(captcha.toString());
    }

    private String shuffle(String input) {
        char[] chars = input.toCharArray();

        for (int i = chars.length - 1; i > 0; i--) {
            int j = random.nextInt(i + 1);
            char temp = chars[i];
            chars[i] = chars[j];
            chars[j] = temp;
        }

        return new String(chars);
    }
}