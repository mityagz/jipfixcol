package JNP.jnfc;

import java.util.Arrays;
import java.util.LinkedHashMap;

/**
 * Created by mitya on 10/15/17.
 */
public class Data {
    private int SetID;
    private int Length;
    private int LengthSumTemplate = 0;

    /*
                    Data Record Format

             +--------------------------------------------------+
             | Field Value                                      |
             +--------------------------------------------------+
             | Field Value                                      |
             +--------------------------------------------------+
              ...
             +--------------------------------------------------+
             | Field Value                                      |
             +--------------------------------------------------+

             The example in Figure Q shows a Data Set.  It consists of a Set
             Header and several Field Values.

     0                   1                   2                   3
     0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Set ID = Template ID        |          Length               |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Record 1 - Field Value 1    |   Record 1 - Field Value 2    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Record 1 - Field Value 3    |             ...               |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Record 2 - Field Value 1    |   Record 2 - Field Value 2    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Record 2 - Field Value 3    |             ...               |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Record 3 - Field Value 1    |   Record 3 - Field Value 2    |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |   Record 3 - Field Value 3    |             ...               |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
    |              ...              |      Padding (optional)       |
    +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

                Figure Q: Data Set, Containing Data Record
    */

    Data(byte [] data) {
        SetID = Helper.getInt(Arrays.copyOfRange(data, IPFIXDatagram.beginPosition, 2)); // Set Header
        Length = Helper.getInt(Arrays.copyOfRange(data, IPFIXDatagram.beginPosition + 2, 4));
        Template t = IPFIXCol.templateCache.getTemplate(SetID);
        LinkedHashMap dataRecords = t.getTemplateRecord();
        for (Object keyRecord : dataRecords.keySet()) {
            Integer entityLength = (Integer) dataRecords.get(keyRecord);
            System.out.println("Record key: " + keyRecord + "/" + entityLength);
            LengthSumTemplate += entityLength;
        }
        System.out.println("Length from template: " + LengthSumTemplate);
        int numFlow = ((Length - 4) / LengthSumTemplate);
        System.out.println("Number flows: " + numFlow);
        int beginPos = 4;
        int countFlow = 0;
        IPFIXEntities entities = new IPFIXEntities();
        int currPos = beginPos;
        while (beginPos < Length - 4) {
            //System.out.println("SrcIPv4Address: " + Helper.byteToIp(Arrays.copyOfRange(data, beginPos, beginPos + 4)) + " DstIPv4Address: " + Helper.byteToIp(Arrays.copyOfRange(data, beginPos + 4, beginPos + 8)));
            //System.out.println("sourceTransportPort: " + Helper.getInt2(Arrays.copyOfRange(data, beginPos + 8, beginPos + 10)) + " destinationTransportPort: " + Helper.getInt2(Arrays.copyOfRange(data, beginPos + 10, beginPos + 12)));
            for(Object keyRecord : dataRecords.keySet()) {
                int currLen = (Integer) dataRecords.get(keyRecord);
                System.out.print("Record type: " + entities.ent.get(keyRecord).getName() + " | Length: " + dataRecords.get(keyRecord));
                if(entities.ent.get(keyRecord).getName().contains("sourceIPv4Address")) {
                    System.out.println(" | " + entities.ent.get(keyRecord).getName() + ": " + Helper.byteToIp(Arrays.copyOfRange(data, currPos, currPos + currLen)));
                } else if (entities.ent.get(keyRecord).getName().contains("destinationIPv4Address")) {
                    System.out.println(" | " + entities.ent.get(keyRecord).getName() + ": " + Helper.byteToIp(Arrays.copyOfRange(data, currPos, currPos + currLen)));
                } else {
                    System.out.println(" | " + entities.ent.get(keyRecord).getName() + ": " + Helper.getIntV(Arrays.copyOfRange(data, currPos, currPos + currLen)));
                }
                currPos += currLen;
                //Integer entityLength = (Integer) dataRecords.get(keyRecord);
            }
            beginPos += LengthSumTemplate;
            System.out.println("Count flow: " + (++countFlow) + " beginPos: " + beginPos);
        }
        entities = null;
    }
}
