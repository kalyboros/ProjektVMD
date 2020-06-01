package com.example.projektvmd;

import android.content.Context;
import android.hardware.Camera;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import org.opencv.android.JavaCameraView;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

public class CapturePhoto extends JavaCameraView implements android.hardware.Camera.PictureCallback {

    /*static {
        System.loadLibrary("native-lib");
    }*/
    private static final String TAG = "OpenCV";
    private String mPictureFileName;

    public CapturePhoto(Context context, AttributeSet attrs) {
        super(context, attrs);
    }


    public void takePicture(final String fileName){
        Log.i(TAG, "Taking picture");
        this.mPictureFileName = fileName;
        mCamera.setPreviewCallback(null);

        mCamera.takePicture(null, null, this);
    }

    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
        Log.i(TAG, "Saving a bitmap to file");
        List<MatOfPoint> contours;
        //mCamera.startPreview();
        //mCamera.setPreviewCallback(this);
        Mat jpegData = new Mat(1, data.length, CvType.CV_8UC1);
        Mat mat2 = new Mat(1, data.length, CvType.CV_8UC1);
        //Mat threshold = new Mat(1, data.length, CvType.CV_8UC1);
        Mat mIntermediateMat = new Mat(1, data.length, CvType.CV_8UC4);
        Mat mat1 = new Mat(1, data.length, CvType.CV_8UC4);
        Mat HSV = new Mat(1, data.length, CvType.CV_8UC3);
        Mat hierarchy = new Mat();
        jpegData.put(0, 0, data);
        Mat bgrMat = Imgcodecs.imdecode(jpegData, Imgcodecs.IMREAD_COLOR);
        Imgproc.resize(bgrMat, bgrMat, new Size(620, 344));
        //________________________________________________________________________________________________________________________________________
        mat1=bgrMat;
        Imgproc.cvtColor(mat1,mat1, Imgproc.COLOR_BGR2RGB);
        Mat mRgbaT= mat1.t();  // naredi kopijo matrike mat1
        Core.flip(mat1.t(), mRgbaT, 1); // obrne camero za 90 stopinj
        Imgproc.resize(mRgbaT,mRgbaT,mat1.size()); // po tem ko obrne sliko jo more resize-at nazaj tako kot je mat1
        Imgproc.GaussianBlur(mRgbaT,mIntermediateMat,new Size(7,7),1);
        Imgproc.cvtColor(mIntermediateMat,mat2,Imgproc.COLOR_BGR2GRAY);  // spremeni mat2 v sivo sliko

        //Imgproc.threshold(mat2, threshold, 190, 255, Imgproc.THRESH_BINARY); // pobarva piksle ali črno ali belo glede na podan threshold

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
        //__________________________________________________________________________________________________________________________________________
        Imgproc.cvtColor(mRgbaT, mRgbaT, Imgproc.COLOR_RGB2BGR);
        MatOfByte matOfByte = new MatOfByte();

        Imgcodecs.imencode(".jpeg", mRgbaT, matOfByte);
        byte[] byteArray = matOfByte.toArray();

        try{
            FileOutputStream fos = new FileOutputStream(mPictureFileName);

            fos.write(byteArray);
            fos.close();
        }
        catch (java.io.IOException e){
            Log.e("PictureDemo", "Exception in photoCallback", e);
        }
    }
}
