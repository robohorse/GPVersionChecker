package com.robohorse.gpversionchecker.utils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by v.shchenev on 05/10/2017.
 */

public class DateFormatUtils {
    private static final String DATE_FORMAT = "dd/MM/yyyy";
    private static DateFormat formatter;

    static {
        formatter = new SimpleDateFormat(DATE_FORMAT, Locale.UK);
    }

    public Date formatTodayDate() {
        final Date today = new Date(System.currentTimeMillis());
        try {
            return formatter.parse(formatter.format(today));

        } catch (ParseException e) {
            e.printStackTrace();
        }
        return today;
    }
}
