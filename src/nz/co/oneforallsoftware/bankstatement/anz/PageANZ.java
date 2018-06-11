package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.Utils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.util.ArrayList;

import nz.co.oneforallsoftware.bankstatement.Utils.PdfPageOrientation;

public class PageANZ {
    protected static final NumberFormat PAGE_NUMBER_FORMATTER = new DecimalFormat("000");

    private double pageWidth, pageHeight;

    private int pageNumber;
    private ArrayList<LineANZ> lines = new ArrayList<>();
    private Utils.PdfPageOrientation orientation;

    public PageANZ(int pageNumber, double pageWidth, double pageHeight){
        this.pageNumber = pageNumber;
        orientation = Utils.PdfPageOrientation.PORTRAIT;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public int getLineCount(){
        return lines.size();
    }

    public LineANZ getLine(int index){
        if( index < 0 || index >= lines.size() ){
            return null;
        }
        return lines.get(index);
    }

    public void addLine(LineANZ line){
        lines.add(line);
    }

    public void setOrientation(PdfPageOrientation orientation) {
        this.orientation = orientation;
    }

    public PdfPageOrientation getOrientation() {
        return orientation;
    }

    public void process(ArrayList<ANZStatement> statements, String fileName){
        boolean transactionStarted = false;
        boolean transactionFinished = false;
        boolean newStatement = false;
        int transactionIndex = 0;
        ANZStatement statement = null;
        ANZPage stmtPage = null;

        HorizonCoordinate pdfHCoordinate = new HorizonCoordinate(pageNumber, pageWidth, pageHeight, orientation);

        for(LineANZ line: lines){
            if( !transactionStarted ){
                //System.out.println(line.toString());
                if( line.getLineNumber() == 0 ){
                    if(line.getWordCount() == 1 && line.getWord(0).getText().equalsIgnoreCase(LineANZ.NEW_STATEMENT_KEYWORD)) {
                        statement = new ANZStatement(fileName, pageNumber);
                        statements.add(statement);
                        newStatement = true;
                        stmtPage = new ANZPage(1, pageNumber, pdfHCoordinate);
                    }else{
                        statement = statements.get(statements.size()-1);
                        int pageCount = statement.getPageCount();

                        stmtPage = new ANZPage(pageCount + 1, pageNumber, pdfHCoordinate);
                    }

                    statement.addPage(stmtPage);
                    continue;
                }

                if( newStatement ){
                    if( line.getLineNumber() == 2 ){
                        if(  line.getWordCount() == 1) {
                            WordANZ word = line.getWord(0);
                            String version = word.getStringOfInt();
                            if (version != null) {
                                statement.setVersion(version);
                            }
                        }
                        //continue;
                    }

                    int index = line.indexOfKeyword(LineANZ.ACCOUNT_NAME_KEYWORD);
                    if( index != -1 && line.getWordCount() > index + 1){
                        statement.setAccountName(line.getWord(index + 1 ).getText());
                    }

                    index = line.indexOfKeyword(LineANZ.ACCOUNT_NUMBER_KEYWORD );
                    if( index != -1 && line.getWordCount() > index + 1){
                        WordANZ word = line.getWord(index+1);
                        String bankAccNumber = word.getBankAccount();
                        if( bankAccNumber != null ){
                            statement.setAccountNumber(bankAccNumber);
                        }
                    }

                    index = line.indexOfKeyword(LineANZ.STATEMENT_PERIOD_KEYWORD);
                    if( index != -1 && line.getWordCount() > index + 1){
                        WordANZ word = line.getWord(index+1);
                        //System.out.println(word.getText());
                        LocalDate starDate = word.getPeriodStartDate();
                        LocalDate endDate = word.getPeriodEndDate();
                        if( starDate != null && endDate != null ){
                            statement.setStartDate(starDate);
                            statement.setEndDate(endDate);
                        }
                    }

                    index = line.indexOfKeyword(LineANZ.CLOSING_BALANCE_KEYWORD);
                    if( index != -1 ){

                    }
                }

                int count = line.getWordCount();
                if( count == LineANZ.TRANSACTION_PAGE_HEADER_WORDS.length){
                    transactionStarted = true;
                    for(int wordIndex = 0; wordIndex < count; wordIndex++){
                        WordANZ word = line.getWord(wordIndex);
                        if( !word.getText().equalsIgnoreCase(LineANZ.TRANSACTION_PAGE_HEADER_WORDS[wordIndex])){
                            transactionStarted = false;;
                        }
                    }
                }
            }else{
                if( line.indexOfKeyword(LineANZ.TRANSACTION_PAGE_FOOTER_KEYWORDS) != -1){
                    transactionFinished = true;
                    continue;
                }

                if( transactionFinished ) {
                    int index = line.indexOfKeyword(LineANZ.TRANSACTION_STATEMENT_FOOTER_KEYWORDS);
                    if (index != -1) {
                        if (line.getWordCount() > index + 3) {
                            WordANZ word = line.getWord(index + 3);
                            Double doubleObj = word.getCurrencyValue();
                            if (doubleObj != null) {
                                statement.setClosingBalance(doubleObj.doubleValue());
                            }
                        }
                    }
                    break;
                }

                int index = line.indexOfKeyword(LineANZ.TRANSACTION_STATEMENT_SUB_HEADER_KEYWORDS);
                if( index != -1){
                    if( line.getWordCount() > index +1 ){
                        WordANZ word = line.getWord(index +1);
                        Double doubleObj = word.getCurrencyValue();
                        if( doubleObj != null ){
                            statement.setOpeningBalance(doubleObj.doubleValue());
                        }
                    }
                    continue;
                }
                if( line.indexOfKeyword(LineANZ.TRANSACTION_PAGE_SUB_HEADER_KEYWORDS) != -1){
                    continue;
                }

                if(line.getWordCount() == 0 ){
                    continue;
                }

                WordANZ dateWord = line.getDateWord(orientation);
                WordANZ typeWord = line.getTypeWord(orientation);
                WordANZ thirdPartyWord = line.getThirdPartyWord(orientation);

                String thirdParty = "";
                if( thirdPartyWord != null ){
                    thirdParty = thirdPartyWord.getText();
                }
                if( dateWord == null && typeWord == null){
                    ANZTransaction transaction = (ANZTransaction)stmtPage.getLastTransaction();
                    transaction.getPdfVCoordinate().setTransactionBottomY(line.getBottomY());
                    if( line.isInterestSubLine(orientation)) {
                        continue;
                    }
                    if( thirdPartyWord != null ){
                        transaction.setThirdParty(transaction.getThirdParty() + " " + thirdParty);
                        continue;
                    }
                }

                //System.out.println(dateWord.getText() + " Start Y " + dateWord.getStartY());

                LocalDate startDate = statement.getStartDate();
                LocalDate endDate = statement.getEndDate();
                int startYear = startDate.getYear();
                int endYear = endDate.getYear();
                LocalDate currentDate = null;
                try {
                    currentDate = ANZUtils.DATE_MONTH_YEAR_FORMATTER.parse(dateWord.getText() + " " + startYear, LocalDate::from);
                    if (currentDate.isBefore(startDate) || currentDate.isAfter(endDate)) {
                        currentDate = ANZUtils.DATE_MONTH_YEAR_FORMATTER.parse(dateWord.getText() + " " + endYear, LocalDate::from);
                    }
                }catch(Exception exp){
                    //exp.printStackTrace();
                    //System.out.println(pageNumber + " " + line.getLineNumber() + " " + line.getWordCount());
                    continue;
                }

                ANZTransaction.ANZTransactionType ANZTransactionType = ANZTransaction.ANZTransactionType.OTHER;
                if( typeWord == null ){
                    if( thirdPartyWord != null ){
                        if( thirdPartyWord.getText().endsWith(" INTEREST") ) {
                            ANZTransactionType = ANZTransaction.ANZTransactionType.INTEREST;
                        }else if( thirdPartyWord.getText().contains(" FEE") ){
                            ANZTransactionType = ANZTransaction.ANZTransactionType.ANZ;
                        }else if( thirdPartyWord.getText().contains("WITHHOLDING TAX PAID") ){
                            ANZTransactionType = ANZTransaction.ANZTransactionType.WITHHOLDING_TAX;
                        }
                    }
                }else{
                    try{
                        ANZTransactionType = ANZTransaction.ANZTransactionType.valueOf(typeWord.getText().toUpperCase());
                    }catch(Exception exp){
                        exp.printStackTrace();
                    }
                }
                String code = "", reference = "", particular = "";
                double amount = 0, balance = 0;

                WordANZ codeWord = line.getCodeWord(orientation);
                if( codeWord != null ){
                    code = codeWord.getText();
                }
                WordANZ particularWord = line.getParticularWord(orientation);
                if( particularWord != null ){
                    particular = particularWord.getText();
                }
                WordANZ referWord = line.getReferenceWord(orientation);
                if( referWord != null ){
                    reference = referWord.getText();
                }
                WordANZ withdrawWord = line.getWithdrawWord(orientation);
                WordANZ depositWord = line.getDepositWord(orientation);
                WordANZ balanceWord = line.getBalanceWord(orientation);
                if( withdrawWord != null ){
                    amount = Utils.getCurrencyValueObj("-" + withdrawWord.getText()).doubleValue();
                }
                if( depositWord != null ){
                    amount = Utils.getCurrencyValueObj(depositWord.getText()).doubleValue();
                }
                if( withdrawWord == null && depositWord == null ){
                    System.out.println(line.getWord(line.getWordCount()-2).getEndX());
                }
                if( withdrawWord != null && depositWord != null ){
                    System.out.println(line.getWord(line.getWordCount()-2).getEndX());
                }

                if( balanceWord != null ){
                    balance = Utils.getCurrencyValueObj(balanceWord.getText()).doubleValue();
                }else {
                    System.out.println(line.getWord(line.getWordCount() - 1).getEndX());
                }

                if( orientation == PdfPageOrientation.PORTRAIT ){
                    if( thirdPartyWord != null ){
                        double thirdPartyEndX = thirdPartyWord.getEndX();
                        if( pdfHCoordinate.getReferenceEndX() < thirdPartyEndX ){
                            pdfHCoordinate.setReferenceEndX(thirdPartyEndX);
                        }
                    }
                }else{
                    if( referWord != null ){
                        if( pdfHCoordinate.getReferenceEndX() < referWord.getEndX()){
                            pdfHCoordinate.setReferenceEndX(referWord.getEndX());
                        }
                    }
                }

                if( withdrawWord != null ){
                    if( pdfHCoordinate.getWithdrawStartX() > withdrawWord.getStartX() ){
                        pdfHCoordinate.setWithdrawStartX(withdrawWord.getStartX());
                    }
                }
                if( depositWord != null ){
                    if( pdfHCoordinate.getDepositStartX() > depositWord.getStartX() ){
                        pdfHCoordinate.setDepositStartX(depositWord.getStartX());
                    }
                }
                if( balanceWord != null ){
                    if( pdfHCoordinate.getBalanceStartX() > balanceWord.getStartX()){
                        pdfHCoordinate.setBalanceStartX(balanceWord.getStartX());
                    }
                }

                //System.out.println(dateWord.getText() + " Start Y " + line.getStartY() + "--" + line.getEndY());
                VerticalCoordinate verticalCoordinate = new VerticalCoordinate(pageNumber, line.getTopY(), line.getBottomY(), line.getTextHeight(), orientation);
                ANZTransaction transaction = new ANZTransaction(stmtPage.getStatementPageNumber(), statement.getLastTransactionIndex() + 1, currentDate, ANZTransactionType, thirdParty, code, particular, reference, amount, balance, verticalCoordinate);
                stmtPage.addTransaction(transaction);
            }
        }
    }
}
