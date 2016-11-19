package com.example.administrator.pass.UI;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;

import com.baidu.mapapi.SDKInitializer;
import com.example.administrator.pass.Fragment.trajectoryFragment;
import com.example.administrator.pass.R;

/**
 * Created by Administrator on 11/1 0001.
 * 主界面
 */

public class ZoneActivity extends AppCompatActivity {
	Button btnToMap ;
	FrameLayout fl;
	trajectoryFragment trajectoryFragment;

	@Override
	protected void onCreate(@Nullable Bundle savedInstanceState) {
		SDKInitializer.initialize(getApplicationContext());
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zone);
		fl = (FrameLayout) findViewById(R.id.trajectory_framelayout);
		setDefaultFragment();
		btnToMap = (Button) findViewById(R.id.btnToMap);
		btnToMap.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				startActivity(new Intent(ZoneActivity.this,MapActivity.class));
			}
		});
	}

	private void setDefaultFragment()
	{
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		trajectoryFragment = new trajectoryFragment();
		transaction.replace(R.id.trajectory_framelayout, trajectoryFragment);
		transaction.commit();
	}

}
