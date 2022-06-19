package `in`.kit.college_management_system.utils

import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.*

class CalendarHelperClass {

    @SuppressLint("SimpleDateFormat")
    fun getFormattedDate(date: Date, format: String): String {
        val simpleDateFormat = SimpleDateFormat(format)
        return simpleDateFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun getCurrentTimeAndDate(): String {
        val ci = Calendar.getInstance()
        val date: Date = ci.time
        val simpleDateFormat = SimpleDateFormat("MMM dd yyyy")
        return simpleDateFormat.format(date)
    }

    @SuppressLint("SimpleDateFormat")
    fun getDateOfTheMonth(date: String, format: String): Int {
        val parsedDate = SimpleDateFormat(format).parse(date)
        val simpleDateFormat = SimpleDateFormat("dd")
        val onlyFormattedDate: String = simpleDateFormat.format(parsedDate!!)
        return if (onlyFormattedDate[0] == '0') {
            onlyFormattedDate[1].toString().toInt()
        } else {
            onlyFormattedDate.toInt()
        }
    }

    @SuppressLint("SimpleDateFormat")
    fun getCalendarInstanceFromDate(date: String, parsingDateFormat: String): Calendar {
        val parsedDate = SimpleDateFormat(parsingDateFormat).parse(date)
        //val simpleDateFormat = SimpleDateFormat("MMMM")
        val instance = Calendar.getInstance()
        instance.time = parsedDate!!
        return instance
    }

    @SuppressLint("SimpleDateFormat")
    fun getTimeFromDate(): String {
        val ci = Calendar.getInstance()
        val date: Date = ci.time
        val simpleDateFormat = SimpleDateFormat("hh:mm:ss a")
        return simpleDateFormat.format(date)
    }
}