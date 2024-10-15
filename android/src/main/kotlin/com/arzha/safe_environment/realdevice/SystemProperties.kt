package com.arzha.safe_environment.realdevice

public class SystemProperties {

    private static final Class<?> SP = getSystemPropertiesClass();
    public static String get(String key) {
        try {
            return (String) SP.getMethod("get", String.class).invoke(null, key);
        } catch (Exception e) {
            return null;
        }
    }

    public static String get(String key, String def) {
        try {
            return (String) SP.getMethod("get", String.class, String.class).invoke(null, key, def);
        } catch (Exception e) {
            return def;
        }
    }

    public static boolean getBoolean(String key, boolean def) {
        try {
            return (Boolean) SP.getMethod("getBoolean", String.class, boolean.class)
                    .invoke(null, key, def);
        } catch (Exception e) {
            return def;
        }
    }

    public static int getInt(String key, int def) {
        try {
            return (Integer) SP.getMethod("getInt", String.class, int.class).invoke(null, key, def);
        } catch (Exception e) {
            return def;
        }
    }

    public static long getLong(String key, long def) {
        try {
            return (Long) SP.getMethod("getLong", String.class, long.class).invoke(null, key, def);
        } catch (Exception e) {
            return def;
        }
    }

    private static Class<?> getSystemPropertiesClass() {
        try {
            return Class.forName("android.os.SystemProperties");
        } catch (ClassNotFoundException shouldNotHappen) {
            return null;
        }
    }

    private SystemProperties() {
        throw new AssertionError("no instances");
    }

}