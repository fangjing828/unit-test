package http;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.http.HttpClientCodec;
import io.netty.handler.codec.http.HttpContentCompressor;
import io.netty.handler.timeout.IdleStateHandler;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * Created by fang_j on 2021/07/15.
 */
public class NettyHttpClient {
    public static void main(String[] args) throws InterruptedException, ExecutionException, TimeoutException {
        Bootstrap bootstrap = new Bootstrap().group(new NioEventLoopGroup())
                .channel(NioSocketChannel.class)
                .handler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel sc) {
                        ChannelPipeline p = sc.pipeline();
                        p.addLast(new IdleStateHandler(0L, 0L, 60_000, TimeUnit.MILLISECONDS));
                        p.addLast(new HttpClientCodec());
                        p.addLast(new HttpContentCompressor());
                    }
                });

        bootstrap.connect("127.0.0.1", 8080).get(5000, TimeUnit.MILLISECONDS);
    }
}
