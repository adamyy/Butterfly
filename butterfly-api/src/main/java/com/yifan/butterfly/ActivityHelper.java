package com.yifan.butterfly;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.support.v4.app.ActivityOptionsCompat;

public abstract class ActivityHelper {

    protected Bundle _options;
    protected Intent _intent;

    protected int _enterAnim = 0;
    protected int _exitAnim = 0;

    public ActivityHelper() {
        _intent = new Intent();
        _options = new Bundle();
    }

    public ActivityHelper withFlags(int flags){
        _intent.setFlags(flags);
        return this;
    }

    public ActivityHelper withAnim(@AnimRes int enterAnim, @AnimRes int exitAnim){
        _enterAnim = enterAnim;
        _exitAnim = exitAnim;
        return this;
    }

    public void start(Context context){
        _options.putAll(ActivityOptionsCompat.makeCustomAnimation(context, _enterAnim, _exitAnim).toBundle());
    }

    public void startForResult(Activity activity, int requestCode){
        _options.putAll(ActivityOptionsCompat.makeCustomAnimation(activity, _enterAnim, _exitAnim).toBundle());
    }

}
