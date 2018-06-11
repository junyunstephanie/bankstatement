package nz.co.oneforallsoftware.bankstatement.concurrence;

import javafx.concurrent.Task;
import nz.co.oneforallsoftware.bankstatement.img.ImageConverter;
import nz.co.oneforallsoftware.bankstatement.img.ImgOfPdfPage;

import java.util.ArrayList;

public class PdfToImgTask extends Task<ImgOfPdfPage> {
    private String pdfFilePath;
    private ImageConverter converter;

    public PdfToImgTask(String pdfFilePath){
        this.pdfFilePath = pdfFilePath;
    }
    @Override
    protected ImgOfPdfPage call() throws Exception {

        converter = new ImageConverter(new ImageConverter.Listener() {
            @Override
            public void setTotalPageCount(int pageCount) {
                updateProgress(0, pageCount);
            }

            @Override
            public void newImage(ImgOfPdfPage imgOfPdfPage, int totalPageCount) {
                if( isCancelled() ){
                    converter.cancel();
                }
                updateProgress(imgOfPdfPage.getPageIndex() + 1, totalPageCount);
                updateValue(imgOfPdfPage);
            }
        });
        ArrayList<ImgOfPdfPage> imgOfPdfPages = converter.convert(pdfFilePath);
        if( imgOfPdfPages == null || imgOfPdfPages.size() == 0 ){
            return null;
        }else {
            return imgOfPdfPages.get(imgOfPdfPages.size() - 1);
        }
    }
}
