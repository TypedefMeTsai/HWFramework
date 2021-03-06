package com.android.server.pm;

import android.content.pm.ApplicationInfo;
import android.content.pm.IShortcutService;
import android.content.pm.PackageParser;
import android.os.Binder;
import android.os.FileUtils;
import android.os.IBackupSessionCallback;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.UserHandle;
import android.os.UserManagerInternal;
import android.text.TextUtils;
import android.util.ArrayMap;
import android.util.Log;
import android.util.Slog;
import android.util.SparseArray;
import com.android.server.LocalServices;
import com.android.server.pm.Installer;
import com.android.server.pm.auth.HwCertification;
import com.android.server.wm.HwWmConstants;
import com.huawei.server.am.ProcessListEx;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

public final class HwFileBackupManager {
    public static final int BACKUP_TASK_CMD_ARG_MAX = 6;
    public static final int BACKUP_TASK_CMD_ARG_NORMAL = 4;
    public static final int BACKUP_TASK_CMD_ARG_THREE = 3;
    public static final int BACKUP_TASK_CMD_ARG_TWO = 2;
    public static final int BACKUP_TASK_FAILED = -1;
    public static final int BACKUP_TASK_NO_PERMISSION = -2;
    public static final int BACKUP_TASK_SUCCESS = 0;
    public static final int BACKUP_TASK_UNSUPPORTED_CMD = -3;
    private static final String CLEAR_SHORTCUTS_TEMP_FILES_CMD = "clear shortcuts temp files";
    private static final String[] PACKAGE_NAMES_FILE_BACKUP = {"com.hicloud.android.clone", HwWmConstants.INTELLIGENT_PKG_NAME, "com.huawei.KoBackup", "com.huawei.hidisk", "com.huawei.localBackup", "com.huawei.phoneClone"};
    public static final int PATH_INVALID = -1;
    private static final String RESTORE_SHORTCUTS_CMD = "restore shortcuts";
    public static final int SESSION_DELETE_BY_INSTALLD = 5;
    private static final String SHORTCUT_CLONE_DATA_DIR = "/data/data/android/shortcut_clone";
    private static final String SHORTCUT_SERVICE_DIR_SUFFIX = "/shortcut_service";
    private static final String SHORTCUT_SERVICE_SYSTEM_DIR = "/data/system_ce/0/shortcut_service";
    private static final String TAG = "HwFileBackupManager_BackupSession";
    private static final int VERSION_CODE = 3;
    private static volatile HwFileBackupManager sInstance;
    private final ArrayList<BackupDeathHandler> mBackupDeathHandlers = new ArrayList<>();
    private final Installer mInstaller;
    private NativeBackupCallback mNativeBackupCallback = new NativeBackupCallback();
    private final SparseArray<IBackupSessionCallback> mSessions = new SparseArray<>();
    private UserManagerInternal mUserManagerInternal;

    private HwFileBackupManager(Installer installer) {
        this.mInstaller = installer;
    }

    public static HwFileBackupManager getInstance(Installer installer) {
        if (sInstance == null) {
            synchronized (HwFileBackupManager.class) {
                if (sInstance == null) {
                    sInstance = new HwFileBackupManager(installer);
                }
            }
        }
        return sInstance;
    }

    /* access modifiers changed from: private */
    public class BackupDeathHandler implements IBinder.DeathRecipient {
        private IBinder mCb;
        private int mSessionId;

        BackupDeathHandler(int sessionId, IBinder cb) {
            this.mSessionId = sessionId;
            this.mCb = cb;
        }

        @Override // android.os.IBinder.DeathRecipient
        public void binderDied() {
            Log.w(HwFileBackupManager.TAG, "backup client with sessionId " + this.mSessionId + " died");
            long ident = Binder.clearCallingIdentity();
            HwFileBackupManager.this.finishBackupSession(this.mSessionId);
            Binder.restoreCallingIdentity(ident);
        }

        public IBinder getBinder() {
            return this.mCb;
        }

        public int getSessionId() {
            return this.mSessionId;
        }
    }

    /* access modifiers changed from: private */
    public final class NativeBackupCallback extends IBackupSessionCallback.Stub {
        private NativeBackupCallback() {
        }

        public void onTaskStatusChanged(int sessionId, int taskId, int statusCode, String appendData) {
            HwFileBackupManager.this.handleNativeBackupSessionCallback(sessionId, taskId, statusCode, appendData);
        }
    }

    /* access modifiers changed from: private */
    /* access modifiers changed from: public */
    private void handleNativeBackupSessionCallback(int sessionId, int taskId, int statusCode, String appendData) {
        synchronized (this.mSessions) {
            IBackupSessionCallback callback = this.mSessions.get(sessionId);
            if (callback == null) {
                Log.e(TAG, "no callback set for session:" + sessionId);
                return;
            }
            try {
                callback.onTaskStatusChanged(sessionId, taskId, statusCode, appendData);
                if (statusCode == 5) {
                    finishBackupSession(sessionId);
                }
            } catch (RemoteException e) {
                Log.w(TAG, "callback binder death!");
            }
        }
    }

    public int startBackupSession(IBackupSessionCallback callback) {
        Slog.i(TAG, "application bind call startBackupSession");
        if (callback == null) {
            Slog.e(TAG, "startBackupSession but callback is null!");
            return -1;
        }
        try {
            if (this.mInstaller == null) {
                Slog.e(TAG, "startBackupSession but installer is null!");
                return -1;
            }
            int sessionId = this.mInstaller.startBackupSession(this.mNativeBackupCallback);
            Slog.i(TAG, "application startBackupSession sessionid:" + sessionId);
            if (sessionId < 0) {
                return -1;
            }
            synchronized (this.mBackupDeathHandlers) {
                BackupDeathHandler hdlr = new BackupDeathHandler(sessionId, callback.asBinder());
                try {
                    callback.asBinder().linkToDeath(hdlr, 0);
                    this.mBackupDeathHandlers.add(hdlr);
                } catch (RemoteException e) {
                    Log.w(TAG, "startBackupSession() could not link to " + callback.asBinder() + " binder death");
                    return -1;
                }
            }
            synchronized (this.mSessions) {
                this.mSessions.put(sessionId, callback);
            }
            return sessionId;
        } catch (Installer.InstallerException e2) {
            Slog.w(TAG, "Trouble startBackupSession");
            return -1;
        }
    }

    public int executeBackupTask(int sessionId, String taskCmd) {
        Slog.i(TAG, "bind call executeBackupTask on session:" + sessionId + ", taskCmd: " + taskCmd);
        if (sessionId == -1 && "getVersionCode".equalsIgnoreCase(taskCmd)) {
            return 3;
        }
        synchronized (this.mSessions) {
            if (this.mSessions.indexOfKey(sessionId) < 0) {
                Slog.e(TAG, "no session with id=" + sessionId);
                return -1;
            }
        }
        try {
            if (TextUtils.isEmpty(taskCmd)) {
                return -3;
            }
            if (RESTORE_SHORTCUTS_CMD.equals(taskCmd)) {
                if (!backupShortcuts(new File("/data/data/android/shortcut_clone/shortcut_service"), new File(SHORTCUT_SERVICE_SYSTEM_DIR))) {
                    return -1;
                }
                Slog.i(TAG, "Succeed to backup shortcuts data to /data/system_ce/0/shortcut_service in new phone!");
                clearShortcutsTempFiles();
                return restoreShortcuts();
            } else if (CLEAR_SHORTCUTS_TEMP_FILES_CMD.equals(taskCmd)) {
                clearShortcutsTempFiles();
                return 0;
            } else if (this.mInstaller != null) {
                return this.mInstaller.executeBackupTask(sessionId, taskCmd);
            } else {
                Slog.e(TAG, "executeBackupTask but installer is null!");
                return -1;
            }
        } catch (Installer.InstallerException e) {
            Slog.w(TAG, "Trouble executeBackupTask");
            return -1;
        }
    }

    private boolean backupShortcuts(File srcPath, File destPath) {
        if (srcPath == null || destPath == null) {
            return false;
        }
        if (!srcPath.isDirectory()) {
            Slog.w(TAG, srcPath + " is not directory.");
            return false;
        }
        File[] srcFiles = srcPath.listFiles();
        if (destPath.isDirectory() || destPath.mkdirs()) {
            for (File srcFile : srcFiles) {
                File destFile = new File(destPath, srcFile.getName());
                Slog.i(TAG, "srcFile: " + srcFile.toString() + ", destFile: " + destFile.toString());
                if (srcFile.isFile()) {
                    if (!FileUtils.copyFile(srcFile, destFile)) {
                        Slog.w(TAG, "copy file failed");
                        return false;
                    }
                } else if (srcFile.isDirectory() && !backupShortcuts(srcFile, destFile)) {
                    Slog.w(TAG, "copy dir failed");
                    return false;
                }
            }
            return true;
        }
        Slog.w(TAG, destPath + " mkdir fail.");
        return false;
    }

    private int restoreShortcuts() {
        try {
            getShortcutService().restoreShortcuts(0);
            return 0;
        } catch (RemoteException e) {
            Slog.w(TAG, "Call restoreShortcuts failed for RemoteException!");
            return -1;
        } catch (Exception e2) {
            Slog.w(TAG, "Restore failed for Exception");
            return -1;
        }
    }

    private void clearShortcutsTempFiles() {
        recursionDeleteFile(new File(SHORTCUT_CLONE_DATA_DIR));
    }

    private void recursionDeleteFile(File toDeletefile) {
        if (toDeletefile != null) {
            if (toDeletefile.isFile()) {
                toDeletefile.delete();
            } else if (toDeletefile.isDirectory()) {
                File[] childFile = toDeletefile.listFiles();
                if (childFile == null || childFile.length == 0) {
                    toDeletefile.delete();
                    return;
                }
                for (File file : childFile) {
                    recursionDeleteFile(file);
                }
                toDeletefile.delete();
            }
        }
    }

    private IShortcutService getShortcutService() {
        return IShortcutService.Stub.asInterface(ServiceManager.getService("shortcut"));
    }

    public int finishBackupSession(int sessionId) {
        Slog.i(TAG, "bind call finishBackupSession sessionId:" + sessionId);
        int result = -1;
        try {
            if (this.mInstaller == null) {
                Slog.e(TAG, "finishBackupSession but installer is null!");
                return -1;
            }
            result = this.mInstaller.finishBackupSession(sessionId);
            synchronized (this.mSessions) {
                this.mSessions.remove(sessionId);
            }
            synchronized (this.mBackupDeathHandlers) {
                for (int i = this.mBackupDeathHandlers.size() - 1; i >= 0; i--) {
                    BackupDeathHandler hdlr = this.mBackupDeathHandlers.get(i);
                    if (hdlr.getSessionId() == sessionId) {
                        this.mBackupDeathHandlers.remove(i);
                        try {
                            hdlr.getBinder().unlinkToDeath(hdlr, 0);
                        } catch (NoSuchElementException e) {
                            Log.e(TAG, "unlinkToDeath NoSuchElementException");
                        } catch (Exception e2) {
                            Log.e(TAG, "unlinkToDeath exception");
                        }
                    }
                }
            }
            return result;
        } catch (Installer.InstallerException e3) {
            Slog.w(TAG, "Trouble finishBackupSession");
        }
    }

    public boolean checkBackupPackageName(String pkgName) {
        boolean isBackup = false;
        String[] strArr = PACKAGE_NAMES_FILE_BACKUP;
        int length = strArr.length;
        int i = 0;
        while (true) {
            if (i >= length) {
                break;
            } else if (TextUtils.equals(pkgName, strArr[i])) {
                isBackup = true;
                break;
            } else {
                i++;
            }
        }
        if (!isBackup) {
            Slog.d(TAG, "BackupSession checkBackupPackageName failed, pkgName is " + pkgName);
        }
        return isBackup;
    }

    private String normalizeTaskCmd(String taskCmd, List<String> cmdInfo) {
        String[] args = taskCmd.split(" ");
        StringBuilder sbTaskCmd = new StringBuilder();
        for (String str : args) {
            String arg = str.replace(" ", "");
            if (!TextUtils.isEmpty(arg)) {
                if (cmdInfo != null) {
                    cmdInfo.add(arg);
                }
                sbTaskCmd.append(arg);
                sbTaskCmd.append(" ");
            }
        }
        String normalizedTaskCmd = sbTaskCmd.toString().trim();
        Slog.d(TAG, "BackupSession prepareBackupTaskCmd,after normalize is:" + normalizedTaskCmd);
        return normalizedTaskCmd;
    }

    public String prepareBackupTaskCmd(String taskCmd, ArrayMap<String, PackageParser.Package> packages) {
        if (CLEAR_SHORTCUTS_TEMP_FILES_CMD.equals(taskCmd)) {
            return taskCmd;
        }
        if (TextUtils.isEmpty(taskCmd) || packages == null) {
            return "";
        }
        List<String> cmdInfo = new ArrayList<>(6);
        String normalizedTaskCmd = normalizeTaskCmd(taskCmd, cmdInfo);
        if (cmdInfo.size() > 6) {
            return "";
        }
        if (cmdInfo.size() < 4) {
            return normalizedTaskCmd;
        }
        String srcPath = cmdInfo.get(2);
        PathData srcPathData = PathData.create(srcPath);
        if (!isUsableSrcPath(srcPathData)) {
            Slog.e(TAG, "is not a supported src data path!");
            return "";
        }
        PathData destPathData = PathData.create(cmdInfo.get(3));
        if (!isUsableDestPath(destPathData)) {
            Slog.e(TAG, "is not a supported dest path!");
            return "";
        } else if (destPathData.isShortcutPath) {
            StringBuilder sb = new StringBuilder(getShortcutsBackupCmdByDestPath(cmdInfo, SHORTCUT_CLONE_DATA_DIR));
            sb.append(" ");
            sb.append(HwCertification.SIGNATURE_PLATFORM);
            sb.append(" ");
            sb.append(1000);
            Slog.i(TAG, "Shortcuts backup cmd: " + sb.toString());
            return sb.toString();
        } else {
            if (srcPathData.isShortcutPath) {
                clearShortcutsTempFiles();
                if (backupShortcuts(new File(srcPath), new File("/data/data/android/shortcut_clone/shortcut_service"))) {
                    normalizedTaskCmd = getShortcutsBackupCmdBySrcPath(cmdInfo, "/data/data/android/shortcut_clone/shortcut_service");
                }
            }
            synchronized (packages) {
                PackageParser.Package pkg = packages.get(destPathData.packageName);
                if (pkg == null || pkg.applicationInfo == null) {
                    Slog.d(TAG, "BackupSession prepareBackupTaskCmd, target path must begin with a existing app's data directory since we need get seinfo for task cmd!");
                    return "";
                }
                ApplicationInfo app = pkg.applicationInfo;
                StringBuilder sb2 = new StringBuilder(normalizedTaskCmd);
                sb2.append(" ");
                sb2.append(app.seInfo);
                sb2.append(" ");
                if (!destPathData.isMultiUserPath) {
                    if (!destPathData.isShortcutPath) {
                        sb2.append(app.uid);
                        return sb2.toString();
                    }
                }
                sb2.append(UserHandle.getUid(destPathData.userId, app.uid));
                return sb2.toString();
            }
        }
    }

    private String getShortcutsBackupCmdBySrcPath(List<String> cmdInfo, String newSrcPath) {
        StringBuilder sb = new StringBuilder(cmdInfo.get(0));
        sb.append(" ");
        sb.append(cmdInfo.get(1));
        sb.append(" ");
        sb.append(newSrcPath);
        sb.append(" ");
        sb.append(cmdInfo.get(3));
        Slog.i(TAG, "Shortcuts backup task cmd: " + sb.toString());
        return sb.toString();
    }

    private String getShortcutsBackupCmdByDestPath(List<String> cmdInfo, String newDestPath) {
        return cmdInfo.get(0) + " " + cmdInfo.get(1) + " " + cmdInfo.get(2) + " " + newDestPath;
    }

    private UserManagerInternal getUserManagerInternal() {
        if (this.mUserManagerInternal == null) {
            this.mUserManagerInternal = (UserManagerInternal) LocalServices.getService(UserManagerInternal.class);
        }
        return this.mUserManagerInternal;
    }

    private boolean isUsableSrcPath(PathData pathData) {
        if (pathData == null) {
            return false;
        }
        if (!isUsableShortcutDataPath(pathData) && pathData.isAppDataPath) {
            return isUsableAppDataPath(pathData);
        }
        return true;
    }

    private boolean isUsableDestPath(PathData pathData) {
        if (isUsableShortcutDataPath(pathData)) {
            return true;
        }
        if (pathData != null && !TextUtils.isEmpty(pathData.packageName)) {
            return isUsableAppDataPath(pathData);
        }
        Slog.e(TAG, "dest path does not contain package name, check package name is null!");
        return false;
    }

    private boolean isUsableAppDataPath(PathData pathData) {
        if (pathData == null || !pathData.isAppDataPath) {
            return false;
        }
        if (!pathData.isMultiUserPath || pathData.userId == 0) {
            return true;
        }
        if (HwPackageManagerService.isSupportCloneAppInCust(pathData.packageName) && getUserManagerInternal().isClonedProfile(pathData.userId)) {
            return true;
        }
        Slog.e(TAG, pathData.path + " is not a support clone app data path!");
        return false;
    }

    private boolean isUsableShortcutDataPath(PathData pathData) {
        if (pathData == null || !pathData.isShortcutPath) {
            return false;
        }
        return true;
    }

    /* access modifiers changed from: private */
    public static class PathData {
        private static final String DATA_DATA_PATH = "/data/data/";
        private static final String MULTI_USER_PATH = "/data/user/";
        private boolean isAppDataPath = true;
        private boolean isMultiUserPath;
        private boolean isShortcutPath;
        private String packageName;
        private String path;
        private int userId = ProcessListEx.INVALID_ADJ;

        private PathData() {
        }

        public static PathData create(String path2) {
            if (TextUtils.isEmpty(path2)) {
                return null;
            }
            PathData instance = new PathData();
            instance.path = path2;
            if (path2.startsWith(MULTI_USER_PATH)) {
                instance.isMultiUserPath = true;
            } else if (path2.startsWith(DATA_DATA_PATH)) {
                instance.isMultiUserPath = false;
            } else if (HwFileBackupManager.SHORTCUT_SERVICE_SYSTEM_DIR.equals(path2) || HwFileBackupManager.SHORTCUT_CLONE_DATA_DIR.equals(path2)) {
                Slog.i(HwFileBackupManager.TAG, path2 + " is shortcut dir!");
                instance.isShortcutPath = true;
            } else {
                instance.isAppDataPath = false;
            }
            instance.parsePath();
            return instance;
        }

        private void parsePath() {
            if (!this.isAppDataPath) {
                Slog.d(HwFileBackupManager.TAG, this.path + " is not a app data path,no need parse!");
            } else if (this.isMultiUserPath) {
                parseMultiUserPath();
            } else if (this.isShortcutPath) {
                parseShortcutDataPath();
            } else {
                parseDefaultDataPath();
            }
        }

        private void parseMultiUserPath() {
            int startIndex = MULTI_USER_PATH.length();
            if (startIndex >= this.path.length()) {
                Slog.e(HwFileBackupManager.TAG, this.path + " does not contain userId!");
                return;
            }
            int userIdLocation = this.path.indexOf("/", startIndex);
            if (userIdLocation == -1) {
                Slog.e(HwFileBackupManager.TAG, this.path + " does not contain userId or package name!");
                return;
            }
            String userIdStr = this.path.substring(startIndex, userIdLocation);
            if (!TextUtils.isEmpty(userIdStr)) {
                try {
                    this.userId = Integer.parseInt(userIdStr);
                } catch (NumberFormatException e) {
                    Slog.e(HwFileBackupManager.TAG, this.path + " does not contain correct userId, find:" + userIdStr);
                    return;
                }
            }
            this.packageName = getPackageName(this.path, userIdLocation + 1);
        }

        private void parseDefaultDataPath() {
            this.packageName = getPackageName(this.path, DATA_DATA_PATH.length());
        }

        private String getPackageName(String pathString, int startIndex) {
            String pkgName;
            if (TextUtils.isEmpty(pathString) || startIndex >= pathString.length()) {
                Slog.e(HwFileBackupManager.TAG, this.path + " does not contain package name!");
                return "";
            }
            int endIndex = pathString.indexOf("/", startIndex);
            if (endIndex == -1) {
                pkgName = pathString.substring(startIndex).trim();
            } else {
                pkgName = pathString.substring(startIndex, endIndex).trim();
            }
            Slog.d(HwFileBackupManager.TAG, pkgName + " found in " + pathString);
            return pkgName;
        }

        private void parseShortcutDataPath() {
            Slog.i(HwFileBackupManager.TAG, "this is shortcut data path, does not contain package name!");
            this.packageName = "";
        }
    }
}
