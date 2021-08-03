package com.example.plansly;

import android.app.IntentService;
import android.content.Intent;
import android.os.Build;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.nio.charset.MalformedInputException;
import java.util.Scanner;

/*
* When user presses edit, this service takes the info from the card being edited and finds it in file. After finding its
* line in the file, this service sends the information in the line to the appropriate intent(planner/event)
* */

public class CompareCardService extends IntentService {
    private File inputFile;
    private BufferedReader reader;
    private String TAG = "CompareCardService";

    public CompareCardService()
    {
        super("CompareCardService");
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
        String cardInfoToRetrieve = intent.getStringExtra("cardInfoToRetrieve");
        String currentLine;
        String[] cardInfo = null;

        try {
            Scanner reader = new Scanner(inputFile);


            while (reader.hasNextLine()) {
                currentLine = reader.nextLine();
                if (currentLine.contains(cardInfoToRetrieve)) {
                    cardInfo = currentLine.split("\\|");
                    Log.i(TAG, currentLine);

                    for (String i : cardInfo)
                    {
                        Log.i(TAG,i);

                    }
                }
            }
            reader.close();
            Intent plannerEvent;

            if (cardInfo != null)
            {
                if (cardInfo[0].equals("Task"))
                {
                    //Going to need retrieve entire line so that user can edit whatever they want and then save it.
                    plannerEvent = new Intent(this, planner.class);
                    plannerEvent.putExtra("TaskType", cardInfo[0]);
                    plannerEvent.putExtra("Date", cardInfo[1]);
                    plannerEvent.putExtra("Time", cardInfo[2]);
                    plannerEvent.putExtra("Task", cardInfo[3]);
                    plannerEvent.putExtra("Note", cardInfo[4]);
                    plannerEvent.putExtra("Repeat",cardInfo[5]);
                    startActivity(plannerEvent);
                }
                else if (cardInfo[0].equals("Event"))
                {
                    //Going to need retrieve entire line so that user can edit whatever they want and then save it.
                    plannerEvent = new Intent(this, Event.class);
                    plannerEvent.putExtra("EventType", cardInfo[0]);
                    plannerEvent.putExtra("startDate", cardInfo[1]);
                    plannerEvent.putExtra("startTime", cardInfo[2]);
                    plannerEvent.putExtra("Event", cardInfo[3]);
                    plannerEvent.putExtra("eventNote", cardInfo[4]);
                    plannerEvent.putExtra("endDate", cardInfo[5]);
                    plannerEvent.putExtra("endTime", cardInfo[6]);
                    plannerEvent.putExtra("location", cardInfo[7]);
                    plannerEvent.putExtra("remindMe", cardInfo[8]);
                    startActivity(plannerEvent);
                }
            }

        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
            Log.i(TAG, "File not found");
        }
    }
}
