package com.yifan.butterflyproject;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;

import com.yifan.butterfly.BActivity;
import com.yifan.butterfly.Butterfly;
import com.yifan.butterflyproject.entity.ParcelableObject;
import com.yifan.butterflyproject.entity.SerializableObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

@BActivity(alias = "MainActivityAlias")
public class MainActivity extends AppCompatActivity {

    @BindView(R.id.sample_string_extra)
    EditText stringExtra;

    @BindView(R.id.sample_int_extra)
    EditText intExtra;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.extra_activity_button)
    public void launchExtraActivity() {
        Butterfly.getExtraActivity$$Helper()
                .with_stringExtra(stringExtra.getText().toString())
                .with_intExtra(Integer.parseInt(intExtra.getText().toString()))
                .with_parcelableExtra(new ParcelableObject("Parcelable"))
                .with_serializableExtra(new SerializableObject("Serializable"))
                .start(this);
    }

}
