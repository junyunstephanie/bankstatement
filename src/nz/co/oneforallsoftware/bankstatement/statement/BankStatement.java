package nz.co.oneforallsoftware.bankstatement.statement;

import nz.co.oneforallsoftware.bankstatement.anz.ANZPage;

import java.time.LocalDate;
import java.util.ArrayList;

public class BankStatement {
    protected String fileName;
    protected String accountNumber;
    protected LocalDate startDate, endDate;
    protected String accountName;

    protected ArrayList<StatementPage> pages = new ArrayList<>();

    protected double openingBalance, closingBalance;

    public BankStatement(String filePath){
        fileName = filePath;
    }

    public void setAccountNumber(String accountNumber) {
        this.accountNumber = accountNumber;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getAccountNumber() {
        return accountNumber;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public String getFileName() {
        return fileName;
    }


    public double getOpeningBalance() {
        return openingBalance;
    }

    public void setOpeningBalance(double openingBalance) {
        this.openingBalance = openingBalance;
    }

    public double getClosingBalance() {
        return closingBalance;
    }

    public void setClosingBalance(double closingBalance) {
        this.closingBalance = closingBalance;
    }


    public int getPageCount(){
        return pages.size();
    }

    public StatementPage getPage(int index){
        if( index < 0 || index >= pages.size()){
            return null;
        }
        return pages.get(index);
    }

    public StatementPage getLastPage(){
        if( pages.size() == 0 ){
            return null;
        }

        return pages.get(pages.size()-1);
    }

    public void addPage(StatementPage page){
        pages.add(page);
    }

    public double getTotalDeposit(){
        double deposit = 0;
        for(StatementPage page: pages){
            deposit = deposit + page.getTotalDeposit();
        }
        return deposit;
    }

    public double getTotalWithdraw(){
        double withdraw = 0;
        for(StatementPage page: pages){
            withdraw = withdraw + page.getTotalWithdraw();
        }
        return withdraw;
    }

    public double getTotalDeposit(boolean gstIncl){
        double deposit = 0;
        for(StatementPage page: pages){
            deposit = deposit + page.getTotalDeposit(gstIncl);
        }
        return deposit;
    }

    public double getTotalWithdraw(boolean gstIncl){
        double withdraw = 0;
        for(StatementPage page: pages){
            withdraw = withdraw + page.getTotalWithdraw(gstIncl);
        }
        return withdraw;
    }

    public Transaction getTransactionByTransactionIndex(int transactionIndex){
        for(StatementPage statementPage: pages){
            int transactionCount = statementPage.getTransactionCount();
            for(int index = 0; index < transactionCount; index++){
                Transaction transaction = statementPage.getTransaction(index);
                if( transaction.getTransactionIndex() == transactionIndex){
                    return transaction;
                }
            }
        }
        return null;
    }
}
