package edu.wit.ontime.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CalendarView;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import edu.wit.ontime.R;

public class CalanderFragment extends Fragment {
    CalendarView view;
    Button returnButton;
    TextView changeDate;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.frahment_calander_view, container, false);

        view = v.findViewById(R.id.calendarViewFragment);
        changeDate = v.findViewById(R.id.calendarTextView);
        returnButton = v.findViewById(R.id.returnToScheduleButton);
        returnButton.setOnClickListener(returnToUser);


        view.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            String  curDate = String.valueOf(dayOfMonth);
            String  Year = String.valueOf(year);
            String  Month = String.valueOf(month);

            String date = " Day:" + curDate + " 10:30 PM - 3:30 AM";
            changeDate.setText(date);
        });


        return v;



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
        getChildFragmentManager().beginTransaction().replace(R.id.test321, test).commit();
    };


}
