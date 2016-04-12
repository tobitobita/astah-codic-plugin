package dsk.utils;

/**
 * システムに関するヘルパー関数の集まり。
 */
public final class SystemHelper {

    private SystemHelper() {
    }

    /**
     * 環境変数から値を取得する。
     *
     * @param key 環境変数のキー。
     * @return 環境変数の値。
     */
    public static String getEnv(final String key) {
        // 設定の差異を吸収するため2段階でとる。
        String value = System.getenv(key);
        if (value == null) {
            value = System.getProperty(key);
        }
        return value;
    }

    /**
     * Mac OS かを取得する。
     *
     * @return Mac OS の場合にtrueを返す。
     */
    public static boolean isMacOs() {
        return System.getProperty("os.name").startsWith("Mac");
    }
}
