package com.example.samplecompasstracker.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.support.v4.content.ContextCompat;

import com.example.samplecompasstracker.R;

public class Compass {

    private Paint mainPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint backgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint degreesTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint directionsTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private Paint destinationPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    private Path userDirectionPath = new Path();
    private Path compassSmallClockPath = null;
    private Path compassMediumClockPath = null;
    private Path compassBigClockPath = null;
    private Path destinastionPath = new Path();

    private Context context;
    private float maxCompassRadius = 430;
    private float scale;
    private Point centerPoint;


    private int textMainColor;
    private int textSecondaryColor;
    private int backgroundColor;
    private int smallDegreesColor;
    private int circleColor;
    private int northColor;
    private int destinationColor;
    private boolean isCreated = false;
    private float azimuth = 0.0f;
    private float destinationBearing;
    private boolean isTracking = false;

    public Compass(Context context) {
        this.context = context;
    }

    public void drawCompass(Canvas canvas) {
        scale = ((float) Math.min(canvas.getWidth(), canvas.getHeight())) / 1000.0f;
        centerPoint = new Point(canvas.getWidth() / 2, canvas.getHeight() / 2);

        setPaints();

        drawBackground(canvas);
        drawCompassClock(canvas);
        drawUserDirection(canvas);

        if (isTracking) {
            canvas.save();
            canvas.rotate(-azimuth, centerPoint.x, centerPoint.y);
            drawDestinationPoint(canvas, destinationBearing);
            canvas.restore();
        }
    }

    private void drawDestinationPoint(Canvas canvas, float destinationBearing) {
        float point = realPx(40);
        float radius = realPx(maxCompassRadius);
        destinationPaint.setColor(destinationColor);
        destinationPaint.setStyle(Paint.Style.FILL);

        float cos = (float) Math.cos(Math.toRadians(destinationBearing));
        float sin = (float) Math.sin(Math.toRadians(destinationBearing));

        float x = (cos * radius) + centerPoint.x;
        float y = (sin * radius) + centerPoint.y;

        destinastionPath.reset();
        destinastionPath.lineTo(x, y + 5);
        destinastionPath.lineTo(x + point / 2.0f, y + point);
        destinastionPath.lineTo(x - point / 2.0f, y + point);
        destinastionPath.lineTo(x, y + 5);

        canvas.save();

        canvas.rotate(90.0f + destinationBearing, x, y);
        canvas.drawPath(destinastionPath, destinationPaint);

        canvas.restore();
    }

    private void drawUserDirection(Canvas canvas) {
        float triangleLength = realPx(45);
        float newX = centerPoint.x;
        float newY = centerPoint.y - realPx(maxCompassRadius + 22) + triangleLength / 2.0f;

        userDirectionPath.reset();
        userDirectionPath.lineTo(newX - triangleLength / 2.0f, newY - triangleLength);
        userDirectionPath.lineTo(newX + triangleLength / 2.0f, newY - triangleLength);
        userDirectionPath.lineTo(newX, newY);
        userDirectionPath.lineTo(newX - triangleLength / 2.0f, newY - triangleLength);

        mainPaint.setStyle(Paint.Style.FILL);
        mainPaint.setColor(northColor);
        canvas.drawPath(userDirectionPath, mainPaint);

        directionsTextPaint.setTextSize(realPx(80));
        String str = ((int) azimuth + "°");
        Rect rect = new Rect();
        directionsTextPaint.getTextBounds(str, 0, str.length(), rect);
        newX = centerPoint.x - directionsTextPaint.measureText(str) / 2.0f;
        newY = centerPoint.y + rect.height() / 2.0f;
        canvas.drawText(str, newX, newY, directionsTextPaint);
    }

    private void drawCompassClock(Canvas canvas) {
        canvas.save();
        canvas.rotate(-azimuth, centerPoint.x, centerPoint.y);
        drawSmallClock(canvas);
        drawMediumClock(canvas);
        drawBigClock(canvas);
        drawDegrees(canvas);
        drawDirections(canvas);
        canvas.restore();
    }

    private void drawDirections(Canvas canvas) {
        directionsTextPaint.setTextSize(realPx(70));

        Paint.FontMetrics fontMetrics = directionsTextPaint.getFontMetrics();
        float fontHeight = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;
        float radius = realPx(380) - fontHeight;

        directionsTextPaint.setColor(textMainColor);
        drawDirectionText(canvas, 0.0f, "E", radius, directionsTextPaint);
        drawDirectionText(canvas, 90.0f, "S", radius, directionsTextPaint);
        drawDirectionText(canvas, 180.0f, "W", radius, directionsTextPaint);
        directionsTextPaint.setColor(northColor);
        drawDirectionText(canvas, 270.0f, "N", radius, directionsTextPaint);

        directionsTextPaint.setTextSize(realPx(40));
        directionsTextPaint.setColor(textSecondaryColor);
        drawDirectionText(canvas, 315.0f, "NE", radius, directionsTextPaint);
        drawDirectionText(canvas, 45.0f, "SE", radius, directionsTextPaint);
        drawDirectionText(canvas, 135.0f, "SW", radius, directionsTextPaint);
        drawDirectionText(canvas, 225.0f, "NW", radius, directionsTextPaint);
    }

    private void drawDirectionText(Canvas canvas, float degree, String directionText, float radius, Paint directionsTextPaint) {
        Paint.FontMetrics fontMetrics = directionsTextPaint.getFontMetrics();
        float height = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;

        float cos = (float) Math.cos(Math.toRadians(degree));
        float sin = (float) Math.sin(Math.toRadians(degree));

        float x = (cos * (radius)) + centerPoint.x;
        float y = (sin * (radius)) + centerPoint.y;

        canvas.save();
        canvas.translate(x, y);

        canvas.rotate(90 + degree);
        canvas.drawText(directionText, -directionsTextPaint.measureText(directionText) / 2.0f, height, directionsTextPaint);
        canvas.restore();
    }

    private void drawDegrees(Canvas canvas) {
        float radius = 330;
        drawDegrees(canvas, 270.0f, "0", radius, 30);
        drawDegrees(canvas, 315.0f, "45", radius, 30);
        drawDegrees(canvas, 360.0f, "90", radius, 30);
        drawDegrees(canvas, 45.0f, "135", radius, 30);
        drawDegrees(canvas, 90.0f, "180", radius, 30);
        drawDegrees(canvas, 135.0f, "225", radius, 30);
        drawDegrees(canvas, 180.0f, "270", radius, 30);
        drawDegrees(canvas, 225.0f, "315", radius, 30);

        drawDegrees(canvas, 285.0f, "15", radius, 20);
        drawDegrees(canvas, 300.0f, "30", radius, 20);
        drawDegrees(canvas, 330.0f, "60", radius, 20);
        drawDegrees(canvas, 345.0f, "75", radius, 20);
        drawDegrees(canvas, 15.0f, "105", radius, 20);
        drawDegrees(canvas, 30.0f, "120", radius, 20);
        drawDegrees(canvas, 60.0f, "150", radius, 20);
        drawDegrees(canvas, 75.0f, "165", radius, 20);
        drawDegrees(canvas, 105.0f, "195", radius, 20);
        drawDegrees(canvas, 120.0f, "210", radius, 20);
        drawDegrees(canvas, 150.0f, "240", radius, 20);
        drawDegrees(canvas, 165.0f, "255", radius, 20);
        drawDegrees(canvas, 195.0f, "285", radius, 20);
        drawDegrees(canvas, 210.0f, "300", radius, 20);
        drawDegrees(canvas, 240.0f, "330", radius, 20);
        drawDegrees(canvas, 255.0f, "345", radius, 20);
    }

    private void drawDegrees(Canvas canvas, float degree, String degreeText, float radius, int textSize) {
        degreesTextPaint.setTextSize(realPx(textSize));
        Paint.FontMetrics fontMetrics = degreesTextPaint.getFontMetrics();
        float height = fontMetrics.bottom - fontMetrics.top + fontMetrics.leading;

        float cos = (float) Math.cos(Math.toRadians(degree));
        float sin = (float) Math.sin(Math.toRadians(degree));

        float x = (cos * realPx(radius)) + centerPoint.x;
        float y = (sin * realPx(radius)) + centerPoint.y;

        canvas.save();

        canvas.translate(x, y);
        canvas.rotate(90.0f + degree);
        canvas.drawText(degreeText, -degreesTextPaint.measureText(degreeText) / 2.0f, height, degreesTextPaint);

        canvas.restore();
    }

    private void drawBigClock(Canvas canvas) {
        mainPaint.setColor(textMainColor);
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setStrokeWidth(realPx(7));

        if (compassBigClockPath == null) {
            compassBigClockPath = new Path();
            float degree = 45f;
            for (float startDegree = 0.0f; startDegree < 2 * Math.PI; startDegree += Math.toRadians(degree)) {
                float cos = (float) Math.cos(startDegree);
                float sin = (float) Math.sin(startDegree);

                float x = realPx(330) * cos;
                float y = realPx(330) * sin;
                compassBigClockPath.moveTo(x + ((float) centerPoint.x), y + ((float) centerPoint.y));

                x = realPx(380) * cos;
                y = realPx(380) * sin;
                compassBigClockPath.lineTo(x + ((float) centerPoint.x), y + ((float) centerPoint.y));
            }
        }
        canvas.drawPath(compassBigClockPath, mainPaint);

        userDirectionPath.reset();
        mainPaint.setColor(northColor);
        mainPaint.setStrokeWidth(realPx(9));

        float north = (float) Math.toRadians(270.0d);
        float cosNorth = (float) Math.cos((double) north);
        float sinNorth = (float) Math.sin((double) north);

        float x = realPx(329) * cosNorth;
        float y = realPx(329) * sinNorth;
        userDirectionPath.moveTo(((float) centerPoint.x) + x, ((float) centerPoint.y) + y);

        x = realPx(381) * cosNorth;
        y = realPx(381) * sinNorth;
        userDirectionPath.lineTo(x + centerPoint.x, y + centerPoint.y);

        canvas.drawPath(userDirectionPath, mainPaint);
    }

    private void drawMediumClock(Canvas canvas) {
        mainPaint.setColor(smallDegreesColor);
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setStrokeWidth(realPx(3));

        if (compassMediumClockPath == null) {
            compassMediumClockPath = new Path();
            float degree = 15f;
            for (float startDegree = 0.0f; startDegree < 2 * Math.PI; startDegree += Math.toRadians(degree)) {
                float cos = (float) Math.cos(startDegree);
                float sin = (float) Math.sin(startDegree);

                float x = realPx(340) * cos;
                float y = realPx(340) * sin;
                compassMediumClockPath.moveTo(x + ((float) centerPoint.x), y + ((float) centerPoint.y));

                x = realPx(380) * cos;
                y = realPx(380) * sin;
                compassMediumClockPath.lineTo(x + ((float) centerPoint.x), y + ((float) centerPoint.y));
            }
        }
        canvas.drawPath(compassMediumClockPath, mainPaint);
    }

    private void drawSmallClock(Canvas canvas) {
        mainPaint.setColor(smallDegreesColor);
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setStrokeWidth(realPx(3));

        if (compassSmallClockPath == null) {
            compassSmallClockPath = new Path();
            float degree = 3.0f;

            for (float startDegree = 0.0f; startDegree < 2 * Math.PI; startDegree += Math.toRadians(degree)) {
                float cos = (float) Math.cos(startDegree);
                float sin = (float) Math.sin(startDegree);

                float x = realPx(350) * cos;
                float y = realPx(350) * sin;
                compassSmallClockPath.moveTo(x + ((float) centerPoint.x), y + ((float) centerPoint.y));

                x = realPx(380) * cos;
                y = realPx(380) * sin;
                compassSmallClockPath.lineTo(x + ((float) centerPoint.x), y + ((float) centerPoint.y));
            }

        }
        canvas.drawPath(compassSmallClockPath, mainPaint);
    }

    private void drawBackground(Canvas canvas) {
        canvas.drawCircle(centerPoint.x, centerPoint.y, realPx(maxCompassRadius), backgroundPaint);

        mainPaint.setColor(circleColor);
        mainPaint.setStyle(Paint.Style.STROKE);
        mainPaint.setStrokeWidth(realPx(2));
        canvas.drawCircle(centerPoint.x,centerPoint.y, realPx(maxCompassRadius), mainPaint);
    }

    private void setColorsResource() {
        textMainColor = ContextCompat.getColor(context, R.color.compass_text_primary_color);
        textSecondaryColor = ContextCompat.getColor(context, R.color.compass_text_secondary_color);
        backgroundColor = ContextCompat.getColor(context, R.color.compass_background_color);
        smallDegreesColor = ContextCompat.getColor(context, R.color.compass_small_degree_color);
        circleColor = ContextCompat.getColor(context, R.color.compass_text_secondary_color);
        northColor = ContextCompat.getColor(context, R.color.compass_north_color);
        destinationColor = ContextCompat.getColor(context, R.color.compass_destination_color);
    }

    private void setPaints() {
        if (isCreated)
            return;

        setColorsResource();

        mainPaint.setTextSize(realPx(30));
        mainPaint.setColor(textMainColor);

        backgroundPaint.setColor(backgroundColor);
        backgroundPaint.setStyle(Paint.Style.FILL);

        degreesTextPaint.setColor(textMainColor);

        isCreated = true;
    }

    private float realPx(float width) {
        return width * scale;
    }

    public void onSizeChanged(int w, int h, int oldw, int oldh) {
        isCreated = false;
    }

    public void setAzimuth(float azimuth) {
        this.azimuth = azimuth;
    }

    public void setDestinationLocation(float bearing) {
        destinationBearing = bearing;
    }

    public void setTracking(boolean isTracking) {
        this.isTracking = isTracking;
    }

    public boolean isTracking() {
        return isTracking;
    }
}
