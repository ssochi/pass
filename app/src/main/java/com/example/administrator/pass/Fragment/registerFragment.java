package com.example.administrator.pass.Fragment;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.example.administrator.pass.R;

/**
 * Created by Administrator on 10/30 0030.
 */
public class registerFragment extends Fragment {
	Button btn;
	AutoCompleteTextView tv_user;
	AutoCompleteTextView tv_password;
	AutoCompleteTextView tv_password_again;
	AutoCompleteTextView tv_email;
	mResgisterTask mResgisterTask;
	@Nullable
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View root = inflater.inflate(R.layout.fragment_register, container, false);
		btn = (Button) root.findViewById(R.id.btn_register_submit);
		tv_user = (AutoCompleteTextView) root.findViewById(R.id.tv_user);
		tv_password = (AutoCompleteTextView) root.findViewById(R.id.tv_password);
		tv_password_again = (AutoCompleteTextView) root.findViewById(R.id.tv_password_again);
		tv_email = (AutoCompleteTextView) root.findViewById(R.id.tv_email);
		btn.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				attempt();
			}
		});


		return root;
	}

	private void attempt() {
		if(mResgisterTask!=null){
			mResgisterTask = null;
		}
		String user = tv_user.getText().toString();
		String password = tv_password.getText().toString();
		String passwordAgain = tv_password_again.getText().toString();
		String email = tv_email.getText().toString();
		if(user.length()<6){
			tv_user.setError(getString(R.string.error_user_register));
			return;
		}
		if (password.length()<6){
			tv_password.setError(getString(R.string.error_password_register));
			return;
		}
		if (!passwordAgain.equals(password)){
			tv_password_again.setError(getString(R.string.error_password_again_register));
			return;
		}
		if (!email.contains("@")){
			tv_email.setError(getString(R.string.error_email_register));
			return;
		}
		mResgisterTask = new mResgisterTask(user,password,email);
		mResgisterTask.execute((Void[]) null);

	}



	class mResgisterTask extends AsyncTask<Void,Void,Boolean>{
		String user = tv_user.getText().toString();
		String password = tv_password.getText().toString();
		String email = tv_email.getText().toString();
		mResgisterTask(String user,String password,String email){
				this.user = user;
				this.password = password;
				this.email = email;
		}
		@Override
		protected Boolean doInBackground(Void... params) {
			return true;
		}

		@Override
		protected void onPostExecute(Boolean aBoolean) {
			if(aBoolean){
				FragmentManager fragmentManager = getActivity().getFragmentManager();
				FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
				fragmentTransaction.replace(R.id.LoginFrameLayout,new LoginFragment());
				fragmentTransaction.addToBackStack(null);
				fragmentTransaction.commit();
			}
		}
}
	public void setToast(String toast) {
		Toast.makeText(getActivity(),toast,Toast.LENGTH_LONG).show();
	}
}
