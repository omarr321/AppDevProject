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

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;


import androidx.activity.OnBackPressedCallback;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.type.DateTime;

import edu.wit.ontime.R;


public class HomeFragment extends Fragment {

    TextView time;
    String currentTimeFormat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_main, container, false);

        currentTimeFormat = new SimpleDateFormat("HHmm", Locale.getDefault()).format(new Date());
        //System.out.println(currentTimeFormat + "HERE!");
        time = v.findViewById(R.id.currentTime);



        Date date = null;
        try {
            date = new SimpleDateFormat("hhmm").parse(String.format("%04d", Integer.parseInt(currentTimeFormat)));
        } catch (ParseException e) {
            e.printStackTrace();
        }
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");
        System.out.println(sdf.format(date));
        time.setText(sdf.format(date));

        return v;
    }

    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public static HomeFragment newInstance(String text) {

        HomeFragment f = new HomeFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }


}

