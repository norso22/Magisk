package com.topjohnwu.magisk.model.entity

import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import com.topjohnwu.magisk.model.entity.MagiskPolicy.Companion.INTERACTIVE


data class MagiskPolicy(
    val uid: Int,
    val packageName: String,
    val appName: String,
    val policy: Int = INTERACTIVE,
    val until: Long = -1L,
    val logging: Boolean = true,
    val notification: Boolean = true,
    val applicationInfo: ApplicationInfo
) {

    companion object {
        const val INTERACTIVE = 0
        const val DENY = 1
        const val ALLOW = 2
    }

}

/*@Throws(PackageManager.NameNotFoundException::class)
fun ContentValues.toPolicy(pm: PackageManager): MagiskPolicy {
    val uid = getAsInteger("uid")
    val packageName = getAsString("package_name")
    val info = pm.getApplicationInfo(packageName, 0)
    if (info.uid != uid)
        throw PackageManager.NameNotFoundException()
    return MagiskPolicy(
        uid = uid,
        packageName = packageName,
        policy = getAsInteger("policy"),
        until = getAsInteger("until").toLong(),
        logging = getAsInteger("logging") != 0,
        notification = getAsInteger("notification") != 0,
        applicationInfo = info,
        appName = info.loadLabel(pm).toString()
    )
}
fun MagiskPolicy.toContentValues() = ContentValues().apply {
    put("uid", uid)
    put("uid", uid)
    put("package_name", packageName)
    put("policy", policy)
    put("until", until)
    put("logging", if (logging) 1 else 0)
    put("notification", if (notification) 1 else 0)
}*/

fun MagiskPolicy.toMap() = mapOf(
    "uid" to uid,
    "package_name" to packageName,
    "policy" to policy,
    "until" to until,
    "logging" to if (logging) 1 else 0,
    "notification" to if (notification) 1 else 0
).mapValues { it.value.toString() }

@Throws(PackageManager.NameNotFoundException::class)
fun Map<String, String>.toPolicy(pm: PackageManager): MagiskPolicy {
    val uid = get("uid")?.toIntOrNull() ?: -1
    val packageName = get("package_name").orEmpty()
    val info = pm.getApplicationInfo(packageName, 0)

    if (info.uid != uid)
        throw PackageManager.NameNotFoundException()

    return MagiskPolicy(
        uid = uid,
        packageName = packageName,
        policy = get("policy")?.toIntOrNull() ?: INTERACTIVE,
        until = get("until")?.toLongOrNull() ?: -1L,
        logging = get("logging")?.toIntOrNull() != 0,
        notification = get("notification")?.toIntOrNull() != 0,
        applicationInfo = info,
        appName = info.loadLabel(pm).toString()
    )
}

@Throws(PackageManager.NameNotFoundException::class)
fun Int.toPolicy(pm: PackageManager): MagiskPolicy {
    val pkg = pm.getPackagesForUid(this)?.firstOrNull()
        ?: throw PackageManager.NameNotFoundException()
    val info = pm.getApplicationInfo(pkg, 0)
    return MagiskPolicy(
        uid = this,
        packageName = pkg,
        applicationInfo = info,
        appName = info.loadLabel(pm).toString()
    )
}