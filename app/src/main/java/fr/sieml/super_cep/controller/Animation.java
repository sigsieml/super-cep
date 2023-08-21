package fr.sieml.super_cep.controller;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

public class Animation {

    public static void pulseViewOnLongPress(final View view) {
        final ObjectAnimator scaleDown = ObjectAnimator.ofPropertyValuesHolder(view,
                PropertyValuesHolder.ofFloat("scaleX", 1.1f),
                PropertyValuesHolder.ofFloat("scaleY", 1.1f));
        scaleDown.setDuration(310);
        scaleDown.setRepeatCount(ObjectAnimator.INFINITE);
        scaleDown.setRepeatMode(ObjectAnimator.REVERSE);

        final GestureDetector gestureDetector = new GestureDetector(view.getContext(), new GestureDetector.SimpleOnGestureListener() {
            @Override
            public void onLongPress(MotionEvent e) {
                super.onLongPress(e);
                scaleDown.start();
            }
        });

        view.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
                    scaleDown.cancel();
                    view.setScaleX(1f); // reset scale to 1
                    view.setScaleY(1f); // reset scale to 1
                }
                return false; // return false here so that other listeners can also process this event
            }
        });
    }


}
