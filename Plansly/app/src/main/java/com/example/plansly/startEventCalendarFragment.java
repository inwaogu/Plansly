package com.example.plansly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class startEventCalendarFragment extends DialogFragment {
    String TAG = "startEventCalendarFragment";

    private CalendarView calendarView;
    private AlertDialog.Builder builder;

    //save button
    private Button saveButton;
    static String date;

    public interface  OnInputListener{
        void sendInput(String input);
    }

    protected static OnInputListener mOnInputListener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Use the builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());

        //Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        calendarView = (CalendarView) inflater.inflate(R.layout.start_event_calendar_layout,null);
        builder.setView(calendarView);

        return builder.create();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        saveButton = calendarView.findViewById(R.id.savebutton);

        calendarView.setOnDateChangeListener(new CalendarView.OnDateChangeListener() {
            @Override
            public void onSelectedDayChange(@NonNull CalendarView calendarView, int i, int i1, int i2) {
                //Date
                date = (i1+1)+ "/" + i2 + "/" +i;
            }
        });

        return calendarView;
    }


    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try{
            mOnInputListener = (OnInputListener) getActivity();
        }
        catch (ClassCastException e)
        {
            Log.e(TAG, "onAttach: ClassCastException: " + e.getMessage());
        }
    }

}
