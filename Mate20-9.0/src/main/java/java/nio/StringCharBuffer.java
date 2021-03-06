package java.nio;

class StringCharBuffer extends CharBuffer {
    CharSequence str;

    StringCharBuffer(CharSequence s, int start, int end) {
        super(-1, start, end, s.length());
        int n = s.length();
        if (start < 0 || start > n || end < start || end > n) {
            throw new IndexOutOfBoundsException();
        }
        this.str = s;
    }

    public CharBuffer slice() {
        StringCharBuffer stringCharBuffer = new StringCharBuffer(this.str, -1, 0, remaining(), remaining(), this.offset + position());
        return stringCharBuffer;
    }

    private StringCharBuffer(CharSequence s, int mark, int pos, int limit, int cap, int offset) {
        super(mark, pos, limit, cap, null, offset);
        this.str = s;
    }

    public CharBuffer duplicate() {
        StringCharBuffer stringCharBuffer = new StringCharBuffer(this.str, markValue(), position(), limit(), capacity(), this.offset);
        return stringCharBuffer;
    }

    public CharBuffer asReadOnlyBuffer() {
        return duplicate();
    }

    public final char get() {
        return this.str.charAt(nextGetIndex() + this.offset);
    }

    public final char get(int index) {
        return this.str.charAt(checkIndex(index) + this.offset);
    }

    /* access modifiers changed from: package-private */
    public char getUnchecked(int index) {
        return this.str.charAt(this.offset + index);
    }

    public final CharBuffer put(char c) {
        throw new ReadOnlyBufferException();
    }

    public final CharBuffer put(int index, char c) {
        throw new ReadOnlyBufferException();
    }

    public final CharBuffer compact() {
        throw new ReadOnlyBufferException();
    }

    public final boolean isReadOnly() {
        return true;
    }

    /* access modifiers changed from: package-private */
    public final String toString(int start, int end) {
        return this.str.toString().substring(this.offset + start, this.offset + end);
    }

    public final CharBuffer subSequence(int start, int end) {
        try {
            int pos = position();
            StringCharBuffer stringCharBuffer = new StringCharBuffer(this.str, -1, pos + checkIndex(start, pos), pos + checkIndex(end, pos), capacity(), this.offset);
            return stringCharBuffer;
        } catch (IllegalArgumentException e) {
            throw new IndexOutOfBoundsException();
        }
    }

    public boolean isDirect() {
        return false;
    }

    public ByteOrder order() {
        return ByteOrder.nativeOrder();
    }
}
