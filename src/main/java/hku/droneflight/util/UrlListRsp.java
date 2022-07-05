package hku.droneflight.util;

import hku.droneflight.entity.User;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author yhp
 * @create 2022-06-28 17:50
 */
public class UrlListRsp extends UrlRsp{

    public List<Url> urlRspList;
    public UrlListRsp(Result result) {
        super(result);
    }
    public UrlListRsp(Result result, String reason) {
        super(result, reason);
    }
}
