package com.yifan.butterfly;

import android.app.Activity;
import android.content.Intent;

public abstract class Binder <T extends Activity> {

    protected Intent _intent;

    public Binder(T target){
        bind(target);
    }

    public void bind(T activity) {
        _intent = activity.getIntent();
        if (_intent == null) throw new IllegalStateException("Intent to this activity cannot be found.");
    }

}
