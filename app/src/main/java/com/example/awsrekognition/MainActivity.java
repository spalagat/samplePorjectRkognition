package com.example.awsrekognition;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.List;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.rekognition.RekognitionClient;
import software.amazon.awssdk.services.rekognition.RekognitionClientBuilder;
import software.amazon.awssdk.services.rekognition.model.CompareFacesMatch;
import software.amazon.awssdk.services.rekognition.model.CompareFacesRequest;
import software.amazon.awssdk.services.rekognition.model.CompareFacesResponse;
import software.amazon.awssdk.services.rekognition.model.Image;

public class MainActivity extends AppCompatActivity {

    public static final int REQUEST_IMAGE_CAPTURE=1;
    String currentPhotoPath=null;
    File firstPhotFile=null;
    File secondPhotoFile=null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //AmazonRekognitionClient rekognitionClient=
        findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    dispatchTakePictureIntent();
            }
        });
        findViewById(R.id.secondButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dispatchTakePictureIntent_2();
            }
        });
        findViewById(R.id.compare).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try{
                    compare(firstPhotFile,secondPhotoFile);
                }
                catch (Exception e){
                    e.printStackTrace();
                }

            }
        });


    }
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try{
                firstPhotFile=createImageFile("sandeep");
            }
            catch(Exception e){
                e.printStackTrace();
            }
            if(firstPhotFile!=null){
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Uri photoUri= FileProvider.getUriForFile(getApplicationContext(),"com.example.awsrekognition",firstPhotFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,photoUri);
                startActivityForResult(takePictureIntent,REQUEST_IMAGE_CAPTURE);
            }

        }
    }
    private File createImageFile(String name) throws IOException {
        // Create an image file name
        //String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + name + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        return image;
    }
    private void dispatchTakePictureIntent_2() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            try {
                secondPhotoFile = createImageFile("sai");
            } catch (Exception e) {
                e.printStackTrace();
            }
            if (secondPhotoFile != null) {
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                Uri photoUri = FileProvider.getUriForFile(getApplicationContext(), "com.example.awsrekognition", secondPhotoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }

        }
    }
    private void compare(File file1,File file2) throws IOException {
        SdkBytes sourceBytes=SdkBytes.fromByteArray(convertToByteArray(file1));
        SdkBytes targetBytes=SdkBytes.fromByteArray(convertToByteArray(file2));
        RekognitionClientBuilder rekognitionClientBuilder= RekognitionClient.builder()
                .region(Region.US_EAST_1);
        RekognitionClient rekognitionClient=rekognitionClientBuilder.build();
        CompareFacesRequest compareFacesRequest=CompareFacesRequest.builder()
                                                                    .sourceImage(Image.builder().bytes(sourceBytes).build())
                                                                    .targetImage(Image.builder().bytes(targetBytes).build())
                                                                    .similarityThreshold(98.0f)
                                                                    .build();

        CompareFacesResponse response=rekognitionClient.compareFaces(compareFacesRequest);
        List<CompareFacesMatch> faces=response.faceMatches();
        Toast.makeText(getApplicationContext(),faces.toString(),Toast.LENGTH_LONG).show();
    }
    private byte[] convertToByteArray(File file) throws IOException {
        return FileUtils.readFileToByteArray(file);
    }
}