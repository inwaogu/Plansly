package com.example.plansly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.TextView;


public class setRepeatFragment extends DialogFragment {

    String TAG = "repeatFragment";
    AlertDialog.Builder builder;
    View setRepeatFragmentView;
    static EditText setDays;
    static String unitOfTime;
    public interface  repeatInputListener{
        void sendRepeatInput(String input, String unitOfTime);
    }
    public static repeatInputListener mOnInputListener;

    public setRepeatFragment() {
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Use the builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());

        //Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        setRepeatFragmentView = (View) inflater.inflate(R.layout.set_repeat_layout,null);

        setDays = (EditText) setRepeatFragmentView.findViewById(R.id.setDays);

        //Get widgets reference from xml layout
        NumberPicker unitTimePicker = (NumberPicker) setRepeatFragmentView.findViewById(R.id.unit_of_repeat_Picker);

        //Hour picker values
        final String[] unitOFTimeValues = {"Day(s)","Week(s)","Month(s)","Year(s)"};

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
            }
        });


        builder.setView(setRepeatFragmentView);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mOnInputListener =(repeatInputListener) getActivity();
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
