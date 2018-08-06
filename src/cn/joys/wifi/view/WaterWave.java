/**
 * 
 */
package cn.joys.wifi.view;

import cn.joys.wifi.R;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.os.Handler;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ProgressBar;

/**
 * 
 * 
 * @category View必须是正方形
 * 
 */
public class WaterWave extends View {

	private Context mContext;

	private Paint mCirclePaint;
	private Paint mWavePaint;

	private int mRingSTROKEWidth = 15;
	private int mCircleSTROKEWidth = 2;
	private int mLineSTROKEWidth = 1;

	private int mCircleColor = Color.WHITE;
	private int mRingColor = Color.WHITE;
	private int mWaveColor;

	private Handler mHandler;
	private long c = 0L;
	private boolean mStarted = false;
	private final float f = 0.033F;
	private int mAlpha = 50;// 透明度
	private float mAmplitude = 10.0F; // 振幅
	private float mWateLevel = 0.5F;// 水高(0~1)
	private Path mPath;

	private String flowNum = "1024M";
	private String flowLeft = "还剩余";

	/**
	 * @param context
	 */
	public WaterWave(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		mContext = context;
		init(mContext);
	}

	/**
	 * @param context
	 * @param attrs
	 */
	public WaterWave(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		mContext = context;
		init(mContext);
	}

	/**
	 * @param context
	 * @param attrs
	 * @param defStyleAttr
	 */
	public WaterWave(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		// TODO Auto-generated constructor stub
		mContext = context;
		init(mContext);
	}

	private void init(Context context) {
		mCircleColor = context.getResources().getColor(R.color.gprs);
		mWaveColor = context.getResources().getColor(R.color.round);// context.getResources().getColor(R.color.progress);R.color.round
		mCirclePaint = new Paint();
		mCirclePaint.setColor(mCircleColor);
		mCirclePaint.setStyle(Paint.Style.STROKE);
		mCirclePaint.setAntiAlias(true);
		mCirclePaint.setStrokeWidth(mCircleSTROKEWidth);

		mWavePaint = new Paint();
		mWavePaint.setStrokeWidth(1.0F);
		mWavePaint.setColor(mWaveColor);
		// mWavePaint.setAlpha(mAlpha);
		mPath = new Path();

		mHandler = new Handler() {
			@Override
			public void handleMessage(android.os.Message msg) {
				if (msg.what == 0) {
					invalidate();
					if (mStarted) {
						// 不断发消息给自己，使自己不断被重绘
						mHandler.sendEmptyMessageDelayed(0, 60L);
					}
				}
			}
		};
	}

	@Override
	protected void onDraw(Canvas canvas) {
		// TODO Auto-generated method stub
		super.onDraw(canvas);
		// 得到控件的宽高
		int width = getWidth();
		int height = getHeight();

		int radius = (int) (width / 2 - 5 / 2); // 圆环的半径

		canvas.drawCircle(width / 2, width / 2, radius - 5, mCirclePaint);

		// 如果未开始（未调用startWave方法）,绘制一个扇形
		if ((!mStarted) || (width == 0) || (height == 0)) {

			// 设置个新的长方形，扫描测量//height / 2 - height / 2 / 2
			RectF oval = new RectF(width / 4 + mRingSTROKEWidth / 2 - width / 4
					+ 5, height / 2 - height / 2 / 2 - 30, width - 8 - 5,
					height - 8);// 设置个新的长方形，扫描测量

			canvas.drawArc(oval, 0, 180, true, mWavePaint);
			return;
		}
		// 绘制,即水面静止时的高度

		RectF oval = new RectF(
				width / 4 + mRingSTROKEWidth / 2 - width / 4 + 5, height / 2
						- height / 2 / 2 - 30, width - 8 - 5, height - 8);// 设置个新的长方形，扫描测量
		canvas.drawArc(oval, 0, 180, true, mWavePaint);

		// RectF oval2 = new RectF(width / 4 + mRingSTROKEWidth / 2 - width / 4,
		// height / 2 - height / 2 / 2 - 80, width - 8, height - 5);//
		// 设置个新的长方形，扫描测量
		// canvas.drawArc(oval2, 0, 180, true, mWavePaint);

		if (this.c >= 8388607L) {
			this.c = 0L;
		}
		// 每次onDraw时c都会自增
		c = (1L + c);
		float f1 = height * (1.0F - mWateLevel);
		int top = (int) (f1 + mAmplitude);
		mPath.reset();
		int startX = width / 4 + mRingSTROKEWidth / 2 - width / 4 + 5;
		// 波浪效果
		while (startX < width - 12) {// width / 4 - mRingSTROKEWidth / 2 + width
										// /4
			int startY = (int) (f1 - mAmplitude
					* Math.sin(Math.PI
							* (2.0F * (startX + this.c * width * this.f))
							/ width)) + 25;
			canvas.drawLine(startX, startY, startX, top + 25, mWavePaint);
			startX++;
		}
		// canvas.restore();
	}

	@Override
	public Parcelable onSaveInstanceState() {
		// Force our ancestor class to save its state
		Parcelable superState = super.onSaveInstanceState();
		SavedState ss = new SavedState(superState);
		ss.progress = (int) c;
		return ss;
	}

	@Override
	public void onRestoreInstanceState(Parcelable state) {
		SavedState ss = (SavedState) state;
		super.onRestoreInstanceState(ss.getSuperState());
		c = ss.progress;
	}

	// @Override
	// protected void onAttachedToWindow() {
	// super.onAttachedToWindow();
	// // 关闭硬件加速，防止异常unsupported operation exception
	// this.setLayerType(View.LAYER_TYPE_SOFTWARE, null);
	// }

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
	}

	/**
	 * @category 开始波动
	 */
	public void startWave() {
		if (!mStarted) {
			this.c = 0L;
			mStarted = true;
			this.mHandler.sendEmptyMessage(0);
		}
	}

	/**
	 * @category 停止波动
	 */
	public void stopWave() {
		if (mStarted) {
			this.c = 0L;
			mStarted = false;
			this.mHandler.removeMessages(0);
		}
	}

	/**
	 * @category 保存状态
	 */
	static class SavedState extends BaseSavedState {
		int progress;

		/**
		 * Constructor called from {@link ProgressBar#onSaveInstanceState()}
		 */
		SavedState(Parcelable superState) {
			super(superState);
		}

		/**
		 * Constructor called from {@link #CREATOR}
		 */
		private SavedState(Parcel in) {
			super(in);
			progress = in.readInt();
		}

		@Override
		public void writeToParcel(Parcel out, int flags) {
			super.writeToParcel(out, flags);
			out.writeInt(progress);
		}

		public static final Parcelable.Creator<SavedState> CREATOR = new Parcelable.Creator<SavedState>() {
			public SavedState createFromParcel(Parcel in) {
				return new SavedState(in);
			}

			public SavedState[] newArray(int size) {
				return new SavedState[size];
			}
		};
	}

}
