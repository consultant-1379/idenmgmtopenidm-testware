package com.ericsson.nms.security.taf.test.utils;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JsonRetriever {

    private String jsonString;
    private String[] nestedKeys;

    public JsonRetriever() {
    }

    public JsonRetriever(String jsonString, String[] keys) {
        this.jsonString = jsonString;
        nestedKeys = keys;
    }

    public void setJsonString(String str) {
        this.jsonString = str;
    }

    public void setNestedKeys(String[] keys) {
        this.nestedKeys = keys;
    }

    public String getValue(String lastKey) {
        String value = null;
        JSONParser parser = new JSONParser();
        try {
            Object obj = parser.parse(jsonString);
            JSONObject jsonObj = (JSONObject) obj;
            if (nestedKeys != null) {
                for (String key : nestedKeys) {
                    System.out.println("key " + key);
                    jsonObj = (JSONObject) jsonObj.get(key);
                }
            }
            value = (String) jsonObj.get(lastKey);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return value;
    }
}
