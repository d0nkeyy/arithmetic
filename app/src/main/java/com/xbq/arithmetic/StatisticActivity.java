package com.xbq.arithmetic;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class StatisticActivity extends AppCompatActivity {

    private Map<String, List<Integer>> scoreMapList;

    private Set<String> dateSet;

    private Spinner dateSpinner;

    private BarChart barChart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistic);

        scoreMapList = new HashMap<>();
        dateSet = new HashSet<>();

        dateSpinner = findViewById(R.id.dateSpinner);

        File f = new File("/data/data/com.xbq.arithmetic/files/");
        File[] archiveFiles = f.listFiles();

        InputStream inputStream = null;
        Reader reader = null;
        BufferedReader bufferedReader = null;
        //先将数据全部读取出来
        String tmpDate = null;
        List<Integer> scores = null;
        for (int i = 0; i < Objects.requireNonNull(archiveFiles).length; i++) {
            //获取日期数据，用来填充下拉框
            String fileName = archiveFiles[i].getName();
            fileName = fileName.substring(0, fileName.indexOf("."));
            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日", Locale.getDefault());
            Date d = new Date(Long.parseLong(fileName));
            String date = simpleDateFormat.format(d);
            dateSet.add(date);

            if (tmpDate == null) {
                tmpDate = date;
                scores = new LinkedList<>();
            } else if (!tmpDate.equals(date)){
                tmpDate = date;
                scores = new LinkedList<>();
            }

            try {
                inputStream = new FileInputStream(archiveFiles[i]);
                reader = new InputStreamReader(inputStream);
                bufferedReader = new BufferedReader(reader);
                //获取得分
                String tmp = bufferedReader.readLine();
                scores.add(Integer.parseInt(tmp));
                scoreMapList.put(tmpDate, scores);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (inputStream != null) {
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (bufferedReader != null) {
            try {
                bufferedReader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String[] array = dateSet.toArray(new String[dateSet.size()]);
        ArrayAdapter<String> dateSpinnerAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, array);
        dateSpinner.setAdapter(dateSpinnerAdapter);
        dateSpinner.setSelection(0);
        dateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                String date = adapterView.getItemAtPosition(i).toString();
                generateChart(scoreMapList.get(date));
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    public void generateChart(List<Integer> list) {
        //添加数据
        List<BarEntry> entries = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            entries.add(new BarEntry(i + 1, list.get(i)));
        }
        //将数据添加到数据集
        BarDataSet dataSet = new BarDataSet(entries, "得分");
        //柱体颜色
        dataSet.setColor(Color.parseColor("#FF6363"));
        //显示数值
        dataSet.setDrawValues(true);
        //数据集赋值给数据对象
        BarData data = new BarData(dataSet);
        //设置柱子宽度
        data.setBarWidth(0.2f);
        barChart = findViewById(R.id.barChart);
        Description description = barChart.getDescription();
        description.setText("");
        barChart.setDescription(description);
        //设置无数据时的显示
        barChart.setNoDataTextColor(Color.parseColor("#003B4C"));
        barChart.setNoDataText("暂无数据");
        barChart.setData(data);
        barChart.invalidate();

        //取消缩放
        barChart.setScaleEnabled(false);
        barChart.setClickable(false);
        barChart.setHighlightPerDragEnabled(false);
        barChart.setHighlightPerTapEnabled(false);
        //设置X轴
        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(new IndexAxisValueFormatter());
        xAxis.setDrawGridLines(false);

        //设置Y轴
        YAxis yAxis = barChart.getAxisLeft();
        yAxis.setDrawGridLines(false);
        YAxis rAxis = barChart.getAxisRight();
        rAxis.setEnabled(false);


    }
}