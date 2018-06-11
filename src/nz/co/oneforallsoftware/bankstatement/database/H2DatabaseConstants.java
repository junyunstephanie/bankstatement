package nz.co.oneforallsoftware.bankstatement.database;

public class H2DatabaseConstants {
    final static String ANZ_RULE_TABLE = "ANZ_RULE_TABLE";
    final static String ID = "ID";
    final static String IS_DEPOSIT = "IS_DEPOSIT";
    final static String RESULT_NOTE = "RESULT_NOTE";
    final static String IS_GST_INCL = "GST_INCL";
    final static String BANK_ACCOUNT_NUMBER = "BANK_ACCOUNT_NUMBER";
    final static String TRANSACTION_TYPE = "transactionType";

    final static String CREATE_ANZ_RULE_TABLE = "CREATE TABLE IF NOT EXISTS " + ANZ_RULE_TABLE +
            " ( " +
            ID + " VARCHAR(36) NOT NULL," +
            BANK_ACCOUNT_NUMBER  + " VARCHAR(20) NOT NULL," +
            IS_DEPOSIT + " BOOLEAN, " +
            IS_GST_INCL + " BOOLEAN, " +
            RESULT_NOTE + " VARCHAR(128), " +
            TRANSACTION_TYPE + " VARCHAR(128), " +
            " PRIMARY KEY ( " + ID + " )" +
            ")";
    final static String INSERT_ANZ_RULE_TABLE = "INSERT INTO " + ANZ_RULE_TABLE +
            " VALUES (?,?,?,?,?,?)";

    final static String FIELD_NAME = "FIELD_NAME";
    final static String CHECK_TEXT = "CHECK_TEXT";
    final static String COMPARATOR = "COMPARATOR";
    final static String ANZ_RULE_FIELD_TABLE = "ANZ_RULE_FIELD_TABLE";
    final static String CREATE_ANZ_RULE_FIELD_TABLE = "CREATE TABLE IF NOT EXISTS " + ANZ_RULE_FIELD_TABLE  +
            " ( " +
            ID + " VARCHAR(36) NOT NULL," +
            FIELD_NAME + " VARCHAR(64), " +
            CHECK_TEXT + " VARCHAR(128), " +
            COMPARATOR + " VARCHAR(64), " +
            " PRIMARY KEY ( " + ID + ", " + FIELD_NAME + ", " + CHECK_TEXT + ", " + COMPARATOR + " ), " +
            " CONSTRAINT anz_rule_field_fk FOREIGN KEY (" + ID + ") REFERENCES " + ANZ_RULE_TABLE + " (" + ID + ") ON DELETE CASCADE ON UPDATE CASCADE " +
            ")";
    final static String INSERT_ANZ_RULE_FIELD_TABLE = "INSERT INTO " + ANZ_RULE_FIELD_TABLE + " VALUES (?,?,?,?)";

    final static String ANZ_TRANSACTION_NOTE_TABLE = "ANZ_TRANSACTION_NOTE_TABLE";
    final static String START_DATE = "START_DATE";
    final static String ANZ_TRANSACTION_TYPE = "ANZ_TRANSACTION_TYPE";
    final static String INDEX = "INDEX";
    final static String NOTE = "NOTE";
    final static String DATE = "DATE";
    final static String THIRD_PARTY = "THIRD_PARTY";
    final static String CODE = "CODE";
    final static String PARTICULARS = "PARTICULARS";
    final static String REFERENCE = "REFERENCE";
    final static String AMOUNT = "AMOUNT";
    final static String BALANCE = "BALANCE";
    final static String HYPERLINK = "HYPERLINK";
    final static String CREATE_ANZ_TRANSACTION_NOTE_TABLE = "CREATE TABLE IF NOT EXISTS " + ANZ_TRANSACTION_NOTE_TABLE +
            "(" +
            BANK_ACCOUNT_NUMBER + " VARCHAR(20) NOT NULL," +
            START_DATE + " INT NOT NULL," +
            INDEX + " INT NOT NULL," +
            DATE + " INT NOT NULL," +
            ANZ_TRANSACTION_TYPE + " VARCHAR(128), " +
            THIRD_PARTY + " VARCHAR(128)," +
            CODE + " VARCHAR(128)," +
            PARTICULARS + " VARCHAR(128)," +
            REFERENCE + " VARCHAR(128)," +
            AMOUNT + " DOUBLE NOT NULL, " +
            BALANCE + " DOUBLE NOT NULL, " +
            NOTE + " VARCHAR(128)," +
            HYPERLINK + " VARCHAR(256), " +
            IS_GST_INCL + " BOOLEAN, " +
            ID + " VARCHAR(36) NOT NULL, " +
            TRANSACTION_TYPE + " VARCHAR(128), " +
            " PRIMARY KEY ( " + START_DATE + ", " + INDEX + ", " + BANK_ACCOUNT_NUMBER + " ), " +
            ")";

    final static String INSERT_ANZ_TRANSACTION_NOTE_TABLE = "INSERT INTO " + ANZ_TRANSACTION_NOTE_TABLE +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";

    final static String MERGE_ANZ_TRANSACTION_NOTE_TABLE = "MERGE INTO " + ANZ_TRANSACTION_NOTE_TABLE +
            " VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)";
}
