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
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;

public class remindFragment extends DialogFragment {
    String TAG = "remindFragment";
    AlertDialog.Builder builder;
    View remindFragmentView;
    static String unitOfTime;
   static EditText remindTimeEditText;



    public interface  remindOnClickInterface
    {
        void sendRemindTime(String time, String unitOfTime);
    }

    public static remindOnClickInterface remindInterface;

    public remindFragment() {
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Use the builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());

        //Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        remindFragmentView = (View) inflater.inflate(R.layout.remind_fragment_layout,null);

        remindTimeEditText = remindFragmentView.findViewById(R.id.remindTimeEditText);

        //Get widgets reference from xml layout
        NumberPicker unitTimePicker = (NumberPicker) remindFragmentView.findViewById(R.id.unit_of_time_Picker);

        //Hour picker values
        final String[] unitOFTimeValues = {"Minute(s) Before","Hour(s) Before","Day(s) Before","Week(s) Before"};

        //Set the minimum/maximum values of the remindTimePicker
        unitTimePicker.setMinValue(0);
        unitTimePicker.setMaxValue(unitOFTimeValues.length-1);

        //Displays the hour values
        unitTimePicker.setDisplayedValues(unitOFTimeValues);

        //If the selector wheel reaches the max or min value wrap back around
        unitTimePicker.setWrapSelectorWheel(true);

        //Todo:set default value for unit of time
        //Value listener for unit of time picker
        unitTimePicker.setOnValueChangedListener(new NumberPicker.OnValueChangeListener() {
            @Override
            public void onValueChange(NumberPicker numberPicker, int oldVal, int newVal) {
                unitOfTime = unitOFTimeValues[newVal];
                //time = remindTimeEditText.getText().toString();
            }
        });

        builder.setView(remindFragmentView);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.remind_fragment_layout, container, false);
    }


    @Override
    public void onResume() {
        super.onResume();
        //Used to set width and height of fragment
        //width and height wouldn't set properly when tried in xml file so I did it here instead.
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = 620;
        params.height = 500;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }


    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            remindInterface =(remindOnClickInterface) getActivity();
        }
        catch (ClassCastException e)
        {
            Log.e(TAG,"onAttach: ClassCastException: " + e.getMessage());
        }

    }
}
