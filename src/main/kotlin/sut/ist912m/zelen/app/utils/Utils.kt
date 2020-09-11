package sut.ist912m.zelen.app.utils

import net.minidev.json.JSONObject
import java.lang.reflect.Field

fun generateResponse(vararg fields: Pair<String, Any>): JSONObject {
    val json : JSONObject = JSONObject()
    for ((key, value) in fields) {
        json.appendField(key, value)
    }
    return json
}