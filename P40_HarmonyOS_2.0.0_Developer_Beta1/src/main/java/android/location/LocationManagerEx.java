package android.location;

import android.common.HwPartBaseLocationFactory;
import java.util.ArrayList;

public class LocationManagerEx {
    private static final int DEFAULT_RESULT = -1;
    private static final int DEFAULT_SIZE = 16;
    public static final int POWER_ERROR = -1;
    public static final int POWER_HIGH = 2;
    public static final int POWER_LOW = 1;
    public static final int POWER_NONE = 0;

    public static int getPowerTypeByPackageName(String packageName) {
        IHwInnerLocationManager locationManager = HwPartBaseLocationFactory.loadFactory().createHwInnerLocationManager();
        if (locationManager != null) {
            return locationManager.getPowerTypeByPackageName(packageName);
        }
        return -1;
    }

    public static int logEvent(int type, int event, String parameter) {
        IHwInnerLocationManager locationManager = HwPartBaseLocationFactory.loadFactory().createHwInnerLocationManager();
        if (locationManager != null) {
            return locationManager.logEvent(type, event, parameter);
        }
        return -1;
    }

    public static String getNetworkProviderPackage() {
        return "";
    }

    public static ArrayList<String> gnssDetect(String packageName) {
        ArrayList<String> results = new ArrayList<>(16);
        IHwInnerLocationManager locationManager = HwPartBaseLocationFactory.loadFactory().createHwInnerLocationManager();
        if (locationManager != null) {
            return locationManager.gnssDetect(packageName);
        }
        return results;
    }
}
