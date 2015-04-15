package org.rechargeplanrecommender.com.rechargeplanrecommender; /**
 * Created by DELL on 31-03-2015.
 */


import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.IOException;
import java.sql.SQLException;

public class MySQLiteHelper extends SQLiteOpenHelper
{
    SQLiteDatabase db;
    public Context mContext;
    DataBaseHelper myDbHelper;
    Cursor cursor;
    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "operator_details";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.mContext=context;
        //myDbHelper = new DataBaseHelper();
        myDbHelper = new DataBaseHelper(mContext);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
    public String eval(String user_num, String find) throws SQLException
    {
        boolean result=false;
        String code1="",code2="",code3="",user_circle="",user_operator="",t="";
        int count=0,flag=10;
        String[] COLUMNS = new String[]{"Circle","Operator"};

        try
        {

            myDbHelper.createDataBase();

        }
        catch (IOException ioe)
        {

            throw new Error("Unable to create database");

        }

        try
        {
            myDbHelper.openDataBase();
        }
        catch(SQLException sqle)
        {
            throw sqle;
        }
        try
        {
            t=user_num.substring(0,1);

           // Log.e("error",t);
           if(t.equalsIgnoreCase("0"))
           {
                code1=user_num.substring(1,4);
               flag=1;
           }
           else if(t.equals("+"))
            {
                code1=user_num.substring(3,6);
                //Log.e("error",t);
                flag=2;
            }
            else
                code1=user_num.substring(0,3);
            //Log.e("error","database");
            //  get reference to readable DB
            db = myDbHelper.getReadableDatabase();
             cursor =
                    db.query("operator", // a. table
                            COLUMNS, // b. column names
                            " _id = ?", // c. selections
                            new String[] { String.valueOf(code1) }, // d. selections args
                            null, // e. group by
                            null, // f. having
                            null, // g. order by
                            null);
            //Log.e("error","no error4");
            if(cursor.moveToFirst())
            {
                //Log.e("error","no error4.1");
                if (find == "Circle")
                {
                     user_circle = cursor.getString(0);
                    return (user_circle);
                }
                else if (find == "Operator")
                {
                    user_operator = cursor.getString(1);
                    return user_operator;
                }
            }
            else
            {
                    if(flag==1)
                        code2=user_num.substring(1,5);
                    else if(flag==2)
                        code2=user_num.substring(3,7);
                    else {

                        Log.e("USER NUM", user_num);
                        code2 = user_num.substring(0, 4);

                    }db = myDbHelper.getReadableDatabase();
                    cursor =db.query("operator", // a. table
                                COLUMNS, // b. column names
                                " _id = ?", // c. selections
                                new String[] { String.valueOf(code2) }, // d. selections args
                                null, // e. group by
                                null, // f. having
                                null, // g. order by
                                null);
                    if(cursor.moveToFirst())
                    {
                          //Log.e("error", "error4.2");
                        if (find == "Circle")
                        {
                            user_circle = cursor.getString(0);
                            return (user_circle);
                        }
                        else if (find == "Operator")
                        {
                            user_operator = cursor.getString(1);
                            return user_operator;
                        }
                    }
                        else
                        {
                            if(flag==1)
                                code3=user_num.substring(1,6);
                           else if(flag==2)
                                code3=user_num.substring(3,8);
                            else
                                code3=user_num.substring(0,5);
                            db = myDbHelper.getReadableDatabase();
                            cursor =db.query("operator", // a. table
                                            COLUMNS, // b. column names
                                            " _id = ?", // c. selections
                                            new String[] { String.valueOf(code3) }, // d. selections args
                                            null, // e. group by
                                            null, // f. having
                                            null, // g. order by
                                            null);
                            if (cursor.moveToFirst())
                            {
                                 // Log.e("error", "error4.3");
                                if (find == "Circle")
                                {
                                    user_circle = cursor.getString(0);
                                    return (user_circle);
                                }
                                else if (find == "Operator")
                                {
                                    user_operator = cursor.getString(1);
                                    return user_operator;
                                }
                            }
                        }
                    }
                }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        finally
        {
            try
            {
                myDbHelper.close();
                cursor.close();
                db.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return user_circle;
    }
}
