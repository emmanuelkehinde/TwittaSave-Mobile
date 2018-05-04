/*
 * Copyright (C) 2017 Emmanuel Kehinde
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kehinde.twittasave.utils;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.widget.Toast;

import com.esafirm.rxdownloader.RxDownloader;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterApiClient;
import com.twitter.sdk.android.core.TwitterCore;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.models.Tweet;
import com.twitter.sdk.android.core.services.StatusesService;

import retrofit2.Call;
import rx.Observer;


/**
 * Created by kehinde on 7/5/17.
 */

public class ServiceUtil {

    private static Context c;


    public static void fetchTweet(String copiedURL,Context context) {
        c=context;
        String fname;


        //Check if the tweet url field has text containing twitter.com/...
        if (copiedURL.length()>0 && copiedURL.contains("twitter.com/")) {

            Long id = getTweetId(copiedURL);

            //Call method to get tweet
            if (id !=null) {
                //set the tweet Id as the filename
                fname = String.valueOf(id);

                getTweet(id, fname);
            }

        }else {
            alertNoUrl();
        }
    }

    private static Long getTweetId(String s) {
        try {
            String[] split = s.split("\\/");
            String id = split[5].split("\\?")[0];
            return Long.parseLong(id);
        }catch (Exception e){
            Log.d("TAG", "getTweetId: "+e.getLocalizedMessage());
            alertNoUrl();
            return null;
        }
    }

    private static void alertNoVideo() {
        Toast.makeText(c, "The tweet contains no video or gif file", Toast.LENGTH_LONG).show();
    }

    private static void alertNoMedia() {
        Toast.makeText(c, "The tweet contains no media file", Toast.LENGTH_LONG).show();
    }

    private static void alertNoUrl() {
        Toast.makeText(c, "Not a tweet url", Toast.LENGTH_LONG).show();
    }


    private static void showFetchingTweetNoti() {
        Toast.makeText(c, "Fetching Tweet...", Toast.LENGTH_LONG).show();
    }

    private static void getTweet(final Long id, final String fname){
        showFetchingTweetNoti();

        TwitterApiClient twitterApiClient= TwitterCore.getInstance().getApiClient();
        StatusesService statusesService=twitterApiClient.getStatusesService();
        Call<Tweet> tweetCall=statusesService.show(id,null,null,null);
        tweetCall.enqueue(new Callback<Tweet>() {
            @Override
            public void success(Result<Tweet> result) {

                //Check if media is present
                if (result.data.extendedEtities==null && result.data.entities.media==null){
                    alertNoMedia();
                }
                //Check if gif or mp4 present in the file
                else if (!(result.data.extendedEtities.media.get(0).type).equals("video") && !(result.data.extendedEtities.media.get(0).type).equals("animated_gif")){
                    alertNoVideo();
                }
                else {
                    String filename=fname;
                    String url;

                    //Set filename to gif or mp4
                    if ((result.data.extendedEtities.media.get(0).type).equals("video")) {
                        filename = filename + ".mp4";
                    }else {
                        filename = filename + ".gif";
                    }

                    int i=0;
                    url = result.data.extendedEtities.media.get(0).videoInfo.variants.get(i).url;
                    while (!url.contains(".mp4")){
                        try {
                            if (result.data.extendedEtities.media.get(0).videoInfo.variants.get(i) != null) {
                                url = result.data.extendedEtities.media.get(0).videoInfo.variants.get(i).url;
                                i += 1;
                            }
                        } catch (IndexOutOfBoundsException e) {
                            downloadVideo(url,filename);
                        }
                    }

                    downloadVideo(url,filename);
                }
            }

            @Override
            public void failure(TwitterException exception) {
                Toast.makeText(c, "Request Failed: Check your internet connection", Toast.LENGTH_SHORT).show();
            }
        });
    }


    private static void downloadVideo(String url, String fname) {


        //Check if External Storage permission js allowed
        if (!storageAllowed()) {
            // We don't have permission so prompt the user
//            ActivityCompat.requestPermissions(c, Constant.PERMISSIONS_STORAGE, Constant.REQUEST_EXTERNAL_STORAGE);
            Toast.makeText(c, "Kindly grant the storage permission for TwittaSave and try again", Toast.LENGTH_SHORT).show();
        }else {
            RxDownloader.getInstance(c)
                    .download(url,fname, "video/*") // url, filename, and mimeType
                    .subscribe(new Observer<String>() {
                        @Override
                        public void onCompleted() {
                            Toast.makeText(c, "Download Completed", Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onError(Throwable e) {
                            Toast.makeText(c, e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onNext(String s) {

                        }
                    });

            Toast.makeText(c, "Download Started: Check Notification", Toast.LENGTH_LONG).show();
        }
    }

    private static boolean storageAllowed() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            int permission = ActivityCompat.checkSelfPermission(c, Manifest.permission.WRITE_EXTERNAL_STORAGE);

            return permission == PackageManager.PERMISSION_GRANTED;

        }

        return true;
    }

}
