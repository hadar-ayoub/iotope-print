package org.iotope.ipp;

import okio.Buffer;
import okio.Okio;
import okio.Sink;
import org.iotope.util.BinUtil;
import org.junit.Test;

import java.io.ByteArrayInputStream;
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

public class WireshartTest {


    @Test
    public void ippHex01() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("resources/hex01.txt"));
        assertParseAndWrite(187, list.get(0));
        assertParseAndWrite(224, list.get(1));
    }


    @Test
    public void ippHex02() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("resources/hex02.txt"));
        assertParseAndWrite(187, list.get(0));
        assertParseAndWrite(223, list.get(1));
    }

    @Test
    public void ippHex03() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("resources/hex03.txt"));
        assertParseAndWrite(187, list.get(0));
        assertParseAndWrite(224, list.get(1));
    }

    @Test
    public void ippHex04() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("resources/hex04.txt"));
        assertParseAndWrite(187, list.get(0));
        assertParseAndWrite(224, list.get(1));
    }

    @Test
    public void ippHex05() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("resources/hex05.txt"));
        assertParseAndWrite(187, list.get(0));
        assertParseAndWrite(224, list.get(1));
    }


    @Test
    public void constructHex06() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("resources/hex06b.txt"));
        assertParseAndWrite(198, list.get(0));

//        System.out.println("hex06");
//        List<Buffer> buffers = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("hex06.txt"));
////    Buffer buffer1 = buffers.get(0);
////    Buffer buffer2 = buffers.get(2);
////
////    buffer1.skip(198);
////    buffer2.skip(6);
////    int bs1 = (int) buffer1.size() - 2;
////    int bs2 = (int) buffer2.size() - 7;
////    byte[] send = new byte[bs1 + bs2];
////    buffer1.read(send, 0, bs1);
////    byte[] bytes = buffer2.readByteArray();
////    System.arraycopy(bytes, 0, send, bs1, bs2);
//        Buffer buffer1 = buffers.get(0);
//        buffer1.skip(198);
//
    }


    @Test
    public void ippHex07() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("resources/hex07.txt"));
        assertParseAndWrite(187, list.get(0));
        assertParseAndWrite(224, list.get(1));
    }

    @Test
    public void ippHex09() throws IOException {
        List<Buffer> list = BinUtil.wiresharkToBuffers(getClass().getResourceAsStream("resources/hex09.txt"));
        assertParseAndWrite(187, list.get(0));
        assertParseAndWrite(224, list.get(1));
    }

    private void assertParseAndWrite(int skip, Buffer original) throws IOException {
        original.skip(skip);
        byte[] source = original.readByteArray();
        IppParser parser = new IppParser();

        IppRoot root = parser.read(Okio.source(new ByteArrayInputStream(source)));
        System.out.println(root.toString());
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        Sink sink = Okio.sink(stream);
        IppWriter writer = new IppWriter();
        writer.write(sink, root);
        byte[] bytes = stream.toByteArray();

        assertThat(bytes).isEqualTo(source);
    }
}
