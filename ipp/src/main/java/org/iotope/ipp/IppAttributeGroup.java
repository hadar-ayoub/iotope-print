package org.iotope.ipp;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexvanboxel on 03/05/15.
 */
public class IppAttributeGroup {
    int tag;

    List<IppAttributeValue> values = new ArrayList<IppAttributeValue>();

    public static class Builder {
        private IppAttributeGroup obj = new IppAttributeGroup();

        public Builder add(IppAttributeValue value) {
            obj.values.add(value);
            return this;
        }

        public Builder addTextWithoutLanguage(String name, String value) {
            return add(IppAttributeValue.builder().tag(0x41).name(name).value(value).build());
        }

        public Builder addNameWithoutLanguage(String name, String value) {
            return add(IppAttributeValue.builder().tag(0x42).name(name).value(value).build());
        }

        public Builder addKeyword(String name, String value) {
            return add(IppAttributeValue.builder().tag(0x44).name(name).value(value).build());
        }

        public Builder addKeyword(String value) {
            return addKeyword("",value);
        }

        public Builder addURI(String name, String value) {
            return add(IppAttributeValue.builder().tag(0x45).name(name).value(value).build());
        }

        public Builder addChar(String name, String value) {
            return add(IppAttributeValue.builder().tag(0x47).name(name).value(value).build());
        }

        public Builder addNaturalChar(String name, String value) {
            return add(IppAttributeValue.builder().tag(0x48).name(name).value(value).build());
        }

        public Builder add49(String name, String value) {
            return add(IppAttributeValue.builder().tag(0x49).name(name).value(value).build());
        }

        public Builder addIntRange(String name, int from,int till) {
            return add(IppAttributeValue.builder().tag(0x33).name(name).value(new int[] {from,till}).build());
        }

        public Builder addInt(String name, int i) {
            return add(IppAttributeValue.builder().tag(0x21).name(name).value(i).build());
        }

        public Builder addEnum(String name, int i) {
            return add(IppAttributeValue.builder().tag(0x23).name(name).value(i).build());
        }

        public Builder addBoolean(String name, boolean b) {
            return add(IppAttributeValue.builder().tag(0x22).name(name).value(b).build());
        }

        public IppAttributeGroup build() {
            return obj;
        }

        public Builder tag(int tag) {
            obj.tag = tag;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static String tagToString(int tag) {
        switch (tag) {
            case 0x01: // operation-attributes-tag
                return "operation-attributes-tag";
            case 0x02: // job-attributes-tag
                return "job-attributes-tag";
            case 0x04: // printer-attributes-tag
                return "printer-attributes-tag";
            case 0x05: // unsupported-attributes-tag
                return "unsupported-attributes-tag";
            default:
                return "ERROR";
        }

    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("G " + tagToString(tag));
        builder.append("[");
        for (IppAttributeValue value : values) {
            builder.append(value.toString());
            builder.append(",");
        }
        builder.append("]");

        return builder.toString();
    }
}