package in.tagteen.tagteen.workers;

import android.os.AsyncTask;

import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.GetObjectRequest;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.PutObjectResult;
import com.amazonaws.services.s3.model.S3Object;

import java.io.File;
import java.io.InputStream;

/**
 * Created by user on 17-07-2017.
 */

public class S3Example extends AsyncTask<Void,Void,Void> {

    @Override
    protected Void doInBackground(Void... params) {

        // Initialize the Amazon Cognito credentials provider
        /*CognitoCachingCredentialsProvider credentialsProvider = new CognitoCachingCredentialsProvider(
                MY-ACTIVITY.getApplicationContext(), // Application Context
                "MY-IDENTITY-POOL-ID", // Identity Pool ID
                Regions.SELECT_YOUR_REGION // Region enum
        );*/
/*
        AmazonS3Client s3Client = new AmazonS3Client(credentialsProvider);
        File fileToUpload = YOUR_FILE;
        //(Replace "MY-BUCKET" with your S3 bucket name, and "MY-OBJECT-KEY" with whatever you would like to name the file in S3)
        PutObjectRequest putRequest = new PutObjectRequest("MY-BUCKET", "MY-OBJECT-KEY",
                fileToUpload);
        PutObjectResult putResponse = s3Client.putObject(putRequest);

        GetObjectRequest getRequest = new GetObjectRequest("MY-BUCKET", "MY-OBJECT-KEY");
        S3Object getResponse = s3Client.getObject(getRequest);
        InputStream myObjectBytes = getResponse.getObjectContent();

        // Do what you want with the object

        myObjectBytes.close();*/

        return null;
    }
}
