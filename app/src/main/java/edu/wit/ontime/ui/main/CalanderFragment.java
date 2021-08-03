package edu.wit.ontime.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import edu.wit.ontime.R;

public class CalanderFragment extends Fragment {
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_schedule_view, container, false);

        TextView tv = (TextView) v.findViewById(R.id.year);
        tv.setText(getArguments().getString("msg"));
        return v;
    }

    public static CalanderFragment newInstance(String text) {

        CalanderFragment f = new CalanderFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }
}
