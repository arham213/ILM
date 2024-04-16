package com.example.ilm;


import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import androidx.activity.result.ActivityResultLauncher;
import androidx.recyclerview.widget.RecyclerView;

public class QuestionAdapter extends RecyclerView.Adapter<QuestionAdapter.NoteViewHolder> implements Filterable {
    private ArrayList<Question> mDataset;
    private ArrayList<Question> filteredQuestions;
    private QuestionItemClickListener listener;
    private Filter filter;
    private Context context;

    @Override
    public Filter getFilter() {
        if (filter == null){
            filter = new QuestionsFilter();
        }
        return filter;
    }
    public class NoteViewHolder extends RecyclerView.ViewHolder {
        public TextView title;
        public TextView content;
        public String question;
        public String answer;
        public NoteViewHolder(View v) {
            super(v);
            title = (TextView) v.findViewById(R.id.item_title);
            content =(TextView) v.findViewById(R.id.item_content);
            //context=v.getContext();

            v.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int pos = (int) v.getTag();
                    listener.onClick(filteredQuestions.get(pos));
                }
            });
        }
    }
    // Provide a suitable constructor (depends on the kind of dataset)
    public QuestionAdapter(ArrayList<Question> myDataset, QuestionItemClickListener ls) {
        mDataset = myDataset;
        filteredQuestions=myDataset;
        listener=ls;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public NoteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v=LayoutInflater.from(parent.getContext()).inflate(R.layout.item_question, parent, false);
        NoteViewHolder vh = new NoteViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(NoteViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        int viewType=getItemViewType(position);
        String title=filteredQuestions.get(position).getTitle();
        if(title.contains("\n")) {
            title=title.substring(0, title.indexOf('\n'));
        }
        holder.title.setText(title);

        String content = filteredQuestions.get(position).getAnswer();
        if(content.length()>=60) {
            String temp=content.substring(0, 60);
            temp=temp+"...";
            holder.content.setText(temp);
        }
        else{
            holder.content.setText(content);
        }

        holder.question=filteredQuestions.get(position).getQuestion();
        holder.answer=filteredQuestions.get(position).getAnswer();

        //set context
        context=holder.itemView.getContext();

        //set listeners
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(v.getContext(), ViewQuestion.class);
                intent.putExtra("title", "Question");
                intent.putExtra("question",holder.question);
                intent.putExtra("answer",holder.answer);
                v.getContext().startActivity(intent);
            }
        });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        if(filteredQuestions!=null)
            return filteredQuestions.size();
        return 0;
    }

    public void updateData(ArrayList<Question> ds){
        mDataset = ds;
        filteredQuestions = ds;
        notifyDataSetChanged();
    }

    public interface QuestionItemClickListener{
        public void onClick(Question n);
    }

    private class QuestionsFilter extends Filter {

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            FilterResults results = new FilterResults();
            if(constraint != null && constraint.length() > 0){
                ArrayList<Question> filteredList = new ArrayList<Question>();
                for(int i=0; i < mDataset.size(); i++){
                    if((mDataset.get(i).getQuestion().contains(constraint)) || (mDataset.get(i).getAnswer().contains(constraint))){
                        filteredList.add(mDataset.get(i));
                    }
                }

                results.count = filteredList.size();
                results.values = filteredList;

            }
            else{
                results.count = mDataset.size();
                results.values = mDataset;
            }

            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            filteredQuestions = (ArrayList<Question>) results.values;
            notifyDataSetChanged();
        }

    }

    /*@Override
    public int getItemViewType(int position){
        if(position%2==0){
            return 0;
        }
        return 1;
    }*/
}
