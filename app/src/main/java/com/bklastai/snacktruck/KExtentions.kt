package com.bklastai.snacktruck

import android.content.Context
import android.content.res.Resources
import android.widget.Toast

fun Context.safeGetString(intKey: Int): String {
    return try { resources.getString(intKey) } catch (err: Resources.NotFoundException) { "" }
}

fun Context.toast(intText: Int) { Toast.makeText(this, safeGetString(intText), Toast.LENGTH_SHORT).show() }
fun Context.toast(text: String) { Toast.makeText(this, text, Toast.LENGTH_SHORT).show() }
