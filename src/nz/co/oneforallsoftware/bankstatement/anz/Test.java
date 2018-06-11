package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.Utils;
import org.apache.commons.validator.routines.BigDecimalValidator;
import org.apache.commons.validator.routines.CurrencyValidator;
import org.apache.commons.validator.routines.DoubleValidator;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Test {
    public static void main(String[] args){
        NumberFormat numberFormat = new DecimalFormat("#,##0.00");
        try {
            DoubleValidator validator = new DoubleValidator(true, DoubleValidator.CURRENCY_FORMAT);
            double value = validator.validate("$1.00").doubleValue();

            System.out.println(value);
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }
}
