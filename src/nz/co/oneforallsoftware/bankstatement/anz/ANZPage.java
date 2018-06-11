package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.statement.StatementPage;
import nz.co.oneforallsoftware.bankstatement.statement.Transaction;

import java.util.ArrayList;

public class ANZPage extends StatementPage{
    private static final double DEFAULT_LINE_PADDING = 3.595001220703125;
    private int pdfDocumentPageNumber;
    private HorizonCoordinate pdfPageHCoordinate;
    private double linePadding;

    public ANZPage(int statementPageNumber, int pdfDocumentPageNumber, HorizonCoordinate pdfPageHCoordinate){
        super(statementPageNumber);
        this.pdfDocumentPageNumber = pdfDocumentPageNumber;
        this.pdfPageHCoordinate = pdfPageHCoordinate;
        linePadding = DEFAULT_LINE_PADDING;
    }

    public int getPdfDocumentPageNumber() {
        return pdfDocumentPageNumber;
    }

    public void addTransaction(Transaction transaction){
        if (transaction == null || !(transaction instanceof ANZTransaction)) {
            return;
        }
        transactions.add(transaction);
    }

    public double getLinePadding() {
        return linePadding;
    }

    public void setLinePadding(double linePadding) {
        this.linePadding = linePadding;
    }

    public HorizonCoordinate getPdfPageHCoordinate() {
        return pdfPageHCoordinate;
    }
}
