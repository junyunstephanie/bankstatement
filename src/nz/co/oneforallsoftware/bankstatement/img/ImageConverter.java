package nz.co.oneforallsoftware.bankstatement.img;

import net.coobird.thumbnailator.Thumbnails;
import nz.co.oneforallsoftware.bankstatement.MainPane;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;
import org.apache.pdfbox.tools.imageio.ImageIOUtil;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;

public class ImageConverter {
    private static final double DEFAULT_THUMBNAIL_WIDTH = 100d;
    private static final String DEFAULT_DIR = "pdfimg/";
    private String dir;
    private Listener listener;
    private boolean cancelled = false;

    public ImageConverter()throws Exception{
        this(DEFAULT_DIR, null);
    }

    public ImageConverter(Listener listener)throws Exception{
        this(DEFAULT_DIR, listener);
    }
    public ImageConverter(String dir, Listener listener)throws Exception{
        if(dir != null) {
            this.dir = dir;
        }else{
            this.dir = DEFAULT_DIR;
        }

        File dirFile = new File(dir);
        if(!dirFile.exists() || !dirFile.isDirectory()){
            if( !dirFile.mkdirs() ){
                throw new Exception("Failed to create directory for created image files");
            }
        }
        this.listener = listener;
    }

    public void cancel(){
        cancelled = true;
    }

    public ArrayList<ImgOfPdfPage> convert(String pdfFilename)throws Exception{
        File pdfFile = new File(pdfFilename);
        String filename = pdfFile.getName();
        String dirName = dir + filename.substring(0, filename.lastIndexOf(".")) + "/";
        File dirFile = new File(dirName);
        if(!dirFile.exists()|| !dirFile.isDirectory()){
            if( !dirFile.mkdirs() ){
                throw new Exception("Failed to create directory for created image files");
            }
        }
        String thumbnailDirName = dirName + "/thumbnails/";
        File thumbnailDirFile = new File(thumbnailDirName);
        if( !thumbnailDirFile.exists() || !thumbnailDirFile.isDirectory()){
            if( !thumbnailDirFile.mkdirs() ){
                throw  new Exception("Failed creating thumbnail directory");
            }
        }
        String thumbnailFilename = thumbnailDirName + filename.substring(0, filename.lastIndexOf("."));
        filename = dirName + filename.substring(0, filename.lastIndexOf("."));

        PDDocument document = PDDocument.load(new File(pdfFilename));
        int numberOfPages = document.getNumberOfPages();
        if( listener != null ){
            listener.setTotalPageCount(numberOfPages);
        }
        PDFRenderer pdfRenderer = new PDFRenderer(document);

        ArrayList<ImgOfPdfPage> imgOfPdfPages = new ArrayList<>();
        for (int page = 0; page < document.getNumberOfPages(); ++page) {
            if( cancelled ){
                System.out.println("Cancelled at page " + page);
                return imgOfPdfPages;
            }

            PDPage pdfPage = document.getPage(page);
            double pdfPageWidth = pdfPage.getMediaBox().getWidth();
            double pdfPageHeight = pdfPage.getMediaBox().getHeight();

            BufferedImage bim = pdfRenderer.renderImageWithDPI(page, MainPane.setting.getPdf2ImgDPI().getDpi(), ImageType.RGB);

            String imgFile = filename + "-" + (page+1) + ".png";
            String thumbnailFile = thumbnailFilename + "-" + (page+1) + ".png";
            ImageIOUtil.writeImage(bim, imgFile, MainPane.setting.getPdf2ImgDPI().getDpi());

            SimpleImageInfo imageInfo = new SimpleImageInfo(new File(imgFile));
            double imgWidth = imageInfo.getWidth();
            double imgHeight = imageInfo.getHeight();

            int thumbnailWidth = (int)DEFAULT_THUMBNAIL_WIDTH;
            int thumbnailHeight = (int)(imgHeight / imgWidth * DEFAULT_THUMBNAIL_WIDTH + 0.5);

            if( imgHeight < imgWidth ){
                thumbnailHeight = (int)DEFAULT_THUMBNAIL_WIDTH;
                thumbnailWidth = (int)(imgWidth / imgHeight * DEFAULT_THUMBNAIL_WIDTH + 0.5);
            }
            Thumbnails.of(imgFile)
                    .size(thumbnailWidth, thumbnailHeight)
                    .toFile(thumbnailFile);
            ImgOfPdfPage imgOfPdfPage = new ImgOfPdfPage(pdfFilename, page, imgFile, thumbnailFile, pdfPageWidth, pdfPageHeight, imgWidth, imgHeight, thumbnailWidth, thumbnailHeight);
            imgOfPdfPages.add(imgOfPdfPage);
            if( listener != null ){
                listener.newImage(imgOfPdfPage, numberOfPages);
            }
        }
        document.close();
        return imgOfPdfPages;
    }

    public static interface Listener{
        public void setTotalPageCount(int pageCount);
        public void newImage(ImgOfPdfPage imgOfPdfPage, int totalPageCount);
    }
}
