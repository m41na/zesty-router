package com.practicaldime.zesty.servlet;

public class HandlerResult {

    public final Long startInMillis;
    public Boolean status = Boolean.TRUE;

    public HandlerResult() {
        this.startInMillis = System.currentTimeMillis();
    }

    public Long timeSinceStart(){
        return System.currentTimeMillis() - this.startInMillis;
    }

    public Boolean isSuccess(){
        return this.status;
    }

    public static HandlerResult build(Boolean status){
        HandlerResult res = new HandlerResult();
        res.status = status;
        return res;
    }
}
