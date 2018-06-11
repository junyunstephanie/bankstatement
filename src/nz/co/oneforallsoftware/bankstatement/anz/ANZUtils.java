package nz.co.oneforallsoftware.bankstatement.anz;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class ANZUtils {
    public static final DateTimeFormatter DATE_MONTH_YEAR_FORMATTER = DateTimeFormatter.ofPattern("dd MMM yyyy");
    public static final DateTimeFormatter DATE_MONTH_YEAR_DIGIT_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd");
    public static final DateTimeFormatter DATE_MONTH_FORMATTER = DateTimeFormatter.ofPattern("dd MMM");
    //protected static final NumberFormat CURRENCY_FORMAT = new DecimalFormat("#,##0.00");

    protected static LocalDate parseDateMonthYear(String text) {
        try{
            return DATE_MONTH_YEAR_FORMATTER.parse(text, LocalDate::from);
        }catch(Exception exp){
            return null;
        }
    }

    public static int getIntValueOfLocalDate(LocalDate date){
        try{
            return Integer.parseInt(DATE_MONTH_YEAR_DIGIT_FORMATTER.format(date));
        }catch(Exception exp){
            return 0;
        }
    }

    protected static double getCurrencyValue(String text){
        String str = text.replaceAll(",", "");
        if( str.startsWith("$")){
            str = str.substring(1);
        }
        try{
            return Double.parseDouble(str);
        }catch(Exception exp){
            return 0d;
        }
    }

}
