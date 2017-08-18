package com.kodeguy.qrbarreader.GeneralFragments;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.kodeguy.qrbarreader.BuildConfig;
import com.kodeguy.qrbarreader.R;

public class AboutFragment extends Fragment {

    private Activity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View rootView = inflater.inflate(R.layout.fragment_about, container, false);

        ((TextView)rootView.findViewById(R.id.secusoWebsite)).setMovementMethod(LinkMovementMethod.getInstance());

        ((TextView)rootView.findViewById(R.id.textFieldVersionName)).setText(BuildConfig.VERSION_NAME);
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.activity = activity;
    }

}