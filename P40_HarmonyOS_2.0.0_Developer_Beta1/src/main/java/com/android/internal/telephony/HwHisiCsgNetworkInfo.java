package com.android.internal.telephony;

public class HwHisiCsgNetworkInfo {
    public boolean isSelectedFail;
    private String mCSGId;
    private String mCsgIdName;
    private int mCsgIdType;
    private boolean mIsConnected;
    private String mLongName;
    private String mOper;
    private int mRat;
    private int mRsrp;
    private int mRsrq;
    private String mShortName;
    private CSGTYPE mState;

    public enum CSGTYPE {
        CSG_ALLOW_LIST,
        CSG_OPERATOR_LIST_ALLOW,
        CSG_OPERATOR_LIST_FORBIDEN,
        CSG_UNKNOW_LIST
    }

    public HwHisiCsgNetworkInfo(String oper, String csgId, int rat, int csgIdType, String csgIdName, String longName, String shortName, int rsrp, int rsrq, boolean isConnected, CSGTYPE state) {
        this.isSelectedFail = false;
        this.mState = CSGTYPE.CSG_UNKNOW_LIST;
        this.mOper = oper;
        this.mCSGId = csgId;
        this.mRat = rat;
        this.mLongName = longName;
        this.mShortName = shortName;
        this.mCsgIdType = csgIdType;
        this.mCsgIdName = csgIdName;
        this.mRsrp = rsrp;
        this.mRsrq = rsrq;
        this.mIsConnected = isConnected;
        this.mState = state;
    }

    public HwHisiCsgNetworkInfo(String oper, String csgId, int rat, int csgIdType, String csgIdName, String longName, String shortName, int rsrp, int rsrq, boolean isConnected, String stateString) {
        this(oper, csgId, rat, csgIdType, csgIdName, longName, shortName, rsrp, rsrq, isConnected, rilStateToState(stateString));
    }

    public HwHisiCsgNetworkInfo(String oper, String CSGId, int rat) {
        this.isSelectedFail = false;
        this.mState = CSGTYPE.CSG_UNKNOW_LIST;
        this.mOper = oper;
        this.mCSGId = CSGId;
        this.mRat = rat;
    }

    private static CSGTYPE rilStateToState(String stateString) {
        if (stateString.equals("allow_list")) {
            return CSGTYPE.CSG_ALLOW_LIST;
        }
        if (stateString.equals("operator_list")) {
            return CSGTYPE.CSG_OPERATOR_LIST_ALLOW;
        }
        if (stateString.equals("forbiden_list")) {
            return CSGTYPE.CSG_OPERATOR_LIST_FORBIDEN;
        }
        if (stateString.equals("unallow_list")) {
            return CSGTYPE.CSG_UNKNOW_LIST;
        }
        throw new RuntimeException("RIL impl error: Invalid network state '" + stateString + "'");
    }

    public CSGTYPE getCSGState() {
        return this.mState;
    }

    public String getLongName() {
        return this.mLongName;
    }

    public void setLongName(String longName) {
        this.mLongName = longName;
    }

    public String getShortName() {
        return this.mShortName;
    }

    public void setShortName(String shortName) {
        this.mShortName = shortName;
    }

    public void setRsrp(int rsrp) {
        this.mRsrp = rsrp;
    }

    public void setRsrq(int rsrq) {
        this.mRsrq = rsrq;
    }

    public int getmRsrp() {
        return this.mRsrp;
    }

    public int getmRsrq() {
        return this.mRsrq;
    }

    public boolean ismIsConnected() {
        return this.mIsConnected;
    }

    public void setmIsConnected(boolean isConnected) {
        this.mIsConnected = isConnected;
    }

    public int getCsgIdType() {
        return this.mCsgIdType;
    }

    public void setCsgIdType(int csgId_type) {
        this.mCsgIdType = csgId_type;
    }

    public String getCsgIdName() {
        return this.mCsgIdName;
    }

    public void setCsgIdName(String csgId_name) {
        this.mCsgIdName = csgId_name;
    }

    public String getOper() {
        return this.mOper;
    }

    public void setOper(String oper) {
        this.mOper = oper;
    }

    public String getCSGId() {
        return this.mCSGId;
    }

    public void setCSGId(String CSGId) {
        this.mCSGId = CSGId;
    }

    public int getRat() {
        return this.mRat;
    }

    public void setRat(int rat) {
        this.mRat = rat;
    }

    public boolean isEmpty() {
        String str;
        String str2;
        return this.mRat == 0 && ((str = this.mOper) == null || str.isEmpty()) && ((str2 = this.mCSGId) == null || str2.isEmpty());
    }

    public String toString() {
        return "HwHisiCsgNetworkInfo{mOper='" + this.mOper + "', mCSGId='" + this.mCSGId + "', mRat=" + this.mRat + ", isSelectedFail=" + this.isSelectedFail + ", mLongName=" + this.mLongName + ", mShortName=" + this.mShortName + ", mCsgIdType=" + this.mCsgIdType + ", mCsgIdName=" + this.mCsgIdName + ", mRsrp=" + this.mRsrp + ", mRsrq=" + this.mRsrq + ", mState=" + this.mState + '}';
    }
}
