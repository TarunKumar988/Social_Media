package com.example.social_media;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import de.hdodenhof.circleimageview.CircleImageView;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.social_media.Extras.MyApplication;
import com.example.social_media.Model.Users;
import com.facebook.login.LoginManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Profile_Activity extends AppCompatActivity implements Toolbar.OnMenuItemClickListener{
    MaterialTextView username,userDiscription;
    CircleImageView profile,image;
    Button logout;
    TextView appName,tittle;
    SharedPreferences sharedPreferences;
    SharedPreferences.Editor editor;
    Dialog mydailog;
    int PICK_IMAGE=10;
    static Uri imageUri;
    StorageReference storageReference;
    StorageTask uploadTask;
    static String currentUser;
    TextInputEditText discription,name;
    MaterialButton save,cancel;
    String getName,getDiscription;


    private static final String TAG = Profile_Activity.class.getSimpleName();
    public static final int REQUEST_IMAGE = 100;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        username=findViewById(R.id.username);
        userDiscription=findViewById(R.id.discription);
        profile=findViewById(R.id.profile);
        logout=findViewById(R.id.logout);
        image=findViewById(R.id.image);
        appName=findViewById(R.id.name);




        // Clearing older images from cache directory
        // don't call this line if you want to choose multiple images in the same activity
        // call this once the bitmap(s) usage is over



        mydailog=new Dialog(this);

        appName.setText(R.string.app_name);
        Glide.with(this).load(R.mipmap.ic_launcher).into(image);


        Toolbar toolbar=findViewById(R.id.toolbar);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            toolbar.inflateMenu(R.menu.profile_menu);
            toolbar.setOnMenuItemClickListener(this);
        }

        sharedPreferences=getSharedPreferences("SIGN_IN",MODE_PRIVATE);
        storageReference= FirebaseStorage.getInstance().getReference("Uploads").child("Profiles");

        currentUser=FirebaseAuth.getInstance().getCurrentUser().getUid();
        getUserDetails();




        ImagePickerActivity.clearCache(this);

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
            }
        });

        profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                onProfileImageClick();
            }
        });

    }

    private void getUserDetails() {

        DatabaseReference ref=FirebaseDatabase.getInstance().getReference("Users").child(currentUser);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Users user=snapshot.getValue(Users.class);
                getName=user.getName();
                getDiscription=user.getDiscription();
                username.setText(getName);
                userDiscription.setText(getDiscription);

                Glide.with(getApplicationContext()).load(user.getImageURL()).into(profile);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        if (item.getItemId()==R.id.edit)
        {
            Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
            editProfile();


        }
        return false;
    }

    private void editProfile() {


                mydailog.setContentView(R.layout.custom_popup_edit_profile);
                name=mydailog.findViewById(R.id.name);
                discription=mydailog.findViewById(R.id.discription);
                cancel=mydailog.findViewById(R.id.cancel);
                save=mydailog.findViewById(R.id.save);
                name.setText(getName);
                discription.setText(getDiscription);
        save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mydailog.dismiss();
                     if (currentUser!=null)
                     {
                         String n=name.getText().toString();
                         String d=discription.getText().toString();
                         DatabaseReference discriptionRef=FirebaseDatabase.getInstance().getReference("Users")
                                 .child(currentUser);
                         discriptionRef.child("discription").setValue(d).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful())
                                 {
                                     Toast.makeText(Profile_Activity.this, "profile Updated", Toast.LENGTH_SHORT).show();
                                    userDiscription.setText(d);
                                 }
                             }
                         });
                         DatabaseReference nameRef=FirebaseDatabase.getInstance().getReference("Users")
                                 .child(currentUser);
                         nameRef.child("name").setValue(n).addOnCompleteListener(new OnCompleteListener<Void>() {
                             @Override
                             public void onComplete(@NonNull Task<Void> task) {
                                 if (task.isSuccessful())
                                 {
                                     Toast.makeText(Profile_Activity.this, "profile Updated", Toast.LENGTH_SHORT).show();
                                 username.setText(n);
                                 }
                             }
                         });
                     }

                    }
                });

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mydailog.dismiss();
                        Toast.makeText(Profile_Activity.this, "Cancel", Toast.LENGTH_SHORT).show();
                    }
                });
                mydailog.show();
    }


    void  uploadFromGallary()
    {
//       Intent upload=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
//       upload.setType("image/*");
        Intent upload = new Intent();
        upload.setType("image/*");
        upload.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(upload, PICK_IMAGE);

    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
//    {
//        super.onActivityResult(requestCode, resultCode, data);
//        if (resultCode==RESULT_OK&&requestCode==PICK_IMAGE)
//        {
//            imageUri=data.getData();
//
//            if (uploadTask!=null&& uploadTask.isInProgress())
//            {
//                Toast.makeText(this, "Uploading ...", Toast.LENGTH_SHORT).show();
//            }
//            else {
//                uploadImage();
//            }
//        }
//
//    }

    private void uploadImage( Bitmap bitmap ) {
        if(imageUri!=null)
        {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Uploading ... ");
        progressDialog.show();


            final StorageReference sr=storageReference.child(currentUser+"  "+System.currentTimeMillis()+".jpeg");
//            uploadTask=sr.putFile(imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
            byte[] data = baos.toByteArray();
            uploadTask=sr.putBytes(data);

//            UploadTask uploadTask = uploadTask.putBytes(data);
           Task task= uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                if (!task.isSuccessful())
                {
                    throw task.getException();
                }

                    return sr.getDownloadUrl();
                }
            });

            task. addOnCompleteListener(new OnCompleteListener() {
                @Override
                public void onComplete(@NonNull Task task) {
                    if (task.isSuccessful()) {
                        Uri uri = (Uri) task.getResult();
                        //Glide.with(getApplicationContext()).load(String.valueOf(imageUri)).into(profile);

                        assert uri != null;
                        String iUri = uri.toString();

                        Uri downloaduri = (Uri) task.getResult();
                        String mUri = downloaduri.toString();
                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(currentUser);
                        HashMap<String, Object> map = new HashMap<>();
                        map.put("imageURL", mUri);
                        reference.updateChildren(map);

                        progressDialog.dismiss();
                    }
                        else {
                        Toast.makeText(getApplicationContext(), "Failed!", Toast.LENGTH_SHORT).show();
                        progressDialog.dismiss();
                    }


                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(Profile_Activity.this, " "+e.toString(), Toast.LENGTH_SHORT).show();
progressDialog.dismiss();
                }
            });

        }




    }
    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver =getContentResolver();
        MimeTypeMap mimeTypeMap = MimeTypeMap.getSingleton();
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void loadProfile(String url) {
//        Log.d(TAG, "Image cache path: " + url);
        Glide.with(this).load(url)
                .into(profile);
        profile.setColorFilter(ContextCompat.getColor(this, android.R.color.transparent));
    }





    void onProfileImageClick() {
        Dexter.withActivity(this)
                .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                .withListener(new MultiplePermissionsListener() {
                    @Override
                    public void onPermissionsChecked(MultiplePermissionsReport report) {

                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions();
                        }

                        if (report.isAnyPermissionPermanentlyDenied()) {
                            showSettingsDialog();
                        }
                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                        token.continuePermissionRequest();
                    }


                }).check();
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(Profile_Activity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, REQUEST_IMAGE);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(Profile_Activity.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, REQUEST_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {

                Uri uri = data.getParcelableExtra("path");
                imageUri=uri;



                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);

                    // loading profile image from local cache
                    loadProfile(uri.toString());

                    if (uploadTask != null && uploadTask.isInProgress()) {
                        Toast.makeText(this, "Uploading ...", Toast.LENGTH_SHORT).show();
                    } else {
                        uploadImage(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Showing Alert Dialog with Settings option
     * Navigates user to app settings
     * NOTE: Keep proper title and message depending on your app
     */
    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(Profile_Activity.this);
        builder.setTitle(getString(R.string.dialog_permission_title));
        builder.setMessage(getString(R.string.dialog_permission_message));
        builder.setPositiveButton(getString(R.string.go_to_settings), (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

}
