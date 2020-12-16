package com.example.colordiaryexample;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.content.CursorLoader;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.wang.avi.AVLoadingIndicatorView;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class AddDiaryActivity extends AppCompatActivity {

    private static final int PORT = 12345; //서버에서 설정한 PORT 번호

    String ip="121.171.214.169"; //서버 단말기의 IP주소.

    Socket socket;     //클라이언트의 소켓
    DataInputStream is;
    DataOutputStream os;
    boolean isConnected=true;
    String msg="";
    String pictureLink;
    Uri selectedImageUri;
    ImageButton dpicture;
    String result;

    private DatePickerDialog.OnDateSetListener callbackMethod;
    private AVLoadingIndicatorView avi;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_diary);
        avi= (AVLoadingIndicatorView) findViewById(R.id.avi);
        stopAnim();

        final TextView ddate = findViewById(R.id.add_date);

        dpicture = findViewById(R.id.imageButton);



      //  ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
        //        R.array.emotionlist, android.R.layout.simple_spinner_item);

      //  adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);


        dpicture.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, 200);
            }
        });

        Button saveBtn = findViewById(R.id.save_btn);
        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //저장버튼을 눌렀을 때
                String content = ((EditText)findViewById(R.id.add_content)).getText().toString();
            //    startAnim();
                if(content.length() >= 43){
                    startToast("43글자 이하로 입력해주세요");
                    Log.d("fffffff","들어옴");
                }else{
                    localUpoad();
                    getConnection();
                    Log.d("fffffff","안들어옴");

                }

        /*        Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        stopAnim();
                      //  startPopupActivity();
                    }
                }, 3000);
        */
            }
        });

        callbackMethod = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                ddate.setText((year+"-"+(month+1)+"-"+dayOfMonth));
            }
        };
    }

    public String getPath(Uri uri){
        String[]proj = {MediaStore.Images.Media.DATA};
        CursorLoader cursorLoader = new CursorLoader(this,uri,proj,null,null,null);

        Cursor cursor = cursorLoader.loadInBackground();
        int index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);

        cursor.moveToFirst();

        return cursor.getString(index);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 200 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            selectedImageUri = data.getData();
            dpicture.setImageURI(selectedImageUri);
        }
    }

    public void OnClickHandler(View view) {
        Calendar calendar = Calendar.getInstance();
        DatePickerDialog dialog = new DatePickerDialog(this, callbackMethod,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
        dialog.show();
    }

    private void localUpoad() {
        String title = ((EditText)findViewById(R.id.add_title)).getText().toString();

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Create a storage reference from our app
        StorageReference storageRef = storage.getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        Uri file = Uri.fromFile(new File(getPath(selectedImageUri)));
        final StorageReference riversRef = storageRef.child("diarys/"+user.getUid() + "/" + title);
        UploadTask uploadTask = riversRef.putFile(file);

        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }

                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();
            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downloadUri = task.getResult();
                    pictureLink = downloadUri.toString();
                    DiaryUpdate();
                }
            }
        });

    }




    //회원정보 db등록 함수
    private void DiaryUpdate() {
        Date currentTime = Calendar.getInstance().getTime();
        long now = System.currentTimeMillis();
        Date dateNow = new Date(now);
        SimpleDateFormat sdfNow = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        String formatDate = sdfNow.format(dateNow);



        String title = ((EditText)findViewById(R.id.add_title)).getText().toString();
        String date = ((TextView)findViewById(R.id.add_date)).getText().toString();
        String content = ((EditText)findViewById(R.id.add_content)).getText().toString();
        String db_date = currentTime.toString();
        String db_time = formatDate;
        Intent intent = getIntent();
        String birthday = intent.getStringExtra("birthday");
        String pre_emotion = "";

        if(title.length()>0 && date.length() > 0  && content.length() > 0) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            // Access a Cloud Firestore instance from your Activity
            FirebaseFirestore db = FirebaseFirestore.getInstance();

            DiaryInfo diaryInfo = new DiaryInfo(title, date, content, pictureLink, db_date, db_time, pre_emotion, birthday);
            db.collection("diarys/"+user.getUid()+"/diarys").document(title).set(diaryInfo)
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void avoid) {
                            startToast("다이어리 등록을 성공했습니다.");


                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            startToast("다이어리 등록에 실패했습니다.");
                        }
                    });

        }else{
            startToast("다이어리를 입력해주세요.");
        }

    }


    void startAnim(){
        avi.show();
        // or avi.smoothToShow();
    }

    void stopAnim(){
        avi.hide();
        // or avi.smoothToHide();
    }


    private void startToast(String msg) {
        Toast.makeText(this,msg,Toast.LENGTH_SHORT).show();
    }


    private void startPopupActivity(){
        Intent intent = new Intent(this,PopupActivity.class);
        intent.putExtra("title",((EditText)findViewById(R.id.add_title)).getText().toString());
        startActivityForResult(intent,1);
    }

    private void sendGoodEmotion(){

        Intent intent = new Intent(this,PopupActivity.class);
        intent.putExtra("title",((EditText)findViewById(R.id.add_title)).getText().toString());
        intent.putExtra("preEmotion","긍정");
        startActivityForResult(intent,1);
        finish();
    }

    private void sendBadEmotion(){
        Intent intent = new Intent(this,PopupActivity.class);
        intent.putExtra("title",((EditText)findViewById(R.id.add_title)).getText().toString());
        intent.putExtra("preEmotion","부정");
        startActivityForResult(intent,1);
        finish();
    }

    public void getConnection(){
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // TODO Auto-generated method stub
                        try {
                            //서버와 연결하는 소켓 생성..
                            socket= new Socket(InetAddress.getByName(ip), PORT );
                            Log.d("문제없이 연결됨 ", "connect");

                            //여기까지 왔다는 것을 예외가 발생하지 않았다는 것이므로 소켓 연결 성공..
                            //서버와 메세지를 주고받을 통로 구축
                            is=new DataInputStream(socket.getInputStream());
                            os=new DataOutputStream(socket.getOutputStream());
                            sending();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                        //서버와 접속이 끊길 때까지 무한반복하면서 서버의 메세지 수신
                        while(true){
                            try {
                                msg = String.valueOf(is.read());

                                Log.d("서버에서 받아온 값 :", msg);

                                if(msg.equals("12")){
                                    sendGoodEmotion();

                                }else if(msg.equals("34")){
                                    sendBadEmotion();
                                }

                                socket.close();
                                break;

                            } catch (IOException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }//while
                    }//run method...
                }).start();//Thread 실행..
                if(os==null) return;   //서버와 연결되어 있지 않다면 전송불가..
                //네트워크 작업이므로 Thread 생성
    }

    public void sending(){
        new Thread(new Runnable() {
            public void run() {
                // TODO Auto-generated method stub
                //서버로 보낼 메세지 EditText로 부터 얻어오기
                String msg= ((EditText)findViewById(R.id.add_content)).getText().toString();


                try {
                    os.writeUTF(msg);  //서버로 메세지 보내기.UTF 방식으로(한글 전송가능...)
                    os.flush();        //다음 메세지 전송을 위해 연결통로의 버퍼를 지워주는 메소드..
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }//run method..
        }).start(); //Thread 실행..
    }



}