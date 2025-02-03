package com.android.wishOnTime;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;


import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Locale;


public class MainActivity extends AppCompatActivity{


    CardView createlayout;
    private Calendar calendar;
    EditText dob,timepic,name;
    TextView error,nobirthday;
    private String namestr,dobstr,timestr,relation="Other";

    //creating storage

    ArrayList<persondetails> data=new ArrayList<>();
    RecyclerView recycle;
    private static final int NOTIFICATION_PERMISSION_CODE = 1001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        SharedPreferences sp=getSharedPreferences("birthday",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        //asking permission for notification

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);
            }
        }

        //getting id's
        Toolbar toolbar=findViewById(R.id.toolbar);
       recycle=findViewById(R.id.recycle);
        timepic=findViewById(R.id.timepic);
        dob=findViewById(R.id.dob);
        Spinner spin=findViewById(R.id.spinner);
        TextView back=findViewById(R.id.back);
        createlayout=findViewById(R.id.createlayout);
        AppCompatButton save=findViewById(R.id.save);
        name=findViewById(R.id.name);
        error=findViewById(R.id.error);
        nobirthday=findViewById(R.id.nobirthday);
        //creating array
        String s[]={"Family","Friend","Other"};
        //setting toolbar
        setSupportActionBar(toolbar);
        toolbar.setTitle("WishOnTime");
        toolbar.setSubtitle("It will remaind you to wish birthday's");
        //closing window of adding birthday's
        back.setOnClickListener(view -> createlayout.setVisibility(View.GONE));
        //setting adapter for relation
        ArrayAdapter adap=new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item,s);
        spin.setAdapter(adap);
        spin.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
              relation = adapterView.getItemAtPosition(i).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
            //nothing
            }
        });
        //setting Date of birth
        calendar=Calendar.getInstance();
        dob.setOnClickListener(view -> showDatePicker());
        //setting time
        timepic.setOnClickListener(view -> showTimePicker());
        //set recycle
        recycle.setLayoutManager(new LinearLayoutManager(this));
        setdata();
        adapter recycleadap=new adapter(data,getApplicationContext());
        recycle.setAdapter(recycleadap);
        if(data.isEmpty()) {
            nobirthday.setVisibility(View.VISIBLE);
        }
        //saving data
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                namestr=name.getText().toString().strip();
                dobstr=dob.getText().toString().strip();
                timestr=timepic.getText().toString().strip();
                if(!namestr.isEmpty()&&!dobstr.isEmpty()&&!timestr.isEmpty()){
                   int i=sp.getInt("temp",0);
                    editor.putString("namestorage"+i,namestr);
                    editor.putString("datestorage"+i,dobstr);
                    editor.putString("relationstorage"+i,relation);
                    editor.putString("timestorage"+i,timestr);
                    editor.putInt("temp",i);
                    editor.apply();
                    //for notification remainder
                    schedulenotification();
                    String dateget=sp.getString("datestorage"+i,null);
                    ArrayList<String> datetake=new ArrayList<>(Arrays.asList(dateget.split("/")));
                    data.add(new persondetails(datetake.get(0),datetake.get(1),sp.getString("namestorage"+i,null),sp.getString("relationstorage"+i,null)));
                    editor.putInt("temp",++i);
                    editor.apply();
                    recycleadap.notifyItemInserted(data.size()-1);
                        Toast.makeText(MainActivity.this, "Data Saved", Toast.LENGTH_SHORT).show();
                        createlayout.setVisibility(View.GONE);
                        nobirthday.setVisibility(View.GONE);
                        name.setText("");
                        dob.setText("");
                        timepic.setText("");
                }
                else {
                    error.setVisibility(View.VISIBLE);
                }

            }
        });
        //calling notification

    }
    public void setdata() {
        SharedPreferences sp=getSharedPreferences("birthday",MODE_PRIVATE);
        SharedPreferences.Editor editor=sp.edit();
        int temp=sp.getInt("temp",-1);
        int i=0;
        while(i<temp)
        {
            int j=0;
            String dateget=sp.getString("datestorage"+i,null);
            ArrayList<String> datetake=new ArrayList<>(Arrays.asList(dateget.split("/")));
           data.add(new persondetails(datetake.get(0),datetake.get(1),sp.getString("namestorage"+i,null),sp.getString("relationstorage"+i,null)));
            i++;
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolmenu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId()==R.id.create)
        {
            error.setVisibility(View.GONE);
            createlayout=findViewById(R.id.createlayout);
            createlayout.setVisibility(View.VISIBLE);
        }
        return super.onOptionsItemSelected(item);
    }
    private void showDatePicker() {
        int year = calendar.get(Calendar.YEAR);
        int month = calendar.get(Calendar.MONTH);
        int day = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, selectedYear, selectedMonth, selectedDay) -> {
                    calendar.set(selectedYear, selectedMonth, selectedDay);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    dob.setText(sdf.format(calendar.getTime()));
                },
                year, month, day
        );

        datePickerDialog.show();
    }
    private void showTimePicker() {
        int hour = calendar.get(Calendar.HOUR_OF_DAY);
        int minute = calendar.get(Calendar.MINUTE);

        TimePickerDialog timePickerDialog = new TimePickerDialog(
                this,
                (view, selectedHour, selectedMinute) -> {
                    // Convert 24-hour time to 12-hour format
                    String amPm = (selectedHour >= 12) ? "PM" : "AM";
                    int hourIn12Format = (selectedHour == 0) ? 12 : (selectedHour > 12) ? selectedHour - 12 : selectedHour;

                    // Format and set time
                    String formattedTime = String.format(Locale.getDefault(), "%02d:%02d %s", hourIn12Format, selectedMinute, amPm);
                    timepic.setText(formattedTime); //  Update `time`
                },
                hour, minute, false // `false` means 12-hour format
        );

        timePickerDialog.show();
    }

void schedulenotification(){
    long yearlyInterval = 365L * 24 * 60 * 60 * 1000;
    SharedPreferences sharedPreferences=getSharedPreferences("birthday",MODE_PRIVATE);
    int i=sharedPreferences.getInt("temp",-1);

ArrayList<String> datediv=new ArrayList<>(Arrays.asList(sharedPreferences.getString("datestorage"+i,null).split("/")));
    int year = Integer.valueOf(datediv.get(2));
    int month = Integer.valueOf(datediv.get(1));
    int day = Integer.valueOf(datediv.get(0));
    ArrayList<String> timediv =new ArrayList<>(Arrays.asList(sharedPreferences.getString("timestorage"+i,null).split(":")));
    int hour =Integer.valueOf(timediv.get(0));
    ArrayList<String> timemin=new ArrayList<>(Arrays.asList(timediv.get(1).split(" ")));
    int minute=Integer.valueOf(timemin.get(0));
if(year != 0 && month != 0 && day != 0)
{
    Calendar calendar = Calendar.getInstance();
    calendar.set(Calendar.YEAR, year);
    calendar.set(Calendar.MONTH, month);
    calendar.set(Calendar.DAY_OF_MONTH, day);
    calendar.set(Calendar.HOUR_OF_DAY, hour);
    calendar.set(Calendar.MINUTE, minute);
    calendar.set(Calendar.SECOND, 0);
}
    if (calendar.before(Calendar.getInstance())) {
        calendar.add(Calendar.YEAR, 1);
    }

    Intent intent = new Intent(this, NotificationReceiver.class);
    intent.putExtra("name",sharedPreferences.getString("namestorage"+i,null));
    PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT| PendingIntent.FLAG_IMMUTABLE);
    AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
    Log.e("alarmmanager",""+calendar.getTime());

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), yearlyInterval, pendingIntent);
   // alarmManager.set(AlarmManager.RTC_WAKEUP,System.currentTimeMillis()+(10*1000),pendingIntent);
        Log.e("sucess","to set alarm");





}

}