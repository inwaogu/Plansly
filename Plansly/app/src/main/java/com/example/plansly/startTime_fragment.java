package com.example.plansly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.NumberPicker;

public class startTime_fragment extends DialogFragment {
    private String TAG = "startTimeFragment";
    private AlertDialog.Builder builder;
    private View startTimeFragmentView;

    static String hour;
    static String minutes;
    static String timeZone;

    interface  timeOnClickInterface
    {
        void sendStartTime(String hour, String minutes, String timeZone);
    }

    public static timeOnClickInterface timeInterface;


    public startTime_fragment() {
        // Required empty public constructor
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        //Use the builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());

        //Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        startTimeFragmentView = (View) inflater.inflate(R.layout.event_start_time_fragment_layout,null);

        builder.setView(startTimeFragmentView);

        //Get widgets reference from xml layout
        NumberPicker hourPicker = (NumberPicker) startTimeFragmentView.findViewById(R.id.hourPicker);
        NumberPicker minutePicker = (NumberPicker) startTimeFragmentView.findViewById(R.id.minutePicker);
        NumberPicker timeZonePicker = (NumberPicker) startTimeFragmentView.findViewById(R.id.timeZonePicker);

        //Hour picker values
        final String[] hourValues = {"1","2","3","4","5","6","7","8","9","10","11","12"};

        //Minute picker values
        final String[] minuteValues = {"00","01","02","03","04","05","06","07","08","09","10","11","12","13","14","15","16","17","18","19"
                ,"20","21","22","23","24","25","26","27","28","29","30","31","32","33","34","35","36","37","38","39","40","41","42","43","44","45"
                ,"46","47","48","49","50","51","52","53","54","55","56","57","58","59"};

        //Time zone picker values
        final String[] timeZoneValues = {"AM","PM"};

        //Set the minimum/maximum values of the hourPicker
        hourPicker.setMinValue(0);
        hourPicker.setMaxValue(hourValues.length-1);

        //Displays the hour values
        hourPicker.setDisplayedValues(hourValues);

        //Set the minimum/maximum values of the hourPicker
        minutePicker.setMinValue(0);
        minutePicker.setMaxValue(minuteValues.length-1);

        //Displays the minute values
        minutePicker.setDisplayedValues(minuteValues);

        //Set the minimum/maximum values of the timeZonePicker
        timeZonePicker.setMinValue(0);
        timeZonePicker.setMaxValue(timeZoneValues.length-1);

        //Displays the time zone values
        timeZonePicker.setDisplayedValues(timeZoneValues);


        //If the selector wheel reaches the max or min value wrap back around
        hourPicker.setWrapSelectorWheel(true);
        minutePicker.setWrapSelectorWheel(true);
        timeZonePicker.setWrapSelectorWheel(true);

        hour = hourValues[0];
        minutes = minuteValues[0];
        timeZone = timeZoneValues[0];

        //Value listener for hour picker
        hourPicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                hour = hourValues[newVal];
            }
        });

        //Value listener for minute picker
        minutePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                minutes = minuteValues[newVal];
            }
        });

        //Value listener for time zone picker
        timeZonePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                timeZone = timeZoneValues[newVal];
            }
        });

        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.event_start_time_fragment_layout, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        //Used to set width and height of fragment
        //width and height wouldn't set properly when tried in xml file so I did it here instead.
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = 612;
        params.height = 500;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            timeInterface =(timeOnClickInterface) getActivity();
        }
        catch (ClassCastException e)
        {
            Log.e(TAG,"onAttach: ClassCastException: " + e.getMessage());
        }

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }


}
