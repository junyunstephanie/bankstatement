package nz.co.oneforallsoftware.bankstatement.img;

public class ImgOfPdfPage {
    private String pdfFilePath;
    private int pageIndex;
    private String imgFilePath, thumbnailFilePath;
    private double pdfPageWidth, pdfPageHeight;
    private double imgWidth, imgHeight;
    private double thumbnailWidth, thumbnailHeight;

    public ImgOfPdfPage(String pdfFilePath, int pageIndex, String imgFilePath, String thumbnailFilePath, double pdfPageWidth, double pdfPageHeight, double imgWidth, double imgHeight, double thumbnailWidth, double thumbnailHeight) {
        this.pdfFilePath = pdfFilePath;
        this.pageIndex = pageIndex;
        this.imgFilePath = imgFilePath;
        this.thumbnailFilePath = thumbnailFilePath;
        this.pdfPageWidth = pdfPageWidth;
        this.pdfPageHeight = pdfPageHeight;
        this.imgWidth = imgWidth;
        this.imgHeight = imgHeight;
        this.thumbnailWidth = thumbnailWidth;
        this.thumbnailHeight = thumbnailHeight;
    }

    public String getPdfFilePath() {
        return pdfFilePath;
    }

    public int getPageIndex() {
        return pageIndex;
    }

    public String getImgFilePath() {
        return imgFilePath;
    }

    public String getThumbnailFilePath() {
        return thumbnailFilePath;
    }

    public double getPdfPageWidth() {
        return pdfPageWidth;
    }

    public double getPdfPageHeight() {
        return pdfPageHeight;
    }

    public double getImgWidth() {
        return imgWidth;
    }

    public double getImgHeight() {
        return imgHeight;
    }

    public double getThumbnailWidth() {
        return thumbnailWidth;
    }

    public double getThumbnailHeight() {
        return thumbnailHeight;
    }
}
