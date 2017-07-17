package com.yifan.butterflyproject;

import android.content.Intent;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import com.yifan.butterfly.BActivity;
import com.yifan.butterfly.Butterfly;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

@BActivity
public class MainActivity extends AppCompatActivity {

    private static final String TAG = MainActivity.class.getSimpleName();

    @BindView(R.id.sample_string_extra)
    TextInputEditText stringExtra;

    @BindView(R.id.sample_int_extra)
    TextInputEditText intExtra;

    @BindView(R.id.button_extra_activity)
    Button launch;

    @BindView(R.id.button_extra_activity_for_result)
    Button launchForResult;

    @BindView(R.id.result_extra_activity)
    TextInputEditText result;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnTextChanged(R.id.sample_int_extra)
    public void onTextChanged(CharSequence text) {
        launch.setEnabled(!text.toString().isEmpty());
        launchForResult.setEnabled(!text.toString().isEmpty());
    }

    @OnClick(R.id.button_extra_activity)
    public void launchExtraActivity() {
        Butterfly.toExtraActivity()
                .withId(Integer.parseInt(intExtra.getText().toString()))
                .withName(stringExtra.getText().toString())
                .withAnim(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out)
                .go(this);
    }

    @OnClick(R.id.button_extra_activity_for_result)
    public void launchExtraActivityForResult() {
        Butterfly.toExtraActivity()
                .withId(Integer.parseInt(intExtra.getText().toString()))
                .withName(stringExtra.getText().toString())
                .withAnim(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out)
                .goForResult(this, 123);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Log.d(TAG, requestCode + " " + resultCode);
        if (resultCode != RESULT_OK) return;
        switch (requestCode) {
            case 123:
                result.setText(data.getStringExtra("result"));
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }
}
