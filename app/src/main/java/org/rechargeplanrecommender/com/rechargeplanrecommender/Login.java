package org.rechargeplanrecommender.com.rechargeplanrecommender;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.CallLog;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by NOKIA ASHA on 13/03/2015.
 */
public class Login extends Activity implements View.OnClickListener, TextWatcher {
    Button login, signUp;
    Info value;
    EditText  e_my_num;
    Spinner dropdown;
    ArrayAdapter<String> adapter1;
    String  my_num,str;
    String[] circles, operators;
    MySQLiteHelper db;
    private String my_circle = null,user_operator=null,user_circle=null,my_operator=null;

    private double min=0.0,local_inter_min=0.0,local_intra_min=0.0,local_inter_sec=0.0,local_intra_sec=0.0,std_inter_min=0.0,std_intra_min=0.0,
            std_inter_sec=0.0,std_intra_sec=0.0;
    private Button submit;
    public  String phNumber=null, callType=null ,callDate=null, callDuration=null;
    Date callDayTime;
  //  public Calendar c ;
    SimpleDateFormat df ;
    String dir = null;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        db= new MySQLiteHelper(this);

       // Log.e("error","no error");
        e_my_num = (EditText)findViewById(R.id.my_num);

        e_my_num.addTextChangedListener(this);
        dropdown = (Spinner)findViewById(R.id.circle_list);
        circles = new String[]{ "Karnataka", "Uttar Pradesh (west)", "Madhya Pradesh & Chattisgarh",my_circle};
        adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, circles);

        dropdown.setAdapter(adapter1);
        Spinner dropdown = (Spinner)findViewById(R.id.operator_list);
        operators = new String[]{"MTNL", "Bharti Airtel Limited", "Aircel Limited","Docomo",my_operator};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operators);
        dropdown.setAdapter(adapter);


        login=(Button)findViewById(R.id.login);
        signUp=(Button)findViewById(R.id.signup);


        login.setOnClickListener(this);
        signUp.setOnClickListener(this);

        if(isConnected()){
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

    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String POST(String url, Info value){
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
            jsonObject.accumulate("number", value.getNumber());
            jsonObject.accumulate("circle", value.getCircle());
            jsonObject.accumulate("operator", value.getOperator());


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
              if(inputStream != null)
                  result = convertInputStreamToString(inputStream);
              else
                 result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent i;
        switch(id) {
            case R.id.login:
                i = new Intent(Login.this, MainActivity.class);
                try {
                    local_inter_min = 0.0;
                    local_intra_min = 0.0;
                    local_inter_sec = 0.0;
                    local_intra_sec = 0.0;
                    std_inter_min = 0.0;
                    std_intra_min = 0.0;
                    std_inter_sec = 0.0;
                    std_intra_sec = 0.0;

                    my_num = e_my_num.getText().toString();
                    my_circle = db.eval(my_num, "Circle");
                    my_operator = db.eval(my_num, "Operator");

                    getCallDetails();
                    i.putExtra("my_num",my_num);
                    i.putExtra("local_inter_min", local_inter_min);
                    i.putExtra("local_intra_min", local_intra_min);
                    i.putExtra("local_inter_sec", local_inter_sec);
                    i.putExtra("local_intra_sec", local_intra_sec);
                    i.putExtra("std_inter_min", std_inter_min);
                    i.putExtra("std_intra_min", std_intra_min);
                    i.putExtra("std_inter_sec", std_inter_sec);
                    i.putExtra("std_intra_sec", std_intra_sec);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                startActivity(i);
                break;
            case R.id.signup:
                try {
                    my_circle = db.eval(my_num, "Circle");
                    my_operator = db.eval(my_num, "Operator");
                    Log.e("error",my_circle);
                    if (!my_circle.equalsIgnoreCase("Karnataka")){
                        Toast.makeText(getBaseContext(), "Enter number from Karnataka only!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        // call AsynTask to perform network operation on separate thread
                        new HttpAsyncTask().execute("http://192.168.137.132:8888/register");
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;


        }
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            value = new Info();
            value.setNumber(my_num);
            value.setCircle(my_circle);
            value.setOperator(my_operator);
            str=POST(urls[0],value);
            return str;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            if(isConnected()){
                Toast.makeText(getBaseContext(), str , Toast.LENGTH_LONG).show();
            }
            else{
                Toast.makeText(getBaseContext(), "Please Connect to Internet!", Toast.LENGTH_LONG).show();
            }

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

    private void getCallDetails() {
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        Log.e("mainActivity","managed Cursor");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext()) {
            phNumber = managedCursor.getString(number);
            callType = managedCursor.getString(type);
            callDate = managedCursor.getString(date);
            callDayTime = new Date(Long.valueOf(callDate));
            callDuration = managedCursor.getString(duration);
            dir = null;
            int  dircode = Integer.parseInt(callType);
            switch (dircode) {
                case CallLog.Calls.OUTGOING_TYPE:
                    dir = "OUTGOING";
                    break;

                case CallLog.Calls.INCOMING_TYPE:
                    dir = "INCOMING";
                    break;

                case CallLog.Calls.MISSED_TYPE:
                    dir = "MISSED";
                    break;
            }
            if(dir=="OUTGOING")
            {

                try {
                    // sb.append(phNumber+"\n");
                    user_circle=db.eval(phNumber, "Circle");
                    //sb.append(user_circle+"\n");
                    user_operator=db.eval(phNumber, "Operator");
                    // sb.append(user_operator+"\n");
                    min = (Integer.parseInt(callDuration)) / 60.0;
                    if (my_circle.equalsIgnoreCase(user_circle)) {
                        if (my_operator.equalsIgnoreCase(user_operator)){
                           // sb.append(" INTER LOCAL CALL\n");
                            local_intra_sec = local_intra_sec + Integer.parseInt(callDuration);
                            local_intra_min = local_intra_min + Math.ceil(min);
                        }

                        else{
                            //sb.append(" INTRA LOCAL CALL\n");
                            local_inter_sec = local_inter_sec + Integer.parseInt(callDuration);
                            local_inter_min = local_inter_min + Math.ceil(min);
                        }
                    }
                    else {
                        if (my_operator.equalsIgnoreCase(user_operator))
                        {
                            std_intra_sec = std_intra_sec + Integer.parseInt(callDuration);
                            std_intra_min = std_intra_min + Math.ceil(min);
                            //sb.append(" INTER STD CALL\n");
                        }
                        else{
                            //sb.append(" INTRA STD CALL\n");
                            std_inter_sec = std_inter_sec + Integer.parseInt(callDuration);
                            std_inter_min = std_inter_min + Math.ceil(min);
                        }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        managedCursor.close();
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        String num;
        int l=s.length();

        if(l!=0) {
            char c = s.toString().charAt(0);
            if (c == '0' && l == 11) {
                my_num = e_my_num.getText().toString();
                try {
                    my_circle = db.eval(my_num, "Circle");

                //t_my_circle.setText("Your circle"+my_circle);

                    my_operator =db.eval(my_num, "Operator");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //t_my_operator.setText("Your operator"+my_operator);
                dropdown = (Spinner)findViewById(R.id.circle_list);
                circles = new String[]{my_circle, "Karnataka", "Uttar Pradesh (west)", "Madhya Pradesh & Chattisgarh"};
                adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, circles);

                dropdown.setAdapter(adapter1);
                Spinner dropdown = (Spinner)findViewById(R.id.operator_list);
                operators = new String[]{my_operator,"MTNL", "Bharti Airtel Limited", "Aircel Limited"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operators);
                dropdown.setAdapter(adapter);

            }
            else if (c != '0' && l == 10) {
                my_num = e_my_num.getText().toString();
                try {
                    my_circle = db.eval(my_num, "Circle");

                    //t_my_circle.setText("Your circle"+my_circle);

                    my_operator =db.eval(my_num, "Operator");
                } catch (SQLException e) {
                    e.printStackTrace();
                }

                //t_my_operator.setText("Your operator"+my_operator);
                dropdown = (Spinner)findViewById(R.id.circle_list);
                circles = new String[]{my_circle, "Karnataka", "Uttar Pradesh (west)", "Madhya Pradesh & Chattisgarh"};
                adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, circles);

                dropdown.setAdapter(adapter1);
                Spinner dropdown = (Spinner)findViewById(R.id.operator_list);
                operators = new String[]{my_operator,"MTNL", "Bharti Airtel Limited", "Aircel Limited"};
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operators);
                dropdown.setAdapter(adapter);
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) {

    }
}
