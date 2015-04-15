package org.rechargeplanrecommender.com.rechargeplanrecommender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;


public class MainActivity extends Activity implements View.OnClickListener {
    String s,str,my_num;
    Double local_inter_min,local_inter_sec,std_inter_min,std_inter_sec,local_intra_min,local_intra_sec,std_intra_min,std_intra_sec,local_min,local_sec,std_min,std_sec;
    TimeValue value;
    TextView tvIsConnected;
    TextView t_local_min,t_local_sec,t_std_min,t_std_sec;
    Button suggest,usage_graph,btnPost;
    String fromDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Bundle b= getIntent().getExtras();
        my_num=b.getString("my_num");
        local_inter_min=b.getDouble("local_inter_min");
        local_inter_sec=b.getDouble("local_inter_sec");
        std_inter_min=b.getDouble("std_inter_min");
        std_inter_sec=b.getDouble("std_inter_sec");
        local_intra_min=b.getDouble("local_intra_min");
        local_intra_sec=b.getDouble("local_intra_sec");
        std_intra_min=b.getDouble("std_intra_min");
        std_intra_sec=b.getDouble("std_intra_sec");

        local_min=local_inter_min+local_intra_min;
        local_sec=local_inter_sec+local_intra_sec;
        std_min=std_inter_min+std_intra_min;
        std_sec=std_intra_sec+std_inter_sec;



        t_local_min=(TextView)findViewById(R.id.local_minute);
        t_local_min.setText(local_min.toString());
        t_local_sec=(TextView)findViewById(R.id.local_sec);
        t_local_sec.setText(local_sec.toString());
        t_std_min=(TextView)findViewById(R.id.std_minute);
        t_std_min.setText(std_min.toString());
        t_std_sec=(TextView)findViewById(R.id.std_sec);
        t_std_sec.setText(std_sec.toString());


        suggest = (Button)findViewById(R.id.suggest);
        usage_graph = (Button)findViewById(R.id.usage_graph);
        btnPost = (Button) findViewById(R.id.btnPost);
        tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        suggest.setOnClickListener(this);
        usage_graph.setOnClickListener(this);
        btnPost.setOnClickListener(this);
        if(isConnected()){
            tvIsConnected.setText("You are connected");
        }
        else{
            tvIsConnected.setText("You are NOT connected");
        }

    }


    public static String POST(String url, TimeValue value){
        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json = "";

            // 3. build jsonObject
            JSONObject jsonObject = new JSONObject();

            jsonObject.put("plan", value.getPlan());

            JSONObject jsonObject1 =new JSONObject();
            jsonObject1.accumulate("local_intra_minute", value.getLocal_intra_minute());
            jsonObject1.accumulate("std_intra_minute", value.getStd_intra_minute());
            jsonObject1.accumulate("local_inter_minute", value.getLocal_inter_minute());
            jsonObject1.accumulate("std_inter_minute", value.getStd_inter_minute());

            JSONObject jsonObject2 =new JSONObject();
            jsonObject2.accumulate("local_intra_sec", value.getLocal_intra_sec());
            jsonObject2.accumulate("std_intra_sec", value.getStd_intra_sec());
            jsonObject2.accumulate("local_inter_sec", value.getLocal_inter_sec());
            jsonObject2.accumulate("std_inter_sec", value.getStd_inter_sec());

            JSONArray a=new JSONArray();
            a.put(jsonObject1);
            a.put(jsonObject2);

            jsonObject.put("data", a);
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();

            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(se);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null) {
                result = convertInputStreamToString(inputStream);
                return result;
            }
            else
                return "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }


    public boolean isConnected(){
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }
    @Override
    public void onClick(View view) {
        int id =view.getId();
        Intent i=null;

        switch(id){
            case R.id.suggest:
                i=new Intent(this,Suggest.class);
                startActivity(i);
                break;
            case R.id.usage_graph:
                i=new Intent(this,SimpleXYPlotActivity.class);
                i.putExtra("my_num",my_num);
                startActivity(i);
                break;
            case R.id.btnPost:
                    //Toast.makeText(getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();
                // call AsynTask to perform network operation on separate thread
                final CharSequence[] items = {"LOCAL", "LOCAL & STD"};

                //Prepare the list dialog box

                AlertDialog.Builder builder = new AlertDialog.Builder(this);

                //Set its title

                builder.setTitle("Choose Topup That You Want To Search For");

                //Set the list items along with checkbox and assign with the click listener

                builder.setSingleChoiceItems(items, -1, new DialogInterface.OnClickListener() {

                    // Click listener

                    public void onClick(DialogInterface dialog, int item) {

                        //Toast.makeText(getApplicationContext(), items[item], Toast.LENGTH_SHORT).show();

                        //If the Cheese item is chosen close the dialog box

                        if(items[item]=="LOCAL") {
                            new HttpAsyncTask().execute("http://192.168.137.132:8888/want_plans");
                            dialog.dismiss();

                        }
//                        if(items[item]=="STD") {
//                            new HttpAsyncTask().execute("http://192.168.137.132:8888/want_plans");
//                            dialog.dismiss();
//                        }
                         if(items[item]=="LOCAL & STD")
                                 new HttpAsyncTask().execute("http://192.168.137.132:8888/want_plans");

                                 dialog.dismiss();

                             s=(String)items[item];


                    }

                });

                AlertDialog alert = builder.create();

                //display dialog box

                alert.show();
//                try {
//                    wait(1000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//                i=new Intent(this,Suggest.class);
//                i.putExtra("str", str);
//                startActivity(i);
////
//                String c=
//                i.putExtra("str", c);
//                startActivity(i);

                break;


        }
    }

//    private String onBackgroundTaskDataObtained(String results) {
//
//   fromDatabase= results;
//        return results;
//        //do stuff with the results here..
//    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
                @Override
        protected String doInBackground(String... urls) {

            value = new TimeValue();
            value.setPlan(s);
            value.setLocal_intra_minute(local_intra_min);
            value.setLocal_intra_sec(local_intra_sec);
            value.setStd_intra_minute(std_intra_min);
            value.setStd_intra_sec(std_intra_sec);
            value.setLocal_inter_minute(local_inter_min);
            value.setLocal_inter_sec(local_inter_sec);
            value.setStd_inter_minute(std_inter_min);
            value.setStd_inter_sec(std_inter_sec);
            str=POST(urls[0],value);
            return str;
        }

       // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Toast.makeText(getBaseContext(), "Data Send!!", Toast.LENGTH_LONG).show();
           // MainActivity.this.onBackgroundTaskDataObtained(result);

        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;

    }
}


