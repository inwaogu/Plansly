package com.example.plansly;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;

public class ReadFromFileService extends IntentService {

    private Scanner reader;
    private String path;
    private String TAG = "ReadFromFileService";
    private String date;
    private static ArrayList<String[]> dateInfo;
    private String[] tempArray;
    private String fileName;
    private String status;

    //Defines a custom Intent action
    private static final String BROADCAST_ACTION = "com.example.plansly.BROADCAST";

    //Defines the key for the status extra in an intent
    private static final String  EXTENDED_DATA_STATUS = "com.example.plansly.STATUS";


    public ReadFromFileService()
    {
        super("ReadFromFileService");
    }



    @Override
    public void onCreate() {
        super.onCreate();
        path = getFilesDir().toString()+"/"+ Build.MODEL;
        dateInfo = new ArrayList<String[]>();
        status = "notFinished";

        Log.i(TAG, "onCreate called");
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        String[] monthsArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};


        String contentRead = "";
        String getStringData = intent.getStringExtra("fileName");
        fileName = monthsArray[Integer.parseInt(getStringData) - 1];
        //dateInfo.clear();
        Log.i(TAG, "ReadFromFileService Started");

        try {
            reader = new Scanner(new File(path + "/" + fileName));
            date = intent.getStringExtra("date");


        } catch (FileNotFoundException e) {
            Log.i(TAG, "could not find file to read from");
        }

        String[] tempInfo;

        if (fileExists(fileName)){
            while (reader.hasNextLine()) {
                /*
                 * reader.nextLine() literally jumps to the next line
                 * I want it to give me the value of its current line so I store the line in a variable.
                 * */
                String nextLine = reader.nextLine();
                if (nextLine.contains(date) && nextLine.indexOf(date) <= 7) {

                    contentRead = nextLine;
                    tempInfo = contentRead.split("\\|");
                    //Log.i(TAG,nextLine);
                    for (String i : tempInfo) {
                        //Log.i(TAG, i);
                    }
                    Log.i(TAG, "read from file service started");
                    dateInfo.add(tempInfo);
                }
            }
            reader.close();
            Log.i(TAG,Integer.toString(getDateInfo().size()));
    }


        status = "finished";
        Intent localIntent = new Intent().setAction(BROADCAST_ACTION).putExtra(EXTENDED_DATA_STATUS, status);
        //Broadcasts the Intent to receivers in this app
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);

    }
    public ArrayList<String[]> getDateInfo()
    {
        return dateInfo;
    }


    private boolean fileExists(String fileName)
    {
        File filePath = new File(path+"/"+fileName);

        boolean doesExist = filePath.exists();

        return doesExist;
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
