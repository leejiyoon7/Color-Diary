package com.example.colordiaryexample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class DiaryAdapter extends ArrayAdapter {

    Context context;
    int resId;
    List<DiaryInfo> diarys;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    File localFile;
    Bitmap mBitmap;
    Uri selectedImageUri;


    public DiaryAdapter(@NonNull Context context,int resource, @NonNull List<DiaryInfo> objects) {
        super(context,resource,objects);
        this.context = context;
        this.resId = resource;
        this.diarys = objects;
    }

    @Override
    public int getCount() {
        return diarys.size();
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        final int pos = position;
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(resId, null);
        }
        final DiaryInfo diary = diarys.get(position);
        final ImageView diaryImage = convertView.findViewById(R.id.diary_img);
        TextView diaryTitle = convertView.findViewById(R.id.title);
        TextView diaryDate = convertView.findViewById(R.id.date);
        TextView diaryEmotion = convertView.findViewById(R.id.emotion);
        ImageView share = convertView.findViewById(R.id.share);

        final FirebaseStorage storage = FirebaseStorage.getInstance();
        final StorageReference storageRef = storage.getReference();
        final String title = diary.getTitle();
      //  Bitmap bitmap = BitmapFactory.decodeFile(diary.getPicture());
        diaryTitle.setText(diary.getTitle());
        diaryDate.setText(diary.getDate());
        diaryEmotion.setText(diary.getPre_emotion());



        StorageReference islandRef = storageRef.child("diarys/"+user.getUid() + "/"+ title);
        final long ONE_MEGABYTE = 1024 * 1024;
        islandRef.getBytes(ONE_MEGABYTE).addOnSuccessListener(new OnSuccessListener<byte[]>() {
            @Override
            public void onSuccess(byte[] bytes) {
                // Data for "images/island.jpg" is returns, use this as needed
                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                diaryImage.setImageBitmap(bitmap);

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
            }
        });



        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu popup = new PopupMenu(context.getApplicationContext(), v);
                popup.getMenuInflater().inflate(R.menu.item_menu,
                        popup.getMenu());
                popup.show();
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            //텍스트를 받아와서 서버에 올림
                            case R.id.share_txt:
                                db.collection("diaryShare/")
                                        .add(diary)
                                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                            @Override
                                            public void onSuccess(DocumentReference documentReference) {
                                                diaryImage.setDrawingCacheEnabled(true);
                                                diaryImage.buildDrawingCache();
                                                Bitmap bitmap = ((BitmapDrawable) diaryImage.getDrawable()).getBitmap();
                                                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                                                bitmap.compress(Bitmap.CompressFormat.JPEG, 50, baos);
                                                byte[] data = baos.toByteArray();
                                                StorageReference mountainsRef = storageRef.child("diaryShare/" + title);

                                                UploadTask uploadTask = mountainsRef.putBytes(data);
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
                                                    }
                                                });
                                                Log.d("d", "DocumentSnapshot added with ID: " + documentReference.getId());
                                                startToast("전송선공");
                                            }
                                        })
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Log.w("d ", "Error adding document", e);
                                            }
                                        });

                                break;

                            //이름과 날짜를 받아와서 db 삭제
                            case R.id.delete:
                              // Toast.makeText(context.getApplicationContext(), " Delete Clicked at position " + " : " + pos, Toast.LENGTH_LONG).show();
                                db.collection("diarys/"+user.getUid()+"/diarys").document(diary.getTitle())
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
                                StorageReference storageRef = storage.getReference();
                                StorageReference desertRef = storageRef.child("diarys/"+user.getUid() + "/" + diary.getTitle());
                                desertRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        // File deleted successfully
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception exception) {
                                        // Uh-oh, an error occurred!
                                    }
                                });
                                diarys.remove(diary);
                                notifyDataSetChanged();
                                break;

                            default:
                                break;
                        }
                        return true;
                    }
                });
            }
        });


        ConstraintLayout cmdArea = (ConstraintLayout)convertView.findViewById(R.id.linearLayout);
        cmdArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DiaryDetailActivity.class);
                intent.putExtra("title", diarys.get(pos).getTitle());
                intent.putExtra("time", diarys.get(pos).getDb_time());
                intent.putExtra("emotion", diarys.get(pos).getPre_emotion());
                intent.putExtra("content", diarys.get(pos).getContent());
                intent.putExtra("date", diarys.get(pos).getDate());
                intent.putExtra("birthday", diarys.get(pos).getBirthday());
                context.startActivity(intent);
            }
        });


        return convertView;
    }


    private void startToast(String msg) {
        Toast.makeText(context ,msg,Toast.LENGTH_SHORT).show();
    }




    private class LoadImage extends AsyncTask<String, String, Bitmap>{

        ProgressDialog pDialog;
        View convertView = LayoutInflater.from(getContext()).inflate(resId, null);
        final ImageView diaryImage = convertView.findViewById(R.id.diary_img);
        private LoadImage() {

        }

        @Override
        protected void onPreExecute(){
            super.onPreExecute();
            pDialog = new ProgressDialog(context);
            pDialog.setMessage("이미지 로딩중입니다 ... ");
            pDialog.show();
        }


        @Override
        protected Bitmap doInBackground(String... strings) {

            try {
                InputStream InputStream;
                mBitmap = BitmapFactory.decodeStream((InputStream) new URL(strings[0]).getContent());
            } catch (IOException e) {
                e.printStackTrace();
            }

            return mBitmap;
        }

        protected void onPostExecute(Bitmap image){

            if(image != null){
                diaryImage.setImageBitmap(image);
                pDialog.dismiss();
            }else{
                pDialog.dismiss();


            }
        }


    }
}
