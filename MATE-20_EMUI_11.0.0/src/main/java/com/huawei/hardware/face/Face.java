package com.huawei.hardware.face;

import android.os.Parcel;
import android.os.Parcelable;

public final class Face implements Parcelable {
    public static final Parcelable.Creator<Face> CREATOR = new Parcelable.Creator<Face>() {
        /* class com.huawei.hardware.face.Face.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public Face createFromParcel(Parcel in) {
            return new Face(in);
        }

        @Override // android.os.Parcelable.Creator
        public Face[] newArray(int size) {
            return new Face[size];
        }
    };
    private long mDeviceId;

    public Face(long deviceId) {
        this.mDeviceId = deviceId;
    }

    private Face(Parcel in) {
        this.mDeviceId = in.readLong();
    }

    public long getDeviceId() {
        return this.mDeviceId;
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        out.writeLong(this.mDeviceId);
    }
}
