package com.yifan.butterfly.router;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by yifan on 17/2/26.
 */

public abstract class ActivityHelper {

    protected Bundle _extras;
    protected Bundle _options;
    protected Intent _intent;

    public ActivityHelper() {
        _intent = new Intent();
        _extras = new Bundle();
        _options = new Bundle();
    }

    public abstract void start(Context from);

    public abstract void start(Activity activity);

    public abstract void startForResult(Activity from, int requestCode);

}
