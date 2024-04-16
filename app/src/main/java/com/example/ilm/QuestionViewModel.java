package com.example.ilm;

import android.os.Bundle;

import java.io.File;
import java.util.ArrayList;

import androidx.lifecycle.ViewModel;

public class QuestionViewModel extends ViewModel {

    private ArrayList<Question> questions;
    IQuestionDAO dao;

    public ArrayList<Question> getNotes(Bundle savedInstanceState, String key){
        if (questions == null){
            if (savedInstanceState == null) {
               if (dao != null){
                    questions = Question.load(dao);
                }
                else questions = new ArrayList<Question>();
            }
            else{
                questions = (ArrayList<Question>) savedInstanceState.get(key);
            }
        }
        return questions;
    }

    public void setDao(IQuestionDAO d){
        dao = d;
    }
    public ArrayList<Question> update(){
        if (dao != null){
            questions = Question.load(dao);
        }
        else questions = new ArrayList<Question>();
        return questions;
    }
}
