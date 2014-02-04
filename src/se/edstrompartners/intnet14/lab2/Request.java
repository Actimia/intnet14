package se.edstrompartners.intnet14.lab2;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Request {

    private static final Pattern requestP = Pattern.compile("^(GET|POST) ([\\w+\\/.]+).*",
            Pattern.DOTALL);
    private static final Pattern cookieP = Pattern.compile(".*(?:Cookie: SESSIONID=([0-9]+)).*",
            Pattern.DOTALL);
    private static final Pattern guessP = Pattern.compile(".*guess=([0-9]+).*", Pattern.DOTALL);

    public String method;
    public String file;
    public String cookie;
    public String guess;

    public static Request parse(CharSequence cs) {
        Matcher m = requestP.matcher(cs);
        Request r = new Request();
        if (m.matches()) {
            r.method = m.group(1);
            r.file = m.group(2);

            Matcher cm = cookieP.matcher(cs);
            if (cm.matches()) {
                r.cookie = cm.group(1);
            }

            Matcher gm = guessP.matcher(cs);
            if (gm.matches()) {
                r.guess = gm.group(1);
            }
        }
        return r;
    }

    @Override
    public String toString() {
        return String.format("METHOD: %s%nFILE: %s%nCOOKIE: %s", method, file, cookie);
    }
}
