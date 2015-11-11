package org.iotope.ipp;

import okio.Buffer;
import okio.Okio;
import okio.Sink;
import org.iotope.util.BinUtil;
import org.junit.Test;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;


/**
 * https://github.com/jfuchs/receipt-printy/blob/master/pbm2lwxl.c
 * http://ascii.cl/
 * <p/>
 * ESC ASCII 0x00 0x00
 */

public class IppWriterTest {

    @Test
    public void constructHex01() throws IOException {
        // (Get-Printer-Attributes,1 [G operation-attributes-tag[(A(47) {attributes-charset=utf-8}),(A(48) {attributes-natural-language=en-us}),(A(45) {printer-uri=ipp://ICON-1488a2.local.:631/ipp/print}),(A(44) {requested-attributes=compression-supported}),(A(44) {=copies-supported}),(A(44) {=cups-version}),(A(44) {=document-format-supported}),(A(44) {=marker-colors}),(A(44) {=marker-high-levels}),(A(44) {=marker-levels}),(A(44) {=marker-low-levels}),(A(44) {=marker-message}),(A(44) {=marker-names}),(A(44) {=marker-types}),(A(44) {=media-col-supported}),(A(44) {=multiple-document-handling-supported}),(A(44) {=operations-supported}),(A(44) {=print-color-mode-supported}),(A(44) {=printer-alert}),(A(44) {=printer-alert-description}),(A(44) {=printer-is-accepting-jobs}),(A(44) {=printer-state}),(A(44) {=printer-state-message}),(A(44) {=printer-state-reasons}),]])

        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("hex01.txt"));

        IppRoot root = IppRoot.builder().getPrinterAttributes().request(1).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-1488a2.local.:631/ipp/print")
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
        assertConstruct(list.get(0), 187, root);
    }


    @Test
    public void readHex02() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("hex02.txt"));

        IppRoot root = IppRoot.builder().validateJob().request(2).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-1488a2.local.:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "alexvanboxel")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .add49("document-format", "application/octet-stream")
                        .build()).build();


        assertConstruct(list.get(0), 187, root);
    }

    @Test
    public void readHex03() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("hex03.txt"));

        IppRoot root = IppRoot.builder().getPrinterAttributes().request(3).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-1488a2.local.:631/ipp/print")
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


        assertConstruct(list.get(0), 187, root);
    }

    @Test
    public void readHex04() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("hex04.txt"));

        IppRoot root = IppRoot.builder().createJob().request(4).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-1488a2.local.:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "alexvanboxel")
                        .addNameWithoutLanguage("job-name", "Blank Landscape Card")
                        .build()).build();


        assertConstruct(list.get(0), 187, root);
    }

    @Test
    public void readHex05() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("hex05.txt"));

        IppRoot root = IppRoot.builder().getJobs().request(5).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-1488a2.local.:631/ipp/print")
                        .addNameWithoutLanguage("requesting-user-name", "alexvanboxel")
                        .addKeyword("requested-attributes", "job-id")
                        .addKeyword("job-impressions-completed")
                        .addKeyword("job-media-sheets-completed")
                        .addKeyword("job-name")
                        .addKeyword("job-originating-user-name")
                        .addKeyword("job-state")
                        .addKeyword("job-state-reasons")
                        .build()).build();


        assertConstruct(list.get(0), 187, root);
    }


    @Test
    public void readHex07() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("hex07.txt"));

        IppRoot root = IppRoot.builder().getPrinterAttributes().request(7).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-1488a2.local.:631/ipp/print")
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

        assertConstruct(list.get(0), 187, root);
    }

    @Test
    public void readHex09() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("hex09.txt"));

        IppRoot root = IppRoot.builder().getJobAttributes().request(8).add(
                IppAttributeGroup.builder().tag(1)
                        .addChar("attributes-charset", "utf-8")
                        .addNaturalChar("attributes-natural-language", "en-us")
                        .addURI("printer-uri", "ipp://ICON-1488a2.local.:631/ipp/print")
                        .addInt("job-id", 4)
                        .addNameWithoutLanguage("requesting-user-name", "alexvanboxel")
                        .addKeyword("requested-attributes", "job-id")
                        .addKeyword("job-impressions-completed")
                        .addKeyword("job-media-sheets-completed")
                        .addKeyword("job-name")
                        .addKeyword("job-originating-user-name")
                        .addKeyword("job-state")
                        .addKeyword("job-state-reasons")
                        .build()).build();

        assertConstruct(list.get(0), 187, root);
    }

    private void assertConstruct(Buffer original, int skip, IppRoot root) throws IOException {
        original.skip(skip);
        byte[] source = original.readByteArray();
        IppParser parser = new IppParser();

        System.out.println(root.toString());

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Sink sink = Okio.sink(stream);
        IppWriter writer = new IppWriter();
        writer.write(sink, root);
        byte[] bytes = stream.toByteArray();

        assertThat(bytes).isEqualTo(source);
    }

}
