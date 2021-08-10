package edu.wit.ontime.ui.main;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import edu.wit.ontime.R;


public class HomeFragment extends Fragment {

    private FirebaseFunctions mFunctions;
    private Date startDate;
    private TextView time;
    private TextView schedule;
    private int hours = 0;
    private Button punchIn, punchOut, onBreak, offBreak;
    private TextView statusText;
    private JSONObject member;
    private String status = " ";
    private int yellow, blue, green, light_green;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        yellow = ResourcesCompat.getColor(getResources(), R.color.yellow, null);
        blue = ResourcesCompat.getColor(getResources(), R.color.teal_700, null);
        green = ResourcesCompat.getColor(getResources(), R.color.green, null);
        light_green = ResourcesCompat.getColor(getResources(), R.color.light_green, null);

        punchIn = v.findViewById(R.id.punchIn);
        punchOut = v.findViewById(R.id.punchOut);
        onBreak = v.findViewById(R.id.onBreak);
        offBreak = v.findViewById(R.id.offBreak);

        punchIn.setVisibility(View.GONE);
        punchOut.setVisibility(View.GONE);
        onBreak.setVisibility(View.GONE);
        offBreak.setVisibility(View.GONE);

        statusText = v.findViewById(R.id.statusText);

        Instant i = Instant.now();
        startDate = new Date(i.toEpochMilli());
        time = v.findViewById(R.id.currentTime);
        schedule = v.findViewById(R.id.currentShift);

        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        time.setText(sdf.format(startDate));

        mFunctions = FirebaseFunctions.getInstance();





        String authTok = FirebaseAuth.getInstance().getUid();
        getCurrShift(authTok)
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
                            Log.d("HOME dataGotten Shifts", task.getResult());
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
                                    startDateFormat = FormatDateToString(shiftTimes[0]);
                                    endDateFormat = FormatDateToString(shiftTimes[1]);
                                    String shiftView = startDateFormat + " - " + endDateFormat;
                                    Log.d("HOME formatted str", shiftView);
                                    schedule.setText(shiftView);
                                }
                                if (shiftsArr.length() == 0) {
                                    Log.d("HOME formatted str", "No Shift");
                                    schedule.setText("No Shift");
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // ...
                    }
                });

        getHourAccrued(authTok)
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
                            try {
                                JSONObject totalHour = new JSONObject(task.getResult());
                                hours = totalHour.getInt("total_hours");
                                Log.d("HOME Total Hours", String.valueOf(hours));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            TextView hoursWorked = v.findViewById(R.id.todayHours);
                            TextView todayGross = v.findViewById(R.id.todayGross);
                            TextView todayNet = v.findViewById(R.id.todayNet);

                            getOrg(authTok)
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
                                                try{
                                                    JSONArray temp = new JSONArray(task.getResult());

                                                    JSONObject org = (JSONObject) temp.get(0);

                                                    Log.d("HOME Org Data", org.toString());

                                                    member = (JSONObject) org.get("member");
                                                    Log.d("HOME Member Data", member.toString());
                                                    updateStatus();

                                                    JSONObject pay = (JSONObject) member.get("pay");
                                                    Log.d("HOME Pay Data", pay.toString());
                                                    double dailyPay = pay.getInt("amount");
                                                    String type = pay.getString("type");

                                                    hoursWorked.setText("Today's Hours: " + hours);

                                                    if (type.equals("hourly")) {
                                                        todayGross.setText("Today's Gross: $" + (hours*dailyPay));
                                                        todayNet.setText("Today's Net: $" + ((hours*dailyPay)*.9375));
                                                    } else {
                                                        todayGross.setText("Today's Gross: N/A");
                                                        todayNet.setText("Today's Net: N/A");
                                                    }

                                                } catch (JSONException e) {
                                                    e.printStackTrace();
                                                }
                                            }

                                            // ...
                                        }
                                    });
                        }

                        // ...
                    }
                });

        punchIn.setOnClickListener(pi -> {
            status = "working";
            punch(authTok)
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

                            }
                        }
                    });
            updateStatus();
        });

        punchOut.setOnClickListener(po -> {
            status = "active";
            punch(authTok)
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

                            }
                        }
                    });
            updateStatus();
        });

        onBreak.setOnClickListener(ob -> {
            status = "break";
            pbreak(authTok)
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

                            }
                        }
                    });
            updateStatus();
        });

        offBreak.setOnClickListener(ofb -> {
            status = "working";
            pbreak(authTok)
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

                            }
                        }
                    });
            updateStatus();
        });

        return v;
    }

    private void updateStatus() {
        if (status.equals(" ")) {
            try {
                status = member.getString("status");
                Log.d("HOME status", status);

            } catch (JSONException e) {
                Log.d("HOME ERROR", "Error getting Status!");
                e.printStackTrace();
            }
        }

        switch (status){
            case "active":
                punchIn.setVisibility(View.VISIBLE);
                punchOut.setVisibility(View.GONE);
                onBreak.setVisibility(View.GONE);
                offBreak.setVisibility(View.GONE);
                if (schedule.getText().equals("No Shift")) {
                    statusText.setText("DAY OFF");
                    statusText.setBackgroundColor(blue);
                } else {
                    statusText.setText("PUNCHED OUT");
                    statusText.setBackgroundColor(yellow);
                }
                break;
            case "working":
                punchIn.setVisibility(View.GONE);
                punchOut.setVisibility(View.VISIBLE);
                onBreak.setVisibility(View.VISIBLE);
                offBreak.setVisibility(View.GONE);
                statusText.setText("PUNCHED IN");
                statusText.setBackgroundColor(green);
                break;
            case "break":
                punchIn.setVisibility(View.GONE);
                punchOut.setVisibility(View.GONE);
                onBreak.setVisibility(View.GONE);
                offBreak.setVisibility(View.VISIBLE);
                statusText.setText("ON BREAK");
                statusText.setBackgroundColor(light_green);
                break;
            default:
                System.out.println("Error: Invalid Status!");
                System.exit(-1);
        }
    }

    private String FormatDateToString(Date shiftDate){
        DateFormat df = new SimpleDateFormat("hh:mm aa");
        return df.format(shiftDate);
    }


    private Date[] ShiftToDate(JSONObject obj) throws JSONException {
        JSONObject shiftTime = obj.getJSONObject("time_start");
        Date shiftStart = new Date(shiftTime.getLong("_seconds")*1000);
        shiftTime = obj.getJSONObject("time_end");
        Date shiftEnd = new Date(shiftTime.getLong("_seconds")*1000);
        Date[] temp = {shiftStart, shiftEnd};
        return temp;
    }


    private Task<String> punch(String text) {
        return mFunctions.getHttpsCallable("punch")
                .call()
                .continueWith(task -> {
                    Gson g = new Gson();
                    return g.toJson(task.getResult().getData());
                });
    }

    private Task<String> pbreak(String text) {
        return mFunctions.getHttpsCallable("break")
                .call()
                .continueWith(task -> {
                    Gson g = new Gson();
                    return g.toJson(task.getResult().getData());
                });
    }

    private Task<String> getCurrShift(String text) {
        Map<String, Object> data = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.DAY_OF_WEEK, -1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        data.put("time_start", cal.getTime().getTime());

        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        data.put("time_end", cal.getTime().getTime());

        //System.out.println(startDate.getTime() + "Here!!! - home");


        return mFunctions.getHttpsCallable("shifts")
                .call(data)
                .continueWith(task -> {
                    Gson g = new Gson();
                    return g.toJson(task.getResult().getData());
                });
    }

    private Task<String> getOrg(String text) {
        return mFunctions.getHttpsCallable("getOrganizations")
                .call()
                .continueWith(task -> {
                    Gson g = new Gson();
                    return g.toJson(task.getResult().getData());
                });
    }

    private Task<String> getHourAccrued(String text) {
        Map<String, Object> data = new HashMap<>();
        Calendar cal = Calendar.getInstance();
        cal.setTime(startDate);
        cal.set(Calendar.DAY_OF_WEEK, -1);
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        data.put("time_start", cal.getTime().getTime());

        cal.set(Calendar.HOUR, 23);
        cal.set(Calendar.MINUTE, 59);
        cal.set(Calendar.SECOND, 59);
        cal.set(Calendar.MILLISECOND, 999);
        data.put("time_end", cal.getTime().getTime());

        return mFunctions.getHttpsCallable("hoursAccumulated")
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

