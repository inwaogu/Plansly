package com.example.plansly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;

public class repeatFragment extends DialogFragment {
    String TAG = "repeatFragment";
    AlertDialog.Builder builder;
    View repeatFragmentView;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Use the builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity());

        //Get the layout inflater
       LayoutInflater inflater = requireActivity().getLayoutInflater();
        repeatFragmentView = (View) inflater.inflate(R.layout.repeat_layout,null);

        builder.setView(repeatFragmentView);
        return builder.create();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return super.onCreateView(inflater, container, savedInstanceState);
    }
}
