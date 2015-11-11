package org.iotope.iotopeprint;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.pdf.PdfDocument;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

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
import java.io.IOException;

import okio.Buffer;
import okio.Okio;
import okio.Sink;

public class MainActivity extends AppCompatActivity {

    private static final String url = "http://172.19.0.218:631/ipp/print";

    public static final MediaType IPP
            = MediaType.parse("application/ipp");

    OkHttpClient client = new OkHttpClient();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btn = (Button) findViewById(R.id.print);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AsyncTask<Void, Void, Void>() {

                    @Override
                    protected Void doInBackground(Void... params) {
                        try {
                            print();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return null;
                    }
                }.execute();


            }
        });
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


    public void createDoc(Lwxl lwxl) throws IOException {

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


    public ParcelFileDescriptor openFile(File file) throws FileNotFoundException {
        if (file.exists()) {
            ParcelFileDescriptor parcel = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
            return parcel;
        } else {
            return null;
        }
    }

    public void print() throws IOException {

        IppRoot root = IppRoot.builder().getPrinterAttributes().request(1).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-000000.local.:631/ipp/print")
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
                        .addURI("printer-uri", "ipp://ICON-000000.local.:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .add49("document-format", "application/octet-stream")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().getPrinterAttributes().request(3).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-000000.local.:631/ipp/print")
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
                        .addURI("printer-uri", "ipp://ICON-000000.local.:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "username")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().getJobs().request(5).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-000000.local.:631/ipp/print")
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
                        .addURI("printer-uri", "ipp://ICON-000000.local.:631/ipp/print")
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


}
