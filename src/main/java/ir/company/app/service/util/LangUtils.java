package ir.company.app.service.util;

import java.util.Locale;


/**
 * @author : Hamed Hatami , Javad Sarhadi , Farzad Sedaghatbin, Atefeh Ahmadi
 * @version : 0.8
 */
public class LangUtils {
    public static Locale LOCALE_FARSI = new Locale("fa");
    public static Locale LOCALE_ENGLISH = Locale.ENGLISH;

    public static String getNumber(int number, Locale locale) {
        return getNumber("" + number, locale);
    }

    public static String getNumber(String number, Locale locale) {
        if (locale.equals(LOCALE_FARSI))
            return getFarsiNumber(number);
        return number;
    }

    public static String getFarsiNumber(String number) {
        StringBuffer farsiNumberString = new StringBuffer();
        char c;

        for (int i = 0; i < number.length(); i++) {
            c = number.charAt(i);
            switch (c) {
                case '0':
                    farsiNumberString.append("۰");
                    break;

                case '1':
                    farsiNumberString.append("۱");
                    break;

                case '2':
                    farsiNumberString.append("۲");
                    break;

                case '3':
                    farsiNumberString.append("۳");
                    break;

                case '4':
                    farsiNumberString.append("۴");
                    break;

                case '5':
                    farsiNumberString.append("۵");
                    break;

                case '6':
                    farsiNumberString.append("۶");
                    break;

                case '7':
                    farsiNumberString.append("۷");
                    break;

                case '8':
                    farsiNumberString.append("۸");
                    break;

                case '9':
                    farsiNumberString.append("۹");
                    break;

                default:
                    farsiNumberString.append(c);
                    break;
            }
        }
        return farsiNumberString.toString();
    }

    public static Locale refine(Locale l) {

        if (l == null)
            return LOCALE_ENGLISH;

        if ("fa".equalsIgnoreCase(l.getLanguage()))
            return LOCALE_FARSI;

        return LOCALE_ENGLISH;

    }


}
