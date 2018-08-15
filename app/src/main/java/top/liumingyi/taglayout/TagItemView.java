package top.liumingyi.taglayout;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

@SuppressLint("ViewConstructor") public class TagItemView
    extends android.support.v7.widget.AppCompatTextView {

  private static final String TAG = "TagItemView";

  private static final int PADDING = (int) Utils.dpToPixel(4);

  private Animation shake;

  private VibrationListener vibrationListener;

  private Paint paint;

  {
    paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    paint.setColor(Color.RED);
  }

  public TagItemView(Context context, String text) {
    super(context);
    init(text);
  }

  private void init(String text) {
    setText(text);
    setTextSize(Utils.dpToPixel(6));
    setBackground(ContextCompat.getDrawable(getContext(), R.drawable.tag_background));
    ViewGroup.MarginLayoutParams marginLayoutParams =
        new ViewGroup.MarginLayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT);
    setPadding(PADDING, PADDING, PADDING, PADDING);
    setLayoutParams(marginLayoutParams);
    setMaxLines(1);
    setOnLongClickListener(new OnLongClickListener() {
      @Override public boolean onLongClick(View v) {
        vibration();
        return true;
      }
    });

    setOnClickListener(new OnClickListener() {
      @Override public void onClick(View v) {
        clearAnimation();
      }
    });
  }

  /**
   * 开启抖动
   */
  private void vibration() {
    if (shake == null) {
      shake = AnimationUtils.loadAnimation(getContext(), R.anim.shake);
      shake.setAnimationListener(new Animation.AnimationListener() {
        @Override public void onAnimationStart(Animation animation) {
          if (vibrationListener != null) {
            vibrationListener.onVibration(TagItemView.this);
          }
        }

        @Override public void onAnimationEnd(Animation animation) {

        }

        @Override public void onAnimationRepeat(Animation animation) {

        }
      });
    }
    startAnimation(shake);
  }

  @Override protected void onDetachedFromWindow() {
    super.onDetachedFromWindow();
    if (getAnimation() != null) {
      Log.d(TAG, "onDetachedFromWindow: ------");
      clearAnimation();
    }
  }

  public interface VibrationListener {
    void onVibration(View v);
  }

  public void setOnVibrationListener(VibrationListener l) {
    this.vibrationListener = l;
  }
}
