package com.example.social_media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import io.grpc.Compressor;

import android.annotation.TargetApi;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Base64OutputStream;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bumptech.glide.Glide;
import com.example.social_media.Extras.Util;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.gowtham.library.ui.ActVideoTrimmer;
import com.gowtham.library.utils.TrimmerConstants;
import com.vincent.videocompressor.VideoCompress;


import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;


public class PostActivity extends AppCompatActivity {

    int PICK_IMAGE_VIDEO = 10;
    ImageView post_image;
    VideoView post_video;
    TextInputEditText caption;
    MaterialButton upload, post, cancel;
    Uri uploadData;

    boolean canPost = false;
    UploadTask uploadTask;
    StorageReference storageReference,videoStorageRef,imageStorageRef;
    String currentUser;
    long startTime, endTime;
    DatabaseReference postRef;
    static Uri destUri,trimmedUri;

    ProgressBar progressBar;
    TextView percentage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);
        post_image = findViewById(R.id.post_image);
        post_video = findViewById(R.id.post_video);
        caption = findViewById(R.id.caption);
        upload = findViewById(R.id.upload);
        post = findViewById(R.id.post);
        cancel = findViewById(R.id.cancel);




        currentUser = FirebaseAuth.getInstance().getCurrentUser().getUid();


        post_video.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (post_video.isPlaying()) {
                    post_video.pause();
                } else {
                    post_video.start();
                }
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                uploadFromGallary();
            }
        });
        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                if (trimmedUri!=null) {
//                    compressVideo();
//                }
//                else {
//                    Toast.makeText(PostActivity.this, "Trimme Null", Toast.LENGTH_SHORT).show();
//                }


                uploadPost();
            }
        });
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                compressVideo();
            }
        });

    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    void uploadFromGallary() {
        Intent upload = new Intent(Intent.ACTION_PICK);
        upload.setType("*/*");
        upload.putExtra(Intent.EXTRA_MIME_TYPES, new String[]{"image/*", "video/*"});
//       upload.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        upload.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(upload, PICK_IMAGE_VIDEO);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == PICK_IMAGE_VIDEO) {
            uploadData = data.getData();

            final Uri pathURi = data.getData();
            String path = pathURi.toString();
            Log.e(" Data Uri :", "  " + uploadData);
            if (path.contains("/video")) {
                post_image.setVisibility(View.GONE);
                post_video.setVisibility(View.VISIBLE);
                canPost = true;
                String pathgo = pathURi.toString();

            } else if (path.contains("/image")) {
                post_video.setVisibility(View.GONE);
                post_image.setVisibility(View.VISIBLE);
                 Glide.with(PostActivity.this).load(uploadData).into(post_image);
                canPost = true;
                File file = new File(path);

                //new ImageCompressionAsyncTask(this,post_image).execute(path,
                //  Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+getPackageName()+"/media/images");

//                new VideoCompressAsync(PostActivity.this).execute(file.getPath(), file.getParent());
            }

        }


    }


    private void uploadPost() {

        Dialog dialog=new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_progressbar);


        Uri uploadedUri=null;
        if (canPost)
        {

                String path = uploadData.getPath();
                storageReference = FirebaseStorage.getInstance().getReference("Uploads").child("Posts");
                Task task1 = null;
                if (path.contains("/video")) {
                    dialog.show();
                    videoStorageRef = storageReference.child("" + System.currentTimeMillis() + "video." + getFileExtension(uploadData));
                    uploadedUri=destUri;
                    Log.e("Upload Task "," "+uploadedUri);
                    UploadTask task = videoStorageRef.putFile(uploadedUri);
                     task1= task.continueWithTask(new Continuation() {
                        @Override
                        public Object then(@NonNull Task task) throws Exception {

                            if (!task.isSuccessful())
                            {
                                throw task.getException();
                            }
                            return storageReference.getDownloadUrl();
                        }
                    });

                }
                else if (path.contains("/image"))
                {
                    dialog.show();


                    imageStorageRef = storageReference.child("" + System.currentTimeMillis() + "image." + getFileExtension(uploadData));
                    uploadedUri=uploadData;
                    Bitmap bmp = null;
                    try {
                            bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uploadData);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                    byte[] data = baos.toByteArray();
                    //uploading the image
                    UploadTask uploadTask2 = imageStorageRef.putBytes(data);

                    task1=uploadTask2.continueWithTask(new Continuation() {
                    @Override
                        public Object then(@NonNull Task task) throws Exception {

                            if (!task.isSuccessful())
                            {
                                    throw task.getException();
                                }
                                return storageReference.getDownloadUrl();
                            }
                        });


                }
                if (task1 != null)
                {


                    task1.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(@NonNull Task task) {
                        if (task.isSuccessful()) {

                            String postId = "" + System.currentTimeMillis();
                            String currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
                            String currentTime = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

                            Uri uri = (Uri) task.getResult();
                            String mUri = uri.toString();

                            postRef = FirebaseDatabase
                                    .getInstance().getReference("Posts").child(postId);
                            Map map = new HashMap();
                            map.put("postUrl", mUri);
                            map.put("time", currentTime);
                            map.put("date", currentDate);
                            map.put("postId", postId);
                            map.put("id", currentUser);
                            map.put("caption", caption.getText().toString());
                            postRef.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Map statusMap = new HashMap();
                                        statusMap.put("likes", 0);
                                        statusMap.put("comments", 0);

                                        postRef.child("postInfo").setValue(statusMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                Toast.makeText(PostActivity.this, "Posted", Toast.LENGTH_SHORT).show();
                                              dialog.dismiss();
                                            }
                                        });

                                    }
                                }
                            });


                        }


                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("PostActvity", "" + e);
                        dialog.dismiss();
                    }
                });
            } else
                {

                    Toast.makeText(this, "Path is Null Con not post", Toast.LENGTH_SHORT).show();
                }

        }

    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private Locale getLocale() {
        Configuration config = getResources().getConfiguration();
        Locale sysLocale = null;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = getSystemLocale(config);
        } else {
            sysLocale = getSystemLocaleLegacy(config);
        }

        return sysLocale;
    }

    @SuppressWarnings("deprecation")
    public static Locale getSystemLocaleLegacy(Configuration config) {
        return config.locale;
    }

    @TargetApi(Build.VERSION_CODES.N)
    public static Locale getSystemLocale(Configuration config) {
        return config.getLocales().get(0);
    }


    private void compressVideo() {


        Dialog dialog=new Dialog(this);
        dialog.setCancelable(false);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.progress_bar_with_percentage);
        progressBar=dialog.findViewById(R.id.progress_horizontal);
        percentage=dialog.findViewById(R.id.value123);

        File folder = new File(Environment.getExternalStorageDirectory() +
                File.separator + "Social_Media");
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();

        }
        String desPath=folder.getAbsolutePath()+ File.separator + "VID_" + new SimpleDateFormat("yyyyMMdd_HHmmss", getLocale()).format(new Date()) + ".mp4";

        try {
            String uri = Util.getFilePath(this, uploadData);



        VideoCompress.compressVideoMedium(uri, desPath , new VideoCompress.CompressListener() {
            @Override
            public void onStart() {
           dialog.show();

                Toast.makeText(PostActivity.this, "Starting....", Toast.LENGTH_SHORT).show();
                caption.setText("Compressing..." + "\n"
                        + "Start at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
                startTime = System.currentTimeMillis();
                Util.writeFile(PostActivity.this, "Start at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()) + "\n");
            }

            @Override
            public void onSuccess(String compressedPath) {
                Toast.makeText(PostActivity.this, "Sucess", Toast.LENGTH_SHORT).show();

                String previous = caption.getText().toString();
                caption.setText(previous + "\n"
                        + "Compress Success!" + "\n"
                        + "End at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
                endTime = System.currentTimeMillis();
                Util.writeFile(PostActivity.this, "End at: " + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()) + "\n");
                Util.writeFile(PostActivity.this, "Total: " + ((endTime - startTime)/1000) + "s" + "\n");
                Util.writeFile(PostActivity.this);
                destUri=Uri.fromFile(new File(compressedPath));
                post_video.setVideoURI(destUri);
                dialog.dismiss();

            }

            @Override
            public void onFail() {
                Toast.makeText(PostActivity.this, "Fail....", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
                caption.setText("Compress Failed!");
                endTime = System.currentTimeMillis();
                Util.writeFile(PostActivity.this, "Failed Compress!!!" + new SimpleDateFormat("HH:mm:ss", getLocale()).format(new Date()));
            }

            @Override
            public void onProgress(float percent) {
                progressBar.setProgress((int) percent);
                percentage.setText(String.valueOf( percent));
                }
        });

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private void trimmeVideo(Uri videoUri)
    {
        Intent intent=new Intent(this, ActVideoTrimmer.class);
        intent.putExtra(TrimmerConstants.TRIM_VIDEO_URI,String.valueOf(videoUri));
        //intent.putExtra(TrimmerConstants.DESTINATION,"/storage/emulated/0/DCIM/MYFOLDER"); //optional default output path /storage/emulated/0/DOWNLOADS
        startActivityForResult(intent,TrimmerConstants.REQ_CODE_VIDEO_TRIMMER);
    }
}







