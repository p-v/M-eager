package com.pv.m_eager;

import android.os.Bundle;
import android.support.v4.widget.NestedScrollView;
import android.support.v7.app.AppCompatActivity;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.pv.m_eager.external.LocalDictionaryImpl;
import com.pv.m_eager.external.WiktionaryImpl;

public class SearcherActivity extends AppCompatActivity {

    private Dictionary getDictionary(int flag){
        switch (flag){
            case 1:
                return new WiktionaryImpl(this);
            case 2:
                return new LocalDictionaryImpl(this);
        }
        return null;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcher);

        NestedScrollView view = (NestedScrollView)findViewById(R.id.scrollView);
        Dictionary dictionary = getDictionary(2);
        if(dictionary == null){
            Toast.makeText(this,"Something went wrong",Toast.LENGTH_LONG).show();
            finish();
            return;
        }
        view.addView(dictionary.getView(),new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

        TextView title = (TextView)findViewById(R.id.word_title);
        String word = getIntent().getStringExtra("word");
        title.setText(word);

        dictionary.onLoad(word);
    }

}
