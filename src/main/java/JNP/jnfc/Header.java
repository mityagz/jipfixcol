package JNP.jnfc;

import java.util.Arrays;

/**
 * Created by mitya on 10/15/17.
 */
public class Header {
    /*
       |   Version Number          |            Length             |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                           Export Time                         |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                       Sequence Number                         |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                    Observation Domain ID
 */
    private int version;
    private int length;
    private int exportTime;
    private int sequenceNumber;
    private int observationDomainId;

    Header(byte [] header){
        version = Helper.getInt(Arrays.copyOfRange(header, 0, 2));
        length = Helper.getInt(Arrays.copyOfRange(header, 2, 4));
        exportTime = Helper.getInt(Arrays.copyOfRange(header, 4, 8));
        sequenceNumber = Helper.getInt(Arrays.copyOfRange(header, 8, 12));
        observationDomainId = Helper.getInt(Arrays.copyOfRange(header, 12, 16));
    }
}
