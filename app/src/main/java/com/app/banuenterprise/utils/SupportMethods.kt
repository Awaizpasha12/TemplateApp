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
                0 -> "Sunday"
                1 -> "Monday"
                2 -> "Tuesday"
                3 -> "Wednesday"
                4 -> "Thursday"
                5 -> "Friday"
                6 -> "Saturday"
                else -> "Unknown"
            }
        }


        // Function to get the current day as an integer (0 for Monday, 6 for Sunday)
        fun getCurrentDay(): Int {
            val calendar = Calendar.getInstance()
            val dayOfWeek = calendar.get(Calendar.DAY_OF_WEEK)

            // Calendar returns Sunday as 1, Monday as 2, ..., Saturday as 7
            // Adjusting to: 0=Sunday, 1=Monday, ..., 6=Saturday
            return when (dayOfWeek) {
                Calendar.SUNDAY -> 0
                Calendar.MONDAY -> 1
                Calendar.TUESDAY -> 2
                Calendar.WEDNESDAY -> 3
                Calendar.THURSDAY -> 4
                Calendar.FRIDAY -> 5
                Calendar.SATURDAY -> 6
                else -> -1 // Should never happen
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
                            pendingAmount = bill.optDouble("pendingAmount", 0.0),
                            netValue = bill.optDouble("netValue", 0.0),
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
            val formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy")
            return currentDate.format(formatter)
        }
    }

}
