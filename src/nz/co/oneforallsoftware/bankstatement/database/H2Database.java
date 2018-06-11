package nz.co.oneforallsoftware.bankstatement.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;

import nz.co.oneforallsoftware.bankstatement.statement.BankAccountNumber;

import nz.co.oneforallsoftware.bankstatement.anz.*;
import nz.co.oneforallsoftware.bankstatement.rules.RuleField;
import nz.co.oneforallsoftware.bankstatement.rules.RuleItem;
import nz.co.oneforallsoftware.bankstatement.rules.RuleItemComparator;
import nz.co.oneforallsoftware.bankstatement.statement.Transaction;

public class H2Database {
    private static final String DB_NAME = "bank_statement_h2";
    private static final String DEFAULT_DB_DIR = "./data/";

    private Connection conn;

    private String sql;
    private PreparedStatement stmt;
    private ResultSet rs;

    public H2Database() throws Exception{
        File file = new File(DEFAULT_DB_DIR);
        if( !file.exists() || !file.isDirectory()){
            if( !file.mkdirs() ){
                throw new Exception("Failed to create database directory");
            }
        }
        String dbUrl = "jdbc:h2:file:" + DEFAULT_DB_DIR + DB_NAME;
        try {
            Class.forName("org.h2.Driver");
            conn = DriverManager.getConnection(dbUrl, "", "");

            createTables();
        }catch(Exception exp){
            exp.printStackTrace();
            throw exp;
        }
    }

    private void createTables()throws Exception{
        sql = H2DatabaseConstants.CREATE_ANZ_RULE_TABLE;
        stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();

        sql = H2DatabaseConstants.CREATE_ANZ_RULE_FIELD_TABLE;
        stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();

        sql = H2DatabaseConstants.CREATE_ANZ_TRANSACTION_NOTE_TABLE;
        stmt = conn.prepareStatement(sql);
        stmt.executeUpdate();
        stmt.close();
    }

    public ArrayList<ANZTransactionRule> readANZTransactionRules(BankAccountNumber accountNumber)throws Exception{
        try{
            ArrayList<ANZTransactionRule> transactionRules = new ArrayList<>();
            sql = "SELECT * FROM " + H2DatabaseConstants.ANZ_RULE_TABLE +
                    " WHERE " +
                    H2DatabaseConstants.BANK_ACCOUNT_NUMBER + "=?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, accountNumber.toString());
            rs = stmt.executeQuery();
            while(rs.next()){
                String id = rs.getString(H2DatabaseConstants.ID);
                ANZTransactionRule transactionRule = new ANZTransactionRule(accountNumber, id);

                String text = rs.getString(H2DatabaseConstants.RESULT_NOTE);
                transactionRule.setResultNote(text);
                boolean gstIncl = rs.getBoolean(H2DatabaseConstants.IS_GST_INCL);
                transactionRule.setGstIncl(gstIncl);
                boolean deposit = rs.getBoolean(H2DatabaseConstants.IS_DEPOSIT);
                transactionRule.setDeposit(deposit);
                Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(rs.getString(H2DatabaseConstants.TRANSACTION_TYPE));
                transactionRule.setTransactionType(transactionType);
                transactionRules.add(transactionRule);
            }
            rs.close();

            for(ANZTransactionRule transactionRule: transactionRules) {
                sql = "SELECT * FROM " + H2DatabaseConstants.ANZ_RULE_FIELD_TABLE +
                        " WHERE " +
                        H2DatabaseConstants.ID + "=?";
                stmt = conn.prepareStatement(sql);
                stmt.setString(1, transactionRule.getId());
                rs = stmt.executeQuery();
                while (rs.next()) {
                    String text = rs.getString(H2DatabaseConstants.CHECK_TEXT);
                    RuleItemComparator comparator = RuleItemComparator.valueOf(rs.getString(H2DatabaseConstants.COMPARATOR));
                    String fieldName = rs.getString(H2DatabaseConstants.FIELD_NAME);

                    RuleItem ruleItem = new RuleItem(text, comparator);

                    RuleField ruleField = null;

                    int count = transactionRule.getRuleFieldCount();
                    for (int index = 0; index < count; index++) {
                        RuleField field = transactionRule.getRuleField(index);
                        if (field.getFieldName().equalsIgnoreCase(fieldName)) {
                            ruleField = field;
                            break;
                        }
                    }
                    if (ruleField == null) {
                        ruleField = new RuleField(fieldName);
                        transactionRule.addRuleField(ruleField);
                    }

                    ruleField.addRuleItem(ruleItem);
                }
                rs.close();
            }

            return transactionRules;
        }catch(Exception exp){
            exp.printStackTrace();
            throw exp;
        }
    }

    public void saveNewANZTransactionRule(ANZTransactionRule transactionRule)throws Exception {
        try{
            conn.setAutoCommit(false);
            stmt = conn.prepareStatement(H2DatabaseConstants.INSERT_ANZ_RULE_TABLE);
            stmt.setString(1, transactionRule.getId());
            stmt.setString(2, transactionRule.getAccountNumber().toString());
            stmt.setBoolean(3, transactionRule.isDeposit());
            stmt.setString(5, transactionRule.getResultNote());
            stmt.setBoolean(4, transactionRule.isGstIncl());
            stmt.setString(6, transactionRule.getTransactionType().name());
            stmt.executeUpdate();

            stmt = conn.prepareStatement(H2DatabaseConstants.INSERT_ANZ_RULE_FIELD_TABLE);
            int ruleFieldCount = transactionRule.getRuleFieldCount();
            for(int ruleFieldIndex = 0; ruleFieldIndex < ruleFieldCount; ruleFieldIndex++){
                RuleField ruleField = transactionRule.getRuleField(ruleFieldIndex);
                int itemCount = ruleField.getRuleItemCount();
                for(int itemIndex = 0; itemIndex < itemCount; itemIndex++){
                    RuleItem ruleItem = ruleField.getRuleItem(itemIndex);
                    stmt.clearParameters();
                    stmt.setString(1, transactionRule.getId());
                    stmt.setString(2, ruleField.getFieldName());
                    stmt.setString(3, ruleItem.getCheckAgainstText());
                    stmt.setString(4, ruleItem.getComparator().name());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        }catch(Exception exp){
            exp.printStackTrace();
            throw exp;
        }finally{
            try{
                stmt.close();
            }catch(Exception e){

            }
            conn.setAutoCommit(true);
        }
    }

    public void updateANZTransactionRule(ANZTransactionRule transactionRule) throws Exception{
        try{
            conn.setAutoCommit(false);
            sql = "DELETE FROM " +H2DatabaseConstants.ANZ_RULE_TABLE +
                    " WHERE " +
                    H2DatabaseConstants.BANK_ACCOUNT_NUMBER + "=?" +
                    " AND " +
                    H2DatabaseConstants.ID + "=?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, transactionRule.getAccountNumber().toString());
            stmt.setString(2, transactionRule.getId());
            stmt.executeUpdate();

            stmt = conn.prepareStatement(H2DatabaseConstants.INSERT_ANZ_RULE_TABLE);
            stmt.clearParameters();
            stmt.setString(1, transactionRule.getId());
            stmt.setString(2, transactionRule.getAccountNumber().toString());
            stmt.setBoolean(3, transactionRule.isDeposit());
            stmt.setString(5, transactionRule.getResultNote());
            stmt.setBoolean(4, transactionRule.isGstIncl());
            stmt.setString(6, transactionRule.getTransactionType().name());
            stmt.executeUpdate();

            stmt = conn.prepareStatement(H2DatabaseConstants.INSERT_ANZ_RULE_FIELD_TABLE);
            int ruleFieldCount = transactionRule.getRuleFieldCount();
            for(int ruleFieldIndex = 0; ruleFieldIndex < ruleFieldCount; ruleFieldIndex++){
                RuleField ruleField = transactionRule.getRuleField(ruleFieldIndex);
                int itemCount = ruleField.getRuleItemCount();
                for(int itemIndex = 0; itemIndex < itemCount; itemIndex++){
                    RuleItem ruleItem = ruleField.getRuleItem(itemIndex);
                    stmt.clearParameters();
                    stmt.setString(1, transactionRule.getId());
                    stmt.setString(2, ruleField.getFieldName());
                    stmt.setString(3, ruleItem.getCheckAgainstText());
                    stmt.setString(4, ruleItem.getComparator().name());
                    stmt.executeUpdate();
                }
            }

            conn.commit();
        }catch(Exception exp){
            exp.printStackTrace();
            throw exp;
        }finally{
            try{
                stmt.close();
            }catch(Exception e){

            }
            conn.setAutoCommit(true);
        }
    }

    public void readANZTransactionNotes(ANZStatement statement) throws Exception{
        try{
            sql = "SELECT " +
                    H2DatabaseConstants.INDEX + "," + H2DatabaseConstants.NOTE + "," +
                    H2DatabaseConstants.IS_GST_INCL + "," + H2DatabaseConstants.HYPERLINK + "," +
                    H2DatabaseConstants.TRANSACTION_TYPE + "," + H2DatabaseConstants.ID +

                    " FROM " + H2DatabaseConstants.ANZ_TRANSACTION_NOTE_TABLE +

                    " WHERE " +
                    H2DatabaseConstants.BANK_ACCOUNT_NUMBER + "=?" +
                    " AND " +
                    H2DatabaseConstants.START_DATE + "=?";

            stmt = conn.prepareStatement(sql);
            stmt.setString(1, statement.getAccountNumber().toString());
            stmt.setInt(2, ANZUtils.getIntValueOfLocalDate(statement.getStartDate()));

            rs = stmt.executeQuery();
            while(rs.next()){
                int transactionIndex = rs.getInt(H2DatabaseConstants.INDEX);
                String note = rs.getString(H2DatabaseConstants.NOTE);
                boolean isGstIncl = rs.getBoolean(H2DatabaseConstants.IS_GST_INCL);
                String hyperLink = rs.getString(H2DatabaseConstants.HYPERLINK);
                String noteRuleId = rs.getString(H2DatabaseConstants.ID);
                Transaction.TransactionType transactionType = Transaction.TransactionType.valueOf(rs.getString(H2DatabaseConstants.TRANSACTION_TYPE));
                ANZTransaction transaction = (ANZTransaction)statement.getTransactionByTransactionIndex(transactionIndex);
                transaction.setNote(note);
                transaction.setGstIncl(isGstIncl);
                transaction.setShareLink(hyperLink);
                transaction.setTransactionType(transactionType);
                transaction.setNoteRuleId(noteRuleId);
            }
            rs.close();
        }catch(Exception exp){
            exp.printStackTrace();
            throw exp;
        }
    }

    public void saveANZTransactionNote(ANZStatement statement, ANZTransaction transaction) throws Exception{
        try{
            stmt = conn.prepareStatement(H2DatabaseConstants.MERGE_ANZ_TRANSACTION_NOTE_TABLE);
            stmt.setString(1, statement.getAccountNumber().toString());
            stmt.setInt(2, ANZUtils.getIntValueOfLocalDate(statement.getStartDate()));
            stmt.setInt(3, transaction.getTransactionIndex());
            stmt.setInt(4, ANZUtils.getIntValueOfLocalDate(transaction.getDate()));
            stmt.setString(5, transaction.getANZTransactionType().name());
            stmt.setString(6, transaction.getThirdParty()==null?"":transaction.getThirdParty());
            stmt.setString(7, transaction.getCode()==null?"":transaction.getCode());
            stmt.setString(8, transaction.getParticulars()==null?"":transaction.getParticulars());
            stmt.setString(9, transaction.getReference()==null?"":transaction.getReference());
            stmt.setDouble(10, transaction.getAmount());
            stmt.setDouble(11, transaction.getBalance());
            stmt.setString(12, transaction.getNote());
            stmt.setString(13, transaction.getShareLink()==null?"":transaction.getShareLink());
            stmt.setBoolean(14, transaction.isGstIncl());
            stmt.setString(15, transaction.getNoteRuleId());
            stmt.setString(16, transaction.getTransactionType().name());
            stmt.executeUpdate();
        }catch(Exception exp){
            exp.printStackTrace();
            throw exp;
        }
    }

    public void saveANZTransactionNotes(ANZStatement statement)throws Exception{
        try{
            conn.setAutoCommit(false);

            sql = "DELETE FROM " + H2DatabaseConstants.ANZ_TRANSACTION_NOTE_TABLE +
                    " WHERE " +
                    H2DatabaseConstants.BANK_ACCOUNT_NUMBER + "=?" +
                    " AND " +
                    H2DatabaseConstants.START_DATE + "=?";
            stmt = conn.prepareStatement(sql);
            stmt.setString(1, statement.getAccountNumber().toString());
            stmt.setInt(2, ANZUtils.getIntValueOfLocalDate(statement.getStartDate()));
            stmt.executeUpdate();

            stmt = conn.prepareStatement(H2DatabaseConstants.MERGE_ANZ_TRANSACTION_NOTE_TABLE);
            int pageCount = statement.getPageCount();
            for(int pageIndex = 0; pageIndex < pageCount; pageIndex++){
                ANZPage anzPage = (ANZPage)statement.getPage(pageIndex);
                int transactionCount = anzPage.getTransactionCount();
                for(int transactionIndex = 0; transactionIndex < transactionCount; transactionIndex++){
                    ANZTransaction transaction = (ANZTransaction)anzPage.getTransaction(transactionIndex);

                    stmt.setString(1, statement.getAccountNumber().toString());
                    stmt.setInt(2, ANZUtils.getIntValueOfLocalDate(statement.getStartDate()));
                    stmt.setInt(3, transaction.getTransactionIndex());
                    stmt.setInt(4, ANZUtils.getIntValueOfLocalDate(transaction.getDate()));
                    stmt.setString(5, transaction.getANZTransactionType().name());
                    stmt.setString(6, transaction.getThirdParty()==null?"":transaction.getThirdParty());
                    stmt.setString(7, transaction.getCode()==null?"":transaction.getCode());
                    stmt.setString(8, transaction.getParticulars()==null?"":transaction.getParticulars());
                    stmt.setString(9, transaction.getReference()==null?"":transaction.getReference());
                    stmt.setDouble(10, transaction.getAmount());
                    stmt.setDouble(11, transaction.getBalance());
                    stmt.setString(12, transaction.getNote());
                    stmt.setString(13, transaction.getShareLink()==null?"":transaction.getShareLink());
                    stmt.setBoolean(14, transaction.isGstIncl());
                    stmt.setString(15, transaction.getNoteRuleId());
                    stmt.setString(16, transaction.getTransactionType().name());

                    stmt.executeUpdate();
                }
            }

            conn.commit();
        }catch(Exception exp){
            exp.printStackTrace();
            throw exp;
        }finally{
            try{
                stmt.close();
            }catch(Exception e){

            }
            conn.setAutoCommit(true);
        }
    }
}
