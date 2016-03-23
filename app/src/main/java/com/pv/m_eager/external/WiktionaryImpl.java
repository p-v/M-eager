package com.pv.m_eager.external;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;

import com.pv.m_eager.Dictionary;

/**
 * @author p-v
 */
public class WiktionaryImpl implements Dictionary{

    private Context context;
    private WebView webView;

    public WiktionaryImpl(Context context){
        this.context = context;
    }

    @Override
    public View getView() {
        webView = new WebView(context);
        webView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        return webView;
    }

    @Override
    public void onLoad(String word) {
        webView.loadUrl(String.format("https://en.wiktionary.org/w/index.php?title=%s&printable=yes",word));
    }
}
