package nz.co.oneforallsoftware.bankstatement;

import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class ProgressDialog extends Stage {

    private ProgressPane progressPane;

    public ProgressDialog(String initMessage, ProgressCancelledListener listener) {
        super();
        this.initStyle(StageStyle.UNDECORATED);
        this.initModality(Modality.APPLICATION_MODAL);

        progressPane = new ProgressPane(initMessage, listener);
        setScene(new Scene(progressPane));
    }

    public void setMessage(Stage parentStage, String message) {
        System.out.println("Progress dialog message " + message);
        progressPane.msgLabel.setText(message);
        this.sizeToScene();
        showDialog(parentStage);
    }

    public void showDialog(Stage parentStage) {
        if( !this.isShowing() ) {
            show();
        }

        double width = this.getWidth();
        double height = this.getHeight();

        double parentWidth = 0;
        double parentHeight = 0;
        double parentX = 0;
        double parentY = 0;

        if( parentStage != null && parentStage.isShowing()) {
            parentX = parentStage.getX();
            parentY = parentStage.getY();

            parentWidth = parentStage.getWidth();
            parentHeight = parentStage.getHeight();
        }else {
            Rectangle2D primaryScreenBounds = Screen.getPrimary().getVisualBounds();
            parentWidth = primaryScreenBounds.getWidth();
            parentHeight = primaryScreenBounds.getHeight();
        }

        setX(parentX + (parentWidth - width)/2);
        setY(parentY + (parentHeight - height)/2);
    }

    private class ProgressPane extends BorderPane {
        private Label msgLabel;
        private ImageView cancelImgView;

        private ProgressPane(String message, ProgressCancelledListener listener) {
            this.setBorder(new Border(new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

            BorderPane left = new BorderPane();
            left.setPadding(new Insets(10, 10, 10, 10));
            Image img = new Image(this.getClass().getResourceAsStream("/images/ajax_loader_blue_32.gif"));
            ImageView imgView = new ImageView(img);
            left.setCenter(imgView);
            this.setLeft(left);

            if( message != null ) {
                msgLabel = new Label(message);
            }else {
                msgLabel = new Label();
            }

            msgLabel.setWrapText(true);
            msgLabel.setMaxWidth(500);
            msgLabel.setTextAlignment(TextAlignment.LEFT);

            this.setCenter(msgLabel);

            BorderPane right = new BorderPane();
            right.setPadding(new Insets(3, 3, 0, 0));
            Image cancelLightImg = new Image(this.getClass().getResourceAsStream("/images/cancel_round_light_red_16.png"));
            Image cancelDarkImg = new Image(this.getClass().getResourceAsStream("/images/cancel_round_dark_red_16.png"));
            cancelImgView = new ImageView(cancelLightImg);

            right.setTop(cancelImgView);

            cancelImgView.addEventFilter(MouseEvent.MOUSE_PRESSED, new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent arg0) {
                    if( arg0.getButton() == MouseButton.PRIMARY ) {
                        ProgressDialog.this.close();
                        if( listener != null ) {
                            listener.onCancelled();
                        }
                    }
                }

            });

            cancelImgView.addEventFilter(MouseEvent.MOUSE_ENTERED, new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent arg0) {
                    cancelImgView.setImage(cancelDarkImg);
                }

            });

            cancelImgView.addEventFilter(MouseEvent.MOUSE_EXITED, new EventHandler<MouseEvent>() {

                @Override
                public void handle(MouseEvent arg0) {
                    cancelImgView.setImage(cancelLightImg);
                }

            });
            this.setRight(right);
        }
    }

    public interface ProgressCancelledListener{
        public void onCancelled();
    }
}
