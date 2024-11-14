package com.arzha.safe_environment.realdevice

class SystemProperties private constructor() {
    companion object {
        private val SP: Class<*>? = getSystemPropertiesClass()

        fun get(key: String): String? {
            return try {
                SP?.getMethod("get", String::class.java)?.invoke(null, key) as String?
            } catch (e: Exception) {
                null
            }
        }

        fun get(key: String, def: String): String {
            return try {
                SP?.getMethod("get", String::class.java, String::class.java)?.invoke(null, key, def) as String
            } catch (e: Exception) {
                def
            }
        }

        fun getBoolean(key: String, def: Boolean): Boolean {
            return try {
                SP?.getMethod("getBoolean", String::class.java, Boolean::class.javaPrimitiveType)?.invoke(null, key, def) as Boolean
            } catch (e: Exception) {
                def
            }
        }

        fun getInt(key: String, def: Int): Int {
            return try {
                SP?.getMethod("getInt", String::class.java, Int::class.javaPrimitiveType)?.invoke(null, key, def) as Int
            } catch (e: Exception) {
                def
            }
        }

        fun getLong(key: String, def: Long): Long {
            return try {
                SP?.getMethod("getLong", String::class.java, Long::class.javaPrimitiveType)?.invoke(null, key, def) as Long
            } catch (e: Exception) {
                def
            }
        }

        private fun getSystemPropertiesClass(): Class<*>? {
            return try {
                Class.forName("android.os.SystemProperties")
            } catch (shouldNotHappen: ClassNotFoundException) {
                null
            }
        }
    }
}
