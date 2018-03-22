package com.secrething.tools.client;

import com.secrething.tools.common.async.AbstractAdapterFuture;
import com.secrething.tools.common.protocol.RequestEntity;
import com.secrething.tools.common.protocol.ResponseEntity;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class MessageFuture extends AbstractAdapterFuture<ResponseEntity> {
    private final RequestEntity request;

    public MessageFuture(RequestEntity request) {
        this.request = request;
    }

    public RequestEntity getRequest() {
        return request;
    }
}
