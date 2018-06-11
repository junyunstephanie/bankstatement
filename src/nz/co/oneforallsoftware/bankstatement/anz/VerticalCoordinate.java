package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.Utils;

public class VerticalCoordinate {
    private int pageNumber;
    private double topY, bottomY, textHeight, transactionBottomY;
    private Utils.PdfPageOrientation pageOrientation;

    public VerticalCoordinate(int pageNumber, double topY, double bottomY, double textHeight, Utils.PdfPageOrientation pageOrientation){
        this.pageNumber = pageNumber;
        this.topY = topY;
        this.bottomY = bottomY;
        transactionBottomY = bottomY;
        this.pageOrientation = pageOrientation;
        this.textHeight = textHeight;
    }

    public double getTransactionBottomY() {
        return transactionBottomY;
    }

    public void setTransactionBottomY(double transactionBottomY) {
        this.transactionBottomY = transactionBottomY;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public double getTopY() {
        return topY;
    }

    public double getBottomY() {
        return bottomY;
    }

    public void setBottomY(double bottomY) {
        this.bottomY = bottomY;
    }

    public double getTextHeight() {
        return textHeight;
    }

    public Utils.PdfPageOrientation getPageOrientation() {
        return pageOrientation;
    }
}