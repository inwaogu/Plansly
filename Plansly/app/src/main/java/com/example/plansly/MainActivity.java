package com.example.plansly;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.DialogFragment;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity{
    private String TAG = "MainActivity";
    private String readStatus;
    private String SAVESTATUS;
    private String DELETESTATUS;

    private static boolean editClicked;
    //Broadcast receiver for receiving status updates from the IntentService
    private class MyRespondReceiver extends BroadcastReceiver
    {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.i(TAG,"onRecieveCalled");
            //Log.i(TAG,intent.getStringExtra("com.example.plansly.STATUS"));
            Log.i(TAG,action);
            if (action.equals("com.example.plansly.BROADCAST"))
            {
                readStatus = intent.getStringExtra("com.example.plansly.STATUS");
                Log.i(TAG,readStatus);
            }
            else if (action.equals("com.example.plansly.BROADCASTSAVE"))
            {
                SAVESTATUS = intent.getStringExtra("com.example.plansly.SAVESTATUS");
                Log.i(TAG,SAVESTATUS);
            }
            else if (action.equals("com.example.plansly.DELETE_BROADCAST"))
            {
                DELETESTATUS = intent.getStringExtra("com.example.plansly.DELETE_STATUS");
                Log.i("checkCalled",DELETESTATUS);

            }

        }

        public String getReadStatus()
        {
            return readStatus;
        }
    }

    private Typeface fontSans;
    private TextView logoText;
    private DrawerLayout drawerLayout;

    private RecyclerView mRecyclerView;
    private CardAdapter mAdapter;
    private CardView cardView;
    private RecyclerView.LayoutManager mLayoutManager;
    private ReadFromFileService readFromFile;
    private SaveToFileService saveToFileService;
    private ArrayList<CardItems> cardItemsList;
    private Intent readFromFileService;

    private Date date;
    private String currentDate;

    private IntentFilter readStatusIntentFilter;
    private IntentFilter saveStatusIntentFilter;
    private IntentFilter deleteStatusIntentFilter;
    private IntentFilter editStatusIntentFilter;
    private MyRespondReceiver respondReceiver;

    private DialogFragment editDelete;
    private DialogFragment deleteConfirmFragment;
    private int cardPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View actionBarView = getLayoutInflater().inflate(R.layout.actionbar,null);
        readFromFile = new ReadFromFileService();
        saveToFileService = new SaveToFileService();
        //Toolbar
        Toolbar myToolbar=(Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(myToolbar);
        ActionBar actionBar =getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setCustomView(actionBarView);

        //Navigation Drawer
        drawerLayout = findViewById(R.id.drawer_layout);
        actionBar.setHomeButtonEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setHomeAsUpIndicator(R.drawable.ic_menu);

        //Toolbar Header(PLANSLY)
        logoText= actionBarView.findViewById(R.id.myTitle);
        fontSans = Typeface.createFromAsset(this.getAssets(),"fonts/opensans.ttf");
        logoText.setTypeface(fontSans);


        //Select navigation drawer item
        NavigationView navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                //set item as selected to persist highlight
                menuItem.setChecked(true);
                //close drawer when item is tapped
                drawerLayout.closeDrawers();
                return true;
            }
        });


        //region card view code

        readStatus="";
        SAVESTATUS="";
        DELETESTATUS="";
        //Sets broadcast to be searching for
        readStatusIntentFilter = new IntentFilter("com.example.plansly.BROADCAST");
        saveStatusIntentFilter = new IntentFilter("com.example.plansly.BROADCASTSAVE");
        deleteStatusIntentFilter = new IntentFilter("com.example.plansly.DELETE_BROADCAST");
        editClicked = false;

        respondReceiver = new MyRespondReceiver();
        //Registers MyRespondReceiver and its intent filters
        LocalBroadcastManager.getInstance(this).registerReceiver(respondReceiver, readStatusIntentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(respondReceiver, saveStatusIntentFilter);
        LocalBroadcastManager.getInstance(this).registerReceiver(respondReceiver, deleteStatusIntentFilter);


        SimpleDateFormat formatter = new SimpleDateFormat("M/d/yyyy");
        date = new Date();
        currentDate = formatter.format(date);
        Log.i(TAG,currentDate);

        readFromFileService = new Intent(this, ReadFromFileService.class);
        //File i'm looking for
        readFromFileService.putExtra("fileName",currentDate.substring(0,1));

        //date i'm searching for in file
        readFromFileService.putExtra("date",currentDate);
        startService(readFromFileService);

        //Todo: fix dialog box sizes on bigger phones or smaller

        Log.i("checkCalled", "onCreate called!");

        editDelete = new Edit_Delete_fragment();
        deleteConfirmFragment = new deleteConfirmFragment();

        cardItemsList = new ArrayList<CardItems>();
        mRecyclerView = findViewById(R.id.task_recycler_view);
        View v = getLayoutInflater().inflate(R.layout.card_items,null);
        cardView = v.findViewById(R.id.cardItemsContainer);

        mLayoutManager = new LinearLayoutManager(getApplicationContext());
        //improves performance of app
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);

        final ProgressBar cardProgressBar = findViewById(R.id.cardProgressBar);
        //buildRecyclerView();
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                //while read from file service hasn't finished reading from files then keep checking read status
                while (!respondReceiver.getReadStatus().equals("finished"))
                {
                    respondReceiver.getReadStatus();

                }
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {

                        if (respondReceiver.getReadStatus().equals("finished"))
                        {
                            // updates the UI
                            mRecyclerView.setVisibility(View.VISIBLE);
                            cardProgressBar.setVisibility(View.GONE);
                            for (int i = 0; i < readFromFile.getDateInfo().size(); i++)
                            {
                                cardItemsList.add(new CardItems(readFromFile.getDateInfo().get(i)[2], readFromFile.getDateInfo().get(i)[3], readFromFile.getDateInfo().get(i)[4]));
                            }
                            //listens for card clicks
                            cardClickedListener();
                        }

                    }
                });

            }
        };

        // If there hasn't been a configuration change then run the getCardInfo thread
        if (savedInstanceState == null)
        {
            Log.i("checkCalled", "Oncreate: savedInstanceState was null");
            Thread getCardInfo = new Thread(runnable);
            getCardInfo.start();
        }
        else
        {
            Log.i("checkCalled", "onCreate read from instance state and not file");
            int count = 0;
            while (count < savedInstanceState.getStringArray("cardInfo").length - 1) {
                cardProgressBar.setVisibility(View.VISIBLE);
                cardItemsList.add(new CardItems(savedInstanceState.getStringArray("cardInfo")[count], savedInstanceState.getStringArray("cardInfo")[++count], savedInstanceState.getStringArray("cardInfo")[++count]));
                count++;
            }
            cardProgressBar.setVisibility(View.GONE);
            cardClickedListener();

        }
        //endregion


        com.getbase.floatingactionbutton.FloatingActionButton addTaskButton = findViewById(R.id.taskLabel);
        //launch Planner activity
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(getApplicationContext(), planner.class);
                startActivity(I);
            }
        });

        com.getbase.floatingactionbutton.FloatingActionButton addEventButton = findViewById(R.id.eventLabel);
        //launch Event activity
        addEventButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent I = new Intent(getApplicationContext(), Event.class);
                startActivity(I);
            }
        });
    }

    public  void removeItem(int position)
    {
        cardItemsList.remove(position);

        mAdapter.notifyItemRemoved(position);
    }



    private void cardClickedListener()
    {
        mAdapter = new CardAdapter(cardItemsList);
        mRecyclerView.setAdapter(mAdapter);

        mAdapter.setOnItemClickListener(new CardAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {
                cardPosition = position;
                editDelete.show(getSupportFragmentManager(),TAG);
            }
        });

    }



    protected void onResume()
    {
        super.onResume();


        //region card view code
        Log.i("checkCalled","onResume called");
        if (SAVESTATUS.equals("saved") || editClicked) {

            //Start read from file service
            stopService(readFromFileService);
            startService(readFromFileService);
            //mRecyclerView.setVisibility(View.GONE);
            cardItemsList = new ArrayList<>();

            //Todo: fix dialog box sizes on bigger phones or smaller
            //Todo: This has nothing to do with this file in particular but don't forget to set character limit on notes

            //mRecyclerView = findViewById(R.id.task_recycler_view);
            final ProgressBar cardProgressBar = findViewById(R.id.cardProgressBar);
            cardProgressBar.setVisibility(View.VISIBLE);
            Runnable runnable = new Runnable() {
                @Override
                public void run() {
                    //while read from file service hasn't finished reading from files then keep checking read status
                    while (!respondReceiver.getReadStatus().equals("finished"))
                    {

                        respondReceiver.getReadStatus();

                    }
                    /*
                    * onSaveInstanceState is called and saves state, onCreate is called and restores state by reading intent from parameters
                    * */

                    for (int i = 0; i < readFromFile.getDateInfo().size(); i++)
                    {
                        //time, task, note
                        cardItemsList.add(new CardItems(readFromFile.getDateInfo().get(i)[2],readFromFile.getDateInfo().get(i)[3],readFromFile.getDateInfo().get(i)[4]));
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (respondReceiver.getReadStatus().equals("finished"))
                            {
                                Log.i("checkCalled","finished");
                                /// updates the UI
                                //mRecyclerView.setVisibility(View.VISIBLE);
                                cardProgressBar.setVisibility(View.GONE);
                                stopService(readFromFileService);
                                //listens for card clicks
                                cardClickedListener();

                            }

                        }
                    });

                }
            };

            //Starts runnable
            Thread getCardInfo = new Thread(runnable);
            getCardInfo.start();
            //resets SAVESTATUS and readStatus
            SAVESTATUS =" ";
            readStatus = " ";
            editClicked = false;
            //endregion
        }

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        /*
         * Get the Time, Task, and Note from each card.
         * Store them.
         * [card1][task,note,time]
         * */

        String[][] items = new String[cardItemsList.size()][3];

        if (items.length != 0){
            for (int card = 0; card < cardItemsList.size(); card++) {
                items[card][0] = cardItemsList.get(card).getTime();
                items[card][1] = cardItemsList.get(card).getTask();
                items[card][2] = cardItemsList.get(card).getNote();
            }
        String[] item = new String[items.length * items[0].length];
        int itemSize = 0;

        while (itemSize < items.length * items[0].length) {
            for (int row = 0; row < items.length; row++) {
                for (int columns = 0; columns < items[0].length; columns++) {
                    item[itemSize] = items[row][columns];
                    itemSize++;
                }
            }

        }

        outState.putStringArray("cardInfo", item);
    }
    else
        {
            Log.e(TAG, "onSaveInstanceState: items.length is 0. items is empty");
        }
        Log.i("checkCalled", "onSaveInstanceState called");

        /*for (int i = 0; i < item.length; i++)
        {
            Log.i("checkCalled",item[i]);
        }

        for (int row = 0; row < items.length; row++)
        {
            for (int columns = 0; columns < items[0].length; columns++)
            {
                Log.i("checkCalled", "onSaveInstanceState called: "+items[row][columns]);
            }
        }*/
    }


    //Click back arrow in navigation drawer
    public void backOnClick(View view)
    {
        drawerLayout.closeDrawers();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void deleteClicked(View view)
    {
        deleteConfirmFragment.show(getSupportFragmentManager(),TAG);
    }

    public void deleteSureClicked(View view)
    {
        Intent deleteFromFilesService = new Intent(this, DeleteFromFilesService.class);
        deleteFromFilesService.putExtra("fileName",currentDate.substring(0,1));
        deleteFromFilesService.putExtra("date", currentDate);
        deleteFromFilesService.putExtra("note",cardItemsList.get(cardPosition).getNote());
        deleteFromFilesService.putExtra("taskEvent",cardItemsList.get(cardPosition).getTask());
        deleteFromFilesService.putExtra("time",cardItemsList.get(cardPosition).getTime());

        removeItem(cardPosition);
        deleteConfirmFragment.dismiss();
        editDelete.getDialog().dismiss();

        startService(deleteFromFilesService);
        cardClickedListener();

        //Reset DELETESTATUS FLAG
        DELETESTATUS = "";

        Log.i(TAG, "item deleted");
    }

    public void deleteCancelClicked(View view)
    {
        deleteConfirmFragment.dismiss();
    }

    public void editClicked(View view)
    {
        String cardInfoToRetrieve = cardItemsList.get(cardPosition).getTime()+"|"+cardItemsList.get(cardPosition).getTask()+"|"+cardItemsList.get(cardPosition).getNote();
        Intent compareCardService = new Intent(this, CompareCardService.class);
        compareCardService.putExtra("cardInfoToRetrieve",cardInfoToRetrieve);
        compareCardService.putExtra("fileName",currentDate.substring(0,1));
        editClicked = true;
        startService(compareCardService);
        editDelete.getDialog().dismiss();
    }

    public boolean getEditClicked()
    {
        return editClicked;
    }
}
