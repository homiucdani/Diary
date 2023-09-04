package com.example.diary.util

import androidx.compose.runtime.Composable
import io.realm.kotlin.types.RealmInstant
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun RealmInstant.toInstant(): Instant {
    val sec: Long = this.epochSeconds
    val nano: Int = this.nanosecondsOfSecond

    return if (sec >= 0) {
        Instant.ofEpochSecond(sec, nano.toLong())
    } else {
        Instant.ofEpochSecond(sec - 1, 1_000_000 + nano.toLong())
    }
}

fun Instant.toStringTime(): String {
    val localTime = LocalDateTime.ofInstant(this, ZoneId.systemDefault())
    val pattern = "hh:mm a"
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localTime.format(formatter)
}

@Composable
fun Instant.formatDateTimeToPattern(pattern: String): String {
    // if the time is not formatted correct then remove "remember"
    val localDateTime = LocalDateTime.ofInstant(this, ZoneId.systemDefault())
    val formatter = DateTimeFormatter.ofPattern(pattern)
    return localDateTime.format(formatter)
}

fun Instant.toRealmInstant(): RealmInstant {
    val sec: Long = this.epochSecond
    val nano: Int = this.nano

    return if (sec >= 0) {
        RealmInstant.from(sec, nano)
    } else {
        RealmInstant.from(sec + 1, -1_000_000 + nano)
    }
}

fun Long.toInstant(): Instant {
    return Instant.ofEpochMilli(this).apply {

    }
}

