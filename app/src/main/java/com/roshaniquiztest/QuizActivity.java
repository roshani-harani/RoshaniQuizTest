package com.roshaniquiztest;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.roshaniquiztest.POJO.QuestionsList;
import com.roshaniquiztest.POJO.Result;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class QuizActivity extends AppCompatActivity implements View.OnClickListener{

    private QuestionsList posts;
    private List<Result> quesList;
    private int score = 0;
    private int qid = 0;


    Result currentQ;
    TextView txtQuestion, times, scored;
    Button button1, button2, button3,button4;
    QuizHelper db = new QuizHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_quiz);
        txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        button1 = (Button) findViewById(R.id.button1);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.btn4);
        scored = (TextView) findViewById(R.id.score);
        times = (TextView) findViewById(R.id.timers);

        button1.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        getquestionlist();

    }
    public void getquestionlist() {
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(this,R.drawable.spinner);
        progressDialog.show();
        String JSON_URL = "https://opentdb.com/api.php?amount=10&category=9&difficulty=easy";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, JSON_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        GsonBuilder builder = new GsonBuilder();
                        Gson mGson = builder.create();
                        posts = (mGson.fromJson(response, QuestionsList.class));

                        for(int i=0;i<posts.getResults().size();i++){
                            db.addQuestion(posts.getResults().get(i));
                        }
                        quesList = db.getAllQuestions();
                        currentQ = quesList.get(qid);
                        setQuestionView();
                       // times.setText("00:02:00");


                        CounterClass timer = new CounterClass(180000, 1000);
                        timer.start();
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //displaying the error in toast if occurrs
                        Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);

    }
    public void getAnswer(String AnswerString) {
        if (currentQ.getCorrectAnswer().equals(AnswerString)) {


            score++;
            scored.setText("Score : " + score);
        } else {


//
//            Intent intent = new Intent(QuizActivity.this,
//                    ResultActivity.class);
//
//
//            Bundle b = new Bundle();
//            b.putInt("score", score);
//            intent.putExtras(b);
//            startActivity(intent);
//            finish();
        }
        if (qid < posts.getResults().size()) {


            currentQ = quesList.get(qid);
            setQuestionView();
        } else {


            Intent intent = new Intent(QuizActivity.this,won.class);
            Bundle b = new Bundle();
            b.putInt("score",score);
            intent.putExtras(b);
            startActivity(intent);
            finish();
        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button1:
                getAnswer(button1.getText().toString());
                break;
            case R.id.button2:
                getAnswer(button2.getText().toString());
                break;
            case R.id.button3:
                getAnswer(button3.getText().toString());
                break;
            case R.id.btn4:
                getAnswer(button4.getText().toString());
                break;
        }
    }


    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("NewApi")
    public class CounterClass extends CountDownTimer {

        public CounterClass(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
            // TODO Auto-generated constructor stub
        }


        @Override
        public void onFinish() {
            times.setText("Time is up");
            open();
        }

        @Override
        public void onTick(long millisUntilFinished) {
            // TODO Auto-generated method stub

            long millis = millisUntilFinished;
            String hms = String.format(
                    "%02d:%02d:%02d",
                    TimeUnit.MILLISECONDS.toHours(millis),
                    TimeUnit.MILLISECONDS.toMinutes(millis)
                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
                            .toHours(millis)),
                    TimeUnit.MILLISECONDS.toSeconds(millis)
                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
                            .toMinutes(millis)));
            System.out.println(hms);
            times.setText(hms);
        }


    }

    private void setQuestionView() {

        // the method which will put all things together
        txtQuestion.setText(currentQ.getQuestion());
        button1.setText(currentQ.getOPTA());
        button2.setText(currentQ.getOPTB());
        button3.setText(currentQ.getOPTC());
        button4.setText(currentQ.getOPTD());
        qid++;
    }

    public void open(){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Time is up");
                alertDialogBuilder.setPositiveButton("Play Again",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                getquestionlist();
                            }
                        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }
}

