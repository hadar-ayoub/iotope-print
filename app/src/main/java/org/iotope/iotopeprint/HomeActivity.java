package org.iotope.iotopeprint;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

public class HomeActivity extends Activity {
    Intent i;
    LinearLayout devoxxLayout, devoxxForKidsLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        devoxxLayout = (LinearLayout) findViewById(R.id.devoxx_layout);
        devoxxForKidsLayout = (LinearLayout) findViewById(R.id.devoxx_for_kids_layout);

        devoxxLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        i =new Intent(getApplicationContext(),DevoxxLabelPrinterActivity.class);
                        startActivity(i);
                    }
                }
        );
        devoxxForKidsLayout.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        i =new Intent(getApplicationContext(),DevoxxForKidsLabelPrinterActivity.class);
                        startActivity(i);
                    }
                }
        );


    }
    public Bitmap getResizeBitmap(Bitmap b, int newWidth, int newHeight){
        int width = b.getWidth();
        int height = b.getHeight();
        float scaleWidth = ((float) newWidth)/width;
        float scaleHeight = ((float) newHeight)/height;
        // Create Matrix for manipulation
        Matrix matrix = new Matrix();
        matrix.postScale(scaleWidth,scaleHeight);

        // recreate new bitmap
        Bitmap resizedBitmap = Bitmap.createBitmap(b, 0,0,width,height,matrix,false);
        b.recycle();
        return resizedBitmap;
    }

}
