package com.bklastai.snacktruck

import android.content.Context
import android.content.SharedPreferences
import android.content.res.Resources
import android.widget.Toast

const val MY_SHARED_PREFS = "com.bklastai.snacktruck.MY_SHARED_PREFS"

fun Context.getPrefs(): SharedPreferences { return getSharedPreferences(MY_SHARED_PREFS, Context.MODE_PRIVATE) }
fun Context.getPrefsEditor(): SharedPreferences.Editor { return getPrefs().edit() }

fun Context.safeGetString(intKey: Int): String {
    return try { resources.getString(intKey) } catch (err: Resources.NotFoundException) { "" }
}

fun Context.toast(intText: Int) { Toast.makeText(this, safeGetString(intText), Toast.LENGTH_SHORT).show() }
fun Context.toast(text: String) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show() }

// get and put String
fun Context.getStringPref(intKey: Int): String? { return getStringPref(safeGetString(intKey)) }
fun Context.getStringPref(strKey: String): String? { return getPrefs().getString(strKey, null) }
fun Context.putStringPref(intKey: Int, value: String) { putStringPref(safeGetString(intKey), value) }
fun Context.putStringPref(strKey: String, value: String) { getPrefsEditor().putString(strKey, value).commit() }

// get and put Float
fun Context.getFloatPref(intKey: Int): Float? { return getFloatPref(safeGetString(intKey)) }
fun Context.getFloatPref(strKey: String): Float? {
    return if (getPrefs().contains(strKey)) getPrefs().getFloat(strKey, 0f) else null }
fun Context.putFloatPref(intKey: Int, value: Float) { putFloatPref(safeGetString(intKey), value) }
fun Context.putFloatPref(strKey: String, value: Float) { getPrefsEditor().putFloat(strKey, value).commit() }

// get and put Int
fun Context.getIntPref(intKey: Int): Int? { return getIntPref(safeGetString(intKey)) }
fun Context.getIntPref(strKey: String): Int? {
    return if (getPrefs().contains(strKey)) getPrefs().getInt(strKey, 0) else null }
fun Context.putIntPref(intKey: Int, value: Int) { putIntPref(safeGetString(intKey), value) }
fun Context.putIntPref(strKey: String, value: Int) { getPrefsEditor().putInt(strKey, value).commit() }

// get and put Boolean
fun Context.getBooleanPref(intKey: Int): Boolean? { return getBooleanPref(safeGetString(intKey)) }
fun Context.getBooleanPref(strKey: String): Boolean? {
    return if (getPrefs().contains(strKey)) getPrefs().getBoolean(strKey, false) else null }
fun Context.putBooleanPref(intKey: Int, value: Boolean) { putBooleanPref(safeGetString(intKey), value) }
fun Context.putBooleanPref(strKey: String, value: Boolean) { getPrefsEditor().putBoolean(strKey, value).commit() }

// get and put Long
fun Context.getLongPref(intKey: Int): Long? { return getLongPref(safeGetString(intKey)) }
fun Context.getLongPref(strKey: String): Long? {
    return if (getPrefs().contains(strKey)) getPrefs().getLong(strKey, 0) else null }
fun Context.putLongPref(intKey: Int, value: Long) { putLongPref(safeGetString(intKey), value) }
fun Context.putLongPref(strKey: String, value: Long) { getPrefsEditor().putLong(strKey, value).commit() }

// get and put StringSet
fun Context.getStringSetPref(intKey: Int): MutableSet<String?>? { return getStringSetPref(safeGetString(intKey)) }
fun Context.getStringSetPref(strKey: String): MutableSet<String?>? { return getPrefs().getStringSet(strKey, null) }
fun Context.putStringSetPref(intKey: Int, value: Set<String?>?) { putStringSetPref(safeGetString(intKey), value) }
fun Context.putStringSetPref(strKey: String, value: Set<String?>?) { getPrefsEditor().putStringSet(strKey, value).commit() }