package com.secrething.tools.common;

import com.secrething.tools.common.utils.Assert;
import com.secrething.tools.common.utils.SerializeUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;
import redis.clients.jedis.JedisPubSub;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;

/**
 * Created by secret on 2018/3/27.
 */
public class RedisOperator {
    private static final Logger log = LoggerFactory.getLogger(RedisOperator.class);
    JedisPool pool;

    public RedisOperator(String address, int port, String pwd) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMinIdle(500);
        config.setMaxIdle(1000);
        config.setMaxTotal(4000);
        config.setMaxWaitMillis(2000);
        config.setTestOnBorrow(true);
        if (pwd == null || pwd.isEmpty()) {
            pool = new JedisPool(config, address, port, 3000);
        } else {
            pool = new JedisPool(config, address, port, 3000, pwd);
        }
    }

    public RedisOperator(String address, int port, String pwd, int minIdle, int maxIdle, int maxTotal) {
        JedisPoolConfig config = new JedisPoolConfig();
        config.setMinIdle(minIdle);
        config.setMaxIdle(maxIdle);
        config.setMaxTotal(maxTotal);
        config.setMaxWaitMillis(2000);
        config.setTestOnBorrow(true);
        if (pwd == null || pwd.isEmpty()) {
            pool = new JedisPool(config, address, port, 3000);
        } else {
            pool = new JedisPool(config, address, port, 3000, pwd);
        }
    }

    public <T> T getAndSet(String key, T newvalue) {
        Assert.notNull(newvalue);
        Assert.notNull(pool);
        Assert.notBlank(key);
        T value = null;
        Jedis jedis = null;
        byte[] bk = key.getBytes();
        byte[] bv = SerializeUtil.serialize(newvalue);
        try {
            jedis = pool.getResource();
            if (jedis != null) {
                byte[] bytes = jedis.getSet(bk, bv);
                if (null != bytes && bytes.length > 0) {
                    value = SerializeUtil.deserialize(bytes, (Class<? extends T>) newvalue.getClass());
                }
            }
        } catch (Exception e) {
            throw e;
        } finally {
            if (null != jedis)
                jedis.close();
        }
        return value;
    }

    public boolean isSubscribed(final String channel) {
        JedisPubSub jedisPubSub = jedisPubSubs.get(channel);
        return jedisPubSub != null && jedisPubSub.isSubscribed();
    }

    public static interface Listener {
        public void onMessage(String channel, String message);
    }

    public static interface RedisCallback<K> {
        K call(Jedis jedis);
    }

    <K> K callable(RedisCallback<K> callback) {
        Jedis jedis = pool.getResource();
        return callback.call(jedis);
    }

    private Map<String, JedisPubSub> jedisPubSubs = new ConcurrentHashMap<>();

    public void unsubscribe(final String channel) {
        try {
            JedisPubSub jedisPubSub = jedisPubSubs.remove(channel);
            if (jedisPubSub != null && jedisPubSub.isSubscribed()) {
                jedisPubSub.unsubscribe();
            }
        } catch (Exception e) {
            log.warn("unsubscribe error, channel:{}", channel);
        }
    }

    private void processMessage(final String channel, final Listener listener, final String message, ExecutorService executorService) {
        try {
            if (null != executorService)
                executorService.execute(() -> {
                            listener.onMessage(channel, message);
                        }
                );
            else
                listener.onMessage(channel, message);
        } catch (Exception e) {
            log.warn("process message error! channel:{}, message:{}", channel, message, e);
        }
    }

    JedisPubSub createJedisPubSub(String channel, final Listener listener, final ExecutorService executorService) {
        try {
            JedisPubSub jedisPubSub = new JedisPubSub() {

                @Override
                public void onMessage(String channel, String message) {
                    log.debug("onMessage channel:{}, message:{}", channel, message);
                    processMessage(channel, listener, message, executorService);
                }

                @Override
                public void onSubscribe(String channel, int subscribedChannels) {
                    log.debug("onSubscribe channel:{}", channel);
                }

                @Override
                public void onUnsubscribe(String channel, int subscribedChannels) {
                    log.debug("onUnsubscribe channel:{}", channel);
                }

                @Override
                public void onPMessage(String pattern, String channel, String message) {
                }

                @Override
                public void onPUnsubscribe(String pattern, int subscribedChannels) {
                }

                @Override
                public void onPSubscribe(String pattern, int subscribedChannels) {
                }
            };
            jedisPubSubs.put(channel, jedisPubSub);
            return jedisPubSub;
        } catch (Exception e) {
            log.warn("create JedisPubSub error! channel:{}", channel, e);
            return null;
        }
    }

    public void syncSubcribe(final String channel, final Listener listener) {
        subscribe(channel, listener, null);
    }

    public void asyncSubcribe(final String channel, final Listener listener, final ExecutorService executorService) {
        subscribe(channel, listener, executorService);
    }

    private void subscribe(final String channel, final Listener listener, final ExecutorService executorService) {
        unsubscribe(channel);

        new Thread(() -> {
            JedisPubSub jedisPubSub = createJedisPubSub(channel, listener, executorService);
            if (jedisPubSub == null) {
                log.warn("subscribe error! create JedisPubSub fail! channel:{}", channel);
                return;
            }

            while (true) { //短线或自动重连必须的
                log.info("begin subscribe, channel:" + channel);

                Jedis jedis = null;
                try {
                    jedis = pool.getResource();
                    jedis.subscribe(jedisPubSub, channel); //这里会阻塞，直到异常或unsubscribe
                    log.info("unsubscribe, channel:{}", channel);
                    break;
                } catch (final Exception e) {
                    log.warn("subscribe catch exception, channel:{}", channel, e);
                } finally {
                    if (null != jedis)
                        jedis.close();
                }
            }

        }).start();
    }

    public Long publish(final String channel, final String message) {
        return callable((Jedis jedis) -> jedis.publish(channel, message));
    }

}
