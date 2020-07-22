package com.example.social_media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.social_media.Extras.Extras;
import com.example.social_media.Model.Users;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;

public class Login_Activity extends AppCompatActivity {
    LoginButton facebookLoginButton;
    SignInButton googleSignInButton;
    GoogleSignInClient googleSignInClient;
    String TAG = "LoginActivity";

    // Button facebook,gmail;
    CallbackManager callbackManager;
    FirebaseAuth firebaseAuth;
    DatabaseReference databaseReference;
    int sign_in_code = 9;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    AccessToken AccessToken;
    boolean isExist = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_);
//        facebook=findViewById(R.id.facebook);
//        gmail=findViewById(R.id.gmail);

        sharedPreferences = getSharedPreferences("SIGN_IN", MODE_PRIVATE);

        editor = sharedPreferences.edit();
        facebookLoginButton = findViewById(R.id.login_button);
        googleSignInButton = findViewById(R.id.signIn);
        if (Extras.isInternetConnected(this)) {


            firebaseAuth = FirebaseAuth.getInstance();

            checkFirebaseUserlogged();

//            checkloginstatus();

            //Facebook SignIn Start

            callbackManager = CallbackManager.Factory.create();
            facebookLoginButton.setReadPermissions(Arrays.asList("public_profile", "email"));
            facebookLoginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
                @Override
                public void onSuccess(LoginResult loginResult) {
                    handleFacebookAccessToken(loginResult.getAccessToken());
                }

                @Override
                public void onCancel() {
                    Log.e("LoginActivity", "Facebook onCancel Call");
                }

                @Override
                public void onError(FacebookException error) {
                    Log.e("LoginActivity", "Error  " + error);
                }
            });

            // Google Sign Start

            GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build();
            googleSignInClient = GoogleSignIn.getClient(this, gso);
            googleSignInButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    signIn();
                }
            });
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == sign_in_code) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        } else {
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }

    }

    AccessTokenTracker accessTokenTracker = new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {
            if (currentAccessToken == null) {
                Toast.makeText(Login_Activity.this, "USer Logged out", Toast.LENGTH_SHORT).show();

            } else {
                AccessToken = currentAccessToken;
            }

        }
    };

    void loadFacebookprofile(AccessToken newAccesstoken) {
        GraphRequest request = GraphRequest.newMeRequest(newAccesstoken, new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                try {
                    String first_name = object.getString("first_name");
                    String last_name = object.getString("last_name");
                    String email = object.getString("email");
                    String id = object.getString("id");
                    String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                    String temp = first_name + "  " + last_name + "  " + email + " " + id;
                    Log.e("Image Url ", image_url);


                    if (!isExist) {
                        submitData(first_name, email, image_url);
                    }
                    Intent intent = new Intent(getApplicationContext(), MainActivity2.class);

                    startActivity(intent);
                    finish();


//                    Toast.makeText(Login_Activity.this, "IS store"+is, Toast.LENGTH_SHORT).show();


                    //  facebook.setText(temp);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        Bundle patrameters = new Bundle();
        patrameters.putString("fields", "first_name,last_name,email,id");
        request.setParameters(patrameters);
        request.executeAsync();
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
//        FirebaseUser currentUser = firebaseAuth.getCurrentUser();
        // updateUI(currentUser);
    }

    private void handleFacebookAccessToken(AccessToken token) {
        Log.d("Calling login", "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Debug", "signInWithCredential:success");
                            FirebaseUser user = firebaseAuth.getCurrentUser();
                            if (AccessToken != null) {
                                getExist(2);


                            } else {
                                Toast.makeText(Login_Activity.this, "Can't get AccessToken", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("w ..", "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }

    void signIn() {
        Intent signInIntent = googleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, sign_in_code);


    }

    void handleSignInResult(Task<GoogleSignInAccount> task) {
        try {
            GoogleSignInAccount acc = task.getResult(ApiException.class);
            Toast.makeText(this, "SignIn Successfull", Toast.LENGTH_SHORT).show();
            firebaseGoogleAuth(acc);
        } catch (ApiException e) {
            Toast.makeText(this, "SignIn Not Successfull", Toast.LENGTH_SHORT).show();
            firebaseGoogleAuth(null);
        }
    }

    void firebaseGoogleAuth(GoogleSignInAccount acc) {
        if (!String.valueOf(acc).equals("null")) {
            AuthCredential authCredential = GoogleAuthProvider.getCredential(acc.getIdToken(), null);
            firebaseAuth.signInWithCredential(authCredential).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        Toast.makeText(Login_Activity.this, "Successfull", Toast.LENGTH_SHORT).show();

                        getExist(1);
                        Intent i = new Intent(Login_Activity.this, MainActivity2.class);
                        startActivity(i);
                        finish();
                    } else {
                        Toast.makeText(Login_Activity.this, "Not Successfull", Toast.LENGTH_SHORT).show();
                        updateUI(null);
                    }
                }

            });
        }
    }

    void updateUI(FirebaseUser user) {

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());

        if (user != null) {
            String name = account.getDisplayName();
            String givenName = account.getGivenName();
            String email = account.getEmail();
            String familyNmae = account.getFamilyName();
            String id = account.getId();
            String photo = String.valueOf(account.getPhotoUrl());

            // String temp=name+" "+givenName+" "+email+" "+familyNmae+" "+id+" "+photo;

            if (!isExist) {
                submitData(name, email, photo);
                isExist = false;
            }



        }
    }

    void submitData(String name, String email, String image) {
        firebaseAuth = FirebaseAuth.getInstance();
        FirebaseUser user = firebaseAuth.getCurrentUser();
        String userid = user.getUid();
        databaseReference = FirebaseDatabase.getInstance().getReference("Users").child(userid);

        HashMap<String, String> hs = new HashMap<>();
        hs.put("name", name);
        hs.put("email", email);
        hs.put("id", userid);
        hs.put("imageURL", image);
        hs.put("status", "offline");
        hs.put("discription"," ");


        databaseReference.setValue(hs).addOnCompleteListener(
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e("RegisterSuceess", "Done ");

                        }
                    }
                });


    }


    void checkloginstatus() {
        if (AccessToken.getCurrentAccessToken() != null) {
            GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
                @Override
                public void onCompleted(JSONObject object, GraphResponse response) {
                    try {
                        Log.e(TAG, " " + object);
                        String name = object.getString("first_name");
                        String id = object.getString("id");
                        String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";
                        Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                        intent.putExtra("image", image_url);
                        intent.putExtra("name", name);
                        startActivity(intent);
                        finish();

                    } catch (Exception e) {
                        Log.e("ExceptionLoginAc", "... Occures" + e);
                    }

                }
            });
            Bundle patrameters = new Bundle();
            patrameters.putString("fields", "first_name,last_name,email,id");
            request.setParameters(patrameters);
            request.executeAsync();
        } else {
            String check = String.valueOf(GoogleSignIn.getLastSignedInAccount(getApplicationContext()));
            if (!check.equals("null")) {
                GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getApplicationContext());
                String photo = String.valueOf(account.getPhotoUrl());
                String name = account.getDisplayName();
                Intent intent = new Intent(getApplicationContext(), MainActivity2.class);
                intent.putExtra("name", name);
                intent.putExtra("image", photo);
                startActivity(intent);
                finish();
            }
        }
    }


    private boolean getExist(int id) {
        String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference("/Users");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    Users users = dataSnapshot.getValue(Users.class);
                    if (uid.equals(users.getId())) {
                        isExist = true;

                    }


                }

                if (id == 1) {
                    FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                    updateUI(firebaseUser);

                } else if (id == 2) {
                    loadFacebookprofile(AccessToken);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return isExist;
    }


    private void checkFirebaseUserlogged()
    {
        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
        {
            Intent intent=new Intent(Login_Activity.this,MainActivity2.class);
            startActivity(intent);
            finish();
        }
    }

    }

