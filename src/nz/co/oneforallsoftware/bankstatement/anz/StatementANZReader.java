package nz.co.oneforallsoftware.bankstatement.anz;

import nz.co.oneforallsoftware.bankstatement.Utils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDFont;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class StatementANZReader {
    private ANZStatementStripper stripper;

    public  StatementANZReader() throws Exception{
    }

    public ArrayList<ANZStatement> readStatement(File pdfStmt) throws IOException{
        stripper = new ANZStatementStripper(pdfStmt.getAbsolutePath());
        PDDocument document = PDDocument.load(pdfStmt);
        stripper.getText(document);
        document.close();

        StatementANZ statementANZ = stripper.getStatement();

        ArrayList<ANZStatement> statements = new ArrayList<>();
        statementANZ.process(statements);

        return statements;
    }

    private static class ANZStatementStripper extends PDFTextStripper{
        private boolean startOfLine;
        private int pageNumber = 0;
        private int lineNumber = 0;
        private StatementANZ statement;
        private PageANZ currentPage;
        private LineANZ currentLine;

        public ANZStatementStripper(String fileName) throws IOException {
            startOfLine = true;
            setSortByPosition( true );
            statement = new StatementANZ(fileName);
        }

        protected void startPage(PDPage page) throws IOException {
            pageNumber ++;
            PDRectangle mediaBox = page.getMediaBox();
            boolean isLandscape = mediaBox.getWidth() > mediaBox.getHeight();
            int rotation = page.getRotation();
            if (rotation == 90 || rotation == 270) {
                isLandscape = !isLandscape;
            }
            currentPage = new PageANZ(pageNumber, page.getMediaBox().getWidth(), page.getMediaBox().getHeight());
            if(isLandscape){
                currentPage.setOrientation(Utils.PdfPageOrientation.LANDSCAPE);
            }else{
                currentPage.setOrientation(Utils.PdfPageOrientation.PORTRAIT);
            }
            statement.addPage(currentPage);
            //System.out.println("Page Number " + pageNumber);
            lineNumber = 0;
            startOfLine = true;
            super.startPage(page);
        }

        @Override
        protected void writeLineSeparator() throws IOException {
            lineNumber++;
            //System.out.println("Line Number " + lineNumber);
            startOfLine = true;
            super.writeLineSeparator();
        }

        @Override
        protected void writeString(String text, List<TextPosition> textPositions) throws IOException {
            TextPosition firstPosition = textPositions.get(0);
            TextPosition lastPosition = textPositions.get(textPositions.size()-1);
            double startX = firstPosition.getX();
            double endX = lastPosition.getEndX();
            double bottomY = firstPosition.getEndY();
            double height = lastPosition.getFontSize() / 6d;
            /*
            if (height != 0) {
                System.out.println("Text Position Height " + height  + " Bottom Y " + bottomY);
            }

            PDFont font = lastPosition.getFont();
            String fontName = font.getName();
            height = font.getFontDescriptor().getFontBoundingBox().getHeight();
            //System.out.println("Font " + fontName);
            if (height != 0) {
                System.out.println("Text Position Matrix Height " + height  + " Bottom Y " + bottomY);
            }else{
                //System.out.println("Text Position Height 0 ");
            }
            for(TextPosition textPosition: textPositions) {
                height = textPosition.getFont().getBoundingBox().getHeight();
                if (height != 0) {
                    //System.out.println("Text Position Height " + height);
                }
            }
            */
            WordANZ word = new WordANZ(text, startX, endX, bottomY, height);
            if (startOfLine) {
                currentLine = new LineANZ(pageNumber, lineNumber, word);
                currentPage.addLine(currentLine);
                //writeString(String.format("[%s %s]", firstPosition.getEndX(), firstPosition.getX()));
                startOfLine = false;
            }else{
                currentLine.addWord(word);
            }
            super.writeString(text, textPositions);
        }

        protected StatementANZ getStatement(){
            return statement;
        }
    }
}
