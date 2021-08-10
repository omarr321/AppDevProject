package edu.wit.ontime.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import edu.wit.ontime.R;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.functions.FirebaseFunctions;
import com.google.firebase.functions.FirebaseFunctionsException;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

public class PayStubFragment extends Fragment {
    private FirebaseFunctions mFunctions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_pay_stub, container, false);

        mFunctions = FirebaseFunctions.getInstance();
        TextView tv = (TextView) v.findViewById(R.id.txtPayRate);
        tv.setText(getArguments().getString("msg"));

        addMessage("")
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
                            Gson g = new GsonBuilder().setPrettyPrinting().create();
                            JsonElement json = new JsonParser().parse(task.getResult());
                            String json32 = g.toJson(json);
                            System.out.println(json32);


                        }

                    }
                });


        return v;
    }

    public static PayStubFragment newInstance(String text) {

        PayStubFragment f = new PayStubFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    private Task<String> addMessage(String text) {
        return mFunctions.getHttpsCallable("getOrganizations")
                .call(" ")
                .continueWith(task -> {
                    //String result = (String) task.getResult().getData();
                    Gson g = new Gson();
                    return g.toJson(task.getResult().getData());
                });
    }
}
