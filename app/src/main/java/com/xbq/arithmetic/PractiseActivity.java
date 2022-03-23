package com.xbq.arithmetic;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import com.xbq.Question;
import com.xbq.utils.ConfigConstants;
import com.xbq.utils.QuestionGenerator;

import org.w3c.dom.Text;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PractiseActivity extends AppCompatActivity {

    private ListView listViewQuestions;

    private List<QuestionItem> questionItemsList = new ArrayList<>();

    private QuestionGenerator questionGenerator;
    private ArrayList<Question> questions;

    private CountDownTimer countDownTimer;

    private boolean isCompleted = false;

    private Map<Integer, String> answers = new HashMap<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practise);

        listViewQuestions = findViewById(R.id.questionListView);

        TextView remainTime = findViewById(R.id.remainTime);
        Button buttonSubmit = findViewById(R.id.submit);

        countDownTimer = new CountDownTimer(1000 * 60 * 10, 1) {
            @Override
            public void onTick(long l) {
                long min = (l / 1000) / 60;
                long sec = (l / 1000) % 60;
                remainTime.setText(MessageFormat.format("剩余时间：{0}:{1}", min, sec));
            }

            @Override
            public void onFinish() {
                remainTime.setText(MessageFormat.format("剩余时间：{0}:{0}", 0));
                isCompleted = true;
                onComplete();
            }
        };

        countDownTimer.start();
        //初始化答题卡
        for (int i = 0; i < ConfigConstants.MAX_QUESTION_NUM; i++) {
            answers.put(i, "");
        }

        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!isCompleted) {
                    AlertDialog.Builder confirmToSubmitDialog =
                            new AlertDialog.Builder(PractiseActivity.this)
                                    .setTitle("确认提交吗？")
                                    .setMessage("测试正在进行中")
                                    .setNeutralButton("点错了", null)
                                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            onComplete();
                                        }
                                    });
                    confirmToSubmitDialog.show();
                }
            }
        });
        generateQuestionList();
    }

    class QuestionAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return questionItemsList.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup viewGroup) {
            View view = null;
            if (convertView == null) {
                Log.i("info:", "没有缓存，重新生成" + position);
                LayoutInflater inflater = PractiseActivity.this.getLayoutInflater();
                view = inflater.inflate(R.layout.question_list_item_view, null);
            } else {
                Log.i("info:", "有缓存，不需要重新生成" + position);
                view = convertView;
            }
            QuestionItem q = questionItemsList.get(position);

            TextView serial = view.findViewById(R.id.serialNumber);
            serial.setText(String.valueOf(q.getQuestionSerial()));

            TextView detail = view.findViewById(R.id.questionDetail);
            detail.setText(q.getQuestionDetail());

            TextView answer = view.findViewById(R.id.answerTextView);
            answer.setText(q.getRightAnswer());

            EditText editText = view.findViewById(R.id.answerEditText);
            editText.setTag(position);

            class MyTextWatcher implements TextWatcher {
                private EditText editText;
                public MyTextWatcher(EditText editText1)  {
                    this.editText = editText1;
                }
                @Override
                public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                }

                @Override
                public void afterTextChanged(Editable editable) {
                    if (editable != null) {
                        int pos = (Integer) editText.getTag();

                        answers.put(pos, editable.toString());
                    }
                }
            }
            editText.addTextChangedListener(new MyTextWatcher(editText));

            return view;
        }
    }
    private QuestionAdapter questionAdapter;

    public void generateAnswerList() {
        for (int i = 0; i < questions.size(); i++) {
            QuestionItem questionItem = new QuestionItem();
            questionItem.setQuestionDetail(questions.get(i).getDetail());
            questionItem.setQuestionSerial(i + 1);
            if (answers.get(i).equals(questions.get(i).getRightAnswer())) {
                questionItem.setRightAnswer("√");
            } else {
                questionItem.setRightAnswer("× 正确答案：" + questions.get(i).getRightAnswer());
            }
            questionItemsList.set(i,questionItem);
        }
        questionAdapter.notifyDataSetChanged();
    }

    public void generateQuestionList() {
        isCompleted = false;
        questionAdapter = new QuestionAdapter();
        questionGenerator = new QuestionGenerator();
        questions = questionGenerator.generate();

        for (int i = 0; i < questions.size(); i++) {
            QuestionItem questionItem = new QuestionItem();
            questionItem.setQuestionDetail(questions.get(i).getDetail());
            questionItem.setQuestionSerial(i + 1);
            questionItem.setRightAnswer("");
            questionItemsList.add(questionItem);
        }
        listViewQuestions.setAdapter(questionAdapter);
        countDownTimer.start();
    }

    public void onComplete() {
        isCompleted = true;
        countDownTimer.cancel();
        int score = 0;
        for (int i = 0; i < questions.size(); i++) {
            if (answers.get(i).equals(questions.get(i).getRightAnswer())) {
                score += 5;
            }
        }

        AlertDialog.Builder completeDialog =
                new AlertDialog.Builder(PractiseActivity.this)
                .setTitle("得分")
                .setMessage("" + score)
                .setCancelable(false)
                .setNeutralButton("查看答案", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        generateAnswerList();
                    }
                })
                .setPositiveButton("再刷一套", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        generateQuestionList();
                    }
                });
        completeDialog.show();
    }

    @Override
    public void onBackPressed() {
        if (!isCompleted) {
            AlertDialog.Builder backPressedWarning =
                    new AlertDialog.Builder(PractiseActivity.this)
                            .setTitle("确认返回吗？")
                            .setMessage("测试正在进行中，退出后操作将不保存记录！")
                            .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    finish();
                                }
                            })
                            .setNeutralButton("点错了", null);
            backPressedWarning.show();
        } else {
            finish();
        }
    }
}