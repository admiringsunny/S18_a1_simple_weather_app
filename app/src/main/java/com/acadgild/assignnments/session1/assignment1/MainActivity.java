package com.acadgild.assignnments.session1.assignment1;

import android.app.ListActivity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class MainActivity extends ListActivity {

    private ProgressDialog pDialog;

    // URL to get open weather
    private static String url = "http://api.openweathermap.org/data/2.5/weather?q=London,uk&appid=d7b900681c37193223281142bd919019";

    // JSON Node names
    private static final String TAG_RESULTS = "weather";
    private static final String TAG_ID = "id";
    private static final String TAG_DESC = "main";

    // result JSONArray
    JSONArray result = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> weatherDesc;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        weatherDesc = new ArrayList<>();

        ListView lv = getListView();  //inbuilt - list id

        // Calling async task to get json
        new GetContacts().execute();
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetContacts extends AsyncTask<Void, Void, Void> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(MainActivity.this);
            pDialog.setMessage("Please wait ...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);

            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    result = jsonObj.getJSONArray(TAG_RESULTS);


                    // getting JSON Obj
                    JSONObject c = result.getJSONObject(0);
                    String id = c.getString(TAG_ID);
                    String desc = c.getString(TAG_DESC);


                    // tmp hashmap for weather result id-desc pair
                    HashMap<String, String> weather = new HashMap<>();

                    // adding each child node to HashMap key => value
                    weather.put(TAG_ID, id);
                    weather.put(TAG_DESC, desc);

                    // adding weather (id-desc pair) to list
                    weatherDesc.add(weather);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                Log.e("ServiceHandler", "Couldn't get any data from the url");
            }
            return null;
        }
        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            // Dismiss the progress dialog
            if (pDialog != null && pDialog.isShowing())
                pDialog.dismiss();

            ListAdapter adapter = new SimpleAdapter(MainActivity.this, weatherDesc, R.layout.list_item, new String[] { TAG_DESC,}, new int[] {R.id.value });

            setListAdapter(adapter);
        }

    }

}