package com.tutorial.kneecast.ui.util

import java.time.LocalDate
import java.time.format.DateTimeFormatter

/**
 * 日付文字列（ISO）を表示用フォーマットに変換
 */
object DateFormatter {
    private val parser = DateTimeFormatter.ISO_LOCAL_DATE
    private val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    fun format(dateString: String): String =
        LocalDate.parse(dateString, parser)
            .format(formatter)
}