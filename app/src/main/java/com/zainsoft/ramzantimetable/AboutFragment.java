package com.zainsoft.ramzantimetable;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;


public class AboutFragment extends Fragment {
	
	private TextView txtMobile;
	private TextView txtEmail;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_about, container, false);
		txtEmail = (TextView) view.findViewById(R.id.textEmail);
		txtMobile = (TextView) view.findViewById(R.id.textPhone);
		
		txtMobile.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				callMe();
			}
		});

		txtEmail.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				emailMe();
			}
		});
		return view;
	}

	public static AboutFragment newInstance() {
		AboutFragment fragment = new AboutFragment();
		return fragment;
	}
	
	@Override
	public void onActivityCreated(@Nullable Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		setUserVisibleHint(true);
	}
	
	public void callMe() {
		Intent i = new Intent( Intent.ACTION_DIAL);
        String p = "tel:" + getString(R.string.contact_no);
        i.setData( Uri.parse(p));
        startActivity(i);
	}
	
	public void emailMe() {
		Intent email = new Intent( Intent.ACTION_SEND);
		email.putExtra( Intent.EXTRA_EMAIL, new String[]{getString(R.string.email)});
		email.putExtra( Intent.EXTRA_SUBJECT, "Regarding Salah Time application");
		email.putExtra( Intent.EXTRA_TEXT, "I am using your Salah Time application.");
		email.setType("message/rfc822");
		startActivity( Intent.createChooser(email, "Choose an Email client :"));
	}
}
