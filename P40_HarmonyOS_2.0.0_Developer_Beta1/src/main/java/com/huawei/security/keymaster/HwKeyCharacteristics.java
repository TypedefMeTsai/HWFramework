package com.huawei.security.keymaster;

import android.os.Parcel;
import android.os.Parcelable;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class HwKeyCharacteristics implements Parcelable {
    private static final int ARRAY_LIST_SIZE = 16;
    public static final Parcelable.Creator<HwKeyCharacteristics> CREATOR = new Parcelable.Creator<HwKeyCharacteristics>() {
        /* class com.huawei.security.keymaster.HwKeyCharacteristics.AnonymousClass1 */

        @Override // android.os.Parcelable.Creator
        public HwKeyCharacteristics createFromParcel(Parcel in) {
            return new HwKeyCharacteristics(in);
        }

        @Override // android.os.Parcelable.Creator
        public HwKeyCharacteristics[] newArray(int length) {
            return new HwKeyCharacteristics[length];
        }
    };
    public HwKeymasterArguments hwEnforced;
    public HwKeymasterArguments swEnforced;

    public HwKeyCharacteristics() {
    }

    protected HwKeyCharacteristics(Parcel in) {
        readFromParcel(in);
    }

    @Override // android.os.Parcelable
    public int describeContents() {
        return 0;
    }

    @Override // android.os.Parcelable
    public void writeToParcel(Parcel out, int flags) {
        this.swEnforced.writeToParcel(out, flags);
        this.hwEnforced.writeToParcel(out, flags);
    }

    public final void readFromParcel(Parcel in) {
        this.swEnforced = HwKeymasterArguments.CREATOR.createFromParcel(in);
        this.hwEnforced = HwKeymasterArguments.CREATOR.createFromParcel(in);
    }

    public Integer getEnum(int tag) {
        if (this.hwEnforced.containsTag(tag)) {
            return Integer.valueOf(this.hwEnforced.getEnum(tag, -1));
        }
        if (this.swEnforced.containsTag(tag)) {
            return Integer.valueOf(this.swEnforced.getEnum(tag, -1));
        }
        return null;
    }

    public List<Integer> getEnums(int tag) {
        List<Integer> result = new ArrayList<>(16);
        result.addAll(this.hwEnforced.getEnums(tag));
        result.addAll(this.swEnforced.getEnums(tag));
        return result;
    }

    public long getUnsignedInt(int tag, long defaultValue) {
        if (this.hwEnforced.containsTag(tag)) {
            return this.hwEnforced.getUnsignedInt(tag, defaultValue);
        }
        return this.swEnforced.getUnsignedInt(tag, defaultValue);
    }

    public List<BigInteger> getUnsignedLongs(int tag) {
        List<BigInteger> result = new ArrayList<>(16);
        result.addAll(this.hwEnforced.getUnsignedLongs(tag));
        result.addAll(this.swEnforced.getUnsignedLongs(tag));
        return result;
    }

    public Date getDate(int tag) {
        Date result = this.swEnforced.getDate(tag, null);
        if (result != null) {
            return result;
        }
        return this.hwEnforced.getDate(tag, null);
    }

    public boolean getBoolean(int tag) {
        if (this.hwEnforced.containsTag(tag)) {
            return this.hwEnforced.getBoolean(tag);
        }
        return this.swEnforced.getBoolean(tag);
    }
}
