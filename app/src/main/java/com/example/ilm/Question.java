package com.example.ilm;

import android.content.Context;
import android.widget.Toast;

import java.io.Serializable;
import java.text.ParseException;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.UUID;
import java.util.Date;

public class Question implements Serializable {
    private String id;
    private String title;
    private String question;
    private String answer;
    private String Date;
    private transient IQuestionDAO dao = null;
    public Question(){
        init();
    }

    private void init(){
        this.id= UUID.randomUUID().toString();
    }

    public void setTitle(String t){
        this.title=t;
    }

    public void setQuestion(String q){
        this.question=q;
    }

    public void setAnswer(String a){this.answer=a;}
    public void setDao(IQuestionDAO d){this.dao=d;}

    public String getTitle(){
        return this.title;
    }
    public String getQuestion(){
        return this.question;
    }

    public String getAnswer(){return this.answer;}

    public String getId(){
        return this.id;
    }

    public String getDate() {
        return Date;
    }

    public void save(){
        if (dao != null){
            Hashtable<String,String> data = new Hashtable<String, String>();
            data.put("id",id);
            data.put("title",title);
            data.put("question",question);
            data.put("answer",answer);
            dao.save(data);
        }
    }

    public void save(String id, String answer){
        dao.save(id,answer);
    }

    public void load(Hashtable<String,String> data){
        id = data.get("id");
        title=data.get("title");
        question=data.get("question");
        answer=data.get("answer");
    }

    public static ArrayList<Question> load(IQuestionDAO dao){
        ArrayList<Question> questions = new ArrayList<Question>();
        if(dao != null){
            ArrayList<Hashtable<String,String>> objects = dao.load();
            for(Hashtable<String,String> obj : objects){
                Question q = new Question();
                q.setDao(dao);
                q.load(obj);
                questions.add(q);
            }
        }
        return questions;
    }
}

