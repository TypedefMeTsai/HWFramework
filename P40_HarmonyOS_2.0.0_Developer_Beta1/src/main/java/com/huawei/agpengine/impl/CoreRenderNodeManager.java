package com.huawei.agpengine.impl;

class CoreRenderNodeManager {
    private transient long agpCptr;
    transient boolean isAgpCmemOwn;

    CoreRenderNodeManager(long cptr, boolean isCmemoryOwn) {
        this.isAgpCmemOwn = isCmemoryOwn;
        this.agpCptr = cptr;
    }

    static long getCptr(CoreRenderNodeManager obj) {
        long j;
        if (obj == null) {
            return 0;
        }
        synchronized (obj) {
            j = obj.agpCptr;
        }
        return j;
    }
}
