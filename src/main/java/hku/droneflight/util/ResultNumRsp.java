package hku.droneflight.util;

import hku.droneflight.entity.ResultNum;
import java.util.List;

/**
 *
 */

public class ResultNumRsp extends ResponseMsg{
    public List<ResultNum> resultNumList;

    public ResultNumRsp(Result result) {
        super(result);
    }

    public ResultNumRsp(Result result, String reason) {
        super(result, reason);
    }

    public ResultNumRsp(Result result, List<ResultNum> resultNumList) {
        super(result);
        this.resultNumList = resultNumList;
    }
}
