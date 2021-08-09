package edu.wit.ontime.ui.main;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import edu.wit.ontime.MainActivity;
import edu.wit.ontime.R;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class ScheduleViewFragment extends Fragment {
    Button logout;
    private FirebaseFunctions mFunctions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        //Date startDate = new Date(savedInstanceState.getLong("startDate"));

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

        CollectionReference users = db.collection("users");
        String authTok = FirebaseAuth.getInstance().getUid();
        Log.d("Token", authTok);
        users
                .whereEqualTo("auth_id", authTok)
        .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d("User", document.getId() + " => " + document.getData());
                            }
                        } else {
                            Log.d("User", "Error getting documents: ", task.getException());
                        }
                    }
                });

        CollectionReference org = db.collection("organizations");

        logout = v.findViewById(R.id.calendarView);
        logout.setOnClickListener(logoutUser1);

        mFunctions = FirebaseFunctions.getInstance();



        addMessage(authTok)
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

                        // ...
                    }
                });


        return v;
    }

    private Task<String> addMessage(String text) {
        // Create the arguments to the callable function.


        return mFunctions.getHttpsCallable("shifts")
                .call(text)
                .continueWith(task -> {
                    //String result = (String) task.getResult().getData();
                    System.out.println(task.getResult().getData());
                    return (String) task.getResult().getData();
                });
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
