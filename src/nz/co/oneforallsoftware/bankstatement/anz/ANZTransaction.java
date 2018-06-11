package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.statement.Transaction;

import java.time.LocalDate;

public class ANZTransaction extends Transaction{
    public static enum ANZTransactionType {
        ANZ ("ANZ Bank Fee"),
        INTEREST ("Interest Paid"),
        WITHHOLDING_TAX ("Withholding Tax Deducted"),
        OTHER ("Other Transaction ANZTransactionType"),
        AP ("Automatic Payment"),
        BP ("Bill Payment"),
        DC ("Direct Credit"),
        FX ("Foreign Exchange"),
        IP ("International EFTPOS Transaction"),
        AT ("Automatic Teller Machine"),
        CQ ("Cheque/Withdrawal"),
        DD ("Direct Debit"),
        EP ("EFTPOS Transaction"),
        IA ("International Money Machine"),
        VT ("Visa Transaction"),
        ED ("Electronic Dishonour");

        ANZTransactionType(String description){
            this.description = description;
        }

        private String description;

        public String getDescription(){
            return description;
        }

        public String toString(){
            return name() + " (" + description + ")";
        }
    }

    private ANZTransactionType ANZTransactionType;
    private VerticalCoordinate pdfVCoordinate;
    private String note;

    protected ANZTransaction(int statementPageNumber, int transactionIndex, LocalDate date, ANZTransactionType ANZTransactionType, String thirdParty, String code, String particulars, String reference, double amount, double balance, VerticalCoordinate pdfVCoordinate){
        super(statementPageNumber, transactionIndex, date, thirdParty, code, particulars, reference, amount, balance );
        this.pdfVCoordinate = pdfVCoordinate;
        this.ANZTransactionType = ANZTransactionType;
        note = "";
    }

    public VerticalCoordinate getPdfVCoordinate() {
        return pdfVCoordinate;
    }

    public ANZTransactionType getANZTransactionType(){
        return ANZTransactionType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public int hashCode(){
        int hashCode = statementPageNumber + transactionIndex * 7 + date.hashCode();
        return hashCode;
    }
    public boolean equals(Object obj){
        if( obj == null || !(obj instanceof ANZTransaction)){
            return false;
        }

        ANZTransaction transaction = (ANZTransaction)obj;

        return statementPageNumber == transaction.statementPageNumber &&
                transactionIndex == transaction.transactionIndex &&
                date.isEqual(transaction.date) &&
                ANZTransactionType == transaction.ANZTransactionType &&
                amount == transaction.amount &&
                balance == transaction.balance;
    }
}
