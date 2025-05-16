package com.tutorial.kneecast.ui.unit

import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

/**
 * 日付文字列（ISO）を表示用フォーマットに変換
 */
object DateFormatter {
    private val isoDateParser = DateTimeFormatter.ISO_LOCAL_DATE
    private val isoDateTimeParser = DateTimeFormatter.ISO_DATE_TIME
    private val formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    /**
     * ISO形式の日付文字列を表示用フォーマットに変換
     * 時間情報がある場合（ISO_DATE_TIME形式）と
     * 日付のみの場合（ISO_LOCAL_DATE形式）の両方に対応
     */
    fun format(dateString: String): String {
        return try {
            // まずISO_DATE_TIME形式でパースを試みる
            val dateTime = LocalDateTime.parse(dateString, isoDateTimeParser)
            dateTime.toLocalDate().format(formatter)
        } catch (e: DateTimeParseException) {
            try {
                // 次にISO_LOCAL_DATE形式でパースを試みる
                val date = LocalDate.parse(dateString, isoDateParser)
                date.format(formatter)
            } catch (e: DateTimeParseException) {
                // どちらの形式でもパースできない場合は元の文字列を返す
                dateString
            }
        }
    }
}