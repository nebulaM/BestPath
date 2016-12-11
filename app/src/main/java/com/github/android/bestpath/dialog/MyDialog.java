package com.github.android.bestpath.dialog;

/**
 * Created by nebulaM on 12/10/2016.
 */

public interface MyDialog{
    interface onCloseListener{
        /**
         * Do something on dialog close
         * @param tag name of the dialog being closed
         */
        void onDialogClose(String tag, int Value);
    }
}
