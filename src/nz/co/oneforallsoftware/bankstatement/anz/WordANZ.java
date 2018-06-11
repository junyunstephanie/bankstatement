package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.Utils;

import java.time.LocalDate;
import java.util.ArrayList;

public class WordANZ {
    private String text;
    private double startX, endX;
    private double topY, bottomY;
    private double textHeight;

    public WordANZ(String text, double startX, double endX, double endY, double height){
        this.text = text;
        this.startX = startX;
        this.bottomY = endY;
        this.endX = endX;
        this.topY = bottomY + height;
        textHeight = height;
    }

    public String getText() {
        return text;
    }

    public double getStartX() {
        return startX;
    }

    public double getEndX() {
        return endX;
    }

    public double getTopY() {
        return topY;
    }

    public double getBottomY() {
        return bottomY;
    }

    public double getTextHeight(){
        return textHeight;
    }

    public String getStringOfInt(){
        if( text == null || text.trim().length() == 0 ){
            return null;
        }
        try{
            Integer.parseInt(text.trim());
            return text;
        }catch(Exception exp){
            return null;
        }
    }

    public Double getCurrencyValue(){
        if( text == null || text.trim().length() == 0 ){
            return null;
        }
        if( text.startsWith("$") ){
            if( Utils.isCurrency(text, true) ){
                Double obj = Utils.getCurrencyValueObj(text);
                if( obj != null ){
                    return obj.doubleValue();
                }else{
                    return null;
                }
            }else{
                return null;
            }
        }
        if( Utils.isCurrency(text, false) ){
            Double obj = Utils.getCurrencyValueObj(text);
            if( obj != null ){
                return obj.doubleValue();
            }else{
                return null;
            }
        }else{
            return null;
        }
    }

    public LocalDate getPeriodStartDate(){
        if( text == null || text.trim().length() == 0 ){
            return null;
        }

        String[] dates = text.split("-");
        if( dates.length != 2 ){
            return null;
        }

        try{
            return ANZUtils.DATE_MONTH_YEAR_FORMATTER.parse(dates[0].trim(), LocalDate::from);
        }catch(Exception exp){
            return null;
        }
    }

    public LocalDate getPeriodEndDate(){
        if( text == null || text.trim().length() == 0 ){
            return null;
        }

        String[] dates = text.split("-");
        if( dates.length != 2 ){
            return null;
        }

        try{
            return ANZUtils.DATE_MONTH_YEAR_FORMATTER.parse(dates[1].trim(), LocalDate::from);
        }catch(Exception exp){
            return null;
        }
    }

    public String getBankAccount(){
        if( Utils.isBankAccountNumber(text, "-")){
            return text;
        }else{
            return null;
        }
    }
}
