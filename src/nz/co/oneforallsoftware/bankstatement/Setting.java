package nz.co.oneforallsoftware.bankstatement;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class Setting {
    public static enum PdfZoomRate{
        ONE("100%"),
        ONE_AND_HALF("150%"),
        TWO("200%"),
        TWO_AND_HALF("250%"),
        THREE("300%");

        PdfZoomRate(String percent){
            this.percent = percent;
        }

        private String percent;

        public String toString(){
            return percent;
        }

        public double getDoubleValue(){
            String str = percent.substring(0, 3);
            return Double.parseDouble(str)/100d;
        }
    }

    public static enum Pdf2ImgDPI{
        HIGH(400),
        GOOD(300),
        NORMAL(200);

        Pdf2ImgDPI(int dpi){
            this.dpi = dpi;
        }

        private int dpi;
        public String toString(){
            return dpi + "dpi";
        }

        public int getDpi(){
            return dpi;
        }
    }

    private static final String FILE_DIR = "/setting/";
    private static final String FILE_NAME = "setting.txt";
    private static final String BANK_STATEMENT_DIR = "/bank statements/";

    public static final String[] PDF_ZOOM_RATES = new String[]{"100%", "150%", "200%", "250%", "300%"};

    private PdfZoomRate pdfZoomRate;
    private Pdf2ImgDPI pdf2ImgDPI;
    private String bankStatementDir;

    private Setting(){
        pdfZoomRate = PdfZoomRate.TWO;
        pdf2ImgDPI = Pdf2ImgDPI.NORMAL;
        bankStatementDir = "./." + BANK_STATEMENT_DIR;
        File stmtDir = new File(bankStatementDir);
        if(!stmtDir.exists() || !stmtDir.isDirectory()){
            stmtDir.mkdirs();
        }
    }

    public PdfZoomRate getPdfZoomRate() {
        return pdfZoomRate;
    }

    public void setPdfZoomRate(PdfZoomRate pdfZoomRate) {
        this.pdfZoomRate = pdfZoomRate;
    }

    public Pdf2ImgDPI getPdf2ImgDPI() {
        return pdf2ImgDPI;
    }

    public void setPdf2ImgDPI(Pdf2ImgDPI pdf2ImgDPI) {
        this.pdf2ImgDPI = pdf2ImgDPI;
    }

    public String getBankStatementDir() {
        return bankStatementDir;
    }

    public void setBankStatementDir(String bankStatementDir) {
        this.bankStatementDir = bankStatementDir;
    }

    public static Setting loadSetting(){
        File dir = new File(FILE_DIR);
        if(!dir.exists()|| dir.isDirectory()){
            return null;
        }

        File file = new File(FILE_DIR + FILE_NAME);
        if(!file.exists() || !file.isFile()){
            return null;
        }

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String str = "";
            String line = reader.readLine();
            while (line != null) {
                str = str + "\n" + line;
                line = reader.readLine();
            }
            reader.close();

            Gson gson = new Gson();
            Setting setting = gson.fromJson(str, Setting.class);
            return setting;
        }catch(Exception exp){
            exp.printStackTrace();
            return null;
        }
    }

    public static Setting getDefaultSetting(){
        File dir = new File(FILE_DIR);
        if(!dir.exists()|| !dir.isDirectory()){
            if(!dir.mkdirs()){
                return null;
            }
        }

        Setting setting = new Setting();
        setting.save();
        return setting;
    }

    public void save(){
        try {
            File dir = new File(FILE_DIR);
            if (!dir.exists() || dir.isDirectory()) {
                dir.mkdirs();
            }
            File file = new File(FILE_DIR + FILE_NAME);
            Gson gson = new Gson();
            String json = gson.toJson(this, this.getClass());
            FileWriter writer = new FileWriter(file);
            writer.write(json);
            writer.close();
        }catch(Exception exp){
            exp.printStackTrace();
        }
    }
}
