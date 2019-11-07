package ch.ranil.sample.android.biometricpromptsample.util

import android.content.SharedPreferences
import android.util.Base64

fun SharedPreferences.getBase64DecodedString(key: String): ByteArray? {
    return getString(key, null)?.let { Base64.decode(it, Base64.DEFAULT) }
}

fun SharedPreferences.Editor.putBase64EncodedString(key: String, data: ByteArray) {
    putString(key, Base64.encodeToString(data, Base64.DEFAULT))
}
