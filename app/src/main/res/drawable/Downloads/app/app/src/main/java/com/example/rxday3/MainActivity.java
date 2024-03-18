package com.example.rxday3;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.schedulers.Schedulers;

public class MainActivity extends AppCompatActivity {
    List<String> names =new ArrayList<>();
    List<String> age =new ArrayList<>();
    EditText namesTv;
    EditText ageTv;
    TextView mix;
    Button btn;
    String currentNam;
    String getCurrentAge;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        namesTv=findViewById(R.id.namTv);
        ageTv=findViewById(R.id.ageTv);
        mix=findViewById(R.id.mixTv);
        btn=findViewById(R.id.button);
        namesTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                names.add(namesTv.getText().toString());
                Log.i("TAG", "onClick: "+namesTv.getText().toString());
                namesTv.setText("");

            }
        });
        ageTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                age.add(ageTv.getText().toString());
                Log.i("TAG", "onClick: "+ageTv.getText().toString());
                ageTv.setText("");

            }
        });

        ArrayList<String> res=new ArrayList<>();
        ArrayList<String> res2=new ArrayList<>();
        Observable<String> observable=Observable.fromIterable(names);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<String> res = new ArrayList<>();
                List<String> res2 = new ArrayList<>();

                Observable<String> observable = Observable.fromIterable(names);
                //Observable<String> observable2 = /* your second observable */;
                Observable<String> observable2=Observable.fromIterable(age);

                Observable.zip(
                                observable.subscribeOn(Schedulers.newThread())
                                        .filter(e -> (!e.isEmpty() && e != null && !e.equals(" ")))
                                        .observeOn(AndroidSchedulers.mainThread()),
                                observable2.subscribeOn(Schedulers.newThread())
                                        .filter(e -> (!e.isEmpty() && e != null && !e.equals(" ")))
                                        .observeOn(AndroidSchedulers.mainThread()),
                                (item1, item2) -> item1 + "\t" + item2
                        ).observeOn(AndroidSchedulers.mainThread())
                        .subscribe(
                                mergedItem -> {
                                    Log.i("TAG", "onTextChanged: " + mergedItem);
                                    res.add(mergedItem);
                                      },
                                error -> {

                                },
                                () -> {
                                    for(int i=0;i<res.size();i++){
                               currentNam=mix.getText().toString();
                                mix.setText(currentNam+"\n"+res.get(i).toString());
//
                           }
                                      }
                        );

////
//                observable.subscribeOn(Schedulers.newThread()).filter(e->(!e.isEmpty()&&e!=null&&e!=" "))
//                        .observeOn(AndroidSchedulers.mainThread()).subscribe(
//                        item->{
//                            Log.i("TAG", "onTextChanged: ");
//                            res.add(item);
//                        },
//                        error->{},
//                        ()->{
//                            for(int i=0;i<res.size();i++){
//                                currentNam=mix.getText().toString();
////                                mix.setText(currentNam+"\n"+age.get(i).toString());
//
//                            }
//
//                        }
//                );
//                Observable<String> observable2=Observable.fromIterable(age);
//                observable2.subscribeOn(Schedulers.newThread()).
//                        filter(e->(!e.isEmpty()&&e!=null&&e!=" "))
//                        .observeOn(AndroidSchedulers.mainThread()).subscribe(
//                                item->{
//                                    Log.i("TAG", "onTextChanged: ");
//                                    res2.add(item);
//                                },
//                                error->{},
//                                ()->{  if(res.size()> res2.size()){
//                                    for(int i=0;i<res2.size();i++){
//
//                                        currentNam=mix.getText().toString();
//                                        mix.setText(currentNam+"\n"+res.get(i).toString()+"\t"+res2.get(i).toString());
//
//                                    }
//                                }
//                                    else{     for(int i=0;i<res.size();i++){
//
//                                    currentNam=mix.getText().toString();
//                                    mix.setText(currentNam+"\n"+res.get(i).toString()+"\t"+res2.get(i).toString());
//
//                                }}
//
//                                }
//                        );

            }
        });

    }
}