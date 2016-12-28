package com.github.android.bestpath;


import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Toast;

public class HelpFragment extends Fragment implements View.OnClickListener{
    public static final String TAG="HelpFragment";
    private ImageView mNextPage;
    private ImageView mLastPage;
    private ImageView mPageContent;
    private ImageView mClose;
    private int currentPageNumber=0;
    private Toast mToast;
    private Boolean mSound;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Thread t1=new Thread(new Runnable() {
            @Override
            public void run() {
                mSound=getActivity().getSharedPreferences(MainActivity. SP_FILE_NAME, Context.MODE_PRIVATE).getBoolean(MainActivity.SP_KEY_SOUND, MainActivity.SP_KEY_SOUND_DEFAULT);
            }
        });
        t1.start();
        //overwrite never opened help in SP
        Thread t2=new Thread(new Runnable() {
            @Override
            public void run() {
                if(getActivity().getSharedPreferences(MainActivity. SP_FILE_NAME, Context.MODE_PRIVATE).getBoolean(MainActivity.SP_KEY_NEVER_OPENED_HELP,true)) {
                    getActivity().getSharedPreferences(MainActivity.SP_FILE_NAME, Context.MODE_PRIVATE).edit().putBoolean(MainActivity.SP_KEY_NEVER_OPENED_HELP, false).apply();
                }
            }
        });
        t2.start();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        mNextPage=(ImageView)view.findViewById(R.id.next_page);
        mLastPage=(ImageView)view.findViewById(R.id.last_page);
        mPageContent=(ImageView)view.findViewById(R.id.page_content);
        mClose=(ImageView)view.findViewById(R.id.close);

        setPageContent(true);

        mNextPage.setOnClickListener(this);
        mLastPage.setOnClickListener(this);
        mClose.setOnClickListener(this);
        mToast= Toast.makeText(getActivity().getApplicationContext(),"",Toast.LENGTH_SHORT);
        try{t1.join();}catch (InterruptedException e){e.printStackTrace();}
        try{t2.join();}catch (InterruptedException e){e.printStackTrace();}
        return view;
    }

    @Override
    public void onClick(View v) {
        MainActivity.playSound(TAG,mSound,"click");
        switch (v.getId()) {
            case R.id.next_page:
                setPageContent(true);
                break;
            case R.id.last_page:
                setPageContent(false);
                break;
            case R.id.close:
                //TODO:BUG the first time change language
                getFragmentManager().popBackStack();
                break;
            default:
                break;
        }
    }

    private void setPageContent(boolean nextPage) {
        if(nextPage){
            if (currentPageNumber < 4){
                currentPageNumber++;
            }else {
                mToast.setText(R.string.last_page);
                mToast.show();
                return;
            }
        }else {
            if (currentPageNumber > 1){
                currentPageNumber--;
            }else {
                mToast.setText(R.string.first_page);
                mToast.show();
                return;
            }
        }
        switch (MainActivity.DISPLAY_LANGUAGE){
            case MainActivity.LANGUAGE_JA:
                switch (currentPageNumber){
                    case 1:
                        mPageContent.setImageResource(R.drawable.help_ja_1);
                        break;
                    case 2:
                        mPageContent.setImageResource(R.drawable.help_ja_2);
                        break;
                    case 3:
                        mPageContent.setImageResource(R.drawable.help_ja_3);
                        break;
                    case 4:
                        mPageContent.setImageResource(R.drawable.help_ja_4);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LANGUAGE_ZH_PRC:
                switch (currentPageNumber){
                    case 1:
                        mPageContent.setImageResource(R.drawable.help_zh_1);
                        break;
                    case 2:
                        mPageContent.setImageResource(R.drawable.help_zh_2);
                        break;
                    case 3:
                        mPageContent.setImageResource(R.drawable.help_zh_3);
                        break;
                    case 4:
                        mPageContent.setImageResource(R.drawable.help_zh_4);
                        break;
                    default:
                        break;
                }
                break;
            case MainActivity.LANGUAGE_ZH_TW:
                switch (currentPageNumber){
                    case 1:
                        mPageContent.setImageResource(R.drawable.help_zh_tw_1);
                        break;
                    case 2:
                        mPageContent.setImageResource(R.drawable.help_zh_tw_2);
                        break;
                    case 3:
                        mPageContent.setImageResource(R.drawable.help_zh_tw_3);
                        break;
                    case 4:
                        mPageContent.setImageResource(R.drawable.help_zh_tw_4);
                        break;
                    default:
                        break;
                }
                break;
            default:
                switch (currentPageNumber) {
                    case 1:
                        mPageContent.setImageResource(R.drawable.help_en_1);
                        break;
                    case 2:
                        mPageContent.setImageResource(R.drawable.help_en_2);
                        break;
                    case 3:
                        mPageContent.setImageResource(R.drawable.help_en_3);
                        break;
                    case 4:
                        mPageContent.setImageResource(R.drawable.help_en_4);
                        break;
                    default:
                        break;
                }
                break;
        }
    }

}
