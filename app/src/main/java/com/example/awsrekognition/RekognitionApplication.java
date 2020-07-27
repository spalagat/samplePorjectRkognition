package com.example.awsrekognition;

import android.app.Application;
import android.util.Log;

import com.amplifyframework.AmplifyException;
import com.amplifyframework.auth.cognito.AWSCognitoAuthPlugin;
import com.amplifyframework.core.Amplify;
import com.amplifyframework.storage.s3.AWSS3StoragePlugin;

class RekognitionApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        try {
            Amplify.addPlugin(new AWSCognitoAuthPlugin());
            Amplify.addPlugin(new AWSS3StoragePlugin());
            Amplify.configure(getApplicationContext());
            Log.i("Rkognition","Initialized the app");
        } catch (AmplifyException e) {
            e.printStackTrace();
        }
    }
}
