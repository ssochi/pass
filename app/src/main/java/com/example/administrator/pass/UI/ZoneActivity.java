package com.example.administrator.pass.UI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.baidu.mapapi.SDKInitializer;
import com.example.administrator.pass.Adapter.ChatMsgViewAdapter;
import com.example.administrator.pass.DataStructure.ChatMsgEntity;
import com.example.administrator.pass.Fragment.trajectoryFragment;
import com.example.administrator.pass.R;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


/**
 * Created by Administrator on 11/1 0001.
 * 主界面
 */

public class ZoneActivity extends AppCompatActivity implements View.OnClickListener{
	private Button mBtToMap ;//跳转至大地图
    private Button mBtnSend;// 发送btn
    private EditText mEditTextContent;
    private ListView mListView;
    private ChatMsgViewAdapter mAdapter;// 消息视图的Adapter
    private List<ChatMsgEntity> mDataArrays = new ArrayList<>();// 消息对象数组
	private trajectoryFragment trajectoryFragment;

//  以下为模拟的测试数据

    private String[] msgArray = new String[]
            { "你为什么要召唤我？", "为了十四斤的农药", "讲课开始了~~", "嘿嘿，该收钱了~",
            "侍从！来我房间！", "~啊~感觉真棒！", "随风而去吧 小虫子们！ ", "我对你非常感谢",
            "铭记于心年轻人", "这群怪不是我引的", "你~需要我的帮助~" };

    private String[] dataArray = new String[] { "2012-09-22 18:00:02",
            "2012-09-22 18:10:22", "2012-09-22 18:11:24",
            "2012-09-22 18:20:23", "2012-09-22 18:30:31",
            "2012-09-22 18:35:37", "2012-09-22 18:40:13",
            "2012-09-22 18:50:26", "2012-09-22 18:52:57",
            "2012-09-22 18:55:11", "2012-09-22 18:56:45",
            "2012-09-22 18:57:33", };
    private final static int COUNT = 12;// 初始化数组总数



	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zone);
        init();     //初始化操作在这里
        setDefaultFragment();
        simulation();   //模拟数据的获取
        mListView.setSelection(mAdapter.getCount() - 1);
	}

    private void simulation() {
        for (int i = 0; i < COUNT-1; i++) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setDate(dataArray[i]);
            if (i % 2 == 0) {
                entity.setName("路人甲");
                entity.setMsgType(true);// 收到的消息
            } else {
                entity.setName("骑马卖火柴");
                entity.setMsgType(false);// 自己发送的消息
            }
            entity.setMessage(msgArray[i]);
            mDataArrays.add(entity);
        }
        mAdapter = new ChatMsgViewAdapter(this, mDataArrays);
        mListView.setAdapter(mAdapter);
    }

    private void init() {
        SDKInitializer.initialize(getApplicationContext());
        mBtToMap = (Button) findViewById(R.id.btnToMap);
        mBtToMap.setOnClickListener(this);
        mBtnSend = (Button) findViewById(R.id.bt_zoneCatting_send);
        mBtnSend.setOnClickListener(this);
        mEditTextContent = (EditText) findViewById(R.id.et_sendmessage);
        mListView = (ListView) findViewById(R.id.listView_zoneChatting);
    }


    private void setDefaultFragment()
	{
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		trajectoryFragment = new trajectoryFragment();
		transaction.replace(R.id.trajectory_framelayout, trajectoryFragment);
		transaction.commit();
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnToMap:
                startActivity(new Intent(ZoneActivity.this,MapActivity.class));
                break;
            case R.id.bt_zoneCatting_send:
                send();
        }
    }

    private void send() {
        String contString = mEditTextContent.getText().toString();
        if (contString.length() > 0) {
            ChatMsgEntity entity = new ChatMsgEntity();
            entity.setName("骑马卖火柴");
            entity.setDate(getDate());
            entity.setMessage(contString);
            entity.setMsgType(false);
            mDataArrays.add(entity);
            mAdapter.notifyDataSetChanged();// 通知ListView，数据已发生改变
            mEditTextContent.setText("");// 清空编辑框数据
            mListView.setSelection(mListView.getCount() - 1);// 发送一条消息时，ListView显示选择最后一项
        }
    }

    /**
     * 发送消息时，获取当前事件
     *
     * @return 当前时间
     */
    private String getDate() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        return format.format(new Date());
    }

}
