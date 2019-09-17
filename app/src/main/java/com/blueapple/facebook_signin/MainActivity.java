package com.blueapple.facebook_signin;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.se.omapi.Session;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.Request;
import com.bumptech.glide.request.target.BitmapImageViewTarget;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity {

    LoginButton loginButton;
    ImageView imageView;
    TextView textView,text_email;
    String email;
    LoginManager fbLoginManager;
    Button button;


    private CallbackManager callbackManager;
    SharedPreferences sharedPreferences,preferences;
    SharedPreferences.Editor editor;

    Session facebookSession;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        loginButton = findViewById(R.id.login_button);
        imageView = findViewById(R.id.profilepic_id);
        textView = findViewById(R.id.text_nameid);
        text_email = findViewById(R.id.text_emailid);
        button = findViewById(R.id.btn_loginid);

        

        // loginButton.setReadPermissions(Arrays.asList("email","public_profile"));

        sharedPreferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        preferences = getSharedPreferences("pref", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        String name = preferences.getString("name", null);
        if (name != null) {
            //   Toast.makeText(this, "signed in", Toast.LENGTH_SHORT).show();


            final Profile profile = Profile.getCurrentProfile();


            Intent intent = new Intent(MainActivity.this, AfterLogin_Activity.class);
            intent.putExtra("username", profile.getFirstName()+" "+profile.getLastName());
            intent.putExtra("imageurl", profile.getProfilePictureUri(150, 150).toString());

            Toast.makeText(MainActivity.this, "" + profile.getFirstName(), Toast.LENGTH_SHORT).show();

            editor.putString("name", profile.getFirstName());
            editor.commit();

            startActivity(intent);



        }

        FacebookSdk.sdkInitialize(this.getApplicationContext());

        callbackManager = CallbackManager.Factory.create();

        LoginManager.getInstance().registerCallback(callbackManager,
                new FacebookCallback<LoginResult>() {
                    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        Log.d("Success", "Login");


                      //  Toast.makeText(MainActivity.this, "success"+loginResult.getAccessToken(), Toast.LENGTH_SHORT).show();

                        Log.d("user_id",loginResult.getAccessToken().getUserId());
                        Profile profile=Profile.getCurrentProfile();

                        Intent intent=new Intent(MainActivity.this,AfterLogin_Activity.class);
                        intent.putExtra("username",profile.getFirstName());
                        intent.putExtra("imageurl",profile.getProfilePictureUri(150,150).toString());


                       // Toast.makeText(MainActivity.this, ""+profile.getFirstName(), Toast.LENGTH_SHORT).show();

                        editor.putString("name",profile.getFirstName());
                        editor.commit();
                        startActivity(intent);
                        finishAffinity();
                    }

                    @Override
                    public void onCancel()
                    {
                        Toast.makeText(MainActivity.this, "Login Cancel", Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        Toast.makeText(MainActivity.this, exception.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });



      button.setOnClickListener(new View.OnClickListener() {
          @Override
          public void onClick(View v) {

              LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));


          }
      });
      /*  String name=preferences.getString("name",null);

    //    Toast.makeText(this, "name : "+name, Toast.LENGTH_SHORT).show();

        if (name!=null)
        {
         //   Toast.makeText(this, "signed in", Toast.LENGTH_SHORT).show();


            startActivity(new Intent(MainActivity.this,AfterLogin_Activity.class));
        }
*/


        //loginButton.setReadPermissions("user_friends");  //
        //loginButton.registerCallback(callbackManager,callback);
    /*    loginButton.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {


            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });*/



    }

   /* @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


        callbackManager.onActivityResult(requestCode,resultCode,data);
        super.onActivityResult(requestCode, resultCode, data);
    }*/
/*
    AccessTokenTracker tracker=new AccessTokenTracker() {
        @Override
        protected void onCurrentAccessTokenChanged(AccessToken oldAccessToken, AccessToken currentAccessToken) {

            if (currentAccessToken==null)
            {
                Toast.makeText(MainActivity.this, "logged out", Toast.LENGTH_SHORT).show();

                text_email.setText("");
                textView.setText("");
                imageView.setImageResource(0);
            }

            else
            {
               // tracker.startTracking();
                Log.d("token: ", String.valueOf(currentAccessToken));
                //getUserProfile(currentAccessToken);

            }





    }
    };*/


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(callbackManager.onActivityResult(requestCode, resultCode, data)) {
            return;
        }
    }

    public void getUserProfile(AccessToken accessToken)
    {
        Toast.makeText(this, "token : "+accessToken, Toast.LENGTH_SHORT).show();
    GraphRequest graphRequest=GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public void onCompleted(JSONObject object, GraphResponse response) {


            try {
                 email=object.getString("email");
                String firstname=object.getString("first_name");
                String lastname=object.getString("last_name");
                String id=object.getString("id");
                String image_url = "https://graph.facebook.com/" + id + "/picture?type=normal";


                textView.setText(firstname+" "+lastname);
                text_email.setText(email);
                Glide.with(MainActivity.this).load(image_url).into(imageView);

                editor.putString("name",firstname);
                editor.commit();


                Intent intent=new Intent(MainActivity.this,AfterLogin_Activity.class);
                intent.putExtra("email",email);
                intent.putExtra("name",firstname+" "+lastname);
                intent.putExtra("image_url",image_url);
                startActivity(intent);

                finishActivity(0);

             //   finishAffinity();

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }
    });

    Bundle bundle=new Bundle();
    bundle.putString("fields","first_name,last_name,email,id");
    graphRequest.setParameters(bundle);
    graphRequest.executeAsync();

    }


}
