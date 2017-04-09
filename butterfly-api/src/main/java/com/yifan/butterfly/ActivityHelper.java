package com.yifan.butterfly;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.v4.app.ActivityOptionsCompat;

public abstract class ActivityHelper {

    protected Bundle _extras;
    protected Bundle _options;
    protected Intent _intent;

    protected final Context _context;

    public ActivityHelper(Context from) {
        _context = from;
        _intent = new Intent();
        _extras = new Bundle();
        _options = new Bundle();
    }

    public ActivityHelper withFlags(int flags){
        _intent.setFlags(flags);
        return this;
    }

    public ActivityHelper withAnim(@AnimRes int enterAnim, @AnimRes int exitAnim){
        _options.putAll(ActivityOptionsCompat.makeCustomAnimation(_context, enterAnim, exitAnim).toBundle());
        return this;
    }

    public abstract void start();

    public abstract void startForResult(int requestCode);

}
