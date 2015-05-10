package org.rechargeplanrecommender.com.rechargeplanrecommender;


import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;

public class Suggest extends ListActivity implements  AdapterView.OnItemClickListener {

    private ProgressDialog pDialog;
    ListAdapter adapter;

    // URL to get DATAS JSON
    private static String url = "http://192.168.137.132:8888/want_plans";

    // JSON Node names
    public final static String EXTRA_VALUE = "org.rechargeplanrecommender.com.rechargeplanrecommender.VALUE";
    String str="Anisha";
    private static final String TAG_DATAS = "data";
    private static final String TAG_ID = "id";
    private static final String TAG_RECHARGE_VALUE = "recharge_value";
    private static final String TAG_RECHARGE_TALKTIME = "recharge_talktime";
    private static final String TAG_RECHARGE_VALIDITY = "recharge_validity";
    private static final String TAG_RECHARGE_SHORT_DESCRIPTION = "recharge_short_description";
    private static final String TAG_RECHARGE_DESCRIPTION = "recharge_description";
    private static final String TAG_RECHARGE_DESCRIPTION_MORE = "recharge_description_more";
    private static final String TAG_PRODUCT_TYPE = "product_type";
    private static final String TAG_CIRCLE_MASTER = "circle_master";
    private static final String TAG_OPERATOR_MASTER = "operator_master";
    private static final String TAG_RECHARGE_MASTER = "recharge_master";
    private static final String TAG_IS_PREPAID = "is_prepaid";
    private static final String ESTIMATED_COST = "estimated_cost";
    private static final String VALUE1 = "value";
    //private static  int length=10;

    // contacts JSONArray
    JSONArray datas = null;

    // Hashmap for ListView
    ArrayList<HashMap<String, String>> dataList;
    ListView lv;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.suggest_plans);
        //Intent intent = getIntent();
        dataList = new ArrayList<HashMap<String, String>>();

        lv= getListView();
        Bundle b=getIntent().getExtras();
        str  = b.getString("str");
        Log.e("Suggest",str);
        new GetDatas().execute();
        lv.setOnItemClickListener(this);
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(Suggest.this, PlanDescription.class);
        HashMap<String, String> data = (HashMap<String, String>)adapter.getItem(position);
        String value=data.get(TAG_RECHARGE_VALUE);
        intent.putExtra("value", value);
        String operator=data.get(TAG_OPERATOR_MASTER);
        intent.putExtra("operator",operator );
        String validity=data.get(TAG_RECHARGE_VALIDITY);
        intent.putExtra("validity",validity );
        String estimated=data.get(ESTIMATED_COST);
        intent.putExtra("estimated_cost",estimated);
        String description=data.get(TAG_RECHARGE_DESCRIPTION);

        intent.putExtra("description",description);

        startActivity(intent);
    }

    class MapComparator implements Comparator<HashMap<String, String>>
    {
        private final String key;

        public MapComparator(String key)
        {
            this.key = key;
        }

        public int compare(HashMap<String, String> first,
                           HashMap<String, String> second)
        {
            // TODO: Null checking, both for maps and values

            String firstValue = first.get(key);
            String secondValue = second.get(key);
//            if (firstValue == null ^ secondValue == null) {
//                return (firstValue == null) ? -1 : 1;
//            }
//
//            if (firstValue == null && secondValue == null) {
//                return 0;
//            }

            if(secondValue==null)return -1;
//            if(firstValue == null)
//                if(secondValue == null)
//                    return 0; //equal
//                else
//                    return -1; // null is before other strings
//            else // this.member != null
//                if(secondValue == null)
//                    return 1;  // all other strings are after null
//                else
            return firstValue.compareTo(secondValue);

        }
    }

    /**
     * Async task class to get json by making HTTP call
     * */
    private class GetDatas extends AsyncTask<Void, Void, Void> {
        private Suggest suggest;
        public GetDatas(Suggest suggest) {
            this.suggest=suggest;
        }
        public GetDatas() {

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // Showing progress dialog
            pDialog = new ProgressDialog(Suggest.this);
            pDialog.setMessage("Please wait...");
            pDialog.setCancelable(false);
            pDialog.show();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            // Creating service handler class instance
            ServiceHandler sh = new ServiceHandler();

            // Making a request to url and getting response
            //String jsonStr = sh.makeServiceCall(url, ServiceHandler.GET);
            //  String jsonStr = "{\"data\":[{\"id\":\"12954868\",\"recharge_value\":\"5\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"1 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" National Roaming Voice Calls O G LOC or STD Calls 1.5 ps per Sec\",\"recharge_description_more\":\" FREE INCOMING CALLS: ROAM_5\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954872\",\"recharge_value\":\"21\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"30 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" Free call Forwarding to any BSNL Landline WLL number within LSA\",\"recharge_description_more\":\" VOICE_21\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954880\",\"recharge_value\":\"44\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"30 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" Local Any Net Call 1.4ps per 2 Sec\",\"recharge_description_more\":\" VOICE_44\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954882\",\"recharge_value\":\"47\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"28 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" 20ps min Loc on net\",\"recharge_description_more\":\" STV_47\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954884\",\"recharge_value\":\"65\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"28 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" 12ps per Min Local On net\",\"recharge_description_more\":\" VOICE_65\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954886\",\"recharge_value\":\"69\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"30 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" National Roaming Voice Calls O G LOC or STD Calls 1.5 ps per Sec\",\"recharge_description_more\":\" FREE INCOMING CALLS: ROAM_69\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954888\",\"recharge_value\":\"84\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"84 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" STD voice calls at Rs 0.35 PER Min with 84 days\",\"recharge_description_more\":\" STD_VOICE_84\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954890\",\"recharge_value\":\"91\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"90 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" National Roaming Voice Calls O G LOC or STD Calls 1.5 ps per Sec\",\"recharge_description_more\":\" FREE INCOMING CALLS: RoamSTV_91\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954892\",\"recharge_value\":\"132\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"90 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" Local Any Net Call 1.4ps per 2 Sec\",\"recharge_description_more\":\" VOICE_132\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954898\",\"recharge_value\":\"164\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"6 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" Unlimited Local or STD On Net\",\"recharge_description_more\":\" VOICE_164\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954900\",\"recharge_value\":\"344\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"27 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" Local call On Net Unlimited free\",\"recharge_description_more\":\" VOICE_344\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"12954910\",\"recharge_value\":\"894\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"81 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" Local call On Net Unlimited free\",\"recharge_description_more\":\" VOICE_894\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"},{\"id\":\"14552797\",\"recharge_value\":\"18\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"30 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" All Voice calls to Nepal Rs.8.50 per Minute with validity of 30 Days\",\"recharge_description_more\":\" ISD_18\",\"product_type\":\"Mobile\",\"circle_master\":\"Karnataka\",\"operator_master\":\"Bsnl\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"}]}";
            String jsonStr=str;
            // jsonStr = jsonStr.substring(1, jsonStr.length()-1);
            Log.d("Response: ", "> " + jsonStr);

            if (jsonStr != null) {
                try {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    datas = jsonObj.getJSONArray(TAG_DATAS);

                    // looping through All datas
                    for (int i = 0; i < datas.length(); i++) {
                        JSONObject calldata = datas.getJSONObject(i);

                        // Phone node is JSON Object
                        //JSONObject calldata = c.getJSONObject(CALL_DETAIL);
                        String id = calldata.getString(TAG_ID);
                        String recharge_value = calldata.getString(TAG_RECHARGE_VALUE);
                        String recharge_talktime = calldata.getString(TAG_RECHARGE_TALKTIME);
                        String recharge_validity = calldata.getString(TAG_RECHARGE_VALIDITY);
                        String recharge_short_description = calldata.getString(TAG_RECHARGE_SHORT_DESCRIPTION);
                        String recharge_description = calldata.getString(TAG_RECHARGE_DESCRIPTION);
                        String recharge_description_more = calldata.getString(TAG_RECHARGE_DESCRIPTION_MORE);
                        String product_type = calldata.getString(TAG_PRODUCT_TYPE);
                        String circle_master = calldata.getString(TAG_CIRCLE_MASTER);
                        String operator_master = calldata.getString(TAG_OPERATOR_MASTER);
                        String recharge_master = calldata.getString(TAG_RECHARGE_MASTER);
                        String is_prepaid = calldata.getString(TAG_IS_PREPAID);
                        String estimated_cost=calldata.getString(ESTIMATED_COST);
                        int a=(Integer.parseInt(estimated_cost))/(Integer.parseInt(recharge_validity));
                        String value1=String.valueOf(a);
                        // Log.e("a--------------------",String.valueOf(a));
                        Log.e("value1--------------",value1);
                        Log.e("RV--------------",recharge_validity);  // tmp hashmap for single data
                        HashMap<String, String> data = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        data.put(TAG_ID, id);
                        data.put(TAG_OPERATOR_MASTER,operator_master);
                        data.put(TAG_RECHARGE_VALUE, recharge_value);
                        data.put(TAG_RECHARGE_TALKTIME, recharge_talktime);
                        data.put(TAG_RECHARGE_VALIDITY, recharge_validity);
                        data.put(VALUE1,value1);
                        data.put(TAG_RECHARGE_DESCRIPTION,recharge_description);
                        data.put(ESTIMATED_COST,estimated_cost);

                        // adding data to data list
                        dataList.add(data);

                    }
                    //    Log.e("Anisha",String.valueOf(dataList));
                   Collections.sort(dataList, new MapComparator("value1"));
                   //    Log.e("Anisha",String.valueOf(dataList));
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
            if (pDialog.isShowing())
                pDialog.dismiss();
            /**
             * Updating parsed JSON data into ListView
             * */
            adapter = new SimpleAdapter(
                    Suggest.this, dataList,
                    R.layout.list_item, new String[] { TAG_RECHARGE_VALUE, ESTIMATED_COST,
                    TAG_RECHARGE_VALIDITY }, new int[] { R.id.recharge_value,
                    R.id.recharge_talktime, R.id.recharge_validity });

            setListAdapter(adapter);
        }

    }
}