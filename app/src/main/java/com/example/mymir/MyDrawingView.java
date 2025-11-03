package com.example.mymir;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.animation.ValueAnimator;

public class MyDrawingView extends View {

    private Paint paintFill;
    private Paint paintStroke;
    private Bitmap background;

    private float rocketX = 0, rocketY = 0; //позиция ракеты
    private boolean dragging = false;
    private float lastX, lastY;

    private float amogusRotation = 0f;

    public MyDrawingView(Context context) {
        super(context);
        init();
    }

    public MyDrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public MyDrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        paintFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintFill.setStyle(Paint.Style.FILL);

        paintStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        paintStroke.setStyle(Paint.Style.STROKE);
        paintStroke.setColor(Color.BLACK);
        paintStroke.setStrokeWidth(4);

        // Фон space.jpg из drawable
        background = BitmapFactory.decodeResource(getResources(), R.drawable.space);

        // Запуск анимации вращения амогуса
        ValueAnimator animator = ValueAnimator.ofFloat(0, 360);
        animator.setDuration(4000);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setInterpolator(new LinearInterpolator());
        animator.addUpdateListener(a -> {
            amogusRotation = (float) a.getAnimatedValue();
            invalidate();
        });
        animator.start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //фон через битмап
        if (background != null) {
            Rect dst = new Rect(0, 0, getWidth(), getHeight());
            canvas.drawBitmap(background, null, dst, null);
        }

        //отрисовка и перемещение ракеты
        canvas.save();
        canvas.translate(rocketX, rocketY);
        drawRocket(canvas);
        canvas.restore();

        //отрисовка и вращение амогуса
        canvas.save();
        canvas.translate(375, 125); //центр
        canvas.rotate(amogusRotation);
        canvas.translate(-375, -125);
        drawAmogus(canvas);
        canvas.restore();
    }

    //РАКЕТА
    private void drawRocket(Canvas canvas) {
        paintFill.setColor(Color.RED);

        //вершина ракеты/треугольник через path
        Path top = new Path();
        top.moveTo(250, 100);
        top.lineTo(200, 200);
        top.lineTo(300, 200);
        top.close();
        canvas.drawPath(top, paintFill);
        canvas.drawPath(top, paintStroke);

        //корпус 1
        canvas.drawRect(200, 200, 300, 400, paintFill);
        canvas.drawRect(200, 200, 300, 400, paintStroke);

        //корпус 2
        canvas.drawRect(200, 400, 300, 500, paintFill);
        canvas.drawRect(200, 400, 300, 500, paintStroke);

        //окна/круги
        paintFill.setColor(Color.YELLOW);
        Paint thickStroke = new Paint(paintStroke);
        thickStroke.setStrokeWidth(10); // толще обводка

        canvas.drawCircle(250, 250, 25, paintFill);
        canvas.drawCircle(250, 250, 25, thickStroke);

        canvas.drawCircle(250, 325, 25, paintFill);
        canvas.drawCircle(250, 325, 25, thickStroke);

        // Линии
        canvas.drawLine(250, 100, 250, 200, paintStroke);
        canvas.drawLine(250, 100, 225, 200, paintStroke);
        canvas.drawLine(250, 100, 275, 200, paintStroke);

        //Пропеллеры/параллелограммы (через Path)
        Paint propFill = new Paint(Paint.ANTI_ALIAS_FLAG);
        propFill.setStyle(Paint.Style.FILL);
        propFill.setColor(Color.GRAY);

        Paint propStroke = new Paint(Paint.ANTI_ALIAS_FLAG);
        propStroke.setStyle(Paint.Style.STROKE);
        propStroke.setColor(Color.DKGRAY);
        propStroke.setStrokeWidth(3);

        Path p1 = new Path();
        p1.moveTo(150, 250);
        p1.lineTo(200, 200);
        p1.lineTo(200, 300);
        p1.lineTo(150, 350);
        p1.close();
        canvas.drawPath(p1, propFill);   // заливка пропеллера
        canvas.drawPath(p1, propStroke); // контур пропеллера

        Path p2 = new Path();
        p2.moveTo(150, 450);
        p2.lineTo(200, 400);
        p2.lineTo(200, 500);
        p2.lineTo(150, 550);
        p2.close();
        canvas.drawPath(p2, propFill);
        canvas.drawPath(p2, propStroke);

        Path p3 = new Path();
        p3.moveTo(300, 200);
        p3.lineTo(350, 250);
        p3.lineTo(350, 350);
        p3.lineTo(300, 300);
        p3.close();
        canvas.drawPath(p3, propFill);
        canvas.drawPath(p3, propStroke);

        Path p4 = new Path();
        p4.moveTo(300, 400);
        p4.lineTo(350, 450);
        p4.lineTo(350, 550);
        p4.lineTo(300, 500);
        p4.close();
        canvas.drawPath(p4, propFill);
        canvas.drawPath(p4, propStroke);

        // Овальный пропеллер
        RectF oval = new RectF(225, 400, 275, 550);
        canvas.drawOval(oval, propFill);
        canvas.drawOval(oval, propStroke);
    }

    //амогус/космонавт
    private void drawAmogus(Canvas canvas) {
        paintFill.setColor(Color.CYAN);

        //Тело
        canvas.drawCircle(375, 75, 50, paintFill);
        canvas.drawRect(325, 75, 425, 125, paintFill);

        //Визор (овал)
        paintFill.setColor(Color.WHITE);
        RectF visor = new RectF(350, 50, 400, 75);
        canvas.drawOval(visor, paintFill);

        //Ноги
        paintFill.setColor(Color.CYAN);
        canvas.drawRect(325, 125, 350, 175, paintFill);
        canvas.drawRect(400, 125, 425, 175, paintFill);
    }


    // Перетаскивание ракеты

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //попали ли по ракете (простая область)
                if (x > 200 + rocketX && x < 300 + rocketX && y > 100 + rocketY && y < 500 + rocketY) {
                    dragging = true;
                    lastX = x;
                    lastY = y;
                    return true;
                }
                break;

            case MotionEvent.ACTION_MOVE:
                if (dragging) {
                    rocketX += x - lastX;
                    rocketY += y - lastY;
                    lastX = x;
                    lastY = y;
                    invalidate();
                    return true;
                }
                break;

            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                dragging = false;
                break;
        }
        return true;
    }
}
