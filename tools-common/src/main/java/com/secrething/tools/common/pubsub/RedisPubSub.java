package com.secrething.tools.common.pubsub;

import com.secrething.tools.common.RedisOperator;
import com.secrething.tools.common.utils.Assert;

import java.util.concurrent.ExecutorService;

/**
 * Created by secret on 2018/3/28.
 */
public class RedisPubSub extends RedisOperator {
    public RedisPubSub(String address, int port, String pwd) {
        super(address, port, pwd);
    }

    public RedisPubSub(String address, int port, String pwd, int minIdle, int maxIdle, int maxTotal) {
        super(address, port, pwd, minIdle, maxIdle, maxTotal);
    }

    public void subcribe(String channel, final SubscribeListener listener) {
        Assert.notBlank(channel);
        Assert.notNull(listener);
        syncSubcribe(channel, new Listener() {
            @Override
            public void onMessage(String channel, String message) {
                listener.onMessage(message);
            }
        });
    }

    public void subcribe(String channel, final SubscribeListener listener, ExecutorService executorService) {
        Assert.notBlank(channel);
        Assert.notNull(listener);
        Assert.notNull(executorService);
        asyncSubcribe(channel, new Listener() {
            @Override
            public void onMessage(String channel, String message) {
                listener.onMessage(message);
            }
        }, executorService);
    }
}
