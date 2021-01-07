package FileEngine.Web.Plugin.checkVersion;

public class VersionCheckUtil {
    public static final String version = "1.0";
    private static String updateUrl;

    public static boolean isLatest() {
        //todo 检查是否为最新版本，如果不是，则给updateURL赋值。
        return true;
    }

    public static String getUpdateUrl() {
        return updateUrl;
    }
}
