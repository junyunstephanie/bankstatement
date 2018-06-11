package nz.co.oneforallsoftware.bankstatement.statement;

import java.util.ArrayList;

public class StatementPage {
    protected int statementPageNumber;


    protected ArrayList<Transaction> transactions = new ArrayList<>();
    public StatementPage(int statementPageNumber){
        this.statementPageNumber = statementPageNumber;
    }


    public int getStatementPageNumber() {
        return statementPageNumber;
    }

    public int getTransactionCount(){
        return transactions.size();
    }

    public Transaction getTransaction(int index){
        if( index < 0 || index >= transactions.size() ){
            return null;
        }
        return transactions.get(index);
    }

    public void addTransaction(Transaction transaction){
        transactions.add(transaction);
    }

    public Transaction getLastTransaction(){
        if( transactions.size() == 0 ){
            return null;
        }

        return transactions.get(transactions.size()-1);
    }

    public double getTotalDeposit(){
        double deposit = 0;
        for(Transaction transaction: transactions){
            if( transaction.getAmount() > 0 ){
                deposit = deposit + transaction.getAmount();
            }
        }
        return deposit;
    }

    public double getTotalWithdraw(){
        double withdraw = 0;
        for(Transaction transaction: transactions){
            if(transaction.getAmount() < 0 ){
                withdraw = withdraw - transaction.getAmount();
            }
        }
        return withdraw;
    }

    public double getTotalDeposit(boolean gstIncl){
        double deposit = 0;
        for(Transaction transaction: transactions){
            if( transaction.getAmount() > 0 && transaction.isGstIncl() == gstIncl ){
                deposit = deposit + transaction.getAmount();
            }
        }
        return deposit;
    }

    public double getTotalWithdraw(boolean gstIncl){
        double withdraw = 0;
        for(Transaction transaction: transactions){
            if(transaction.getAmount() < 0 && transaction.isGstIncl() == gstIncl){
                withdraw = withdraw - transaction.getAmount();
            }
        }
        return withdraw;
    }
}
