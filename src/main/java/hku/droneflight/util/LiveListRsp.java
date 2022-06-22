package hku.droneflight.util;

import hku.droneflight.entity.Video;
import java.util.List;

/**
 *
 */

public class LiveListRsp extends ResponseMsg {
    public List<String> streamUrls;
    public List<String> resultUrls;

    public LiveListRsp(Result result) {
        super(result);
    }

    public LiveListRsp(Result result, String reason) {
        super(result, reason);
    }
}
