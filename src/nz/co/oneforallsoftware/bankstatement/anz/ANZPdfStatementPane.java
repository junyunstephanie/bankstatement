package nz.co.oneforallsoftware.bankstatement.anz;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import nz.co.oneforallsoftware.bankstatement.statement.BankAccountNumber;
import nz.co.oneforallsoftware.bankstatement.ProgressDialog;
import nz.co.oneforallsoftware.bankstatement.Setting;
import nz.co.oneforallsoftware.bankstatement.YesNoCancelResult;
import nz.co.oneforallsoftware.bankstatement.annotation.AnnotationWriter;
import nz.co.oneforallsoftware.bankstatement.concurrence.PdfToImgTask;
import nz.co.oneforallsoftware.bankstatement.database.H2Database;
import nz.co.oneforallsoftware.bankstatement.img.ImgOfPdfPage;

import java.awt.*;
import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

public class ANZPdfStatementPane extends BorderPane {
    private static final String DEFAULT_NOTE_FONT_FAMILY = "Verdana";
    private static final int DEFAULT_NOTE_FONT_SIZE = 14;

    //Font.font("Verdana", FontPosture.ITALIC, 14);
    //private static final double DEFAULT_ZOOM_RATE = 2d;
    //private static final String[] ZOOM_RATES_TEXTS = new String[]{"100%", "150%", "200%", "250%", "300%"};
    @FXML
    VBox thumbnailBox;

    @FXML
    AnchorPane pdfPageAnchorPane;

    @FXML
    ImageView pdfPageImgView;

    private Stage stage;
    private ArrayList<ImgOfPdfPage> imgOfPdfPages = new ArrayList<>();
    private ArrayList<ANZStatement> statements = new ArrayList<>();
    private ANZStatement currentStmt;
    private int selectedPageIndex;
    private ANZPage selectedPage;
    private ANZTransaction selectedTransaction;
    private String bankStmtFilePath;
    private PdfToImgTask task;

    private TextField noteField = new TextField();

    private BorderPane selectedTransactionRect = new BorderPane();

    private double currentZoomRate;
    private Font noteFont;

    private H2Database db;
    private HashMap<ANZTransaction, Label> noteLabels = new HashMap<>();
    private Label removedLabel;
    ArrayList<ANZTransactionRule> transactionRules = new ArrayList<>();

    private ContextMenu anchorPaneContextMenu = new ContextMenu();

    public ANZPdfStatementPane(Setting.PdfZoomRate pdfZoomRate, H2Database db, Stage stage){
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

        this.db = db;
        this.stage = stage;

        selectedTransactionRect.setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        selectedTransactionRect.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1d))));
        selectedTransactionRect.setMouseTransparent(true);

        noteField.getStyleClass().add("no_border");
        noteField.setPadding(new Insets(0, 2, 0, 2));
        currentZoomRate = pdfZoomRate.getDoubleValue();
        noteFont = Font.font(DEFAULT_NOTE_FONT_FAMILY, FontPosture.ITALIC, DEFAULT_NOTE_FONT_SIZE);

        MenuItem createRuleMenuItem = new MenuItem("Create Rule");
        MenuItem editRuleMenuItem = new MenuItem("Edit Rule");
        MenuItem deleteRuleMenuItem = new MenuItem("Delete Rule");

        Menu gstMenu = new Menu("GST Included");
        RadioMenuItem gstInclMenuItem = new RadioMenuItem("GST Included");
        RadioMenuItem gstExclMenuItem = new RadioMenuItem("GST Excluded");
        gstMenu.getItems().setAll(gstInclMenuItem, gstExclMenuItem);
        ToggleGroup toggleGroup = new ToggleGroup();
        gstExclMenuItem.setToggleGroup(toggleGroup);
        gstInclMenuItem.setToggleGroup(toggleGroup);
        gstInclMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if( selectedTransaction != null ){
                    selectedTransaction.setGstIncl(true);
                }
            }
        });
        gstExclMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                if( selectedTransaction != null ){
                    selectedTransaction.setGstIncl(false);
                }
            }
        });

        createRuleMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                createNewANZTransactionRule();
            }
        });

        editRuleMenuItem.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                editANZTransactionRule();
            }
        });

        anchorPaneContextMenu.getItems().setAll(createRuleMenuItem, editRuleMenuItem, deleteRuleMenuItem, new SeparatorMenuItem(), gstMenu);

        anchorPaneContextMenu.setOnShown(new EventHandler<WindowEvent>() {
            @Override
            public void handle(WindowEvent event) {
                if( selectedTransaction == null ){
                    createRuleMenuItem.setDisable(true);
                    editRuleMenuItem.setDisable(true);
                    deleteRuleMenuItem.setDisable(true);
                }else{
                    ANZTransactionRule currentRule = null;
                    for(ANZTransactionRule transactionRule: transactionRules){
                        if( transactionRule.isRuleFollowed(selectedTransaction)){
                            currentRule = transactionRule;
                            break;
                        }
                    }
                    if( currentRule == null ){
                        createRuleMenuItem.setDisable(false);
                        editRuleMenuItem.setDisable(true);
                        deleteRuleMenuItem.setDisable(true);
                    }else{
                        createRuleMenuItem.setDisable(true);
                        editRuleMenuItem.setDisable(false);
                        deleteRuleMenuItem.setDisable(false);
                    }

                    if( selectedTransaction.isGstIncl() ){
                        gstMenu.setText("GST Included");
                        gstInclMenuItem.setSelected(true);
                    }else{
                        gstMenu.setText("GST Excluded");
                        gstExclMenuItem.setSelected(true);
                    }
                }
            }
        });

        pdfPageAnchorPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( anchorPaneContextMenu.isShowing() ){
                    anchorPaneContextMenu.hide();
                }
                setSelectedTransaction(event, false);
            }
        });


        pdfPageAnchorPane.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if( event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 2 ){
                    setSelectedTransaction(event, true);
                }
            }
        });

        noteField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
                if( !newValue ){
                    restoreNoteLabel();
                }
            }
        });

        noteField.textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if( selectedTransaction != null ){
                    selectedTransaction.setNote(newValue);
                    try{
                        db.saveANZTransactionNote(currentStmt,selectedTransaction);
                    }catch(Exception exp){
                        exp.printStackTrace();
                    }
                }
                if(removedLabel != null ){
                    removedLabel.setText(newValue);
                }
            }
        });

        noteField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                KeyCode code = event.getCode();
                if( code == KeyCode.ENTER ){
                    restoreNoteLabel();
                }
            }
        });

        pdfPageAnchorPane.setOnContextMenuRequested(new EventHandler<ContextMenuEvent>() {
            @Override
            public void handle(ContextMenuEvent event) {
                if( selectedTransaction != null ){
                    anchorPaneContextMenu.show(pdfPageAnchorPane, null, event.getX(), event.getY());
                }
            }
        });
    }

    private void restoreNoteLabel(){
        //String text = noteField.getText();
        if( removedLabel != null && !pdfPageAnchorPane.getChildren().contains(removedLabel)){
            //removedLabel.setText(text);
            pdfPageAnchorPane.getChildren().add(removedLabel);
            removedLabel = null;
        }

        if( pdfPageAnchorPane.getChildren().contains(noteField) ){
            pdfPageAnchorPane.getChildren().remove(noteField);
        }

    }

    private void createNewANZTransactionRule(){
        BankAccountNumber accountNumber = BankAccountNumber.parseAccountNumber(currentStmt.getAccountNumber());
        NewANZTransactionRuleDialog dialog = new NewANZTransactionRuleDialog(selectedTransaction, accountNumber, transactionRules, db);
        YesNoCancelResult result = dialog.showDialog();
        if( result.getResult() == YesNoCancelResult.Result.YES ){
            Object obj = result.getResultObject();
            if( obj != null && obj instanceof ANZTransactionRule ){
                ANZTransactionRule anzTransactionRule = (ANZTransactionRule)obj;
                createNoteFromNewRule(anzTransactionRule);
            }
        }
    }

    private void editANZTransactionRule(){
        if( selectedTransaction == null ){
            return;
        }

        for(ANZTransactionRule transactionRule: transactionRules){
            if( transactionRule.isRuleFollowed(selectedTransaction) ){
                EditANZTransactionRuleDialog dialog = new EditANZTransactionRuleDialog(transactionRule, db);
                YesNoCancelResult result = dialog.showDialog();
                if( result.getResult() == YesNoCancelResult.Result.YES ){
                    updateNoteFromModifiedRule(transactionRule);
                }
            }
        }
    }

    public void setZoomRate(Setting.PdfZoomRate rate){
        pdfPageAnchorPane.getChildren().remove(selectedTransactionRect);
        selectedTransaction = null;
        pdfPageAnchorPane.getChildren().remove(noteField);

        int fontSize = DEFAULT_NOTE_FONT_SIZE;

        try{
            currentZoomRate = rate.getDoubleValue();
            if( currentZoomRate == 1d ){
                fontSize = 10;
            }else if( currentZoomRate == 1.5d ){
                fontSize = 12;
            }else if( currentZoomRate == 2d ){
                fontSize = 14;
            }else if( currentZoomRate == 2.5d ){
                fontSize = 16;
            }else if( currentZoomRate == 3d ){
                fontSize = 18;
            }
            noteFont = Font.font(DEFAULT_NOTE_FONT_FAMILY, FontPosture.ITALIC, fontSize);
            noteField.setFont(noteFont);
            if( imgOfPdfPages.size() > 0 && selectedPageIndex != -1 ) {
                ImgOfPdfPage imgOfPdfPage = imgOfPdfPages.get(selectedPageIndex);
                double pdfPageWidth = imgOfPdfPage.getPdfPageWidth();
                double pdfPageHeight = imgOfPdfPage.getPdfPageHeight();
                pdfPageImgView.setFitWidth(pdfPageWidth * currentZoomRate);
                pdfPageImgView.setFitHeight(pdfPageHeight * currentZoomRate);
                showAllNotes();
            }
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }

    private void setSelectedTransaction(MouseEvent dblClickEvent, boolean showTextField){
        if( currentStmt == null || selectedPage == null ){
            System.out.println("NULL Statement Page selected");
            return;
        }

        double linePadding = selectedPage.getLinePadding();
        double pdfY = (imgOfPdfPages.get(selectedPageIndex).getPdfPageHeight() * currentZoomRate - dblClickEvent.getY()) / currentZoomRate;
        selectedTransaction = null;
        int transactionCount = selectedPage.getTransactionCount();
        for(int transactionIndex = 0; transactionIndex < transactionCount; transactionIndex++) {
            ANZTransaction transaction = (ANZTransaction)selectedPage.getTransaction(transactionIndex);
            VerticalCoordinate verticalCoordinate = transaction.getPdfVCoordinate();
            if( pdfY >= verticalCoordinate.getTransactionBottomY() - linePadding && pdfY <= verticalCoordinate.getTopY() + linePadding ){
                selectedTransaction = transaction;
                break;
            }
        }

        if( selectedTransaction != null ){
            VerticalCoordinate verticalCoordinate = selectedTransaction.getPdfVCoordinate();
            HorizonCoordinate horizonCoordinate = selectedPage.getPdfPageHCoordinate();

            AnchorPane.setRightAnchor(selectedTransactionRect, 1d);
            AnchorPane.setLeftAnchor(selectedTransactionRect, 1d);
            double pdfHeight = imgOfPdfPages.get(selectedPageIndex).getPdfPageHeight();
            double topAnchor = verticalCoordinate.getTopY();
            AnchorPane.setTopAnchor(selectedTransactionRect, (pdfHeight - topAnchor - linePadding) * currentZoomRate);
            AnchorPane.setBottomAnchor(selectedTransactionRect, (verticalCoordinate.getTransactionBottomY() - linePadding) * currentZoomRate);
            if( !pdfPageAnchorPane.getChildren().contains(selectedTransactionRect)){
                pdfPageAnchorPane.getChildren().add(selectedTransactionRect);
            }

            if( showTextField ) {
                double bottomAnchor = (verticalCoordinate.getBottomY()) * currentZoomRate - 5d;
                AnchorPane.setBottomAnchor(noteField, bottomAnchor);

                if (selectedTransaction.getAmount() < 0) {
                    double width = (horizonCoordinate.getBalanceStartX() - horizonCoordinate.getWithdrawEndX()) * currentZoomRate - 10d;
                    noteField.setPrefWidth(width);
                    noteField.setMinWidth(width);
                    double leftAnchor = horizonCoordinate.getWithdrawEndX() * currentZoomRate + 5d;
                    AnchorPane.setLeftAnchor(noteField, leftAnchor);
                    noteField.setAlignment(Pos.CENTER_LEFT);
                    //System.out.println("Bottom Anchor " + bottomAnchor + " Width " + width + " Left Anchor " + leftAnchor);
                } else {
                    double width = (horizonCoordinate.getDepositStartX() - horizonCoordinate.getReferenceEndX()) * currentZoomRate - 10d;
                    noteField.setPrefWidth(width);
                    noteField.setMinWidth(width);
                    double leftAnchor = horizonCoordinate.getReferenceEndX() * currentZoomRate + 5d;
                    AnchorPane.setLeftAnchor(noteField, leftAnchor);
                    noteField.setAlignment(Pos.CENTER_RIGHT);
                    //System.out.println("Bottom Anchor " + bottomAnchor + " Width " + width + " Left Anchor " + leftAnchor);
                }

                Label noteLabel = noteLabels.get(selectedTransaction);
                if (noteLabel != null) {
                    //System.out.println("remove note label");
                    pdfPageAnchorPane.getChildren().remove(noteLabel);
                    removedLabel = noteLabel;
                }
                noteField.setText(selectedTransaction.getNote());
                //noteField.setText("test");
                if (!pdfPageAnchorPane.getChildren().contains(noteField)) {
                    pdfPageAnchorPane.getChildren().add(noteField);
                    //System.out.println("Add Note Field");
                }
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        noteField.requestFocus();
                        noteField.end();
                    }
                });
            }
        }else{
            System.out.println("NULL Transaction selected");
            pdfPageAnchorPane.getChildren().remove(selectedTransactionRect);
            pdfPageAnchorPane.getChildren().remove(noteField);
        }

    }

    private void showAllNotes(){
        if( selectedPage == null ){
            return;
        }

        HorizonCoordinate horizonCoordinate = selectedPage.getPdfPageHCoordinate();
        int transactionCount = selectedPage.getTransactionCount();
        for(int transactionIndex = 0; transactionIndex < transactionCount; transactionIndex++){
            ANZTransaction transaction = (ANZTransaction)selectedPage.getTransaction(transactionIndex);
            VerticalCoordinate verticalCoordinate = transaction.getPdfVCoordinate();
            Label label = noteLabels.get(transaction);
            if(label == null ){
                label = new Label();
                noteLabels.put(transaction, label);
            }
            label.setFont(noteFont);
            label.setText(transaction.getNote());
            AnchorPane.setBottomAnchor(label, verticalCoordinate.getBottomY()*currentZoomRate - 1.5d * currentZoomRate);
            if( transaction.getAmount() < 0 ){
                label.setTextFill(Color.RED);
                AnchorPane.setLeftAnchor(label, horizonCoordinate.getWithdrawEndX() * currentZoomRate + 5d);
            }else{
                label.setTextFill(AnnotationWriter.Annotation.DEFAULT_TEXT_COLOR);
                AnchorPane.setRightAnchor(label, (horizonCoordinate.getPageWidth() - horizonCoordinate.getDepositStartX()) * currentZoomRate + 5d);
            }

            if(!pdfPageAnchorPane.getChildren().contains(label)) {
                pdfPageAnchorPane.getChildren().add(label);
            }
        }
    }

    public void openANZPdfStatement(String pdfStmtPath){
        bankStmtFilePath = pdfStmtPath;
        try{
            ProgressDialog progressDialog = new ProgressDialog("Loading ANZ Statements", new ProgressDialog.ProgressCancelledListener() {
                @Override
                public void onCancelled() {
                    cancelPdfToImgTask();
                }
            });
            File file = new File(bankStmtFilePath);
            thumbnailBox.getChildren().clear();

            for(Label label: noteLabels.values()){
                pdfPageAnchorPane.getChildren().remove(label);
            }
            noteLabels.clear();
            pdfPageAnchorPane.getChildren().remove(selectedTransactionRect);
            pdfPageAnchorPane.getChildren().remove(noteField);
            pdfPageAnchorPane.setVisible(false);
            imgOfPdfPages.clear();
            selectedPageIndex = -1;

            task = new PdfToImgTask(file.getAbsolutePath());
            task.valueProperty().addListener(new ChangeListener<ImgOfPdfPage>() {
                @Override
                public void changed(ObservableValue<? extends ImgOfPdfPage> observable, ImgOfPdfPage oldValue, ImgOfPdfPage newValue) {
                    if( newValue != null ){
                        imgOfPdfPages.add(newValue);
                        initImageOfPdf(newValue);
                    }
                }
            });
            task.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    extractTransactions();
                    transactionRules.clear();
                    readTransactionRules();
                    createNotesFromRules();
                    showAllNotes();
                    if( progressDialog.isShowing() ){
                        progressDialog.hide();
                    }
                }
            });
            task.setOnCancelled(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    clearImages();
                }
            });
            task.setOnFailed(new EventHandler<WorkerStateEvent>() {
                @Override
                public void handle(WorkerStateEvent event) {
                    if( progressDialog.isShowing() ){
                        progressDialog.hide();
                    }
                }
            });

            Thread thread = new Thread(task);
            thread.start();
            progressDialog.showDialog(null);
            //alert.showAndWait();
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }

    private void readTransactionRules(){
        if( statements.size() == 0 ){
            return;
        }
        ANZStatement statement = statements.get(0);
        try{
            transactionRules = db.readANZTransactionRules(BankAccountNumber.parseAccountNumber(statement.getAccountNumber()));
            System.out.println("Transaction Rule Count " + transactionRules.size());
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }

    private void extractTransactions(){
        try {
            File file = new File(bankStmtFilePath);
            StatementANZReader reader = new StatementANZReader();
            statements = reader.readStatement(file);

            for(ANZStatement statement: statements){
                db.readANZTransactionNotes(statement);
            }
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }

    private void createNotesFromRules(){
        for(ANZStatement statement: statements){
            int pageCount = statement.getPageCount();
            for(int pageIndex = 0; pageIndex < pageCount; pageIndex++){
                ANZPage page = (ANZPage)statement.getPage(pageIndex);
                int transactionCount = page.getTransactionCount();
                for(int transactionIndex = 0; transactionIndex < transactionCount; transactionIndex++){
                    ANZTransaction transaction = (ANZTransaction)page.getTransaction(transactionIndex);
                    if(transaction.getNote().trim().length() > 0 ){
                        continue;
                    }
                    for(ANZTransactionRule transactionRule: transactionRules){
                        if(transactionRule.isRuleFollowed(transaction)){
                            transaction.setNote(transactionRule.getResultNote());
                            transaction.setGstIncl(transactionRule.isGstIncl());
                            transaction.setTransactionType(transactionRule.getTransactionType());
                            transaction.setNoteRuleId(transactionRule.getId());
                            break;
                        }
                    }
                }
            }
            try{
                db.saveANZTransactionNotes(statement);
            }catch(Exception exp){
                exp.printStackTrace();
            }
        }
    }

    private void createNoteFromNewRule(ANZTransactionRule transactionRule){
        for(ANZStatement statement: statements){
            int pageCount = statement.getPageCount();
            for(int pageIndex = 0; pageIndex < pageCount; pageIndex++){
                ANZPage anzPage = (ANZPage)statement.getPage(pageIndex);
                int transactionCount = anzPage.getTransactionCount();
                for(int transactionIndex = 0; transactionIndex < transactionCount; transactionIndex++){
                    ANZTransaction transaction = (ANZTransaction)anzPage.getTransaction(transactionIndex);
                    if( transactionRule.isRuleFollowed(transaction)){
                        transaction.setNoteRuleId(transactionRule.getId());
                        transaction.setNote(transactionRule.getResultNote());
                        transaction.setGstIncl(transactionRule.isGstIncl());
                        transaction.setTransactionType(transactionRule.getTransactionType());
                        try{
                            db.saveANZTransactionNote(currentStmt, transaction);
                        }catch(Exception exp){
                            exp.printStackTrace();
                        }
                    }
                }
            }
        }
        showAllNotes();
    }

    private void updateNoteFromModifiedRule(ANZTransactionRule transactionRule){
        for(ANZStatement statement: statements) {
            int pageCount = statement.getPageCount();
            for (int pageIndex = 0; pageIndex < pageCount; pageIndex++) {
                ANZPage anzPage = (ANZPage) statement.getPage(pageIndex);
                int transactionCount = anzPage.getTransactionCount();
                for (int transactionIndex = 0; transactionIndex < transactionCount; transactionIndex++) {
                    ANZTransaction transaction = (ANZTransaction) anzPage.getTransaction(transactionIndex);
                    String ruleId = transaction.getNoteRuleId();
                    if( ruleId != null && ruleId.equals(transactionRule.getId())){
                        transaction.setNote(transactionRule.getResultNote());
                        transaction.setGstIncl(transactionRule.isGstIncl());
                        transaction.setTransactionType(transactionRule.getTransactionType());
                        try{
                            db.saveANZTransactionNote(currentStmt, transaction);
                        }catch(Exception exp){
                            exp.printStackTrace();
                        }
                    }else{
                        if( transactionRule.isRuleFollowed(transaction) ) {
                            transaction.setNoteRuleId(transactionRule.getId());
                            transaction.setNote(transactionRule.getResultNote());
                            transaction.setGstIncl(transactionRule.isGstIncl());
                            transaction.setTransactionType(transactionRule.getTransactionType());
                            try {
                                db.saveANZTransactionNote(currentStmt, transaction);
                            } catch (Exception exp) {
                                exp.printStackTrace();
                            }
                        }
                    }
                }
            }
        }
        showAllNotes();
    }

    public void saveFile(){
        if( bankStmtFilePath == null || statements.size() == 0 ){
            return;
        }

        LocalDate startDate = null, endDate = null;
        for(ANZStatement statement: statements){
            if( startDate == null || startDate.isAfter(statement.getStartDate())){
                startDate = statement.getStartDate();
            }
            if( endDate == null || endDate.isBefore(statement.getEndDate())){
                endDate = statement.getEndDate();
            }
        }
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("PDF Files(*.pdf)", "*.pdf"));
        String filePath = bankStmtFilePath.substring(0, bankStmtFilePath.lastIndexOf(".")) +
                "(" + ANZUtils.DATE_MONTH_FORMATTER.format(startDate) + "-" + ANZUtils.DATE_MONTH_FORMATTER.format(endDate) + ")" + "-note.pdf";
        File targetFile = new File(filePath);

        fileChooser.setInitialFileName(targetFile.getName());
        fileChooser.setInitialDirectory(targetFile.getParentFile());
        File file = fileChooser.showSaveDialog(stage);

        if( file == null ){
            return;
        }
        try{
            AnnotationWriter writer = new AnnotationWriter(bankStmtFilePath);

            int stmtCount = statements.size();
            for(int stmtIndex = 0; stmtIndex < stmtCount; stmtIndex++) {
                ANZStatement statement = statements.get(stmtIndex);

                int pageCount = statement.getPageCount();
                for (int index = 0; index < pageCount; index++) {
                    ANZPage anzPage = (ANZPage) statement.getPage(index);
                    int transactionCount = anzPage.getTransactionCount();

                    for(int transactionIndex = 0; transactionIndex < transactionCount; transactionIndex++){
                        ANZTransaction transaction = (ANZTransaction)anzPage.getTransaction(transactionIndex);
                        String note = transaction.getNote();
                        Color textColor = AnnotationWriter.Annotation.DEFAULT_TEXT_COLOR;
                        double y = transaction.getPdfVCoordinate().getBottomY();
                        double x = anzPage.getPdfPageHCoordinate().getDepositStartX();
                        boolean endValueX = true;
                        if( transaction.getAmount() < 0 ){
                            x = anzPage.getPdfPageHCoordinate().getWithdrawEndX() + 10d;
                            endValueX = false;
                            textColor = Color.RED;
                        }
                        AnnotationWriter.Annotation annotation = new AnnotationWriter.Annotation(note, anzPage.getPdfDocumentPageNumber()-1, x, y, endValueX, textColor);
                        writer.writeAnnotation(annotation);
                    }
                }
            }

            writer.save(file.getAbsolutePath());

            if(Desktop.isDesktopSupported()){
                Desktop.getDesktop().open(file);
            }
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }

    private void initImageOfPdf(ImgOfPdfPage imgOfPdfPage){
        BorderPane borderPane = new BorderPane();
        ImageView imgView = new ImageView(new javafx.scene.image.Image(new File(imgOfPdfPage.getThumbnailFilePath()).toURI().toString()));
        imgView.setFitHeight(imgOfPdfPage.getThumbnailHeight());
        imgView.setFitWidth(imgOfPdfPage.getThumbnailWidth());
        borderPane.setCenter(imgView);
        borderPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));

        BorderPane thumbnailPane = new BorderPane();
        thumbnailPane.setCenter(borderPane);
        Label label = new Label();
        label.setText(String.valueOf(imgOfPdfPage.getPageIndex()+1));
        BorderPane.setAlignment(label, Pos.CENTER);
        label.setPadding(new Insets(5, 0, 5, 0));
        thumbnailPane.setBottom(label);
        if( !task.isCancelled() ) {
            thumbnailBox.getChildren().add(thumbnailPane);
            thumbnailPane.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    setSelectedPageIndex(imgOfPdfPage.getPageIndex());
                }
            });
        }

        int pageIndex = imgOfPdfPage.getPageIndex();
        if( pageIndex == 0 ) {
            setSelectedPageIndex(0);
        }
    }

    private void setSelectedPageIndex(int index){
        if( selectedPageIndex == index ){
            return;
        }
        pdfPageAnchorPane.getChildren().remove(selectedTransactionRect);
        pdfPageAnchorPane.getChildren().remove(noteField);
        for(Label label: noteLabels.values()){
            pdfPageAnchorPane.getChildren().remove(label);
        }
        noteLabels.clear();

        if( selectedPageIndex != -1 ){
            Node node = thumbnailBox.getChildren().get(selectedPageIndex);
            if( node != null && node instanceof BorderPane){
                BorderPane borderPane = (BorderPane)node;
                Node center = borderPane.getCenter();
                if( center != null && center instanceof BorderPane){
                    BorderPane imgPane = (BorderPane)center;
                    imgPane.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
                }
                Node bottom = borderPane.getBottom();
                if( bottom != null && bottom instanceof Label){
                    Label label = (Label)bottom;
                    label.setTextFill(Color.BLACK);
                }
            }
        }

        selectedPageIndex = index;

        if( selectedPageIndex != -1 ){
            Node node = thumbnailBox.getChildren().get(selectedPageIndex);
            if( node != null && node instanceof BorderPane){
                BorderPane borderPane = (BorderPane)node;
                Node center = borderPane.getCenter();
                if( center != null && center instanceof BorderPane){
                    BorderPane imgPane = (BorderPane)center;
                    imgPane.setBorder(new Border(new BorderStroke(Color.RED, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, new BorderWidths(1))));
                }
                Node bottom = borderPane.getBottom();
                if( bottom != null && bottom instanceof Label){
                    Label label = (Label)bottom;
                    label.setTextFill(Color.RED);
                }
            }

            ImgOfPdfPage imgOfPdfPage = imgOfPdfPages.get(selectedPageIndex);
            pdfPageImgView.setFitWidth(imgOfPdfPage.getPdfPageWidth() * currentZoomRate);
            pdfPageImgView.setFitHeight(imgOfPdfPage.getPdfPageHeight() * currentZoomRate);

            pdfPageImgView.setImage(new Image(new File(imgOfPdfPage.getImgFilePath()).toURI().toString()));
            if( !pdfPageAnchorPane.isVisible()){
                pdfPageAnchorPane.setVisible(true);
            }

            currentStmt = null;
            selectedPage = null;
            selectedTransaction = null;
            int pdfPageIndex = imgOfPdfPage.getPageIndex();
            for(ANZStatement anzStatement: statements){
                int count = anzStatement.getPageCount();
                for(int pageIndex = 0; pageIndex < count; pageIndex++){
                    ANZPage anzPage = (ANZPage)anzStatement.getPage(pageIndex);
                    if(anzPage.getPdfDocumentPageNumber() == pdfPageIndex + 1){
                        currentStmt = anzStatement;
                        selectedPage = anzPage;
                        break;
                    }
                }
                if(selectedPage != null){
                    break;
                }
            }

            showAllNotes();
        }else{
            currentStmt = null;
            selectedPage = null;
            selectedTransaction = null;
            pdfPageAnchorPane.getChildren().remove(selectedTransactionRect);
            pdfPageAnchorPane.getChildren().remove(noteField);
            for(Label label: noteLabels.values()){
                pdfPageAnchorPane.getChildren().remove(label);
            }
            noteLabels.clear();
        }
    }

    private void cancelPdfToImgTask(){
        if( task != null && task.isRunning() ){
            task.cancel();
        }
    }

    private void clearImages(){
        thumbnailBox.getChildren().clear();
        pdfPageImgView.setImage(null);
        selectedPageIndex = -1;
        statements.clear();
        imgOfPdfPages.clear();
        pdfPageAnchorPane.getChildren().remove(selectedTransactionRect);
        pdfPageAnchorPane.getChildren().remove(noteField);
        for(Label label: noteLabels.values()){
            pdfPageAnchorPane.getChildren().remove(label);
        }
        noteLabels.clear();
    }

}
