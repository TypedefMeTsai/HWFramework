package com.huawei.agpengine.impl;

/* access modifiers changed from: package-private */
public enum CoreAnimationPlaybackState {
    STOP(0),
    PLAY(1),
    PAUSE(2);
    
    private final int swigValue;

    /* access modifiers changed from: package-private */
    public final int swigValue() {
        return this.swigValue;
    }

    static CoreAnimationPlaybackState swigToEnum(int swigValue2) {
        CoreAnimationPlaybackState[] swigValues = (CoreAnimationPlaybackState[]) CoreAnimationPlaybackState.class.getEnumConstants();
        if (swigValue2 < swigValues.length && swigValue2 >= 0 && swigValues[swigValue2].swigValue == swigValue2) {
            return swigValues[swigValue2];
        }
        for (CoreAnimationPlaybackState swigEnum : swigValues) {
            if (swigEnum.swigValue == swigValue2) {
                return swigEnum;
            }
        }
        throw new IllegalArgumentException("No enum " + CoreAnimationPlaybackState.class + " with value " + swigValue2);
    }

    private CoreAnimationPlaybackState() {
        this.swigValue = SwigNext.next;
        SwigNext.access$008();
    }

    private CoreAnimationPlaybackState(int swigValue2) {
        this.swigValue = swigValue2;
        int unused = SwigNext.next = swigValue2 + 1;
    }

    private CoreAnimationPlaybackState(CoreAnimationPlaybackState swigEnum) {
        this.swigValue = swigEnum.swigValue;
        int unused = SwigNext.next = this.swigValue + 1;
    }

    private static class SwigNext {
        private static int next = 0;

        private SwigNext() {
        }

        static /* synthetic */ int access$008() {
            int i = next;
            next = i + 1;
            return i;
        }
    }
}
