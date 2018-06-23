package JNP.jnfc;

import java.nio.ByteBuffer;
import java.util.Arrays;
import JNP.jnfc.Helper;
import JNP.jnfc.IPFIXDatagram;
import JNP.jnfc.IPFIXData;

/**
 * Created by mitya on 10/14/17.
 */
public class Process {

    public static void processHeader(byte [] header, IPFIXData ipfixData) {
        System.out.println("Receiving IPFIX Datagram");
        System.out.println("Version: " + Helper.getInt(Arrays.copyOfRange(header, 0, 2)));
        System.out.println("IPFIX Message len: " + Helper.getInt(Arrays.copyOfRange(header, 2, 4)));
        System.out.println("IPFIX Export Time: " + Helper.getInt(Arrays.copyOfRange(header, 4, 8)));
        System.out.println("IPFIX Sequence Number: " + Helper.getInt(Arrays.copyOfRange(header, 8, 12)));
        System.out.println("IPFIX Observation Domain ID: " + Helper.getInt(Arrays.copyOfRange(header, 12, 16)));

        ipfixData.setHeader(header);
    }

    public static void processTemplateSet(byte [] ipfixSets, IPFIXData ipfixData) {
        System.out.println("Template Set Id: " + Helper.getInt(Arrays.copyOfRange(ipfixSets, IPFIXDatagram.beginPosition, 2)));
        System.out.println("Template Set Length: " + Helper.getInt(Arrays.copyOfRange(ipfixSets, IPFIXDatagram.beginPosition + 2, 4)));
        System.out.println("Template ID: " + Helper.getInt(Arrays.copyOfRange(ipfixSets, IPFIXDatagram.beginPosition + 4, 6)));
        System.out.println("Field Count: " + Helper.getInt(Arrays.copyOfRange(ipfixSets, IPFIXDatagram.beginPosition + 6, 8)));

        ipfixData.setTemplate(ipfixSets);
        IPFIXDatagram.beginPosition += Helper.getInt(Arrays.copyOfRange(ipfixSets, IPFIXDatagram.beginPosition + 2, 4));
    }

    public static void processDataSet (byte [] data, IPFIXData ipfixData) {
        System.out.println("Date Set with Id: " + Helper.getInt(Arrays.copyOfRange(data, IPFIXDatagram.beginPosition, 2)));
        System.out.println("Data Set Length: " + Helper.getInt(Arrays.copyOfRange(data, IPFIXDatagram.beginPosition + 2, 4)));

        ipfixData.setData(data);
        IPFIXDatagram.beginPosition += Helper.getInt(Arrays.copyOfRange(data, IPFIXDatagram.beginPosition + 2, 4));
    }
}
