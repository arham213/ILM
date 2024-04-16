package com.example.ilm;

import java.util.ArrayList;
import java.util.Hashtable;

public interface IQuestionDAO {

    public void save(Hashtable<String,String> attributes);
    public void save(String id, String answer);
    public void save(ArrayList<Hashtable<String,String>> objects);
    public ArrayList<Hashtable<String,String>> load();
    public Hashtable<String,String> load(String id);
    public void setObserver(QuestionFirebaseDAO.DataObserver obs);
}
