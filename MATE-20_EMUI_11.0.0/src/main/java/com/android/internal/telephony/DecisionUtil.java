package com.android.internal.telephony;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Handler;
import android.os.IBinder;
import android.util.ArrayMap;
import android.util.Log;
import com.huawei.common.service.IDecision;
import com.huawei.common.service.IDecisionCallback;
import com.huawei.hwparttelephonyopt.BuildConfig;

public final class DecisionUtil {
    private static final String CATEGORY_KEY = "category";
    private static final String ID_KEY = "id";
    private static final int OPER_SUCCESS = 0;
    private static final String TAG = DecisionUtil.class.getSimpleName();
    private static Context mContext;
    private static IDecision mDecisionApi = null;
    private static ServiceConnection mDecisionConnection = new ServiceConnection() {
        /* class com.android.internal.telephony.DecisionUtil.AnonymousClass1 */

        @Override // android.content.ServiceConnection
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(DecisionUtil.TAG, "service connected.");
            IDecision unused = DecisionUtil.mDecisionApi = IDecision.Stub.asInterface(service);
            DecisionUtil.executeEvent(DecisionUtil.mEventName);
            Log.d(DecisionUtil.TAG, "onServiceConnected");
            DecisionUtil.unbindService(DecisionUtil.mContext);
        }

        @Override // android.content.ServiceConnection
        public void onServiceDisconnected(ComponentName name) {
            IDecision unused = DecisionUtil.mDecisionApi = null;
            Log.i(DecisionUtil.TAG, "service disconnect.");
        }
    };
    private static String mEventName = null;
    private static Handler mHander = null;

    public static void bindService(Context context, String eventName) {
        mEventName = eventName;
        if (context == null || mDecisionApi != null || mEventName == null) {
            Log.i(TAG, "service already binded");
            return;
        }
        mContext = context;
        if (mHander == null) {
            mHander = new Handler(context.getMainLooper());
        }
        Intent actionService = new Intent("com.huawei.recsys.decision.action.BIND_DECISION_SERVICE");
        actionService.setPackage("com.huawei.recsys");
        try {
            context.bindService(actionService, mDecisionConnection, 1);
        } catch (Exception e) {
            Log.e(TAG, "bindService exception");
        }
    }

    public static void unbindService(Context context) {
        if (context != null) {
            try {
                context.unbindService(mDecisionConnection);
            } catch (Exception e) {
                Log.e(TAG, "unbindService exception");
            }
            mDecisionApi = null;
        }
    }

    public static boolean executeEvent(String eventName) {
        return executeEvent(eventName, null);
    }

    public static boolean executeEvent(String eventName, String dataId) {
        if (mDecisionApi == null) {
            return false;
        }
        ArrayMap<String, Object> extra2 = new ArrayMap<>();
        extra2.put(ID_KEY, dataId != null ? dataId : BuildConfig.FLAVOR);
        if (eventName != null) {
            extra2.put(CATEGORY_KEY, eventName);
        }
        try {
            String str = TAG;
            Log.v(str, "executeEvent " + mEventName);
            mDecisionApi.executeEvent(extra2, (IDecisionCallback) null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}
