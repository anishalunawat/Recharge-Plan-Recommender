package org.rechargeplanrecommender.com.rechargeplanrecommender;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

/**
 * Created by NOKIA ASHA on 27/03/2015.
 */
public class PlanDescription extends Activity {


    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.plan_description);
        Intent intent = getIntent();

        TextView textView = ((TextView)findViewById(R.id.recharge_value));
        String value  = intent.getStringExtra("value");
        textView.setText(value);
        TextView textView1 = ((TextView)findViewById(R.id.operator_master));
        String operator  = intent.getStringExtra("operator");
        textView1.setText(operator);
        TextView textView2 = ((TextView)findViewById(R.id.recharge_validity));
        String validity  = intent.getStringExtra("validity");
        textView2.setText(validity);

    }
}
