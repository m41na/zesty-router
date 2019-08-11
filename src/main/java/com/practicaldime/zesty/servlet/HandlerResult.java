package com.practicaldime.zesty.servlet;

public class HandlerResult {

    private final Long startInMillis;
    private Boolean status = Boolean.TRUE;

    public HandlerResult() {
        this.startInMillis = System.currentTimeMillis();
    }

    public Boolean isSuccess(){
        return this.status;
    }

    public Long updateStatus(Boolean status){
        this.status = status;
        return System.currentTimeMillis() - this.startInMillis;
    }

    public static HandlerResult build(Boolean status){
        HandlerResult res = new HandlerResult();
        res.status = status;
        return res;
    }
}
