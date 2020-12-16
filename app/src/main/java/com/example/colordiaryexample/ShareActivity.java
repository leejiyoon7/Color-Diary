package com.example.colordiaryexample;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ListView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;


public class ShareActivity extends AppCompatActivity {
    ListView dlistview;
    File localFile;
    ArrayList<Map<String, Object>> diarys = new ArrayList<Map<String, Object>>();
    List<DiaryInfo> diarySave = new ArrayList<>();
    List<DiaryInfo> diarySaving = new ArrayList<>();
    private ArrayList<DiaryInfo> arraylist;
    private DiaryShareAdapter adapter;


    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_share);

        final Toolbar share_toolbar = findViewById(R.id.share_toolbar);
        setSupportActionBar(share_toolbar);


        final FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        final FirebaseFirestore db = FirebaseFirestore.getInstance();

        diarySave = getAll();
        diarySaving = getAll();

        dlistview = findViewById(R.id.share_item_view);
        DiaryShareAdapter adapter = new DiaryShareAdapter(ShareActivity.this ,R.layout.list_share_diary, diarySave);
        dlistview.setAdapter(adapter);




        final EditText editTextFilter = (EditText)findViewById(R.id.editTextFilter) ;
        editTextFilter.addTextChangedListener(new TextWatcher() {

            @Override
            public void afterTextChanged(Editable edit) {
                dlistview = findViewById(R.id.share_item_view);
                DiaryShareAdapter adapter = new DiaryShareAdapter(ShareActivity.this ,R.layout.list_share_diary, diarySave);
                dlistview.setAdapter(adapter);
                String text = editTextFilter.getText().toString();
                search(text);
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }
        }) ;


        final SwipeRefreshLayout swipeRefreshLayout = findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                DiaryShareAdapter adapter = (DiaryShareAdapter) dlistview.getAdapter();
                adapter.diarys = getAll();
                diarySave = adapter.diarys;
                setSupportActionBar(share_toolbar);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);


            }
        });

        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayShowTitleEnabled(false); // 기존 title 지우기
        actionBar.setDisplayHomeAsUpEnabled(true); // 뒤로가기 버튼 만들기




        View.OnClickListener onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.fab:
                        startPopupAgeActivity();
                }
            }
        };

        findViewById(R.id.fab).setOnClickListener(onClickListener);
    }


    public void search(String charText) {

        dlistview = findViewById(R.id.share_item_view);
        DiaryShareAdapter adapter = new DiaryShareAdapter(ShareActivity.this ,R.layout.list_share_diary, diarySave);
        dlistview.setAdapter(adapter);
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        diarySave.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            diarySave = getAll();
           // diarySave.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else
        {

            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < diarySaving.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (diarySaving.get(i).getTitle().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    diarySave.add(diarySaving.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }

    public void searchAge(String charText) {

        dlistview = findViewById(R.id.share_item_view);
        DiaryShareAdapter adapter = new DiaryShareAdapter(ShareActivity.this ,R.layout.list_share_diary, diarySave);
        dlistview.setAdapter(adapter);
        // 문자 입력시마다 리스트를 지우고 새로 뿌려준다.
        diarySave.clear();

        // 문자 입력이 없을때는 모든 데이터를 보여준다.
        if (charText.length() == 0) {
            diarySave = getAll();
            // diarySave.addAll(arraylist);
        }
        // 문자 입력을 할때..
        else
        {

            // 리스트의 모든 데이터를 검색한다.
            for(int i = 0;i < diarySaving.size(); i++)
            {
                // arraylist의 모든 데이터에 입력받은 단어(charText)가 포함되어 있으면 true를 반환한다.
                if (diarySaving.get(i).getBirthday().contains(charText))
                {
                    // 검색된 데이터를 리스트에 추가한다.
                    diarySave.add(diarySaving.get(i));
                }
            }
        }
        // 리스트 데이터가 변경되었으므로 아답터를 갱신하여 검색된 데이터를 화면에 보여준다.
        adapter.notifyDataSetChanged();
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();
        return true;
    }




    public List<DiaryInfo> getAll() {
        final List<DiaryInfo> diarySave = new ArrayList<>();
        final ArrayList<Map<String, Object>> diarys = new ArrayList<Map<String, Object>>();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseStorage storage = FirebaseStorage.getInstance();
        StorageReference listRef = storage.getReference().child("files/uid");



        db.collection("diaryShare/")
                //  .orderBy("db_date",Query.Direction.DESCENDING)
                .orderBy("db_time",Query.Direction.ASCENDING)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                diarys.add(document.getData());
                            }
                            for (int i=diarys.size()-1;i>-1;i--) {
                                DiaryInfo diaryInfo = new DiaryInfo((String)diarys.get(i).get("title"),(String)diarys.get(i).get("date"),(String)diarys.get(i).get("content"),(String)diarys.get(i).get("picture"),(String)diarys.get(i).get("db_date"),(String)diarys.get(i).get("db_time"),(String)diarys.get(i).get("pre_emotion"),(String)diarys.get(i).get("birthday"));
                                diarySave.add(diaryInfo);
                            }
                            dlistview = findViewById(R.id.share_item_view);
                            DiaryShareAdapter adapter = new DiaryShareAdapter(ShareActivity.this ,R.layout.list_share_diary, diarySave);
                            dlistview.setAdapter(adapter);
                        } else {


                        }
                    }
                });

        return diarySave;

    }



    private void startPopupAgeActivity(){
        Intent intent = new Intent(this,PopupAgeActivity.class);
        startActivityForResult(intent,1);
    }

    public void onBackPressed() {
        Intent intent = new Intent(this,MainActivity.class);
        startActivity(intent);
        finish();;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
           String age = data.getExtras().getString("age");
           Log.d("gggggggggggggg","잘들어오는지");
           Log.d("결과값들", age);
            dlistview = findViewById(R.id.share_item_view);
            DiaryShareAdapter adapter = new DiaryShareAdapter(ShareActivity.this ,R.layout.list_share_diary, diarySave);
            dlistview.setAdapter(adapter);
           searchAge(age);
        }

    }
    @Override
    public void onResume() {
        super.onResume();
        final Toolbar share_toolbar = findViewById(R.id.share_toolbar);
        setSupportActionBar(share_toolbar);


    }

}