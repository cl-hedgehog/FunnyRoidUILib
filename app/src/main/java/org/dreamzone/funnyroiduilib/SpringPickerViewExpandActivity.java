package org.dreamzone.funnyroiduilib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.dreamzone.funnyroidui.springpicker.PickerView;
import org.dreamzone.funnyroidui.springpicker.SpringPickerViewExpand;

/**
 * @ClassName: SpringPickerViewExpandActivity
 * @Description: 利用自定义PickerView的SpringPickerView的Demo
 * @author bohe
 * @date 2016/7/15 10:20
 */
public class SpringPickerViewExpandActivity extends FragmentActivity {
    private SpringPickerViewExpand springPickerView;
    private PickerView pickerView;
    private final String TAG = SpringPickerViewExpandActivity.class.getSimpleName();

    private Button btnDilate;
    private Button btnShrink;

    public static void start(Context context) {
        Intent intent = new Intent(context, SpringPickerViewExpandActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_picker_expand);
        springPickerView = (SpringPickerViewExpand) findViewById(R.id.sp_picker);
        pickerView = (PickerView) findViewById(R.id.picker_view);

        btnDilate = (Button) findViewById(R.id.btn_dilate);
        btnShrink = (Button) findViewById(R.id.btn_shink);

        pickerView.setStyleType(1);
        btnDilate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                springPickerView.setLevel(2, false);
            }
        });
        btnShrink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                springPickerView.setLevel(3, true);
            }
        });


        springPickerView.setOnLevelPickedListener(new SpringPickerViewExpand.OnLevelPickedListener() {
            @Override
            public void onLevelPicked(SpringPickerViewExpand.PickerLevel pickerLevel) {
                Log.e(TAG, "pickerStatus= " + pickerLevel);
                switch (pickerLevel) {
                    case LEVEL_ZERO:
                        Toast.makeText(SpringPickerViewExpandActivity.this, "ZERO", Toast.LENGTH_SHORT).show();
                        break;
                    case LEVEL_ONE:
                        Toast.makeText(SpringPickerViewExpandActivity.this, "ONE", Toast.LENGTH_SHORT).show();
                        break;
                    case LEVEL_TWO:
                        Toast.makeText(SpringPickerViewExpandActivity.this, "TWO", Toast.LENGTH_SHORT).show();
                        break;
                    case LEVEL_THREE:
                        Toast.makeText(SpringPickerViewExpandActivity.this, "THREE", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onPickerStatusChanged(SpringPickerViewExpand.PickerStatus pickerStatus) {
                Log.e(TAG, "pickerStatus= " + pickerStatus);
            }
        });
    }

}
