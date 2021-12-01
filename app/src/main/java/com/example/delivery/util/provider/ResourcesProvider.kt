package com.example.delivery.util.provider

import android.content.res.ColorStateList
import androidx.annotation.ColorRes
import androidx.annotation.StringRes

interface ResourcesProvider {

    /*
    @stringres, @ColorRes
    리소스 매개변수에 R.string 참조가 포함 되어 있는지 검사할 수 있다.
    */
    fun getString(@StringRes resId: Int): String

    fun getString(@StringRes resId: Int, vararg formatArgs: Any): String

    fun getColorStateList(@ColorRes resId: Int): ColorStateList
}
