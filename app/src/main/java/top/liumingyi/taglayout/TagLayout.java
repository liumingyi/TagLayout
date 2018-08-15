package top.liumingyi.taglayout;

import android.content.Context;
import android.graphics.PointF;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 标签排列
 */
public class TagLayout extends ViewGroup {

  private static final String TAG = "TagLayout";

  private static final int MARGIN = (int) Utils.dpToPixel(8);
  private static final int EDIT_WIN_SIZE = (int) Utils.dpToPixel(150);

  private static final int NONE = -1;
  private static final int EDIT = -2;

  private Rect[] childrenBounds;
  private int id;

  private int currentId = NONE;

  // 编辑框的基准点
  private PointF editWindowBasePoint;
  private Rect editWindow;

  {
    editWindowBasePoint = new PointF();
    editWindow = new Rect();
  }

  public TagLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
  }

  @Override protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
    int widthUsed = 0;//使用的宽
    int heightUsed = 0; //使用的高,PS:
    int childCount = currentId == NONE ? getChildCount() : getChildCount() - 1;
    int lineHeight = 0; //每一行高度
    int lineWidth = 0;

    // 创建 子View的Bounds
    if (childrenBounds == null) {
      childrenBounds = new Rect[childCount];
    } else if (childrenBounds.length < childCount) {
      childrenBounds = Arrays.copyOf(childrenBounds, childCount);
    }

    for (int i = 0; i < childCount; i++) {
      View child = getChildAt(i);

      // 测量时候不用管 editWindow
      if ((int) child.getTag() == EDIT) {
        continue;
      }

      Rect childBounds = childrenBounds[i];

      lineWidth += MARGIN;

      // 测量子View,设置高宽的起始点
      measureChildWithMargins(child, widthMeasureSpec, lineWidth, heightMeasureSpec,
          MARGIN + heightUsed);

      // 判断换行
      if (MeasureSpec.getMode(widthMeasureSpec) != MeasureSpec.UNSPECIFIED
          && MeasureSpec.getSize(widthMeasureSpec)
          < lineWidth + child.getMeasuredWidth() + MARGIN) {

        lineWidth = MARGIN;
        heightUsed += lineHeight;
        lineHeight = 0;
        measureChildWithMargins(child, widthMeasureSpec, lineWidth, heightMeasureSpec, heightUsed);
      }

      if (childBounds == null) {
        childBounds = childrenBounds[i] = new Rect();
      }

      childBounds.set(lineWidth, MARGIN + heightUsed, lineWidth + child.getMeasuredWidth(),
          MARGIN + heightUsed + child.getMeasuredHeight());

      // 测量编辑框
      if ((int) child.getTag() == currentId) {
        editWindowBasePoint.x = (child.getRight() + child.getLeft()) * 0.5f;
        editWindowBasePoint.y = child.getTop() - Utils.dpToPixel(10);
      }

      lineWidth += child.getMeasuredWidth();
      lineHeight = Math.max(lineHeight, child.getMeasuredHeight() + MARGIN);
      widthUsed = Math.max(widthUsed, lineWidth);
    }

    // 父View的高宽,这个margin是尾巴上的。高里面已经包含了margin
    int width = widthUsed + MARGIN;
    int height = heightUsed + lineHeight + MARGIN;

    setMeasuredDimension(resolveSizeAndState(width, widthMeasureSpec, 0),
        resolveSizeAndState(height, heightMeasureSpec, 0));
  }

  @Override protected void onLayout(boolean changed, int l, int t, int r, int b) {
    for (int i = 0, length = getChildCount(); i < length; i++) {
      View child = getChildAt(i);
      Rect rect;
      Log.d(TAG, "IDDD : " + (int) child.getTag());
      if ((int) child.getTag() == EDIT) {
        editWindow.set((int) editWindowBasePoint.x - EDIT_WIN_SIZE / 2,
            (int) editWindowBasePoint.y - EDIT_WIN_SIZE,
            (int) editWindowBasePoint.x + EDIT_WIN_SIZE / 2,
            (int) editWindowBasePoint.y - (int) Utils.dpToPixel(10));
        child.layout(editWindow.left, editWindow.top, editWindow.right, editWindow.bottom);
      } else {
        rect = childrenBounds[i];
        child.layout(rect.left, rect.top, rect.right, rect.bottom);
      }
    }
  }

  private List<TagItemView> getTagItemViews(List<String> data) {
    List<TagItemView> tvList = new ArrayList<>();

    if (data == null || data.size() == 0) {
      return null;
    }

    for (String datum : data) {
      TagItemView itemView = createTextView(datum);
      tvList.add(itemView);
    }

    return tvList;
  }

  private TagItemView createTextView(String text) {
    TagItemView itemView = new TagItemView(getContext(), text);
    itemView.setTag(id++);
    Log.d(TAG, ">> ID : " + (int) itemView.getTag());
    itemView.setOnVibrationListener(new TagItemView.VibrationListener() {
      @Override public void onVibration(View v) {
        currentId = (int) v.getTag();
        // FIXME: 2018/8/15 暂时不知道怎么改
        //invalidate();
        //View editWindow = createEditWindow();
        //addView(editWindow);
        //requestLayout();
        Log.d(TAG, "== ID : " + id);
      }
    });
    return itemView;
  }

  // FIXME: 2018/8/15
  private View createEditWindow() {
    //View view = createTextView("编");
    //view.setTag(EDIT);
    //return view;
    LayoutInflater inflater = LayoutInflater.from(getContext());
    View view = inflater.inflate(R.layout.view_edit_window, this, false);
    view.setLayoutParams(
        new MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
    view.setTag(EDIT);
    return view;
  }

  public void setData(List<String> data) {
    List<TagItemView> children = getTagItemViews(data);
    for (TagItemView child : children) {
      addView(child);
    }
    requestLayout();
  }

  public void addData(String tag) {
    TagItemView itemView = createTextView(tag);
    addView(itemView);
  }

  // 动态添加，不需要进行这个设置了
  //@Override public LayoutParams generateLayoutParams(AttributeSet attrs) {
  //  return new MarginLayoutParams(getContext(), attrs);
  //}
}
