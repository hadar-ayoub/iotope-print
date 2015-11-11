package org.iotope.ipp;


import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexvanboxel on 03/05/15.
 */
public class IppRoot {
    int versionMajor;
    int versionMinor;

    /*
     0x0000              reserved, not used
     0x0001              reserved, not used
     0x0002              Print-Job
     0x0003              Print-URI
     0x0004              Validate-Job
     0x0005              Create-Job
     0x0006              Send-Document
     0x0007              Send-URI
     0x0008              Cancel-Job
     0x0009              Get-Job-Attributes
     0x000A              Get-Jobs
     0x000B              Get-Printer-Attributes
     0x000C              Hold-Job
     0x000D              Release-Job
     0x000E              Restart-Job
     0x000F              reserved for a future operation
     0x0010              Pause-Printer
     0x0011              Resume-Printer
     0x0012              Purge-Jobs
     0x0013-0x3FFF       reserved for future IETF standards track
     operations (see section 6.4)
     0x4000-0x8FFF       reserved for vendor extensions (see section 6.4)
     */
    int operation;

    int sequence;

    List<IppAttributeGroup> values = new ArrayList<IppAttributeGroup>();


    public static class Builder {
        private IppRoot obj = new IppRoot();

        public Builder major(int value) {
            obj.versionMajor = value;
            return this;
        }

        public Builder minor(int value) {
            obj.versionMinor = value;
            return this;
        }

        public Builder operation(int value) {
            obj.operation = value;
            return this;
        }

        public Builder request(int value) {
            obj.sequence = value;
            return this;
        }

        public IppRoot build() {
            return obj;
        }

        public Builder add(IppAttributeGroup group) {
            obj.values.add(group);
            return this;
        }

        public Builder getPrinterAttributes() {
            return major(2).minor(0).operation(0x0B);
        }

        public Builder validateJob() {
            return major(2).minor(0).operation(0x04);
        }

        public Builder createJob() {
            return major(2).minor(0).operation(0x05);
        }

        public Builder getJobs() {
            return major(2).minor(0).operation(0x0A);
        }

        public Builder getJobAttributes() {
            return major(2).minor(0).operation(0x09);
        }

        public Builder sendDocument() {
            return major(2).minor(0).operation(0x06);
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    public static String operationToString(int operation) {
        switch (operation) {
            case 0x00:
                return "reserved, not used (0x00)";

            case 0x01:
                return "reserved, not used (0x01)";

            case 0x02:
                return "Print-Job";

            case 0x03:
                return "Print-URI";

            case 0x04:
                return "Validate-Job";
            case 0x05:
                return "Create-Job";
            case 0x06:
                return "Send-Document";
            case 0x07:
                return "Send-URI";
            case 0x08:
                return "Cancel-Job";
            case 0x09:
                return "Get-Job-Attributes";
            case 0x0A:
                return "Get-Jobs";
            case 0x0B:
                return "Get-Printer-Attributes";
            case 0x0C:
                return "Hold-Job";
            case 0x0D:
                return "Release-Job";
            case 0x0E:
                return "Restart-Job";
            case 0x0F:
                return "reserved for a future operation (0x0F)";
            case 0x10:
                return "Pause-Printer";
            case 0x11:
                return "Resume-Printer";
            case 0x12:
                return "Purge-Jobs";
        }
        return "XXXXX";
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("(");
        builder.append(operationToString(operation));
        builder.append(",");
        builder.append(sequence);
        builder.append(" [");
        for (IppAttributeGroup value : values) {
            builder.append(value.toString());
        }

        builder.append("])");
        return builder.toString();
    }

}
