import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.util.AttributeSet;
import android.widget.LinearLayout;

/**
 * @author bohe
 * @ClassName: CouponBgView
 * @Description:
 * @date 2016/7/11 10:46
 */
public class CouponBgView extends LinearLayout{
    private Context mContext;

    public CouponBgView(Context context) {
        super(context);
    }

    public CouponBgView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public CouponBgView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public CouponBgView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }
}
