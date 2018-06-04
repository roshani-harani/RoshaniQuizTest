package com.roshaniquiztest;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
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

public class QuizActivity extends AppCompatActivity implements View.OnClickListener{

    private QuestionsList posts;
    private List<Result> quesList;
    private int score = 0;
    private int qid = 0;
    String JSON_URL = "https://opentdb.com/api.php?amount=10&category=9&difficulty=easy";
    Result currentQ;
    TextView txtQuestion, times, scored;
    Button option1, option2, option3,option4;
    QuizHelper db = new QuizHelper(this);
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_quiz);
        txtQuestion = (TextView) findViewById(R.id.txtQuestion);
        option1 = (Button) findViewById(R.id.option1);
        option2 = (Button) findViewById(R.id.option2);
        option3 = (Button) findViewById(R.id.option3);
        option4 = (Button) findViewById(R.id.option4);
        scored = (TextView) findViewById(R.id.score);
        times = (TextView) findViewById(R.id.timers);

        option1.setOnClickListener(this);
        option2.setOnClickListener(this);
        option3.setOnClickListener(this);
        option4.setOnClickListener(this);
        getquestionlist();

    }
    public void getquestionlist() {
        final TransparentProgressDialog progressDialog = new TransparentProgressDialog(this,R.drawable.spinner);
        progressDialog.show();
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


//                        CounterClass timer = new CounterClass(180000, 1000);
//                        timer.start();
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
    public void getAnswer(String AnswerString, Button button) {
        if (currentQ.getCorrectAnswer().equals(AnswerString)) {

            button.setBackgroundColor(getResources().getColor(R.color.green));
            score++;
            scored.setText("Score : " + score);
        } else {
            button.setBackgroundColor(getResources().getColor(R.color.red));
        }
        if (qid < posts.getResults().size()) {


            currentQ = quesList.get(qid);
            new CountDownTimer(1000,1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                     setQuestionView();
                }
            }.start();

        } else {
            new CountDownTimer(1000,1000){

                @Override
                public void onTick(long millisUntilFinished) {

                }

                @Override
                public void onFinish() {
                    Intent intent = new Intent(QuizActivity.this,wonActivity.class);
                    Bundle b = new Bundle();
                    b.putInt("score",score);
                    intent.putExtras(b);
                    startActivity(intent);
                    finish();
                }
            }.start();


        }


    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.option1:
                getAnswer(option1.getText().toString(),option1);
                break;
            case R.id.option2:
                getAnswer(option2.getText().toString(),option2);
                break;
            case R.id.option3:
                getAnswer(option3.getText().toString(),option3);
                break;
            case R.id.option4:
                getAnswer(option4.getText().toString(),option4);
                break;
        }
    }


//    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
//    @SuppressLint("NewApi")
//    public class CounterClass extends CountDownTimer {
//
//        public CounterClass(long millisInFuture, long countDownInterval) {
//            super(millisInFuture, countDownInterval);
//            // TODO Auto-generated constructor stub
//        }
//
//
//        @Override
//        public void onFinish() {
//            times.setText("Time is up");
//            open();
//        }
//
//        @Override
//        public void onTick(long millisUntilFinished) {
//            // TODO Auto-generated method stub
//
//            long millis = millisUntilFinished;
//            String hms = String.format(
//                    "%02d:%02d:%02d",
//                    TimeUnit.MILLISECONDS.toHours(millis),
//                    TimeUnit.MILLISECONDS.toMinutes(millis)
//                            - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS
//                            .toHours(millis)),
//                    TimeUnit.MILLISECONDS.toSeconds(millis)
//                            - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS
//                            .toMinutes(millis)));
//            System.out.println(hms);
//            times.setText(hms);
//        }
//
//
//    }

    private void setQuestionView() {

        option1.setBackgroundColor(getResources().getColor(R.color.light_red_2));
        option2.setBackgroundColor(getResources().getColor(R.color.light_red_2));
        option3.setBackgroundColor(getResources().getColor(R.color.light_red_2));
        option4.setBackgroundColor(getResources().getColor(R.color.light_red_2));
        if(currentQ.getOPTC().equals("") && currentQ.getOPTD().equals("")){
            option3.setVisibility(View.GONE);
            option4.setVisibility(View.GONE);
        }else {
            option3.setVisibility(View.VISIBLE);
            option4.setVisibility(View.VISIBLE);
        }
        // the method which will put all things together
        txtQuestion.setText(currentQ.getQuestion());
        option1.setText(currentQ.getOPTA());
        option2.setText(currentQ.getOPTB());
        option3.setText(currentQ.getOPTC());
        option4.setText(currentQ.getOPTD());
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

