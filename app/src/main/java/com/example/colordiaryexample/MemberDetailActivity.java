package com.example.colordiaryexample;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MemberDetailActivity extends AppCompatActivity {

    ImageView profileImage;
    TextView nameText;
    TextView phoneNumberText;
    TextView birthDayText;
    TextView addressText;
    List<MemberInfo> diaryMember = new ArrayList<>();
    File localFile;
    Button changeButton;
    Button checkButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_member_detail);
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        diaryMember = getMember();

        changeButton = findViewById(R.id.changeButton);
        checkButton = findViewById(R.id.checkButton);

        changeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startChangeActivity();
                finish();
            }
        });

        checkButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
                finish();
            }
        });

    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private void startChangeActivity() {
        Intent intent = new Intent(this, MemberChangeActivity.class);
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
                        birthDayText.setText(memberInfo.getBirthDay());
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

        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();

    }
}