package com.secrething.tools.client;

import com.secrething.tools.common.*;
import com.secrething.tools.common.contant.ConfigProp;
import com.secrething.tools.common.protocol.ProtocolDecoder;
import com.secrething.tools.common.protocol.ProtocolEncoder;
import com.secrething.tools.common.protocol.RequestEntity;
import com.secrething.tools.common.protocol.ResponseEntity;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.TimeUnit;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class Client {
    public static final String proxy_ip;
    public static final int proxy_prot;
    static {
        String fromPro = ConfigProp.getConfig("proxy_ip");
        if (StringUtils.isBlank(fromPro)) {
            proxy_ip = "localhost";
        } else
            proxy_ip = fromPro;
        String port = ConfigProp.getConfig("proxy_prot");
        if (StringUtils.isBlank(port))
            proxy_prot = 9999;
        else {
            int p = 9999;
            try {
                p = Integer.valueOf(port);
            } catch (Exception e) {
            }
            proxy_prot = p;

        }

    }

    public Client() {
    }

    private void connect(final MessageFuture request) throws Exception {
        try {
            NioEventLoopGroup workerGroup = new NioEventLoopGroup();

            try {
                Bootstrap b = new Bootstrap();
                b.group(workerGroup);
                b.channel(NioSocketChannel.class);
                b.option(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
                b.handler(new ChannelInitializer<SocketChannel>() {
                    public void initChannel(SocketChannel ch) throws Exception {
                        ch.pipeline().addLast(new ChannelHandler[]{new ProtocolEncoder()});
                        ch.pipeline().addLast(new ChannelHandler[]{new ProtocolDecoder()});
                        ch.pipeline().addLast(new ChannelHandler[]{new ClientHandler(request)});
                    }
                });
                ChannelFuture f = b.connect(proxy_ip, proxy_prot).sync();
                f.channel().closeFuture().sync();
            } finally {
                workerGroup.shutdownGracefully();
            }
        } catch (Exception e) {
        }

    }

    /**
     *
     * @param model {@link RequestEntity}
     * @param remoteAddr 远程的Ip地址，例如自家的测试服务器(***.***.***.163)
     * @param port Server启动时绑定的端口
     * @return
     * @throws Exception
     */
    public static String sendRequest(RequestEntity model) throws Exception {
        MessageFuture future = new MessageFuture(model);
        Client client = new Client();
        client.connect(future);
        ResponseEntity response = future.get(2, TimeUnit.MINUTES);
        if (null != response.getThrowable())
            response.getThrowable().printStackTrace();
        return response.getResult().toString();
    }

    public static void main(String[] args) {
        String url = "http://59.110.6.12:8080/tuniuhm/search";
        String request = "{\"type\":\"0\",\"cid\":\"tuniu\",\"tripType\":\"1\",\"fromCity\":\"BKK\",\"toCity\":\"HKT\",\"fromDate\":\"20180331\",\"all\":\"\",\"adultNum\":\"1\",\"childNum\":\"0\",\"infantNumber\":\"0\",\"retDate\":\"\"}";
        int waitTime = 60000;
        String res = ProxyHttpPoolManage.sendJsonPostRequest(url,request,waitTime);
        System.out.println(res);
    }
}
