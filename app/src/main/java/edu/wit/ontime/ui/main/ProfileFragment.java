package edu.wit.ontime.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import edu.wit.ontime.R;
import com.bumptech.glide.Glide;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.OptionalPendingResult;
import com.google.android.gms.common.api.Result;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment implements GoogleApiClient.OnConnectionFailedListener {

    Button logout;
    ImageView profile;
    TextView username, userEmail, userID;
    FirebaseUser user;
    GoogleApiClient googleApiClient;
    GoogleSignInOptions gso;
    GoogleSignInAccount account;
    private ProfileFragment ProfileFragment;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        user = getArguments().getParcelable("user");

        View v = inflater.inflate(R.layout.fragment_profile, container, false);


        username = v.findViewById(R.id.name);
        userEmail = v.findViewById(R.id.email);
        userID = v.findViewById(R.id.userId);
        logout = v.findViewById(R.id.logoutBtn);
        profile = v.findViewById(R.id.profileImage);
        logout.setOnClickListener(logoutUser);

        if(userID.getText().toString().equals("id")){
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestEmail()
                    .requestIdToken(getString(R.string.web_client_id))
                    .build();
            googleApiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(), this)
                    .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                    .build();
        }





        return v;
    }

    @Override
    public void onStart() {
        super.onStart();

        if(userID.getText().toString().equals("id")){
            OptionalPendingResult <GoogleSignInResult> opr = Auth.GoogleSignInApi.silentSignIn(googleApiClient);
            if(opr.isDone()){
                GoogleSignInResult result = opr.get();
                handleSignIn(result);
            } else {
                opr.setResultCallback(googleSignInResult -> handleSignIn(googleSignInResult));
            }
        }

    }

    public static ProfileFragment newInstance(String text) {

        ProfileFragment f = new ProfileFragment();
        Bundle b = new Bundle();
        b.putString("msg", text);

        f.setArguments(b);

        return f;
    }

    private void handleSignIn(GoogleSignInResult result) {
        if(result.isSuccess()){
            GoogleSignInAccount account = result.getSignInAccount();
            username.setText(account.getDisplayName());
            userEmail.setText(account.getEmail());
            userID.setText(account.getId());
        }

        FirebaseUser mUser = FirebaseAuth.getInstance().getCurrentUser();
        try{
            Glide.with(getActivity()).load(mUser.getPhotoUrl()).into(profile);
        } catch(NullPointerException e){
            System.out.println(e);
            //Toast.makeText(getActivity(), "image not found", Toast.LENGTH_LONG).show();
        }


    }

    private View.OnClickListener logoutUser = v -> {
        System.out.println("Did it");
        FirebaseAuth.getInstance().signOut();
        signOut();
    };


    private void signOut(){

        GoogleSignInClient googleClient = GoogleSignIn.getClient(getActivity(),gso);
        googleClient.signOut();

        Intent intent = new Intent(getActivity().getBaseContext(),SignInActivity.class);
        intent.putExtra("boolean",true);
        startActivity(intent);
    }

    @Override
    public void onConnectionFailed(@NonNull @NotNull ConnectionResult connectionResult) {

    }

    @Override
    public void onPause() {
        super.onPause();
        googleApiClient.stopAutoManage(getActivity());
        googleApiClient.disconnect();

    }
}
