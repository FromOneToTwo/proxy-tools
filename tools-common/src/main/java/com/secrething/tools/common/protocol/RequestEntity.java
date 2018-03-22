package com.secrething.tools.common.protocol;

import com.alibaba.fastjson.JSONObject;
import com.secrething.tools.common.manage.HttpPoolManage;

/**
 * @author liuzz
 * @create 2018/3/14
 * 例:
 * RequestEntity entity = new RequestEntity();
 * String url = "http://59.110.6.12:8080/tuniuhm/search";
 * String request = "{\"type\":\"0\",\"cid\":\"tuniu\",\"tripType\":\"1\",\"fromCity\":\"BKK\",\"toCity\":\"HKT\",\"fromDate\":\"20180331\",\"all\":\"\",\"adultNum\":\"1\",\"childNum\":\"0\",\"infantNumber\":\"0\",\"retDate\":\"\"}";
 * int waitTime = 60000;
 * Object[] params = new Object[3];
 * params[0] = url;
 * params[1] = request;
 * params[2] = waitTime;
 * entity.setParams(params);
 * entity.setMethodName("sendJsonPostRequest");
 * String res = sendRequest(entity, "***.***.***.163", 8888);
 */
public class RequestEntity {
    /**
     * 此methodName为HttpPoolManage中的某个方法的名字,
     * 用来反射调用HttpPoolManage的方法
     *
     * @see HttpPoolManage
     */
    private String methodName;
    /**
     * 调用method所需要的参数
     * 例
     * @see RequestEntity#methodName
     */
    private Param[] params;

    public String getMethodName() {
        return methodName;
    }

    public void setMethodName(String methodName) {
        this.methodName = methodName;
    }

    public Param[] getParams() {
        return params;
    }

    public void setParams(Param[] params) {
        this.params = params;
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("{");
        sb.append("\"methodName\":\"")
                .append(methodName).append('\"');
        sb.append(",\"params\":")
                .append(JSONObject.toJSONString(params));
        sb.append('}');
        return sb.toString();
    }
}
