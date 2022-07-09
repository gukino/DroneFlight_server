package hku.droneflight.util;

/**
 *
 */

public class ServerReq extends RequestMsg{
    public String serverId;

    public ServerReq(){}

    public ServerReq(String serverId) {
        this.serverId = serverId;
    }
}
