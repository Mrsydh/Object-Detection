package sample;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.opencv.videoio.VideoCapture;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.*;

public class Controller {

    @FXML //fxml button
    private Button insert_pic;
    @FXML //fxml image view
    private ImageView currentFrame;
    @FXML
    private TextField nameField;
    @FXML
    private ImageView originalFrame;
    @FXML
    private ImageView maskImage;
    @FXML
    private ImageView morphImage;
    @FXML
    private Slider hueStart;
    @FXML
    private Slider hueStop;
    @FXML
    private Slider saturationStart;
    @FXML
    private Slider saturationStop;
    @FXML
    private Slider valueStart;
    @FXML
    private Slider valueStop;
    @FXML
    private Label hsvCurrentValues; //to show current values set with the sliders

    private ScheduledExecutorService timer;
    private boolean cameraActive=false;
    private VideoCapture capture=new VideoCapture();
    private static int cameraId=0; //assign 0 because it is webcam
    private ObjectProperty<String> hsvValuesProp;

    public void insertPic(ActionEvent event) {

        hsvValuesProp=new SimpleObjectProperty<>();
        this.hsvCurrentValues.textProperty().bind(hsvValuesProp);

        this.imageViewProperties(this.maskImage, 200);
        this.imageViewProperties(this.morphImage, 200);

        int width=100;
        int height=100;


        //HighGui.imshow("Image", matrix);
        //HighGui.waitKey();

        Runnable frameGrabber=new Runnable() {
            @Override
            public void run() {
                Mat frame=grabFrame();
                Image imageToShow=Utils.mat2Image(frame);
                updateImageView(originalFrame,imageToShow);
            }
        };
        this.timer=Executors.newSingleThreadScheduledExecutor();
        this.timer.scheduleAtFixedRate(frameGrabber,0,33,TimeUnit.MILLISECONDS);

        this.insert_pic.setText("Stop picture");

    }

    private void imageViewProperties(ImageView image, int dimension) {
        image.setFitWidth(dimension);
        image.setPreserveRatio(true);
    }

    private void updateImageView(ImageView view, Image imageToShow) {
        Utils.onFXThread(view.imageProperty(), imageToShow);
    }

    private Mat grabFrame() {
        String file="C://Users//user//IdeaProjects//OpenCvVers2//RedBallpng.png";
        Imgcodecs image=new Imgcodecs();
        Mat frame=image.imread(file);
        Mat blurredImage=new Mat();
        Mat hsvImage=new Mat();
        Mat mask=new Mat();
        Mat morph=new Mat();
        //to remove noise
        Imgproc.blur(frame,blurredImage,new Size(7,7));
        //to convert color from rgb to hsv
        Imgproc.cvtColor(blurredImage,hsvImage,Imgproc.COLOR_BGR2HSV);

        //Scalar minValues=new Scalar(150,150,50);
        //Scalar maxValues=new Scalar(180,255,150);


        Scalar minValues=new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(), this.valueStart.getValue());
        Scalar maxValues=new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(), this.valueStop.getValue());

        String valuesToPrint="Hue range "+minValues.val[0]+"-" +maxValues.val[0]+"\tSaturation range "+minValues.val[1]+"-" +maxValues.val[1]+"" +
                "\tValue range "+minValues.val[2]+"-" +maxValues.val[2];
        Utils.onFXThread(this.hsvValuesProp,valuesToPrint);

        //threshold to get the color of the object
        Core.inRange(hsvImage,minValues,maxValues,mask);
        updateImageView(maskImage,Utils.mat2Image(mask));

        Mat dilateElement=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(24,4));
        Mat erodeElement=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(12,12));

        Imgproc.erode(mask,morph,erodeElement);
        Imgproc.erode(morph,morph,erodeElement);
        Imgproc.dilate(morph,morph,dilateElement);
        Imgproc.dilate(morph,morph,dilateElement);


        updateImageView(morphImage,Utils.mat2Image(morph));
        return frame;

    }

    /*@FXML
    private Mat grabFrame() {

        Mat frame=new Mat();
        if(this.capture.isOpened()) {
            try {
                this.capture.read(frame);
                if (!frame.empty()) {

                    //mat object is used to store the values of an image
                    Mat blurredImage=new Mat();
                    Mat hsvImage=new Mat();
                    Mat mask=new Mat();
                    Mat morphOut=new Mat();

                    //remove noise
                    Imgproc.blur(frame,blurredImage,new Size(7,7));
                    //convert frame to hsv
                    Imgproc.cvtColor(blurredImage, hsvImage, Imgproc.COLOR_HSV2BGR);

                    Scalar minValues=new Scalar(this.hueStart.getValue(), this.saturationStart.getValue(), this.valueStart.getValue());
                    Scalar maxValues=new Scalar(this.hueStop.getValue(), this.saturationStop.getValue(), this.valueStop.getValue());

                    String valuesToPrint="Hue range "+minValues.val[0]+"-" +maxValues.val[0]+"\tSaturation range "+minValues.val[1]+"-" +maxValues.val[1]+"" +
                            "\tValue range "+minValues.val[2]+"-" +maxValues.val[2];

                    Utils.onFXThread(this.hsvValuesProp,valuesToPrint); //Run the specified Runnable on the JavaFX Application Thread
                    // at some unspecified time in the future. This method, which may be called from any thread, will post the Runnable
                    // to an event queue and then return immediately to the caller.

                    Core.inRange(hsvImage,minValues,maxValues,mask);

                    updateImageView(maskImage, Utils.mat2Image(mask));

                    Mat dilateElement=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(24,4));
                    Mat erodeElement=Imgproc.getStructuringElement(Imgproc.MORPH_RECT,new Size(12,12));

                    Imgproc.erode(mask,morphOut,erodeElement);
                    Imgproc.erode(morphOut,morphOut,erodeElement);

                    Imgproc.dilate(morphOut,morphOut,dilateElement);
                    Imgproc.dilate(morphOut,morphOut,dilateElement);

                    this.updateImageView(this.morphImage,Utils.mat2Image(morphOut));

                    frame=this.findAndDrawBalls(morphOut,frame);

                }
            }
            catch(Exception e){
                System.err.println("Exception during the image elaboration: "+e);
            }

        }

        return frame;
    }
    public void startCamera(ActionEvent event) {

        hsvValuesProp=new SimpleObjectProperty<>();
        this.hsvCurrentValues.textProperty().bind(hsvValuesProp);

        //set a fixed width for all the image to show and preserve image ratio
        this.imageViewProperties(this.originalFrame, 400);
        this.imageViewProperties(this.maskImage, 200);
        this.imageViewProperties(this.morphImage, 200);

        if(!this.cameraActive){
            this.capture.open(cameraId);

            if(this.capture.isOpened()){
                this.cameraActive=true;

                Runnable frameGrabber=new Runnable() {
                    @Override
                    public void run() {
                        Mat frame=grabFrame(); //mat object is used to store images data of grayscale or color in an n dimensional array
                        Image imageToShow=Utils.mat2Image(frame);
                        updateImageView(originalFrame, imageToShow);

                    }
                };

                this.timer=Executors.newSingleThreadScheduledExecutor();
                this.timer.scheduleAtFixedRate(frameGrabber,0,33,TimeUnit.MILLISECONDS);

                this.start_btn.setText("Stop Camera");
            }
            else{
                System.out.println("Failed to open camera connection...");

            }
        }
        else{

            this.cameraActive=false;
            this.start_btn.setText("Start camera");

            this.stopAcquisition();
        }
    }

    private void imageViewProperties(ImageView image, int dimension) {
        image.setFitWidth(dimension); //set a fixed width for the given image view
        image.setPreserveRatio(true); //preserve the image ratio
    }


    private void updateImageView(ImageView view, Image imageToShow) {
        Utils.onFXThread(view.imageProperty(), imageToShow);
    }



    private Mat findAndDrawBalls(Mat maskedImage, Mat frame) {

        List<MatOfPoint> contours=new ArrayList<>();
        Mat hierarchy=new Mat();

        //find contours
        Imgproc.findContours(maskedImage,contours,hierarchy,Imgproc.RETR_CCOMP,Imgproc.CHAIN_APPROX_SIMPLE);

        //if any contours exist
        if(hierarchy.size().height>0&&hierarchy.size().width>0){
            for(int idx=0; idx>=0; idx=(int) hierarchy.get(0,idx)[0]){
                Imgproc.drawContours(frame,contours,idx,new Scalar(250,0,0));
            }
        }
        return frame;
    }

    private void stopAcquisition(){
        if(this.timer!=null&& !this.timer.isShutdown()){
            try{
                this.timer.shutdown();
                this.timer.awaitTermination(33,TimeUnit.MILLISECONDS);
            }

            catch (InterruptedException e){
                System.err.println("Exception in stopping the frame capture, trying to release the camera now "+e);
            }
        }

        if(this.capture.isOpened()){
            this.capture.release();
        }
    }

    public void setClosed() {
        this.stopAcquisition();
    }*/
}
