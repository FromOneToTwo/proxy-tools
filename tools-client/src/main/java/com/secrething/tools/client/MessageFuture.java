package com.secrething.tools.client;

import com.secrething.tools.common.RequestEntity;
import com.secrething.tools.common.ResponseEntity;
import com.secrething.tools.common.async.AbstractAdapterFuture;

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
