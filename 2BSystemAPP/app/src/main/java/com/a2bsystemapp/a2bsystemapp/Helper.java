package com.a2bsystemapp.a2bsystemapp;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Helper {
    //Retourne la premiere colonne
    public static JSONObject GetFirstRow(byte[] source) {
        return GetRowAt(source, 0);
    }

    public static JSONArray GetList(byte[] source) {
        String s = new String(source);
        try {
            return new JSONObject(s).getJSONArray("recordsets").getJSONArray(0);
        } catch (JSONException e) {
            return null;
        }
    }


    // Retourne la colonne x
    public static JSONObject GetRowAt(byte[] source, int index) {
        try {
            return GetList(source).getJSONObject(index);
        } catch (JSONException e) {
            return null;
        }
    }
}
