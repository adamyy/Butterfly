package com.yifan.butterflyproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.widget.EditText;

import com.yifan.butterfly.BActivity;
import com.yifan.butterfly.BExtra;
import com.yifan.butterfly.Butterfly;
import com.yifan.butterflyproject.entity.ParcelableObject;
import com.yifan.butterflyproject.entity.SerializableObject;

import butterknife.BindView;
import butterknife.ButterKnife;

@BActivity(hasResult = true)
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

    @BExtra(alias = "id")
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

    @BExtra(alias = "name")
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
    TextInputEditText stringText;

    @BindView(R.id.int_extra)
    TextInputEditText intText;

    @BindView(R.id.activity_extra_result)
    EditText resultInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_extra);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        ButterKnife.bind(this);
        Butterfly.bind(this);

        stringText.setText(_stringExtra);
        intText.setText(String.valueOf(_intExtra));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onBackPressed() {
        if (resultInput.getText() != null) {
            Log.d(TAG, resultInput.getText().toString());
            setResult(RESULT_OK, new Intent().putExtra("result", resultInput.getText().toString()));
            finish();
        }
        super.onBackPressed();
    }
}
