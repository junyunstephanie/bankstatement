package nz.co.oneforallsoftware.bankstatement.anz;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import nz.co.oneforallsoftware.bankstatement.statement.BankAccountNumber;
import nz.co.oneforallsoftware.bankstatement.MainPane;
import nz.co.oneforallsoftware.bankstatement.YesNoCancelResult;
import nz.co.oneforallsoftware.bankstatement.database.H2Database;

import java.util.ArrayList;
import java.util.Optional;

public class NewANZTransactionRuleDialog extends Dialog {
    private NewANZTransactionRuleBox box;
    private BankAccountNumber accountNumber;
    private ButtonType okBtnType;
    private Button okBtn;
    private Stage stage;
    private H2Database db;
    private ArrayList<ANZTransactionRule> transactionRules;
    private ANZTransactionRule newTransactionRule;

    public NewANZTransactionRuleDialog(ANZTransaction anzTransaction, BankAccountNumber bankAccountNumber, ArrayList<ANZTransactionRule> transactionRules, H2Database db){
        accountNumber = bankAccountNumber;
        this.db = db;
        this.transactionRules = transactionRules;

        this.getDialogPane().getScene().getStylesheets().add(MainPane.class.getResource("style.css").toExternalForm());

        stage = (Stage) this.getDialogPane().getScene().getWindow();
        stage.setTitle("New ANZ Bank Statement Rule");
        setHeaderText(null);
        setGraphic(null);

        okBtnType = new ButtonType("Create (Ctrl+S)", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelBtnType = new ButtonType("Cancel (Esc)", ButtonBar.ButtonData.CANCEL_CLOSE);
        getDialogPane().getButtonTypes().setAll(okBtnType, cancelBtnType);
        okBtn = (Button)getDialogPane().lookupButton(okBtnType);

        box = new NewANZTransactionRuleBox(bankAccountNumber, anzTransaction);
        getDialogPane().setContent(box);

        okBtn.addEventFilter(ActionEvent.ANY, new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                newTransactionRule = box.validateInput();
                if( newTransactionRule == null){
                    event.consume();
                }else{
                    try{
                        db.saveNewANZTransactionRule(newTransactionRule);
                        transactionRules.add(newTransactionRule);
                    }catch(Exception exp){
                        exp.printStackTrace();
                    }
                }
            }
        });

        stage.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if( code == KeyCode.F1 ){
                    box.showHelp();
                }else if( code == KeyCode.S && event.isControlDown() ){
                    newTransactionRule = box.validateInput();
                    if( newTransactionRule == null){
                        event.consume();
                    }else{
                        try{
                            db.saveNewANZTransactionRule(newTransactionRule);
                            transactionRules.add(newTransactionRule);
                        }catch(Exception exp){
                            exp.printStackTrace();
                        }
                        okBtn.fire();
                    }
                }
            }
        });
    }

    public YesNoCancelResult showDialog(){
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                box.initFocus();
            }
        });

        Optional<ButtonType> options = showAndWait();
        ButtonType btnType = options.orElse(null);

        YesNoCancelResult result = new YesNoCancelResult(YesNoCancelResult.Result.YES);
        if( btnType == okBtnType ){
            result.setResultObject(newTransactionRule);
            return result;
        }else{
            result.setResult(YesNoCancelResult.Result.CANCEL);
            return result;
        }
    }
}
