package edu.wit.ontime;

import android.os.Build;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.tabs.TabLayout;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;
import androidx.appcompat.app.AppCompatActivity;

import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import edu.wit.ontime.ui.main.CalanderFragment;
import edu.wit.ontime.ui.main.HostedFragmentTest;
import edu.wit.ontime.ui.main.ProfileFragment;
import edu.wit.ontime.ui.main.ScheduleViewFragment;
import edu.wit.ontime.ui.main.SectionsPagerAdapter;
import edu.wit.ontime.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    public static ViewPager viewPager;
    SectionsPagerAdapter sectionsPagerAdapter;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Fragment[] test = {ScheduleViewFragment.newInstance("string"), ProfileFragment.newInstance("test"), CalanderFragment.newInstance("Stest")};

        sectionsPagerAdapter = new SectionsPagerAdapter(this, getSupportFragmentManager());
        viewPager = binding.viewPager;
        viewPager.setAdapter(sectionsPagerAdapter);
        TabLayout tabs = binding.tabs;
        tabs.setupWithViewPager(viewPager);



        /**
        FloatingActionButton fab = binding.fab;

        fab.setOnClickListener(view -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show());
         */
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void openNewContentFragment(String test) {
        HostedFragmentTest hostFragment = (HostedFragmentTest) sectionsPagerAdapter.getItem(viewPager.getCurrentItem());
        hostFragment.replaceFragment(CalanderFragment.newInstance(test), true);
    }

}