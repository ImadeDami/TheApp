package com.theapp.zeathon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    EditText fstName, sndName, phoneNum, mof;
    RadioGroup radioGroup;
    RadioButton radioButton;
    Spinner marital_status;
    Button submit, viewBtn;
    DBHelper dbHelper;
    BroadcastReceiver broadcastReceiver;
    AutoCompleteTextView listView;
    ListDataActivity2 listDataActivity2;

   // ProgressDialog progressDialog;
   // ProgressDialog prgDialog;

    //1 means data is synced and 0 means data is not synced
   // public static final int NAME_SYNCED_WITH_SERVER = 1;
    //public static final int NAME_NOT_SYNCED_WITH_SERVER = 0;
    private static final String TAG = "ListDataActivity2";
    public static final int SYNC_STATUS_OK = 1;
    public static final int SYNC_STATUS_FAILED = 0;

    //a broadcast to know weather the data is synced or not
    public static final String DATA_SAVED_BROADCAST = "net.simplifiedcoding.datasaved";
    public static final String URL_SAVE_NAME = "http://namarkets.com/nasurvey/submit.php";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        listView = findViewById(R.id.firstName);
        sndName = findViewById(R.id.secondName);
        radioGroup = findViewById(R.id.gender);
        phoneNum = findViewById(R.id.phoneNum);
        //mof = findViewById(R.id.mof);
        marital_status = findViewById(R.id.marital_status);

        dbHelper = new DBHelper(this);
        populateListView();
       // prgDialog = new ProgressDialog(this);

        //registering the broadcast receiver to update sync status
        registerReceiver(broadcastReceiver, new IntentFilter(DATA_SAVED_BROADCAST));
        registerReceiver(new NetworkStateChecker2(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));

        dbHelper.getData();
        broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
dbHelper.getData();
            }
        };

        findViewById(R.id.submit).setOnClickListener(this);
        viewBtn = findViewById(R.id.viewBtn);

        viewBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, ListDataActivity2.class);
                startActivity(intent);
            }
        });
   }

    // autocomplete code 06 21 2019 start
    private void populateListView() {
        Log.d(TAG, "populateListView: Displaying data in the ListView.");

// get all data and append to list
        /*Cursor data = dbHelper.getData();
        ArrayList<String> listData = new ArrayList<>();
        while (data.moveToNext()){
            listData.add(data.getString(1));
        }*/

        //create the list adapter and set adapter
       /* ListAdapter adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,listData);
        listView.setAdapter(adapter);
        ((ArrayAdapter) adapter).notifyDataSetChanged(); */

       dbHelper = new DBHelper(this);
        List<String> lables = dbHelper.getAllLabels();
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, lables);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        listView.setAdapter(dataAdapter);

    }
    // autocomplete code 06 21 2019 end

        private void userSignUp(){
        String fName = listView.getText().toString();
        String sName = sndName.getText().toString();
        String phNo = phoneNum.getText().toString();
        String marSt = marital_status.getSelectedItem().toString();
        //String gndr = mof.getText().toString();
        int selectedId = radioGroup.getCheckedRadioButtonId();
        radioButton = findViewById(selectedId);
       final String gndr = radioButton.getText().toString();

        if(fName.isEmpty()) {
            fstName.setError("first name is empty");
            fstName.requestFocus();
         return;
        }

            if(phNo.isEmpty()) {
                phoneNum.setError("phone number is empty");
                phoneNum.requestFocus();
                return;
            }

            if(sName.isEmpty()){
                sndName.setError("second name is empty");
                sndName.requestFocus();
                return;
            }

        /*  boolean insertData = dbHelper.addData(fName, sName, marSt, gndr, phNo SYNC_STATUS_FAILED);

            if(insertData == true){
                Toast.makeText(MainActivity.this, "saved...", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(MainActivity.this, "oops...", Toast.LENGTH_LONG).show();
            }*/

            /** do user registration using api call **/
            Call<ResponseBody> call = RetrofitClient
                    .getInstance()
                    .getNaMarkets()
                    .createUser(fName, sName, gndr, marSt, phNo);

            call.enqueue(new Callback<ResponseBody>() {
                @Override
                public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {


                   try {
                        JSONObject obj = new JSONObject(String.valueOf(response));
                        if (!obj.getBoolean("error")) {
                            //if there is a success
                            //storing the name to sqlite with status synced
                            dbHelper.addData(fName, sName, gndr, marSt, phNo, SYNC_STATUS_OK);
                        } else {
                            //if there is some error
                            //saving the name to sqlite with status unsynced
                            dbHelper.addData(fName, sName, gndr, marSt, phNo, SYNC_STATUS_FAILED);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                    try {
                        //String s = response.body().toString();
                        Toast.makeText(MainActivity.this, "Submitted...", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                        startActivity(intent);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }

                @Override
                public void onFailure(Call<ResponseBody> call, Throwable t) {
                    dbHelper.addData(fName, sName, gndr, marSt, phNo, SYNC_STATUS_FAILED);
                    //Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_LONG).show();
                    Toast.makeText(MainActivity.this,"data has been saved on phone and will submitted once there is internet connection", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent(MainActivity.this, WelcomeActivity.class);
                    startActivity(intent);
                }
            });

        }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.submit:
                userSignUp();
                break;
            case R.id.login:
                //registerUser();
                startActivity(new Intent(this, LoginActivity.class));
                break;
        }

    }

    // Configure access cache when offline
    /*private final Interceptor REWRITE_CACHE_CONTROL_INTERCEPTOR = new Interceptor() {
        @Override
        public okhttp3.Response intercept(Chain chain) throws IOException {
            okhttp3.Response originalResponse = chain.proceed(chain.request());
            if (checkNetworkConnection()) {
                int maxAge = 180; // read from cache for 1 minute
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, max-age=" + maxAge)
                        .build();
            } else {
                int maxStale = 60 * 60 * 24 * 28; // tolerate 4-weeks stale
                return originalResponse.newBuilder()
                        .removeHeader("Pragma")
                        .header("Cache-Control", "public, only-if-cached, max-stale=" + maxStale)
                        .build();
            }
        }
    }; */

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id==R.id.refresh){
            //syncSQLiteMySQLDB();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


public void onStart(){
        super.onStart();
        registerReceiver(broadcastReceiver, new IntentFilter(DbContract.UI_UPDATE_BROADCAST));
    if(SharedPrefManager.getInstance(this).isLoggedIn()){
        Intent intent = new Intent(this, ProfileActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
        
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(broadcastReceiver);
    }

    /** @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                startActivity(new Intent(this, WelcomeActivity.class));
                return true;
        }
        return super.onOptionsItemSelected(item);
    } **/
}
