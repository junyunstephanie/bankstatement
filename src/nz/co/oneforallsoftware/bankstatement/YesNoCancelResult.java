package nz.co.oneforallsoftware.bankstatement;

public class YesNoCancelResult {
    private Object resultObject = null;

    public YesNoCancelResult() {
        this.result = Result.CANCEL;

    }

    public YesNoCancelResult(Result result) {
        super();
        this.result = result;

    }
    public YesNoCancelResult(Result result, boolean repeat) {
        super();
        this.result = result;

    }
    public static enum Result{
        YES,
        NO,
        CANCEL;
    }

    private Result result;

    public Result getResult() {
        return result;
    }
    public void setResult(Result result) {
        this.result = result;
    }

    public Object getResultObject() {
        return resultObject;
    }

    public void setResultObject(Object resultObject) {
        this.resultObject = resultObject;
    }
}
