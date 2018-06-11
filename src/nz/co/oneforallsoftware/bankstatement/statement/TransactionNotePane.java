package nz.co.oneforallsoftware.bankstatement.statement;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

public class TransactionNotePane extends HBox {
    @FXML
    Label noteLabel, gstStatusLabel, transactionTypeLabel, descriptionLabel;

    private ImageView gstStatusImgView, transactionTypeImgView, descriptionImgView;
    private TextField noteField;
    private Transaction transaction;
    private boolean editing;

    public TransactionNotePane(Transaction transaction){
        super();
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
        this.transaction = transaction;
    }

    public Transaction getTransaction() {
        return transaction;
    }

    public void setTextColor(Color color){
        noteLabel.setTextFill(color);

    }

    public void setFont(Font font){
        noteLabel.setFont(font);
        noteField.setFont(font);
    }

    public void setFontSize(int size){

    }

    public boolean isEditing(){
        return editing;
    }

    public void startEditing(){
        editing = true;
    }

    public void endEditing(){
        editing = false;
    }
}
