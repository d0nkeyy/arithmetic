package com.xbq.arithmetic;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

public class ArchiveActivity extends AppCompatActivity {

    private ListView archivesListView;

    private ArchiveItemAdapter archiveItemAdapter;

    private List<String> archiveNameList;
    private List<String> archiveItemList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_archive);
        setTitle("归档");

        archivesListView = findViewById(R.id.archivesListView);
        archiveItemAdapter = new ArchiveItemAdapter();

        archiveItemList = new LinkedList<>();
        FileInputStream fileInputStream = null;
        Reader reader = null;
        BufferedReader bufferedReader = null;

        File f = new File("/data/data/com.xbq.arithmetic/files/");
        File[] archiveFiles = f.listFiles();
        assert archiveFiles != null;
        for (int i = 0; i < archiveFiles.length; i++) {
            archiveItemList.add("");
        }
        //一定要在有数据之后设置适配器
        archivesListView.setAdapter(archiveItemAdapter);
        for (int i = 0; i < archiveFiles.length; i++) {
            File _f = archiveFiles[i];
            if (_f.isFile() && _f.getName().endsWith(".ar")) {
                String fileName = _f.getName();
                Log.i("file name", fileName);
                try {
                    fileInputStream = openFileInput(fileName);
                    reader = new InputStreamReader(fileInputStream);
                    bufferedReader = new BufferedReader(reader);
                    String temp = bufferedReader.readLine();

                    fileName = fileName.substring(0, fileName.indexOf("."));
                    SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy年MM月dd日 HH:mm", Locale.getDefault());
                    Date d = new Date(Long.parseLong(fileName));
                    String date = simpleDateFormat.format(d);

                    archiveItemList.set(i, date + " " + temp + "分");
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        archiveItemAdapter.notifyDataSetChanged();
        if (reader != null) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (fileInputStream != null) {
            try {
                fileInputStream.close();
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

        archivesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                Intent intent = new Intent(ArchiveActivity.this, ArchiveDetailActivity.class);
                intent.putExtra("File", archiveFiles[i]);
                startActivity(intent);
            }
        });
    }
    class ArchiveItemAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return archiveItemList.size();
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
                LayoutInflater layoutInflater = ArchiveActivity.this.getLayoutInflater();
                view = layoutInflater.inflate(R.layout.archive_list_item_view, null);
            } else {
                Log.i("info:", "有缓存，不需要重新生成" + position);
                view = convertView;
            }

            TextView archiveItem = view.findViewById(R.id.archiveItemTextView);
            archiveItem.setText(archiveItemList.get(position));

            return view;
        }
    }
}