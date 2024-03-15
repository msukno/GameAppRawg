package com.msukno.gameapprawg.data

import android.content.Context

/**
 * Provides methods for loading and writing game parameters to SharedPreferences file
 */
interface AppParamManager {
    suspend fun loadParams(params: Map<String, String>): Map<String, String>
    suspend fun writeParams(params: Map<String, String>)
}

const val PARAM_FILE = "cache_params"

class SharedPrefAppParamManager(val context: Context): AppParamManager{

    //Load parameters as map whose key-value paris correspond to parameter name-value pairs
    override suspend fun loadParams(params: Map<String, String>): Map<String, String> {
        val loadedParams: MutableMap<String, String> = mutableMapOf()
        val sharedPref = context.getSharedPreferences(PARAM_FILE, Context.MODE_PRIVATE)
        params.keys.forEach { key ->
            val defValue = params[key]!!
            val obtainedVal = sharedPref.getString(key, defValue)
            loadedParams[key] =  obtainedVal ?: defValue
        }
        return loadedParams
    }

    override suspend fun writeParams(params: Map<String, String>) {
        val sharedPref = context.getSharedPreferences(PARAM_FILE, Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        params.keys.forEach { key -> editor.putString(key, params[key]) }
        editor.apply()
    }
}