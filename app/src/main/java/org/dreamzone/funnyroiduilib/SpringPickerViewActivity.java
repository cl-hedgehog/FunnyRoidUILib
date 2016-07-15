package org.dreamzone.funnyroiduilib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.widget.Toast;

import org.dreamzone.funnyroidui.springpicker.SpringPickerView;


public class SpringPickerViewActivity extends FragmentActivity {
    private SpringPickerView springPickerView;

    public static void start(Context context) {
        Intent intent = new Intent(context, SpringPickerViewActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_spring_picker);
        springPickerView = (SpringPickerView) findViewById(R.id.sp_picker);
        springPickerView.setOnLevelPickedListener(new SpringPickerView.OnLevelPickedListener() {
            @Override
            public void onLevelPicked(SpringPickerView.PickerLevel pickerLevel) {
                switch (pickerLevel) {
                    case LEVEL_ZERO:
                        Toast.makeText(SpringPickerViewActivity.this, "ZERO", Toast.LENGTH_SHORT).show();
                        break;
                    case LEVEL_ONE:
                        Toast.makeText(SpringPickerViewActivity.this, "ONE", Toast.LENGTH_SHORT).show();
                        break;
                    case LEVEL_TWO:
                        Toast.makeText(SpringPickerViewActivity.this, "TWO", Toast.LENGTH_SHORT).show();
                        break;
                    case LEVEL_THREE:
                        Toast.makeText(SpringPickerViewActivity.this, "THREE", Toast.LENGTH_SHORT).show();
                        break;
                }
            }

            @Override
            public void onPickerStatusChanged(SpringPickerView.PickerStatus pickerStatus) {

            }
        });
    }

}
