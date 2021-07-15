package discard;

import echo.NettyEchoClient;
import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Created by fang_j on 2021/07/15.
 */
public class NettyDiscardClient {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap()
                    .group(group)
                    .channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .handler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel ch) throws Exception {
                            ChannelPipeline p = ch.pipeline();
                            p.addLast(new DiscardClientHandler());
                        }
                    });

            ChannelFuture f = b.connect("localhost", 8080);
            f.channel().closeFuture().sync();
        } finally {
            group.shutdownGracefully();
        }
    }

    static class DiscardClientHandler extends SimpleChannelInboundHandler<Object> {
        private ChannelHandlerContext ctx;
        private ByteBuf content;

        @Override
        public void channelActive(ChannelHandlerContext ctx) throws Exception {
            this.ctx = ctx;
            // Initialize the message.
            content = ctx.alloc().directBuffer(1000).writeZero(1000);

            // Send the initial messages.
            generateTraffic();
        }

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Object msg) throws Exception {
            // discard
        }

        @Override
        public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
            cause.printStackTrace();
            ctx.close();
        }

        AtomicLong counter = new AtomicLong();

        private void generateTraffic() {
            ctx.writeAndFlush(content.duplicate().retain()).addListener(trafficGenerator);
        }

        private final ChannelFutureListener trafficGenerator = future -> {
            if (future.isSuccess()) {
                generateTraffic();
            } else {
                future.cause().printStackTrace();
                future.channel().close();
            }
            System.out.println(counter.getAndIncrement());
        };
    }


}
