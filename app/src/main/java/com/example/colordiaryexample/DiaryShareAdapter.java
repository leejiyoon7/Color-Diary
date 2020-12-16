package com.example.colordiaryexample;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

public class DiaryShareAdapter extends ArrayAdapter {

    Context context;
    int resId;
    List<DiaryInfo> diarys;
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    File localFile;
    Bitmap mBitmap;

    public DiaryShareAdapter(@NonNull Context context, int resource, @NonNull List<DiaryInfo> objects) {
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

        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference storageRef = storage.getReference();
        String title = diary.getTitle();
        Bitmap bitmap = BitmapFactory.decodeFile(diary.getPicture());
        diaryTitle.setText(diary.getTitle());
        diaryDate.setText(diary.getDate());
        String picture = diary.getPicture();
        diaryEmotion.setText(diary.getPre_emotion());


        StorageReference islandRef = storageRef.child("diaryShare/"+ title);
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

        ConstraintLayout cmdArea = (ConstraintLayout)convertView.findViewById(R.id.linearLayout);
        cmdArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context,DiaryShareDetailActivity.class);
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
