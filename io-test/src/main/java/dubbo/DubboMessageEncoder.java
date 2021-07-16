package dubbo;

import com.alibaba.dubbo.rpc.RpcInvocation;
import com.google.common.base.Strings;
import dubbo.exception.DubboEncodeMessageException;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.apache.dubbo.common.URL;
import org.apache.dubbo.common.io.Bytes;
import org.apache.dubbo.common.serialize.ObjectOutput;
import org.apache.dubbo.common.serialize.Serialization;
import org.apache.dubbo.common.utils.ReflectUtils;
import org.apache.dubbo.remoting.transport.CodecSupport;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import static org.apache.dubbo.common.constants.CommonConstants.PATH_KEY;
import static org.apache.dubbo.common.constants.CommonConstants.VERSION_KEY;

/**
 * Created by fang_j on 2021/07/16.
 */
@ChannelHandler.Sharable
public class DubboMessageEncoder extends MessageToByteEncoder<DubboRequest> {
    public static final int HEADER_LENGTH = 16;

    // Magic header.
    public static final short MAGIC = (short) 0xdabb;
    public static final byte MAGIC_HIGH = Bytes.short2bytes(MAGIC)[0];
    public static final byte MAGIC_LOW = Bytes.short2bytes(MAGIC)[1];

    // Message flag.
    public static final byte FLAG_REQUEST = (byte) 0x80;
    public static final byte FLAG_TWOWAY = (byte) 0x40;
    public static final byte FLAG_EVENT = (byte) 0x20;
    public static final int SERIALIZATION_MASK = 0x1f;

    public static final URL DUMMY_DUBBO_URL = new URL("dubbo", "localhost", 20880);

    public static final String SERIALIZATION_KEY = "serialization";

    @Override
    protected void encode(ChannelHandlerContext ctx, DubboRequest msg, ByteBuf out) throws Exception {
        try {
            encodeRequest(msg);
            out.writeBytes(msg.getHeaderData());
            out.writeBytes(msg.getBodyData());
        } catch (Exception e) {
            throw new DubboEncodeMessageException("Failed to encode a Dubbo message", e);
        }
    }

    private void encodeRequest(DubboRequest req) throws IOException {
        Serialization serialization = CodecSupport.getSerializationById(req.getContentTypeId());

        // header.
        byte[] header = new byte[HEADER_LENGTH];

        // set magic number.
        Bytes.short2bytes(MAGIC, header);

        // set request and serialization flag.
        header[2] = (byte) (FLAG_REQUEST | serialization.getContentTypeId());

        if (req.isTwoWay()) {
            header[2] |= FLAG_TWOWAY;
        }
        if (req.isEvent()) {
            header[2] |= FLAG_EVENT;
        }

        // set request id.
        Bytes.long2bytes(req.getId(), header, 4);

        // encode request data.
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ObjectOutput out = serialization.serialize(DUMMY_DUBBO_URL, bos);
        encodeRequestData(out, req.getData(), req.getVersion());
        out.flushBuffer();
        bos.flush();
        bos.close();
        byte[] body = bos.toByteArray();
        int len = body.length;
        Bytes.int2bytes(len, header, 12);

        req.setHeaderData(header);
        req.setBodyData(body);
    }

    //org.apache.dubbo.rpc.protocol.dubbo.DubboCodec
    private void encodeRequestData(ObjectOutput out, Object data, String version) throws IOException {
        RpcInvocation inv = (RpcInvocation) data;

        out.writeUTF(version);
        out.writeUTF(inv.getAttachment(PATH_KEY));
        if (!Strings.isNullOrEmpty(inv.getAttachment(VERSION_KEY))) {
            out.writeUTF(inv.getAttachment(VERSION_KEY));
        } else {
            out.writeUTF("0.0.0");
        }

        out.writeUTF(inv.getMethodName());
        out.writeUTF(ReflectUtils.getDesc(inv.getParameterTypes()));
        Object[] args = inv.getArguments();
        if (args != null) {
            for (Object arg : args) {
                out.writeObject(arg);
            }
        }
        out.writeObject(inv.getAttachments());
    }
}
