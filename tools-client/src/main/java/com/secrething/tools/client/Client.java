package com.secrething.tools.client;

import com.secrething.tools.common.contant.ConfigProp;
import com.secrething.tools.common.protocol.MessageProtocol;
import com.secrething.tools.common.protocol.RequestEntity;
import com.secrething.tools.common.protocol.ResponseEntity;
import com.secrething.tools.common.utils.SerializeUtil;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class Client {
    public static final String proxy_ip;
    public static final int proxy_prot;
    public static final ConcurrentMap<String, MessageFuture> futureConcurrentMap = new ConcurrentHashMap<>();
    public static final Logger logger = LoggerFactory.getLogger(Client.class);

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

    private Client() {
    }

    private volatile EventLoopGroup workerGroup = null;
    private volatile Channel channel = null;
    private volatile Bootstrap bootstrap = null;
    private AtomicBoolean initMark = new AtomicBoolean();

    private void init() throws Exception {
        try {
            initMark.set(true);
            Bootstrap b = new Bootstrap();
            workerGroup = new NioEventLoopGroup();
            b.group(workerGroup);
            b.channel(NioSocketChannel.class);
            b.option(ChannelOption.SO_KEEPALIVE, Boolean.valueOf(true));
            b.handler(new ClientInitializer());
            bootstrap = b;
        } catch (Exception e) {
        }
    }

    private boolean needInit() {
        return !initMark.get();
    }

    private void connect(Bootstrap b) throws InterruptedException {
        if (null == this.channel){
            ChannelFuture f = b.connect(proxy_ip, proxy_prot).sync().syncUninterruptibly();
            f.channel().closeFuture().addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    logger.info("connection closed");
                    channel = null;
                }
            });
            this.channel = f.channel();
        }
    }

    private static final Client client = new Client();

    private void sendRequest(MessageProtocol protocol) {
        if (null == channel){
            try {
                connect(bootstrap);
            }catch (Exception e){
                return;
            }

        }
        channel.writeAndFlush(protocol);
    }

    /**
     * @param model      {@link RequestEntity}
     * @param remoteAddr 远程的Ip地址，例如自家的测试服务器(***.***.***.163)
     * @param port       Server启动时绑定的端口
     * @return
     * @throws Exception
     */
    public static String sendRequest(RequestEntity request) throws Exception {
        MessageFuture future = new MessageFuture(request);
        Bootstrap b = null;
        if (client.needInit()) {
            client.init();
        }
        try {
            byte[] content = SerializeUtil.serialize(request);
            int contentLength = content.length;
            MessageProtocol protocol = new MessageProtocol(contentLength, content);
            protocol.setMessageUID(UUID.randomUUID().toString());
            protocol.setMesg_type(MessageProtocol.PROXY);
            futureConcurrentMap.put(protocol.getMessageUID(), future);
            client.sendRequest(protocol);
            ResponseEntity response = future.get(2, TimeUnit.MINUTES);
            if (null != response.getThrowable())
                response.getThrowable().printStackTrace();
            return response.getResult().toString();
        } catch (Exception e) {

        } finally {
            //client.close();
        }
        return "";
    }

    public static void main(String[] args) throws InterruptedException {
        String url = "http://59.110.6.12:8080/tuniuhm/search";
        String request = "{\"type\":\"0\",\"cid\":\"tuniu\",\"tripType\":\"1\",\"fromCity\":\"BKK\",\"toCity\":\"HKT\",\"fromDate\":\"20180421\",\"all\":\"\",\"adultNum\":\"1\",\"childNum\":\"0\",\"infantNumber\":\"0\",\"retDate\":\"\"}";
        int waitTime = 60000;
        String res = ProxyHttpPoolManage.sendJsonPostRequest(url, request, waitTime);
        System.out.println(res);
        Thread.sleep(18000);
        res = ProxyHttpPoolManage.sendJsonPostRequest(url, request, waitTime);
        System.out.println(res);
    }
}
