package br.com.opencv.app;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.io.File;
import java.io.FileDescriptor;
import java.io.IOException;

public class MainActivity extends AppCompatActivity {

    private ImageView imageView;
    private int CODIGO_SELECIONAR_IMG = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        boolean opencv = OpenCVLoader.initDebug();
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = (ImageView) findViewById(R.id.imageViewfilter);

        if(!opencv) Toast.makeText(this, "Biblioteca OpenCV não iniciada.", Toast.LENGTH_SHORT).show();
        else Toast.makeText(this, "Biblioteca OpenCV iniciada.", Toast.LENGTH_SHORT).show();

        abri_browser_file_system();

    }

    public void abri_browser_file_system() {

        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");

        startActivityForResult(intent, CODIGO_SELECIONAR_IMG );
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CODIGO_SELECIONAR_IMG && resultCode == RESULT_OK) {
            if (data != null) {
                Uri uri = data.getData();
                Bitmap bitmap = get_bitmap_from_uri(uri);

                if(bitmap != null) imageView.setImageBitmap(bitmap);
                else Toast.makeText(this, "A imagem não pôde ser aberta.", Toast.LENGTH_LONG).show();
            }
        }
    }

    public Bitmap get_bitmap_from_uri(Uri uri){
        try {
            ParcelFileDescriptor parcelFileDescriptor = getContentResolver().openFileDescriptor(uri, "r");
            FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
            Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
            parcelFileDescriptor.close();
            return image;
        } catch (IOException e) {
            return null; }
    }

    public Mat get_mat_from_imageview(){
        Bitmap img_bitmap = ((BitmapDrawable) imageView.getDrawable()).getBitmap();
        Mat img = new Mat();
        Utils.bitmapToMat(img_bitmap, img);
        return img;
    }

    public void replace_filter(View v){
        Mat img = get_mat_from_imageview();

        Imgproc.cvtColor(img, img, Imgproc.COLOR_RGB2BGRA);
        Mat img_result = img.clone();
        Imgproc.Canny(img, img_result, 80, 90);
        //Imgproc.medianBlur(img, img_result, 50);

        Bitmap img_bitmap = Bitmap.createBitmap( img_result.cols(), img_result.rows(), Bitmap.Config.ARGB_8888);
        Utils.matToBitmap(img_result, img_bitmap);
        imageView.setImageBitmap(img_bitmap);
    }

}
