package android.iawareperf;

public class UniPerfIntf {
    public static final int UNIPERF_CLIENT_DEFAULT = 0;
    public static final int UNIPERF_CLIENT_EAPA = 3;
    public static final int UNIPERF_CLIENT_POWERGENIE = 1;
    public static final int UNIPERF_CLIENT_TEMPERATURE_CONTROL = 2;
    public static final int UNIPERF_CTRL_TYPE_HIGHPERF = 0;
    public static final int UNIPERF_CTRL_TYPE_LOW_TEMP_LOW_VOLTAGE = 4;
    public static final int UNIPERF_CTRL_TYPE_LOW_VOLTAGE = 3;
    public static final int UNIPERF_CTRL_TYPE_SUSTAINED_MODE = 1;
    public static final int UNIPERF_CTRL_TYPE_THERMAL_PROTECT = 2;
    public static final int UNIPERF_EVENT_APP_START = 4099;
    public static final int UNIPERF_EVENT_APP_WARMSTART = 4399;
    public static final int UNIPERF_EVENT_BENCHMARK = 4118;
    public static final int UNIPERF_EVENT_CLEAN_LIST = 20224;
    public static final int UNIPERF_EVENT_FINGERPRINT = 4103;
    public static final int UNIPERF_EVENT_GAME_SCENE = 4120;
    public static final int UNIPERF_EVENT_GAME_START = 4122;
    public static final int UNIPERF_EVENT_IAWARE_EAS_DEFAULT = 4113;
    public static final int UNIPERF_EVENT_IAWARE_EAS_SCREENOFF = 4114;
    public static final int UNIPERF_EVENT_KEYCTL_MOVE = 13200;
    public static final int UNIPERF_EVENT_LISTFLING = 4112;
    public static final int UNIPERF_EVENT_LUCKYMONEY = 4106;
    public static final int UNIPERF_EVENT_OFF = -1;
    public static final int UNIPERF_EVENT_ON = 0;
    public static final int UNIPERF_EVENT_ON_FIRE = 4121;
    public static final int UNIPERF_EVENT_PROBE = 4116;
    public static final int UNIPERF_EVENT_RESET_BOOST_EAS = 4101;
    public static final int UNIPERF_EVENT_ROTATION = 4105;
    public static final int UNIPERF_EVENT_SCREEN_ON = 4102;
    public static final int UNIPERF_EVENT_SCREEN_STATE = 20226;
    public static final int UNIPERF_EVENT_SCROLLER = 4097;
    public static final int UNIPERF_EVENT_SET_BOOST_EAS = 4100;
    public static final int UNIPERF_EVENT_SET_LEVEL_DEFAULT = 5376;
    public static final int UNIPERF_EVENT_SPECIAL_GAME_SCENE = 4119;
    public static final int UNIPERF_EVENT_STATUS_BAR = 4104;
    public static final int UNIPERF_EVENT_SUB_SWITCH = 20225;
    public static final int UNIPERF_EVENT_SUPER_MODE = 4123;
    public static final int UNIPERF_EVENT_TOUCH_MOVE = 4096;
    public static final int UNIPERF_EVENT_UPDATE_PARSER = 20230;
    public static final int UNIPERF_EVENT_WATCH_DEFAULT_CPUFREQ = 13242;
    public static final int UNIPERF_EVENT_WINDOW_SWITCH = 4098;
    public static final int UNIPERF_MAX_SPECIAL_SCENE = 4224;
    public static final int UNIPERF_TAG_B_CPU_CUR = 8;
    public static final int UNIPERF_TAG_B_CPU_MAX = 7;
    public static final int UNIPERF_TAG_B_CPU_MEMLAT_POL_INTERVAL = 35;
    public static final int UNIPERF_TAG_B_CPU_MEMLAT_TAR_RATIO = 32;
    public static final int UNIPERF_TAG_B_CPU_MIN = 6;
    public static final int UNIPERF_TAG_CTRL_TYPE_NEW = 57;
    public static final int UNIPERF_TAG_DDR_CUR = 14;
    public static final int UNIPERF_TAG_DDR_MAX = 13;
    public static final int UNIPERF_TAG_DDR_MIN = 12;
    public static final int UNIPERF_TAG_DEF_B_CPU_MAX = 48;
    public static final int UNIPERF_TAG_DEF_B_CPU_MIN = 47;
    public static final int UNIPERF_TAG_DEF_DDR_MAX = 52;
    public static final int UNIPERF_TAG_DEF_DDR_MIN = 51;
    public static final int UNIPERF_TAG_DEF_GPU_MAX = 50;
    public static final int UNIPERF_TAG_DEF_GPU_MIN = 49;
    public static final int UNIPERF_TAG_DEF_L3C_MAX = 54;
    public static final int UNIPERF_TAG_DEF_L3C_MIN = 53;
    public static final int UNIPERF_TAG_DEF_L_CPU_MAX = 44;
    public static final int UNIPERF_TAG_DEF_L_CPU_MIN = 43;
    public static final int UNIPERF_TAG_DEF_M_CPU_MAX = 46;
    public static final int UNIPERF_TAG_DEF_M_CPU_MIN = 45;
    public static final int UNIPERF_TAG_GOV_B_CPU_DELAY_DOWN = 26;
    public static final int UNIPERF_TAG_GOV_B_CPU_DELAY_UP = 25;
    public static final int UNIPERF_TAG_GOV_B_CPU_LOAD = 24;
    public static final int UNIPERF_TAG_GOV_GPU_DELAY_DOWN = 29;
    public static final int UNIPERF_TAG_GOV_GPU_DELAY_UP = 28;
    public static final int UNIPERF_TAG_GOV_GPU_LOAD = 27;
    public static final int UNIPERF_TAG_GOV_L_CPU_DELAY_DOWN = 20;
    public static final int UNIPERF_TAG_GOV_L_CPU_DELAY_UP = 19;
    public static final int UNIPERF_TAG_GOV_L_CPU_LOAD = 18;
    public static final int UNIPERF_TAG_GOV_M_CPU_DELAY_DOWN = 23;
    public static final int UNIPERF_TAG_GOV_M_CPU_DELAY_UP = 22;
    public static final int UNIPERF_TAG_GOV_M_CPU_LOAD = 21;
    public static final int UNIPERF_TAG_GPU_CUR = 11;
    public static final int UNIPERF_TAG_GPU_MAX = 10;
    public static final int UNIPERF_TAG_GPU_MIN = 9;
    public static final int UNIPERF_TAG_HISI_CPU_BOOST = 42;
    public static final int UNIPERF_TAG_HMP_POLICY_STATE = 60;
    public static final int UNIPERF_TAG_HMP_PRIORITY = 59;
    public static final int UNIPERF_TAG_HMP_THRES = 58;
    public static final int UNIPERF_TAG_IO_SPEED = 39;
    public static final int UNIPERF_TAG_IPA_SUSTAINABLE_POWER = 40;
    public static final int UNIPERF_TAG_IPA_SWITCH_TEMP = 41;
    public static final int UNIPERF_TAG_L3C_CUR = 17;
    public static final int UNIPERF_TAG_L3C_MAX = 16;
    public static final int UNIPERF_TAG_L3C_MIN = 15;
    public static final int UNIPERF_TAG_LATENCY_CPU = 36;
    public static final int UNIPERF_TAG_LATENCY_DDR = 38;
    public static final int UNIPERF_TAG_LATENCY_GPU = 37;
    public static final int UNIPERF_TAG_L_CPU_CUR = 2;
    public static final int UNIPERF_TAG_L_CPU_MAX = 1;
    public static final int UNIPERF_TAG_L_CPU_MEMLAT_POL_INTERVAL = 33;
    public static final int UNIPERF_TAG_L_CPU_MEMLAT_TAR_RATIO = 30;
    public static final int UNIPERF_TAG_L_CPU_MIN = 0;
    public static final int UNIPERF_TAG_MAX = 61;
    public static final int UNIPERF_TAG_M_CPU_CUR = 5;
    public static final int UNIPERF_TAG_M_CPU_MAX = 4;
    public static final int UNIPERF_TAG_M_CPU_MEMLAT_POL_INTERVAL = 34;
    public static final int UNIPERF_TAG_M_CPU_MEMLAT_TAR_RATIO = 31;
    public static final int UNIPERF_TAG_M_CPU_MIN = 3;
    public static final int UNIPERF_TAG_SCHED_LEVEL_CHANGE_ONE = 55;
    public static final int UNIPERF_TAG_SET_SCHED_LEVEL = 56;
    public static final int UNIPERF_TAG_SUB_SWITCH = 10000;
}