package android.graphics;

import java.io.OutputStream;

public class YuvImage {
    private static final int WORKING_COMPRESS_STORAGE = 4096;
    private byte[] mData;
    private int mFormat;
    private int mHeight;
    private int[] mStrides;
    private int mWidth;

    private static native boolean nativeCompressToJpeg(byte[] bArr, int i, int i2, int i3, int[] iArr, int[] iArr2, int i4, OutputStream outputStream, byte[] bArr2);

    public YuvImage(byte[] yuv, int format, int width, int height, int[] strides) {
        if (format != 17 && format != 20) {
            throw new IllegalArgumentException("only support ImageFormat.NV21 and ImageFormat.YUY2 for now");
        } else if (width <= 0 || height <= 0) {
            throw new IllegalArgumentException("width and height must large than 0");
        } else if (yuv != null) {
            if (strides == null) {
                this.mStrides = calculateStrides(width, format);
            } else {
                this.mStrides = strides;
            }
            this.mData = yuv;
            this.mFormat = format;
            this.mWidth = width;
            this.mHeight = height;
        } else {
            throw new IllegalArgumentException("yuv cannot be null");
        }
    }

    public boolean compressToJpeg(Rect rectangle, int quality, OutputStream stream) {
        if (!new Rect(0, 0, this.mWidth, this.mHeight).contains(rectangle)) {
            throw new IllegalArgumentException("rectangle is not inside the image");
        } else if (quality < 0 || quality > 100) {
            throw new IllegalArgumentException("quality must be 0..100");
        } else if (stream != null) {
            adjustRectangle(rectangle);
            return nativeCompressToJpeg(this.mData, this.mFormat, rectangle.width(), rectangle.height(), calculateOffsets(rectangle.left, rectangle.top), this.mStrides, quality, stream, new byte[4096]);
        } else {
            throw new IllegalArgumentException("stream cannot be null");
        }
    }

    public byte[] getYuvData() {
        return this.mData;
    }

    public int getYuvFormat() {
        return this.mFormat;
    }

    public int[] getStrides() {
        return this.mStrides;
    }

    public int getWidth() {
        return this.mWidth;
    }

    public int getHeight() {
        return this.mHeight;
    }

    /* access modifiers changed from: package-private */
    public int[] calculateOffsets(int left, int top) {
        int i = this.mFormat;
        if (i == 17) {
            int[] iArr = this.mStrides;
            return new int[]{(iArr[0] * top) + left, (this.mHeight * iArr[0]) + ((top / 2) * iArr[1]) + ((left / 2) * 2)};
        } else if (i == 20) {
            return new int[]{(this.mStrides[0] * top) + ((left / 2) * 4)};
        } else {
            return null;
        }
    }

    private int[] calculateStrides(int width, int format) {
        if (format == 17) {
            return new int[]{width, width};
        }
        if (format == 20) {
            return new int[]{width * 2};
        }
        return null;
    }

    private void adjustRectangle(Rect rect) {
        int width = rect.width();
        int height = rect.height();
        if (this.mFormat == 17) {
            width &= -2;
            rect.left &= -2;
            rect.top &= -2;
            rect.right = rect.left + width;
            rect.bottom = rect.top + (height & -2);
        }
        if (this.mFormat == 20) {
            rect.left &= -2;
            rect.right = rect.left + (width & -2);
        }
    }
}