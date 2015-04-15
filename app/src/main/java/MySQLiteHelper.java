/**
 * Created by DELL on 31-03-2015.
 */


import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.LinkedList;
import java.util.List;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class MySQLiteHelper extends SQLiteOpenHelper
{

    // Database Version
    private static final int DATABASE_VERSION = 1;
    // Database Name
    private static final String DATABASE_NAME = "operator_details";

    public MySQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    //SQLiteDatabase mydatabase = SQLiteDatabase.openDatabase("D:/ishita/cloud_project/operator_details.sqlite", null, 0);

    public String eval(String user_num, String find)
    {
        Log.e("error","no error");
        Connection connection = null;
        ResultSet resultset = null;
        boolean result=false,flag=false;
        PreparedStatement psmt=null;
        String code1="",code2="",code3="",user_circle="",user_operator="",t="";
        int count=0;
        String[] COLUMNS = {"Circle","Operator"};

        t=user_num.substring(0,0);
        try
        {
            if(t.equals("+"))
            {
                code1=user_num.substring(3,6);
                flag=true;
            }
            else
                code1=user_num.substring(0,3);
            Log.e("error","no error2");
            SQLiteDatabase mydatabase = SQLiteDatabase.openDatabase("D:/ishita/cloud_project/operator_details.sqlite", null, 0);
            //Class.forName("org.sqlite.JDBC");
            Log.e("error","no error3");

            // 1. get reference to readable DB
            SQLiteDatabase db = this.getReadableDatabase();
            Cursor cursor =
                    db.query("operator", // a. table
                            COLUMNS, // b. column names
                            " Operator_Level = ?", // c. selections
                            new String[] { String.valueOf(code1) }, // d. selections args
                            null, // e. group by
                            null, // f. having
                            null, // g. order by
                            null);
            //connection = DriverManager.getConnection("jdbc:sqlite:D:/ishita/cloud_project/operator_details.sqlite");
            // psmt=connection.prepareStatement("SELECT Circle,Operator FROM operator where Operator_Level=?"  );
            //psmt.setString(1,code1);
            //result = psmt.execute();
            if(cursor !=null)
            {
                cursor.moveToFirst();
                if (find == "Circle") {
                   // System.out.println(resultset.getString("Circle"));
                    user_circle = cursor.getString(1);
                    return (user_circle);
                } else if (find == "Operator") {
                   // System.out.println(resultset.getString("Operator"));
                    user_operator = cursor.getString(2);
                    return user_operator;
                }
                db.close();
            }
            else
            {
                    Log.e("error","no error4");
                    if(flag==true)
                        code2=user_num.substring(3,7);
                    else
                        code2=user_num.substring(0,4);
                 db = this.getReadableDatabase();
                cursor =
                        db.query("operator", // a. table
                                COLUMNS, // b. column names
                                " Operator_Level = ?", // c. selections
                                new String[] { String.valueOf(code2) }, // d. selections args
                                null, // e. group by
                                null, // f. having
                                null, // g. order by
                                null);
                   // psmt=connection.prepareStatement("SELECT Circle,Operator FROM operator where Operator_Level=?"  );
                   // psmt.setString(1,code2);
                    //result = psmt.execute();
                    if(cursor !=null)
                    {
                        cursor.moveToFirst();

                        if (find == "Circle") {
                            Log.e("error", "error10");
                            // System.out.println(cursor.getString(1));
                            user_circle = cursor.getString(1);
                            return (user_circle);
                        } else if (find == "Operator") {
                            //System.out.println(resultset.getString("Operator"));
                            user_operator = cursor.getString(2);
                            return user_operator;
                        }
                        db.close();

                    }
                        else
                        {
                            if(flag==true)
                                code2=user_num.substring(3,8);
                            else
                                code3=user_num.substring(0,5);
                            db = this.getReadableDatabase();
                            cursor =
                                    db.query("operator", // a. table
                                            COLUMNS, // b. column names
                                            " Operator_Level = ?", // c. selections
                                            new String[] { String.valueOf(code3) }, // d. selections args
                                            null, // e. group by
                                            null, // f. having
                                            null, // g. order by
                                            null);
                          //  psmt=connection.prepareStatement("SELECT Circle,Operator FROM operator where Operator_Level=?"  );
                            //psmt.setString(1,code3);
                            //result = psmt.execute();
                            if (cursor !=null)
                            {
                                cursor.moveToFirst();

                                if (find == "Circle") {
                                    // System.out.println(resultset.getString("Circle"));
                                    user_circle = cursor.getString(1);
                                    return (user_circle);
                                } else if (find == "Operator") {
                                    //System.out.println(resultset.getString("Operator"));
                                    user_operator = cursor.getString(2);
                                    return user_operator;
                                }
                                db.close();
                            }
                        }
                    }
                }
        catch (Exception e)
        {
            e.printStackTrace();
        }
        /*finally
        {
            try
            {

                db.close();
                //resultset.close();
               // psmt.close();
                //connection.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }*/

        return user_circle;
    }


}
