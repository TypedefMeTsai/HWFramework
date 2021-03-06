package com.android.internal.telephony;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.SystemProperties;
import android.provider.ContactsContract;
import android.telephony.Rlog;
import android.util.Log;
import huawei.android.telephony.TelephonyInterfacesHW;

public class HwCallerInfo {
    private static final String TAG = "CallerInfo";
    private static final boolean VDBG = Rlog.isLoggable(TAG, 2);

    public static CallerInfo getCallerInfo(Context context, Uri contactRef, Cursor cursor, String compNum) {
        if (SystemProperties.getInt("ro.config.hwft_MatchNum", 0) < 7 && SystemProperties.getInt(HwTelephonyProperties.PROPERTY_GLOBAL_VERSION_NUM_MATCH, 0) == 0) {
            return CallerInfo.getCallerInfo(context, contactRef, cursor);
        }
        CallerInfo info = new CallerInfo();
        info.photoResource = 0;
        info.phoneLabel = null;
        info.numberType = 0;
        info.numberLabel = null;
        info.cachedPhoto = null;
        info.isCachedPhotoCurrent = false;
        info.needUpdate = false;
        info.contactRefUri = contactRef;
        Log.v(TAG, "construct callerInfo from cursor");
        if (cursor == null) {
            return info;
        }
        try {
            try {
                int fixedIndex = ((TelephonyInterfacesHW) Class.forName("huawei.android.telephony.CallerInfoHW").newInstance()).getCallerIndex(cursor, compNum);
                if (-1 == fixedIndex) {
                    cursor.close();
                    return info;
                }
                if (cursor.moveToPosition(fixedIndex)) {
                    int columnIndex = cursor.getColumnIndex("display_name");
                    if (columnIndex != -1) {
                        info.name = cursor.getString(columnIndex);
                        logd("info.name: " + info.name);
                    }
                    int columnIndex2 = cursor.getColumnIndex("number");
                    if (columnIndex2 != -1) {
                        info.phoneNumber = cursor.getString(columnIndex2);
                        logd("info.phoneNumber: " + info.phoneNumber);
                    }
                    int columnIndex3 = cursor.getColumnIndex("normalized_number");
                    if (columnIndex3 != -1) {
                        info.normalizedNumber = cursor.getString(columnIndex3);
                    }
                    int columnIndex4 = cursor.getColumnIndex("label");
                    if (columnIndex4 != -1) {
                        int typeColumnIndex = cursor.getColumnIndex("type");
                        if (typeColumnIndex != -1) {
                            info.numberType = cursor.getInt(typeColumnIndex);
                            info.numberLabel = cursor.getString(columnIndex4);
                            info.phoneLabel = ContactsContract.CommonDataKinds.Phone.getDisplayLabel(context, info.numberType, info.numberLabel).toString();
                        }
                    }
                    int columnIndex5 = -1;
                    String url = contactRef.toString();
                    if (url.startsWith("content://com.android.contacts/data/phones")) {
                        if (VDBG) {
                            Log.v(TAG, "URL path starts with 'data/phones' using RawContacts.CONTACT_ID");
                        }
                        columnIndex5 = cursor.getColumnIndex("contact_id");
                    } else if (url.startsWith("content://com.android.contacts/phone_lookup")) {
                        if (VDBG) {
                            Log.v(TAG, "URL path starts with 'phone_lookup' using PhoneLookup._ID");
                        }
                        columnIndex5 = cursor.getColumnIndex("_id");
                    } else {
                        Log.e(TAG, "Bad contact URL 'XXXXXX'");
                    }
                    if (columnIndex5 != -1) {
                        info.contactIdOrZero = cursor.getLong(columnIndex5);
                    } else {
                        Log.e(TAG, "person_id column missing for " + contactRef);
                    }
                    int columnIndex6 = cursor.getColumnIndex("custom_ringtone");
                    if (columnIndex6 == -1 || cursor.getString(columnIndex6) == null) {
                        info.contactRingtoneUri = null;
                    } else {
                        info.contactRingtoneUri = Uri.parse(cursor.getString(columnIndex6));
                    }
                    int columnIndex7 = cursor.getColumnIndex("send_to_voicemail");
                    info.shouldSendToVoicemail = columnIndex7 != -1 && cursor.getInt(columnIndex7) == 1;
                    info.contactExists = true;
                    int columnIndex8 = cursor.getColumnIndex("data7");
                    if (columnIndex8 != -1) {
                        info.mVoipDeviceType = cursor.getInt(columnIndex8);
                    }
                }
                cursor.close();
                info.needUpdate = false;
                info.name = normalize(info.name);
                info.contactRefUri = contactRef;
                return info;
            } catch (InstantiationException e) {
                Log.e(TAG, "getCallerInfo InstantiationException");
                return CallerInfo.getCallerInfo(context, contactRef, cursor);
            } catch (IllegalAccessException e2) {
                Log.e(TAG, "getCallerInfo IllegalAccessException");
                return CallerInfo.getCallerInfo(context, contactRef, cursor);
            }
        } catch (ClassNotFoundException e3) {
            Log.e(TAG, "getCallerInfo ClassNotFoundException");
            return CallerInfo.getCallerInfo(context, contactRef, cursor);
        }
    }

    private static String normalize(String s) {
        if (s == null || s.length() > 0) {
            return s;
        }
        return null;
    }

    private static void logd(String msg) {
        if (Log.isLoggable(TAG, 3)) {
            Log.d(TAG, msg);
        }
    }
}
