package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.statement.BankStatement;
import nz.co.oneforallsoftware.bankstatement.statement.StatementPage;

import java.time.LocalDate;
import java.util.ArrayList;

public class ANZStatement extends BankStatement{

    private int pdfDocumentStartPageNumber;

    private String version;



    public ANZStatement(String fileName, int pdfDocumentStartPageNumber){
        super(fileName);
        this.pdfDocumentStartPageNumber = pdfDocumentStartPageNumber;
    }


    public int getPdfDocumentStartPageNumber() {
        return pdfDocumentStartPageNumber;
    }


    public String getVersion() {
        return version;
    }

    protected void setVersion(String version) {
        this.version = version;
    }

    public int getPageCount(){
        return pages.size();
    }

    public int getLastTransactionIndex(){
        int transactionIndex = -1;
        for(int pageIndex = pages.size() - 1; pageIndex > 0; pageIndex--){
            ANZPage anzPage = (ANZPage)pages.get(pageIndex);
            int transactionCount = anzPage.getTransactionCount();
            if(transactionCount == 0 ){
                continue;
            }else{
                ANZTransaction anzTransaction = (ANZTransaction)anzPage.getTransaction(transactionCount-1);
                transactionIndex = anzTransaction.getTransactionIndex();
                break;
            }
        }
        return transactionIndex;
    }

    public void addPage(StatementPage page){
        if( page == null || !(page instanceof ANZPage)){
            return;
        }else {
            super.addPage(page);
        }
    }

    public String toString(){
        String text = "Account Number " + accountNumber + "\n" +
                "PDF " + fileName + "\n" +
                "Start Page Number " + pdfDocumentStartPageNumber + "\n" +
                "Version " + version + "\n" +
                "Opening Balance " + openingBalance + "\n" +
                "Closing Balance " + closingBalance + "\n" +
                "Start Date " + ANZUtils.DATE_MONTH_YEAR_FORMATTER.format(startDate) + "\n" +
                "End Date " + ANZUtils.DATE_MONTH_YEAR_FORMATTER.format(endDate) + "\n" +
                "Number Of Page " + pages.size();
         return text;
    }
}
