package com.example.plansly;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.preference.PreferenceManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.zip.Inflater;

public class planner extends AppCompatActivity implements calendarFragment.OnInputListener,setRepeatFragment.repeatInputListener,time_fragment.timeOnClickInterface{
    private String TAG = "plannerActivity";
    private TextView date;
    private TextView repNev;
    private TextView task;
    private TextView note;

    private TextView plannerTime;


    public String getDate()
    {
        return date.getText().toString();
    }

    public String getRepNev()
    {
        return repNev.getText().toString();
    }

    public String getTask()
    {
        return task.getText().toString();
    }

    public String getNote()
    {
        return note.getText().toString();
    }

    public String getTime()
    {
        return plannerTime.getText().toString();
    }

    private String[] spinnerOptions;

    //ctl + alt + t to create regions
    private ActionBar actionBar;
    private DialogFragment calFragment;
    private DialogFragment repFragment;
    private DialogFragment setRepFragment;
    private DialogFragment timeFragment;
    private DialogFragment confirmSavingFragment;

    private ArrayAdapter<String> adapter;
    private boolean saved;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_planner);

        //Action bar
        actionBar = getSupportActionBar();

        //Set color of action bar
        ColorDrawable colorDrawable = new ColorDrawable(Color.parseColor("#196bf9"));
        actionBar.setBackgroundDrawable(colorDrawable);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle("Add Task");
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_arrow_back);

        date = findViewById(R.id.date);
        repNev = findViewById(R.id.repeat);
        task = findViewById(R.id.editText);
        note = findViewById(R.id.note);

        calFragment = new calendarFragment();
        repFragment = new repeatFragment();
        setRepFragment = new setRepeatFragment();
        timeFragment = new time_fragment();
        confirmSavingFragment = new confirm_saving_fragment();


        plannerTime = findViewById(R.id.plannerTime);
        saved = false;
        //Retrieves info from CompareCardService
        Bundle cardServiceData = getIntent().getExtras();

        if (cardServiceData != null) {
            if (cardServiceData.get("Date") != null && cardServiceData.get("Time") != null && cardServiceData.get("Task") != null &&
                    cardServiceData.get("Note") != null && cardServiceData.get("Repeat") != null) {
                task.setText(cardServiceData.get("Task").toString());
                date.setText(cardServiceData.get("Date").toString());
                plannerTime.setText(cardServiceData.get("Time").toString());
                note.setText(cardServiceData.get("Note").toString());
                repNev.setText(cardServiceData.get("Repeat").toString());
            }
        }

        //saves unedited values so they can be used to compare with edited values
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = prefs.edit();
        if (date.getText().toString().length() > 0) {
            editor.putString("date", date.getText().toString());
            editor.apply();
        }
        if (task.getText().toString().length() > 0) {
            editor.putString("taskEvent", task.getText().toString());
            editor.apply();
        }
        if (note.getText().toString().length() > 0) {
            editor.putString("note", note.getText().toString());
            editor.apply();
        }
        if (plannerTime.getText().toString().length() > 0) {
            editor.putString("time", plannerTime.getText().toString());
            editor.apply();
        }

        //removes none from note text field
        if (note.getText().toString().equals("none"))
            note.setText("");

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.save_menu,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if (date.getText().length() != 0 && plannerTime.getText().length() != 0 && task.getText().length() != 0)
        {
            outState.putString("Date", date.getText().toString());
            outState.putString("Time", plannerTime.getText().toString());
            outState.putString("taskToComplete", task.getText().toString());
            if (note.getText().length() != 0)
                outState.putString("Note", note.getText().toString());
            if (repNev.getText().length() != 0)
            {
                outState.putString("Repeat", repNev.getText().toString());
            }
        }

    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        if (savedInstanceState.getString("Date") != null)
        {
            date.setText(savedInstanceState.getString("Date"));
        }
        if (savedInstanceState.getString("Time") != null)
        {
            plannerTime.setText(savedInstanceState.getString("Time"));
        }
        if (savedInstanceState.getString("taskToComplete") != null)
        {
            task.setText(savedInstanceState.getString("taskToComplete"));
        }
        if (savedInstanceState.getString("Note") != null)
        {
            note.setText(savedInstanceState.getString("Note"));
        }
        if (savedInstanceState.getString("Repeat") != null)
        {
            repNev.setText(savedInstanceState.getString("Repeat"));
        }

    }

    @Override
    public void onBackPressed() {
        if (!saved)
        {
            confirmSavingFragment.show(getSupportFragmentManager(), TAG);

        }
    }

    //Back button
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home)
        {
            if (!saved)
            {
                confirmSavingFragment.show(getSupportFragmentManager(), TAG);

            }

            return false;
        }
        else if(item.getItemId() == R.id.save_task_event)
        {
            Intent saveToFileService = new Intent(this, SaveToFileService.class);

            if (date.getText().length() != 0 && plannerTime.getText().length() != 0 && task.getText().length() != 0)
            {
                saveToFileService.putExtra("Date",date.getText().toString());
                saveToFileService.putExtra("Time", plannerTime.getText().toString());
                saveToFileService.putExtra("taskToComplete",task.getText().toString());
                if (note.getText().length() != 0)
                    saveToFileService.putExtra("Note", note.getText().toString());
                if (repNev.getText().length() != 0)
                    saveToFileService.putExtra("Repeat", repNev.getText().toString());
                saveToFileService.putExtra("calling-Activity","planner");
                startService(saveToFileService);
                saved = true;
                Toast.makeText(this,"Saved",Toast.LENGTH_SHORT).show();
                finish();
                return false;

            }
            else
            {
                Toast.makeText(this, "Title, Date & Time must be filled", Toast.LENGTH_LONG).show();
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

    //region methods for date text field
    //Click on date text field
    public void plannerCalendarClicked(View view)
    {
        calFragment.show(getSupportFragmentManager(),TAG);
    }

    //Save button clicked
    public void saveClicked(View view)
    {
        calendarFragment.mOnInputListener.sendInput(calendarFragment.date);
        calFragment.getDialog().dismiss();
    }

    //Send data from calendar to date text field
    @Override
    public void sendInput(String input) {
        Log.d(TAG,"sendInput: got the input: " + input);
        date.setText(input);

    }
    //endregion

    //region methods for repeat text field
    public void repeatClicked(View view)
    {
        repFragment.show(getSupportFragmentManager(),"repeatFragment");
    }


    public void neverClicked(View view)
    {
        repNev.setText("Never");
        repFragment.getDialog().dismiss();
    }


    public void setRepeatClicked(View view)
    {
        setRepFragment.show(getSupportFragmentManager(),"setRepeatFragment");

        if (repNev.getText().toString().length() > 0)
        {
            setRepeatFragment.setDays.setText(repNev.getText().toString());
        }

        //dismisses the repeat fragment that appears before setRepeatFragment. this is fine don't remove it. dont get confused
        repFragment.getDialog().dismiss();
    }

    //Method used to save number of days entered in setRepeatFragment
    public void repeatSaveClicked(View view)
    {
        //Created an interface called repeatInputListener in the setRepeatFragment class to retrieve setDays EditText text because
        //I would receive a null pointer error if we didn't do it this way
        setRepeatFragment.mOnInputListener.sendRepeatInput(setRepeatFragment.setDays.getText().toString(),setRepeatFragment.unitOfTime);
        setRepFragment.getDialog().dismiss();

    }

    public void repeatCancelClicked(View view)
    {
        setRepFragment.getDialog().dismiss();
    }
    //This is the method that was declared in the repeatInputListener Interface. Here I implement the method and use it to set the never/repeat text field
    //in the planner activity
    @Override
    public void sendRepeatInput(String input, String unitOfTime) {
        repNev.setText(input+" "+unitOfTime);
    }
    //endregion

    //region Methods for time text
    public void plannerTimeClicked(View view)
    {
        timeFragment.show(getSupportFragmentManager(),"timeFragment");
    }

    public void saveTimeClicked(View view)
    {
        time_fragment.timeInterface.sendTime(time_fragment.hour,time_fragment.minutes,time_fragment.timeZone);
        timeFragment.getDialog().dismiss();
    }

    public void cancelClicked(View view)
    {
        timeFragment.getDialog().dismiss();
    }

    @Override
    public void sendTime(String hour, String minutes, String timeZone) {
        Log.d(TAG,"sendTime: got the time: " + hour+":"+minutes+" "+timeZone);
        plannerTime.setText(hour+":"+minutes+" "+timeZone);
    }
    //endregion



}
