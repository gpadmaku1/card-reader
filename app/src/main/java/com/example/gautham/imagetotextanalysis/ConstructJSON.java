package com.example.gautham.imagetotextanalysis;

import android.os.AsyncTask;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Gautham on 7/9/2017.
 */

public class ConstructJSON extends AsyncTask<Void, Void, JSONObject> {

    private String base64EncodedString;

    public ConstructJSON(String base64EncodedString) {
        this.base64EncodedString = base64EncodedString;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }


    @Override
    protected void onPostExecute(JSONObject jsonObject) {
        super.onPostExecute(jsonObject);
    }

    
    @Override
    protected JSONObject doInBackground(Void... params) {

            //ImageObject and FeaturesArrayObject go inside InnerJSONObject
            //ImageObject
            JSONObject imageObject = new JSONObject();
            try {
                imageObject.put("content", base64EncodedString);
            } catch (JSONException e){
                e.printStackTrace();
            }

            JSONObject type = new JSONObject();
            try{
                type.put("type","DOCUMENT_TEXT_DETECTION");
            }catch (JSONException e){
                e.printStackTrace();
            }

            //FeaturesArrayObject
            JSONArray featuresArray = new JSONArray();
            featuresArray.put(type);

            //InnerJSONObject
            JSONObject innerJSONObject = new JSONObject();
            try {
                innerJSONObject.put("image", imageObject);
                innerJSONObject.put("features", featuresArray);
            } catch (JSONException e){
                e.printStackTrace();
            }

            //InnerJSONObject goes inside RequestsArray
            JSONArray requestsArray = new JSONArray();
            requestsArray.put(innerJSONObject);

            //RequestsArray goes inside MainObject
            JSONObject mainObject = new JSONObject();
            try {
                mainObject.put("requests", requestsArray);
            } catch(JSONException e){
                e.printStackTrace();
            }

            return mainObject;
        }
    }

