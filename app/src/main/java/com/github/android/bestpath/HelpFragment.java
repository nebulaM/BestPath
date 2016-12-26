package com.github.android.bestpath;


import android.app.Fragment;
import android.os.Bundle;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class HelpFragment extends Fragment implements View.OnClickListener{
    public static final String TAG="HelpFragment";
    private ImageView mNextPage;
    private ImageView mLastPage;
    private ImageView mPageContent;
    private ImageView mClose;
    private int currentPageNumber=0;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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

        return view;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.next_page:
                setPageContent(true);
                break;
            case R.id.last_page:
                setPageContent(false);
                break;
            case R.id.close:
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
                return;
            }
        }else {
            if (currentPageNumber > 1){
                currentPageNumber--;
            }else {
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
