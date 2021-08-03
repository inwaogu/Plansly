package com.example.plansly;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

public class DeleteFromFilesService extends IntentService {

    private File inputFile;
    private File tempFile;
    private BufferedReader reader;
    private BufferedWriter writer;

    private String TAG = "DeleteFromFilesService";

    //Defines a custom Intent action
    private static final String BROADCAST_ACTION = "com.example.plansly.DELETE_BROADCAST";

    //Defines the key for the status extra in an intent
    private static final String  EXTENDED_DATA_STATUS = "com.example.plansly.DELETE_STATUS";

    public DeleteFromFilesService()
    {
        super("DeleteFromFilesService");
    }


    @Override
    public void onCreate() {
        super.onCreate();

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String[] monthsArray = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};

        String path = getFilesDir().toString()+"/"+ Build.MODEL;
        String fileName = monthsArray[ Integer.parseInt(intent.getStringExtra("fileName"))-1];
        inputFile = new File(path + "/" + fileName);
        tempFile = new File(path + "/" +"temp"+intent.getStringExtra("fileName"));
        String status = "notFinished";

        String date = intent.getStringExtra("date");
        String taskEvent = intent.getStringExtra("taskEvent");
        String note = intent.getStringExtra("note");
        String time = intent.getStringExtra("time");
        String currentLine;

        try {
            Scanner reader = new Scanner(inputFile);
            writer = new BufferedWriter(new FileWriter(tempFile));


            while (reader.hasNextLine())
            {
                currentLine = reader.nextLine();
                if (currentLine.contains(date+"|"+time+"|"+taskEvent+"|"+note) ) {
                    continue;
                }
                writer.write(currentLine+"\n");
                Log.i(TAG, currentLine);
            }
            writer.close();
            reader.close();
            //delete the old file and replace with the name of the new file
            boolean deleted = inputFile.delete();
            boolean successful = tempFile.renameTo(inputFile);

            Log.i(TAG, inputFile.getName()+" was deleted:"+Boolean.toString(deleted));
            Log.i(TAG, tempFile.getName()+" was renamed "+inputFile.getName()+":"+Boolean.toString(successful));


        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "File not found");
        } catch (IOException e) {
            e.printStackTrace();
        }
        status = "deleteFinished";
        Intent localIntent = new Intent().setAction(BROADCAST_ACTION).putExtra(EXTENDED_DATA_STATUS, status);
        //Broadcasts the Intent to receivers in this app
        LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);
    }
}
