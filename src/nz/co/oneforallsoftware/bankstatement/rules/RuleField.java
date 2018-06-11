package nz.co.oneforallsoftware.bankstatement.rules;

import java.util.ArrayList;

public class RuleField {
    private String fieldName;
    private ArrayList<RuleItem> ruleItems = new ArrayList<>();

    public RuleField(String fieldName){
        this.fieldName = fieldName;
    }

    public String getFieldName() {
        return fieldName;
    }

    public void addRuleItem(RuleItem ruleItem){
        if( !ruleItems.contains(ruleItem)) {
            ruleItems.add(ruleItem);
        }
    }

    public int getRuleItemCount(){
        return ruleItems.size();
    }

    public RuleItem getRuleItem(int index){
        if( index < 0 || index > ruleItems.size() - 1 ){
            return null;
        }

        return ruleItems.get(index);
    }

    public void removeRuleItem(RuleItem ruleItem){
        ruleItems.remove(ruleItem);
    }

    public void clearRuleItems(){
        ruleItems.clear();
    }

    public boolean isRuleFollowed(String text){
        if( text == null || text.length() == 0 ){
            return false;
        }

        for(RuleItem ruleItem: ruleItems){
            if( !ruleItem.isRuleFollowed(text) ){
                return false;
            }
        }

        return true;
    }

    public int hashCode(){
        return fieldName.toLowerCase().hashCode();
    }

    public boolean equals(Object obj){
        if( obj == null || !(obj instanceof RuleField)){
            return false;
        }

        RuleField ruleField = (RuleField)obj;

        return fieldName.equalsIgnoreCase(ruleField.fieldName);
    }
}
