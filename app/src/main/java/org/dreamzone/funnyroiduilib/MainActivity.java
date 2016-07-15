package org.dreamzone.funnyroiduilib;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import org.dreamzone.funnyroiduilib.adapter.DemoListRecyclerAdapter;

import java.util.Arrays;
import java.util.List;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends FragmentActivity {
    @Bind(R.id.tv_demo_info)
    TextView tvDemoInfo;
    @Bind(R.id.rv_demo_list)
    RecyclerView rvDemoList;

    private List<String> titleList;
    private DemoListRecyclerAdapter demoRecyclerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);
        // 获取demo的题目列表
        tvDemoInfo.setText(R.string.demo_list);
        titleList = Arrays.asList(getResources().getStringArray(R.array.title_array));
        initRecyclerView();
        tvDemoInfo.requestFocus();
    }

    private void initRecyclerView() {
        demoRecyclerAdapter = new DemoListRecyclerAdapter(this, titleList);
        rvDemoList.setHasFixedSize(true);
        rvDemoList.setAdapter(demoRecyclerAdapter);
        rvDemoList.setLayoutManager(new LinearLayoutManager(this));
        rvDemoList.setItemAnimator(new DefaultItemAnimator());
        demoRecyclerAdapter.setItemClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                int pos = (int) v.getTag();
                displayDemoActivity(pos);
            }
        });
    }

    private void displayDemoActivity(int pos) {
        switch (pos) {
            case 0:
                SpringPickerViewActivity.start(this);
                break;
            case 1:
                BeautyDiffuseActivity.start(this);
                break;
            case 2:
                SpringPickerViewExpandActivity.start(this);
                break;
            default:
                break;
        }
    }
}
