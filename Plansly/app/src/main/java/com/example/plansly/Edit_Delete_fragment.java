package com.example.plansly;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class Edit_Delete_fragment extends DialogFragment {

    private AlertDialog.Builder builder;
    private View view;
    private String TAG = "edit_delete_fragment";

    public Edit_Delete_fragment()
    {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Use the builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity(), R.style.AppBackgroundColor);
        //Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        view=  inflater.inflate(R.layout.edit_delete_fragment_layout,null);
        builder.setView(view);


        return builder.create();
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {

        return view;
    }

    public void onResume() {
        super.onResume();

    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }

}
