package com.app.banuenterprise.utils

import java.util.Calendar

class SupportMethods {
    companion object {
        // Static method to convert integer to day of the week
        fun getDayFromInt(dayInt: Int): String {
            return when (dayInt) {
                0 -> "Monday"
                1 -> "Tuesday"
                2 -> "Wednesday"
                3 -> "Thursday"
                4 -> "Friday"
                5 -> "Saturday"
                6 -> "Sunday"
                else -> "Unknown"  // In case an invalid integer is passed
            }
        }

        // Function to get the current day as an integer (0 for Monday, 6 for Sunday)
        fun getCurrentDay(): Int {
            return 5;
//            val calendar = Calendar.getInstance()
//            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)
//
//            // Calendar returns Sunday as 1, Monday as 2, ..., Saturday as 7
//            // Adjusting to match the format you want (0 for Monday, 6 for Sunday)
//            return when (dayOfWeek) {
//                Calendar.SUNDAY -> 6
//                Calendar.MONDAY -> 0
//                Calendar.TUESDAY -> 1
//                Calendar.WEDNESDAY -> 2
//                Calendar.THURSDAY -> 3
//                Calendar.FRIDAY -> 4
//                Calendar.SATURDAY -> 5
//                else -> -1 // Default for invalid day (should never happen)
//            }
        }
    }

}
