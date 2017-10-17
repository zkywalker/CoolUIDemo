package org.zky.tool.cooluidemo.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AnimationSet;
import android.view.animation.OvershootInterpolator;

import org.zky.tool.cooluidemo.R;

/**
 * 点赞
 * Created by zhangkun on 2017/10/16.
 */

public class PraiseView extends View {

    private OnPraiseListener listener;

    private Context mContext;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mDrawPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Paint mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    //用来绘制渐隐的画笔
    private Paint mTextPaint2 = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Bitmap mPraiseRed;

    private Bitmap mPraiseGray;

    private Bitmap mAttention;

    private ObjectAnimator animator = ObjectAnimator.ofFloat(this, "progress", 100, 0);

    //文本的动画的差值器和图标的不能一样
    private ObjectAnimator animator2 = ObjectAnimator.ofFloat(this, "textProgress", 100, 0);

    private AnimatorSet animationSet;

    private Bitmap mIcon;

    private Point startPoint;

    private Rect rect0;

    private Rect rect1;

    private Rect rect2;

    private Rect rect3;

    private Rect textRect = new Rect();

    private int mIconHeight;

    private int mIconWidth;

    private float progress = 0;

    private float textProgress = 100;

    private int color = 0xffff0000;

    private boolean mCurrentButtonState = false;

    private int touchState;

    private int mNum;
    private int oldNum;


    public PraiseView(Context context) {
        this(context, null);
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PraiseView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mContext = context;
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.praise_view);
        int textColor = typedArray.getColor(R.styleable.praise_view_textColor, Color.GRAY);
        float textSize = typedArray.getDimension(R.styleable.praise_view_textSize, sp2px(14));

        typedArray.recycle();

        mPraiseGray = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_praise_gray);
        mPraiseRed = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_praise_red);
        mAttention = BitmapFactory.decodeResource(context.getResources(), R.drawable.ic_attention);

        mIcon = mPraiseGray;
        mIconHeight = mIcon.getHeight();
        mIconWidth = mIcon.getWidth();

        startPoint = new Point((int) (dp2px(2) + 0.5 * mIconWidth), dp2px(2) + mAttention.getHeight() / 2);

        rect0 = new Rect(0, 0, mIconWidth, mIconHeight);
        rect1 = new Rect(startPoint.x, startPoint.y, mIconWidth + startPoint.x, mIconHeight + startPoint.y);
        rect2 = new Rect(0, 0, mAttention.getWidth(), mAttention.getHeight());
        rect3 = new Rect(startPoint.x, startPoint.y - mAttention.getHeight() / 2, mAttention.getWidth() + startPoint.x, mAttention.getHeight() / 2 + startPoint.y);

        mDrawPaint.setStyle(Paint.Style.STROKE);
        mDrawPaint.setStrokeWidth(dp2px(2));

        mTextPaint.setColor(textColor);
        mTextPaint.setTextSize(textSize);
        mTextPaint2.setColor(textColor);
        mTextPaint2.setTextSize(textSize);

        OvershootInterpolator value = new OvershootInterpolator(0.4f);
        animator.setInterpolator(value);
        animator.setDuration(400);

        animator2.setDuration(400);

        animationSet = new AnimatorSet();
        animationSet.playTogether(animator, animator2);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        //以图标为基准的总高度
        float height1 = mIconHeight + mAttention.getHeight() + dp2px(2) * 2;
        //以文字为基准的总高度
        float height2 = mTextPaint.getTextSize() * 3 + dp2px(2) * 2;

        mTextPaint.getTextBounds(Integer.toString(getNum()) + " ", 0, Integer.toString(getNum()).length(), textRect);
        int width = (int) (mIconWidth * 2 + dp2px(5) + textRect.right + dp2px(2) * 2);
        setMeasuredDimension(width, (int) (height1 > height2 ? height1 : height2));
    }

    /**
     * 我们只关心点击事件
     * 按下的时候图标变小
     * 松开的时候开始动画
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchState = MotionEvent.ACTION_DOWN;
                if (!animator.isRunning())
                    down();
                return true;
            case MotionEvent.ACTION_MOVE:
                //不需要处理移动的操作
                return true;
            case MotionEvent.ACTION_UP:
                touchState = MotionEvent.ACTION_UP;
                if (!animator.isRunning())
                    up();
                break;
        }
        return super.onTouchEvent(event);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawNum(canvas);

        //绘制按钮
        canvas.save();
        mPaint.setAlpha((int) (255 * (float) (1 - 0.6 * progress / 100)));
        canvas.scale((float) (1 - 0.3 * progress / 100), (float) (1 - 0.3 * progress / 100), mIconWidth / 2 + startPoint.x, mIconWidth / 2 + startPoint.y);
        canvas.drawBitmap(mIcon, rect0, rect1, mPaint);
        canvas.restore();

        //非按下的时候才开始动画绘制
        if (touchState != MotionEvent.ACTION_DOWN) {
            if (mCurrentButtonState) {
                //绘制圆圈
                canvas.save();
                mDrawPaint.setColor(color);
                mDrawPaint.setAlpha((int) (255 * 0.4f * (progress / 100)));
                canvas.drawCircle(mIconWidth / 2 + startPoint.x, mIconWidth / 2 + startPoint.y, mIconWidth / 2 * (1.5f - 0.7f * progress / 100), mDrawPaint);
                canvas.restore();

                //绘制短线
                if (progress <= 30) {
                    canvas.save();
                    mPaint.setAlpha(255);
                    canvas.scale(1.5f - progress / 60, 1.5f - progress / 60, mAttention.getWidth() / 2 + startPoint.x, startPoint.y);
                    canvas.drawBitmap(mAttention, rect2, rect3, mPaint);
                    canvas.restore();
                }
            }

        }


//        Log.d("draw:", "progress=" + progress);

    }

    private void drawNum(Canvas canvas) {
        char[] old = Integer.toString(oldNum).toCharArray();
        char[] now = Integer.toString(mNum).toCharArray();

        Rect rect = new Rect();
        int temp = 0;

        if (old.length > now.length) {
            //减法，减少了一位数，也就是所有的数位都要变

        } else if (old.length < now.length) {
            //加法,进位了，也就是所有的数位都要变

        } else {
            //只有两个数位变化的情况
            for (int i = 0; i < now.length; i++) {
                char c = now[i];
                String s = Character.toString(c);
                temp = temp + rect.right;

                canvas.save();
                if (c != old[i]) {
                    canvas.save();

                    if (mCurrentButtonState) {
                        canvas.translate(0, -mTextPaint.getTextSize() * (1 - textProgress / 100));
                        mTextPaint2.setAlpha((int) (255 * textProgress / 100));
                        canvas.drawText(Character.toString(old[i]), (float) (mIconWidth * 1.5 + dp2px(5) + dp2px(2)) + temp, getHeight() / 2 + (textRect.bottom - textRect.top) / 2, mTextPaint2);
                        canvas.restore();

                        //加法操作,(新数)从下往上淡入
                        canvas.save();
                        canvas.translate(0, mTextPaint.getTextSize() * textProgress / 100);

                    } else {
                        canvas.translate(0, mTextPaint.getTextSize() * (1 - textProgress / 100));
                        if (textProgress != 0)
                            canvas.drawText(Character.toString(old[i]), (float) (mIconWidth * 1.5 + dp2px(5) + dp2px(2)) + temp, getHeight() / 2 + (textRect.bottom - textRect.top) / 2, mTextPaint);
                        canvas.restore();

                        //减法操作,(新数)从上往下淡入
                        canvas.save();
                        canvas.translate(0, -mTextPaint.getTextSize() * textProgress / 100);
                    }
                }

                canvas.drawText(s, (float) (mIconWidth * 1.5 + dp2px(5) + dp2px(2)) + temp, getHeight() / 2 + (textRect.bottom - textRect.top) / 2, mTextPaint);
                canvas.restore();


                mTextPaint.getTextBounds(s, 0, 1, rect);
//                temp = (int) (temp + mTextPaint.getLetterSpacing());
            }
        }


    }

    private void down() {
        if (mCurrentButtonState) {
            mIcon = mPraiseRed;
        } else
            mIcon = mPraiseGray;

        mIcon = mPraiseGray;
        setProgress(100);

    }

    private void up() {
        if (mCurrentButtonState) {
            mIcon = mPraiseGray;
        } else
            mIcon = mPraiseRed;

        animationSet.start();

        mCurrentButtonState = !mCurrentButtonState;
        if (mCurrentButtonState)
            addOne();
        else
            subOne();
        requestLayout();
        if (listener != null)
            listener.onPraise(mCurrentButtonState, getNum());
    }

    private void subOne() {
        oldNum = mNum;
        if (mNum > 0)
            mNum = mNum - 1;
    }

    private void addOne() {
        oldNum = mNum;
        mNum = mNum + 1;
    }

    public OnPraiseListener getListener() {
        return listener;
    }

    public void setListener(OnPraiseListener listener) {
        this.listener = listener;
    }

    public int getNum() {
        return mNum;
    }

    public void setNum(int num) {
        mNum = num;
        oldNum = mNum;
        invalidate();
        mCurrentButtonState = false;
    }

    public int getColor() {
        return color;
    }

    public void setColor(int color) {
        mPaint.setColor(color);
        this.color = color;
        invalidate();
    }

    public float getProgress() {
        return progress;
    }

    public void setProgress(float progress) {
        this.progress = progress;
//        rect2 = new Rect(0, 0, (int) (mIconWidth * progress / 100), (int) (mIconHeight * progress / 100));
//        rect1 = new Rect(0, 0, (int) (mIconWidth * progress / 100), (int) (mIconHeight * progress / 100));
        invalidate();
    }

    public float getTextProgress() {
        return textProgress;
    }

    public void setTextProgress(float textProgress) {
        this.textProgress = textProgress;
        invalidate();
    }

    private int dp2px(float dp) {
        float scale = getContext().getResources().getDisplayMetrics().density;
        return (int) (dp * scale + 0.5f);
    }

    private int sp2px(float sp) {
        float fontScale = getContext().getResources().getDisplayMetrics().scaledDensity;
        return (int) (sp * fontScale + 0.5f);
    }

    public interface OnPraiseListener {

        public void onPraise(Boolean b, int num);

    }
}
