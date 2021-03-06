package org.iotope.iotopeprint;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.pdf.PrintedPdfDocument;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import okio.Buffer;
import okio.Okio;
import okio.Sink;

import static android.R.attr.data;
import static com.google.zxing.qrcode.decoder.ErrorCorrectionLevel.L;

public class DevoxxForKidsLabelPrinterActivity extends Activity {

    private static final String url = "http://192.168.1.1:631/ipp/print";
    public static final MediaType IPP
            = MediaType.parse("application/ipp");

    OkHttpClient client = new OkHttpClient();


    private GoogleApiClient client2;
    private HashMap<String,String> guest;
    private ImageView imageView,logo;
    private TextView notifs;
    private Button validate,print;
    private EditText nameTxt, schoolTxt;
    private Spinner cityList;
    private String scanContent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_devoxx_for_kids_label_printer);

        imageView = (ImageView) findViewById(R.id.imageView);
        //logo = (ImageView) findViewById(R.id.logo);
        notifs = (TextView) findViewById(R.id.notifs) ;
        validate = (Button) findViewById(R.id.valide_btn);
        print = (Button) findViewById(R.id.print);
        nameTxt = (EditText) findViewById(R.id.name_kid);
        schoolTxt =(EditText) findViewById(R.id.school_kid);
        cityList = (Spinner) findViewById(R.id.cities);

        setCitiesAdapter(loadJSONFromAsset());

        //Bitmap resizedlogo = getResizeBitmap(BitmapFactory.decodeResource(getResources(), R.drawable.xhub),320,160);
        //logo.setImageBitmap(resizedlogo);


        client2 = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        print.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                print.setEnabled(false);
                new PrintAsyncTask().execute();
            }
        });
        validate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Buffer buffer2 = new Buffer();
                Lwxl lwxl = new Lwxl(buffer2);
                Bitmap bitmap = null;
                try {
                    guest = new HashMap<String, String>();
                    guest.put("full_name",nameTxt.getText().toString());
                    guest.put("school",schoolTxt.getText().toString());
                    guest.put("city",cityList.getSelectedItem().toString());
                    scanContent = guest.get("full_name")+"|"+guest.get("company")+"|"+guest.get("pass");
                    Log.i("Guest's full name", "onClick: "+guest.get("full_name"));
                    Log.i("Guest's school", "onClick: "+guest.get("school"));
                    Log.i("Guest's city", "onClick: "+guest.get("city"));
                    if (nameTxt.getText() == null){
                        print.setEnabled(false);
                    }
                    else{
                        print.setEnabled(true);
                        bitmap = createDoc(lwxl);
                        imageView.setImageBitmap(bitmap);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client2.connect();
        AppIndex.AppIndexApi.start(client2, getIndexApiAction());
    }
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
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client2, getIndexApiAction());
        client2.disconnect();
    }

    // canvas de l'étiquette
    public Bitmap createDoc(Lwxl lwxl) throws IOException   {

        PrintAttributes.MediaSize m = new PrintAttributes.MediaSize(
                "123", "123", 1000, 360
        );
        PrintAttributes.Resolution r = new PrintAttributes.Resolution(
                "123", "123", 300, 300
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



        Paint white = new Paint();
        white.setColor(Color.WHITE);

        Paint black = new Paint();
        black.setColor(Color.BLACK);

        Paint text = new Paint();
        text.setColor(Color.BLACK);

        Paint companyName = new Paint();
        companyName.setColor(Color.BLACK);

        Paint cityText = new Paint();
        cityText.setColor(Color.BLACK);

        Rect rect = new Rect(0, 0, 1000, 360);
        Bitmap original = Bitmap.createBitmap(rect.width(), rect.height(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(original);
        canvas.drawRect(rect, white);

        black.setStrokeWidth(5f);
        canvas.drawLine(20,100,983,100,black);

        // fin  Modifcation ffor ticket
        Bitmap resizedLogoContent = getResizeBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.content_logo_dfork_black),250,60);
        canvas.drawBitmap(resizedLogoContent,505, 30 , null);

        Bitmap resizedLogoHeader = getResizeBitmap(BitmapFactory.decodeResource(getResources(),
                R.drawable.head_logo_dfork_black),225,351);

        canvas.drawBitmap(resizedLogoHeader,740, 0 , null);


        Typeface currentTypeFace =   text.getTypeface();
        Typeface bold = Typeface.create(currentTypeFace, Typeface.BOLD);
        text.setTypeface(bold);
        text.setTextSize(70);
        companyName.setTextSize(48);

        if (isLongerThanNameSize(guest.get("full_name"))){
            String nameData = "";
            String restData = "";
            List<String> listPartName = DivScanContent(guest.get("full_name")," ");
            for (int i = 0; i < listPartName.size(); i++) {
                if (isLongerThanNameSize(nameData+listPartName.get(i))){
                    restData += listPartName.get(i)+" ";
                }
                else {
                    nameData += listPartName.get(i)+ " ";
                }
            }
            canvas.drawText(nameData, 41, 170, text);
            canvas.drawText(restData, 41, 230, text);
            canvas.drawText(guest.get("school"), 58, 290  , companyName);
        }
        else{
            canvas.drawText(guest.get("full_name"), 41, 220, text);
            canvas.drawText(guest.get("school"), 58, 280  , companyName);
        }

        cityText.setTextSize(48);
        canvas.drawText(guest.get("city").toUpperCase(), 20, 80, cityText);

        Bitmap bitmap = original;

        int ooo = 125;
        lwxl.escB(0);
        lwxl.escD(ooo);

        for (int y = 0; y < original.getHeight(); y++) {

            int b8 = 0x00;
            int[] line = new int[1000];
            int length = 0;

            for (int x = 0; x < original.getWidth(); x++) {

                int p = original.getPixel(x, y);
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
        return bitmap;
    }


    // ipp app methodes
    public Bitmap print() throws IOException {

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

        Bitmap bitmap = createDoc(lwxl);
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
        return bitmap;
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

    private class PrintAsyncTask extends AsyncTask<Object, Object, Boolean> {

        @Override
        protected Boolean doInBackground(Object... args) {
            try {
                print();
                return false;
            } catch (IOException e) {
                e.printStackTrace();
                return true;
            }

        }

        @Override
        protected void onPostExecute(Boolean b) {
            if (b){
                notifs.setTextColor(Color.RED);
                notifs.setTextColor(getResources().getColor(R.color.red));
                notifs.setText("Impossible d'imprimer l'étiquette, " +
                        "veuillez vous assurer que vous etes connecté à ICON-XXXXXX");
                print.setEnabled(true);
            }
            else{

                notifs.setTextColor(getResources().getColor(R.color.lightgreen));
                notifs.setText("félicitation ! L'impression s'est bien éfféctué.");
                print.setEnabled(false);
                nameTxt.setText("");
                schoolTxt.setText("");
            }
        }
    }

    // Image decoded to bitMap and resized to to the given argument
    public Bitmap encodeAsBitmap(String str) throws WriterException {
        BitMatrix result;
        try {
            result = new MultiFormatWriter().encode(str,
                    BarcodeFormat.QR_CODE, 360, 360, null);
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
        bitmap.setPixels(pixels, 0, 360, 0, 0, w, h);
        return bitmap;
    } /// end of this method
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

    public void setCitiesAdapter(String dataCities){
        List<String> cities = new ArrayList();
        try {
            JSONObject citiesObject = new JSONObject(dataCities);
            JSONArray citiesarray = new JSONArray(citiesObject.getString("ville"));
            for (int i = 0; i < citiesarray.length(); i++){
                cities.add(citiesarray.getString(i));
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_dropdown_item, cities);
            cityList.setAdapter(adapter);
        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext()," parse  data problem",Toast.LENGTH_LONG).show();
        }
    }
    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getResources().openRawResource
                    (getResources().getIdentifier("villes","raw",getPackageName()));
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }


    // divide QR code scan content method
    public List<String> DivScanContent(String scanContent, String delim){
        List<String> data = new ArrayList();
        StringTokenizer divScanContent = new StringTokenizer(scanContent,delim);
        while (divScanContent.hasMoreElements()) {
            data.add((String) divScanContent.nextElement());
        }
        return data;
    }

    // test lenght full name
    public boolean isLongerThanNameSize(String name){
        if(name.length() > 14){
            return true;
        }
        else{
            return false;
        }
    }

}
