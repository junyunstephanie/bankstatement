package nz.co.oneforallsoftware.bankstatement;


import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Optional;

public class MessageDialog {
    public static void showErrorMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if( title != null && title.trim().length() > 0 ) {
            alert.setTitle(title);
        }else {
            alert.setTitle("Error");
        }
        alert.setHeaderText(null);
        Image img = new Image(MessageDialog.class.getResource("/images/error_32.png").toString());
        ImageView imgView = new ImageView(img);
        alert.setGraphic(imgView);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(img);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showInformationMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if( title != null && title.trim().length() > 0 ) {
            alert.setTitle(title);
        }else {
            alert.setTitle("Info");
        }
        alert.setHeaderText(null);
        Image img = new Image(MessageDialog.class.getResource("/images/info_32.png").toString());
        ImageView imgView = new ImageView(img);
        alert.setGraphic(imgView);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(img);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void showWarningMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        if( title != null && title.trim().length() > 0 ) {
            alert.setTitle(title);
        }else {
            alert.setTitle("Warning");
        }
        alert.setHeaderText(null);
        Image img = new Image(MessageDialog.class.getResource("/images/warning_32.png").toString());
        ImageView imgView = new ImageView(img);
        alert.setGraphic(imgView);
        Stage stage = (Stage) alert.getDialogPane().getScene().getWindow();
        stage.getIcons().add(img);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static YesNoCancelResult showYesNoCancelOptionDialog(String title, String message, boolean repeatConfirm) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        if( title != null && title.trim().length() > 0 ) {
            dialog.setTitle(title);
        }else {
            dialog.setTitle("Option");
        }

        Image img = new Image(MessageDialog.class.getResource("/images/question_32.png").toString());
        ImageView imgView = new ImageView(img);
        dialog.setGraphic(imgView);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(img);
        dialog.setHeaderText(message);
        dialog.setResizable(false);
        dialog.getDialogPane().getStylesheets().add(MessageDialog.class.getResource("style.css").toExternalForm());

        ButtonType yesBtnType = new ButtonType("Yes", ButtonBar.ButtonData.YES);
        ButtonType noBtnType = new ButtonType("No", ButtonBar.ButtonData.NO);
        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(cancelBtnType, noBtnType, yesBtnType);

        BorderPane borderPane = new BorderPane();
        borderPane.setPadding(new Insets(0, 30, 10, 50));

        CheckBox repeatBox = new CheckBox();
        repeatBox.setText("Don't ask me again");
        if( repeatConfirm ) {
            borderPane.setRight(repeatBox);
        }

        dialog.getDialogPane().setContent(borderPane);

        Optional<ButtonType> result = dialog.showAndWait();
        ButtonType type = result.orElse(null);

        YesNoCancelResult option = new YesNoCancelResult();
        option.setResultObject(Boolean.valueOf(!repeatBox.isSelected()));

        if( type == yesBtnType ) {
            option.setResult(YesNoCancelResult.Result.YES);
        }else if( type == noBtnType ) {
            option.setResult(YesNoCancelResult.Result.NO);
        }else {
            option.setResult(YesNoCancelResult.Result.CANCEL);
        }

        return option;
    }

    public static void showSucceedDialog(String title, String message) {

        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        if( title != null && title.trim().length() > 0 ) {
            dialog.setTitle(title);
        }else {
            dialog.setTitle("Succeed");
        }

        Image img = new Image(MessageDialog.class.getResource("/images/checked_32.png").toString());
        ImageView imgView = new ImageView(img);
        dialog.setGraphic(imgView);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(img);
        dialog.setHeaderText(message);
        dialog.setResizable(false);
        dialog.getDialogPane().getStylesheets().add(MessageDialog.class.getResource("style.css").toExternalForm());

        ButtonType okBtnType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().setAll(okBtnType);
        dialog.showAndWait();

    }

    public static YesNoCancelResult showOkCancelDialog(String title, String message) {
        /*
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        if( title != null && title.trim().length() > 0 ) {
            dialog.setTitle(title);
        }else {
            dialog.setTitle("Option");
        }

        Image img = new Image(MessageDialog.class.getResource("/images/question_32.png").toString());
        ImageView imgView = new ImageView(img);
        dialog.setGraphic(imgView);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(img);
        dialog.setHeaderText(message);
        dialog.setResizable(false);
        dialog.getDialogPane().getStylesheets().add(MessageDialog.class.getResource("style.css").toExternalForm());

        ButtonType okBtnType = new ButtonType("OK", ButtonBar.ButtonData.OK_DONE);

        ButtonType cancelBtnType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(cancelBtnType, okBtnType);

        Optional<ButtonType> result = dialog.showAndWait();
        ButtonType type = result.orElse(null);
        YesNoCancelResult option = new YesNoCancelResult();
        if( type == okBtnType ) {
            option.setResult(YesNoCancelResult.Result.YES);
        }else {
            option.setResult(YesNoCancelResult.Result.NO);
        }

        return option;
        */



        return showOkCancelDialog(title, message, "OK", "Cancel");
    }

    public static YesNoCancelResult showOkCancelDialog(String title, String message, String okText, String cancelText) {
        Alert dialog = new Alert(Alert.AlertType.CONFIRMATION);
        if( title != null && title.trim().length() > 0 ) {
            dialog.setTitle(title);
        }else {
            dialog.setTitle("Option");
        }

        Image img = new Image(MessageDialog.class.getResource("/images/question_32.png").toString());
        ImageView imgView = new ImageView(img);
        dialog.setGraphic(imgView);
        Stage stage = (Stage) dialog.getDialogPane().getScene().getWindow();
        stage.getIcons().add(img);
        dialog.setHeaderText(message);
        dialog.setResizable(false);
        dialog.getDialogPane().getStylesheets().add(MessageDialog.class.getResource("style.css").toExternalForm());

        ButtonType okBtnType = new ButtonType(okText, ButtonBar.ButtonData.OK_DONE);

        ButtonType cancelBtnType = new ButtonType(cancelText, ButtonBar.ButtonData.CANCEL_CLOSE);

        dialog.getDialogPane().getButtonTypes().setAll(cancelBtnType, okBtnType);

        Optional<ButtonType> result = dialog.showAndWait();
        ButtonType type = result.orElse(null);
        YesNoCancelResult option = new YesNoCancelResult();
        if( type == okBtnType ) {
            option.setResult(YesNoCancelResult.Result.YES);
        }else {
            option.setResult(YesNoCancelResult.Result.NO);
        }

        return option;
    }
}

