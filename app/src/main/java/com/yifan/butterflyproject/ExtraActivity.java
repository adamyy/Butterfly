package com.yifan.butterflyproject;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

import com.yifan.butterfly.BActivity;
import com.yifan.butterfly.BExtra;
import com.yifan.butterfly.Butterfly;
import com.yifan.butterflyproject.entity.ParcelableObject;
import com.yifan.butterflyproject.entity.SerializableObject;

import butterknife.BindView;
import butterknife.ButterKnife;

@BActivity
public class ExtraActivity extends AppCompatActivity {

    private static final String TAG = ExtraActivity.class.getSimpleName();

    @BExtra
    byte _byteExtra;
    @BExtra
    byte[] _byteArrayExtra;

    @BExtra
    boolean _booleanExtra;
    @BExtra
    boolean[] _booleanArrayExtra;

    @BExtra
    char _charExtra;
    @BExtra
    char[] _charArrayExtra;

    @BExtra
    short _shortExtra;
    @BExtra
    short[] _shortArrayExtra;

    @BExtra
    int _intExtra;
    @BExtra
    int[] _intArrayExtra;

    @BExtra
    float _floatExtra;
    @BExtra
    float[] _floatArrayExtra;

    @BExtra
    double _doubleExtra;
    @BExtra
    double[] _doubleArrayExtra;

    @BExtra
    long _longExtra;
    @BExtra
    long[] _longArrayExtra;

    @BExtra
    String _stringExtra;
    @BExtra
    String[] _stringArrayExtra;

    @BExtra
    SerializableObject _serializableExtra;

    @BExtra
    ParcelableObject _parcelableExtra;
    @BExtra
    ParcelableObject[] _parcelableArrayExtra;

    @BindView(R.id.string_extra)
    TextView stringText;

    @BindView(R.id.int_extra_text_view)
    TextView intText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });


        ButterKnife.bind(this);
        Butterfly.bind(this);

        stringText.setText(_stringExtra);
        intText.setText(String.valueOf(_intExtra));
    }

}
