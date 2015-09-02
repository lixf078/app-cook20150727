package com.shecook.wenyi.personal;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.shecook.wenyi.BaseActivity;
import com.shecook.wenyi.R;
import com.shecook.wenyi.mainpackage.FragmentTabAdapter;

public class PersonalEdition extends BaseActivity implements OnClickListener{
	
	private RadioGroup rgs;
	public List<Fragment> fragments = new ArrayList<Fragment>();
	public ImageView return_img, right_img;
	public TextView middle_title;
	
	PersonalEditionHomework homework;
	PersonalEditionTopic topic;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.personal_edition);
		initView();
	}
	
	
	public void initView(){
		
		right_img = (ImageView) findViewById(R.id.right_img);
		right_img.setVisibility(View.GONE);
		
		return_img = (ImageView) findViewById(R.id.return_img);
		return_img.setOnClickListener(PersonalEdition.this);
		homework = new PersonalEditionHomework();
		topic = new PersonalEditionTopic();
		fragments.add(homework);
		fragments.add(topic);

		rgs = (RadioGroup) findViewById(R.id.tabs_rg);

		FragmentTabAdapter tabAdapter = new FragmentTabAdapter(this, fragments,
				R.id.personal_edition_content, rgs);
		tabAdapter
				.setOnRgsExtraCheckedChangedListener(new FragmentTabAdapter.OnRgsExtraCheckedChangedListener() {
					@Override
					public void OnRgsExtraCheckedChanged(RadioGroup radioGroup,
							int checkedId, int index) {
						Log.d(TAG, "OnRgsExtraCheckedChanged -> " + index);
					}
				});
	}


	@Override
	public void onClick(View view) {
		int id = view.getId();
		switch (id) {
		case R.id.return_img:
			PersonalEdition.this.onBackPressed();
			break;

		default:
			break;
		}
	}
	
	
	
}
