package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.statement.BankAccountNumber;
import nz.co.oneforallsoftware.bankstatement.Utils;
import nz.co.oneforallsoftware.bankstatement.rules.Rule;
import nz.co.oneforallsoftware.bankstatement.rules.RuleField;
import nz.co.oneforallsoftware.bankstatement.rules.RuleItem;
import nz.co.oneforallsoftware.bankstatement.statement.Transaction;

public class ANZTransactionRule extends Rule {
    protected static enum Field{
        TRANSACTION_TYPE,
        THIRD_PARTY,
        PARTICULARS,
        CODE,
        REFERENCE;
    }
    public ANZTransactionRule(BankAccountNumber bankAccountNumber){
        super(bankAccountNumber);
    }

    public ANZTransactionRule(BankAccountNumber accountNumber, String id){
        super(accountNumber, id);
    }
    @Override
    public boolean isRuleFollowed(Transaction transaction) {
        if( transaction == null || !(transaction instanceof ANZTransaction)){
            return false;
        }

        ANZTransaction anzTransaction = (ANZTransaction)transaction;
        if( isDeposit() ){
            if( anzTransaction.getAmount() < 0 ){
                return false;
            }
        }else{
            if( anzTransaction.getAmount() > 0 ){
                return false;
            }
        }

        if( anzTransaction.getPdfVCoordinate().getPageOrientation() == Utils.PdfPageOrientation.LANDSCAPE ){
            for(RuleField ruleField: ruleFields){
                String fieldName = ruleField.getFieldName();
                if( fieldName.equalsIgnoreCase(Field.TRANSACTION_TYPE.name()) ){
                    if( !ruleField.isRuleFollowed(anzTransaction.getANZTransactionType().name())){
                        return false;
                    }
                }else if( fieldName.equalsIgnoreCase(Field.THIRD_PARTY.name()) ){
                    if( !ruleField.isRuleFollowed(anzTransaction.getThirdParty())){
                        return false;
                    }
                }else if( fieldName.equalsIgnoreCase(Field.PARTICULARS.name()) ){
                    if( !ruleField.isRuleFollowed(anzTransaction.getParticulars())){
                        return false;
                    }
                }else if( fieldName.equalsIgnoreCase(Field.CODE.name()) ){
                    if( !ruleField.isRuleFollowed(anzTransaction.getCode())){
                        return false;
                    }
                }else if( fieldName.equalsIgnoreCase(Field.REFERENCE.name()) ){
                    if( !ruleField.isRuleFollowed(anzTransaction.getReference())){
                        return false;
                    }
                }
            }
            return true;
        }else{
            for(RuleField ruleField: ruleFields){
                String fieldName = ruleField.getFieldName();
                if( fieldName.equalsIgnoreCase(Field.TRANSACTION_TYPE.name()) ){
                    if( !ruleField.isRuleFollowed(anzTransaction.getANZTransactionType().name())){
                        return false;
                    }else{
                        int count = ruleField.getRuleItemCount();
                        for(int ruleItemIndex = 0; ruleItemIndex < count; ruleItemIndex++) {
                            RuleItem ruleItem = ruleField.getRuleItem(ruleItemIndex);
                            String text = ruleItem.getCheckAgainstText();
                            if( !anzTransaction.getThirdParty().toLowerCase().contains(text.toLowerCase())){
                                return false;
                            }
                        }
                    }
                }
            }
            return true;
        }
    }

    public boolean equals(Object obj){
        if( obj == null || !(obj instanceof ANZTransactionRule)){
            return false;
        }

        Rule rule = (Rule)obj;

        return getId().equals(rule.getId()) && getAccountNumber().equals(rule.getAccountNumber());
    }
}
