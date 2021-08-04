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

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

public class ScheduleViewFragment extends Fragment {
    Button logout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_view, container, false);

        TextView tv = (TextView) v.findViewById(R.id.year);
        tv.setText(getArguments().getString("msg"));

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

    private View.OnClickListener logoutUser1 = v -> {
        final Dialog fbDialogue = new Dialog(getActivity(), android.R.style.Theme_Black_NoTitleBar);
        //fbDialogue.getWindow().setBackgroundDrawable(new ColorDrawable(Color.argb(100, 0, 0, 0)));
        fbDialogue.setContentView(R.layout.frahment_calander_view);
        fbDialogue.setCancelable(true);
        fbDialogue.show();
    };
}
