package JNP.jnfc;

import java.nio.ByteBuffer;
import java.util.Arrays;

/**
 * Created by mitya on 10/14/17.
 */
public class IPFIXDatagram {
    private ByteBuffer recvBuf;
    private int headerLen = Helper.IPFIXHeaderLength;
    private int ipfixLenFromHeader = 0;
    protected static int beginPosition = 0;
    private IPFIXData ipfixData;
    IPFIXDatagram(ByteBuffer recvBuf) {
        if(recvBuf != null)
            this.recvBuf = recvBuf;
    }

    public void parse() {
        byte [] header = new byte[headerLen];
        recvBuf.get(header, 0, headerLen);


        // IPFIX Message Header
        if(Helper.getInt(Arrays.copyOfRange(header, 0, 2)) == Helper.IPFIXVersion) {
            ipfixData = new IPFIXData();
            Process.processHeader(header, ipfixData);
        } else
            System.exit(1);

        // IPFIX Sets
        ipfixLenFromHeader = Helper.getInt(Arrays.copyOfRange(header, 2, 4)) - header.length;
        byte ipfixSets [] = new byte[ipfixLenFromHeader];
        recvBuf.get(ipfixSets, 0, ipfixLenFromHeader);

        int position = ipfixLenFromHeader;
        beginPosition = 0;
        while(beginPosition  < ipfixLenFromHeader) {
            // IPFIX Set Header
            int typeSet = Helper.getInt(Arrays.copyOfRange(ipfixSets, beginPosition, 2));
            if ((typeSet == Helper.TemplateSets)) {
                Process.processTemplateSet(ipfixSets, ipfixData);
            } else if (typeSet == Helper.OptionsTemplateSets) {
                Process.processTemplateSet(ipfixSets, ipfixData);
            } else if (typeSet > Helper.DataSets) {
                Process.processDataSet(ipfixSets, ipfixData);
            }

            System.out.println("beginPosition = " + beginPosition);
            System.out.println("------------------------------------------------------------------------------------");

            /*
            while (recvBuf.hasRemaining()) {
                recvBuf.get();
            }
            */

        }
        ipfixData = null;
        //System.out.println(result);
    }
}
