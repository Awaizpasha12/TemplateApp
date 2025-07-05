package com.app.banuenterprise.utils

import com.app.banuenterprise.data.model.response.BillItem
import org.json.JSONObject
import java.time.LocalDate
import java.time.format.DateTimeFormatter
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
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            // Calendar returns Sunday as 1, Monday as 2, ..., Saturday as 7
            // Adjusting to match the format you want (0 for Monday, 6 for Sunday)
            return when (dayOfWeek) {
                Calendar.SUNDAY -> 6
                Calendar.MONDAY -> 0
                Calendar.TUESDAY -> 1
                Calendar.WEDNESDAY -> 2
                Calendar.THURSDAY -> 3
                Calendar.FRIDAY -> 4
                Calendar.SATURDAY -> 5
                else -> -1 // Default for invalid day (should never happen)
            }
        }
        fun convertToBillMap(response: JSONObject): HashMap<String, JSONObject> {
            val billMap = HashMap<String, JSONObject>()
            val customersArray = response.getJSONArray("customers")

            for (i in 0 until customersArray.length()) {
                val customerObj = customersArray.getJSONObject(i)
                val customerName = customerObj.getString("name")
                val billItems = customerObj.getJSONArray("billItems")

                for (j in 0 until billItems.length()) {
                    val bill = billItems.getJSONObject(j)
                    val billNumber = bill.getString("billNumber")
                    val brand = bill.getString("brand")
                    val pendingAmount = bill.getDouble("pendingAmount")
                    val billItemId = bill.getString("_id")
                    val key = "$billNumber-$brand"

                    val value = JSONObject().apply {
                        put("customerName", customerName)
                        put("brand", brand)
                        put("amount", pendingAmount)
                        put("billNumber", billNumber)
                        put("billItemId",billItemId)
                    }

                    billMap[key] = value
                }
            }

            return billMap
        }
        fun convertToBillMapNew(response: JSONObject): HashMap<String, JSONObject> {
            val billMap = HashMap<String, JSONObject>()
            val customersArray = response.getJSONArray("customers")

            for (i in 0 until customersArray.length()) {
                val customerObj = customersArray.getJSONObject(i)
                val customerName = customerObj.getString("name")
                val billItems = customerObj.getJSONArray("billItems")

                for (j in 0 until billItems.length()) {
                    val bill = billItems.getJSONObject(j)
                    val billNumber = bill.getString("billNumber")
                    val brand = bill.getString("brand")
                    val pendingAmount = bill.getDouble("pendingAmount")

                    val key = "$billNumber"

                    val value = JSONObject().apply {
                        put("customerName", customerName)
                        put("brand", brand)
                        put("amount", pendingAmount)
                        put("billNumber", billNumber)
                    }

                    billMap[key] = value
                }
            }

            return billMap
        }

        fun extractBillItems(response: JSONObject): List<BillItem> {
            val billItems = mutableListOf<BillItem>()
            val customersArray = response.getJSONArray("customers")

            for (i in 0 until customersArray.length()) {
                val customerObj = customersArray.getJSONObject(i)
                val customerName = customerObj.getString("name")
                val customerBillItems = customerObj.getJSONArray("billItems")

                for (j in 0 until customerBillItems.length()) {
                    val bill = customerBillItems.getJSONObject(j)

                    try {
                        val item = BillItem(
                            billNumber = bill.optString("billNumber", ""),
                            billDate = bill.optString("billDate", ""),
                            route = bill.optString("route", ""),
                            customerName = customerName,
                            pendingAmount = bill.optInt("pendingAmount", 0),
                            netValue = bill.optInt("netValue", 0),
                            brand = bill.optString("brand", ""),
                            creditDays = bill.optInt("creditDays", 0),
                            _id = bill.optString("_id", "")
                        )
                        billItems.add(item)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            return billItems
        }

        fun getCurrentDateFormatted(): String {
            val currentDate = LocalDate.now()
            val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd")
            return currentDate.format(formatter)
        }
    }

}
