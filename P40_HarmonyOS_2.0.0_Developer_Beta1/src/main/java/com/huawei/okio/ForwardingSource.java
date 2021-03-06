package com.huawei.okio;

import java.io.IOException;

@Deprecated
public abstract class ForwardingSource implements Source {
    private final Source delegate;

    public ForwardingSource(Source delegate2) {
        if (delegate2 != null) {
            this.delegate = delegate2;
            return;
        }
        throw new IllegalArgumentException("delegate == null");
    }

    public final Source delegate() {
        return this.delegate;
    }

    @Override // com.huawei.okio.Source
    public long read(Buffer sink, long byteCount) throws IOException {
        return this.delegate.read(sink, byteCount);
    }

    @Override // com.huawei.okio.Source
    public Timeout timeout() {
        return this.delegate.timeout();
    }

    @Override // com.huawei.okio.Source, java.io.Closeable, java.lang.AutoCloseable
    public void close() throws IOException {
        this.delegate.close();
    }

    @Override // java.lang.Object
    public String toString() {
        return getClass().getSimpleName() + "(" + this.delegate.toString() + ")";
    }
}
