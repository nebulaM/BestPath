package com.github.android.bestpath;


import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.github.android.bestpath.R;

public class SettingsFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:better UI for settings
        addPreferencesFromResource(R.xml.preferences);
        //Toast.makeText(getActivity(),"TextCreate!",Toast.LENGTH_SHORT).show();
    }
   /* @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        return view;
    }*/
    /*@Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        getView().setClickable(true);
    }*/


    /*public void onSharedPreferenceChanged(SharedPreferences sharedPreferences,
                                          String key) {
        if (key.equals(MainActivity.SPColorBG)){
            Preference mPref = findPreference(key);
            mPref.setDefaultValue(sharedPreferences.getString(key, ""));
            setBackground(sharedPreferences.getString(key, ""));
        }
    }
    @Override
    public void onResume() {
        super.onResume();
        getPreferenceManager().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        Toast.makeText(getActivity(),"TextResume!",Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onPause() {
        super.onPause();
        getPreferenceManager().getSharedPreferences().unregisterOnSharedPreferenceChangeListener(this);
        Toast.makeText(getActivity(),"TextPasue!",Toast.LENGTH_SHORT).show();
    }*/


}


