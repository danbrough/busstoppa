package danbroid.busapp.utils

import java.net.URLDecoder
import java.net.URLEncoder

fun CharSequence.uriEncode(): String = URLEncoder.encode(toString(), "UTF-8")
fun CharSequence.uriDecode(): String = URLDecoder.decode(toString(), "UTF-8")

fun String.addUrlArgs(vararg keywords: Pair<String, Any?>): String = StringBuilder(this).apply {
  var firstArg = true
  keywords.forEach { arg ->
    if (arg.second == null) return@forEach
    append(if (firstArg) '?' else '&')
    firstArg = false
    append("${arg.first}=${arg.second!!.toString().uriEncode()}")
  }
}.toString()