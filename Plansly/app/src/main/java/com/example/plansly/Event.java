package com.example.plansly;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class Event extends AppCompatActivity implements startEventCalendarFragment.OnInputListener, startTime_fragment.timeOnClickInterface,
        endEventCalendarFragment.OnInputListener,endTime_fragment.timeOnClickInterface,remindFragment.remindOnClickInterface{
    private String TAG = "plannerEventActivity";
    private TextView startDate;
    private TextView endDate;
    private TextView startTime;
    private TextView endTime;
    private TextView note;
    private TextView eventGoal;

    private ActionBar actionBar;
    private DialogFragment startEventFragment;
    private DialogFragment startTimeFragment;

    private DialogFragment remind_Fragment;
    private TextView remindMe;
    private TextView location;

    private DialogFragment endEventFragment;
    private DialogFragment endTimeFragment;

    private String[] spinnerOptions;
    private ArrayAdapter<String> adapter;

    private boolean saved;
    private DialogFragment confirmSavingFragment;

    public String getStartDate()
    {
        return startDate.getText().toString();
    }

    public String getEndDate()
    {
        return endDate.getText().toString();
    }

    public String getNote()
    {
        return note.getText().toString();
    }

    public String getLocation()
    {
        return location.getText().toString();
    }

    public String getEvent()
    {
        return eventGoal.getText().toString();
    }


    public String getStartTime()
    {
        return startTime.getText().toString();
    }

    public String getEndTime()
    {
        return endTime.getText().toString();
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event);

        //Action bar
        actionBar = getSupportActionBar();

        //Set color of action bar
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#196bf9"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Add Event");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);


        startEventFragment = new startEventCalendarFragment();
        startTimeFragment = new startTime_fragment();
        startTime = findViewById(R.id.startTime);

        endEventFragment = new endEventCalendarFragment();
        endTimeFragment = new endTime_fragment();
        endTime = findViewById(R.id.endTime);

        remind_Fragment = new remindFragment();
        remindMe = findViewById(R.id.remindMe);

        startDate = findViewById(R.id.startCalendar);
        endDate = findViewById(R.id.endCalendar);
        note = findViewById(R.id.eventNote);
        location = findViewById(R.id.location);
        eventGoal = findViewById(R.id.eventGoal);
        saved = false;
        confirmSavingFragment = new confirm_saving_fragment();

        //Retrieves info from CompareCardService
        Bundle cardServiceData = getIntent().getExtras();
        if (cardServiceData != null)
        {
            if (cardServiceData.get("Event") != null && cardServiceData.get("startDate") != null && cardServiceData.get("startTime") != null && cardServiceData.get("eventNote") != null &&
                    cardServiceData.get("endDate") != null && cardServiceData.get("endTime") != null && cardServiceData.get("location") != null && cardServiceData.get("remindMe") != null)
            {
                eventGoal.setText(cardServiceData.get("Event").toString());
                startDate.setText(cardServiceData.get("startDate").toString());
                startTime.setText(cardServiceData.get("startTime").toString());
                endDate.setText(cardServiceData.get("endDate").toString());
                endTime.setText(cardServiceData.get("endTime").toString());
                note.setText(cardServiceData.get("eventNote").toString());
                location.setText(cardServiceData.get("location").toString());
                remindMe.setText(cardServiceData.get("remindMe").toString());
            }
        }

        //saves unedited values so they can be used to compare with edited values
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();

        if (eventGoal.getText().toString().length() > 0) {
            editor.putString("taskEvent", eventGoal.getText().toString());
            editor.apply();
        }

        if (startDate.getText().toString().length() > 0) {
            editor.putString("startDate", startDate.getText().toString());
            editor.apply();
        }
        if (endDate.getText().toString().length() > 0) {
            editor.putString("endDate", endDate.getText().toString());
            editor.apply();
        }
        if (note.getText().toString().length() > 0) {
            editor.putString("note", note.getText().toString());
            editor.apply();
        }
        if (location.getText().toString().length() > 0) {
            editor.putString("location", location.getText().toString());
            editor.apply();
        }
        if (startTime.getText().toString().length() > 0) {
            editor.putString("startTime", startTime.getText().toString());
            editor.apply();
        }
        if (endTime.getText().toString().length() > 0) {
            editor.putString("endTime", endTime.getText().toString());
            editor.apply();
        }

        //removes none from text field
        if (note.getText().toString().equals("none"))
            note.setText("");

        if (location.getText().toString().equals("None"))
            location.setText("");

        if (remindMe.getText().toString().equals("Never"))
            remindMe.setText("");


    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    //Pressing back on phone
    @Override
    public void onBackPressed() {
        if (!saved)
        {
            confirmSavingFragment.show(getSupportFragmentManager(), TAG);

        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (startDate.getText().length() != 0 && startTime.getText().length() != 0 && endDate.getText().length() != 0 && endTime.getText().length() != 0 && eventGoal.getText().length() != 0)
        {
            outState.putString("startDate",startDate.getText().toString());
            outState.putString("startTime", startTime.getText().toString());
            outState.putString("endDate",endDate.getText().toString());
            outState.putString("endTime", endTime.getText().toString());
            outState.putString("eventGoal", eventGoal.getText().toString());

            if (note.getText().length() != 0)
                outState.putString("endNote", note.getText().toString());
            if (location.getText().length() != 0)
                outState.putString("location", location.getText().toString());
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState != null)
        {
            startDate.setText(savedInstanceState.getString("startDate"));
            startTime.setText(savedInstanceState.getString("startTime"));
            endDate.setText(savedInstanceState.getString("endDate"));
            endTime.setText(savedInstanceState.getString("endTime"));
            eventGoal.setText(savedInstanceState.getString("eventGoal"));

            if (savedInstanceState.getString("endNote") != null)
            {
                note.setText(savedInstanceState.getString("endNote"));
            }
            if (savedInstanceState.getString("location") != null)
            {
                location.setText(savedInstanceState.getString("location"));
            }
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        //Back button
        if (item.getItemId() == android.R.id.home)
        {
            if (!saved)
            {
                confirmSavingFragment.show(getSupportFragmentManager(), TAG);

            }
            return false;
        }

        //Save button
        else if(item.getItemId() == R.id.save_task_event)
        {
            Intent saveToFileService = new Intent(this, SaveToFileService.class);

            if (startDate.getText().length() != 0 && startTime.getText().length() != 0 && endDate.getText().length() != 0 && endTime.getText().length() != 0 && eventGoal.getText().length() != 0)
            {
                saveToFileService.putExtra("startDate",startDate.getText().toString());
                saveToFileService.putExtra("startTime", startTime.getText().toString());
                saveToFileService.putExtra("endDate",endDate.getText().toString());
                saveToFileService.putExtra("endTime", endTime.getText().toString());
                saveToFileService.putExtra("eventGoal", eventGoal.getText().toString());

                if (note.getText().length() != 0)
                    saveToFileService.putExtra("endNote", note.getText().toString());
                if (location.getText().length() != 0)
                    saveToFileService.putExtra("location", location.getText().toString());
                if (remindMe.getText().length() != 0)
                    saveToFileService.putExtra("remindMe", remindMe.getText().toString());

                saveToFileService.putExtra("calling-Activity","event");
                startService(saveToFileService);
                finish();
                saved = true;
                Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();
                return false;

            }
            else
            {
                Toast.makeText(this, "Event, Date's, & Time's must be filled", Toast.LENGTH_LONG).show();
            }


        }
        return super.onOptionsItemSelected(item);
    }

    //region confirm saving fragment methods
    public void saveSureClicked(View view)
    {
        confirmSavingFragment.dismiss();
        finish();
    }

    public void saveCancelClicked(View view)
    {
        confirmSavingFragment.dismiss();
    }
    //endregion


    //region Start calendar/time code

    // Start Calendar label clicked
    public void startCalendarClicked(View view)
    {
        startEventFragment.show(getSupportFragmentManager(),"startEventCalendarFragment");
    }

    //Save button clicked
    public void saveClicked(View view)
    {
        startEventCalendarFragment.mOnInputListener.sendInput(startEventCalendarFragment.date);
        startEventFragment.getDialog().dismiss();
    }

    //Receives input from startCalendar fragment
    @Override
    public void sendInput(String input) {
        Log.d(TAG,"sendInput: got the input: " + input);
        startDate.setText(input);

    }
    //Time label clicked
    public void startTimeClicked(View view)
    {
        startTimeFragment.show(getSupportFragmentManager(),"startTimeFragment");
    }

    //Save button on start time clicked
    public void saveTimeClicked(View view)
    {
        startTime_fragment.timeInterface.sendStartTime(startTime_fragment.hour,startTime_fragment.minutes,startTime_fragment.timeZone);
        startTimeFragment.getDialog().dismiss();
    }


    //Cancel button on start time clicked
    public void cancelClicked(View view)
    {
        startTimeFragment.getDialog().dismiss();
    }

    @Override
    public void sendStartTime(String hour, String minutes, String timeZone) {
        Log.d(TAG,"sendTime: got the time: " + hour+":"+minutes+" "+timeZone);
        startTime.setText(hour+":"+minutes+" "+timeZone);
    }

    //endregion

    //region End calendar/time code

    // Start Calendar label clicked
    public void endCalendarClicked(View view)
    {
        endEventFragment.show(getSupportFragmentManager(),"endEventCalendarFragment");
    }

    //Save button clicked on end time fragment
    public void saveEndTimeClicked(View view)
    {
        endTime_fragment.timeInterface.sendEndTime(endTime_fragment.hour,endTime_fragment.minutes,endTime_fragment.timeZone);
        endTimeFragment.getDialog().dismiss();
    }

    //Save button clicked on end date fragment
    public void saveEndDateClicked(View view)
    {
        endEventCalendarFragment.mOnInputListener.sendEndInput(endEventCalendarFragment.date);
        endEventFragment.getDialog().dismiss();
    }

    //sets end date label
    @Override
    public void sendEndInput(String input) {
        Log.d(TAG,"sendEndInput: got the input: " + input);
        endDate.setText(input);

    }

    //End time label clicked
    public void endTimeClicked(View view)
    {
        endTimeFragment.show(getSupportFragmentManager(),"endTimeFragment");
    }

    //Cancel clicked on end time fragment
    public void endCancelClicked(View view)
    {
        endTimeFragment.getDialog().dismiss();
    }

    //sets end time label text
    @Override
    public void sendEndTime(String hour, String minutes, String timeZone) {
        Log.d(TAG,"sendEndTime: got the time: " + hour+":"+minutes+" "+timeZone);
        endTime.setText(hour+":"+minutes+" "+timeZone);
    }
    //endregion

    //region Remind me methods
    public void remindMeClicked(View view)
    {
        remind_Fragment.show(getSupportFragmentManager(),"remindFragment");
    }

    public void remindSaveTimeClicked(View view)
    {
        if (remindFragment.remindTimeEditText.getText().toString().length() > 0) {
            remindFragment.remindInterface.sendRemindTime(remindFragment.remindTimeEditText.getText().toString(), remindFragment.unitOfTime);
            remind_Fragment.getDialog().dismiss();
        }
        else
            Toast.makeText(this, "Fill text field.", Toast.LENGTH_SHORT).show();
    }

    public void remindCancelClicked(View view)
    {
        remind_Fragment.getDialog().dismiss();
    }

    @Override
    public void sendRemindTime(String time, String unitOfTime) {
        remindMe.setText(time+" "+unitOfTime);
    }
    //endregion
}
