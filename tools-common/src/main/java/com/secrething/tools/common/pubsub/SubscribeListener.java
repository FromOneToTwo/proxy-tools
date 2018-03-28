package com.secrething.tools.common.pubsub;

/**
 * Created by secret on 2018/3/28.
 */
public interface SubscribeListener {
    void onMessage(String msg);
}
