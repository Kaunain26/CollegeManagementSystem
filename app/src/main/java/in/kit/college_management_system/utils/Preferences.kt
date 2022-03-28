package `in`.kit.college_management_system.utils

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONException
import org.json.JSONObject

class Preferences(private val context: Context) {
    private var sharedPreferences: SharedPreferences? = null

    fun getInstance(context: Context): Preferences {
        return Preferences(context)
    }

    fun saveUserRole(jsonObject: JSONObject) {
        context.getSharedPreferences("user_role", Context.MODE_PRIVATE)
            .edit().putString("user_role", jsonObject.toString()).apply()
    }

    @get:Throws(JSONException::class)
    val userRole: JSONObject
        get() {
            sharedPreferences = context.getSharedPreferences("user_role", Context.MODE_PRIVATE)
            val userDetails = sharedPreferences?.getString("user_role", null)
            return JSONObject(userDetails!!)
        }

    fun removeUserRoleValue() {
        context.getSharedPreferences("user_role", Context.MODE_PRIVATE).edit().clear().apply()
    }


    /* fun saveSwitchAlarm(deviceId: String?, alarmModel: SwitchAlarmModel?) {}*/
}

