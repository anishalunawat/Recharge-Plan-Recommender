package org.rechargeplanrecommender.com.rechargeplanrecommender;

import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

public class Local_std_min
{
    public static void main(String[] args)
    {
        BufferedReader br=new BufferedReader(new InputStreamReader(System.in));
        PreparedStatement psmt=null;

        try
        {
            String user_num[]=new String[5];
            int call_duration[]=new int[5],local_sec=0,std_sec=0;
            double local_min=0.0,std_min=0.0,min=0;

            Log.e("hello", "hw r u");
            String my_num="",my_circle="",my_operator="",user_circle="",user_operator="";
            System.out.println("Enter your mobile no.");
            my_num=br.readLine();
            my_circle=eval(my_num,"Circle");
            my_operator=eval(my_num,"Operator");
            System.out.println("Enter user's mobile number and call duration");
            for(int i=0;i<1;i++)
            {
                user_num[i]=br.readLine();
                call_duration[i]=Integer.parseInt(br.readLine());
            }
            for(int i=0;i<1;i++)
            {
                user_circle=eval(user_num[i],"Circle");
                user_operator=eval(user_num[i],"Operator");
                min=call_duration[i]/60.0;
                if(my_circle.equalsIgnoreCase(user_circle))
                {
                    if(my_operator.equalsIgnoreCase(user_operator))
                        System.out.println(" INTER LOCAL CALL");
                    else
                        System.out.println(" INTRA LOCAL CALL");
                    local_sec=local_sec+call_duration[i];
                    local_min=local_min+Math.ceil(min);
                }
                else
                {
                    if(my_operator.equalsIgnoreCase(user_operator))
                        System.out.println(" INTER STD CALL");
                    else
                        System.out.println(" INTRA STD CALL");
                    std_sec=std_sec+call_duration[i];
                    std_min=std_min+Math.ceil(min);
                }
            }
            System.out.println("LOCAL seconds"+local_sec);
            System.out.println("STD seconds"+std_sec);
            System.out.println("LOCAL minutes"+local_min);
            System.out.println("STD minutes"+std_min);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static String eval(String user_num,String find)
    {
        Connection connection = null;
        ResultSet resultset = null;
        boolean result=false,flag=false;
        PreparedStatement psmt=null;
        String code1="",code2="",code3="",user_circle="",user_operator="",t="";
        int count=0;
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
            Class.forName("org.sqlite.JDBC");
            connection = DriverManager.getConnection("jdbc:sqlite:D:/ishita/cloud_project/operator_details.sqlite");
            psmt=connection.prepareStatement("SELECT Circle,Operator FROM operator where Operator_Level=?"  );
            psmt.setString(1,code1);
            result = psmt.execute();
            if(result)
            {
                resultset = psmt.getResultSet();
                if(resultset.next())
                {
                    if(find=="Circle")
                    {
                        System.out.println(resultset.getString("Circle"));
                        user_circle=resultset.getString("Circle");
                        return (user_circle);
                    }
                    else if(find=="Operator")
                    {
                        System.out.println(resultset.getString("Operator"));
                        user_operator=resultset.getString("Operator");
                        return user_operator;
                    }
                    while(resultset.next())
                    {
                        System.out.println(resultset.getString("Circle")+"\n"+resultset.getString("Operator"));
                        user_circle=resultset.getString("Circle");
                        user_operator=resultset.getString("Operator");
                    }
                }
                else
                {
                    if(flag==true)
                        code2=user_num.substring(3,7);
                    else
                        code2=user_num.substring(0,4);
                    psmt=connection.prepareStatement("SELECT Circle,Operator FROM operator where Operator_Level=?"  );
                    psmt.setString(1,code2);
                    result = psmt.execute();
                    if(result)
                    {
                        resultset = psmt.getResultSet();
                        if(resultset.next())
                        {
                            if(find=="Circle")
                            {
                                System.out.println(resultset.getString("Circle"));
                                user_circle=resultset.getString("Circle");
                                return (user_circle);
                            }
                            else if(find=="Operator")
                            {
                                System.out.println(resultset.getString("Operator"));
                                user_operator=resultset.getString("Operator");
                                return user_operator;
                            }
                            while(resultset.next())
                            {
                                System.out.println(resultset.getString("Circle")+"\n"+resultset.getString("Operator"));
                                user_circle=resultset.getString("Circle");
                                user_operator=resultset.getString("Operator");
                            }
                        }
                        else
                        {
                            if(flag==true)
                                code2=user_num.substring(3,8);
                            else
                                code3=user_num.substring(0,5);
                            psmt=connection.prepareStatement("SELECT Circle,Operator FROM operator where Operator_Level=?"  );
                            psmt.setString(1,code3);
                            result = psmt.execute();
                            if (result)
                            {
                                resultset = psmt.getResultSet();
                                if(resultset.next())
                                {
                                    if(find=="Circle")
                                    {
                                        System.out.println(resultset.getString("Circle"));
                                        user_circle=resultset.getString("Circle");
                                        return (user_circle);
                                    }
                                    else if(find=="Operator")
                                    {
                                        System.out.println(resultset.getString("Operator"));
                                        user_operator=resultset.getString("Operator");
                                        return user_operator;
                                    }
                                    while(resultset.next())
                                    {
                                        System.out.println(resultset.getString("Circle")+"\n"+resultset.getString("Operator"));
                                        user_circle=resultset.getString("Circle");
                                        user_operator=resultset.getString("Operator");
                                    }
                                }
                                else
                                {
                                    count = psmt.getUpdateCount();
                                    if (count >= 0)
                                    {
                                        System.out.println("DDL or update data displayed here.");
                                    }
                                }
                            }
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
                resultset.close();
                psmt.close();
                connection.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        //resultset.close();
        //connection.close();
        return user_circle;
    }
}
