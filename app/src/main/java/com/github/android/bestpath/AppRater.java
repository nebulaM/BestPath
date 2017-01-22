/*
 * Copyright (C) 2017 by nebulaM <nebulam12@gmail.com>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */

package com.github.android.bestpath;

import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;

// modified from http://stackoverflow.com/questions/14514579/how-to-implement-rate-it-feature-in-android-app

public class AppRater {
    private final static int DAYS_UNTIL_PROMPT = 2;//Min number of days
    private final static int LAUNCHES_UNTIL_PROMPT = 3;//Min number of launches

    private final static String SP_FILE_NAME="app_rater";
    private final static String SP_KEY_FIRST_LAUNCH="date_first_launch";
    private final static String SP_KEY_LAUNCH_COUNT="launch_count";
    private final static String SP_KEY_REFUSED_COUNT="refused_count";

    private final static String SP_KEY_NEVER_SHOW_AGAIN="don't_show_again";

    private final static int mTextColor=0xff41bbe7;
    public static void appLaunched(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(SP_FILE_NAME, Context.MODE_PRIVATE);
        //prefs.edit().putBoolean(SP_KEY_NEVER_SHOW_AGAIN, false).apply();
        if (prefs.getBoolean(SP_KEY_NEVER_SHOW_AGAIN, false)) { return ; }

        SharedPreferences.Editor editor = prefs.edit();

        // Increment launch counter
        long launchCount = prefs.getLong(SP_KEY_LAUNCH_COUNT, 0) + 1;
        editor.putLong(SP_KEY_LAUNCH_COUNT, launchCount);

        long refusedCount=prefs.getLong(SP_KEY_REFUSED_COUNT,0);
        //max delay in day until prompt is DAYS_UNTIL_PROMPT*(1+refusedCount)
        if(refusedCount>4){
            refusedCount=4;
        }
        // Get date of first launch
        Long dateFirstLaunch = prefs.getLong(SP_KEY_FIRST_LAUNCH, 0);
        if (dateFirstLaunch == 0) {
            dateFirstLaunch = System.currentTimeMillis();
            editor.putLong(SP_KEY_FIRST_LAUNCH, dateFirstLaunch);
        }

        editor.apply();

        // Wait at least LAUNCHES_UNTIL_PROMPT days before opening
        if (launchCount >= LAUNCHES_UNTIL_PROMPT) {
            if (System.currentTimeMillis() >= dateFirstLaunch +
                    (DAYS_UNTIL_PROMPT * 24 * 60 * 60 * 1000)*(1+refusedCount)) {
                showRateDialog(context, editor,refusedCount);
            }
        }
    }

    private static void showRateDialog(final Context context,final SharedPreferences.Editor editor,final long refusedCount){

            AlertDialog.Builder builder = new AlertDialog.Builder(context);
            final AlertDialog dialog =builder.setTitle(R.string.rate_app_dialog_title)
                    .setMessage(R.string.rate_app_dialog_text)
                    .setNeutralButton(R.string.rate_app_no, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (editor != null) {
                                editor.putBoolean(SP_KEY_NEVER_SHOW_AGAIN, true).apply();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setNegativeButton(R.string.rate_app_later, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            if (editor != null) {
                                editor.putLong(SP_KEY_LAUNCH_COUNT,0);
                                if(refusedCount<4) {
                                    editor.putLong(SP_KEY_REFUSED_COUNT, refusedCount + 1);
                                }
                                editor.apply();
                            }
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton(R.string.rate_app_yes, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            //http://stackoverflow.com/questions/10816757/rate-this-app-link-in-google-play-store-app-on-the-phone
                            Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
                            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
                            try {
                                context.startActivity(goToMarket);
                            } catch (ActivityNotFoundException e) {
                                context.startActivity(new Intent(Intent.ACTION_VIEW,
                                        Uri.parse("http://play.google.com/store/apps/details?id=" + context.getPackageName())));
                            }
                            if (editor != null) {
                                editor.putBoolean(SP_KEY_NEVER_SHOW_AGAIN, true).apply();
                            }
                            dialog.dismiss();
                        }
                    }).create();
            dialog.setOnShowListener( new DialogInterface.OnShowListener() {
                                      @Override
                                      public void onShow(DialogInterface arg0) {
                                          dialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(mTextColor);
                                          dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(mTextColor);
                                          dialog.getButton(AlertDialog.BUTTON_NEUTRAL).setTextColor(mTextColor);
                                      }
                                  });
            dialog.show();
        }

}