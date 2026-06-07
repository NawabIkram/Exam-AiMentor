package com.cssaimentor.app.utils

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimeUtils {
    fun chatTime(timestamp: Long): String =
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
}

