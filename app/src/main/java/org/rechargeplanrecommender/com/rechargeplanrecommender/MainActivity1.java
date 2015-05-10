package org.rechargeplanrecommender.com.rechargeplanrecommender;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class MainActivity1 extends Activity
{

    EditText  e_my_num;
    TextView t_local_intra_min,t_local_intra_sec,t_std_intra_min,t_std_intra_sec,t_my_circle,t_my_operator,
            t_local_inter_min,t_local_inter_sec,t_std_inter_min,t_std_inter_sec, t_user_circle,t_user_operator;
    TextView textView = null;
    MySQLiteHelper db;
    private String my_circle1;
    private String user_circle1;
    int dd,mm,yy,days_in_month[]={31,28,31,30,31,30,31,31,30,31,30,31};;
    public  String phNumber=null, callType=null ,callDate=null, callDuration=null,dateStr,udd,umm,uyy;
    Date callDayTime;
     public Calendar c ;
    String dir = null;
    ArrayList<Number> locals = new ArrayList<Number>();
    ArrayList<Number> stds=new ArrayList<Number>();
    Login login=new Login();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);

        db= new MySQLiteHelper(this);



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



    public void calc_current_date()
    {
        //current date
        c = Calendar.getInstance();
      //  Log.e("error","Current time => " + c.getTime());
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
    }
    public void calc_time(String phn, String call,int j)
    {
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
        for(int i = 0; i < locals.size();i++)
        {
            locals.set(i, 0);
            stds.set(i, 0);
        }
        int i=0;
        Cursor managedCursor=null;

        for(int k=locals.size();k<flag;k++)
        {
            locals.add(0);
            stds.add(0);
        }

        try
        {
            calc_current_date();
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
}
