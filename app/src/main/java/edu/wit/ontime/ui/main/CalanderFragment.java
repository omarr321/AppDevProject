package edu.wit.ontime.ui.main;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
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
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frahment_calander_view, container, false);

        view = v.findViewById(R.id.calendarViewFragment);
        changeDate = v.findViewById(R.id.calendarTextView);
        returnButton = v.findViewById(R.id.returnToScheduleButton);
        returnButton.setOnClickListener(returnToUser);
        mFunctions = FirebaseFunctions.getInstance();
        authTok = FirebaseAuth.getInstance().getUid();

        view.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String  curDate = String.valueOf(dayOfMonth);
            String  Year = String.valueOf(year);
            String  Month = String.valueOf(month);
            GregorianCalendar storedDate = new GregorianCalendar(year, month, dayOfMonth);


            addMessage(storedDate.getTime())
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


                            }else{
                                System.out.println(task.getResult());
                            }

                        }
                    });

            String date = " Day:" + curDate + " 10:30 PM - 3:30 AM";
            changeDate.setText(date);
        });




        return v;



    }


    private Task<String> addMessage(Date text) {
        System.out.println("RAN!");
        System.out.println(text.getTime());
        Map<String, Object> data = new HashMap<>();
        data.put("time_start", text.getTime());
        data.put("time_end", Long.valueOf(1629001818));
        // Create the arguments to the callable function.


        return mFunctions.getHttpsCallable("shifts")
                .call(data)
                .continueWith(task -> {
                    //String result = (String) task.getResult().getData();
                    System.out.println(task.getResult().getData());
                    System.out.println();
                    return (String) task.getResult().getData();
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


         Bundle toSchView = new Bundle();
         Instant i = Instant.now();
         toSchView.putLong("startDate", i.toEpochMilli());
         test.setArguments(toSchView);

        getChildFragmentManager().beginTransaction().replace(R.id.test321, test).commit();
    };


}
