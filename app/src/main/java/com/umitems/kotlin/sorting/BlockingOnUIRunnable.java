package com.umitems.kotlin.sorting;

import android.app.Activity;

/**
 * Created by umanusorn on 12/12/2016 AD.
 */

public class BlockingOnUIRunnable {
    // Activity
    private Activity activity;

    // Event Listener
    private Runnable listener;

    // UI runnable
    private Runnable uiRunnable;

    private boolean ranUI;

    /**
     * Class initialization
     *
     * @param activity Activity
     * @param listener Event listener
     */
    public BlockingOnUIRunnable(Activity activity, Runnable listener) {
        this.activity = activity;
        this.listener = listener;

        uiRunnable = new Runnable() {
            public void run() {
                // Execute custom code
                if (BlockingOnUIRunnable.this.listener != null)
                    BlockingOnUIRunnable.this.listener.run();

                synchronized (this) {
                    ranUI = true;
                    this.notify();
                }
            }
        };
    }


    /**
     * Start runnable on UI thread and wait until finished
     */
    public void startOnUiAndWait() {
        synchronized (uiRunnable) {
            // Execute code on UI thread
            activity.runOnUiThread(uiRunnable);

            // Wait until runnable finished
            while (!ranUI) {
                try {
                    uiRunnable.wait();
                } catch (InterruptedException e) {
                }
            }
        }
    }

}