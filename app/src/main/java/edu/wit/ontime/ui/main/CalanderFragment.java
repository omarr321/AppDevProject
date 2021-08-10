package edu.wit.ontime.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Type;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Map;

import java.time.Instant;
import edu.wit.ontime.R;

@RequiresApi(api = Build.VERSION_CODES.O)
public class CalanderFragment extends Fragment {
    CalendarView view;
    Button returnButton;
    TextView changeDate;
    private FirebaseFunctions mFunctions;
    String authTok;
    RelativeLayout layout;
    private Date startDate;
    private Date endDate;
    String dateFormat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frahment_calander_view, container, false);

        view = v.findViewById(R.id.calendarViewFragment);
        changeDate = v.findViewById(R.id.calendarTextView);
        returnButton = v.findViewById(R.id.returnToScheduleButton);
        returnButton.setOnClickListener(returnToUser);
        mFunctions = FirebaseFunctions.getInstance();
        authTok = FirebaseAuth.getInstance().getUid();

        layout = v.findViewById(R.id.test321);

        view.setOnDateChangeListener((view, year, month, dayOfMonth) -> {

            //Date startDate = new Date(Instant.now().toEpochMilli());

            String  curDate = String.valueOf(dayOfMonth);
            String  Year = String.valueOf(year);
            String  Month = String.valueOf(month);
            GregorianCalendar cal = new GregorianCalendar(year, month, dayOfMonth);


            cal.set(Calendar.AM_PM, Calendar.AM);
            cal.set(Calendar.HOUR, 0);
            cal.set(Calendar.MINUTE, 0);
            cal.set(Calendar.SECOND, 0);
            cal.set(Calendar.MILLISECOND, 0);

            startDate = cal.getTime();

            cal.set(Calendar.AM_PM, Calendar.PM);
            cal.set(Calendar.HOUR, 11);
            cal.set(Calendar.MINUTE, 59);
            cal.set(Calendar.SECOND, 59);
            cal.set(Calendar.MILLISECOND, 999);

            endDate = cal.getTime();

            long epochCurrentDay = cal.getTime().getTime();


            addMessage(" ")
                    .addOnCompleteListener(new OnCompleteListener<String>() {

                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            changeDate.setText("No Shift");
                            if (!task.isSuccessful()) {
                                Exception e = task.getException();
                                if (e instanceof FirebaseFunctionsException) {
                                    FirebaseFunctionsException ffe = (FirebaseFunctionsException) e;
                                    FirebaseFunctionsException.Code code = ffe.getCode();
                                    Object details = ffe.getDetails();
                                }


                            }else{

                                try{
                                JSONArray temp = new JSONArray(task.getResult());
                                JSONArray shiftsArr = new JSONArray();
                                for (int i = 0; i < temp.length(); i++) {
                                    shiftsArr.put(temp.get(i));
                                }
                                String[] shiftsStr = {" "," "," "," "," "," "," "};
                                for (int i = 0; i < shiftsArr.length(); i++) {
                                    Log.d("SCHEDULE shift " + (i+1), shiftsArr.get(i).toString());
                                    Date[] shiftTimes = ShiftToDate((JSONObject) shiftsArr.get(i));
                                    Log.d("SCHEDULE shift start", shiftTimes[0].toString());
                                    Log.d("SCHEDULE shift end", shiftTimes[1].toString());

                                    String startDateFormat;
                                    String endDateFormat;
                                    startDateFormat = FormatDateToString(shiftTimes[0]);
                                    endDateFormat = FormatDateToString(shiftTimes[1]);
                                    Log.d("SCHEDULE formatted str",startDateFormat + " - " + endDateFormat);
                                    Calendar cal = Calendar.getInstance();
                                    cal.setTime(shiftTimes[0]);
                                    int day = cal.get(Calendar.DAY_OF_WEEK);
                                    shiftsStr[day-1] = startDateFormat + " - " + endDateFormat;

                                    //System.out.println(shiftsStr[0]);
                                    dateFormat = startDateFormat + " - " + endDateFormat;
                                    System.out.println(dateFormat);
                                    if(dateFormat.equals(" ")){
                                        changeDate.setText("No Shift");
                                    }else{
                                        changeDate.setText(dateFormat);
                                    }

                                }} catch (Exception e) {
                                    System.out.println(e.getMessage());
                                }

                            }

                        }
                    });


        });




        return v;



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

    private Task<String> addMessage(String text) {
        System.out.println("RAN!");
        System.out.println(startDate.getTime() + "start");
        System.out.println(endDate.getTime() + "end!");
        Map<String, Object> data = new HashMap<>();
        data.put("time_start", startDate.getTime());
        data.put("time_end", endDate.getTime());

        return mFunctions.getHttpsCallable("shifts")
                .call(data)
                .continueWith(task -> {
                    //String result = (String) task.getResult().getData();
                    System.out.println(task.getResult().getData());
                    Gson g = new Gson();
                    return g.toJson(task.getResult().getData());
                });
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // This callback will only be called when MyFragment is at least Started.


        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }



    public static CalanderFragment newInstance(String text) {

        CalanderFragment f = new CalanderFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    private View.OnClickListener returnToUser = v -> {
        System.out.println("Did it");
        ScheduleViewFragment test = new ScheduleViewFragment();



         layout.removeAllViewsInLayout();
         Bundle toSchView = new Bundle();
         Instant i = Instant.now();
         toSchView.putLong("startDate", i.toEpochMilli());
         test.setArguments(toSchView);

        getChildFragmentManager().beginTransaction().replace(R.id.test321, test).commit();
    };


}
