package org.rechargeplanrecommender.com.rechargeplanrecommender;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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
import java.util.Locale;

/**
 * Created by NOKIA ASHA on 13/03/2015.
 */
public class Login extends Activity implements View.OnClickListener, TextWatcher
{
    Button login, signUp;
    Info value;
    EditText  e_my_num;
    Spinner dropdown;
    ArrayAdapter<String> adapter1;
    String[] circles, operators;
    MySQLiteHelper db;
    private String my_circle = null,user_operator=null,user_circle=null,my_operator=null;
    private double min=0.0,local_inter_min=0.0,local_intra_min=0.0,local_inter_sec=0.0,local_intra_sec=0.0,std_inter_min=0.0,std_intra_min=0.0,
            std_inter_sec=0.0,std_intra_sec=0.0;
    private Button submit;
    public  String phNumber=null, callType=null ,callDate=null, callDuration=null,dir=null,my_num,str,dateStr;
    Date callDayTime;
    SharedPreferences sharedPref;
    Intent in;
    int number,period;
    private ProgressDialog progress;
    public static final String MY_PREFS_NAME = "MyPrefsFile";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        db= new MySQLiteHelper(this);
        e_my_num = (EditText)findViewById(R.id.my_num);
        e_my_num.addTextChangedListener(this);
        Log.e("success","oncreate");
       // ActionBar ab =getSupportActionBar(); ab.setLogo(R.drawable.ic_launcher);

         sharedPref = getSharedPreferences("data",MODE_PRIVATE);
       number = sharedPref.getInt("isLogged", 0);
        Log.e("logoutinlogin", String.valueOf(MainActivity.logout));
        if(MainActivity.logout==1)
        {
            MainActivity.logout=0;
            SharedPreferences.Editor prefEditor = sharedPref.edit();
            prefEditor.putInt("isLogged", 0);
            prefEditor.commit();
            Log.e("logoutinloop", String.valueOf(number));
        }
        Log.e("logoutoutloop", String.valueOf(number));
//        if(number == 0) {
//            Log.e("success","set shared preference");
//            //Open the login activity and set this so that next it value is 1 then this conditin will be false.
//            SharedPreferences.Editor prefEditor = sharedPref.edit();
//            prefEditor.putInt("isLogged",1);
//            prefEditor.commit();
            try
            {
//            dropdown = (Spinner) findViewById(R.id.circle_list);
//            circles = db.getDropDowndata("Circle");
//            adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, circles);
//            dropdown.setAdapter(adapter1);
                Log.e("in","Operator list");
                Spinner dropdown = (Spinner) findViewById(R.id.operator_list);
                operators =db.getDropDowndata("Operator");
                Log.e("after dropdown","hello");
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operators);
                dropdown.setAdapter(adapter);

            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
     //   }
     //   else
        if(number==1)
        {
            in = new Intent(Login.this, MainActivity.class);
            String my_number = null;
            Log.e("success","second login");
            SharedPreferences prefs = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
             my_number = prefs.getString("name", "No name defined");//"No name defined" is the default value.

            try {
                local_inter_min = 0.0;
                local_intra_min = 0.0;
                local_inter_sec = 0.0;
                local_intra_sec = 0.0;
                std_inter_min = 0.0;
                std_intra_min = 0.0;
                std_inter_sec = 0.0;
                std_intra_sec = 0.0;
                period=0;
                my_circle = db.eval(my_number , "Circle");
                my_operator = db.eval(my_number, "Operator");
                Log.e("Circle",my_circle);
                Log.e("Operator",my_operator);
                getCallDetails();
                Log.e("period", String.valueOf(period));
//                if(period!=0)
//                    in.putExtra("period",period);
                in.putExtra("my_num",my_number);
                in.putExtra("my_operator",my_operator);
                in.putExtra("local_inter_min", local_inter_min);
                in.putExtra("local_intra_min", local_intra_min);
                in.putExtra("local_inter_sec", local_inter_sec);
                in.putExtra("local_intra_sec", local_intra_sec);
                in.putExtra("std_inter_min", std_inter_min);
                in.putExtra("std_intra_min", std_intra_min);
                in.putExtra("std_inter_sec", std_inter_sec);
                in.putExtra("std_intra_sec", std_intra_sec);
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
           startActivity(in);
            Login.this.finish();
            System.exit(0);
        }
        Log.e("success","first login");

       //paste here
        progress = new ProgressDialog(this);
        login=(Button)findViewById(R.id.login);
        signUp=(Button)findViewById(R.id.signup);


        login.setOnClickListener(this);
        signUp.setOnClickListener(this);

        if(isConnected()){}
        else
        {
            Toast.makeText(getBaseContext(), "Please Connect to Internet!", Toast.LENGTH_LONG).show();
        }

    }
    public boolean isConnected()
    {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings)
        {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public static String POST(String url, Info value){
        InputStream inputStream = null;
        String result = "";
        try
        {
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
            String encrypt=ED.encrypt(json,"qazxswedc");
            // ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity se = new StringEntity(encrypt);
           // StringEntity se = new StringEntity(json);

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
        }
        catch (Exception e)
        {
            Log.d("InputStream", e.getLocalizedMessage());
        }
        // 11. return result
        return result;
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        Intent i;
        my_num = e_my_num.getText().toString();
        switch(id) {
            case R.id.login:
                i = new Intent(Login.this, MainActivity.class);
                    local_inter_min = 0.0;
                    local_intra_min = 0.0;
                    local_inter_sec = 0.0;
                    local_intra_sec = 0.0;
                    std_inter_min = 0.0;
                    std_intra_min = 0.0;
                    std_inter_sec = 0.0;
                    std_intra_sec = 0.0;


                if (my_num.equalsIgnoreCase(null) || my_num.length() < 10 )
                {
                    Toast.makeText(getBaseContext(), "Please enter a valid length number!!", Toast.LENGTH_LONG).show();
                    SharedPreferences.Editor prefEditor = sharedPref.edit();
                    prefEditor.putInt("isLogged",0);
                    prefEditor.commit();
                }
                else
                {
                    try {
                    my_circle = db.eval(my_num, "Circle");
                    my_operator = db.eval(my_num, "Operator");
                    } catch (Exception e) {
                        e.printStackTrace();}
                    if (my_circle == null || my_operator == null)
                    {
                        Toast.makeText(getBaseContext(), "Please enter a valid number!!", Toast.LENGTH_LONG).show();
                        SharedPreferences.Editor prefEditor = sharedPref.edit();
                        prefEditor.putInt("isLogged", 0);
                        prefEditor.commit();
                    }
                    else
                    {
                        if(number == 0) {
                            Log.e("success", "set shared preference");
                            //Open the login activity and set this so that next it value is 1 then this conditin will be false.
                            SharedPreferences.Editor prefEditor = sharedPref.edit();
                            prefEditor.putInt("isLogged", 1);
                            Log.e("Login", String.valueOf(number));
                            prefEditor.commit();
                        }
                        SharedPreferences.Editor editor = getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
                        editor.putString("name", my_num);
                        editor.commit();

                        getCallDetails();
                        Log.e("period", String.valueOf(period));
//                        if(period!=0)
//                            i.putExtra("period",period);
                        i.putExtra("my_num", my_num);
                        i.putExtra("my_operator", my_operator);
                        i.putExtra("local_inter_min", local_inter_min);
                        i.putExtra("local_intra_min", local_intra_min);
                        i.putExtra("local_inter_sec", local_inter_sec);
                        i.putExtra("local_intra_sec", local_intra_sec);
                        i.putExtra("std_inter_min", std_inter_min);
                        i.putExtra("std_intra_min", std_intra_min);
                        i.putExtra("std_inter_sec", std_inter_sec);
                        i.putExtra("std_intra_sec", std_intra_sec);

                        startActivity(i);
                        System.exit(0);
                    }
                }
                //finish();
                break;
            case R.id.signup:
                try {
                    if (my_num.equalsIgnoreCase(null) || my_num.length() < 10) {
                        Toast.makeText(getBaseContext(), "Please enter a valid length number!!", Toast.LENGTH_LONG).show();
                    } else {
                        try {
                            my_circle = db.eval(my_num, "Circle");
                            my_operator = db.eval(my_num, "Operator");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        if (my_circle == null || my_operator == null) {
                            Toast.makeText(getBaseContext(), "Please enter a valid number!!", Toast.LENGTH_LONG).show();
                        }
                        else if (!my_circle.equalsIgnoreCase("Karnataka")) {
                            Toast.makeText(getBaseContext(), "Enter number from Karnataka only!", Toast.LENGTH_LONG).show();
                        } else {
                            // call AsynTask to perform network operation on separate thread
                            Toast.makeText(getBaseContext(), "Please Wait !", Toast.LENGTH_LONG).show();
                            new HttpAsyncTask().execute("http://cloudproject-437146228.ap-southeast-1.elb.amazonaws.com/register");
                        }

                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
                break;
        }
    }
    private class HttpAsyncTask extends AsyncTask<String, Void, String>
    {
        @Override
        protected String doInBackground(String... urls)
        {
            value = new Info();
            value.setNumber(my_num);
            value.setCircle(my_circle);
            value.setOperator(my_operator);
            str=POST(urls[0],value);
            return str;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result)
        {
            if(isConnected())
            {
                Toast.makeText(getBaseContext(), str , Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(getBaseContext(), "Please Connect to Internet!", Toast.LENGTH_LONG).show();
            }
        }
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException
    {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;
        inputStream.close();
        return result;
    }

    private void getCallDetails()
    {
        min=0.0;local_inter_min=0.0;local_intra_min=0.0;local_inter_sec=0.0;local_intra_sec=0.0;std_inter_min=0.0;std_intra_min=0.0;
            std_inter_sec=0.0;std_intra_sec=0.0;period=0;
        String udd,umm,uyy,old=null;
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        Log.e("mainActivity","managed Cursor");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        while (managedCursor.moveToNext())
        {
            phNumber = managedCursor.getString(number);
            callType = managedCursor.getString(type);
            callDate = managedCursor.getString(date);
            callDayTime = new Date(Long.valueOf(callDate));
            dateStr = new SimpleDateFormat("dd:MM:yyyy", Locale.ENGLISH).format(callDayTime);
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
                udd = dateStr.substring(0, 2);
                umm = dateStr.substring(3, 5);
                uyy = dateStr.substring(6);
                calc_period(udd,old);
                old=udd;
                try
                {
                    user_circle=db.eval(phNumber, "Circle");
                    user_operator=db.eval(phNumber, "Operator");
                    min = (Integer.parseInt(callDuration)) / 60.0;
                    if (my_circle.equalsIgnoreCase(user_circle)) {
                        if (my_operator.equalsIgnoreCase(user_operator))
                        {
                            local_intra_sec = local_intra_sec + Integer.parseInt(callDuration);
                            local_intra_min = local_intra_min + Math.ceil(min);
                        }
                        else
                        {
                            local_inter_sec = local_inter_sec + Integer.parseInt(callDuration);
                            local_inter_min = local_inter_min + Math.ceil(min);
                        }
                    }
                    else
                    {
                        if (my_operator.equalsIgnoreCase(user_operator))
                        {
                            std_intra_sec = std_intra_sec + Integer.parseInt(callDuration);
                            std_intra_min = std_intra_min + Math.ceil(min);
                        }
                        else
                        {
                            std_inter_sec = std_inter_sec + Integer.parseInt(callDuration);
                            std_inter_min = std_inter_min + Math.ceil(min);
                        }
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
        managedCursor.close();
    }
    void calc_period(String day,String old )
    {
        if(!day.equals(old))
            period++;
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count)
    {
        int l=s.length();
        if(l!=0)
        {
            char c = s.toString().charAt(0);
            if (c == '0' && l == 11) {
                my_num = e_my_num.getText().toString();
                try
                {
                    my_circle = db.eval(my_num, "Circle");
                    my_operator = db.eval(my_num, "Operator");
                    if (my_circle == " " || my_operator == " ")
                    {
                        Toast.makeText(getBaseContext(), "Enter a valid number", Toast.LENGTH_LONG).show();
                    }
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
                try
                {
//                    dropdown = (Spinner) findViewById(R.id.circle_list);
//                    circles = db.getDropDowndata("Circle");
//                    circles[circles.length - 1] = my_circle;
//                    String temp = circles[circles.length - 1];
//                    circles[circles.length - 1] = circles[0];
//                    circles[0] = temp;
//                    adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, circles);
//                    dropdown.setAdapter(adapter1);

                    Spinner dropdown = (Spinner) findViewById(R.id.operator_list);
                    operators = db.getDropDowndata("Operator");
                    operators[operators.length - 1] = my_operator;
                    String temp1 = operators[operators.length - 1];
                    operators[operators.length - 1] = operators[0];
                    operators[0] = temp1;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operators);
                    dropdown.setAdapter(adapter);
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
            }
            else if (c != '0' && l == 10)
            {
                my_num = e_my_num.getText().toString();
                try
                {
                    my_circle = db.eval(my_num, "Circle");
                    my_operator =db.eval(my_num, "Operator");
                    if(my_circle==" " || my_operator==" ")
                    {
                        Toast.makeText(getBaseContext(), "Enter a valid number", Toast.LENGTH_LONG).show();
                    }
                }
                catch (SQLException e)
                {
                    e.printStackTrace();
                }
                try
                {
//                    dropdown = (Spinner)findViewById(R.id.circle_list);
//                    circles = db.getDropDowndata("Circle");
//                    circles[circles.length - 1] = my_circle;
//                    String temp = circles[circles.length - 1];
//                    circles[circles.length - 1] = circles[0];
//                    circles[0] = temp;
//                    adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, circles);
//                    dropdown.setAdapter(adapter1);

                    Spinner dropdown = (Spinner)findViewById(R.id.operator_list);
                    operators = db.getDropDowndata("Operator");
                    operators[operators.length - 1] = my_operator;
                    String temp1 = operators[operators.length - 1];
                    operators[operators.length - 1] = operators[0];
                    operators[0] = temp1;
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operators);
                    dropdown.setAdapter(adapter);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void afterTextChanged(Editable s) { }

    }