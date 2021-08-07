package edu.wit.ontime.ui.main;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;

import edu.wit.ontime.MainActivity;
import edu.wit.ontime.R;


public class HostedFragmentTest extends Fragment {
    private Fragment fragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_container, container, false);

        ScheduleViewFragment test = new ScheduleViewFragment();
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
