package hku.droneflight.util;

/**
 *
 */

public class UrlRsp extends ResponseMsg{
    public String streamUrl;
    public String resultUrl;

    public UrlRsp(Result result) {
        super(result);
    }

    public UrlRsp(Result result, String reason) {
        super(result, reason);
    }
}
