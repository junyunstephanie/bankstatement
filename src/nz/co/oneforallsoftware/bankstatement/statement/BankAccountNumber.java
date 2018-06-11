package nz.co.oneforallsoftware.bankstatement.statement;

public class BankAccountNumber {
    private String bankCode, branchCode, accountCode, suffixCode;

    private BankAccountNumber(String bankCode, String branchCode, String accountCode, String suffixCode){
        this.bankCode = bankCode;
        this.branchCode = branchCode;
        this.accountCode = accountCode;
        this.suffixCode = suffixCode;
    }

    public String getBankCode() {
        return bankCode;
    }

    public String getBranchCode() {
        return branchCode;
    }

    public String getAccountCode() {
        return accountCode;
    }

    public String getSuffixCode() {
        return suffixCode;
    }

    public int hashCode(){
        int hashCode = bankCode.hashCode() + branchCode.hashCode() * 3 + accountCode.hashCode() * 7;
        if( suffixCode.length() == 2 ){
            hashCode = hashCode + new String("0" + suffixCode).hashCode();
        }else{
            hashCode = hashCode + suffixCode.hashCode();
        }
        return hashCode;
    }

    public boolean equals(Object obj){
        if( obj == null || !(obj instanceof BankAccountNumber)){
            return false;
        }

        BankAccountNumber accountNumber = (BankAccountNumber)obj;

        if( !bankCode.equals(accountNumber.bankCode)){
            return false;
        }

        if( !branchCode.equals(accountNumber.branchCode)){
            return false;
        }

        if( !accountCode.equals(accountNumber.accountCode)){
            return false;
        }

        String suffix1 = suffixCode;
        if( suffix1.length() == 2 ){
            suffix1 = "0" + suffix1;
        }
        String suffix2 = accountNumber.suffixCode;
        if( suffix2.length() == 2 ){
            suffix2 = "0" + suffix2;
        }

        return suffix1.equals(suffix2);
    }

    public String toString(){
        if( suffixCode.length() == 3 ) {
            return bankCode + "-" + branchCode + "-" + accountCode + "-" + suffixCode;
        }else{
            return bankCode + "-" + branchCode + "-" + accountCode + "-0" + suffixCode;
        }
    }

    public static BankAccountNumber parseAccountNumber(String str){
        if( str == null ){
            return null;
        }

        String[] strings = str.split("-");
        if( strings.length == 1 ){
            if( str.length() == 15 || str.length() == 16 ){
                try{
                    String bankCode = str.substring(0, 2);
                    Integer.parseInt(bankCode);

                    String branchCode = str.substring(2, 6);
                    Integer.parseInt(branchCode);

                    String accountCode = str.substring(6, 13);
                    Integer.parseInt(accountCode);

                    String suffixCode = str.substring(13);
                    Integer.parseInt(suffixCode);

                    return new BankAccountNumber(bankCode, branchCode, accountCode, suffixCode);
                }catch(Exception exp){
                    return null;
                }
            }else{
                return null;
            }
        }else if( strings.length == 4 ){
            try{
                String bankCode = strings[0];
                if( bankCode.length() != 2 ){
                    return null;
                }
                Integer.parseInt(bankCode);
                String branchCode = strings[1];
                if( branchCode.length() != 4 ){
                    return null;
                }
                Integer.parseInt(branchCode);
                String accountCode = strings[2];
                if( accountCode.length() != 7 ){
                    return null;
                }
                Integer.parseInt(accountCode);
                String suffixCode = strings[3];
                if( suffixCode.length() != 2 &&  suffixCode.length() != 3){
                    return null;
                }
                Integer.parseInt(suffixCode);

                return new BankAccountNumber(bankCode, branchCode, accountCode, suffixCode);
            }catch(Exception exp){
                return null;
            }
        }else{
            return null;
        }
    }
}
