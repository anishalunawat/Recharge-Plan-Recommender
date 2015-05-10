package org.rechargeplanrecommender.com.rechargeplanrecommender;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Calendar;
import java.util.concurrent.ExecutionException;


public class MainActivity extends Activity implements View.OnClickListener {
    String s="",str1,str,my_num,my_operator;
    Intent i=null,in=null;

    Double local_inter_min=0.0,local_inter_sec=0.0,std_inter_min=0.0,std_inter_sec=0.0,local_intra_min=0.0
    ,local_intra_sec=0.0,std_intra_min=0.0,std_intra_sec=0.0;
    int local_min=0,local_sec=0,std_min=0,std_sec=0;
    int period;
    TimeValue value;
    TextView t_local_min,t_local_sec,t_std_min,t_std_sec;
    Button suggest,usage_graph,btnLogout,mobile_plan;
    String fromDatabase;
    public static int logout=0;
   public static final String MY_PREFS = "MyPrefs";
    SharedPreferences  sharedPref;
    SharedPreferences.Editor editor1;
    public Calendar c;
      ED send;

    @Override
        protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        send=new ED();
        Bundle b= getIntent().getExtras();
        sharedPref = getSharedPreferences(MY_PREFS,MODE_PRIVATE);
        my_num=b.getString("my_num");
        period=b.getInt("period");
        my_operator=b.getString("my_operator");
        local_inter_min=b.getDouble("local_inter_min");
        local_inter_sec=b.getDouble("local_inter_sec");
        std_inter_min=b.getDouble("std_inter_min");
        std_inter_sec=b.getDouble("std_inter_sec");
        local_intra_min=b.getDouble("local_intra_min");
        local_intra_sec=b.getDouble("local_intra_sec");
        std_intra_min=b.getDouble("std_intra_min");
        std_intra_sec=b.getDouble("std_intra_sec");

        local_min=0;local_sec=0;std_min=0;std_sec=0;

        local_min=(int)(local_inter_min+local_intra_min);
        local_sec=(int)(local_inter_sec+local_intra_sec);
        std_min=(int)(std_inter_min+std_intra_min);
        std_sec=(int)(std_intra_sec+std_inter_sec);



        t_local_min=(TextView)findViewById(R.id.local_minute);
        t_local_min.setText(String.valueOf(local_min));
        t_local_sec=(TextView)findViewById(R.id.local_sec);
        t_local_sec.setText(String.valueOf(local_sec));
        t_std_min=(TextView)findViewById(R.id.std_minute);
        t_std_min.setText(String.valueOf(std_min));
        t_std_sec=(TextView)findViewById(R.id.std_sec);
        t_std_sec.setText(String.valueOf(std_sec));

        // Button logout

        btnLogout = (Button) findViewById(R.id.btnLogout);


//        Toast.makeText(getApplicationContext(), "User Login Status: " + session.isLoggedIn(), Toast.LENGTH_LONG).show();

        /**
         * Call this function whenever you want to check user login
         * This will redirect user to LoginActivity is he is not
         * logged in
         * */

//        session.checkLogin();


        suggest = (Button)findViewById(R.id.suggest);
        usage_graph = (Button)findViewById(R.id.usage_graph);
        mobile_plan = (Button)findViewById(R.id.btnPost);

       // tvIsConnected = (TextView) findViewById(R.id.tvIsConnected);
        suggest.setOnClickListener(this);
        usage_graph.setOnClickListener(this);
        mobile_plan.setOnClickListener(this);
        btnLogout.setOnClickListener(this);
    }



    public String POST(String url, TimeValue value){
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
            jsonObject.put("my_num",value.getMy_num());
            jsonObject.put("plan", value.getPlan());
            jsonObject.put("operator",value.getMy_operator());
            jsonObject.put("period",value.getPeriod());

            JSONObject jsonObject1 =new JSONObject();
            jsonObject1.accumulate("local_intra_minute", value.getLocal_intra_minute());
            jsonObject1.accumulate("local_inter_minute", value.getLocal_inter_minute());
            jsonObject1.accumulate("local_intra_sec", value.getLocal_intra_sec());
            jsonObject1.accumulate("local_inter_sec", value.getLocal_inter_sec());

            JSONObject jsonObject2 =new JSONObject();
            jsonObject2.accumulate("std_intra_minute", value.getStd_intra_minute());
            jsonObject2.accumulate("std_inter_minute", value.getStd_inter_minute());
            jsonObject2.accumulate("std_intra_sec", value.getStd_intra_sec());
            jsonObject2.accumulate("std_inter_sec", value.getStd_inter_sec());

            JSONArray a=new JSONArray();
            a.put(jsonObject1);
            a.put(jsonObject2);

            jsonObject.put("data", a);
            // 4. convert JSONObject to JSON to String
            json = jsonObject.toString();
            String encrypt= send.encrypt(json,"qazxswedc");
           // String encrypt=secure.encrypt("qazxswedc",json);
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);
            // 5. set json to StringEntity
            StringEntity se = new StringEntity(encrypt);
            //StringEntity se = new StringEntity(json);
            // 6. set httpPost Entity
            httpPost.setEntity(se);
            Log.e("success","sent");
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

   public  void call(String s)
   {
       int dd,mm,yy;
       if(isConnected()) {
           try {
              // str1 = new HttpAsyncTask().execute("http://cloudproject-437146228.ap-southeast-1.elb.amazonaws.com/want_plans").get();
               str1 = new HttpAsyncTask().execute("http://cloudproject-437146228.ap-southeast-1.elb.amazonaws.com/want_plans").get();
               str=send.decrypt(str1,"qazxswedc");
               c = Calendar.getInstance();
               //  Log.e("error","Current time => " + c.getTime());
               yy = c.get(Calendar.YEAR);
               mm = c.get(Calendar.MONTH);
               dd = c.get(Calendar.DAY_OF_MONTH);
               mm=mm+1;
               editor1 = getSharedPreferences(MY_PREFS, MODE_PRIVATE).edit();
               editor1.putString("plans", str);
               editor1.putInt("day",dd);
               editor1.putInt("month",mm);
               editor1.putInt("year",yy);
               //editor.putString("plans", str1);
               editor1.commit();
           } catch (InterruptedException e) {
               e.printStackTrace();
           } catch (ExecutionException e) {
               e.printStackTrace();
           } catch (Exception e) {
               e.printStackTrace();
           }
           if(str.equals("Register"))
            {
                Toast.makeText(getBaseContext(),"Sorry!!You first need to register to view topup", Toast.LENGTH_LONG).show();
            }
           else if(str.equals("No Plans"))
           {
               AlertDialog.Builder builder = new AlertDialog.Builder(this);

               builder.setTitle("Sorry there are no plans we will update the plans soon");

               AlertDialog alert = builder.create();

               alert.show();
           }
           else
            {
                i = new Intent(this, Suggest.class);
                i.putExtra("str", str);
                //i.putExtra("str", str1);
                startActivity(i);
            }
       }
       else{

           Toast.makeText(getBaseContext(), "Please Connect to Internet!", Toast.LENGTH_LONG).show();
       }

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

        switch(id){
            case R.id.btnLogout:
                logout=1;
                Log.e("logout", String.valueOf(logout));
                SharedPreferences settings = getSharedPreferences(Login.MY_PREFS_NAME, MODE_PRIVATE);
                SharedPreferences.Editor editor = settings.edit();
                editor.clear();
                editor.commit();
                Log.e("logout","logout1");
                setLoginState(0);
                editor1 = getSharedPreferences(MY_PREFS, MODE_PRIVATE).edit();
                editor1.remove("plans");
                editor1.commit();
                Log.e("logout","logout2");
               // Log.d(TAG, "Now log out and start the activity login");
                i=new Intent(MainActivity.this,Login.class);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(i);
                finish();
                break;
            case R.id.btnPost:
                i=new Intent(this,Suggest.class);
                str = sharedPref.getString("plans",null);
//                dd= sharedPref.getInt("day",0);
//                mm=sharedPref.getInt("month",0);
//                yy=sharedPref.getInt("year",0);
               // str="{\"data\":[{\"id\":\"12954868\",\"estimated_cost\":\"20\",\"recharge_value\":\"5\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"1 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" National Roaming Voice Calls O G LOC or STD Calls 1.5 ps per Sec\",\"recharge_description_more\":\" FREE INCOMING CALLS: ROAM_5\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"}]}";
                if(str!=null)
                {
                    i.putExtra("str", str);
                    startActivity(i);
                }
                else
                {
                    Toast.makeText(getBaseContext(), "Nothing to view.. Please click on Find Recommended topups", Toast.LENGTH_LONG).show();
                   // Toast.makeText(getBaseContext(), "sorry no plans are available for this operator.. will fix this issue soon!!", Toast.LENGTH_LONG).show();
                }
                break;
            case R.id.usage_graph:
                i=new Intent(this,SimpleXYPlotActivity.class);
                i.putExtra("my_num",my_num);
                startActivity(i);
                break;
            case R.id.suggest:
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

                        //If the Cheese item is chosen close the dialog box

                        if(items[item]=="LOCAL") {
                            s="LOCAL";
                            dialog.dismiss();
                            call(s);

                        }
                        if(items[item]=="LOCAL & STD") {
                            s = "LOCAL & STD";
                            dialog.dismiss();
                            call(s);

                        }

                    }

                });

                AlertDialog alert = builder.create();

                alert.show();
        }
    }
    private void setLoginState(int status) {
        SharedPreferences sp = getSharedPreferences("data",
                MODE_PRIVATE);
        SharedPreferences.Editor ed = sp.edit();
        ed.putInt("isLogged", status);
        ed.commit();
    }

    private String onBackgroundTaskDataObtained(String results) {

        fromDatabase= results;
        return results;
        //do stuff with the results here..
    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            value = new TimeValue();
            value.setMy_num(my_num);
            value.setPlan(s);
            value.setPeriod(period);
            value.setMy_operator(my_operator);
            value.setLocal_intra_minute(local_intra_min);
            value.setLocal_intra_sec(local_intra_sec);
            value.setStd_intra_minute(std_intra_min);
            value.setStd_intra_sec(std_intra_sec);
            value.setLocal_inter_minute(local_inter_min);
            value.setLocal_inter_sec(local_inter_sec);
            value.setStd_inter_minute(std_inter_min);
            value.setStd_inter_sec(std_inter_sec);
            str=POST(urls[0],value);
           //str="{\"data\":[{\"id\":\"12954868\",\"estimated_cost\":\"20\",\"recharge_value\":\"5\",\"recharge_talktime\":\"0\",\"recharge_validity\":\"1 Days\",\"recharge_short_description\":\"Tariff Voucher\",\"recharge_description\":\" National Roaming Voice Calls O G LOC or STD Calls 1.5 ps per Sec\",\"recharge_description_more\":\" FREE INCOMING CALLS: ROAM_5\",\"product_type\":\"Datacard\",\"circle_master\":\"Karnataka\",\"operator_master\":\"BSNL\",\"recharge_master\":\"\",\"is_prepaid\":\"1\"}]}";
            //str="No Plans";
            return str;
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
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


