package com.kawailab.locationtestth.database
import androidx.room.TypeConverter
import java.util.Calendar
class Converters {
    @TypeConverter fun calendarToDatesstamp(calendar: Calendar):Long = calendar.timeInMillis
    @TypeConverter fun datestampToCalendar(value:Long): Calendar = Calendar.getInstance().apply { timeInMillis = value }
}