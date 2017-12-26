package com.proapps.travelbud.top10downloadapp;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.StringReader;
import java.util.ArrayList;

/**
 * Created by Atique on 12/20/2017.
 */

public class ParseApplications {
    private static final String TAG = "ParseApplications";

    private ArrayList<FeedEntry> applications;

    public ParseApplications() {

        applications =new ArrayList<>();
    }

    public ArrayList<FeedEntry> getApplications() {
        return applications;
    }

    public boolean parse(String xmlData)
    {
        boolean status =true;
        FeedEntry currentEntry = null;
        boolean inEntry = false;
        String textValue = "";

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlData));
            int eventType = xpp.getEventType();
            while(eventType != XmlPullParser.END_DOCUMENT)
            {
                String nameTag = xpp.getName();
                switch (eventType)
                {
                    case XmlPullParser.START_TAG:
                       // Log.d(TAG, "parse: Starting tag for "+nameTag);
                        if("entry".equalsIgnoreCase(nameTag))
                        {
                            inEntry = true;
                            currentEntry = new FeedEntry();
                        }
                        break;

                    case XmlPullParser.TEXT:
                        textValue = xpp.getText();
                        break;

                    case XmlPullParser.END_TAG:
                       // Log.d(TAG, "parse: End tag for "+nameTag);
                        if(inEntry) {
                            if ("entry".equalsIgnoreCase(nameTag)) {
                                applications.add(currentEntry);
                                inEntry = false;
                            } else if ("name".equalsIgnoreCase(nameTag)) {
                                currentEntry.setName(textValue);
                            } else if ("artist".equalsIgnoreCase(nameTag)) {
                                currentEntry.setArtist(textValue);
                            } else if ("releaseDate".equalsIgnoreCase(nameTag)) {
                                currentEntry.setReleaseDate(textValue);
                            } else if ("summary".equalsIgnoreCase(nameTag)) {
                                currentEntry.setSummary(textValue);
                            } else if ("image".equalsIgnoreCase(nameTag)) {
                                currentEntry.setImageURL(textValue);
                            }
                        }
                        break;
                    default:
                        ///nothing to do
                }

                eventType = xpp.next();
            }
         //   for(FeedEntry  app: applications)
         //   {
               // Log.d(TAG, "***********************");
               // Log.d(TAG,app.toString());
           // }

        }catch (Exception exp)
        {
            status = false;
            exp.printStackTrace();
        }

        return status;
    }
}
