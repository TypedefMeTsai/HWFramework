package android.net.wifi;

import android.os.Parcel;
import android.os.Parcelable;
import android.os.Parcelable.Creator;
import java.util.ArrayList;
import java.util.List;

@Deprecated
public class BatchedScanResult implements Parcelable {
    public static final Creator<BatchedScanResult> CREATOR = null;
    private static final String TAG = "BatchedScanResult";
    public final List<ScanResult> scanResults;
    public boolean truncated;

    static {
        /* JADX: method processing error */
/*
        Error: jadx.core.utils.exceptions.DecodeException: Load method exception in method: android.net.wifi.BatchedScanResult.<clinit>():void
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:113)
	at jadx.core.dex.nodes.ClassNode.load(ClassNode.java:256)
	at jadx.core.ProcessClass.process(ProcessClass.java:34)
	at jadx.api.JadxDecompiler.processClass(JadxDecompiler.java:281)
	at jadx.api.JavaClass.decompile(JavaClass.java:59)
	at jadx.api.JadxDecompiler$1.run(JadxDecompiler.java:161)
Caused by: jadx.core.utils.exceptions.DecodeException:  in method: android.net.wifi.BatchedScanResult.<clinit>():void
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:46)
	at jadx.core.dex.nodes.MethodNode.load(MethodNode.java:98)
	... 5 more
Caused by: java.lang.IllegalArgumentException: bogus opcode: 0073
	at com.android.dx.io.OpcodeInfo.get(OpcodeInfo.java:1197)
	at com.android.dx.io.OpcodeInfo.getFormat(OpcodeInfo.java:1212)
	at com.android.dx.io.instructions.DecodedInstruction.decode(DecodedInstruction.java:72)
	at jadx.core.dex.instructions.InsnDecoder.decodeInsns(InsnDecoder.java:43)
	... 6 more
*/
        /*
        // Can't load method instructions.
        */
        throw new UnsupportedOperationException("Method not decompiled: android.net.wifi.BatchedScanResult.<clinit>():void");
    }

    public BatchedScanResult() {
        this.scanResults = new ArrayList();
    }

    public BatchedScanResult(BatchedScanResult source) {
        this.scanResults = new ArrayList();
        this.truncated = source.truncated;
        for (ScanResult s : source.scanResults) {
            this.scanResults.add(new ScanResult(s));
        }
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("BatchedScanResult: ").append("truncated: ").append(String.valueOf(this.truncated)).append("scanResults: [");
        for (ScanResult s : this.scanResults) {
            sb.append(" <").append(s.toString()).append("> ");
        }
        sb.append(" ]");
        return sb.toString();
    }

    public int describeContents() {
        return 0;
    }

    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.truncated ? 1 : 0);
        dest.writeInt(this.scanResults.size());
        for (ScanResult s : this.scanResults) {
            s.writeToParcel(dest, flags);
        }
    }
}
