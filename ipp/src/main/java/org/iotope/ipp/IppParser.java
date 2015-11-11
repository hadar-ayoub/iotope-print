package org.iotope.ipp;

import okio.BufferedSource;
import okio.Okio;
import okio.Source;

import java.io.IOException;
import java.nio.charset.Charset;

/**
 * Created by alexvanboxel on 03/05/15.
 */
public class IppParser {

    IppRoot.Builder root;
    IppAttributeGroup.Builder group;

    /**
     * https://tools.ietf.org/html/rfc2910
     * 3.5.1 Delimiter Tags
     *
     * @param source
     * @throws IOException
     */
    public IppRoot read(Source in) throws IOException {
        BufferedSource source = Okio.buffer(in);
        root = IppRoot.builder()
                .major(source.readByte())
                .minor(source.readByte())
                .operation(source.readShort())
                .request(source.readInt());

        int tag = source.readByte();
        while (true) {
            switch (tag) {
                case 0x01: // operation-attributes-tag
                    System.out.println("operation-attributes-tag");
                    tag = parseGroup(source, tag);
                    break;
                case 0x02: // job-attributes-tag
                    System.out.println("job-attributes-tag");
                    tag = parseGroup(source, tag);
                    break;
                case 0x04: // printer-attributes-tag
                    System.out.println("printer-attributes-tag");
                    tag = parseGroup(source, tag);
                    break;
                case 0x05: // unsupported-attributes-tag
                    System.out.println("unsupported-attributes-tag");
                    tag = parseGroup(source, tag);
                    break;
                case 0x03:
                    return root.build();
                default:
            }
        }
    }

    private int parseGroup(BufferedSource source, int tag) throws IOException {
        group = IppAttributeGroup.builder()
                .tag(tag);
        while (true) {
            int valueType = source.readByte();
            if (valueType >= 0x00 && valueType <= 0x05) {
                root.add(group.build());
                return valueType;
            }


            int length = source.readShort();
            String name = source.readString(length, Charset.forName("ASCII"));
            IppAttributeValue.Builder value = IppAttributeValue.builder()
                    .name(name)
                    .tag(valueType);

            switch (valueType) {
                case 0x41: // textWithoutLanguage
                case 0x42: // nameWithoutLanguage
                case 0X44: // keyword
                case 0x45: // uri
                case 0x47: // char
                case 0x48: // natural char
                case 0x49:
                    parseTextValue(source, value);
                    break;

                case 0x33: // rangeOfInteger
                    parseRangeOfIntegers(source, value);
                    break;
                case 0x21: // integer
                    parseInteger(source, value);
                    break;
                case 0x22: // boolean
                    parseBoolean(source, value);
                    break;
                case 0x23: // enum
                    parseEnum(source, value);
                    break;

                default:
                    System.out.println("UNKNONWN: " + valueType);
                    root.add(group.build());
                    return valueType;
            }
            group.add(value.build());
        }
    }

    private void parseEnum(BufferedSource source, IppAttributeValue.Builder value) throws IOException {
        int length = source.readShort();
        int data = source.readInt();
        value.value(data);
    }

    private void parseBoolean(BufferedSource source, IppAttributeValue.Builder value) throws IOException {
        int length = source.readShort();
        int data = source.readByte();
        value.value(data == 0 ? false : true);
    }

    private void parseInteger(BufferedSource source, IppAttributeValue.Builder value) throws IOException {
        int length = source.readShort();
        int data = source.readInt();
        value.value(data);
    }

    private void parseRangeOfIntegers(BufferedSource source, IppAttributeValue.Builder value) throws IOException {
        int length = source.readShort();
        int left = source.readInt();
        int right = source.readInt();
        value.value(new int[]{left, right});
    }

    private void parseTextValue(BufferedSource source, IppAttributeValue.Builder value) throws IOException {
        int length = source.readShort();
        String text = source.readString(length, Charset.forName("ASCII"));
        value.value(text);
    }

}
