package nz.co.oneforallsoftware.bankstatement.statement;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Transaction {
    public static enum GSTStatus{
        INCLUSIVE("GST Incl."),
        EXCLUSIVE("GST Excl."),
        UNKNOWN("Unknown");

        GSTStatus(String description){
            this.description = description;
        }
        private String description;

        public String toString(){
            return description;
        }
    }
    public static enum TransactionType {
        DEPOSIT_UNSPECIFIED("Deposit Unspecified"),
        WITHDRAW_UNSPECIFIED("Withdraw Unspecified");

        TransactionType(String description){
            this.description = description;
        }
        private String description;

        public String toString(){
            return description;
        }

        public boolean isDeposit(){
            if(description.trim().equalsIgnoreCase(DEPOSIT_UNSPECIFIED.description)){
                return true;
            }else if(description.trim().equalsIgnoreCase(DEPOSIT_UNSPECIFIED.description)){
                return true;
            }
            return false;
        }
    }

    protected LocalDate date;
    protected double amount;
    protected String thirdParty, reference, particulars, code;
    protected double balance;
    protected boolean gstIncl;
    protected String note;
    protected String shareLink;

    protected TransactionType transactionType;

    protected int statementPageNumber, transactionIndex;
    protected String noteRuleId;

    protected TransactionNote transactionNote;

    protected Transaction(int statementPageNumber, int transactionIndex, LocalDate date, String thirdParty, String code, String particulars, String reference, double amount, double balance){
        this.date = date;
        this.thirdParty = thirdParty;
        this.reference = reference;
        this.code = code;
        this.particulars = particulars;
        this.amount = amount;
        if(amount < 0 ){
            transactionType = TransactionType.WITHDRAW_UNSPECIFIED;
        }else{
            transactionType = TransactionType.DEPOSIT_UNSPECIFIED;
        }
        this.balance = balance;
        gstIncl = true;
        note = "";
        this.statementPageNumber = statementPageNumber;
        this.transactionIndex = transactionIndex;
        shareLink = "";
        noteRuleId = "";
        transactionNote = null;
    }

    public TransactionNote getTransactionNote() {
        return transactionNote;
    }

    public void setTransactionNote(TransactionNote transactionNote) {
        this.transactionNote = transactionNote;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public int getStatementPageNumber() {
        return statementPageNumber;
    }

    public int getTransactionIndex() {
        return transactionIndex;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public LocalDate getDate() {
        return date;
    }

    public double getAmount() {
        return amount;
    }

    public String getThirdParty() {
        return thirdParty;
    }
    public void setThirdParty(String thirdParty){
        this.thirdParty = thirdParty;
    }

    public String getReference() {
        return reference;
    }

    public String getParticulars() {
        return particulars;
    }

    public String getCode() {
        return code;
    }

    public double getBalance() {
        return balance;
    }

    public boolean isGstIncl() {
        return gstIncl;
    }

    public void setGstIncl(boolean gstIncl) {
        this.gstIncl = gstIncl;
    }

    public String toString(){
        String text = DateTimeFormatter.ofPattern("dd MMM").format(date) + " " + amount + " " + balance;
        return text;
    }

    public String getShareLink() {
        return shareLink;
    }

    public void setShareLink(String shareLink) {
        this.shareLink = shareLink;
    }

    public String getNoteRuleId() {
        return noteRuleId;
    }

    public void setNoteRuleId(String noteRuleId) {
        this.noteRuleId = noteRuleId;
    }
}
