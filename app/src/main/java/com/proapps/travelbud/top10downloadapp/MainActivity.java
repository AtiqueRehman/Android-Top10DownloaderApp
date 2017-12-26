package com.proapps.travelbud.top10downloadapp;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private String feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
    private int feedLimit = 10;
    private ListView listApp;

    private String feedCachedUrl = "INVALIDATED";
    private static final String STATE_URL = "feedUrl";
    public static final String STATE_FEED = "feedLimit";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        listApp = (ListView)findViewById(R.id.listView);

        if(savedInstanceState != null)
        {
            feedUrl = savedInstanceState.getString(STATE_URL);
            feedLimit = savedInstanceState.getInt(STATE_FEED);

        }
        DownloadUrl(String.format(feedUrl,feedLimit));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.feed_menu, menu);
        if(feedLimit == 10)
        {
            menu.findItem(R.id.mnu10).setChecked(true);
        }
        else
        {
            menu.findItem(R.id.mnu20).setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int itemid = item.getItemId();


        switch(itemid)
        {
            case R.id.mnuFree:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topfreeapplications/limit=%d/xml";
                break;

            case R.id.mnuPaid:
                feedUrl ="http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/toppaidapplications/limit=%d/xml";
                break;

            case R.id.mnuSongs:
                feedUrl = "http://ax.itunes.apple.com/WebObjects/MZStoreServices.woa/ws/RSS/topsongs/limit=%d/xml";
                break;

            case R.id.mnu10:
            case R.id.mnu20:
                if(!item.isChecked())
                {
                    item.setChecked(true);
                    feedLimit = 35 - feedLimit;
                }
                break;

            case R.id.mnuRefresh:
                feedCachedUrl = "INVALIDATED";
                break;

            default:
                return super.onOptionsItemSelected(item);

        }

        DownloadUrl(String.format(feedUrl,feedLimit));
        return true;

    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(STATE_URL,feedUrl);
        outState.putInt(STATE_FEED, feedLimit);
        super.onSaveInstanceState(outState);
    }

    private void DownloadUrl(String url)
    {
        if(!feedUrl.equalsIgnoreCase(feedCachedUrl)) {
            DownloadData download = new DownloadData();
            download.execute(url);
            feedCachedUrl = feedUrl;
        }
    }
    private class DownloadData extends AsyncTask <String, Void, String>
    {
        private static final String TAG = "DownlaodData";
        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            //Log.d(TAG, "onPostExecute: received strinng "+s);
            ParseApplications parseApplications = new ParseApplications();
            parseApplications.parse(s);

           // ArrayAdapter<FeedEntry> arrayAdapter = new ArrayAdapter<FeedEntry>(
           //         MainActivity.this,R.layout.list_item,parseApplications.getApplications());

            //            listApp.setAdapter(arrayAdapter);

            FeedAdapter feedAdapter = new FeedAdapter(MainActivity.this, R.layout.list_record,
                    parseApplications.getApplications());
            listApp.setAdapter(feedAdapter);
        }

        @Override
        protected String doInBackground(String... strings) {
           // Log.d(TAG, "doInBackground: do in background: starts with "+strings[0]);
            
            String rssFeed = downloadXML(strings[0]);
            if(rssFeed == null)
            {
                Log.e(TAG, "doInBackground: error downloading");
            }
            return rssFeed;
        }

        private String downloadXML(String urlString)
        {
            StringBuilder stringBuilder= new StringBuilder();

            try
            {
                URL url = new URL(urlString);
                HttpURLConnection conection = (HttpURLConnection) url.openConnection();
                int response = conection.getResponseCode();
              //  Log.d(TAG, "downloadXML: The response code was "+response);
                InputStream inputStream = conection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

                int count = 0;
                char[] buffer = new char[500];

                while(true)
                {
                    count = bufferedReader.read(buffer);
                    if(count <0)
                    {
                        break;
                    }
                    else if(count >0)
                    {
                        stringBuilder.append(String.copyValueOf(buffer,0,count));
                    }
                }

                bufferedReader.close();

                return stringBuilder.toString();
            }
            catch (MalformedURLException exp)
            {
                Log.e(TAG, "downloadXML: Invalid URL " +exp.getMessage() );
            }
            catch (IOException exp)
            {
                Log.e(TAG, "downloadXML: IO Exception reading data " + exp.getMessage() );
            }
            catch (SecurityException exp)
            {
                Log.e(TAG, "downloadXML: Security Exception permission denied "+exp.getMessage());
                exp.printStackTrace();
            }

            return null;
        }
    }
}
