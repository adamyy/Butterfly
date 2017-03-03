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
    public byte _byteExtra;
    @BExtra
    public byte[] _byteArrayExtra;

    @BExtra
    public boolean _booleanExtra;
    @BExtra
    public boolean[] _booleanArrayExtra;

    @BExtra
    public char _charExtra;
    @BExtra
    public char[] _charArrayExtra;

    @BExtra
    public short _shortExtra;
    @BExtra
    public short[] _shortArrayExtra;

    @BExtra
    public int _intExtra;
    @BExtra
    public int[] _intArrayExtra;

    @BExtra
    public float _floatExtra;
    @BExtra
    public float[] _floatArrayExtra;

    @BExtra
    public double _doubleExtra;
    @BExtra
    public double[] _doubleArrayExtra;

    @BExtra
    public long _longExtra;
    @BExtra
    public long[] _longArrayExtra;

    @BExtra
    public String _stringExtra;
    @BExtra
    public String[] _stringArrayExtra;

    @BExtra
    public SerializableObject _serializableExtra;

    @BExtra
    public ParcelableObject _parcelableExtra;
    @BExtra
    public ParcelableObject[] _parcelableArrayExtra;

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
