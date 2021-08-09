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
import edu.wit.ontime.ui.main.ExecuteOnCaller;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;


import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.firebase.functions.HttpsCallableResult;

import org.w3c.dom.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

public class ScheduleViewFragment extends Fragment {
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseFunctions func = FirebaseFunctions.getInstance();
    public String[] id = {""};
    Button logout;
    String authTok;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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
        authTok = FirebaseAuth.getInstance().getUid();
        final String[] temp = new String[3];

        //id[0] = getUserDocID(authTok, users);



        //Log.d("ID___123", id[0]);
        CollectionReference org = db.collection("organizations");
        //DocumentReference doc = db.collection("users").document(id[0]);
//        db.collectionGroup("members").whereEqualTo("user_id", doc.getPath()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if (task.isSuccessful()) {
//                    for (QueryDocumentSnapshot document: task.getResult()) {
//                        DocumentReference parent = document.getReference().getParent().getParent();
//                        Log.d("Document", parent.toString());
//                    }
//                } else {
//                    Log.d("Document", "Error getting documents: ", task.getException());
//                }
//            }
//        });

        callShifts(null, null, new Date(100), new Date(100))
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
                        } else {
                            Log.d("DEBUG", task.getResult());
                            temp[0] = task.getResult();
                        }
                    }
                });

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

    private Task<String> callShifts(DocumentReference org_id, DocumentReference schedule_id, Date time_start, Date time_end) {
        // Create the arguments to the callable function.
        Map<String, Object> data = new HashMap<>();
        data.put("organization_id", org_id);
        data.put("schedule_id", schedule_id);
        data.put("time_start", time_start);
        data.put("time_end", time_end);

        return func
                .getHttpsCallable("hoursAccumulated")
                .call(authTok)
                .continueWith(new Continuation<HttpsCallableResult, String>() {
                    @Override
                    public String then(@NonNull Task<HttpsCallableResult> task) throws Exception {
                        // This continuation runs on either success or failure, but if the task
                        // has failed then getResult() will throw an Exception which will be
                        // propagated down.
                        String result = (String) task.getResult().getData();
                        return result;
                    }
                });
    }

    public String getUserDocID(String authTok, CollectionReference users) {
        final String[] temp = new String[1];
        ExecuteOnCaller test = new ExecuteOnCaller();
        new Thread(new Runnable(){

            @Override
            public void run(){
                try {
                        Tasks.await(users
                                .whereEqualTo("auth_id", authTok)
                                .get()
                                .addOnCompleteListener(test, new OnCompleteListener<QuerySnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                        if (task.isSuccessful()) {
                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                Log.d("User", document.getId() + " => " + document.getData());
                                                temp[0] = document.getId();
                                            }
                                        } else {
                                            Log.d("User", "Error getting documents: ", task.getException());
                                        }
                                    }
                                }));
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
            }
        }).start();
        return temp[0];
    }


}
