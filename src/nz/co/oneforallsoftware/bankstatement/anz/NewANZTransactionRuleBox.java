package nz.co.oneforallsoftware.bankstatement.anz;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.util.Callback;
import nz.co.oneforallsoftware.bankstatement.statement.BankAccountNumber;
import nz.co.oneforallsoftware.bankstatement.MessageDialog;
import nz.co.oneforallsoftware.bankstatement.rules.RuleField;
import nz.co.oneforallsoftware.bankstatement.rules.RuleItem;
import nz.co.oneforallsoftware.bankstatement.rules.RuleItemComparator;

public class NewANZTransactionRuleBox extends VBox {

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
    private BankAccountNumber bankAccountNumber;
    private ANZTransaction transaction;
    protected NewANZTransactionRuleBox(BankAccountNumber bankAccountNumber, ANZTransaction transaction){
        super();
        this.bankAccountNumber = bankAccountNumber;
        this.transaction = transaction;
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

        double amount = transaction.getAmount();
        if( amount < 0 ){
            titleLabel.setText("New Withdraw Transaction Rule");
        }else{
            titleLabel.setText("New Deposit Transaction Rule");
        }
        transactionTypeCheckBox.setSelected(true);
        transactionTypeComboBox.getItems().setAll(ANZTransaction.ANZTransactionType.values());
        transactionTypeComboBox.getSelectionModel().select(transaction.getANZTransactionType());

        thirdPartyOptionBox.getItems().setAll(RuleItemComparator.values());
        thirdPartyOptionBox.getSelectionModel().select(0);
        particularsOptionBox.getItems().setAll(RuleItemComparator.values());
        particularsOptionBox.getSelectionModel().select(0);
        codeOptionBox.getItems().setAll(RuleItemComparator.values());
        codeOptionBox.getSelectionModel().select(0);
        referenceOptionBox.getItems().setAll(RuleItemComparator.values());
        referenceOptionBox.getSelectionModel().select(0);

        setComboBoxRightAlignment(thirdPartyOptionBox);
        setComboBoxRightAlignment(particularsOptionBox);
        setComboBoxRightAlignment(codeOptionBox);
        setComboBoxRightAlignment(referenceOptionBox);

        thirdParty = transaction.getThirdParty();
        thirdPartyField.setText(thirdParty);
        if( thirdParty.length() > 0 ){
            thirdPartyCheckBox.setSelected(true);
        }

        particulars = transaction.getParticulars();
        particularsField.setText(particulars);
        if( particulars.length() > 0 ){
            particularsCheckBox.setSelected(true);
        }

        code = transaction.getCode();
        codeField.setText(code);
        if( code.length() > 0 ){
            codeCheckBox.setSelected(true);
        }

        reference = transaction.getReference();
        referenceField.setText(reference);
        if( reference.length() > 0 ){
            referenceCheckBox.setSelected(true);
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

    protected ANZTransactionRule validateInput(){
        if( !transactionTypeCheckBox.isSelected()
                && !thirdPartyCheckBox.isSelected()
                && !particularsCheckBox.isSelected()
                && !codeCheckBox.isSelected()
                && !referenceCheckBox.isSelected()){
            MessageDialog.showWarningMessage("No Field Selected", "Please select at lease one field for the new transaction rule");
            return null;
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
                return null;
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
                return null;
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
                return null;
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
                return null;
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
            return null;
        }

        ANZTransactionRule rule = new ANZTransactionRule(bankAccountNumber);
        if(transaction.getAmount() < 0 ){
            rule.setDeposit(false);
        }else{
            rule.setDeposit(true);
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
        return rule;
    }


    protected void initFocus(){
        noteField.requestFocus();
    }

    @FXML
    protected void showHelp(){

    }
}
