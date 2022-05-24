package com.example.quizitup.Results;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.TextView;

import com.example.quizitup.R;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ParticipantResultActivity extends AppCompatActivity {

    HashMap<String,Object> valueMap;
    String name;
    double total;

    TextView nameTxt,scoreTxt;
    RecyclerView marksRecycler;
    HashMap<String,Double> participantMap;
    ArrayList<ResultModel> resultList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_participant_result);

        nameTxt = findViewById(R.id.nameTxt);
        scoreTxt = findViewById(R.id.totalTxt);
        marksRecycler = findViewById(R.id.marksRecycler);

        valueMap =  (HashMap<String, Object>) getIntent().getSerializableExtra("output");
        participantMap = (HashMap<String, Double>) valueMap.get("marks");
        name = String.valueOf(valueMap.get("name"));
        total = (double) valueMap.get("scored");

        nameTxt.setText(name);
        scoreTxt.setText(total+"%");

        marksRecycler.setHasFixedSize(true);
        marksRecycler.setLayoutManager(new LinearLayoutManager(this));
        marksRecycler.setItemAnimator(new DefaultItemAnimator());
        
        resultList = new ArrayList<>();
        for (Map.Entry<String,Double> marks : participantMap.entrySet()) {
            resultList.add(new ResultModel(marks.getKey(),marks.getValue()));
        }

        ResultAdapter adapter = new ResultAdapter(resultList,this);
        marksRecycler.setAdapter(adapter);
        
        
    }
}