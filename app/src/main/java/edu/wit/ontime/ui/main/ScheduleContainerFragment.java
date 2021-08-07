package edu.wit.ontime.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;


import edu.wit.ontime.R;

public class ScheduleContainerFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_container, container, false);


        return v;
    }

    public static ScheduleContainerFragment newInstance(String text) {

        ScheduleContainerFragment f = new ScheduleContainerFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

}

