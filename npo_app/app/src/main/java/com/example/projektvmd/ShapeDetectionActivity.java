package com.example.projektvmd;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Camera;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCamera2View;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class ShapeDetectionActivity extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    ImageView imageView;
    JavaCameraView javaCameraView;

    Scalar scalarLow, scalarHigh;
    Mat mat1,mat2, threshold, hierarchy, mIntermediateMat, HSV; //circles;

    List<MatOfPoint> contours;
    MatOfPoint approxContours;
    MatOfPoint2f thisContour;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shape_detection);
        OpenCVLoader.initDebug();

        javaCameraView = (JavaCameraView)findViewById(R.id.cameraView);
        javaCameraView.setCameraIndex(0);

        scalarLow = new Scalar(45,20,10);
        scalarHigh = new Scalar(75,255,255);
        javaCameraView.setCvCameraViewListener(this);
        javaCameraView.enableView();
    }

    @Override
    protected void onPause() {
        super.onPause();
        javaCameraView.disableView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        javaCameraView.enableView();
    }

    @Override
    protected void onDestroy() {
        javaCameraView.disableView();
        super.onDestroy();
    }

    @Override
    public void onCameraViewStopped() {

    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        mat1 = new Mat(width,height, CvType.CV_8UC4);
        mat2 = new Mat(width,height, CvType.CV_8UC1);
        threshold = new Mat(width,height, CvType.CV_8UC1);
        //circles = new Mat(width,height, CvType.CV_8UC1);
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        HSV = new Mat(height, width, CvType.CV_8UC3);
        hierarchy = new Mat();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat1=inputFrame.rgba(); // matriko mat1 pretvori v barvno
        Mat mRgbaT= mat1.t();  // naredi kopijo matrike mat1
        Core.flip(mat1.t(), mRgbaT, 1); // obrne camero za 90 stopinj
        Imgproc.resize(mRgbaT,mRgbaT,mat1.size()); // po tem ko obrne sliko jo more resize-at nazaj tako kot je mat1
        Imgproc.GaussianBlur(mRgbaT,mIntermediateMat,new Size(7,7),1);
        Imgproc.cvtColor(mIntermediateMat,mat2,Imgproc.COLOR_BGR2GRAY);  // spremeni mat2 v sivo sliko

        Imgproc.threshold(mat2, threshold, 190, 255, Imgproc.THRESH_BINARY); // pobarva piksle ali črno ali belo glede na podan threshold

        Imgproc.cvtColor(mIntermediateMat, HSV, Imgproc.COLOR_RGB2HSV);  // iz barvne v HSV format
        Mat red_hue_range = new Mat();
        Core.inRange(HSV, new Scalar(160,70,50), new Scalar(190,255,255), red_hue_range);  // poišče rdečo barvo

        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();
        Imgproc.Canny(mRgbaT, mIntermediateMat, 240, 255);
        Imgproc.findContours(red_hue_range, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));  // poišče vse contourse

        hierarchy.release();
        for ( int contourIdx=0; contourIdx < contours.size(); contourIdx++ ) // for stavek ki gre skozi vse contourse
        {
            // Minimum size allowed for consideration
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(contourIdx).toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f,true)*0.01;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);  // izračuna približno število črt

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            if(approxCurve.size().height == 8){ // če je število črt enako 4 bo to zaznal kot kvadrat
                double contourArea = Imgproc.contourArea(contours.get(contourIdx));
                if(contourArea > 1500){
                    Imgproc.rectangle(mRgbaT, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 3);  // nariše kvadrat okoli tega objekta
                    Imgproc.putText(mRgbaT, "STOP", new Point(rect.x, rect.y),3 , 1, new Scalar(255, 0, 0, 255), 2);  // poda text najdenemu objektu
                }
            }
        }
        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();

        Mat blue_hue_range = new Mat();
        Core.inRange(HSV, new Scalar(120,150,0), new Scalar(140,255,255), blue_hue_range);  // poišče modro barvo

        Imgproc.findContours(blue_hue_range, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));  // poišče vse contourse
        hierarchy.release();
        for ( int contourIdx=0; contourIdx < contours.size(); contourIdx++ ) // for stavek ki gre skozi vse contourse
        {
            // Minimum size allowed for consideration
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(contourIdx).toArray());
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.01;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);  // izračuna približno število črt

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            if(approxCurve.size().height == 4){ // če je število črt enako 4 bo to zaznal kot kvadrat
                //float w = rect.width;
                //float aspectRatio = w/rect.height;  // izračun aspectRation pogleda če je objekt kvadrat
                double contourArea = Imgproc.contourArea(contours.get(contourIdx));
                if(contourArea > 1500){
                    Imgproc.rectangle(mRgbaT, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 3);  // nariše kvadrat okoli tega objekta
                    Imgproc.putText(mRgbaT, "Moder znak", new Point(rect.x, rect.y),3 , 1, new Scalar(255, 0, 0, 255), 2);  // poda text najdenemu objektu
                }
            }
        }

        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();

        Mat white_hue_range = new Mat();
        Core.inRange(HSV, new Scalar(0,0,255), new Scalar(255,0,255), white_hue_range);  // poišče belo barvo

        Imgproc.findContours(white_hue_range, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));  // poišče vse contourse
        hierarchy.release();
        for ( int contourIdx=0; contourIdx < contours.size(); contourIdx++ ) // for stavek ki gre skozi vse contourse
        {
            // Minimum size allowed for consideration
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(contourIdx).toArray());
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.01;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);  // izračuna približno število črt

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            if(approxCurve.size().height == 3){ // če je število črt enako 3 bo to zaznal kot trikotnik
                double contourArea = Imgproc.contourArea(contours.get(contourIdx));
                if(contourArea > 1500){
                    Imgproc.rectangle(mRgbaT, new Point(rect.x-10, rect.y-10), new Point(rect.x + rect.width + 10, rect.y + rect.height + 10), new Scalar(255, 0, 0, 255), 3);  // nariše kvadrat okoli tega objekta
                    Imgproc.putText(mRgbaT, "Trikotni znak", new Point(rect.x, rect.y),3 , 1, new Scalar(255, 0, 0, 255), 2);  // poda text najdenemu objektu
                }
            }
        }

        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();

        Mat yellow_hue_range = new Mat();
        Core.inRange(HSV, new Scalar(20, 100, 100), new Scalar(30, 255, 255), yellow_hue_range);  // poišče rumeno barvo

        Imgproc.findContours(yellow_hue_range, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));  // poišče vse contourse
        hierarchy.release();
        for ( int contourIdx=0; contourIdx < contours.size(); contourIdx++ ) // for stavek ki gre skozi vse contourse
        {
            // Minimum size allowed for consideration
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f(contours.get(contourIdx).toArray());
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f, true) * 0.01;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);  // izračuna približno število črt

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint(approxCurve.toArray());

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            if(approxCurve.size().height == 4){ // če je število črt enako 4 bo to zaznal kot kvadrat
                float w = rect.width;
                float aspectRatio = w/rect.height;  // izračun aspectRation pogleda če je objekt kvadrat
                double contourArea = Imgproc.contourArea(contours.get(contourIdx));
                if(contourArea > 1500){
                    Imgproc.rectangle(mRgbaT, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 3);  // nariše kvadrat okoli tega objekta
                    Imgproc.putText(mRgbaT, "Prednostni znak", new Point(rect.x, rect.y),3 , 1, new Scalar(255, 0, 0, 255), 2);  // poda text najdenemu objektu
                }
            }
        }
        /*Mat circles = new Mat();
        Imgproc.HoughCircles(threshold, circles, Imgproc.CV_HOUGH_GRADIENT,
                2.0, 100, 100, 300,
                20, 400);

        if (circles.cols() > 0){
            for (int i = 0; i < circles.cols(); i++)
            {
                double[] vCircle = circles.get(0,i);
                Log.d("nekaj", String.valueOf(vCircle.length));

                if (vCircle == null)
                    break;

                Point pt = new Point(Math.round(vCircle[0]), Math.round(vCircle[1]));
                int radius = (int)Math.round(vCircle[2]);

                // draw the found circle
                Imgproc.circle(mRgbaT, pt, radius, new Scalar(0,255,0), 5);
                Imgproc.circle(mRgbaT, pt, 3, new Scalar(0,0,255), 5);
            }
        }*/
        return mRgbaT;  //vrne barvno sliko s vsemi najdenimi objekti
    }

    public void Photo(View view) {

    }
}
