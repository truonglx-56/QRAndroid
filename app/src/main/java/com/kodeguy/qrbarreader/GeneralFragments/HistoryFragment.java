package com.kodeguy.qrbarreader.GeneralFragments;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.kodeguy.qrbarreader.MainActivity;
import com.kodeguy.qrbarreader.R;
import com.kodeguy.qrbarreader.ResultFragments.ContactFragment;
import com.kodeguy.qrbarreader.ResultFragments.EmailFragment;
import com.kodeguy.qrbarreader.ResultFragments.ProductFragment;
import com.kodeguy.qrbarreader.ResultFragments.SendEmailFragment;
import com.kodeguy.qrbarreader.ResultFragments.SmsFragment;
import com.kodeguy.qrbarreader.ResultFragments.TelFragment;
import com.kodeguy.qrbarreader.ResultFragments.UrlFragment;
import com.kodeguy.qrbarreader.ResultFragments.WifiFragment;
import com.kodeguy.qrbarreader.Utility.FragmentGenerator;
import com.kodeguy.qrbarreader.Utility.History;
import com.kodeguy.qrbarreader.Utility.HistoryEntry;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class HistoryFragment extends Fragment {

    private ArrayList<HistoryEntry> historyEntriesF = new ArrayList<HistoryEntry>();
    private List<HistoryViewElement> historyViewElements = new ArrayList<HistoryViewElement>();
    private View rootView;
    private LinearLayout previousScanned;
    private LayoutInflater inflater;
    private HistoryViewElement clickedElement;


    public HistoryFragment() {
        // Empty constructor required for fragment subclasses
    }

    public class HistoryViewElement extends View {

        public int id;
        public String content;
        public boolean trust;
        public View view;

        public HistoryViewElement(Context context) {
            super(context);
            // TODO Auto-generated constructor stub
        }

        public void setArgs(int id, String content, boolean trust, View view) {
            this.id = id;
            this.content = content;
            this.trust = trust;
            this.view = view;
        }

        public void setView(View newView){
            this.view = newView;
        }

        @Override
        public void setId(int id){
            this.id = id;
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        this.inflater = inflater;
        rootView = inflater.inflate(R.layout.fragment_history, container, false);


//        AdView mAdView = (AdView)rootView.findViewById(R.id.adView);
//        AdRequest adRequest = new AdRequest.Builder()
//                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//                // Check the LogCat to get your test device ID
//                .addTestDevice("6D14AF06E5D36B23FFE00EA9F3FBF8AB")
//                .build();
//        mAdView.loadAd(adRequest);

        History.printHistory(getActivity());



        previousScanned = (LinearLayout)rootView.findViewById(R.id.previousScanned);

        SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(rootView.getContext());
        boolean checked = sharedPref.getBoolean("bool_history", true);

        historyEntriesF = History.getHistory(getActivity());

        if(historyEntriesF == null && checked){
            //there is nothing to load
        }
        else if(!checked){
            previousScanned.removeAllViews();
            Toast.makeText(getActivity(), getResources().getString(R.string.history_disabled), Toast.LENGTH_SHORT).show();
        }
        else if(checked) {
            previousScanned.removeAllViews();
            this.createViews();
            loadPreviousScanned(rootView, previousScanned);
        }

        return rootView;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        getActivity().getMenuInflater().inflate(R.menu.history, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    public void createViews() {
        for(HistoryEntry entry: historyEntriesF) {
            View newView = inflater.inflate(R.layout.row_previous, previousScanned, false);
            HistoryViewElement newElement = new HistoryViewElement(rootView.getContext());
            newElement.setArgs(entry.id, entry.content, entry.trust, newView);
            historyViewElements.add(newElement);
        }
    }

    public void refresh() {
        ((MainActivity)getActivity()).selectItem(1,true);
    }

    private void remove(int id) {
        historyEntriesF.remove(id);
        History.saveHistory(getActivity(), historyEntriesF, false);
        refresh();
    }

    public void loadPreviousScanned(final View rootView, final LinearLayout previousScanned) {
        for(final HistoryViewElement element: historyViewElements) {

            final View newView = element.view;

            ImageView icon = (ImageView) newView.findViewById(R.id.icon);
            TextView firstLine = (TextView) newView.findViewById(R.id.result_field_text);
            TextView secondLine = (TextView) newView.findViewById(R.id.secondLine);
            String seconLineText = "";

            Fragment type= FragmentGenerator.getFragmentFromContent(element.content);

            if(type instanceof WifiFragment){
                icon.setImageResource(R.drawable.ic_action_network_wifi);
                String[] content = element.content.substring(element.content.indexOf(":") + 1).split(";");
                int ssid_id = 0;
                for(int i=0; i < content.length; i++) {
                    if(content[i].startsWith("S:")) ssid_id = i;
                }
                firstLine.setText(R.string.title_activity_result_wifi);
                seconLineText = content[ssid_id].substring(2);
                secondLine.setText(seconLineText);
            }
            else if(type instanceof ContactFragment){
                icon.setImageResource(R.drawable.ic_action_person);

                Pattern pattern = Pattern.compile("([\\n|;|:](FN:|N:)[0-9a-zA-Z-\\säöüÄÖÜß,]*[\\n|;])");
                Matcher m = pattern.matcher(element.content);
                String name = "";
                if(m.find())  {
                    name = m.group(1).substring(1);

                    if(name.startsWith("N:")) {
                        seconLineText = name.substring(2).replace(';', ' ');
                        secondLine.setText(seconLineText);
                    }
                    else if(name.startsWith("FN:")) {
                        seconLineText = name.substring(3).replace(';', ' ');
                        secondLine.setText(seconLineText);
                    }
                }

                firstLine.setText(R.string.title_activity_result_contact);
            }
            else if(type instanceof TelFragment) {
                icon.setImageResource(R.drawable.ic_action_call);
                firstLine.setText(R.string.title_activity_result_tel);
                seconLineText = element.content.substring(4);
                secondLine.setText(seconLineText);
            }
            else if(type instanceof SendEmailFragment) {
                icon.setImageResource(R.drawable.ic_action_new_email);
                Pattern r = Pattern.compile("MATMSG:TO:(.+?);SUB:");
                Matcher m = r.matcher(element.content);
                if(m.find()) {
                    seconLineText = m.group(1);
                    secondLine.setText(seconLineText);
                }
                firstLine.setText(R.string.title_activity_result_send_email);
            }
            else if(type instanceof EmailFragment){
                icon.setImageResource(R.drawable.ic_action_email);
                firstLine.setText(R.string.title_activity_result_email);
                seconLineText = element.content.subSequence(7, element.content.length()).toString();
                secondLine.setText(seconLineText);
            }
            else if(type instanceof SmsFragment) {
                icon.setImageResource(R.drawable.ic_action_chat);
                String content = element.content.substring(element.content.indexOf(":") + 1);
                String address = content.substring(0, content.indexOf(":"));
                seconLineText = address;
                secondLine.setText(seconLineText);
                firstLine.setText(R.string.title_activity_result_sms);
            }
            else if(type instanceof UrlFragment) {
                if (element.content.contains("maps.google") | element.content.contains("geo:"))
                    icon.setImageResource(R.drawable.ic_action_place);
                else
                    icon.setImageResource(R.drawable.ic_action_web_site);
                firstLine.setText(R.string.title_activity_result_url);
                seconLineText = element.content;
                secondLine.setText(seconLineText);
            }
            else if(type instanceof ProductFragment) {
                icon.setImageResource(R.drawable.ic_action_about);
                seconLineText = element.content;
                secondLine.setText(seconLineText);
                firstLine.setText(R.string.title_activity_result_product);
            }
            else {
                icon.setImageResource(R.drawable.ic_action_view_as_list);
                seconLineText = element.content;
                secondLine.setText(seconLineText);
                firstLine.setText(R.string.title_activity_result_text);
            }

            newView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Fragment frag = FragmentGenerator.getFragmentFromContent(element.content);
                    Bundle bundle = frag.getArguments();
                    bundle.putBoolean("trust", element.trust);
                    bundle.putBoolean("history", true);
                    frag.setArguments(bundle);
                    ((MainActivity)getActivity()).switchToFragment(frag, false);
                }
            });

            final String clipboardText = seconLineText;

            newView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {

                    newView.setBackgroundColor(Color.parseColor("#ff33b5e5"));

                    AlertDialog.Builder deleteDialog = new AlertDialog.Builder(rootView.getContext());

                    deleteDialog.setTitle("")
                            .setItems(R.array.history_array, new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                    switch (which) {
                                        case 0:
                                            ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                                            clipboard.setText(clipboardText);
                                            Toast.makeText(getActivity(), getResources().getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                                            newView.setBackgroundColor(Color.WHITE);
                                            break;
                                        case 1:
                                            remove(element.id);
                                            refresh();
                                            Toast.makeText(getActivity(), getResources().getString(R.string.element_removed), Toast.LENGTH_SHORT).show();
                                            newView.setBackgroundColor(Color.WHITE);
                                            break;
                                        default:
                                            newView.setBackgroundColor(Color.WHITE);
                                            break;
                                    }
                                }
                            });

                    deleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener () {
                        @Override
                        public void onCancel(DialogInterface dialog) {
                            newView.setBackgroundColor(Color.WHITE);
                        }
                    });
                    deleteDialog.show();
                    return true;
                }
            });


            previousScanned.addView(newView);
        }
    }


}
