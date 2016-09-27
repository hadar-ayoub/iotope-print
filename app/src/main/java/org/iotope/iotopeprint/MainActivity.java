package org.iotope.iotopeprint;

import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.pdf.PdfDocument;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.google.zxing.qrcode.encoder.QRCode;
import com.squareup.okhttp.MediaType;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.iotope.ipp.IppAttributeGroup;
import org.iotope.ipp.IppParser;
import org.iotope.ipp.IppRoot;
import org.iotope.ipp.IppWriter;
import org.iotope.ipp.Lwxl;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Hashtable;

import okio.Buffer;
import okio.Okio;
import okio.Sink;


public class MainActivity extends AppCompatActivity{

    private static final String url = "http://192.168.1.1:631/ipp/print";

    public static final MediaType IPP
            = MediaType.parse("application/ipp");

    OkHttpClient client = new OkHttpClient();
    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client2;
    private TextView formatTxt, contentTxt;
    private Button btn, scan;
    private String scanContent, scanFormat;
    ImageView imageView, qrView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Resources res = getResources();
        imageView = (ImageView) findViewById(R.id.imageView);
        formatTxt = (TextView) findViewById(R.id.scan_format) ;
        contentTxt = (TextView) findViewById(R.id.scan_content) ;

        btn = (Button) findViewById(R.id.print);
        scan = (Button) findViewById(R.id.scan);
        btn.setEnabled(true);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"Imprimer l'étiquette",Toast.LENGTH_LONG).show();
                try {
                    Buffer buffer2 = new Buffer();
                    Lwxl lwxl = new Lwxl(buffer2);
                    lwxl.start();

                    lwxl.length();
                    lwxl.width();
                    lwxl.escB(0x00);
                    lwxl.esc66();

                    Bitmap bitmap = createDoc(lwxl);
                    imageView.setImageBitmap(bitmap);

                    lwxl.formFeed();



                } catch (IOException e) {
                    e.printStackTrace();
                }

                new AsyncTask<Void, Void, Void>() {
                    @Override
                    protected Void doInBackground(Void... params) {
                       /* try {
                            //printInit();
                            print();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }*/
                        return null;
                    }
                }.execute();
            }
        });

        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
                        scanIntegrator.initiateScan();
                        return null;
                    }
                }.execute();
            }
        });
        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
    }

    public void onClick(View v){
//respond to clicks
        if(v.getId()==R.id.scan){
            IntentIntegrator scanIntegrator = new IntentIntegrator(MainActivity.this);
            scanIntegrator.initiateScan();
        }
        if(v.getId()==R.id.print){
            Toast.makeText(getApplicationContext(),"Imprimer l'étiquette",Toast.LENGTH_LONG).show();
        }
    }
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        //retrieve scan result
        IntentResult scanningResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, intent);
        if (scanningResult != null) {

//we have a result
            scanContent = scanningResult.getContents();
            scanFormat = scanningResult.getFormatName();
            formatTxt.setText("FORMAT: " + scanFormat);
            contentTxt.setText("CONTENT: " + scanContent);

        }
        else{
            Toast toast = Toast.makeText(getApplicationContext(),
                    "No scan data received!", Toast.LENGTH_SHORT);
            toast.show();
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    // canvas de l'étiquette
    public Bitmap createDoc(Lwxl lwxl) throws IOException {

        PrintAttributes.MediaSize m = new PrintAttributes.MediaSize(
                "123", "123", 2000, 2000
        );
        PrintAttributes.Resolution r = new PrintAttributes.Resolution(
                "123", "123", 72, 72
        );
        PrintAttributes.Margins g = new PrintAttributes.Margins(
                0, 0, 0, 0
        );

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                .setMediaSize(m)
                .setResolution(r)
                .setMinMargins(g)
                .build();

        PrintedPdfDocument document = new PrintedPdfDocument(this,
                printAttributes);

        // start a page
        PdfDocument.Page page = document.startPage(0);

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint white = new Paint();
        white.setColor(Color.WHITE);

        Paint black = new Paint();
        black.setColor(Color.BLACK);

        Paint text = new Paint();
        text.setColor(Color.BLACK);

        Rect rect = new Rect(0, 0, 1000, 500);
        Bitmap original = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(original);
        canvas.drawRect(rect, white);



        // fin  Modifcation ffor ticket

        //  Drawable d = getDrawable(R.drawable.badge_header);
        //  d.setBounds(0, 0, 0, 0);
        // d.draw(canvas);

        // fin  Modifcation ffor ticket

        Resources res = getResources();
        Bitmap header = BitmapFactory.decodeResource(res, R.drawable.badge_header);
        canvas.drawBitmap(header,0, 0 , null);

        if (scanContent != null){
            try {
                Bitmap QRimage = encodeAsBitmap(scanContent);
                canvas.drawBitmap(QRimage,650,200,null);


            } catch (WriterException e) {
                e.printStackTrace();
            }
        }

        text.setTextSize(80);
        canvas.drawText("Ayoub Hadar", 50, 350, text);

        text.setTextSize(48);
        canvas.drawText("XHUB", 70, 420, text);

        text.setTextSize(72);
        text.setTextAlign(Paint.Align.RIGHT);
        canvas.drawText("TRINGA", 970, 150, text);

        Bitmap bitmap = original;

        int ooo = 125;
        lwxl.escB(0);
        lwxl.escD(ooo);

        for (int y = 0; y < bitmap.getHeight(); y++) {

            int b8 = 0x00;
            int[] line = new int[256];
            int length = 0;

            for (int x = 0; x < bitmap.getWidth(); x++) {

                int p = bitmap.getPixel(x, y);
                int red = Color.red(p);
                int blue = Color.blue(p);
                int green = Color.green(p);
                if (red == 0 && blue == 0 && green == 0) {
                    b8 = b8 | (0x01 << (7 - (x % 8)));
                }

                if ((x % 8) == 7) {
                    line[length++] = b8;
                    // write byte
                    b8 = 0;
                }
            }

            lwxl.sync();
            for (int i = 0; i < ooo; i++) {
                lwxl.writeByte(line[i]);
            }
        }
        //saveImageToInternalStorage(bitmap);
/*        try {
            qrView.setImageBitmap(encodeAsBitmap(scanContent));
        } catch (WriterException e) {
            e.printStackTrace();
        }
*/        System.out.println();
        return bitmap;
    }

    public ParcelFileDescriptor openFile(File file) throws FileNotFoundException {
        if (file.exists()) {
            ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            System.out.println(parcel);

            return parcel;
        } else {
            return null;
        }
    }

    // ipp app methodes
    public void print() throws IOException {

        IppRoot root = IppRoot.builder().getPrinterAttributes().request(1).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addKeyword("requested-attributes", "compression-supported")
                        .addKeyword("copies-supported")
                        .addKeyword("cups-version")
                        .addKeyword("document-format-supported")
                        .addKeyword("marker-colors")
                        .addKeyword("marker-high-levels")
                        .addKeyword("marker-levels")
                        .addKeyword("marker-low-levels")
                        .addKeyword("marker-message")
                        .addKeyword("marker-names")
                        .addKeyword("marker-types")
                        .addKeyword("media-col-supported")
                        .addKeyword("multiple-document-handling-supported")
                        .addKeyword("operations-supported")
                        .addKeyword("print-color-mode-supported")
                        .addKeyword("printer-alert")
                        .addKeyword("printer-alert-description")
                        .addKeyword("printer-is-accepting-jobs")
                        .addKeyword("printer-state")
                        .addKeyword("printer-state-message")
                        .addKeyword("printer-state-reasons")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().validateJob().request(2).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .add49("document-format", "application/octet-stream")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().getPrinterAttributes().request(3).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addKeyword("requested-attributes", "compression-supported")
                        .addKeyword("copies-supported")
                        .addKeyword("cups-version")
                        .addKeyword("document-format-supported")
                        .addKeyword("marker-colors")
                        .addKeyword("marker-high-levels")
                        .addKeyword("marker-levels")
                        .addKeyword("marker-low-levels")
                        .addKeyword("marker-message")
                        .addKeyword("marker-names")
                        .addKeyword("marker-types")
                        .addKeyword("media-col-supported")
                        .addKeyword("multiple-document-handling-supported")
                        .addKeyword("operations-supported")
                        .addKeyword("print-color-mode-supported")
                        .addKeyword("printer-alert")
                        .addKeyword("printer-alert-description")
                        .addKeyword("printer-is-accepting-jobs")
                        .addKeyword("printer-state")
                        .addKeyword("printer-state-message")
                        .addKeyword("printer-state-reasons")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().createJob().request(4).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().getJobs().request(5).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addKeyword("requested-attributes", "job-id")
                        .addKeyword("job-impressions-completed")
                        .addKeyword("job-media-sheets-completed")
                        .addKeyword("job-name")
                        .addKeyword("job-originating-user-name")
                        .addKeyword("job-state")
                        .addKeyword("job-state-reasons")
                        .build()).build();
        System.out.println(send(root).toString());

        // **************************>

        root = IppRoot.builder().sendDocument().request(8).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addInt("job-id", 4)
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addBoolean("last-document", true)
                        .add49("document-format", "application/octet-stream")
                        .build()).build();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Sink sink = Okio.sink(stream);
        IppWriter writer = new IppWriter();
        writer.write(sink, root);
        byte[] b1 = stream.toByteArray();

        Buffer buffer2 = new Buffer();
        Lwxl lwxl = new Lwxl(buffer2);
        lwxl.start();

        lwxl.length();
        lwxl.width();
        lwxl.escB(0x00);
        lwxl.esc66();

        createDoc(lwxl);
        lwxl.formFeed();

        int bs1 = b1.length;
        int bs2 = (int) buffer2.size();
        byte[] send = new byte[bs1 + bs2];

        byte[] bytes = buffer2.readByteArray();
        System.arraycopy(b1, 0, send, 0, bs1);
        System.arraycopy(bytes, 0, send, bs1, bs2);

        RequestBody requestBody = RequestBody.create(IPP, send);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/ipp")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.toString());
        IppParser parser = new IppParser();
        System.out.println(parser.read(response.body().source()));
    }

    private IppRoot send(IppRoot root) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Sink sink = Okio.sink(stream);
        IppWriter writer = new IppWriter();
        writer.write(sink, root);
        RequestBody requestBody = RequestBody.create(IPP, stream.toByteArray());

        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/ipp")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.toString());
        IppParser parser = new IppParser();
        return parser.read(response.body().source());
    }

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }



    public void save(Bitmap bitmap){
        String root = Environment.DIRECTORY_DOWNLOADS;
        System.out.println(root);
        File myDir = new File(root);
        myDir.mkdirs();

        String fname = "save.jpg";
        //if (file.exists ()) file.delete();

        File file = new File (myDir, fname);
        System.out.println(file.getPath());
        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public boolean saveImageToInternalStorage(Bitmap image) {


        try {
            OutputStream fOut = null;
            File file = new File(Environment.getExternalStorageDirectory(),"test.jpg");
            fOut = new FileOutputStream(file);
            System.out.println(file.getPath());
            image.compress(Bitmap.CompressFormat.JPEG, 85, fOut);
            fOut.flush();
            fOut.close();
            MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());
        } catch (IOException e) {
            e.printStackTrace();
        }


        return true;
    }

    // this is method call from on create and return bitmap image of QRCode.
    public Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 300, 300, null);
        } catch (IllegalArgumentException iae) {
            // Unsupported format
            return null;
        }
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offset = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offset + x] = result.get(x, y) ? getResources().getColor(R.color.black):getResources().getColor(R.color.white);
            }
        }
        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, 300, 0, 0, w, h);
        return bitmap;
    } /// end of this method

    // Initial methodes of creating end printing Bitmap Canvas
    public void createDocInit(Lwxl lwxl) throws IOException {

        PrintAttributes.MediaSize m = new PrintAttributes.MediaSize(
                "123", "123", 2000, 2000
        );
        PrintAttributes.Resolution r = new PrintAttributes.Resolution(
                "123", "123", 72, 72
        );
        PrintAttributes.Margins g = new PrintAttributes.Margins(
                0, 0, 0, 0
        );

        PrintAttributes printAttributes = new PrintAttributes.Builder()
                .setColorMode(PrintAttributes.COLOR_MODE_MONOCHROME)
                .setMediaSize(m)
                .setResolution(r)
                .setMinMargins(g)
                .build();

        PrintedPdfDocument document = new PrintedPdfDocument(this,
                printAttributes);

        // start a page
        PdfDocument.Page page = document.startPage(0);

        int titleBaseLine = 72;
        int leftMargin = 54;

        Paint white = new Paint();
        white.setColor(Color.WHITE);

        Paint black = new Paint();
        black.setColor(Color.BLACK);

        Paint text = new Paint();
        text.setColor(Color.BLACK);

        Rect rect = new Rect(0, 0, 2000, 501);
        Bitmap original = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(original);
        canvas.drawRect(rect, white);

        canvas.drawLine(0, 0, 2000, 0, black);
        canvas.drawLine(0, 0, 0, 500, black);

        for (int lx = 0; lx <= 2000; lx += 80) {
            canvas.drawLine(lx, 0, lx, 25, black);
        }
        for (int lx = 40; lx <= 2000; lx += 80) {
            canvas.drawLine(lx, 25, lx, 50, black);
        }
        for (int lx = 20; lx <= 2000; lx += 40) {
            canvas.drawLine(lx, 50, lx, 100, black);
        }

        for (int lx = 0; lx <= 2000; lx += 100) {
            canvas.drawLine(lx, 100, lx, 500, black);
        }
        for (int ly = 0; ly <= 500; ly += 100) {
            canvas.drawLine(0, ly, 2000, ly, black);
        }

        canvas.drawLine(0, 0, 255, 255, black);

        text.setTextSize(42);
        canvas.drawText("Test Title", leftMargin, titleBaseLine + 100, text);

        text.setTextSize(32);
        canvas.drawText("Test paragraph", leftMargin, titleBaseLine + 150, text);

        Bitmap bitmap = original;

        int ooo = 125;
        lwxl.escB(0);
        lwxl.escD(ooo);

        for (int y = 0; y < bitmap.getHeight(); y++) {

            int b8 = 0x00;
            int[] line = new int[256];
            int length = 0;

            for (int x = 0; x < bitmap.getWidth(); x++) {

                int p = bitmap.getPixel(x, y);
                int red = Color.red(p);
                int blue = Color.blue(p);
                int green = Color.green(p);
                if (red == 0 && blue == 0 && green == 0) {
                    b8 = b8 | (0x01 << (7 - (x % 8)));
                }

                if ((x % 8) == 7) {
                    line[length++] = b8;
                    // write byte
                    b8 = 0;
                }


            }

            lwxl.sync();
            for (int i = 0; i < ooo; i++) {
                lwxl.writeByte(line[i]);
            }
        }

        System.out.println();


    }
    public void printInit() throws IOException {

        IppRoot root = IppRoot.builder().getPrinterAttributes().request(1).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addKeyword("requested-attributes", "compression-supported")
                        .addKeyword("copies-supported")
                        .addKeyword("cups-version")
                        .addKeyword("document-format-supported")
                        .addKeyword("marker-colors")
                        .addKeyword("marker-high-levels")
                        .addKeyword("marker-levels")
                        .addKeyword("marker-low-levels")
                        .addKeyword("marker-message")
                        .addKeyword("marker-names")
                        .addKeyword("marker-types")
                        .addKeyword("media-col-supported")
                        .addKeyword("multiple-document-handling-supported")
                        .addKeyword("operations-supported")
                        .addKeyword("print-color-mode-supported")
                        .addKeyword("printer-alert")
                        .addKeyword("printer-alert-description")
                        .addKeyword("printer-is-accepting-jobs")
                        .addKeyword("printer-state")
                        .addKeyword("printer-state-message")
                        .addKeyword("printer-state-reasons")
                        .build()).build();
        System.out.println(send(root).toString());


        root = IppRoot.builder().validateJob().request(2).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .add49("document-format", "application/octet-stream")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().getPrinterAttributes().request(3).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addKeyword("requested-attributes", "compression-supported")
                        .addKeyword("copies-supported")
                        .addKeyword("cups-version")
                        .addKeyword("document-format-supported")
                        .addKeyword("marker-colors")
                        .addKeyword("marker-high-levels")
                        .addKeyword("marker-levels")
                        .addKeyword("marker-low-levels")
                        .addKeyword("marker-message")
                        .addKeyword("marker-names")
                        .addKeyword("marker-types")
                        .addKeyword("media-col-supported")
                        .addKeyword("multiple-document-handling-supported")
                        .addKeyword("operations-supported")
                        .addKeyword("print-color-mode-supported")
                        .addKeyword("printer-alert")
                        .addKeyword("printer-alert-description")
                        .addKeyword("printer-is-accepting-jobs")
                        .addKeyword("printer-state")
                        .addKeyword("printer-state-message")
                        .addKeyword("printer-state-reasons")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().createJob().request(4).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().getJobs().request(5).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addKeyword("requested-attributes", "job-id")
                        .addKeyword("job-impressions-completed")
                        .addKeyword("job-media-sheets-completed")
                        .addKeyword("job-name")
                        .addKeyword("job-originating-user-name")
                        .addKeyword("job-state")
                        .addKeyword("job-state-reasons")
                        .build()).build();
        System.out.println(send(root).toString());

        // **************************>

        root = IppRoot.builder().sendDocument().request(8).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://192.168.1.1:631/ipp/print")
                        .addInt("job-id", 4)
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addBoolean("last-document", true)
                        .add49("document-format", "application/octet-stream")
                        .build()).build();

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Sink sink = Okio.sink(stream);
        IppWriter writer = new IppWriter();
        writer.write(sink, root);
        byte[] b1 = stream.toByteArray();


        Buffer buffer2 = new Buffer();
        Lwxl lwxl = new Lwxl(buffer2);
        lwxl.start();

        lwxl.length();
        lwxl.width();
        lwxl.escB(0x00);
        lwxl.esc66();

        createDocInit(lwxl);
        lwxl.formFeed();

        int bs1 = b1.length;
        int bs2 = (int) buffer2.size();
        byte[] send = new byte[bs1 + bs2];

        byte[] bytes = buffer2.readByteArray();
        System.arraycopy(b1, 0, send, 0, bs1);
        System.arraycopy(bytes, 0, send, bs1, bs2);

        RequestBody requestBody = RequestBody.create(IPP, send);
        Request request = new Request.Builder()
                .url(url)
                .header("Content-Type", "application/ipp")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.toString());
        IppParser parser = new IppParser();
        System.out.println(parser.read(response.body().source()));
    }
}
