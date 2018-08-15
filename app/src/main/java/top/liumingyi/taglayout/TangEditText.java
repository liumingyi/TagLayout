package top.liumingyi.taglayout;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;

/**
 * 功能需求:
 * 1.label
 * 2.label的显时机，当有输入时出现 没有输入时隐藏
 * 渐入渐出 -> 属性动画 -> 属性
 * 3.添加label从底部滑出
 * 4.xml中设置是否使用label , code 中设置能否使用 label
 * 5.自行绘制边框，以适用左边有图标的情况
 */
public class TangEditText extends android.support.v7.widget.AppCompatEditText {

  private static final String TAG = "TangEditText";

  private static final float LABEL_PADDING_TOP = Utils.dpToPixel(8);
  private static final float LABEL_SIZE = Utils.dpToPixel(12);
  private static final float LABEL_OFFSET = Utils.dpToPixel(4);
  private static final float LABEL_OFFSET_Y = Utils.dpToPixel(12);
  private static final float TOTAL_EXTRA_OFFSET = Utils.dpToPixel(16);
  private static final float LINE_OFFSET_Y = Utils.dpToPixel(8);
  private static final float EXTRA_OFFSET_X = Utils.dpToPixel(80);

  private Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

  private float labelFraction;
  private ObjectAnimator animator;
  private boolean labelShown;
  private boolean useFloatingLabel;

  private int[] originSize = new int[4];
  private int colorAccent;

  public TangEditText(Context context) {
    super(context);
  }

  public TangEditText(Context context, AttributeSet attrs) {
    super(context, attrs);
    init(context, attrs);
  }

  public TangEditText(Context context, AttributeSet attrs, int defStyleAttr) {
    super(context, attrs, defStyleAttr);
    init(context, attrs);
  }

  private void init(Context context, AttributeSet attrs) {
    TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TangEditText);
    TypedArray colorAccentTypedArray =
        context.obtainStyledAttributes(attrs, new int[] { R.attr.colorAccent });
    colorAccent = colorAccentTypedArray.getColor(0, Color.BLACK);

    useFloatingLabel = typedArray.getBoolean(R.styleable.TangEditText_useFloatLabel, true);

    typedArray.recycle();
    colorAccentTypedArray.recycle();

    setUseFloatingLabel(useFloatingLabel);
  }

  {
    setBackgroundDrawable(null);
    paint.setTextSize(LABEL_SIZE);
    originSize[0] = getPaddingLeft();
    originSize[1] = getPaddingTop();
    originSize[2] = getPaddingRight();
    originSize[3] = getPaddingBottom();

    addTextChangedListener(new TextWatcher() {
      @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {

      }

      @Override public void onTextChanged(CharSequence s, int start, int before, int count) {

      }

      @Override public void afterTextChanged(Editable s) {
        Log.d(TAG, "afterTextChanged: " + s);
        if (s.length() > 0 && !labelShown) {
          labelShown = true;
          getObjectAnimator().start();
        } else if (s.length() == 0 && labelShown) {
          labelShown = false;
          getObjectAnimator().reverse();
        }
      }
    });
  }

  private void setFloatingLabelPadding() {
    setPadding((int) (originSize[0] + EXTRA_OFFSET_X),
        (int) (originSize[1] + LABEL_PADDING_TOP + LABEL_OFFSET_Y), originSize[2], originSize[3]);
  }

  private void setOriginLabelPadding() {
    setPadding((int) (originSize[0] + EXTRA_OFFSET_X), originSize[1], originSize[2], originSize[3]);
  }

  private ObjectAnimator getObjectAnimator() {
    if (animator == null) {
      animator = ObjectAnimator.ofFloat(TangEditText.this, "labelFraction", 0, 1);
    }
    return animator;
  }

  public float getLabelFraction() {
    return labelFraction;
  }

  public void setLabelFraction(float labelFraction) {
    this.labelFraction = labelFraction;
    invalidate();
  }

  public boolean isUseFloatingLabel() {
    return useFloatingLabel;
  }

  public void setUseFloatingLabel(boolean useFloatingLabel) {
    this.useFloatingLabel = useFloatingLabel;
    if (useFloatingLabel) {
      setFloatingLabelPadding();
    } else {
      setOriginLabelPadding();
    }
    requestLayout();
  }

  @Override protected void onDraw(Canvas canvas) {
    super.onDraw(canvas);

    if (useFloatingLabel) {
      drawFloatingLabel(canvas);
    }

    drawEditFrame(canvas);
  }

  /**
   * 绘制 EditText 边框
   */
  private void drawEditFrame(Canvas canvas) {
    if (hasFocus()) {
      paint.setColor(colorAccent);
      paint.setStrokeWidth(Utils.dpToPixel(2));
    } else {
      paint.setColor(Color.BLACK);
      paint.setStrokeWidth(Utils.dpToPixel(0.75f));
    }
    canvas.drawLine(LABEL_OFFSET + EXTRA_OFFSET_X, getBottom() - LINE_OFFSET_Y,
        getWidth() - LABEL_OFFSET, getBottom() - LINE_OFFSET_Y, paint);
  }

  /**
   * 绘制 Floating Label
   */
  private void drawFloatingLabel(Canvas canvas) {
    CharSequence hint = getHint();
    if (hint == null) {
      hint = "";
    }
    int alpha = paint.getAlpha();
    paint.setAlpha((int) (0xff * labelFraction));
    float extraOffset = TOTAL_EXTRA_OFFSET * (1 - labelFraction);
    canvas.drawText(hint, 0, hint.length(), LABEL_OFFSET + EXTRA_OFFSET_X,
        LABEL_OFFSET_Y + LABEL_SIZE + extraOffset, paint);
    paint.setAlpha(alpha);
  }
}
