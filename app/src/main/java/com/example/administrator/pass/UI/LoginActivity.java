package com.example.administrator.pass.UI;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.example.administrator.pass.Fragment.LoginFragment;
import com.example.administrator.pass.R;

public class LoginActivity extends AppCompatActivity {
	LoginFragment loginFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);
//		getSupportActionBar().hide();
		setDefaultFragment();

	}
	private void setDefaultFragment()
	{
		FragmentManager fm = getFragmentManager();
		FragmentTransaction transaction = fm.beginTransaction();
		loginFragment = new LoginFragment();
		transaction.replace(R.id.LoginFrameLayout, loginFragment);
		transaction.commit();
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (getFragmentManager().getBackStackEntryCount()!=0){
			if(keyCode == KeyEvent.KEYCODE_BACK){
				getFragmentManager().popBackStack();
			}
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
