package nz.co.oneforallsoftware.bankstatement.statement;

import org.apache.fontbox.ttf.GlyphSubstitutionTable;

public class TransactionNote {
    private Transaction.TransactionType transactionType;
    private String note, description;
    private Transaction.GSTStatus gstStatus;
    private String transactionRuleId;

    public TransactionNote(String note, Transaction.TransactionType transactionType, Transaction.GSTStatus gstStatus){
        if(note == null ){
            this.note = "";
        }else {
            this.note = note;
        }
        this.transactionType = transactionType;
        this.gstStatus = gstStatus;
        description = "";
    }

    public Transaction.TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Transaction.TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Transaction.GSTStatus getGstStatus() {
        return gstStatus;
    }

    public void setGstStatus(Transaction.GSTStatus gstStatus) {
        this.gstStatus = gstStatus;
    }

    public String getTransactionRuleId() {
        return transactionRuleId;
    }

    public void setTransactionRuleId(String transactionRuleId) {
        this.transactionRuleId = transactionRuleId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
