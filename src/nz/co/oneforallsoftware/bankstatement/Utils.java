package nz.co.oneforallsoftware.bankstatement;

import org.apache.commons.validator.routines.DoubleValidator;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utils {
    public static enum PdfPageOrientation{
        PORTRAIT,
        LANDSCAPE;
    }

    public static final DoubleValidator CURRENCY_VALIDATOR = new DoubleValidator(true, DoubleValidator.CURRENCY_FORMAT);
    public static final DoubleValidator DOUBLE_VALIDATOR = new DoubleValidator(true, DoubleValidator.STANDARD_FORMAT);

    public static boolean isCurrency(String currency, boolean withSymbol){
        String regx = "^(?:0|[1-9]\\d{0,2}(?:,\\d{3})*)\\.\\d{2}$";
        if( withSymbol){
            regx = "^\\$(?:0|[1-9]\\d{0,2}(?:,\\d{3})*)\\.\\d{2}$";
        }
        //Pattern p=Pattern.compile("^(?:0|[1-9]\\d{0,2}(?:\\.\\d{3})*),\\d{2}$");
        Pattern p=Pattern.compile(regx);
        Matcher matcher = p.matcher(currency);
        return matcher.matches();
    }

    public static Double getCurrencyValueObj(String text){
        if( text.startsWith("$") || text.startsWith("-$")) {
            try {
                return CURRENCY_VALIDATOR.validate(text);
            } catch (Exception exp) {
                return null;
            }
        }else{
            try {
                return DOUBLE_VALIDATOR.validate(text);
            } catch (Exception exp) {
                return null;
            }
        }
    }

    public static boolean isBankAccountNumber(String text, String separator){
        if( text == null ){
            return false;
        }
        if( separator != null ){
            String[] strArray = text.split(separator);
            if( strArray.length != 4 ){
                return false;
            }

            if( strArray[0].length() != 2 ){
                return false;
            }else{
                try{
                    Integer.parseInt(strArray[0]);
                }catch(Exception exp){
                    return false;
                }
            }

            if( strArray[1].length() != 4){
                return false;
            }else{
                try{
                    Integer.parseInt(strArray[1]);
                }catch(Exception exp){
                    return false;
                }
            }

            if( strArray[2].length() != 7 ){
                return false;
            }else{
                try{
                    Integer.parseInt(strArray[2]);
                }catch(Exception exp){
                    return false;
                }
            }

            if( strArray[3].length() != 2 && strArray[3].length() != 3 ){
                return false;
            }else{
                try{
                    Integer.parseInt(strArray[3]);
                }catch(Exception exp){
                    return false;
                }
            }

            return true;
        }else{
            if( text.length() != 15 && text.length() != 16 ){
                return false;
            }else{
                try{
                    Long.parseLong(text.trim());
                    return true;
                }catch(Exception exp){
                    return false;
                }
            }
        }
    }
}
