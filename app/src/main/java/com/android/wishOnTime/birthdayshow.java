package com.android.wishOnTime;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class birthdayshow extends AppCompatActivity {
    AppCompatButton sendwish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_birthdayshow);
        Intent intent=getIntent();
        String name=intent.getStringExtra("name");
        TextView birthname=findViewById(R.id.birthname);
        birthname.setText(name+"\'s birthday");
        //toolbar
        Toolbar tool=findViewById(R.id.toolbar);
        setSupportActionBar(tool);
        tool.setTitle("WishOnTime");
        tool.setSubtitle("It will remaind you to wish birthday's");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        tool.getNavigationIcon().setTint(getResources().getColor(R.color.white));
        //getting id's
        sendwish=findViewById(R.id.sendwish);
        sendwish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent send=new Intent(Intent.ACTION_SEND);
                send.setType("text/plain");
                send.putExtra(Intent.EXTRA_TEXT,"Many more happy returns of the day\uD83C\uDF89\n\uD83C\uDF89 Wishing you a day filled with love, laughter, and endless joy! \uD83E\uDD73 May all your dreams come true. \uD83D\uDC96\uD83C\uDF82.");
                startActivity(Intent.createChooser(send,"Share via"));
            }
        });


    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==android.R.id.home)
            onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}