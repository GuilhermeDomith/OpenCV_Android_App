package br.com.opencv.app;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.objdetect.CascadeClassifier;
import org.opencv.objdetect.Objdetect;

import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends Activity {


    private ImageView imageView;
    private Bitmap imgBitmap;
    private SeekBar seekBar;

    private String opcaoOpenCV = "";
    private int intensidade = 7;
    private final int CODIGO_SELECIONAR_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean opencv = OpenCVLoader.initDebug();

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.image_view_filter);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        seekBar.setProgress(this.intensidade);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                MainActivity.this.intensidade = progress;

                if (MainActivity.this.imgBitmap != null)
                    manipularImagem(null);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {}

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {}
        });


        if(!opencv)
            Toast.makeText(this, "Biblioteca OpenCV não iniciada.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(this, "Biblioteca OpenCV iniciada.", Toast.LENGTH_SHORT).show();
    }

    public void abrirBrowserFileSystem(View v){
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, CODIGO_SELECIONAR_IMG );
    }

    public void salvarImagem(View v){

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_SELECIONAR_IMG && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                this.imgBitmap = bitmapFromUri(uri);

                if(this.imgBitmap != null) imageView.setImageBitmap(this.imgBitmap);
                else Toast.makeText(this, "A imagem não pôde ser aberta.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public Bitmap bitmapFromUri(Uri uri){
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) { return null; }
    }

    private Bitmap matToBitmap(Mat img) {
        Bitmap bitmap = Bitmap.createBitmap( img.cols(), img.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img, bitmap);
        return bitmap;
    }

    public Mat bitmapToMat(Bitmap img){
        Mat mat = new Mat();
        Utils.bitmapToMat(img, mat);
        return mat;
    }

    public void manipularImagem(View btn){
        if(this.imgBitmap == null) return;

        if(btn != null)
            this.opcaoOpenCV = ((Button) btn).getText().toString().toLowerCase();

        switch (this.opcaoOpenCV){
            case "canny": canny();
                break;
            case "cvt color e crop": convertColor();
                break;
            case "gaussian blur": gaussianBlur();
                break;
            case "median blur": medianBlur();
                break;
            case "dilate": dilate();
                break;
            case "sepia": sepia();
                break;
        }

    }

    public void canny(){
        Mat img = bitmapToMat(this.imgBitmap);
        Mat img_result = new Mat();

        /** - threshold1: pixel com intensidade de gradiente abaixo deste valor nao será aceito como Edge
         *  - threshold2: pixel acima deste valor será aceito como Edge
         *  - Pixel entre os valores será aceito como Edge se estiver conectado a outro pixel Edge. */
        int threshold1 = 10 * this.intensidade;
        int threshold2 = 10 * this.intensidade;

        Imgproc.cvtColor(img, img, Imgproc.COLOR_BGR2GRAY);
        Imgproc.Canny(img, img_result, threshold1, threshold2);

        Bitmap bitmap_result = matToBitmap(img_result);
        imageView.setImageBitmap(bitmap_result);
    }

    public void medianBlur(){
        Mat img = bitmapToMat(this.imgBitmap);
        Mat img_result = img.clone();

        // ksize deve ser ímpar e maior que 1.
        int ksize = (2 * this.intensidade) + 1 ;
        Imgproc.medianBlur(img, img_result, ksize);

        Bitmap bitmap_result = matToBitmap(img_result);
        imageView.setImageBitmap(bitmap_result);
    }

    public void sepia(){
        Mat img = bitmapToMat(this.imgBitmap);
        Mat img_result = new Mat();

        Mat kernel = new Mat(4, 4, CvType.CV_32F);

        int d = this.intensidade;

        kernel.put(0, 0, /* R */0.189f*d, 0.769f, 0.393f, 0f);
        kernel.put(1, 0, /* G */0.168f*d, 0.686f, 0.349f, 0f);
        kernel.put(2, 0, /* B */0.131f*d, 0.534f, 0.272f, 0f);
        kernel.put(3, 0, /* A */0.000f, 0.000f, 0.000f, 1f);


        // Imagem deve ser RGBA como o Kernel.
        Imgproc.cvtColor(img, img_result, Imgproc.COLOR_BGR2BGRA);
        Core.transform(img_result, img_result, kernel);

        Bitmap bitmap_result = matToBitmap(img_result);
        imageView.setImageBitmap(bitmap_result);
    }

    public void gaussianBlur(){
        Mat img = bitmapToMat(this.imgBitmap);
        Mat img_result = new Mat();

        int width = (10 * this.intensidade) + 1;
        int height = (10 * this.intensidade) + 1;

        /**
         * Width e Height devem ser ímpar e maior ou igual a 0;
         */
        Imgproc.GaussianBlur(img, img_result, new Size(width, height), 7);

        Bitmap bitmap_result = matToBitmap(img_result);
        imageView.setImageBitmap(bitmap_result);
    }

    public void dilate(){
        Mat img = bitmapToMat(this.imgBitmap);
        Mat img_result = new Mat();

        Mat one = Mat.ones(this.intensidade * 2, this.intensidade * 2, CvType.CV_32F);
        // Aplicar filtro máximo utilizando a matriz one
        Imgproc.dilate(img, img_result, one);

        Bitmap bitmap_result = matToBitmap(img_result);
        imageView.setImageBitmap(bitmap_result);
    }

    public void convertColor(){
        Mat img = bitmapToMat(this.imgBitmap);
        Mat img_result = img.clone();

        switch (this.intensidade){
            case 0:
                break;
            case 1:
                Imgproc.cvtColor(img, img_result, Imgproc.COLOR_RGB2BGR);
                break;
            case 2:
                Imgproc.cvtColor(img, img_result, Imgproc.COLOR_RGB2Luv);
                break;
            case 3:
                Imgproc.cvtColor(img, img_result, Imgproc.COLOR_RGB2HSV);
                break;
            case 4:
                Imgproc.cvtColor(img, img_result, Imgproc.COLOR_RGB2XYZ);
                break;
            case 5:
                Imgproc.cvtColor(img, img_result, Imgproc.COLOR_RGB2HLS);
                break;
            case 6:
                Imgproc.cvtColor(img, img_result, Imgproc.COLOR_RGB2HLS_FULL);
                break;
            case 7:
                Imgproc.cvtColor(img, img_result, Imgproc.COLOR_BGR2GRAY);
                Imgproc.equalizeHist(img_result, img_result);
                break;
            case 8:
                Imgproc.cvtColor(img, img_result, Imgproc.COLOR_RGB2GRAY);
                break;
            case 9:
                Imgproc.cvtColor(img, img_result, Imgproc.COLOR_RGB2Lab);
                break;
            case 10:

                int width_crop = Math.round(img.width() * 0.8f);
                int height_crop = Math.round(img.height() * 0.8f);

                Integer x = (img.width() - width_crop) / 2;
                Integer y = (img.height() - height_crop) / 2;

                Rect rectCrop = new Rect(x, y, width_crop, height_crop);
                img_result = new Mat(img, rectCrop);
                break;
        }

        Bitmap bitmap_result = matToBitmap(img_result);
        imageView.setImageBitmap(bitmap_result);
    }

}
