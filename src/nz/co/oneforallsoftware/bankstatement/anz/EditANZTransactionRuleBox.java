package nz.co.oneforallsoftware.bankstatement.anz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import nz.co.oneforallsoftware.bankstatement.MessageDialog;
import nz.co.oneforallsoftware.bankstatement.rules.RuleField;
import nz.co.oneforallsoftware.bankstatement.rules.RuleItem;
import nz.co.oneforallsoftware.bankstatement.rules.RuleItemComparator;

public class EditANZTransactionRuleBox extends VBox {
    @FXML
    Label titleLabel;

    @FXML
    CheckBox transactionTypeCheckBox, thirdPartyCheckBox, particularsCheckBox, codeCheckBox, referenceCheckBox, gstCheckBox;

    @FXML
    ComboBox<ANZTransaction.ANZTransactionType> transactionTypeComboBox;

    @FXML
    ComboBox<RuleItemComparator> thirdPartyOptionBox, particularsOptionBox, codeOptionBox, referenceOptionBox;

    @FXML
    TextField thirdPartyField, particularsField, codeField, referenceField, noteField;

    private String thirdParty, particulars, code, reference;

    private ANZTransactionRule rule;

    protected EditANZTransactionRuleBox(ANZTransactionRule transactionRule){
        super();
        this.rule = transactionRule;
        try {
            String fxml = getClass().getSimpleName() + ".fxml";
            //System.out.println("FXML File " + fxml);
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
            loader.setRoot(this);
            loader.setController(this);
            loader.load();
        } catch (Exception exp) {
            exp.printStackTrace();
        }

        if( transactionRule.isDeposit() ){
            titleLabel.setText("Edit ANZ Deposit Transaction Rule");
        }else{
            titleLabel.setText("Edit ANZ Withdraw Transaction Rule");
        }

        transactionTypeComboBox.getItems().setAll(ANZTransaction.ANZTransactionType.values());
        transactionTypeComboBox.getSelectionModel().select(0);
        codeOptionBox.getItems().setAll(RuleItemComparator.values());
        codeOptionBox.getSelectionModel().select(0);
        thirdPartyOptionBox.getItems().setAll(RuleItemComparator.values());
        thirdPartyOptionBox.getSelectionModel().select(0);
        particularsOptionBox.getItems().setAll(RuleItemComparator.values());
        particularsOptionBox.getSelectionModel().select(0);
        referenceOptionBox.getItems().setAll(RuleItemComparator.values());
        referenceOptionBox.getSelectionModel().select(0);

        setComboBoxRightAlignment(thirdPartyOptionBox);
        setComboBoxRightAlignment(particularsOptionBox);
        setComboBoxRightAlignment(codeOptionBox);
        setComboBoxRightAlignment(referenceOptionBox);

        gstCheckBox.setSelected(rule.isGstIncl());
        noteField.setText(rule.getResultNote());

        int fieldCount = rule.getRuleFieldCount();
        for(int ruleFieldIndex = 0; ruleFieldIndex < fieldCount; ruleFieldIndex++){
            RuleField ruleField = rule.getRuleField(ruleFieldIndex);
            String fieldName = ruleField.getFieldName();

            RuleItem ruleItem = ruleField.getRuleItem(0);
            String text = ruleItem.getCheckAgainstText().toUpperCase();
            RuleItemComparator comparator = ruleItem.getComparator();
            if( fieldName.equalsIgnoreCase(ANZTransactionRule.Field.TRANSACTION_TYPE.name())){
                transactionTypeCheckBox.setSelected(true);

                ANZTransaction.ANZTransactionType ANZTransactionType = ANZTransaction.ANZTransactionType.valueOf(text);
                transactionTypeComboBox.getSelectionModel().select(ANZTransactionType);
            }else if( fieldName.equalsIgnoreCase(ANZTransactionRule.Field.PARTICULARS.name())){
                particularsCheckBox.setSelected(true);
                particularsOptionBox.getSelectionModel().select(comparator);
                particularsField.setText(text);
            }else if( fieldName.equalsIgnoreCase(ANZTransactionRule.Field.THIRD_PARTY.name())){
                thirdPartyCheckBox.setSelected(true);
                thirdPartyOptionBox.getSelectionModel().select(comparator);
                thirdPartyField.setText(text);
            }else if( fieldName.equalsIgnoreCase(ANZTransactionRule.Field.CODE.name())){
                codeCheckBox.setSelected(true);
                codeOptionBox.getSelectionModel().select(comparator);
                codeField.setText(text);
            }else if( fieldName.equalsIgnoreCase(ANZTransactionRule.Field.REFERENCE.name())){
                referenceCheckBox.setSelected(true);
                referenceOptionBox.getSelectionModel().select(comparator);
                referenceField.setText(text);
            }
        }
    }

    private void setComboBoxRightAlignment(ComboBox<RuleItemComparator> comboBox){
        comboBox.setButtonCell(new ListCell<RuleItemComparator>() {
            @Override
            public void updateItem(RuleItemComparator item, boolean empty) {
                super.updateItem(item, empty);
                if (item != null) {
                    setText(item.toString());
                    setAlignment(Pos.CENTER_RIGHT);
                    //Insets old = getPadding();
                    //setPadding(new Insets(old.getTop(), 10, old.getBottom(), 0));
                }
            }
        });
        comboBox.setCellFactory(new Callback<ListView<RuleItemComparator>, ListCell<RuleItemComparator>>() {
            @Override
            public ListCell<RuleItemComparator> call(ListView<RuleItemComparator> list) {
                return new ListCell<RuleItemComparator>() {
                    @Override
                    public void updateItem(RuleItemComparator item, boolean empty) {
                        super.updateItem(item, empty);
                        if (item != null) {
                            setText(item.toString());
                            setAlignment(Pos.CENTER_RIGHT);
                            Insets old = getPadding();
                            setPadding(new Insets(old.getTop(), 20, old.getBottom(), 0));
                        }
                    }
                };
            }
        });
    }

    protected boolean validateInput(){
        if( !transactionTypeCheckBox.isSelected()
                && !thirdPartyCheckBox.isSelected()
                && !particularsCheckBox.isSelected()
                && !codeCheckBox.isSelected()
                && !referenceCheckBox.isSelected()){
            MessageDialog.showWarningMessage("No Field Selected", "Please select at lease one field for the new transaction rule");
            return false;
        }

        RuleField typeRuleField = null, thirdPartyRuleField = null, codeRuleField = null, particularsRuleField = null, referenceRuleField = null;
        RuleItem typeItem = null, thirdPartyItem = null, particularsItem = null, codeItem = null, referenceItem = null;

        if(transactionTypeCheckBox.isSelected()){
            typeItem = new RuleItem(transactionTypeComboBox.getSelectionModel().getSelectedItem().name(), RuleItemComparator.EQUALS);
            typeRuleField = new RuleField(ANZTransactionRule.Field.TRANSACTION_TYPE.name());
            typeRuleField.addRuleItem(typeItem);
        }

        thirdParty = thirdPartyField.getText();
        if( thirdPartyCheckBox.isSelected() ){
            if( thirdParty.trim().length() == 0) {
                MessageDialog.showWarningMessage("No Third Party", "Please input third party.");
                thirdPartyField.requestFocus();
                return false;
            }else{
                thirdPartyItem = new RuleItem(thirdParty, thirdPartyOptionBox.getValue());
                thirdPartyRuleField = new RuleField(ANZTransactionRule.Field.THIRD_PARTY.name());
                thirdPartyRuleField.addRuleItem(thirdPartyItem);
            }
        }

        particulars = particularsField.getText();
        if( particularsCheckBox.isSelected() ){
            if(particulars.trim().length() == 0) {
                MessageDialog.showWarningMessage("No Particulars", "Please input particulars.");
                particularsField.requestFocus();
                return false;
            }else{
                particularsItem = new RuleItem(particulars, particularsOptionBox.getValue());
                particularsRuleField = new RuleField(ANZTransactionRule.Field.PARTICULARS.name());
                particularsRuleField.addRuleItem(particularsItem);
            }
        }

        code = codeField.getText();
        if( codeCheckBox.isSelected() ){
            if(code.trim().length() == 0) {
                MessageDialog.showWarningMessage("No Code", "Please input code.");
                codeField.requestFocus();
                return false;
            }else{
                codeItem = new RuleItem(code, codeOptionBox.getValue());
                codeRuleField = new RuleField(ANZTransactionRule.Field.CODE.name());
                codeRuleField.addRuleItem(codeItem);
            }
        }

        reference = referenceField.getText();
        if( referenceCheckBox.isSelected() ){
            if(reference.trim().length() == 0) {
                MessageDialog.showWarningMessage("No Reference", "Please input reference.");
                referenceField.requestFocus();
                return false;
            }else{
                referenceItem = new RuleItem(reference, referenceOptionBox.getValue());
                referenceRuleField = new RuleField(ANZTransactionRule.Field.REFERENCE.name());
                referenceRuleField.addRuleItem(referenceItem);
            }
        }

        String note = noteField.getText().trim();
        if( note.length() == 0 ){
            MessageDialog.showWarningMessage("No Transaction Note", "Please input transaction note.");
            noteField.setText("");
            noteField.requestFocus();
            return false;
        }


        rule.setResultNote(note);
        rule.setGstIncl(gstCheckBox.isSelected());

        if( typeRuleField!=null ){
            rule.addRuleField(typeRuleField);
        }
        if( thirdPartyRuleField!=null ){
            rule.addRuleField(thirdPartyRuleField);
        }
        if( codeRuleField!=null ){
            rule.addRuleField(codeRuleField);
        }
        if( referenceRuleField!=null ){
            rule.addRuleField(referenceRuleField);
        }
        return true;
    }


    protected void initFocus(){
        noteField.requestFocus();
        noteField.end();
    }

    @FXML
    protected void showHelp(){

    }
}
