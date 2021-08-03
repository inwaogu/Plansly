package com.example.plansly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class deleteConfirmFragment extends DialogFragment {

    private AlertDialog.Builder builder;
    private View view;
    private String TAG = "delete_confirm_fragment";

    public deleteConfirmFragment ()
    {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Use the builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity(), R.style.AppBackgroundColor);
        //Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        view = inflater.inflate(R.layout.delete_confirm_fragment,null);
        builder.setView(view);


        return builder.create();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        //Used to set width and height of fragment
        //width and height wouldn't set properly when tried in xml file so I did it here instead.
        ViewGroup.LayoutParams params = getDialog().getWindow().getAttributes();
        params.width = 630;
        params.height = 250;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }
}
