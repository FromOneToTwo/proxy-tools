package com.secrething.tools.common.protocol;

/**
 * @author liuzz
 * @create 2018/3/14
 */
public class ResponseEntity extends RequestEntity {

    private Object result;
    private Throwable throwable;
    public Object getResult() {
        return result;
    }

    public void setResult(Object result) {
        this.result = result;
    }

    public Throwable getThrowable() {
        return throwable;
    }

    public void setThrowable(Throwable throwable) {
        this.throwable = throwable;
    }
    public void setRequest(RequestEntity entity){
        this.setMethodName(entity.getMethodName());
        this.setParams(entity.getParams());
    }

}
