package com.practicaldime.router.core.servlet;

public class HandlerResult {

    private final Long startInMillis;
    private Boolean status = Boolean.TRUE;

    public HandlerResult() {
        this.startInMillis = System.currentTimeMillis();
    }

    public static HandlerResult build(Boolean status) {
        HandlerResult res = new HandlerResult();
        res.status = status;
        return res;
    }

    public Boolean isSuccess() {
        return this.status;
    }

    public Long updateStatus(Boolean status) {
        this.status = status;
        return System.currentTimeMillis() - this.startInMillis;
    }
}
