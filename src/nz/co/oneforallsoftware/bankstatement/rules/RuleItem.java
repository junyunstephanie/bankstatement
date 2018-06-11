package nz.co.oneforallsoftware.bankstatement.rules;

public class RuleItem {
    private String checkAgainstText;
    private RuleItemComparator comparator;

    public RuleItem(String checkAgainstText, RuleItemComparator comparator){
        this.checkAgainstText = checkAgainstText.toLowerCase();
        this.comparator = comparator;
    }

    public boolean isRuleFollowed(String text){
        if( text == null || text.length() == 0 ){
            return false;
        }

        String str = text.toLowerCase();
        if( comparator == RuleItemComparator.STARTS_WITH ){
            return str.startsWith(checkAgainstText);
        }else if( comparator == RuleItemComparator.ENDS_WITH ){
            return str.endsWith(checkAgainstText);
        }else if( comparator == RuleItemComparator.CONTAINS ){
            return str.contains(checkAgainstText);
        }else if( comparator == RuleItemComparator.EQUALS ){
            return str.equals(checkAgainstText);
        }

        return !str.startsWith(checkAgainstText) && !str.endsWith(checkAgainstText) && str.contains(checkAgainstText);
    }

    public String getCheckAgainstText() {
        return checkAgainstText;
    }

    public RuleItemComparator getComparator() {
        return comparator;
    }

    public int hashCode(){
        int hashCode = checkAgainstText.toLowerCase().trim().hashCode() + comparator.name().toLowerCase().hashCode();
        return hashCode;
    }

    public boolean equals(Object obj){
        if( obj == null || !(obj instanceof RuleItem)){
            return false;
        }

        RuleItem item = (RuleItem)obj;

        return checkAgainstText.trim().equalsIgnoreCase(item.checkAgainstText.trim()) && comparator == item.comparator;
    }
}
