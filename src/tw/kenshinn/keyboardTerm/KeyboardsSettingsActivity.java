package tw.kenshinn.keyboardTerm;


import java.lang.reflect.Array;
import java.util.ArrayList;

import tw.kenshinn.keyboardTerm.R;

import android.app.AlertDialog;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.Preference.OnPreferenceChangeListener;
import android.util.Log;



public class KeyboardsSettingsActivity extends PreferenceActivity {
	private static final String TAG = "KeyboardsSettings";
	private ArrayList<String> mKeyDefinesList = new ArrayList<String>();
	private ArrayList<String> mKeyValuesList = new ArrayList<String>();
	
	private int mCount;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		 String[] definedArray = getResources().getStringArray(R.array.key_defined_name);
		 for(int i = 0; i < definedArray.length; i++) 
			 mKeyDefinesList.add(definedArray[i]);
		 String[] valueArray = getResources().getStringArray(R.array.key_defined_value);
		 for(int i = 0; i < definedArray.length; i++) 
			 mKeyValuesList.add(valueArray[i]);		 
		
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		try {
			addPreferencesFromResource(R.xml.keyboards);
		} catch (ClassCastException e) {
			Log.e(TAG, "reset default values");
			PreferenceManager.setDefaultValues(this, R.xml.keyboards, true);
			addPreferencesFromResource(R.xml.keyboards);
		}
		
		
		
		mCount = Integer.parseInt(PreferenceManager.getDefaultSharedPreferences(this).getString("settings_arrow_key_group_count","1"));
		turnOnKeyboards(mCount);
		


		getPreferenceScreen().findPreference("settings_use_arrow_key")
				.setOnPreferenceChangeListener(
						new OnPreferenceChangeListener() {
							public boolean onPreferenceChange(
									Preference preference, Object newValue) {
								boolean depend = (Boolean) newValue;
								getPreferenceScreen().findPreference(
										"settings_arrow_key_general").setEnabled(
										depend);
								getPreferenceScreen().findPreference(
								"settings_arrow_key_keyboards").setEnabled(
								depend);
								turnOnKeyboards(mCount);
								return true;
							}
						});
		
		
		getPreferenceScreen().findPreference("settings_arrow_key_group_count")
		.setOnPreferenceChangeListener(
				new OnPreferenceChangeListener() {
					public boolean onPreferenceChange(
							Preference preference, Object newValue) {
						int count = Integer.parseInt(newValue.toString());
						turnOnKeyboards(count);
						return true;
					}
				});


	}
	
	private void turnOnKeyboards(int count) {
		//int count = mCount;
		PreferenceGroup group = (PreferenceGroup)getPreferenceScreen().findPreference(
		"settings_arrow_key_keyboards");
		
		for (int i = 0; i < group.getPreferenceCount(); i++) {
			group.getPreference(i).setEnabled(false);
		}
		
		for(int i = 0; i < count ; i++) {
			Log.v("KeyboardsSettingsActivity", "group.getPreference(i): " + group.getPreference(i).getClass().getName());
			group.getPreference(i).setEnabled(true);
			updateKeyboardButtons((PreferenceGroup)(group.getPreference(i)));
		}
	}
	
	private void updateKeyboardButtons(final PreferenceGroup group) {
		Log.v("KeyboardsSettingsActivity", "updateKeyboardButtons, group: " + group);
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		String keyCountString = pref.getString(group.getPreference(0).getKey(), "8");
		int keyCount = Integer.parseInt(keyCountString);
		
		updateButtonState(group, keyCount);
	}
	
	private void updateButtonState(final PreferenceGroup group, int enableCount) {
		SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
		
		group.getPreference(0).setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				updateButtonState(group, Integer.parseInt(newValue.toString()));
				return true;
			}
		});
		
		group.getPreference(0).setSummary(String.valueOf(enableCount));
		for(int i = 1; i < group.getPreferenceCount(); i++) {
			Preference item = group.getPreference(i);
			String keyValue = pref.getString(item.getKey(), "DPAD_UP");
			
			item.setTitle(getResources().getString(R.string.setting_button) + " " + i);
			if(mKeyValuesList.contains(keyValue)) {
				int index = mKeyValuesList.indexOf(keyValue);
				//Log.v("KeyboardsSettingsActivity", "set button " + i + " : " + mKeyDefinesList.get(index) + ", keyValue: " + keyValue + ", key: " + item.getKey());				
				item.setSummary(mKeyDefinesList.get(index));
			}
			item.setOnPreferenceChangeListener(mButtonChangeListener);
			
			if(i <= enableCount)
				item.setEnabled(true);
			else 
				item.setEnabled(false);
		}
	}
	
	private OnPreferenceChangeListener mButtonChangeListener = new OnPreferenceChangeListener() {
		
		public boolean onPreferenceChange(Preference preference, Object newValue) {
			if(mKeyValuesList.contains(newValue)) {
				int index = mKeyValuesList.indexOf(newValue);							
				preference.setSummary(mKeyDefinesList.get(index));
			}
			return true;
		}
	};


}
