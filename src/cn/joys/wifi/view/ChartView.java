package cn.joys.wifi.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;

public class ChartView extends View {
	private final float radius = 3.0F;
	private float startX;
	private float startY;
	private float stopX;
	private float stopY;

	public ChartView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ChartView(Context paramContext, float paramFloat1,
			float paramFloat2, float paramFloat3, float paramFloat4) {
		super(paramContext);
		this.startX = paramFloat1;
		this.startY = paramFloat2;
		this.stopX = paramFloat3;
		this.stopY = paramFloat4;

	}

	@Override
	protected void onDraw(Canvas canvas) {

		Paint paint = new Paint();
		paint.setAntiAlias(true);
		paint.setStrokeWidth((float) 1.0); // 设置线宽
		paint.setColor(Color.GRAY);
		canvas.drawLine(startX, startY, stopX, stopY, paint);
		canvas.drawCircle(startX, startY, 3.0F, paint);
		canvas.drawCircle(stopX, stopY, 3.0F, paint);

		super.onDraw(canvas);
	}

}
