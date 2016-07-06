package bugtracker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.PixelFormat;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;

public class DebugIconView extends View {	

	private WindowManager.LayoutParams mLayoutParams;
	WindowManager.LayoutParams localLayoutParams;
	private WindowManager windowManager;
	Bitmap debug;
	int width, height;
	
	public DebugIconView(Context context, Bitmap debug,int width, int height) {
		super(context);
		this.debug = debug;
		this.width = width;
		this.height = height;		
		windowManager = (WindowManager)super.getContext().getApplicationContext().getSystemService("window");		
		localLayoutParams = new WindowManager.LayoutParams();
		this.mLayoutParams = localLayoutParams;
		this.mLayoutParams.gravity = Gravity.BOTTOM | Gravity.LEFT;
		this.mLayoutParams.width = width;
		this.mLayoutParams.height = height;
		this.mLayoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
		this.mLayoutParams.format = PixelFormat.TRANSLUCENT;
		this.mLayoutParams.type = WindowManager.LayoutParams.TYPE_PHONE;
		// -1, i5, 2006, 4888, i6	
	}
	
	 protected void onDraw(Canvas canvas) {
	        canvas.drawBitmap(debug, 0 , 0, null);
	  }



	public void dismiss(){
		this.windowManager.removeView(this);
	}

	public void show()	{
		setClickable(true);	    
		setFocusableInTouchMode(true);
		setEnabled(true);
		windowManager.addView(this, localLayoutParams);
	}
	
	public void remove(){
		windowManager.removeView(this);
	}
}