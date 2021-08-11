package edu.wit.ontime.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.wit.ontime.R;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.time.Instant;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PayStubFragment extends Fragment {
    private FirebaseFunctions mFunctions;
    private int hours = 0;
    Date startDate, endDate;
    TextView currentHours;
    TextView currentGross;
    TextView currentNet;
    TextView projectedHours;
    TextView projectedGross;
    TextView projectedNet;
    TextView payRate;
    TextView payType;
    Double rateOfPay;
    String typeOfPay;
    DecimalFormat df = new DecimalFormat("#.00");
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_stub, container, false);

        mFunctions = FirebaseFunctions.getInstance();
        TextView tv = (TextView) v.findViewById(R.id.txtPayRate);
        //tv.setText(getArguments().getString("msg"));

        Calendar calendar = getCalendarForNow();
        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMinimum(Calendar.DAY_OF_MONTH));
        setTimeToBeginningOfDay(calendar);
        startDate = calendar.getTime();

        calendar.set(Calendar.DAY_OF_MONTH, calendar.getActualMaximum(Calendar.DAY_OF_MONTH));
        setTimeToEndofDay(calendar);
        endDate = calendar.getTime();

        //System.out.println(startDate + " startDate");
        //System.out.println(endDate + " endDate");
        currentHours = v.findViewById(R.id.txtCurrHW);
        currentGross = v.findViewById(R.id.txtCurrGI);
        currentNet = v.findViewById(R.id.txtCurrNI);
        projectedHours = v.findViewById(R.id.txtProjectedHW);
        projectedGross = v.findViewById(R.id.txtProjectedGI);
        projectedNet = v.findViewById(R.id.txtProjectedNI);
        payRate = v.findViewById(R.id.txtPayRate);
        payType = v.findViewById(R.id.txtPayType);

        currentHours.setText("Current Hours Worked: " + "Loading...");
        currentGross.setText("Current Gross Income: " + "Loading...");
        currentNet.setText("Current Net Income: " + "Loading");


        getOrg("")
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

                                JSONObject member = (JSONObject) org.get("member");
                                Log.d("HOME Member Data", member.toString());

                                JSONObject pay = (JSONObject) member.get("pay");
                                Log.d("HOME Pay Data", pay.toString());
                                double dailyPay = pay.getDouble("amount");
                                String type = pay.getString("type");
                                rateOfPay = dailyPay;
                                typeOfPay = type;
                                payRate.setText("Pay Rate: $" + df.format(rateOfPay));
                                payType.setText("Pay Type: " + typeOfPay);
                                projectedAmount(false);
                                Calendar currentDay = Calendar.getInstance();
                                setTimeToEndofDay(currentDay);
                                endDate = currentDay.getTime();
                                projectedAmount(true);
                                System.out.println(endDate + "GOT HERE");

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });




        return v;
    }


    public void currentAmount(){

    }
    // @param boolean currentOrProjected - true if current false if projected
    public void projectedAmount(boolean currentOrProjected){
        getHourAccrued("")
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
                                if(currentOrProjected){
                                    currentHours.setText("Current Hours Worked: " + String.valueOf(hours));
                                    currentGross.setText("Current Gross Income: $" + (df.format(hours*rateOfPay)));
                                    currentNet.setText("Current Net Income: $" + (df.format(hours*rateOfPay*0.9375)));
                                }else{
                                    projectedHours.setText("Projected Hours Worked: " + String.valueOf(hours));
                                    projectedGross.setText("Projected Gross Income: $" + (df.format(hours*rateOfPay)));
                                    projectedNet.setText("Projected Net Income: $" + (df.format(hours*rateOfPay*0.9375)));
                                }

                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                        // ...
                    }
                });

    }

    public static PayStubFragment newInstance(String text) {

        PayStubFragment f = new PayStubFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
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


        data.put("time_start", startDate.getTime());
        data.put("time_end", endDate.getTime());

        return mFunctions.getHttpsCallable("hoursAccumulated")
                .call(data)
                .continueWith(task -> {
                    Gson g = new Gson();
                    System.out.println(task.getResult().getData() + "HERE!!!!");
                    return g.toJson(task.getResult().getData());
                });
    }


    private static void setTimeToBeginningOfDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
    }

    private static void setTimeToEndofDay(Calendar calendar) {
        calendar.set(Calendar.HOUR_OF_DAY, 23);
        calendar.set(Calendar.MINUTE, 59);
        calendar.set(Calendar.SECOND, 59);
        calendar.set(Calendar.MILLISECOND, 999);
    }

    private static Calendar getCalendarForNow() {
        Calendar calendar = GregorianCalendar.getInstance();
        calendar.setTime(new Date());
        return calendar;
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }
}
