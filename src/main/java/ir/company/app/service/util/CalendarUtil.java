package ir.company.app.service.util;

import com.ibm.icu.text.SimpleDateFormat;

import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.Locale;

/**
 * Created by farzad on 9/5/17.
 */
public class CalendarUtil {

    public static LocalDate getLocalDate(String stringDate, String format) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat(format, new Locale("fa"));
        return formatter.parse(stringDate).toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    public static String getFormattedDate(LocalDate date, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, new Locale("fa"));
        String formated = formatter.format(Date.from(date.atStartOfDay(ZoneId.systemDefault()).toInstant()));
        return formated;
    }

    public static String toMonth(String s) {
        return s;
    }
}
