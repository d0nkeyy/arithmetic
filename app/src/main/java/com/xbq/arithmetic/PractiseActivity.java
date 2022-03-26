package com.xbq.arithmetic;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.xbq.Question;
import com.xbq.utils.ConfigConstants;
import com.xbq.utils.QuestionGenerator;

import org.w3c.dom.Text;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Inet4Address;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

public class PractiseActivity extends AppCompatActivity implements View.OnClickListener{

    private ListView listViewQuestions;

    private Map<Integer, QuestionItem> questionItemMap = new HashMap<>();

    private QuestionGenerator questionGenerator;
    private ArrayList<Question> questions;

    private CountDownTimer countDownTimer;

    private boolean isCompleted = false;

    private Map<Integer, String> answers = new HashMap<>();
    private QuestionAdapter questionAdapter;
    private boolean isOpenTimer;

    private TextView remainTime;

    private  Button buttonSubmit;
    private Button buttonStart;

    private ImageButton redoImageButton;
    private ImageButton archiveImageButton;

    private CheckBox openTimer;

    private int score = 0;//得分

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_practise);
        setTitle("四则运算");

        openTimer = findViewById(R.id.openTimer);

        listViewQuestions = findViewById(R.id.questionListView);

        remainTime = findViewById(R.id.remainTime);

        buttonSubmit = findViewById(R.id.submitButton);
        buttonStart = findViewById(R.id.startButton);

        redoImageButton = findViewById(R.id.redoImageButton);
        archiveImageButton = findViewById(R.id.archiveImageButton);

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


        redoImageButton.setOnClickListener(this);
        archiveImageButton.setOnClickListener(this);
        buttonSubmit.setOnClickListener(this);
        buttonStart.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.redoImageButton:
                onRedoImageButtonClicked();
                break;
            case R.id.archiveImageButton:
                onArchiveImageButtonClicked();
                break;
            case R.id.startButton:
                onStartButtonClicked();
                break;
            case R.id.submitButton:
                onSubmitButtonClicked();
                break;
        }
    }

    public void onRedoImageButtonClicked() {
        AlertDialog.Builder confirmToRedo =
                new AlertDialog.Builder(PractiseActivity.this);
        if (!isCompleted) {
            if (isOpenTimer) {
                confirmToRedo.setMessage("测试正在进行中");
            }
            confirmToRedo.setTitle("确认重做吗？")
                    .setNeutralButton("点错了", null)
                    .setPositiveButton("确认", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            generateQuestionList();
                        }
                    });
            confirmToRedo.show();
        } else {
            generateQuestionList();
        }
    }

    public void onArchiveImageButtonClicked() {
        if (!isCompleted) {
            Toast.makeText(PractiseActivity.this, "未提交状态下不可存档", Toast.LENGTH_LONG).show();;
        } else {
            FileOutputStream fileOutputStream = null;
            String string = score + "\n";
            for (int i = 0; i < ConfigConstants.MAX_QUESTION_NUM; i++) {
                string += questions.get(i).getDetail();
                if (answers.get(i).equals(questions.get(i).getRightAnswer())) {
                    string += questions.get(i).getRightAnswer();
                    string += " ";
                    string += "✔️";
                } else {
                    string += answers.get(i);
                    string += " ";
                    string += "❌ 正确答案：" + questions.get(i).getRightAnswer();
                }
                string += "\n";
            }
            try {
                long currentTimeMillis = System.currentTimeMillis();
                fileOutputStream = openFileOutput(currentTimeMillis + ".ar", MODE_PRIVATE);
                fileOutputStream.write(string.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (fileOutputStream != null) {
                    try {
                        fileOutputStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            Toast.makeText(PractiseActivity.this, "本次训练已存档", Toast.LENGTH_SHORT).show();
        }
    }

    public void onStartButtonClicked() {
        isOpenTimer = openTimer.isChecked();
        LinearLayout linearLayout1 = findViewById(R.id.first_page);
        linearLayout1.setVisibility(View.GONE);
        LinearLayout linearLayout2 = findViewById(R.id.second_page);
        linearLayout2.setVisibility(View.VISIBLE);

        if (isOpenTimer) {
            countDownTimer.start();
            remainTime.setVisibility(View.VISIBLE);
        } else {
            remainTime.setVisibility(View.GONE);
        }

        //初始化适配器
        questionAdapter = new QuestionAdapter();
        listViewQuestions.setAdapter(questionAdapter);
        generateQuestionList();
    }

    public void onSubmitButtonClicked() {
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


    class QuestionAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return questionItemMap.size();
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
            QuestionItem q = questionItemMap.get(position);

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
                    if (editable != null && !editable.equals("")) {
                        int pos = (Integer) editText.getTag();

                        answers.put(pos, editable.toString());
                        Log.i("" + pos,editable.toString());
                    }
                }
            }
            editText.addTextChangedListener(new MyTextWatcher(editText));
            //解决因EditText复用导致的数据显示问题
            editText.setText(answers.get(position));
            editText.clearFocus();

            return view;
        }
    }

    public void initAnswerSheet() {
        //初始化答题卡
        for (int i = 0; i < ConfigConstants.MAX_QUESTION_NUM; i++) {
            answers.put(i, "");
        }
    }

    public void initQuestionList() {
        //清空问卷
        for (int i = 0; i < ConfigConstants.MAX_QUESTION_NUM; i++) {
            questionItemMap.put(i, null);
        }
    }

    public void generateAnswerList() {
        for (int i = 0; i < questions.size(); i++) {
            QuestionItem questionItem = new QuestionItem();
            questionItem.setQuestionDetail(questions.get(i).getDetail());
            questionItem.setQuestionSerial(i + 1);
            if (answers.get(i).equals(questions.get(i).getRightAnswer())) {
                questionItem.setRightAnswer("✔️");
            } else {
                questionItem.setRightAnswer("❌ 正确答案：" + questions.get(i).getRightAnswer());
            }
            questionItemMap.put(i,questionItem);
        }
        questionAdapter.notifyDataSetChanged();
    }

    public void generateQuestionList() {
        isCompleted = false;

        questionGenerator = new QuestionGenerator();
        questions = questionGenerator.generate();
        initQuestionList();
        initAnswerSheet();

        for (int i = 0; i < questions.size(); i++) {
            QuestionItem questionItem = new QuestionItem();
            questionItem.setQuestionDetail(questions.get(i).getDetail());
            questionItem.setQuestionSerial(i + 1);
            questionItem.setRightAnswer("");
            questionItemMap.put(i, questionItem);
        }
        questionAdapter.notifyDataSetChanged();
        if (isOpenTimer) {
            countDownTimer.start();
        }
    }

    public void onComplete() {
        isCompleted = true;
        if (isOpenTimer) {
            countDownTimer.cancel();
        }
        score = 0;
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


    //设置菜单
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_practise_activity, menu);
        return super.onCreateOptionsMenu(menu);
    }
    //菜单的点击事件
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.archive:
                gotoArchiveActivity();
                return true;
            case R.id.statistics:
                gotoStatisticActivity();
                return true;
            case R.id.about:
                showAbout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void gotoStatisticActivity() {
        Intent intent = new Intent(PractiseActivity.this, StatisticActivity.class);
        startActivity(intent);
    }

    public void gotoArchiveActivity() {
        Intent intent = new Intent(PractiseActivity.this, ArchiveActivity.class);
        startActivity(intent);
    }

    public void showAbout() {
        AlertDialog.Builder dialogAbout =
                new AlertDialog.Builder(PractiseActivity.this)
                        .setTitle("关于")
                        .setMessage("By 谢本齐");
        dialogAbout.show();
    }
}