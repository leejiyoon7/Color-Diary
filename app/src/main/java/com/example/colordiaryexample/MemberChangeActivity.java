package com.example.colordiaryexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemberChangeActivity extends AppCompatActivity {

    String profileLink;

    private static final String TAG_TEXT = "text";

    Uri selectedImageUri;
    ImageView profileImg;

    List<Map<String,Object>> dialogItemList;
    String[] text ={"사진 찍기","갤러리에서 선택"};

    ImageView profileImage;
    TextView nameText;
    TextView phoneNumberText;
    TextView birthDayText;
    TextView addressText;
    List<MemberInfo> diaryMember = new ArrayList<>();
    File localFile;
    Button endButton;
    Button checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_change);

        diaryMember = getMember();

        endButton = findViewById(R.id.endButton);
        checkButton = findViewById(R.id.checkButton);

        profileImage = findViewById(R.id.profileImg);

        profileImage.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 200);
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                localUpoad();
            }
        });
        endButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            profileImage.setImageURI(selectedImageUri);
        }
    }

    private void localUpoad() {

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final StorageReference riversRef = storageRef.child("users/"+user.getUid() + "/" +"Profile Image");
        profileImage.setDrawingCacheEnabled(true);
        profileImage.buildDrawingCache();
        Bitmap bitmap = ((BitmapDrawable) profileImage.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 30, baos);
        byte[] data = baos.toByteArray();

        UploadTask uploadTask = riversRef.putBytes(data);
        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle unsuccessful uploads
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                profileUpdate();
            }
        });

    }

    private void profileUpdate() {

        String name = ((EditText)findViewById(R.id.nameText)).getText().toString();
        String phoneNumber = ((EditText)findViewById(R.id.phoneNumberText)).getText().toString();
        String birthDay = ((Spinner)findViewById(R.id.birthDayEditText)).getSelectedItem().toString();
        String address = ((EditText)findViewById(R.id.addressText)).getText().toString();


        if(name.length()>0 && phoneNumber.length() > 9  && address.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Access a Cloud Firestore instance from your Activity
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            deleteInfo();
            MemberInfo memberInfo = new MemberInfo(name, phoneNumber, birthDay, address, profileLink);
            db.collection("users").document(user.getUid()).set(memberInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            startToast("회원정보 등록을 성공했습니다.");
                            startMainActivity();
                            finish();
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast("회원정보 등록에 실패했습니다.");
                        }
                    });

        }else{
            startToast("회원정보를 입력해주세요.");
        }

    }
    private void deleteInfo(){
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").document(user.getUid())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    private void startToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
    public List<MemberInfo> getMember(){
        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        final List<MemberInfo> diaryMember = new ArrayList<>();
        final ArrayList<Map<String, Object>> diaryM = new ArrayList<Map<String, Object>>();
        final StorageReference storageRef = storage.getReference();


        final DocumentReference docRef = db.collection("users").document(user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        diaryM.add(document.getData());
                        MemberInfo memberInfo = new MemberInfo((String)diaryM.get(0).get("name"), (String)diaryM.get(0).get("phoneNumber"), (String)diaryM.get(0).get("birthDay"),(String)diaryM.get(0).get("address"),(String)diaryM.get(0).get("profileLink"));

                        nameText = findViewById(R.id.nameText);
                        phoneNumberText = findViewById(R.id.phoneNumberText);
                        birthDayText = findViewById(R.id.birthDayText);
                        addressText = findViewById(R.id.addressText);
                        profileImage = findViewById(R.id.profileImg);

                        nameText.setText(memberInfo.getName());
                        phoneNumberText.setText(memberInfo.getPhoneNumber());
                        addressText.setText(memberInfo.getAddress());

                        try {
                            localFile = File.createTempFile("images", "jpg");
                            final StorageReference riversRef = storageRef.child("users/" + user.getUid() + "/Profile Image");
                            riversRef.getFile(localFile)
                                    .addOnSuccessListener(new OnSuccessListener<FileDownloadTask.TaskSnapshot>() {
                                        @Override
                                        public void onSuccess(FileDownloadTask.TaskSnapshot taskSnapshot) {
                                            // Successfully downloaded data to local file
                                            // ...

                                            Bitmap bitmap = BitmapFactory.decodeFile(localFile.getAbsolutePath());
                                            profileImage.setImageBitmap(bitmap);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception exception) {
                                    // Handle failed download
                                    // ...
                                }
                            });
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        // Log.d(TAG, "No such document");
                    }
                } else {
                    //Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });


        return diaryMember;
    }

    public void onBackPressed() {
        //안드로이드 백버튼 막기

        Intent intent = new Intent(this,MemberDetailActivity.class);
        startActivity(intent);
        finish();

    }

}