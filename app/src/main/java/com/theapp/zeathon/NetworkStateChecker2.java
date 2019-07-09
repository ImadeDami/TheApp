package com.theapp.zeathon;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NetworkStateChecker2 extends BroadcastReceiver {
    private Context context;
    private DBHelper db;

    @Override
    public void onReceive(Context context, Intent intent) {
        this.context = context;

       db = new DBHelper(context);
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();

        //if there is a network
        if (activeNetwork != null) {
            //if connected to wifi or mobile data plan
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI || activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {

                //getting all the unsynced names
                Cursor cursor = db.getUnsyncedNames();
                if (cursor.moveToFirst()) {
                    do {
                        //calling the method to save the unsynced name to MySQL
                        userSignUp(
                                cursor.getInt(cursor.getColumnIndex(DBHelper.COL1)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.COL2)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.COL3)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.COL4)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.COL5)),
                                cursor.getString(cursor.getColumnIndex(DBHelper.COL6))
                        );
                    } while (cursor.moveToNext());
                }
            }
        }
    }

    private void userSignUp(final int id, String fName, String sName, String gndr, String marSt, String phNo) {
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
                        //updating the status in sqlite
                        db.updateNameStatus(id, MainActivity.SYNC_STATUS_OK);

                        //sending the broadcast to refresh the list
                        context.sendBroadcast(new Intent(MainActivity.DATA_SAVED_BROADCAST));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Map<String, String> params = new HashMap<>();
                params.put("fname", fName);
                params.put("sname", sName);
                params.put("gndr", gndr);
                params.put("marSt", marSt);
                params.put("phNo", phNo);
                return;
            }
        });
       // RetrofitClient.getInstance().getNaMarkets();

    }

}


