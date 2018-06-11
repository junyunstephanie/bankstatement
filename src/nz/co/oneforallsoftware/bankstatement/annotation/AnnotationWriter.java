package nz.co.oneforallsoftware.bankstatement.annotation;

import javafx.scene.paint.Color;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.pdmodel.font.PDType1Font;

import java.io.File;

public class AnnotationWriter {
    private static final PDFont DEFAULT_FONT = PDType1Font.HELVETICA_OBLIQUE;
    private static final int DEFAULT_FONT_SIZE = 7;
    private PDDocument document;
    private PDFont font;
    private int fontSize;

    public AnnotationWriter(String pdfFile)throws Exception{
        document = PDDocument.load(new File((pdfFile)));
        font = DEFAULT_FONT;
        fontSize = DEFAULT_FONT_SIZE;
    }

    public void writeAnnotation(Annotation annotation)throws Exception{
        PDPage page = document.getPage(annotation.pageIndex);
        PDPageContentStream content = new PDPageContentStream(document, page, PDPageContentStream.AppendMode.APPEND, false, false);

        content.setFont(font, fontSize);

        Color textColor = annotation.getTextColor();
        content.setNonStrokingColor((int)(textColor.getRed() * 255d), (int)(textColor.getGreen() * 255d), (int)(textColor.getBlue() * 255d)); //black text

        content.moveTo(0,0);
        content.beginText();
        content.setFont(font, fontSize);
        if( annotation.isEndValueX() ){
            float width = font.getStringWidth(annotation.getText()) / 1000f * fontSize;
            content.newLineAtOffset((float) annotation.xValue - width - 10f, (float) annotation.yValue);
        }else {
            content.newLineAtOffset((float) annotation.xValue, (float) annotation.yValue);
        }
        content.showText(annotation.text);
        content.endText();

        content.close();
    }
    public void save(String destFile)throws Exception{
        document.save(new File(destFile));
        document.close();
    }

    public static class Annotation{
        public static final Color DEFAULT_TEXT_COLOR = Color.rgb(125, 125, 125);

        private String text;
        private int pageIndex;
        private double xValue, yValue;
        private boolean endValueX = false;

        private Color textColor;

        public Annotation(String text, int pageIndex, double x, double y, boolean endValueX, Color color){
            this.pageIndex = pageIndex;
            xValue = x;
            yValue = y;
            this.endValueX = endValueX;
            if( text == null ){
                this.text = "";
            }else {
                this.text = text;
            }
            textColor = color;
        }

        public Annotation(String text, int pageIndex, double x, double y, boolean endValueX){
            this(text, pageIndex, x, y, endValueX, DEFAULT_TEXT_COLOR);
        }

        public boolean isEndValueX() {
            return endValueX;
        }

        public void setEndValueX(boolean endValueX) {
            this.endValueX = endValueX;
        }

        public Color getTextColor() {
            return textColor;
        }

        public void setTextColor(Color textColor) {
            this.textColor = textColor;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getPageIndex() {
            return pageIndex;
        }

        public void setPageIndex(int pageIndex) {
            this.pageIndex = pageIndex;
        }

        public double getxValue() {
            return xValue;
        }

        public void setxValue(double xValue) {
            this.xValue = xValue;
        }

        public double getyValue() {
            return yValue;
        }

        public void setyValue(double yValue) {
            this.yValue = yValue;
        }
    }
}
