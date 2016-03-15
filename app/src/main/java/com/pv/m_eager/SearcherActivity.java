package com.pv.m_eager;

import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.pv.m_eager.model.Meaning;

import java.util.List;

public class SearcherActivity extends AppCompatActivity {

    private GetMeaningAsyncTask asyncTask;
    private TextView meaning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_searcher);

        TextView title = (TextView)findViewById(R.id.word_title);
        meaning = (TextView)findViewById(R.id.meaning_container);

        String word = getIntent().getStringExtra("word");
        title.setText(word);

        asyncTask = new GetMeaningAsyncTask();
        asyncTask.execute(word);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if(asyncTask!=null){
            asyncTask.cancel(true);
        }
    }

    private class GetMeaningAsyncTask extends AsyncTask<String,String,List<Meaning>> {

        @Override
        protected List<Meaning> doInBackground(String[] params) {
            MeagerDbHelper dbHelper = new MeagerDbHelper(getApplicationContext());
            dbHelper.createDatabase();

            List<Meaning> meaning = null;
            try{
                meaning = dbHelper.getMeaning(params[0]);
            }catch (Exception e){
                e.printStackTrace();
            }
            return meaning;
        }

        @Override
        protected void onPostExecute(List<Meaning> strings) {
            super.onPostExecute(strings);
            if(strings!=null && !strings.isEmpty()){
                meaning.setText(buildMeaningText(strings));
            }else{
                meaning.setText("No results found");
            }
        }

        private String buildMeaningText(List<Meaning> strings){

            StringBuilder builder = new StringBuilder();
            for (int i = 0; i < strings.size(); i++){
                builder.append(i+1);
                builder.append(". ");
                builder.append(strings.get(i).getMeaning());
                builder.append("\n");
            }
            return builder.toString();
        }
    }
}
