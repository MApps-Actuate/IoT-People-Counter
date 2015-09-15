package com.opentext.activitytracker;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.facebook.FacebookSdk;
import com.opentext.otiotwear.R;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login);

        findViewById(R.id.googleSignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNew("gp");
            }
        });

        findViewById(R.id.btnNoSignin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNew("none");
            }
        });
    }
    private void startNew(String activity) {
        Bundle b = new Bundle();
        b.putInt("Activity", 0);

        if(activity == "none") {
            startActivity(new Intent(this, MainActivity.class).putExtras(b));
        }else if(activity == "fb") {
            startActivity(new Intent(this, MainActivity.class).putExtras(b));
        }else if(activity == "gp") {
            startActivity(new Intent(this, MainActivity.class).putExtras(b));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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
}
