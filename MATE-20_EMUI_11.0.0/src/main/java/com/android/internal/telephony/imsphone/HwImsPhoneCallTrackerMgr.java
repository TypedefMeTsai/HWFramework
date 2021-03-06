package com.android.internal.telephony.imsphone;

import com.android.internal.telephony.CallStateException;

public interface HwImsPhoneCallTrackerMgr {
    void checkForDial(IHwImsPhoneCallTrackerInner iHwImsPhoneCallTrackerInner) throws CallStateException;

    void hangupConnectionByIndex(ImsPhoneCall imsPhoneCall, int i, IHwImsPhoneCallTrackerInner iHwImsPhoneCallTrackerInner) throws CallStateException;

    void hangupHisiImsCall(ImsPhoneCall imsPhoneCall, IHwImsPhoneCallTrackerInner iHwImsPhoneCallTrackerInner) throws CallStateException;

    void hangupHisiImsConnection(ImsPhoneConnection imsPhoneConnection, IHwImsPhoneCallTrackerInner iHwImsPhoneCallTrackerInner) throws CallStateException;

    boolean isBreakDialPendingMo(ImsPhoneConnection imsPhoneConnection);
}
