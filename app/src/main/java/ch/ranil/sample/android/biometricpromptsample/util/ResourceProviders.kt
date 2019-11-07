package ch.ranil.sample.android.biometricpromptsample.util

import android.content.Context
import androidx.annotation.StringRes
import javax.inject.Inject
import javax.inject.Singleton

interface ResourceProvider<T> {

    fun provide(resId: Int): T

    fun provide(resId: Int, vararg formatArgs: Any): T

}

@Singleton
class StringResourceProvider @Inject constructor(private val context: Context) :
    ResourceProvider<String> {

    override fun provide(@StringRes resId: Int) = context.getString(resId)

    override fun provide(@StringRes resId: Int, vararg formatArgs: Any) =
        context.getString(resId, *formatArgs)

}