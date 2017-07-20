package com.gpads.gautham.imagetotextanalysis.Tools;


import android.content.Context;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.gpads.gautham.imagetotextanalysis.BuildConfig;

import org.json.JSONException;
import org.json.JSONObject;

public class VolleyNetworking {


    private ProgressBar progressBar;
    private EditText obtainedText;
    private Context context;
    private static final String CLOUD_VISION_API_KEY =  BuildConfig.API_KEY;

    private String googleVisionResult;


    //Default constructor
    public VolleyNetworking(Context context, ProgressBar progressBar, EditText obtainedText){
        this.context = context;
        this.progressBar = progressBar;
        this.obtainedText = obtainedText;
    }


    /**
     * Volley class to make HTTP Post Requests to Google Cloud Vision API
     *
     * @param object, The POST request JSON body
     */
    public void callGoogleVisionAPI(JSONObject object) {
        progressBar.setVisibility(View.VISIBLE);
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        String url = "https://vision.googleapis.com/v1/images:annotate?key=" + CLOUD_VISION_API_KEY;
        Log.w("YEE", url);
        JsonObjectRequest postRequest = new JsonObjectRequest(url, object,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // response
                        progressBar.setVisibility(View.GONE);
                        if(response!= null) {
                            String bCardText = getRelevantString(response);
                            try {
                                bCardText = bCardText.replace("\n", " ");
                                googleVisionResult = bCardText;
                                Log.w("YEE", "volley:" + googleVisionResult);
                                obtainedText.setText(bCardText);
                                Log.d("Response", bCardText);
                            }catch (NullPointerException e){
                                e.printStackTrace();
                            }
                        } else{
                            Log.w("YEE", "response is null");
                        }
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error
                        progressBar.setVisibility(View.GONE);
                        Toast.makeText(context, "Network error. Processing failed", Toast.LENGTH_LONG).show();
                        Log.w("Error.Response", error.toString());
                    }
                }
        );
        requestQueue.add(postRequest);
    }



    /**
     * Gets the text from the returned JSONObject
     *
     * @param response, The JSONObject response send by the API call
     * @return The relevant String to extract the account number from
     */
    private String getRelevantString(JSONObject response) {
        String finalString = null;
        try {
            finalString = response.getJSONArray("responses").getJSONObject(0)
                    .getJSONArray("textAnnotations").getJSONObject(0).get("description").toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return finalString;
    }


}
