package nz.co.oneforallsoftware.bankstatement.rules;

import nz.co.oneforallsoftware.bankstatement.statement.BankAccountNumber;
import nz.co.oneforallsoftware.bankstatement.statement.Transaction;

import java.util.ArrayList;
import java.util.UUID;

public abstract class Rule {
    protected String id;
    protected BankAccountNumber accountNumber;
    protected ArrayList<RuleField> ruleFields = new ArrayList<>();
    protected String resultNote;
    protected boolean gstIncl, deposit;
    protected Transaction.TransactionType transactionType;

    public Rule(BankAccountNumber accountNumber, String id){
        this.accountNumber = accountNumber;
        this.id = id;
        deposit = false;
        transactionType = Transaction.TransactionType.WITHDRAW_UNSPECIFIED;
    }

    public Rule(BankAccountNumber accountNumber){
        this.accountNumber = accountNumber;
        id = UUID.randomUUID().toString();
        deposit = false;
    }

    public boolean isDeposit() {
        return deposit;
    }

    public void setDeposit(boolean deposit) {
        this.deposit = deposit;
        if( deposit ){
            transactionType = Transaction.TransactionType.DEPOSIT_UNSPECIFIED;
        }else{
            transactionType = Transaction.TransactionType.WITHDRAW_UNSPECIFIED;
        }
    }

    public String getId(){
        return id;
    }

    public int getRuleFieldCount(){
        return ruleFields.size();
    }

    public RuleField getRuleField(int index){
        if( index < 0 || index > ruleFields.size() - 1 ){
            return null;
        }

        return ruleFields.get(index);
    }

    public void addRuleField(RuleField ruleField){
        if( !ruleFields.contains(ruleField)){
            ruleFields.add(ruleField);
        }
    }

    public Transaction.TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(Transaction.TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public void removeRuleField(RuleField ruleField){
        ruleFields.remove(ruleField);
    }

    public String getResultNote() {
        return resultNote;
    }

    public void setResultNote(String resultNote) {
        this.resultNote = resultNote;
    }

    public boolean isGstIncl() {
        return gstIncl;
    }

    public void setGstIncl(boolean gstIncl) {
        this.gstIncl = gstIncl;
    }

    public BankAccountNumber getAccountNumber() {
        return accountNumber;
    }

    public int hashCode(){
        return id.hashCode() * 3 + accountNumber.hashCode() * 7;
    }

    public boolean equals(Object obj){
        if( obj == null || !(obj instanceof Rule)){
            return false;
        }

        Rule rule = (Rule)obj;

        return id.equals(rule.id) && accountNumber.equals(rule.accountNumber);
    }

    public abstract  boolean isRuleFollowed(Transaction transaction);
}
