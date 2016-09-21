package org.iotope.ipp;

import com.squareup.okhttp.*;
import okio.Buffer;
import okio.Okio;
import okio.Sink;
import okio.Source;
import org.iotope.util.BinUtil;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

/**
 * Created by alexvanboxel on 03/05/15.
 */
public class HttpTest {

    public static final MediaType IPP
            = MediaType.parse("application/ipp");

    OkHttpClient client = new OkHttpClient();

    private void dumpIpp(int skip, Buffer ipp) throws IOException {
        ipp.skip(skip);
        Source x = Okio.source(ipp.inputStream());

        IppParser parser = new IppParser();

        parser.read(Okio.buffer(x));
    }

    @Test
    public void bar() throws IOException {
        List<Buffer> buffers = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("hex06.txt"));
        Buffer buffer1 = buffers.get(0);
        Buffer buffer2 = buffers.get(2);

        buffer1.skip(194);
        buffer2.skip(6);
        int bs1 = (int) buffer1.size() - 2;
        int bs2 = (int) buffer2.size() - 7;
        byte[] send = new byte[bs1 + bs2];
        buffer1.read(send, 0, bs1);
        byte[] bytes = buffer2.readByteArray();
        System.arraycopy(bytes, 0, send, bs1, bs2);
        System.out.println();

    }

    @Test
    public void fooBar() throws IOException {

        IppRoot root = IppRoot.builder().getPrinterAttributes().request(1).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-156d96.local:631/ipp/print")
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
                        .addURI("printer-uri", "ipp://ICON-156d96.local.:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "alexvanboxel")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .add49("document-format", "application/octet-stream")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().getPrinterAttributes().request(3).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-156d96.local.:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "alexvanboxel")
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
                        .addURI("printer-uri", "ipp://ICON-156d96.local.:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "alexvanboxel")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .build()).build();
        System.out.println(send(root).toString());

        root = IppRoot.builder().getJobs().request(5).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-156d96.local:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "alexvanboxel")
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
                        .addURI("printer-uri", "ipp://ICON-156d96.local.:631/ipp/print")
                        .addInt("job-id", 4)
                        .addNameWithoutLanguage("requesting-user-name", "alexvanboxel")
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

//    lwxl.length();
//    lwxl.width();
//    lwxl.escB(0x00);
//    lwxl.esc66();
//
//    lwxl.escB(0x3B);
//    lwxl.escD(2);
//    for (int i = 0; i < 178; i++) {
//      lwxl.sync();
//      buffer2.writeByte(0x03);
//      buffer2.writeByte(0xE0);
//    }
        lwxl.length();
        lwxl.width();
        lwxl.escB(0x00);
        lwxl.esc66();

        for (int i = 0; i < 178; i++) {
            lwxl.escB(i);
            lwxl.escD(2);
            lwxl.sync();
            buffer2.writeByte(0xFF);
            buffer2.writeByte(0xFF);
        }
//        for (int i = 177; i >= 0; i--) {
//            lwxl.escB(i);
//            lwxl.escD(2);
//            lwxl.sync();
//            buffer2.writeByte(0xFF);
//            buffer2.writeByte(0xFF);
//        }

        lwxl.formFeed();

        int bs1 = b1.length;
        int bs2 = (int) buffer2.size();
        byte[] send = new byte[bs1 + bs2];

        byte[] bytes = buffer2.readByteArray();
        System.arraycopy(b1, 0, send, 0, bs1);
        System.arraycopy(bytes, 0, send, bs1, bs2);

        RequestBody requestBody = RequestBody.create(IPP, send);
        Request request = new Request.Builder()
                .url("http://ICON-156d96.local:631/ipp/print")
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
                .url("http://ICON-156d96.local:631/ipp/print")
                .header("Content-Type", "application/ipp")
                .post(requestBody)
                .build();

        Response response = client.newCall(request).execute();

        System.out.println(response.toString());
        IppParser parser = new IppParser();
        return parser.read(response.body().source());
    }
}