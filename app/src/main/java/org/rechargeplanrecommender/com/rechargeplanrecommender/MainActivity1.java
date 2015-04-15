package org.rechargeplanrecommender.com.rechargeplanrecommender;

import android.database.Cursor;
import android.os.Bundle;
import android.provider.CallLog;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity1 extends ActionBarActivity implements View.OnClickListener
{

    EditText  e_my_num;
    TextView t_local_intra_min,t_local_intra_sec,t_std_intra_min,t_std_intra_sec,t_my_circle,t_my_operator,
            t_local_inter_min,t_local_inter_sec,t_std_inter_min,t_std_inter_sec, t_user_circle,t_user_operator;
    TextView textView = null;
    Spinner dropdown;
    ArrayAdapter<String> adapter1;
    String  my_num,my_num1;
    String[] circles;
    String[] operators;
    MySQLiteHelper db;
    private String my_circle = null,my_circle1;
    private String my_operator=null;
    private String user_circle=null,user_circle1;
    private String user_operator=null;
    private double min=0.0,local_inter_min=0.0,local_intra_min=0.0,local_inter_sec=0.0,local_intra_sec=0.0,std_inter_min=0.0,std_intra_min=0.0,
            std_inter_sec=0.0,std_intra_sec=0.0;
    private Button submit;
    int dd,mm,yy,days_in_month[]={31,28,31,30,31,30,31,31,30,31,30,31};;
    public  String phNumber=null, callType=null ,callDate=null, callDuration=null,dateStr,udd,umm,uyy;
    Date callDayTime;
     public Calendar c ;
    SimpleDateFormat df ;
    String dir = null;
    ArrayList<Number> locals = new ArrayList<Number>();
    ArrayList<Number> stds=new ArrayList<Number>();
    Login login=new Login();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        db= new MySQLiteHelper(this);

        Log.e("error","no error");
        e_my_num = (EditText)findViewById(R.id.my_num);
        Log.e("error","no error 1.1");
        t_local_intra_min = (TextView)findViewById(R.id.local_intra_minute);
        t_local_intra_sec = (TextView)findViewById(R.id.local_intra_sec);
        t_std_intra_min = (TextView)findViewById(R.id.std_intra_minute);
        t_std_intra_sec = (TextView)findViewById(R.id.std_intra_sec);
        t_local_inter_min = (TextView)findViewById(R.id.local_inter_minute);
        t_local_inter_sec = (TextView)findViewById(R.id.local_inter_sec);
        t_std_inter_min = (TextView)findViewById(R.id.std_inter_min);
        t_std_inter_sec = (TextView)findViewById(R.id.std_inter_sec);
        t_my_circle = (TextView)findViewById(R.id.my_circle);
        t_my_operator = (TextView)findViewById(R
                .id.my_operator);
        textView = (TextView) findViewById(R.id.textview_call);
        submit =(Button)findViewById(R.id.submit);
        submit.setOnClickListener(this);


    }
    @Override
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

    @Override
    public void onClick(View v) {

        try
        {
            local_inter_min=0.0;local_intra_min=0.0;local_inter_sec=0.0;local_intra_sec=0.0;std_inter_min=0.0;std_intra_min=0.0;
                    std_inter_sec=0.0;std_intra_sec=0.0;
            my_num = e_my_num.getText().toString();
           // Log.e("error", "no error 1");
            my_circle = db.eval(my_num, "Circle");
            t_my_circle.setText("Your circle"+my_circle);
            my_operator =db.eval(my_num, "Operator");
            t_my_operator.setText("Your operator"+my_operator);
            dropdown = (Spinner)findViewById(R.id.circle_list);
            circles = new String[]{my_circle,"Karnataka", "Uttar Pradesh (west)", "Madhya Pradesh & Chattisgarh"};
            adapter1 = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, circles);
            dropdown.setAdapter(adapter1);
            Spinner dropdown = (Spinner)findViewById(R.id.operator_list);
            operators = new String[]{my_operator,"MTNL", "Bharti Airtel Limited", "Aircel Limited"};
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, operators);
            dropdown.setAdapter(adapter);
          //  Log.e("error", "no error 1.2");
            getCallDetails();
          //  Log.e("error", "no error 1.3");

            t_local_intra_min.setText("LOCAL INTRA minutes"+local_intra_min);
            t_local_intra_sec.setText("LOCAL INTRA seconds"+local_intra_sec);
            t_std_intra_min.setText("STD INTRA minutes"+std_intra_min);
            t_std_intra_sec.setText("STD INTRA seconds"+std_intra_sec);
            t_local_inter_min.setText("LOCAL INTER minutes"+local_inter_min);
            t_local_inter_sec.setText("LOCAL INTER seconds"+local_inter_sec);
            t_std_inter_min.setText("STD INTER minutes"+std_inter_min);
            t_std_inter_sec.setText("STD INTER seconds"+std_inter_sec);
            db.close();
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    public ArrayList<Number> get_local_sec()
    {
        return locals;
    }
    public ArrayList<Number> get_std_sec()
    {
        return stds;
    }

    public boolean is_leap_year(int yy)
    {
        boolean isLeapYear = ((yy % 4 == 0) && (yy % 100 != 0) || (yy % 400 == 0));

        if (isLeapYear)
        {
            return true;
        }
        else
            return false;
    }
    public void calc_date()
    {
        //current date
        c = Calendar.getInstance();
        Log.e("error","Current time => " + c.getTime());
        yy = c.get(Calendar.YEAR);
        mm = c.get(Calendar.MONTH);
        dd = c.get(Calendar.DAY_OF_MONTH);
        mm=mm+1;
    }
    public void check_date()
    {
        boolean val=false;
        if(dd!=1)
        {
            dd=dd-1;
        }
        else if(mm!=1)
        {
            if(mm!=3)
            {
                dd=days_in_month[mm-2];
                mm=mm-1;
            }
            else
            {
                val=is_leap_year(yy);
                if(val==true)
                {
                    dd=29;
                }
                else
                    dd=28;
                mm=2;
            }
        }
        else
        {
            dd = days_in_month[11];
            mm = 12;
        }
        Log.e("error",String.valueOf(dd));
        Log.e("error",String.valueOf(mm));
        Log.e("error",String.valueOf(yy));
    }
    public void calc_time(String phn, String call,int j)
    {
        // Log.e("error","phn"+phn);
        double minn;
        try
        {
            user_circle1=db.eval(phn,"Circle");
            minn = (Integer.parseInt(call)) / 60.0;
            if (my_circle1.equalsIgnoreCase(user_circle1))
            {
                locals.set(j,locals.get(j).intValue()+ Integer.parseInt(call));
                //  localm[j] = localm[j] + Math.ceil(minn);
                // localm.set(j,localm.get(j).intValue()+ (Math.ceil(minn)));
            }
            else
            {
                stds.set(j,stds.get(j).intValue()+ Integer.parseInt(call));
                // stdm[j] = stdm[j] + Math.ceil(minn);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    public void getGraphDetails(int flag,String mynum2)
    {
        if(flag < locals.size())
            locals.subList(flag, locals.size()).clear();
        if(flag < stds.size())
            stds.subList(flag, stds.size()).clear();

        Log.e("success_in_main", String.valueOf(get_local_sec()));
        for(int i = 0; i < locals.size();i++)
        {
            locals.set(i, 0);
            stds.set(i, 0);
        }
        int i=0;
        Log.e("My num",mynum2);
        Cursor managedCursor=null;

        // set_local_sec(locals,flag);

        // set_std_sec(stds,flag);
//need to set values to 0 everytime
        Log.e("success_in_main", String.valueOf(locals.size()));
        for(int k=locals.size();k<flag;k++)
        {
            locals.add(0);
            stds.add(0);
        }

        try
        {
            calc_date();
            my_circle1 = db.eval(mynum2, "Circle");
            managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                    null, null, null);
            Log.e("mainActivity", "managed Cursor");
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
                int dircode = Integer.parseInt(callType);
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
                if (dir == "OUTGOING")
                {
                    udd = dateStr.substring(0, 2);
                    umm = dateStr.substring(3, 5);
                    uyy = dateStr.substring(6);
                    if (i < flag)
                    {
                        Log.e("error", "check loop" + i);
                        if ((Integer.parseInt(udd) == dd) && (Integer.parseInt(umm) == mm) && (Integer.parseInt(uyy) == yy))
                        {
                            calc_time(phNumber, callDuration, i);
                        }
                        else
                        {
                            check_date();
                            i++;
                        }

                    }
                    else
                    {
                        break;
                    }
                }
            }
        }

        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    private void getCallDetails() {
       //Log.e("error", "no error 2");
        StringBuffer sb = new StringBuffer();
        Cursor managedCursor = managedQuery(CallLog.Calls.CONTENT_URI, null,
                null, null, null);
        Log.e("mainActivity","managed Cursor");
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        //Log.e("error", "no error 2.1");
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
            //Log.e("error", "no error 2.3");
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
                            sb.append(" INTER LOCAL CALL\n");
                            local_inter_sec = local_inter_sec + Integer.parseInt(callDuration);
                            local_inter_min = local_inter_min + Math.ceil(min);
                        }

                        else{
                            sb.append(" INTRA LOCAL CALL\n");
                        local_intra_sec = local_intra_sec + Integer.parseInt(callDuration);
                        local_intra_min = local_intra_min + Math.ceil(min);
                    }
                    }
                    else {
                        if (my_operator.equalsIgnoreCase(user_operator))
                        {
                            std_inter_sec = std_inter_sec + Integer.parseInt(callDuration);
                            std_inter_min = std_inter_min + Math.ceil(min);
                            sb.append(" INTER STD CALL\n");
                        }
                        else{
                           sb.append(" INTRA STD CALL\n");
                        std_intra_sec = std_intra_sec + Integer.parseInt(callDuration);
                        std_intra_min = std_intra_min + Math.ceil(min);
                    }
                    }
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }
        }
        managedCursor.close();
       textView.setText(sb);
    }
}
