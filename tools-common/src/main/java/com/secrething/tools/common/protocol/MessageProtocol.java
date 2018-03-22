package com.secrething.tools.common.protocol;

import com.secrething.tools.common.contant.ConstantValue;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class MessageProtocol {
    private int head_data = ConstantValue.HEAD_DATA;
    private String messageUID;
    private int contentLength;
    private byte[] content;

    public MessageProtocol(int contentLength, byte[] content) {
        this.contentLength = contentLength;
        this.content = content;
    }

    public int getHead_data() {
        return this.head_data;
    }

    public String getMessageUID() {
        return this.messageUID;
    }

    public void setMessageUID(String messageUID) {
        this.messageUID = messageUID;
    }

    public int getContentLength() {
        return this.contentLength;
    }

    public void setContentLength(int contentLength) {
        this.contentLength = contentLength;
    }

    public byte[] getContent() {
        return this.content;
    }

    public void setContent(byte[] content) {
        this.content = content;
    }
}
