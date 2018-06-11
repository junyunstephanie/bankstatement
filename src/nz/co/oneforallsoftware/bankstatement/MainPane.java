package nz.co.oneforallsoftware.bankstatement;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Label;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontPosture;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import nz.co.oneforallsoftware.bankstatement.annotation.AnnotationWriter;
import nz.co.oneforallsoftware.bankstatement.anz.*;
import nz.co.oneforallsoftware.bankstatement.concurrence.PdfToImgTask;
import nz.co.oneforallsoftware.bankstatement.database.H2Database;
import nz.co.oneforallsoftware.bankstatement.img.ImgOfPdfPage;

import java.awt.*;
import java.io.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class MainPane extends BorderPane {

    public static Setting setting;
    @FXML
    BorderPane mainBorderPane;

    @FXML
    ComboBox<Setting.PdfZoomRate> zoomRateBox;

    @FXML
    BorderPane openFileImgPane, saveFileImgPane, settingImgPane;

    @FXML
    ImageView openFileImgView, saveFileImgView, settingImgView;

    private Stage stage;

    private String bankStmtFilePath;

    private H2Database db;


    private ANZPdfStatementPane anzPdfPane;
    public MainPane(){
    }

    protected void setStage(Stage stage){
        this.stage = stage;
    }

    @FXML
    public void initialize(){
        setting = Setting.loadSetting();
        if( setting == null ){
            setting = Setting.getDefaultSetting();
        }

        try{
            db = new H2Database();
        }catch(Exception exp){
            exp.printStackTrace();
        }
        zoomRateBox.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Setting.PdfZoomRate>() {
            @Override
            public void changed(ObservableValue<? extends Setting.PdfZoomRate> observable, Setting.PdfZoomRate oldValue, Setting.PdfZoomRate newValue) {
                if( anzPdfPane != null && mainBorderPane.getChildren().contains(anzPdfPane)){
                    anzPdfPane.setZoomRate(newValue);
                }
            }
        });
        zoomRateBox.setPadding(new Insets(0, 0, 0, 0));
        zoomRateBox.editorProperty().getValue().setPadding(new Insets(1, 0, 1, 0));
        zoomRateBox.getItems().setAll(Setting.PdfZoomRate.values());
        zoomRateBox.getSelectionModel().select(setting.getPdfZoomRate());

        zoomRateBox.setVisible(false);

        openFileImgPane.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        saveFileImgPane.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        settingImgPane.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
        openFileImgView.setImage(new Image(getClass().getResource("/images/folder_write_32.png").toExternalForm()));
        saveFileImgView.setImage(new Image(getClass().getResource("/images/folder_tick_32.png").toExternalForm()));
        settingImgView.setImage(new Image(getClass().getResource("/images/folder_setting_32.png").toExternalForm()));
        openFileImgPane.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openFileImgPane.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            }
        });
        openFileImgPane.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openFileImgPane.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            }
        });
        saveFileImgPane.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveFileImgPane.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            }
        });
        saveFileImgPane.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveFileImgPane.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            }
        });
        settingImgPane.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                settingImgPane.setBorder(new Border(new BorderStroke(Color.DARKGRAY, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            }
        });
        settingImgPane.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                settingImgPane.setBorder(new Border(new BorderStroke(Color.TRANSPARENT, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
            }
        });

        openFileImgPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                openFileImgViewClicked();
            }
        });

        saveFileImgPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                saveFile();
            }
        });

        mainBorderPane.getChildren().clear();
    }


    void openFileImgViewClicked(){
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files(*.pdf)", "*.pdf"));
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel Files(*.xlsx)", "*.xlsx"));
        fileChooser.setInitialDirectory(new File(setting.getBankStatementDir()));
        File file = fileChooser.showOpenDialog(stage);
        if( file != null ){
            bankStmtFilePath = file.getAbsolutePath();
            if( anzPdfPane == null ) {
                anzPdfPane = new ANZPdfStatementPane(setting.getPdfZoomRate(), db, stage);
                mainBorderPane.setCenter(anzPdfPane);
            }else{
                if( !mainBorderPane.getChildren().contains(anzPdfPane) ){
                    mainBorderPane.setCenter(anzPdfPane);
                }
            }
            zoomRateBox.setVisible(true);
            anzPdfPane.openANZPdfStatement(bankStmtFilePath);
        }
    }

    private void saveFile(){
        if( anzPdfPane != null && mainBorderPane.getChildren().contains(anzPdfPane)){
            anzPdfPane.saveFile();
        }
    }

}
