package com.kodeguy.qrbarreader.GeneralFragments;

import android.preference.PreferenceFragment;
import android.app.Fragment;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.kodeguy.qrbarreader.R;


public class HelpFragment extends PreferenceFragment {

    public HelpFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.help);
    }
}
