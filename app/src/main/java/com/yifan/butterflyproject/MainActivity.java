package com.yifan.butterflyproject;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;

import com.yifan.butterfly.BActivity;
import com.yifan.butterfly.Butterfly;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnTextChanged;

@BActivity()
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.sample_string_extra)
    EditText stringExtra;

    @BindView(R.id.sample_int_extra)
    EditText intExtra;

    @BindView(R.id.extra_activity_button)
    Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnTextChanged(R.id.sample_int_extra)
    public void onTextChanged(CharSequence text) {
        button.setEnabled(!text.toString().isEmpty());
    }

    @OnClick(R.id.extra_activity_button)
    public void launchExtraActivity() {
        Butterfly.getExtraActivity$$Helper(this)
                .with_intExtra(Integer.parseInt(intExtra.getText().toString()))
                .with_stringExtra(stringExtra.getText().toString())
                .withAnim(R.anim.activity_slide_left_in, R.anim.activity_slide_left_out)
                .withFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                .start();
    }

}
