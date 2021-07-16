package dubbo;

import com.sun.org.apache.xerces.internal.impl.dv.util.Base64;
import dubbo.exception.DubboSerializationException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.ReplayingDecoder;
import org.apache.commons.lang.ArrayUtils;
import org.apache.dubbo.common.io.Bytes;
import org.apache.dubbo.common.serialize.ObjectInput;
import org.apache.dubbo.common.serialize.Serialization;
import org.apache.dubbo.remoting.exchange.Response;
import org.apache.dubbo.remoting.transport.CodecSupport;
import org.apache.dubbo.rpc.protocol.dubbo.DubboCodec;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;

import static dubbo.DubboMessageEncoder.*;
import static org.apache.dubbo.common.utils.ReflectUtils.DESC_PATTERN;

/**
 * Created by fang_j on 2021/07/16.
 */
public class DubboMessageDecoder extends ReplayingDecoder<DubboMessageDecoder.State> {
    private static final Logger logger = LoggerFactory.getLogger(DubboMessageDecoder.class);

    enum State {
        READ_HEADER, READ_BODY
    }

    private long startReceivingTime;
    private boolean errorProcessed = false;
    private byte[] header;

    public DubboMessageDecoder() {
        super(State.READ_HEADER);
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        switch (state()) {
            case READ_HEADER: {
                startReceivingTime = System.currentTimeMillis();
                header = new byte[HEADER_LENGTH];
                in.readBytes(header);
                try {
                    validateHeader(header);
                    checkpoint(State.READ_BODY);
                } catch (Exception ex) {
                    logger.warn("Failed to decode Dubbo message header.\nData: " + Base64.encode(header), ex);
                    if (errorProcessed) {
                        return;
                    }
                    errorProcessed = true;
                    ctx.fireExceptionCaught(ex);
                    ctx.close();
                }
                break;
            }
            case READ_BODY: {
                int bodyLength = Bytes.bytes2int(header, 12);
                byte[] body = new byte[bodyLength];
                in.readBytes(body);

                try {
                    long id = Bytes.bytes2long(header, 4);
                    byte flag = header[2], proto = (byte) (flag & SERIALIZATION_MASK);
                    boolean isHeartbeat = (flag & FLAG_EVENT) != 0;
                    Serialization s = CodecSupport.getSerialization(DUMMY_DUBBO_URL, proto);

                    ObjectInput objectInput =
                            s.deserialize(DUMMY_DUBBO_URL, new ByteArrayInputStream(body));
                    DubboMessage message;
                    if ((flag & FLAG_REQUEST) == 0) {
                        message = new DubboResponse();
                        message.setHeaderData(header);
                        message.setBodyData(body);
                        message.setId(id);
                        message.setHeartbeat(isHeartbeat);
                        fillResponse(objectInput, (DubboResponse) message);
                    } else {
                        message = new DubboRequest();
                        message.setHeaderData(header);
                        message.setBodyData(body);
                        message.setId(id);
                        message.setHeartbeat(isHeartbeat);
                        fillRequest(objectInput, (DubboRequest) message);
                    }

                    message.setStartReceivingTime(startReceivingTime);
                    message.setCompleteReceivingTime(System.currentTimeMillis());

                    out.add(message);
                    checkpoint(State.READ_HEADER);
                } catch (Throwable t) {
                    byte[] fullData = ArrayUtils.addAll(header, body);
                    String msg = "Failed to decode Dubbo message body from" + ctx.channel().remoteAddress() + ".\nFullData: " + Base64.encode(fullData);
                    logger.warn(msg, t);
                    if (!errorProcessed) {
                        return;
                    }
                    errorProcessed = true;
                    ctx.fireExceptionCaught(t);
                    ctx.close();
                }

                break;
            }
        }
    }

    private static void validateHeader(byte[] header) {
        if (header[0] != MAGIC_HIGH || header[1] != MAGIC_LOW) {
            throw new DecoderException(
                    "Illegal Dubbo header: Magic mismatched. Expected=" + Integer.toHexString(MAGIC)
                            + " Actual=" + Integer.toHexString((header[0] << 8) | header[1] & 0xff));
        }
    }

    @SuppressWarnings("unchecked")
    static void fillRequest(ObjectInput in, DubboRequest request) throws IOException, ClassNotFoundException {
        byte flag = request.getHeaderData()[2];
        request.setTwoWay((flag & FLAG_TWOWAY) != 0);
        if (request.isHeartbeat()) {
            request.setHeartbeatData(in.readObject());
            return;
        }

        // Dubbo version
        in.readUTF();
        request.setPath(in.readUTF());
        request.setVersion(in.readUTF());
        request.setMethod(in.readUTF());

        String desc = in.readUTF();
        if (desc.length() != 0) {
            Matcher m = DESC_PATTERN.matcher(desc);
            int argumentCount = 0;
            while (m.find()) {
                ++argumentCount;
            }
            for (int i = 0; i < argumentCount; i++) {
                try {
                    in.readObject();
                } catch (ClassNotFoundException cnfe) {
                    // Ignore ClassNotFoundException since most of the time we don't have the class
                    // used in requests and we don't care about the request object actually.
                } catch (DubboSerializationException ex) {
                    if (ex.getCause() instanceof ClassNotFoundException) {
                        // Ignore ClassNotFoundException for the same reason above.
                        continue;
                    }
                    throw ex;
                }
            }
        }

        Map<String, String> attachments = (Map<String, String>) in.readObject(Map.class);
        request.setAttachments(attachments);
    }

    private static void fillResponse(ObjectInput in, DubboResponse response)
            throws IOException, ClassNotFoundException {
        byte status = response.getHeaderData()[3];
        response.setStatus(status);
        if (status != Response.OK) {
            response.setErrorMessage(in.readUTF());
            return;
        }

        if (response.isHeartbeat()) {
            response.setHeartbeatData(in.readObject());
            return;
        }

        byte responseFlag = in.readByte();
        switch (responseFlag) {
            case DubboCodec.RESPONSE_NULL_VALUE:
            case DubboCodec.RESPONSE_VALUE:
            case DubboCodec.RESPONSE_VALUE_WITH_ATTACHMENTS:
            case DubboCodec.RESPONSE_NULL_VALUE_WITH_ATTACHMENTS:
                // We don't care about response data.
                break;
            case DubboCodec.RESPONSE_WITH_EXCEPTION:
                Object obj = in.readObject();
                if (obj instanceof Throwable) {
                    response.setThrowable((Throwable) obj);
                } else {
                    logger.warn("Response data error, expect Throwable, but get " + obj.getClass().getName());
                }
                break;
            default:
                logger.warn("Unknown result flag, expect '0' '1' '2', get " + responseFlag);
        }
    }
}
