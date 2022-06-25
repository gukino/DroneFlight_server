package hku.droneflight.util;

/**
 *
 */

public class UrlRsp extends ResponseMsg{
    public String url;

    public UrlRsp(Result result) {
        super(result);
    }

    public UrlRsp(Result result, String reason) {
        super(result, reason);
    }
}
