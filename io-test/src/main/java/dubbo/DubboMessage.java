package dubbo;

/**
 * Created by fang_j on 2021/07/16.
 */
public class DubboMessage {
    private long startReceivingTime;
    private long completeReceivingTime;

    private byte[] headerData;
    private byte[] bodyData;

    private long id;
    private boolean isHeartbeat;
    private Object heartbeatData;

    public long getStartReceivingTime() {
        return startReceivingTime;
    }

    public void setStartReceivingTime(long startReceivingTime) {
        this.startReceivingTime = startReceivingTime;
    }

    public long getCompleteReceivingTime() {
        return completeReceivingTime;
    }

    public void setCompleteReceivingTime(long completeReceivingTime) {
        this.completeReceivingTime = completeReceivingTime;
    }

    public byte[] getHeaderData() {
        return headerData;
    }

    public void setHeaderData(byte[] headerData) {
        this.headerData = headerData;
    }

    public byte[] getBodyData() {
        return bodyData;
    }

    public void setBodyData(byte[] bodyData) {
        this.bodyData = bodyData;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public boolean isHeartbeat() {
        return isHeartbeat;
    }

    public void setHeartbeat(boolean heartbeat) {
        isHeartbeat = heartbeat;
    }

    public Object getHeartbeatData() {
        return heartbeatData;
    }

    public void setHeartbeatData(Object heartbeatData) {
        this.heartbeatData = heartbeatData;
    }
}
