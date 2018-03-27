package com.secrething.tools.common.protocol;

import com.secrething.tools.common.contant.ConstantValue;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class MessageProtocol {
    public static final int DEFAULT = 0;
    public static final int PROXY = 1;
    public static final int HEART = 2;
    private int head_data = ConstantValue.HEAD_DATA;
    private int mesg_type = 0;//默认0,1代理,2心跳
    private String messageUID;
    private int contentLength;
    private byte[] content;

    public MessageProtocol(int contentLength, byte[] content) {
        this.contentLength = contentLength;
        this.content = content;
    }

    public void setHead_data(int head_data) {
        this.head_data = head_data;
    }

    public int getMesg_type() {
        return mesg_type;
    }

    public void setMesg_type(int mesg_type) {
        this.mesg_type = mesg_type;
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
