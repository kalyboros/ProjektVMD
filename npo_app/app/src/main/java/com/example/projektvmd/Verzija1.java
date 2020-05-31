package com.example.projektvmd;

import android.os.Bundle;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.JavaCameraView;
import org.opencv.android.OpenCVLoader;
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


public class Verzija1 extends AppCompatActivity implements CameraBridgeViewBase.CvCameraViewListener2 {

    ImageView imageView;
    JavaCameraView javaCameraView;

    Scalar scalarLow, scalarHigh;
    Mat mat1,mat2, threshold, hierarchy, mIntermediateMat;

    List<MatOfPoint> contours;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verzija1);
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
        mIntermediateMat = new Mat(height, width, CvType.CV_8UC4);
        hierarchy = new Mat();
    }

    @Override
    public Mat onCameraFrame(CameraBridgeViewBase.CvCameraViewFrame inputFrame) {
        mat1=inputFrame.rgba(); // matrika mat1 dobi barvno sliko iz kamere
        Mat mRgbaT= mat1.t();  // kopija matrike mat1
        Core.flip(mat1.t(), mRgbaT, 1); // obrne martiko mRgbaT za 90 stopinj
        Imgproc.resize(mRgbaT,mRgbaT,mat1.size()); // matriko mRgbaT resiza nazaj v velikost matrike mat1
        Imgproc.GaussianBlur(mRgbaT,mIntermediateMat,new Size(7,7),1);  // odstranimo moteče dejavnike na sliki
        Imgproc.cvtColor(mIntermediateMat,mat2,Imgproc.COLOR_BGR2GRAY);  // spremeni mat2 v sivo sliko

        Imgproc.threshold(mat2, threshold, 190, 255, Imgproc.THRESH_BINARY); // glede na threshold spremeni piksle v črne ali bele

        contours = new ArrayList<MatOfPoint>();
        hierarchy = new Mat();
        Imgproc.findContours(threshold, contours, hierarchy, Imgproc.RETR_TREE, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));  // najde vse contourse

        hierarchy.release();
        for ( int contourIdx=0; contourIdx < contours.size(); contourIdx++ ) // for stavek ki gre skozi vse contourse
        {
            // Minimum size allowed for consideration
            MatOfPoint2f approxCurve = new MatOfPoint2f();
            MatOfPoint2f contour2f = new MatOfPoint2f( contours.get(contourIdx).toArray() );
            //Processing on mMOP2f1 which is in type MatOfPoint2f
            double approxDistance = Imgproc.arcLength(contour2f,true)*0.01;
            Imgproc.approxPolyDP(contour2f, approxCurve, approxDistance, true);  // izračuna koliko je črt na vsakem contour-u

            //Convert back to MatOfPoint
            MatOfPoint points = new MatOfPoint( approxCurve.toArray() );

            // Get bounding rect of contour
            Rect rect = Imgproc.boundingRect(points);
            if(approxCurve.size().height == 3){  // če je število črt enako 3 bo to zaznal kot trikotnik
                double contourArea = Imgproc.contourArea(contours.get(contourIdx));
                if(contourArea > 1500){
                    Imgproc.rectangle(mRgbaT, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 3);  // nariše kvadrat okoli tega objekta
                    Imgproc.putText(mRgbaT, "Trikotni znak", new Point(rect.x, rect.y),3 , 1, new Scalar(255, 0, 0, 255), 2);  // poda text najdenemu objektu
                }
            }
            else if(approxCurve.size().height == 4){ // če je število črt enako 4 bo to zaznal kot kvadrat
                double contourArea = Imgproc.contourArea(contours.get(contourIdx));
                if(contourArea > 1500){
                    Imgproc.rectangle(mRgbaT, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 3);  // nariše kvadrat okoli tega objekta
                    Imgproc.putText(mRgbaT, "Kvadratast znak", new Point(rect.x, rect.y),3 , 1, new Scalar(255, 0, 0, 255), 2);  // poda text najdenemu objektu
                }
            }
            else if(approxCurve.size().height == 8){ // če je število črt enako 4 bo to zaznal kot kvadrat
                double contourArea = Imgproc.contourArea(contours.get(contourIdx));
                if(contourArea > 1500){
                    Imgproc.rectangle(mRgbaT, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(255, 0, 0, 255), 3);  // nariše kvadrat okoli tega objekta
                    Imgproc.putText(mRgbaT, "STOP", new Point(rect.x, rect.y),3 , 1, new Scalar(255, 0, 0, 255), 2);  // poda text najdenemu objektu
                }
            }
        }
        return mRgbaT;  //vrne barvno sliko s vsemi najdenimi objekti
    }
}
