package nz.co.oneforallsoftware.bankstatement.rules;

public enum RuleItemComparator {
    EQUALS("Equals"),
    CONTAINS("Contains"),
    STARTS_WITH("Starts With"),
    ENDS_WITH("Ends With");

    RuleItemComparator(String description){
        this.description = description;
    }

    private String description;
    public String toString(){
        return description;
    }
}
