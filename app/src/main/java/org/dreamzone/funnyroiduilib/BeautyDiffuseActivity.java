package org.dreamzone.funnyroiduilib;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

import org.dreamzone.funnyroidui.beautydiffuseview.BeautyDiffuseView;


public class BeautyDiffuseActivity extends FragmentActivity {
    private BeautyDiffuseView diffuseView;
    private Button btnStart;

    public static void start(Context context) {
        Intent intent = new Intent(context, BeautyDiffuseActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //        SurfaceView v = new SurfaceView(this);
        //        HolderSurfaceView.getInstance().setSurfaceView(v);
        //        v.setBackgroundResource(R.drawable.img_welcome);
        //        this.setContentView(v);
        //        DrawYH yh=new DrawYH();
        //        v.setOnTouchListener(yh);
        //        yh.begin();

        setContentView(R.layout.activity_beauty_diffuse);
        btnStart = (Button) findViewById(R.id.btn_start);
        diffuseView = (BeautyDiffuseView) findViewById(R.id.view_diffuse);
        //bezierFlowerView = (BezierFlowerView) findViewById(R.id.view_diffuse);
        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                diffuseView.startAnimation();
            }
        });
    }

}
