package org.iotope.ipp;

/**
 * Created by alexvanboxel on 03/05/15.
 */
public class IppAttributeValue {

    String name;
    int tag;
    Object value;


    public static class Builder {
        private IppAttributeValue obj = new IppAttributeValue();


        public IppAttributeValue build() {
            return obj;
        }

        public Builder name(String name) {
            obj.name = name;
            return this;
        }

        public Builder tag(int valueType) {
            obj.tag = valueType;
            return this;
        }

        public Builder value(Object value) {
            obj.value = value;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(A(");
        builder.append(String.format("%x",tag));
        builder.append(") {");
        builder.append(name);
        builder.append("=");
        builder.append(value);
        builder.append("})");
        return builder.toString();
    }
}
