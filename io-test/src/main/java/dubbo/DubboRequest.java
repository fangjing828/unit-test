package dubbo;

import java.util.Map;

/**
 * Created by fang_j on 2021/07/16.
 */
public class DubboRequest extends DubboMessage {
    private boolean twoWay;
    private boolean event;
    private String path;
    private String version;
    private String method;
    private Object data;
    private Map<String, String> attachments;
    private byte contentTypeId;

    public boolean isTwoWay() {
        return twoWay;
    }

    public void setTwoWay(boolean twoWay) {
        this.twoWay = twoWay;
    }

    public boolean isEvent() {
        return event;
    }

    public void setEvent(boolean event) {
        this.event = event;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public Map<String, String> getAttachments() {
        return attachments;
    }

    public void setAttachments(Map<String, String> attachments) {
        this.attachments = attachments;
    }

    public byte getContentTypeId() {
        return contentTypeId;
    }

    public void setContentTypeId(byte contentTypeId) {
        this.contentTypeId = contentTypeId;
    }
}
