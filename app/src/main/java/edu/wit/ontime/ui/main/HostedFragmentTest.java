package edu.wit.ontime.ui.main;

import android.app.Dialog;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Date;

import edu.wit.ontime.MainActivity;
import edu.wit.ontime.R;


public class HostedFragmentTest extends Fragment {
    private Fragment fragment;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_container, container, false);

        Bundle toSchView = new Bundle();
        Instant i = Instant.now();
        toSchView.putLong("startDate", i.toEpochMilli());

        ScheduleViewFragment test = new ScheduleViewFragment();
        test.setArguments(toSchView);
        getChildFragmentManager().beginTransaction().replace(R.id.hosted_fragment, test).commit();
        return v;
    }

    public static HostedFragmentTest newInstance(String text) {

        HostedFragmentTest f = new HostedFragmentTest();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }



    public void replaceFragment(Fragment fragment, boolean addToBackstack) {

    }

}
