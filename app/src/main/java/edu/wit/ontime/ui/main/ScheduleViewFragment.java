package edu.wit.ontime.ui.main;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.wit.ontime.MainActivity;
import edu.wit.ontime.R;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ScheduleViewFragment extends Fragment {
    Button logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_view, container, false);
        TextView year = (TextView) v.findViewById(R.id.year);
        TextView month = (TextView) v.findViewById(R.id.month);


        TextView day1Text = (TextView) v.findViewById(R.id.dayOneText);
        TextView day1WH = (TextView) v.findViewById(R.id.dayOneWH);

        TextView day2Text = (TextView) v.findViewById(R.id.dayTwoText);
        TextView day2WH = (TextView) v.findViewById(R.id.dayTwoWH);

        TextView day3Text = (TextView) v.findViewById(R.id.dayThreeText);
        TextView day3WH = (TextView) v.findViewById(R.id.dayThreeWH);

        TextView day4Text = (TextView) v.findViewById(R.id.dayFourText);
        TextView day4WH = (TextView) v.findViewById(R.id.dayFourWH);

        TextView day5Text = (TextView) v.findViewById(R.id.dayFiveText);
        TextView day5WH = (TextView) v.findViewById(R.id.dayFiveWH);

        TextView day6Text = (TextView) v.findViewById(R.id.daySixText);
        TextView day6WH = (TextView) v.findViewById(R.id.daySixWH);

        TextView day7Text = (TextView) v.findViewById(R.id.daySevenText);
        TextView day7WH = (TextView) v.findViewById(R.id.daySevenWH);

        logout = v.findViewById(R.id.calendarView);
        logout.setOnClickListener(logoutUser1);

        return v;
    }

    public static ScheduleViewFragment newInstance(String text) {

        ScheduleViewFragment f = new ScheduleViewFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //FirebaseAuth.getInstance().getAccessToken(true);

        // This callback will only be called when MyFragment is at least Started.
        OnBackPressedCallback callback = new OnBackPressedCallback(true /* enabled by default */) {
            @Override
            public void handleOnBackPressed() {
                // Handle the back button event
            }
        };
        requireActivity().getOnBackPressedDispatcher().addCallback(this, callback);

        // The callback can be enabled or disabled here or in handleOnBackPressed()
    }

    private View.OnClickListener logoutUser1 = v -> {

        CalanderFragment test = new CalanderFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.test123, test).commit();


        /**
        final Dialog fbDialogue = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
        //fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        fbDialogue.setContentView(R.layout.frahment_calander_view);
        fbDialogue.setCancelable(true);
        fbDialogue.show();
         */
    };
}
