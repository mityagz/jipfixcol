package JNP.jnfc;

import java.util.*;

/**
 * Created by mitya on 10/15/17.
 */
public class Template {
    private int SetID;
    private int Length;
    private int TemplateID;
    private int FieldCount;
    //private HashMap<Integer, Integer> TemplateRecord;
    private LinkedHashMap<Integer, Integer> TemplateRecord;
    private IPFIXEntities ent;
        /*
                Set Header Format
       0                   1                   2                   3
       0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
      |          Set ID               |          Length               |
      +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+


            Template Record Format

         0                   1                   2                   3
         0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
        |      Template ID (> 255)      |         Field Count           |
        +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+

                    Set Format
             +--------------------------------------------------+
             | Set Header                                       |
             +--------------------------------------------------+
             | record                                           |
             +--------------------------------------------------+
             | record                                           |
             +--------------------------------------------------+
              ...
             +--------------------------------------------------+
             | record                                           |
             +--------------------------------------------------+
             | Padding (opt.)                                   |
             +--------------------------------------------------+
    */

    Template(byte [] temp) {
        TemplateRecord = new LinkedHashMap<Integer, Integer>();
        ent = new IPFIXEntities();
        SetID = Helper.getInt(Arrays.copyOfRange(temp, IPFIXDatagram.beginPosition, 2)); // Set Header
        Length = Helper.getInt(Arrays.copyOfRange(temp, IPFIXDatagram.beginPosition + 2, 4));
        TemplateID = Helper.getInt(Arrays.copyOfRange(temp, IPFIXDatagram.beginPosition + 4, 6)); // Template Header
        FieldCount = Helper.getInt(Arrays.copyOfRange(temp, IPFIXDatagram.beginPosition + 6, 8));
        int recordPosition = IPFIXDatagram.beginPosition + 8;
        int recordLen = 2;
        for(int i = 0; i < FieldCount; i++) {
            int InfoE = Helper.getInt(Arrays.copyOfRange(temp, recordPosition, recordPosition + recordLen));
            recordPosition += 2;
            int LenE = Helper.getInt(Arrays.copyOfRange(temp, recordPosition, recordPosition + recordLen));
            recordPosition += 2; // 4 bytes record if enterprise bi is 0
            TemplateRecord.put(InfoE, LenE);
        }
        ent = null;
    }

    public int getSetID() {
        return SetID;
    }

    public int getTemplateID() {
        return TemplateID;
    }

    public LinkedHashMap<Integer, Integer> getTemplateRecord() {
        return TemplateRecord;
    }
}
