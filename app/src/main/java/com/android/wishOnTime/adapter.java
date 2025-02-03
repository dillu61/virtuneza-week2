package com.android.wishOnTime;

import static android.content.Context.MODE_PRIVATE;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;

public class adapter extends RecyclerView.Adapter<adapter.viewhold> {
    ArrayList<persondetails> data;
    Context context;

       public adapter(ArrayList<persondetails> data, Context context){
            this.data=data;
            this.context=context;
        }
    @NonNull
    @Override
    public adapter.viewhold onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v= LayoutInflater.from(parent.getContext()).inflate(R.layout.adapgroup,parent,false);
        return new viewhold(v);
    }

    @Override
    public void onBindViewHolder(@NonNull adapter.viewhold holder, int position) {
           holder.personname.setText(data.get(position).namestr);
           holder.relation.setText(data.get(position).relation);
           holder.date.setText(data.get(position).datestr);
           String datestr=data.get(position).monthstr;

           int month=0;
        try {
            month = Integer.parseInt(datestr);
            // Proceed with your switch statement
        } catch (NumberFormatException e) {
            // Handle invalid month
            month=0;
        }
        String[] months= {"JAN", "FEB", "MAR", "APR", "MAY", "JUN", "JUL", "AUG", "SEP", "OCT", "NOV","DEC"};
        holder.month.setText(months[month-1]);
        holder.itemView.setOnLongClickListener(view -> {
            new AlertDialog.Builder(view.getContext())
                    .setTitle("Delete Entry")
                    .setMessage("Are you sure you want to delete this entry?")
                    .setPositiveButton("Yes", (dialog, which) -> {
                        deleteitem(position); // Call the delete function
                    })
                    .setNegativeButton("No", null)
                    .show();

            return true; // Indicate the event was handled
        });
        holder.itemView.setOnClickListener(view -> {
            callintent(position,view);
        });

    }

    @Override
    public int getItemCount() {
        return data != null ? data.size() : 0;
    }
    public class viewhold extends RecyclerView.ViewHolder{
        View v;
        TextView date,month,personname,relation;
        viewhold(View v){
            super(v);
            this.v=v;
            date=v.findViewById(R.id.date);
            month=v.findViewById(R.id.month);
            personname=v.findViewById(R.id.nameid);
            relation=v.findViewById(R.id.relation);
        }
    }
    private void deleteitem(int position)
    {
        if(position >= 0 && position < data.size())
        {
            SharedPreferences sp=context.getSharedPreferences("birthday",context.MODE_PRIVATE);
            SharedPreferences.Editor editor=sp.edit();
            String name = sp.getString("namestorage" + position, null);
            String date = sp.getString("datestorage" + position, null);
            String time = sp.getString("timestorage" + position, null);

            // Cancel the alarm
            if (date != null && time != null) {
                cancelAlarm(name, date, time);
            }
            int i=sp.getInt("temp",-1)-1;
            editor.remove("namestorage"+position);
            editor.remove("datestorage" + position);
            editor.remove("relationstorage" + position);
            editor.remove("timestorage" + position);
            editor.apply();
            data.remove(position);
            notifyDataSetChanged();
            Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
            if(i>=0) {
                editor.putString("namestorage" + position, sp.getString("namestorage" + i, null));
                editor.putString("datestorage" + position, sp.getString("datestorage" + i, null));
                editor.putString("relationstorage" + position, sp.getString("relationstorage" + i, null));
                editor.putString("timestorage" + position, sp.getString("timestorage" + i, null));
                editor.remove("namestorage" + i);
                editor.remove("datestorage" + i);
                editor.remove("relationstorage" + i);
                editor.remove("timestorage" + i);
                editor.putInt("temp",i);
                editor.apply();

            }


        }
    }
private void callintent(int position,View v)
{
    Calendar calendar = Calendar.getInstance();
    int day = calendar.get(Calendar.DAY_OF_MONTH);
    int month = calendar.get(Calendar.MONTH) + 1;
    if(day==Integer.valueOf(data.get(position).datestr)&&month==Integer.valueOf(data.get(position).monthstr))
    {
        Intent it=new Intent(v.getContext(), birthdayshow.class);
        it.putExtra("name",data.get(position).namestr);
       v.getContext().startActivity(it);
    }
    else
        Toast.makeText(context, "Long click to delete", Toast.LENGTH_SHORT).show();
}



    private void cancelAlarm(String name, String date, String time) {
        Intent intent = new Intent(context, NotificationReceiver.class);
        intent.putExtra("name", name);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                context,
                name.hashCode(), // Unique request code
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        if (alarmManager != null) {
            alarmManager.cancel(pendingIntent);
            Log.e("AlarmManager", "Alarm canceled for " + name);
        }
    }
    
}
