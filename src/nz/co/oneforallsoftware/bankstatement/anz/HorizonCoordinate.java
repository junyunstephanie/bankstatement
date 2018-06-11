package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.Utils;

public class HorizonCoordinate {
    private int pageNumber;
    private Utils.PdfPageOrientation orientation;
    private double pageWidth, pageHeight, referenceEndX, depositStartX, depositEndX, withdrawStartX, withdrawEndX, balanceStartX;

    public HorizonCoordinate(int pageNumber, double pageWidth, double pageHeight, Utils.PdfPageOrientation orientation){
        this.pageNumber = pageNumber;
        this.orientation = orientation;
        this.pageWidth = pageWidth;
        this.pageHeight = pageHeight;

        if(orientation == Utils.PdfPageOrientation.PORTRAIT){
            referenceEndX = LineANZ.TRANSACTION_THIRD_PARTY_PORTRAIT_START_X + 10d;
            withdrawEndX = LineANZ.TRANSACTION_WITHDRAW_PORTRAIT_END_X;
            withdrawStartX = withdrawEndX - 10d;
            depositEndX = LineANZ.TRANSACTION_DEPOSIT_PORTRAIT_END_X;
            depositStartX = depositEndX - 10d;
            balanceStartX = LineANZ.TRANSACTION_BALANCE_PORTRAIT_END_X - 10d;
        }else{
            referenceEndX = LineANZ.TRANSACTION_REFERENCE_LANDSCAPE_START_X + 10d;
            withdrawEndX = LineANZ.TRANSACTION_WITHDRAW_LANDSCAPE_END_X;
            withdrawStartX = withdrawEndX - 10d;
            depositEndX = LineANZ.TRANSACTION_DEPOSIT_LANDSCAPE_END_X;
            depositStartX = depositEndX - 10d;
            balanceStartX = LineANZ.TRANSACTION_BALANCE_LANDSCAPE_END_X - 10d;
        }
    }

    public double getReferenceEndX() {
        return referenceEndX;
    }

    public void setReferenceEndX(double referenceEndX) {
        this.referenceEndX = referenceEndX;
    }

    public double getDepositStartX() {
        return depositStartX;
    }

    public void setDepositStartX(double depositStartX) {
        this.depositStartX = depositStartX;
    }

    public double getDepositEndX() {
        return depositEndX;
    }

    public void setDepositEndX(double depositEndX) {
        this.depositEndX = depositEndX;
    }

    public double getWithdrawStartX() {
        return withdrawStartX;
    }

    public void setWithdrawStartX(double withdrawStartX) {
        this.withdrawStartX = withdrawStartX;
    }

    public double getWithdrawEndX() {
        return withdrawEndX;
    }

    public void setWithdrawEndX(double withdrawEndX) {
        this.withdrawEndX = withdrawEndX;
    }

    public double getBalanceStartX() {
        return balanceStartX;
    }

    public void setBalanceStartX(double balanceStartX) {
        this.balanceStartX = balanceStartX;
    }

    public int getPageNumber() {
        return pageNumber;
    }

    public Utils.PdfPageOrientation getOrientation() {
        return orientation;
    }

    public double getPageWidth() {
        return pageWidth;
    }

    public void setPageWidth(double pageWidth) {
        this.pageWidth = pageWidth;
    }

    public double getPageHeight() {
        return pageHeight;
    }

    public void setPageHeight(double pageHeight) {
        this.pageHeight = pageHeight;
    }
}
