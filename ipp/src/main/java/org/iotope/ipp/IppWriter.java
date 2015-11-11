package org.iotope.ipp;

import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by alexvanboxel on 03/05/15.
 */
public class IppWriter {

    IppRoot.Builder root;
    IppAttributeGroup.Builder group;

    /**
     * https://tools.ietf.org/html/rfc2910
     * 3.5.1 Delimiter Tags
     *
     * @throws IOException
     */
    public void write(Sink in, IppRoot root) throws IOException {
        BufferedSink sink = Okio.buffer(in);
        sink.writeByte(root.versionMajor)
                .writeByte(root.versionMinor)
                .writeShort(root.operation)
                .writeInt(root.sequence);

        for (IppAttributeGroup value : root.values) {
            writeGroup(sink, value);
        }
        sink.writeByte(0x03);
        sink.flush();
    }

    private void writeGroup(BufferedSink sink, IppAttributeGroup group) throws IOException {
        sink.writeByte(group.tag);
        for (IppAttributeValue value : group.values) {
            writeValue(sink, value);
        }

    }

    private void writeValue(BufferedSink sink, IppAttributeValue value) throws IOException {
        sink.writeByte(value.tag);
        sink.writeShort(value.name.length());
        sink.writeString(value.name, Charset.forName("ASCII"));

        switch (value.tag) {
            case 0x41: // textWithoutLanguage
            case 0x42: // nameWithoutLanguage
            case 0X44: // keyword
            case 0x45: // uri
            case 0x47: // char
            case 0x48: // natural char
            case 0x49:
                writeTextValue(sink, value.value);
                break;

            case 0x33: // rangeOfInteger
                writeRangeOfIntegers(sink, value.value);
                break;
            case 0x21: // integer
                writeInteger(sink, value.value);
                break;
            case 0x22: // boolean
                writeBoolean(sink, value.value);
                break;
            case 0x23: // enum
                writeEnum(sink, value.value);
                break;

            default:
                throw new RuntimeException("UNKNONWN: " + value.tag);

        }
    }

    private void writeEnum(BufferedSink sink, Object value) throws IOException {
        sink.writeShort(4);
        sink.writeInt((Integer) value);
    }

    private void writeBoolean(BufferedSink sink, Object value) throws IOException {
        sink.writeShort(1);
        sink.writeByte((Boolean.TRUE.equals(value)) ? 1 : 0);
    }

    private void writeInteger(BufferedSink sink, Object value) throws IOException {
        sink.writeShort(4);
        sink.writeInt((Integer) value);
    }

    private void writeRangeOfIntegers(BufferedSink sink, Object value) throws IOException {
        sink.writeShort(8);
        int[] data = (int[]) value;
        sink.writeInt(data[0]);
        sink.writeInt(data[1]);
    }

    private void writeTextValue(BufferedSink sink, Object value) throws IOException {
        sink.writeShort(((String) value).length());
        sink.writeString((String) value, Charset.forName("ASCII"));
    }

}
