package com.example.ilm;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;

public class QuestionFirebaseDAO  implements IQuestionDAO{
    public interface DataObserver{
        public void update();
    }

    private DataObserver observer;
    FirebaseDatabase database;
    DatabaseReference reference;
    ArrayList<Hashtable<String,String>> questions;

    public QuestionFirebaseDAO(){

    }

    @Override
    public void setObserver(DataObserver obs){
        observer=obs;
        database=FirebaseDatabase.getInstance();
        //database.setPersistenceEnabled(true);
        reference=database.getReference("questions");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                try{
                    questions=new ArrayList<Hashtable<String,String>>();
                    for(DataSnapshot d: snapshot.getChildren()){
                        GenericTypeIndicator<HashMap<String,Object>> type=new GenericTypeIndicator<HashMap<String, Object>>() {};
                        HashMap<String,Object> map=d.getValue(type);

                        Hashtable<String,String> obj=new Hashtable<String,String>();
                        for(String key:map.keySet()){
                            obj.put(key,map.get(key).toString());
                        }
                        questions.add(obj);
                    }
                    observer.update();
                }
                catch (Exception ex){
                    Log.e("firebasedb",ex.getMessage());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Log.w("firebasedb","Failed to Read Value.", error.toException());
            }
        });
    }

    @Override
    public void save(Hashtable<String,String> obj){
        /*reference.child("id").setValue(obj.get("id"));
        reference.child("title").setValue(obj.get("title"));
        reference.child("question").setValue(obj.get("question"));
        reference.child("answer").setValue(obj.get("answer"));*/
        reference.child(obj.get("id")).setValue(obj);
    }

    @Override
    public void save(String id, String answer){
        reference.child(id).child("answer").setValue(answer);
    }

    @Override
    public void save(ArrayList<Hashtable<String,String>> objects){
        for(Hashtable<String,String> obj: objects){
            save(obj);
        }
    }

    @Override
    public ArrayList<Hashtable<String,String>> load(){
        if(questions==null){
            questions=new ArrayList<Hashtable<String,String>>();
        }
        return questions;
    }

    @Override
    public Hashtable<String,String> load(String id){
        return null;
    }

}
