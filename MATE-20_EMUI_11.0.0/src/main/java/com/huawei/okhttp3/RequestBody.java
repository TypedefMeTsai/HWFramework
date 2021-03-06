package com.huawei.okhttp3;

import com.huawei.okhttp3.internal.Util;
import com.huawei.okio.BufferedSink;
import com.huawei.okio.ByteString;
import com.huawei.okio.Okio;
import com.huawei.okio.Source;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import javax.annotation.Nullable;

public abstract class RequestBody {
    @Nullable
    public abstract MediaType contentType();

    public abstract void writeTo(BufferedSink bufferedSink) throws IOException;

    public long contentLength() throws IOException {
        return -1;
    }

    public boolean isDuplex() {
        return false;
    }

    public boolean isOneShot() {
        return false;
    }

    public static RequestBody create(@Nullable MediaType contentType, String content) {
        Charset charset = StandardCharsets.UTF_8;
        if (contentType != null && (charset = contentType.charset()) == null) {
            charset = StandardCharsets.UTF_8;
            contentType = MediaType.parse(contentType + "; charset=utf-8");
        }
        return create(contentType, content.getBytes(charset));
    }

    public static RequestBody create(@Nullable final MediaType contentType, final ByteString content) {
        return new RequestBody() {
            /* class com.huawei.okhttp3.RequestBody.AnonymousClass1 */

            @Override // com.huawei.okhttp3.RequestBody
            @Nullable
            public MediaType contentType() {
                return MediaType.this;
            }

            @Override // com.huawei.okhttp3.RequestBody
            public long contentLength() throws IOException {
                return (long) content.size();
            }

            @Override // com.huawei.okhttp3.RequestBody
            public void writeTo(BufferedSink sink) throws IOException {
                sink.write(content);
            }
        };
    }

    public static RequestBody create(@Nullable MediaType contentType, byte[] content) {
        return create(contentType, content, 0, content.length);
    }

    public static RequestBody create(@Nullable final MediaType contentType, final byte[] content, final int offset, final int byteCount) {
        if (content != null) {
            Util.checkOffsetAndCount((long) content.length, (long) offset, (long) byteCount);
            return new RequestBody() {
                /* class com.huawei.okhttp3.RequestBody.AnonymousClass2 */

                @Override // com.huawei.okhttp3.RequestBody
                @Nullable
                public MediaType contentType() {
                    return MediaType.this;
                }

                @Override // com.huawei.okhttp3.RequestBody
                public long contentLength() {
                    return (long) byteCount;
                }

                @Override // com.huawei.okhttp3.RequestBody
                public void writeTo(BufferedSink sink) throws IOException {
                    sink.write(content, offset, byteCount);
                }
            };
        }
        throw new NullPointerException("content == null");
    }

    public static RequestBody create(@Nullable final MediaType contentType, final File file) {
        if (file != null) {
            return new RequestBody() {
                /* class com.huawei.okhttp3.RequestBody.AnonymousClass3 */

                @Override // com.huawei.okhttp3.RequestBody
                @Nullable
                public MediaType contentType() {
                    return MediaType.this;
                }

                @Override // com.huawei.okhttp3.RequestBody
                public long contentLength() {
                    return file.length();
                }

                /* JADX WARNING: Code restructure failed: missing block: B:11:?, code lost:
                    r0.close();
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:12:0x0018, code lost:
                    r3 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:13:0x0019, code lost:
                    r1.addSuppressed(r3);
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:14:0x001c, code lost:
                    throw r2;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:8:0x0011, code lost:
                    r2 = move-exception;
                 */
                /* JADX WARNING: Code restructure failed: missing block: B:9:0x0012, code lost:
                    if (r0 != null) goto L_0x0014;
                 */
                @Override // com.huawei.okhttp3.RequestBody
                public void writeTo(BufferedSink sink) throws IOException {
                    Source source = Okio.source(file);
                    sink.writeAll(source);
                    if (source != null) {
                        source.close();
                    }
                }
            };
        }
        throw new NullPointerException("file == null");
    }
}
