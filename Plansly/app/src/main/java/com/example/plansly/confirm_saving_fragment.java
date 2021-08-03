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
import android.widget.TextView;

public class confirm_saving_fragment extends DialogFragment {

    private AlertDialog.Builder builder;
    private View view;
    private String TAG = "confirm_saving_fragment";
    private TextView dialogText;

    public confirm_saving_fragment ()
    {

    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        //Use the builder class for convenient dialog construction
        builder = new AlertDialog.Builder(getActivity(), R.style.AppBackgroundColor);
        //Get the layout inflater
        LayoutInflater inflater = requireActivity().getLayoutInflater();

        view=  inflater.inflate(R.layout.confirm_saving_fragment_layout,null);
        builder.setView(view);

        dialogText = view.findViewById(R.id.dialogText);

        dialogText.setLineSpacing(2f,1.2f);
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
        params.height = 350;
        getDialog().getWindow().setAttributes((android.view.WindowManager.LayoutParams) params);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

    }
}
