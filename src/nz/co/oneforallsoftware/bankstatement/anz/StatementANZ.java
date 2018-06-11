package nz.co.oneforallsoftware.bankstatement.anz;

import java.util.ArrayList;

public class StatementANZ {
    private String fileName;
    private ArrayList<PageANZ> pages = new ArrayList<>();

    public StatementANZ(String fileName){
        this.fileName = fileName;
    }

    public int getPageCount(){
        return pages.size();
    }

    public PageANZ getPage(int index){
        if( index < 0 || index >= pages.size() ){
            return null;
        }
        return pages.get(index);
    }

    public void addPage(PageANZ page){
        pages.add(page);
    }

    public void process(ArrayList<ANZStatement> statements){
        for(PageANZ page: pages){
            page.process(statements, fileName);
        }
    }
}
