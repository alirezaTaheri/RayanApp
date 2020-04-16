package rayan.rayanapp;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;

public class TestCustomView extends View {

    Paint paint;

    public TestCustomView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLUE);
        paint.setStyle(Paint.Style.FILL_AND_STROKE);
        paint.setStrokeWidth(2);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int size = 250;
        canvas.drawLine(10,10, 50,50, paint);
        canvas.drawCircle(size/2f, size/2f, size/2f,paint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        Toast.makeText(getContext(), "W:"+widthMeasureSpec+" / H:"+heightMeasureSpec, Toast.LENGTH_SHORT).show();

        int mode = MeasureSpec.getMode(widthMeasureSpec);
        int size = MeasureSpec.getSize(widthMeasureSpec);
        Log.e("@@@@@@@@@@@@@", "ONNNNNNNNNMeasure" + widthMeasureSpec +" // Height"+ heightMeasureSpec+" && MODE and SIZE:"+mode+size);
//        setMeasuredDimension(newsize,newsize);
    }
}
