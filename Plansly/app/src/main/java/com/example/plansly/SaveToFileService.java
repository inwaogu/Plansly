package com.example.plansly;

import android.app.IntentService;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

public class SaveToFileService extends IntentService {

    private String DELETESTATUS;
    //Broadcast receiver for receiving status updates from the IntentService
    private class MyRespondReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals("com.example.plansly.DELETE_BROADCAST"))
            {
                DELETESTATUS = intent.getStringExtra("com.example.plansly.DELETE_STATUS");
                Log.i("checkCalled",DELETESTATUS);

            }


        }

        public String getDeleteStatus()
        {
            return DELETESTATUS;
        }
    }


    //region planner variables/data
    private String Date;
    private String Time;
    private String taskToComplete;
    private String Note;
    private String Repeat;
    //endregion

    //region event variables/data
    private String startDate;
    private String startTime;
    private String endDate;
    private String endTime;
    private String eventGoal;
    private String endNote;
    private String location;
    private String remindMe;
    //endregion

    private String path;
    private String callingActivity;
    private final String TAG = "SaveToFile";

    private File file;
    private FileOutputStream fileOutputStream;
    private boolean made;
    private boolean isExists;
    private static boolean wasSaved;

    private File[] files;
    private String status;

    //Calling activities
    private String task;
    private String event;

    //Debugging code
   private File newDirectory;
   private Scanner reader;
   private boolean editedStatus;
   private boolean deleteServiceStarted;

    private Date dateHolder;
    private String currentDate;

    //In folder for user create file based on month that was received through constructor. position of month in array
    //Is month value sent minus 1
    private String[] monthsArray = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};
    private String month;

    //Defines a custom Intent action
    private static final String BROADCAST_ACTION = "com.example.plansly.BROADCASTSAVE";

    //Defines the key for the status extra in an intent
    private static final String EXTENDED_DATA_STATUS = "com.example.plansly.SAVESTATUS";
    private IntentFilter deleteStatusIntentFilter;
    private MyRespondReceiver respondReceiver;

    public SaveToFileService() {
        super("SaveToFileService");
        wasSaved = false;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        file = new File(getFilesDir(),Build.MODEL);
        path = getFilesDir().toString()+"/"+ Build.MODEL;
        reader = null;
        deleteServiceStarted = false;

        editedStatus = false;
        DELETESTATUS = "";
        deleteStatusIntentFilter = new IntentFilter("com.example.plansly.DELETE_BROADCAST");
        respondReceiver = new MyRespondReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(respondReceiver, deleteStatusIntentFilter);

        //Does the directory exist
        isExists = file.exists();
        //wasSaved = false;
        status = "";

        //If there isn't already a folder containing all the months for certain tasks/Events then create one
        if (!isExists)
        {
            //made contains whether or not the directory was successfully made(true or false)
            made =  file.mkdir();
            Log.i(TAG,"Directory was made: "+String.valueOf(made));
        }
        else
        {
            Log.i(TAG,"Directory already exists");
        }

        SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy");
        dateHolder = new Date();
        currentDate = formatter.format(dateHolder);

    }



    @Override
    protected void onHandleIntent( Intent intent) {

        Bundle extras = intent.getExtras();
        String fileContent = "";
        callingActivity = intent.getStringExtra("calling-Activity");
        /*String tempDate;
        String*/
        //User isn't going to be forced to enter a note so I'll only
        //set note if its not equal to null

        if (extras != null) {
            if (callingActivity.equals("planner")) {
                fileContent += "Task" + "|";
                if (extras.containsKey("Date")) {
                    Date = intent.getStringExtra("Date");
                    fileContent += Date + "|";
                }

                if (extras.containsKey("Time")) {
                    Time = intent.getStringExtra("Time");
                    fileContent += Time + "|";
                }

                if (extras.containsKey("taskToComplete")) {
                    taskToComplete = intent.getStringExtra("taskToComplete");

                    //Don't need to create new line after because there will always be "none" if a note isn't entered so we can create new line in note
                    fileContent += taskToComplete + "|";

                }

                if (extras.containsKey("Note")) {
                    Note = intent.getStringExtra("Note");
                    if (extras.containsKey("Repeat"))
                        fileContent += Note + "|";
                    else
                        fileContent += Note + "\n";
                } else {
                    Note = "none";
                    if (extras.containsKey("Repeat"))
                        fileContent += Note + "|";
                    else
                        fileContent += Note + "\n";
                }

                if (extras.containsKey("Repeat")) {
                    Repeat = intent.getStringExtra("Repeat");
                    fileContent += Repeat + "\n";
                }

            } else if (callingActivity.equals("event")) {
                fileContent += "Event" + "|";
                if (extras.containsKey("startDate")) {
                    startDate = intent.getStringExtra("startDate");
                    fileContent += startDate + "|";
                }
                if (extras.containsKey("startTime")) {
                    startTime = intent.getStringExtra("startTime");
                    fileContent += startTime + "|";
                }
                if (extras.containsKey("eventGoal")) {
                    eventGoal = intent.getStringExtra("eventGoal");
                    fileContent += eventGoal + "|";
                }


                if (extras.containsKey("endNote")) {
                    endNote = intent.getStringExtra("endNote");
                    fileContent += endNote + "|";
                } else {
                    endNote = "none";
                    if (extras.containsKey("endDate"))
                        fileContent += endNote + "|";
                    else
                        fileContent += endNote + "\n";
                }


                if (extras.containsKey("endDate")) {
                    endDate = intent.getStringExtra("endDate");
                    fileContent += endDate + "|";
                }
                if (extras.containsKey("endTime")) {
                    endTime = intent.getStringExtra("endTime");

                        fileContent += endTime + "|";

                }

                if (extras.containsKey("location")) {
                    location = intent.getStringExtra("location");
                    fileContent += location + "|";
                } else {
                    location = "None";
                    fileContent += location + "|";
                }
                if (extras.containsKey("remindMe")) {
                    remindMe = intent.getStringExtra("remindMe");
                    fileContent += remindMe + "\n";
                }
                else {
                    remindMe = "Never";
                    fileContent += remindMe + "\n";
                }
            }
        }


        if (callingActivity.equals("planner")) {
            //Don't edit month variable
            month = monthsArray[Integer.parseInt(Date.substring(0, 1)) - 1];

            MainActivity mainActivity = new MainActivity();
            Log.i(TAG, "Edit Clicked:"+Boolean.toString(mainActivity.getEditClicked()));

            //Used to retrieve unedited text
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String tempDate = prefs.getString("date", "no id"); //no id: default value
            String tempTask = prefs.getString("taskEvent", "no id"); //no id: default value
            String tempNote = prefs.getString("note", "no id"); //no id: default value
            String tempTime = prefs.getString("time", "no id"); //no id: default value
            String tempRepeat = prefs.getString("repeat", "no id"); //no id: default value

            if (mainActivity.getEditClicked()) {
                if (!tempDate.equals(Date) || !tempTask.equals(taskToComplete) || !tempNote.equals(Note) || !tempTime.equals(Time) || !tempRepeat.equals(Repeat)) {
                    editedStatus = true;

                    Intent deleteFromFilesService = new Intent(this, DeleteFromFilesService.class);
                    deleteFromFilesService.putExtra("fileName", currentDate.substring(0, 1));
                    deleteFromFilesService.putExtra("date", tempDate);
                    deleteFromFilesService.putExtra("note", tempNote);
                    deleteFromFilesService.putExtra("taskEvent", tempTask);
                    deleteFromFilesService.putExtra("time", tempTime);
                    startService(deleteFromFilesService);
                    deleteServiceStarted = true;

                    Log.i(TAG, "edit was clicked");
                }
                else
                {
                    return;
                }
            }
        }
        else if (callingActivity.equals("event")) {
            //Don't edit month variable
            month = monthsArray[Integer.parseInt(startDate.substring(0, 1)) - 1];

            Event event = new Event();
            MainActivity mainActivity = new MainActivity();

            //Used to retrieve unedited text
            SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
            String tempStartDate = prefs.getString("startDate", "no id"); //no id: default value
            String tempEndDate = prefs.getString("endDate", "no id"); //no id: default value
            String tempTask = prefs.getString("taskEvent", "no id"); //no id: default value
            String tempNote = prefs.getString("note", "no id"); //no id: default value
            String tempStartTime = prefs.getString("startTime", "no id"); //no id: default value
            String tempEndTime = prefs.getString("endTime", "no id"); //no id: default value
            String tempLocation = prefs.getString("location", "no id"); //no id: default value

            if (mainActivity.getEditClicked()) {
                if (!tempStartDate.equals(startDate) || !tempTask.equals(eventGoal) || !tempNote.equals(endNote) || !tempStartTime.equals(startTime) ||
                        !tempLocation.equals(location) || !tempEndDate.equals(endDate) || !tempEndTime.equals(endTime)) {
                    editedStatus = true;

                    Intent deleteFromFilesService = new Intent(this, DeleteFromFilesService.class);
                    //These are the only parameters that deleteFromFilesService requires
                    deleteFromFilesService.putExtra("fileName", currentDate.substring(0, 1));
                    deleteFromFilesService.putExtra("date", tempStartDate);
                    deleteFromFilesService.putExtra("note", tempNote);
                    deleteFromFilesService.putExtra("taskEvent", tempTask);
                    deleteFromFilesService.putExtra("time", tempStartTime);
                    startService(deleteFromFilesService);
                    deleteServiceStarted = true;

                    Log.i(TAG, "edit was clicked");
                }
                else {
                    return;
                }
            }
        }


        //If the directory doesn't exist then create a new directory otherwise,
        //create create file with month name in director

        /*
         * Wait until our delete is finished then save.
         * */
        if (deleteServiceStarted) {
            while (!DELETESTATUS.equals("deleteFinished")) {
                respondReceiver.getDeleteStatus();
            }
        }

        try {
            //Need to check if the file already exists or not
            fileOutputStream = new FileOutputStream(new File(getFilesDir()+"/"+Build.MODEL+"/"+month),true);
            if (!fileExists(getFilesDir()+"/"+Build.MODEL+"/"+month))
            {
                fileOutputStream.write(fileContent.getBytes());
                fileOutputStream.close();
                wasSaved = true;
                status = "saved";
                Intent localIntent = new Intent(BROADCAST_ACTION).putExtra(EXTENDED_DATA_STATUS, status);
                //Broadcasts the Intent to receivers in this app
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);



            }
            else
            {
                    fileOutputStream.write(fileContent.getBytes());
                    fileOutputStream.close();
                    wasSaved = true;
                status = "saved";
                Intent localIntent = new Intent(BROADCAST_ACTION).putExtra(EXTENDED_DATA_STATUS, status);
                //Broadcasts the Intent to receivers in this app
                LocalBroadcastManager.getInstance(this).sendBroadcast(localIntent);


            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        printFilesInDirectory(path);
        readFileInDirectory("May");

    }

    //region Debugging methods
    public void printFilesInDirectory(String path)
    {
        File directory = new File(path);
        files = directory.listFiles();

        for (int i =0; i< files.length; i++)
        {
            Log.i(TAG, files[i].getName());
        }
    }

    //Allows me to read content of file in directory
    public void readFileInDirectory(String fileName)
    {
        //newDirectory = new File(getFilesDir()+"/"+Build.MODEL+"/"+fileName);
        try {
            reader = new Scanner(new File(getFilesDir()+"/"+Build.MODEL+"/"+fileName));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            Log.i(TAG, "File could not be found");
        }

        while(reader.hasNextLine())
        {
            Log.i(TAG, reader.nextLine());

        }
        reader.close();



    }
    //endregion


    private boolean fileExists(String fileName)
    {
        File filePath = new File(path+"/"+fileName);

        boolean doesExist = filePath.exists();

        return doesExist;
    }

    //checks whether something was recently saved
    public static  boolean getSavedState()
    {
        return wasSaved;
    }
}
