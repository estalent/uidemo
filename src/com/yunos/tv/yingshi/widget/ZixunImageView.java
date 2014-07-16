package com.yunos.tv.yingshi.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.yunos.tv.yingshi.R;

public class ZixunImageView extends MaskImageView {
	private Context context;
	private Paint paint;
	private float posterHeight;
	private float reflectionGapTop;
	private float reflectionHeight;
	private Bitmap comPoster;
	
	private static BitmapDrawable maskDrawable;
	
	public ZixunImageView(Context context) {
        super(context);   
        this.context= context;
        paint = getPaint();
        
    }
	
    public ZixunImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
        this.context= context;
        readCustomAttrs(context, attrs);
    	paint = getPaint();
    }

    public ZixunImageView(Context context, AttributeSet attrs, int defStyle) {
    	super(context, attrs, defStyle);
    	this.context= context;
    	readCustomAttrs(context, attrs);
    	paint = getPaint();
    }

    protected Paint getPaint() {
		if (paint == null) {
			paint = new Paint();
			paint.setDither(true);//设定是否使用图像抖动处理，会使绘制出来的图片颜色更加平滑和饱满，图像更加清晰  
			paint.setAntiAlias(true);//非锯齿效果
		}
		return paint;
	}
    
    private float complexToDimension(int unit, int data){	
		return TypedValue.applyDimension(unit, data, context.getResources().getDisplayMetrics());
    }
    
    public Drawable getMaskDrawable(){
    	return maskDrawable;
    }
    
    private void readCustomAttrs(Context context, AttributeSet attrs) {
		TypedArray types = null;
		
		if ( attrs != null ){
			types = context.obtainStyledAttributes(attrs, R.styleable.ZixunImageViewAttr);	
		}
		if (types != null) {
//			getMaskDrawable()
			if ( maskDrawable == null ){
				maskDrawable = (BitmapDrawable) types.getDrawable(R.styleable.ZixunImageViewAttr_mask );
			}			
			posterHeight = types.getDimension(R.styleable.ZixunImageViewAttr_poster_height, complexToDimension(TypedValue.COMPLEX_UNIT_DIP, 396));
			reflectionGapTop = types.getDimension(R.styleable.ZixunImageViewAttr_reflection_gap_top, complexToDimension(TypedValue.COMPLEX_UNIT_DIP, 5));
			reflectionHeight = types.getDimension(R.styleable.ZixunImageViewAttr_reflection_height, complexToDimension(TypedValue.COMPLEX_UNIT_DIP, 89));
			types.recycle();				
		}
	}
    
//    public static void drawByDrawable(Canvas canvas, Paint paint, Drawable drawable, Rect dstRect, int reflectionGap, int reflectionHeight){
//    	if ( canvas == null || drawable == null ){
//    		return;
//    	}
//    	Matrix matrix = new Matrix();
//        Bitmap bitmap = null;
//        float sx = 0.0f, sy = 0.0f;
//        
//        if ( drawable instanceof BitmapDrawable ){
//        	bitmap = ((BitmapDrawable)drawable).getBitmap();
//        	sx = (float)dstRect.width() / bitmap.getWidth();
//            sy = (float)dstRect.height() / bitmap.getHeight();
//            if ( sx != 1.0f || sy != 1.0f ){
//            	if ( paint != null ){
//            		paint.setFilterBitmap(true);
//            	}
//            	matrix.setScale(sx, sy);
//            }           
//            
//            
//            //draw poster
//            canvas.drawBitmap(bitmap, matrix, paint);
////            canvas.save();
//
//            //draw reflection
//            if ( reflectionHeight > 0 ){
//            	matrix.reset();
//            	matrix.preScale(sx, -sy);     
//            	matrix.postTranslate(0, dstRect.height()*2);
//                canvas.clipRect(dstRect.left, dstRect.bottom + reflectionGap, dstRect.right, dstRect.bottom + reflectionGap + reflectionHeight);               
//                canvas.drawBitmap(bitmap, matrix, paint);
//                
//                LinearGradient shader = new LinearGradient(dstRect.left, dstRect.bottom + reflectionGap, 
//                		dstRect.left, dstRect.bottom + reflectionGap + reflectionHeight, 0xddffffff, 0x00ffffff, TileMode.CLAMP);
//        		paint.setShader(shader);
//        		paint.setXfermode(new PorterDuffXfermode(Mode.DST_IN));
//        		canvas.drawRect(dstRect.left, dstRect.bottom + reflectionGap, dstRect.right, dstRect.bottom + reflectionGap + reflectionHeight, paint);
//            }
////            canvas.restore();
//            
//        }else if ( drawable instanceof NinePatchDrawable ){
//        	drawable.setBounds(dstRect);
//        	drawable.draw(canvas);
//        	
//        }
//        
//    }
    
     @Override 
    protected void onDraw(Canvas canvas) {   
    	 super.onDraw(canvas);
    	/* Log.d("linghu", "**##################**");
//        paint.reset();
    	canvas.getClipBounds(maskBounds);
    	int raw_top = maskBounds.top;
    	int raw_bottom = maskBounds.bottom;   

        //draw poster with reflection   	
    	BitmapDrawable poster = (BitmapDrawable)getDrawable();
//    	if(poster != null && maskDrawable != null){
//    		if(comPoster == null){
    			comPoster = ImageUtils.getOverLapImage(poster.getBitmap(), maskDrawable.getBitmap(), paint);
//    		}
//    	}
    	
        maskBounds.bottom = (int)(raw_top + posterHeight);
        Log.d("linghu", "{"+maskBounds.left+","+maskBounds.right+","+maskBounds.top+","+maskBounds.bottom);
//        if ( comPoster != null ){
        	Log.d("linghu", "*********111111*******");
//        	paint.reset();
        	drawByDrawable(canvas, paint, new BitmapDrawable(comPoster), maskBounds, (int)(reflectionGapTop), (int)reflectionHeight);
//        }
*/       
        //draw mask		
    /*  maskBounds.bottom = raw_bottom;
        if ( maskDrawable != null ){
        	Log.d("linghu", "*********222222*******");
        	paint.reset();
        	drawByDrawable(canvas, paint, maskDrawable, maskBounds, 0, 0);
        }     */
    }

}
