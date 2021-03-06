package com.huawei.zxing.common.reedsolomon;

import com.huawei.motiondetection.MotionTypeApps;

public final class GenericGF {
    public static final GenericGF AZTEC_DATA_10 = new GenericGF(1033, 1024, 1);
    public static final GenericGF AZTEC_DATA_12 = new GenericGF(4201, 4096, 1);
    public static final GenericGF AZTEC_DATA_6 = new GenericGF(67, 64, 1);
    public static final GenericGF AZTEC_DATA_8 = DATA_MATRIX_FIELD_256;
    public static final GenericGF AZTEC_PARAM = new GenericGF(19, 16, 1);
    public static final GenericGF DATA_MATRIX_FIELD_256 = new GenericGF(MotionTypeApps.TYPE_PROXIMITY_ANSWER, 256, 1);
    private static final int INITIALIZATION_THRESHOLD = 0;
    public static final GenericGF MAXICODE_FIELD_64 = AZTEC_DATA_6;
    public static final GenericGF QR_CODE_FIELD_256 = new GenericGF(285, 256, 0);
    private int[] expTable;
    private final int generatorBase;
    private boolean initialized = false;
    private int[] logTable;
    private GenericGFPoly one;
    private final int primitive;
    private final int size;
    private GenericGFPoly zero;

    public GenericGF(int primitive2, int size2, int b) {
        this.primitive = primitive2;
        this.size = size2;
        this.generatorBase = b;
        if (size2 <= 0) {
            initialize();
        }
    }

    private void initialize() {
        int i = this.size;
        this.expTable = new int[i];
        this.logTable = new int[i];
        int x = 1;
        int i2 = 0;
        while (true) {
            int i3 = this.size;
            if (i2 >= i3) {
                break;
            }
            this.expTable[i2] = x;
            x <<= 1;
            if (x >= i3) {
                x = (x ^ this.primitive) & (i3 - 1);
            }
            i2++;
        }
        for (int i4 = 0; i4 < this.size - 1; i4++) {
            this.logTable[this.expTable[i4]] = i4;
        }
        this.zero = new GenericGFPoly(this, new int[]{0});
        this.one = new GenericGFPoly(this, new int[]{1});
        this.initialized = true;
    }

    private void checkInit() {
        if (!this.initialized) {
            initialize();
        }
    }

    /* access modifiers changed from: package-private */
    public GenericGFPoly getZero() {
        checkInit();
        return this.zero;
    }

    /* access modifiers changed from: package-private */
    public GenericGFPoly getOne() {
        checkInit();
        return this.one;
    }

    /* access modifiers changed from: package-private */
    public GenericGFPoly buildMonomial(int degree, int coefficient) {
        checkInit();
        if (degree < 0) {
            throw new IllegalArgumentException();
        } else if (coefficient == 0) {
            return this.zero;
        } else {
            int[] coefficients = new int[(degree + 1)];
            coefficients[0] = coefficient;
            return new GenericGFPoly(this, coefficients);
        }
    }

    static int addOrSubtract(int a, int b) {
        return a ^ b;
    }

    /* access modifiers changed from: package-private */
    public int exp(int a) {
        checkInit();
        return this.expTable[a];
    }

    /* access modifiers changed from: package-private */
    public int log(int a) {
        checkInit();
        if (a != 0) {
            return this.logTable[a];
        }
        throw new IllegalArgumentException();
    }

    /* access modifiers changed from: package-private */
    public int inverse(int a) {
        checkInit();
        if (a != 0) {
            return this.expTable[(this.size - this.logTable[a]) - 1];
        }
        throw new ArithmeticException();
    }

    /* access modifiers changed from: package-private */
    public int multiply(int a, int b) {
        checkInit();
        if (a == 0 || b == 0) {
            return 0;
        }
        int[] iArr = this.expTable;
        int[] iArr2 = this.logTable;
        return iArr[(iArr2[a] + iArr2[b]) % (this.size - 1)];
    }

    public int getSize() {
        return this.size;
    }

    public int getGeneratorBase() {
        return this.generatorBase;
    }

    public String toString() {
        return "GF(0x" + Integer.toHexString(this.primitive) + ',' + this.size + ')';
    }
}
