package com.android.server.hdmi;

import com.android.server.hdmi.HdmiControlService;
import com.android.server.power.IHwShutdownThread;

final class RequestArcTerminationAction extends RequestArcAction {
    private static final String TAG = "RequestArcTerminationAction";

    RequestArcTerminationAction(HdmiCecLocalDevice source, int avrAddress) {
        super(source, avrAddress);
    }

    /* access modifiers changed from: package-private */
    public boolean start() {
        this.mState = 1;
        addTimer(this.mState, IHwShutdownThread.SHUTDOWN_ANIMATION_WAIT_TIME);
        sendCommand(HdmiCecMessageBuilder.buildRequestArcTermination(getSourceAddress(), this.mAvrAddress), new HdmiControlService.SendMessageCallback() {
            public void onSendCompleted(int error) {
                if (error != 0) {
                    RequestArcTerminationAction.this.disableArcTransmission();
                    RequestArcTerminationAction.this.finish();
                }
            }
        });
        return true;
    }
}
