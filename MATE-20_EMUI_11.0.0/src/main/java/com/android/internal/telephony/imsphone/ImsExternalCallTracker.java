package com.android.internal.telephony.imsphone;

import android.os.AsyncResult;
import android.os.Handler;
import android.os.Message;
import android.telecom.VideoProfile;
import android.telephony.ims.ImsCallProfile;
import android.telephony.ims.ImsExternalCallState;
import android.util.ArrayMap;
import android.util.Log;
import com.android.ims.ImsExternalCallStateListener;
import com.android.internal.annotations.VisibleForTesting;
import com.android.internal.telephony.Call;
import com.android.internal.telephony.Connection;
import com.android.internal.telephony.PhoneConstants;
import com.android.internal.telephony.imsphone.ImsExternalConnection;
import com.android.internal.telephony.imsphone.ImsPhoneCallTracker;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ImsExternalCallTracker implements ImsPhoneCallTracker.PhoneStateListener {
    private static final int EVENT_VIDEO_CAPABILITIES_CHANGED = 1;
    public static final String EXTRA_IMS_EXTERNAL_CALL_ID = "android.telephony.ImsExternalCallTracker.extra.EXTERNAL_CALL_ID";
    public static final String TAG = "ImsExternalCallTracker";
    private ImsPullCall mCallPuller;
    private final ImsCallNotify mCallStateNotifier;
    private Map<Integer, Boolean> mExternalCallPullableState = new ArrayMap();
    private final ExternalCallStateListener mExternalCallStateListener;
    private final ExternalConnectionListener mExternalConnectionListener = new ExternalConnectionListener();
    private Map<Integer, ImsExternalConnection> mExternalConnections = new ArrayMap();
    private final Handler mHandler = new Handler() {
        /* class com.android.internal.telephony.imsphone.ImsExternalCallTracker.AnonymousClass1 */

        @Override // android.os.Handler
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                ImsExternalCallTracker.this.handleVideoCapabilitiesChanged((AsyncResult) msg.obj);
            }
        }
    };
    private boolean mHasActiveCalls;
    private boolean mIsVideoCapable;
    private final ImsPhone mPhone;

    public interface ImsCallNotify {
        void notifyPreciseCallStateChanged();

        void notifyUnknownConnection(Connection connection);
    }

    public class ExternalCallStateListener extends ImsExternalCallStateListener {
        public ExternalCallStateListener() {
        }

        public void onImsExternalCallStateUpdate(List<ImsExternalCallState> externalCallState) {
            ImsExternalCallTracker.this.refreshExternalCallState(externalCallState);
        }
    }

    public class ExternalConnectionListener implements ImsExternalConnection.Listener {
        public ExternalConnectionListener() {
        }

        @Override // com.android.internal.telephony.imsphone.ImsExternalConnection.Listener
        public void onPullExternalCall(ImsExternalConnection connection) {
            Log.d(ImsExternalCallTracker.TAG, "onPullExternalCall: connection = " + connection);
            if (ImsExternalCallTracker.this.mCallPuller == null) {
                Log.e(ImsExternalCallTracker.TAG, "onPullExternalCall : No call puller defined");
            } else {
                ImsExternalCallTracker.this.mCallPuller.pullExternalCall(connection.getAddress(), connection.getVideoState(), connection.getCallId());
            }
        }
    }

    @VisibleForTesting
    public ImsExternalCallTracker(ImsPhone phone, ImsPullCall callPuller, ImsCallNotify callNotifier) {
        this.mPhone = phone;
        this.mCallStateNotifier = callNotifier;
        this.mExternalCallStateListener = new ExternalCallStateListener();
        this.mCallPuller = callPuller;
    }

    public ImsExternalCallTracker(ImsPhone phone) {
        this.mPhone = phone;
        this.mCallStateNotifier = new ImsCallNotify() {
            /* class com.android.internal.telephony.imsphone.ImsExternalCallTracker.AnonymousClass2 */

            @Override // com.android.internal.telephony.imsphone.ImsExternalCallTracker.ImsCallNotify
            public void notifyUnknownConnection(Connection c) {
                ImsExternalCallTracker.this.mPhone.notifyUnknownConnection(c);
            }

            @Override // com.android.internal.telephony.imsphone.ImsExternalCallTracker.ImsCallNotify
            public void notifyPreciseCallStateChanged() {
                ImsExternalCallTracker.this.mPhone.notifyPreciseCallStateChanged();
            }
        };
        this.mExternalCallStateListener = new ExternalCallStateListener();
        registerForNotifications();
    }

    public void tearDown() {
        unregisterForNotifications();
    }

    public void setCallPuller(ImsPullCall callPuller) {
        this.mCallPuller = callPuller;
    }

    public ExternalCallStateListener getExternalCallStateListener() {
        return this.mExternalCallStateListener;
    }

    @Override // com.android.internal.telephony.imsphone.ImsPhoneCallTracker.PhoneStateListener
    public void onPhoneStateChanged(PhoneConstants.State oldState, PhoneConstants.State newState) {
        this.mHasActiveCalls = newState != PhoneConstants.State.IDLE;
        Log.i(TAG, "onPhoneStateChanged : hasActiveCalls = " + this.mHasActiveCalls);
        refreshCallPullState();
    }

    private void registerForNotifications() {
        if (this.mPhone != null) {
            Log.d(TAG, "Registering: " + this.mPhone);
            this.mPhone.getDefaultPhone().registerForVideoCapabilityChanged(this.mHandler, 1, null);
        }
    }

    private void unregisterForNotifications() {
        if (this.mPhone != null) {
            Log.d(TAG, "Unregistering: " + this.mPhone);
            this.mPhone.getDefaultPhone().unregisterForVideoCapabilityChanged(this.mHandler);
        }
    }

    public void refreshExternalCallState(List<ImsExternalCallState> externalCallStates) {
        Log.d(TAG, "refreshExternalCallState");
        Iterator<Map.Entry<Integer, ImsExternalConnection>> connectionIterator = this.mExternalConnections.entrySet().iterator();
        boolean wasCallRemoved = false;
        while (connectionIterator.hasNext()) {
            Map.Entry<Integer, ImsExternalConnection> entry = connectionIterator.next();
            if (!containsCallId(externalCallStates, entry.getKey().intValue())) {
                ImsExternalConnection externalConnection = entry.getValue();
                externalConnection.setTerminated();
                externalConnection.removeListener(this.mExternalConnectionListener);
                connectionIterator.remove();
                wasCallRemoved = true;
            }
        }
        if (wasCallRemoved) {
            this.mCallStateNotifier.notifyPreciseCallStateChanged();
        }
        if (!(externalCallStates == null || externalCallStates.isEmpty())) {
            for (ImsExternalCallState callState : externalCallStates) {
                if (!this.mExternalConnections.containsKey(Integer.valueOf(callState.getCallId()))) {
                    Log.d(TAG, "refreshExternalCallState: got = " + callState);
                    if (callState.getCallState() == 1) {
                        createExternalConnection(callState);
                    }
                } else {
                    updateExistingConnection(this.mExternalConnections.get(Integer.valueOf(callState.getCallId())), callState);
                }
            }
        }
    }

    public Connection getConnectionById(int callId) {
        return this.mExternalConnections.get(Integer.valueOf(callId));
    }

    private void createExternalConnection(ImsExternalCallState state) {
        Log.i(TAG, "createExternalConnection : state = " + state);
        int videoState = ImsCallProfile.getVideoStateFromCallType(state.getCallType());
        boolean isCallPullPermitted = isCallPullPermitted(state.isCallPullable(), videoState);
        ImsExternalConnection connection = new ImsExternalConnection(this.mPhone, state.getCallId(), state.getAddress(), isCallPullPermitted);
        connection.setVideoState(videoState);
        connection.addListener(this.mExternalConnectionListener);
        Log.d(TAG, "createExternalConnection - pullable state : externalCallId = " + connection.getCallId() + " ; isPullable = " + isCallPullPermitted + " ; networkPullable = " + state.isCallPullable() + " ; isVideo = " + VideoProfile.isVideo(videoState) + " ; videoEnabled = " + this.mIsVideoCapable + " ; hasActiveCalls = " + this.mHasActiveCalls);
        this.mExternalConnections.put(Integer.valueOf(connection.getCallId()), connection);
        this.mExternalCallPullableState.put(Integer.valueOf(connection.getCallId()), Boolean.valueOf(state.isCallPullable()));
        this.mCallStateNotifier.notifyUnknownConnection(connection);
    }

    private void updateExistingConnection(ImsExternalConnection connection, ImsExternalCallState state) {
        Log.i(TAG, "updateExistingConnection : state = " + state);
        Call.State existingState = connection.getState();
        Call.State newState = state.getCallState() == 1 ? Call.State.ACTIVE : Call.State.DISCONNECTED;
        if (existingState != newState) {
            if (newState == Call.State.ACTIVE) {
                connection.setActive();
            } else {
                connection.setTerminated();
                connection.removeListener(this.mExternalConnectionListener);
                this.mExternalConnections.remove(Integer.valueOf(connection.getCallId()));
                this.mExternalCallPullableState.remove(Integer.valueOf(connection.getCallId()));
                this.mCallStateNotifier.notifyPreciseCallStateChanged();
            }
        }
        int newVideoState = ImsCallProfile.getVideoStateFromCallType(state.getCallType());
        if (newVideoState != connection.getVideoState()) {
            connection.setVideoState(newVideoState);
        }
        this.mExternalCallPullableState.put(Integer.valueOf(state.getCallId()), Boolean.valueOf(state.isCallPullable()));
        boolean isCallPullPermitted = isCallPullPermitted(state.isCallPullable(), newVideoState);
        Log.d(TAG, "updateExistingConnection - pullable state : externalCallId = " + connection.getCallId() + " ; isPullable = " + isCallPullPermitted + " ; networkPullable = " + state.isCallPullable() + " ; isVideo = " + VideoProfile.isVideo(connection.getVideoState()) + " ; videoEnabled = " + this.mIsVideoCapable + " ; hasActiveCalls = " + this.mHasActiveCalls);
        connection.setIsPullable(isCallPullPermitted);
    }

    private void refreshCallPullState() {
        Log.d(TAG, "refreshCallPullState");
        for (ImsExternalConnection imsExternalConnection : this.mExternalConnections.values()) {
            boolean isNetworkPullable = this.mExternalCallPullableState.get(Integer.valueOf(imsExternalConnection.getCallId())).booleanValue();
            boolean isCallPullPermitted = isCallPullPermitted(isNetworkPullable, imsExternalConnection.getVideoState());
            Log.d(TAG, "refreshCallPullState : externalCallId = " + imsExternalConnection.getCallId() + " ; isPullable = " + isCallPullPermitted + " ; networkPullable = " + isNetworkPullable + " ; isVideo = " + VideoProfile.isVideo(imsExternalConnection.getVideoState()) + " ; videoEnabled = " + this.mIsVideoCapable + " ; hasActiveCalls = " + this.mHasActiveCalls);
            imsExternalConnection.setIsPullable(isCallPullPermitted);
        }
    }

    private boolean containsCallId(List<ImsExternalCallState> externalCallStates, int callId) {
        if (externalCallStates == null) {
            return false;
        }
        for (ImsExternalCallState state : externalCallStates) {
            if (state.getCallId() == callId) {
                return true;
            }
        }
        return false;
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleVideoCapabilitiesChanged(AsyncResult ar) {
        this.mIsVideoCapable = ((Boolean) ar.result).booleanValue();
        Log.i(TAG, "handleVideoCapabilitiesChanged : isVideoCapable = " + this.mIsVideoCapable);
        refreshCallPullState();
    }

    private boolean isCallPullPermitted(boolean isNetworkPullable, int videoState) {
        if ((!VideoProfile.isVideo(videoState) || this.mIsVideoCapable) && !this.mHasActiveCalls) {
            return isNetworkPullable;
        }
        return false;
    }
}
