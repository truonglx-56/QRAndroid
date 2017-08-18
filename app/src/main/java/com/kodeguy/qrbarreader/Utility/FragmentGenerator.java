package com.kodeguy.qrbarreader.Utility;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;

import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.BarcodeResult;
import com.kodeguy.qrbarreader.ResultFragments.ContactFragment;
import com.kodeguy.qrbarreader.ResultFragments.EmailFragment;
import com.kodeguy.qrbarreader.ResultFragments.ProductFragment;
import com.kodeguy.qrbarreader.ResultFragments.SendEmailFragment;
import com.kodeguy.qrbarreader.ResultFragments.SmsFragment;
import com.kodeguy.qrbarreader.ResultFragments.TelFragment;
import com.kodeguy.qrbarreader.ResultFragments.TextFragment;
import com.kodeguy.qrbarreader.ResultFragments.UrlFragment;
import com.kodeguy.qrbarreader.ResultFragments.WifiFragment;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class FragmentGenerator {
    public static Fragment getFragment(BarcodeResult result) {
        Fragment fragment;
        String format = result.getBarcodeFormat().name();
        String content = result.getResult().toString();

        if(!format.contains("QR_CODE"))
            fragment = new ProductFragment();
        else {
            if (content.startsWith("WIFI:"))
                fragment = new WifiFragment();
            else if (content.startsWith("tel:"))
                fragment = new TelFragment();
            else if (content.startsWith("SMSTO:"))
                fragment = new SmsFragment();
            else if (content.startsWith("mailto:"))
                fragment = new EmailFragment();
            else if (content.startsWith("MATMSG:"))
                fragment = new SendEmailFragment();
            else if (content.startsWith("BEGIN:VCARD"))
                fragment = new ContactFragment();
            else if (content.startsWith("http://") || content.startsWith("https://") || content.startsWith("www."))
                fragment = new UrlFragment();
            else
                fragment = new TextFragment();
        }

        Bundle bundle = new Bundle();
        bundle.putString("result_content", result.getResult().toString());
        Log.e("TESTER", result.getResult().toString());
        fragment.setArguments(bundle);

        return fragment;
    }

    public static Fragment getFragmentFromContent(String content) {
        Fragment fragment;
        boolean isProduct = false;

        Pattern r = Pattern.compile("[0-9]+");
        Matcher m = r.matcher(content);
        if(m.find()) isProduct = true;

        if (content.startsWith("WIFI:"))
            fragment = new WifiFragment();
        else if (content.startsWith("tel:"))
            fragment = new TelFragment();
        else if (content.startsWith("SMSTO:"))
            fragment = new SmsFragment();
        else if (content.startsWith("mailto:"))
            fragment = new EmailFragment();
        else if (content.startsWith("MATMSG:"))
            fragment = new SendEmailFragment();
        else if (content.startsWith("BEGIN:VCARD"))
            fragment = new ContactFragment();
        else if (content.startsWith("http://") || content.startsWith("https://") || content.startsWith("www."))
            fragment = new UrlFragment();
        else if(isProduct)
            fragment = new ProductFragment();
        else
            fragment = new TextFragment();

        Bundle bundle = new Bundle();
        bundle.putString("result_content", content);
        fragment.setArguments(bundle);

        return fragment;
    }
}
