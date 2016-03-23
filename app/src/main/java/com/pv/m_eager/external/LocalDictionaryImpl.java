package com.pv.m_eager.external;

import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.pv.m_eager.Dictionary;
import com.pv.m_eager.MeagerDbHelper;
import com.pv.m_eager.R;
import com.pv.m_eager.model.Meaning;

import java.util.List;

/**
 * @author p-v
 */
public class LocalDictionaryImpl implements Dictionary{

    private TextView meaningContainer;
    private Context context;

    public LocalDictionaryImpl(Context context){
        this.context = context;
    }

    @Override
    public View getView() {
        meaningContainer = (TextView)LayoutInflater.from(context).inflate(R.layout.localdb_layout,null);
        return meaningContainer;
    }

    @Override
    public void onLoad(String word) {
        GetMeaningAsyncTask meaningAsyncTask = new GetMeaningAsyncTask();
        meaningAsyncTask.execute(word);
    }

    private class GetMeaningAsyncTask extends AsyncTask<String,String,List<Meaning>> {

        @Override
        protected List<Meaning> doInBackground(String[] params) {
            MeagerDbHelper dbHelper = new MeagerDbHelper(context);
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

            if(meaningContainer==null) return;

            if(strings!=null && !strings.isEmpty()){
                meaningContainer.setText(buildMeaningText(strings));
            }else{
                meaningContainer.setText("No results found");
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
