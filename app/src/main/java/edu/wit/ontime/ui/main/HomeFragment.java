package edu.wit.ontime.ui.main;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;
import com.google.type.DateTime;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import edu.wit.ontime.R;


public class HomeFragment extends Fragment {

    private FirebaseFunctions mFunctions;
    private Date startDate;
    private Date endDate;
    TextView time;
    String currentTimeFormat;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        Instant i = Instant.now();
        startDate = new Date(i.toEpochMilli());

        currentTimeFormat = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
        //System.out.println(currentTimeFormat + "HERE!");
        time = v.findViewById(R.id.currentTime);



        Date date = null;
        try {
            date = new SimpleDateFormat("hhmm").parse(String.format("%04d", Integer.parseInt(currentTimeFormat)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        System.out.println(sdf.format(date));
        time.setText(sdf.format(date));

        mFunctions = FirebaseFunctions.getInstance();

        String authTok = FirebaseAuth.getInstance().getUid();
        addMessage(authTok)
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Exception e = task.getException();
                            if (e instanceof FirebaseFunctionsException) {
                                FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                FirebaseFunctionsException.Code code = ffe.getCode();
                                Object details = ffe.getDetails();
                            }
                            Log.d("HOME Object Details", e.toString());
                        }else{
                            Log.d("HOME dataGotten", task.getResult());
                            try {
                                int shiftCount = 0;
                                JSONArray temp = new JSONArray(task.getResult());
                                JSONArray shiftsArr = new JSONArray();
                                for (int i = 0; i < temp.length(); i++) {
                                    shiftsArr.put(temp.get(i));
                                }
                                for (int i = 0; i < shiftsArr.length(); i++) {
                                    Log.d("HOME shift" + i, shiftsArr.get(i).toString());
                                    Date[] shiftTimes = ShiftToDate((JSONObject) shiftsArr.get(i));
                                    Log.d("HOME shift start", shiftTimes[0].toString());
                                    Log.d("HOME shift end", shiftTimes[1].toString());

                                    String startDateFormat;
                                    String endDateFormat;
                                    startDateFormat = FormatDateToString(shiftTimes[0].toString());
                                    endDateFormat = FormatDateToString(shiftTimes[1].toString());
                                    Log.d("HOME formatted str", startDateFormat + " - " + endDateFormat);
                                }

                            } catch (JSONException | ParseException e) {
                                e.printStackTrace();
                            }
                        }

                        // ...
                    }
                });



        return v;
    }

    private String FormatDateToString(String shiftDate) throws ParseException {
        DateFormat dateFormat = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy");
        Date date1 = dateFormat.parse(shiftDate);

        Date date = null;
        try {
            date = new SimpleDateFormat("hhmm").parse(String.format("%04d", Integer.parseInt(date1.getHours() + "" + date1.getMinutes() + "0")));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

        return date1.getDate() + " " + sdf.format(date);
    }


    private Date[] ShiftToDate(JSONObject obj) throws JSONException {
        JSONObject shiftTime = obj.getJSONObject("time_start");
        Date shiftStart = new Date(shiftTime.getLong("_seconds")*1000);
        shiftTime = obj.getJSONObject("time_end");
        Date shiftEnd = new Date(shiftTime.getLong("_seconds")*1000);
        Date[] temp = {shiftStart, shiftEnd};
        return temp;
    }


    private Task<String> addMessage(String text) {
        Map<String, Object> data = new HashMap<>();
        data.put("time_start", startDate.getTime());
        data.put("time_end", startDate.getTime() + (86400000));

        System.out.println(startDate.getTime() + "Here!!! - home");


        return mFunctions.getHttpsCallable("shifts")
                .call(data)
                .continueWith(task -> {
                    Gson g = new Gson();
                    return g.toJson(task.getResult().getData());
                });
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static HomeFragment newInstance(String text) {

        HomeFragment f = new HomeFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


}

