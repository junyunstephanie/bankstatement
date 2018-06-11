package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;

public class LineANZ {
    protected static final NumberFormat LINE_NUMBER_FORMATTER = new DecimalFormat("00");

    protected static final String NEW_STATEMENT_KEYWORD = "Statement of Accounts";
    protected static final String[] TRANSACTION_PAGE_HEADER_WORDS = new String[]{"Date", "Transaction type and details", "Withdrawals", "Deposits","Balance"};
    protected static final String TRANSACTION_STATEMENT_SUB_HEADER_KEYWORDS = "Opening balance";
    protected static final String TRANSACTION_PAGE_FOOTER_KEYWORDS = "Totals at end of page";
    protected static final String ACCOUNT_NAME_KEYWORD = "Account name";
    protected static final String ACCOUNT_NUMBER_KEYWORD = "Account number";
    protected static final String OPENING_BALANCE_KEYWORD = "Opening balance";
    protected static final String CLOSING_BALANCE_KEYWORD = "Closing balance";
    protected static final String STATEMENT_PERIOD_KEYWORD = "Statement period";
    protected static final String TRANSACTION_STATEMENT_FOOTER_KEYWORDS = "Totals at end of period";
    protected static final String TRANSACTION_PAGE_SUB_HEADER_KEYWORDS = "Balance brought forward from previous page";

    protected static final double TRANSACTION_DATE_PORTRAIT_START_X = 45.36000061035156;
    protected static final double TRANSACTION_DATE_LANDSCAPE_START_X = 28.31999969482422;

    protected static final double TRANSACTION_TYPE_LANDSCAPE_START_X = 63.3599967956543;
    protected static final double TRANSACTION_TYPE_LANDSCAPE_START_X_1 = 62.87999725341797;
    protected static final double TRANSACTION_TYPE_PORTRAIT_START_X = 81.83999633789062;

    protected static final double TRANSACTION_THIRD_PARTY_LANDSCAPE_START_X = 90.0;
    protected static final double TRANSACTION_THIRD_PARTY_LANDSCAPE_START_X_1 = 89.75999450683594;
    protected static final double TRANSACTION_THIRD_PARTY_PORTRAIT_START_X = 99.5999984741211;

    protected static final double TRANSACTION_CODE_LANDSCAPE_START_X = 324.0;
    protected static final double TRANSACTION_PARTICULAR_LANDSCAPE_START_X = 234.0;
    protected static final double TRANSACTION_REFERENCE_LANDSCAPE_START_X = 414.0;

    protected static final double TRANSACTION_WITHDRAW_LANDSCAPE_END_X = 603.3480224609375;
    protected static final double TRANSACTION_DEPOSIT_LANDSCAPE_END_X = 702.22802734375;
    protected static final double TRANSACTION_BALANCE_LANDSCAPE_END_X = 801.405029296875;

    protected static final double TRANSACTION_WITHDRAW_PORTRAIT_END_X = 387.1080322265625;
    protected static final double TRANSACTION_WITHDRAW_PORTRAIT_END_X_1 = 386.9940185546875;
    protected static final double TRANSACTION_DEPOSIT_PORTRAIT_END_X = 459.1080322265625;
    protected static final double TRANSACTION_BALANCE_PORTRAIT_END_X = 531.1080322265625;
    protected static final double TRANSACTION_BALANCE_PORTRAIT_END_X_1 = 530.9940185546875;

    protected static final double INTEREST_DESCRIPTION_PORTRAIT_START_X = 115.43999481201172;
    protected static final double INTEREST_VALUE_PORTRAIT_END_X = 315.051025390625;

    protected static final double INTEREST_DESCRIPTION_LANDSCAPE_START_X = 115.43999481201172;
    protected static final double INTEREST_VALUE_LANDSCAPE_END_X = 315.051025390625;

    protected int pageNumber, lineNumber;
    protected double bottomY, topY, textHeight;
    protected ArrayList<WordANZ> words = new ArrayList<>();

    public LineANZ(int pageNumber, int lineNumber, WordANZ wordANZ){
        this.pageNumber = pageNumber;
        this.lineNumber = lineNumber;
        topY = wordANZ.getTopY();
        bottomY = wordANZ.getBottomY();
        words.add(wordANZ);
        textHeight = wordANZ.getTextHeight();
    }

    public double getTextHeight(){
        return textHeight;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public double getBottomY() {
        return bottomY;
    }

    public double getTopY() {
        return topY;
    }

    public int getWordCount(){
        return words.size();
    }

    public WordANZ getWord(int index){
        if( index < 0 || index >= words.size() ){
            return null;
        }

        return words.get(index);
    }

    public void addWord(WordANZ word){
        words.add(word);
    }

    public int indexOfKeyword(String keyword){
        if( keyword == null || keyword.trim().length() == 0 ){
            return -1;
        }

        int count = words.size();
        for(int index = 0; index < count; index++){
            if( words.get(index).getText().trim().equalsIgnoreCase(keyword.trim())){
                return index;
            }
        }
        return -1;
    }

    public WordANZ getDateWord(Utils.PdfPageOrientation orientation){
        if(orientation == Utils.PdfPageOrientation.PORTRAIT ){
            for(WordANZ word: words){
                if(word.getStartX() == TRANSACTION_DATE_PORTRAIT_START_X ){
                    return word;
                }
            }
            return null;
        }else{
            for(WordANZ word: words){
                if(word.getStartX() == TRANSACTION_DATE_LANDSCAPE_START_X ){
                    return word;
                }
            }
            return null;
        }
    }

    public WordANZ getTypeWord(Utils.PdfPageOrientation orientation){
        if(orientation == Utils.PdfPageOrientation.PORTRAIT ){
            for(WordANZ word: words){
                if(word.getStartX() == TRANSACTION_TYPE_PORTRAIT_START_X ){
                    return word;
                }
            }
            return null;
        }else{
            for(WordANZ word: words){
                if(word.getStartX() == TRANSACTION_TYPE_LANDSCAPE_START_X || word.getStartX() == TRANSACTION_TYPE_LANDSCAPE_START_X_1 ){
                    return word;
                }
            }
            return null;
        }
    }

    public WordANZ getCodeWord(Utils.PdfPageOrientation orientation){
        if(orientation == Utils.PdfPageOrientation.LANDSCAPE ){
            for(WordANZ word: words){
                if(word.getStartX() == TRANSACTION_CODE_LANDSCAPE_START_X  ){
                    return word;
                }
            }
            return null;
        }else{
            return null;
        }
    }

    public WordANZ getParticularWord(Utils.PdfPageOrientation orientation){
        if(orientation == Utils.PdfPageOrientation.LANDSCAPE ){
            for(WordANZ word: words){
                if(word.getStartX() == TRANSACTION_PARTICULAR_LANDSCAPE_START_X  ){
                    return word;
                }
            }
            return null;
        }else{
            return null;
        }
    }

    public WordANZ getReferenceWord(Utils.PdfPageOrientation orientation){
        if(orientation == Utils.PdfPageOrientation.LANDSCAPE ){
            for(WordANZ word: words){
                if(word.getStartX() == TRANSACTION_REFERENCE_LANDSCAPE_START_X  ){
                    return word;
                }
            }
            return null;
        }else{
            return null;
        }
    }

    public WordANZ getThirdPartyWord(Utils.PdfPageOrientation orientation){
        if(orientation == Utils.PdfPageOrientation.PORTRAIT ){
            for(WordANZ word: words){
                if(word.getStartX() == TRANSACTION_THIRD_PARTY_PORTRAIT_START_X ){
                    return word;
                }
            }
            return null;
        }else{
            for(WordANZ word: words){
                if(word.getStartX() == TRANSACTION_THIRD_PARTY_LANDSCAPE_START_X || word.getStartX() == TRANSACTION_THIRD_PARTY_LANDSCAPE_START_X_1 ){
                    return word;
                }
            }
            return null;
        }
    }

    public WordANZ getWithdrawWord(Utils.PdfPageOrientation orientation){
        if(orientation == Utils.PdfPageOrientation.PORTRAIT ){
            for(WordANZ word: words){
                if( word.getEndX() >= TRANSACTION_WITHDRAW_PORTRAIT_END_X - 2.0 &&  word.getEndX() <= TRANSACTION_WITHDRAW_PORTRAIT_END_X + 2.0 ){
                    return word;
                }
            }
            return null;
        }else{
            for(WordANZ word: words){
                if( word.getEndX() >= TRANSACTION_WITHDRAW_LANDSCAPE_END_X - 3.0 && word.getEndX() <= TRANSACTION_WITHDRAW_LANDSCAPE_END_X + 3.0){
                    return word;
                }
            }
            return null;
        }
    }

    public WordANZ getDepositWord(Utils.PdfPageOrientation orientation){
        if(orientation == Utils.PdfPageOrientation.PORTRAIT ){
            for(WordANZ word: words){
                if( word.getEndX() >= TRANSACTION_DEPOSIT_PORTRAIT_END_X - 2.0 && word.getEndX() <= TRANSACTION_DEPOSIT_PORTRAIT_END_X + 2.0){
                    return word;
                }
            }
            return null;
        }else{
            for(WordANZ word: words){
                if( word.getEndX() >= TRANSACTION_DEPOSIT_LANDSCAPE_END_X - 3.0 && word.getEndX() <= TRANSACTION_DEPOSIT_LANDSCAPE_END_X + 3.0){
                    return word;
                }
            }
            return null;
        }
    }

    public WordANZ getBalanceWord(Utils.PdfPageOrientation orientation){
        if(orientation == Utils.PdfPageOrientation.PORTRAIT ){
            for(WordANZ word: words){
                if(word.getEndX() >= TRANSACTION_BALANCE_PORTRAIT_END_X - 2.0 && word.getEndX() <= TRANSACTION_BALANCE_PORTRAIT_END_X + 2.0){
                    return word;
                }
            }
            return null;
        }else{
            for(WordANZ word: words){
                if( word.getEndX() >= TRANSACTION_BALANCE_LANDSCAPE_END_X - 3.0 && word.getEndX() <= TRANSACTION_BALANCE_LANDSCAPE_END_X + 3.0){
                    return word;
                }
            }
            return null;
        }
    }

    public boolean isInterestSubLine(Utils.PdfPageOrientation orientation){
        if( getWordCount() != 2 ){
            return false;
        }

        if( orientation == Utils.PdfPageOrientation.PORTRAIT ){
            if( !getWord(0).getText().endsWith("interest") || getWord(0).getStartX() != INTEREST_DESCRIPTION_PORTRAIT_START_X ){
                return false;
            }
            if(!Utils.isCurrency(getWord(1).getText(), true) || getWord(1).getEndX() != INTEREST_VALUE_PORTRAIT_END_X){
                return false;
            }
            return true;
        }else{
            if( !getWord(0).getText().endsWith("interest") || getWord(0).getStartX() != INTEREST_DESCRIPTION_LANDSCAPE_START_X ){
                return false;
            }
            if(!Utils.isCurrency(getWord(1).getText(), true) || getWord(1).getEndX() != INTEREST_VALUE_LANDSCAPE_END_X){
                return false;
            }
            return true;
        }
    }

    public String toString(){
        String str = String.valueOf(PageANZ.PAGE_NUMBER_FORMATTER.format(pageNumber) + "-" + LINE_NUMBER_FORMATTER.format(lineNumber)) + ": ";
        for(WordANZ word: words){
            str = str + word.getText() + ", ";
        }
        if( str.endsWith(",") ){
            str = str.substring(0, str.lastIndexOf(","));
        }
        return str;
    }
}
