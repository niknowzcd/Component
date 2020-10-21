@file:JvmName("Ext")

package com.architect.component.common.utils

import android.content.res.Resources
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.Spanned
import android.text.TextUtils
import android.text.style.*
import android.util.TypedValue
import java.util.*
import java.util.regex.Pattern
import kotlin.collections.ArrayList

/**
 * Created by Xue on 2020/5/4.
 * 扩展属性以及扩展函数
 */

fun String?.isEmpty(): Boolean {
    return this?.length == 0 || this == null
}

//dp转px
val Float.dp
    get() = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            this,
            Resources.getSystem().displayMetrics
    )

//高亮目标字符串
fun String.highLight(target: String?, color: Int): SpannableString {
    val spannableString = SpannableString(this)
    if (target == null) return spannableString
    val matcher = Pattern.compile(target).matcher(this)
    while (matcher.find()) {
        spannableString.setSpan(ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannableString
}

fun String.highLight(vararg target: String?, color: Int): SpannableString {
    val spannableString = SpannableString(this)
    target.forEach {
        val matcher = Pattern.compile(it).matcher(this)
        while (matcher.find()) {
            spannableString.setSpan(ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return spannableString
}

fun String.highLight(targetList: List<String>, color: Int): SpannableString {
    val spannableString = SpannableString(this)
    targetList.forEach {
        val matcher = Pattern.compile(it).matcher(this)
        while (matcher.find()) {
            spannableString.setSpan(ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return spannableString
}


fun String.highLightAndChangeSize(target: String?, color: Int, size: Int): SpannableString {
    val spannableString = SpannableString(this)
    if (target == null) return spannableString
    val matcher = Pattern.compile(target).matcher(this)
    while (matcher.find()) {
        spannableString.setSpan(ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(AbsoluteSizeSpan(DensityUtils.dp2px(size.toFloat())), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannableString
}

fun SpannableString.changeSize(target: String?, size: Int): SpannableString {
    val spannableString = SpannableString(this)
    if (target == null) return spannableString
    val matcher = Pattern.compile(target).matcher(this)
    while (matcher.find()) {
        spannableString.setSpan(AbsoluteSizeSpan(DensityUtils.dp2px(size.toFloat())), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannableString
}

fun String.changeSize(target: String?, size: Int): SpannableString {
    val spannableString = SpannableString(this)
    if (target == null) return spannableString

    var temp = target
    if (target.startsWith("+")) {
        val builder = StringBuilder()
        builder.append("\\").append(target)
        temp = builder.toString()
    }
    val matcher = Pattern.compile(temp).matcher(this)
    while (matcher.find()) {
        spannableString.setSpan(AbsoluteSizeSpan(DensityUtils.dp2px(size.toFloat())), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannableString
}

fun SpannableString.highLight(target: String?, color: Int): SpannableString {
    val spannableString = SpannableString(this)
    if (target == null) return spannableString

    var temp = target
    if (target.startsWith("+")) {
        val builder = StringBuilder()
        builder.append("\\").append(target)
        temp = builder.toString()
    }

    val matcher = Pattern.compile(temp).matcher(this)
    while (matcher.find()) {
        spannableString.setSpan(ForegroundColorSpan(color), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannableString
}

fun SpannableString.highLight(target: String?, textSize: Float): SpannableString {
    val spannableString = SpannableString(this)
    if (target == null) return spannableString

    var temp = target
    if (target.startsWith("+")) {
        val builder = StringBuilder()
        builder.append("\\").append(target)
        temp = builder.toString()
    }

    val matcher = Pattern.compile(temp).matcher(this)
    while (matcher.find()) {
        spannableString.setSpan(RelativeSizeSpan(textSize), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannableString
}

//字体加粗
fun SpannableString.boldFont(targetList: List<String>): SpannableString {
    val spannableString = SpannableString(this)
    targetList.forEach {
        val matcher = Pattern.compile(it).matcher(this)
        while (matcher.find()) {
            spannableString.setSpan(StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }
    }
    return spannableString
}

//字体加粗
fun SpannableString.boldFont(target: String): SpannableString {
    return boldFont(listOf(target))
}

//字体加粗
fun String.boldFont(target: String): SpannableString {
    val spannableString = SpannableString(this)
    val matcher = Pattern.compile(target).matcher(this)
    while (matcher.find()) {
        spannableString.setSpan(StyleSpan(Typeface.BOLD), matcher.start(), matcher.end(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannableString
}

//给目标字符串增加下划线
fun String.underline(): SpannableString {
    val spannableString = SpannableString(this)
    spannableString.setSpan(UnderlineSpan(), 0, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannableString
}


fun String.underline(target: String?): SpannableString {
    val spannableString = SpannableString(this)
    if (target == null) return spannableString

    val matcher = Pattern.compile(target).matcher(this)
    while (matcher.find()) {
        spannableString.setSpan(UnderlineSpan(), matcher.start(), matcher.end(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    }
    return spannableString
}


//修改目标字符串指定字段的大小
fun String.changeSize(size: Int, start: Int, end: Int): SpannableString {
    val spannableString = SpannableString(this)
    if (end > this.length) return spannableString
    spannableString.setSpan(AbsoluteSizeSpan(DensityUtils.dp2px(size.toFloat())), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannableString
}

//修改字符串最后一个字符的大小
fun String.changeLastCharSize(size: Int): SpannableString {
    val spannableString = SpannableString(this)
    if (this.isEmpty()) return spannableString
    spannableString.setSpan(AbsoluteSizeSpan(DensityUtils.dp2px(size.toFloat())), this.length - 1, this.length, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
    return spannableString
}

fun String.isEmptyMoney(): Boolean {
    return TextUtils.isEmpty(this) || "0" == this || "0元" == this
}

fun String.isNotEmptyMoney(): Boolean {
    return !isEmptyMoney()
}


//将时间戳转化为当前月份的日
fun Long.getDate(): Int {
    return Date(this).date
}
