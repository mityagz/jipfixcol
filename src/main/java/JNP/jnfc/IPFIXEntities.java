package JNP.jnfc;

import java.util.HashMap;

/**
 * Created by mitya on 10/14/17.
 */
public class IPFIXEntities {
    HashMap<Integer, Entity> ent;
    IPFIXEntities() {
        ent = new HashMap<Integer, Entity>();
        ent.put(1, new Entity(1, "octetDeltaCount", "unsigned64", "deltaCounter", 8));
        ent.put(2, new Entity(2, "packetDeltaCount", "unsigned64", "deltaCounter", 8));
        ent.put(4, new Entity(4, "protocolIdentifier", "unsigned8", "identifier", 1));
        ent.put(5, new Entity(5, "ipClassOfService", "unsigned8", "identifier", 1));
        ent.put(6, new Entity(6, "tcpControlBits", "unsigned16", "flags", 2));
        ent.put(7, new Entity(7, "sourceTransportPort", "unsigned16", "identifier", 2));
        ent.put(8, new Entity(8, "sourceIPv4Address", "ipv4Address", "default", 4));
        ent.put(11, new Entity(11, "destinationTransportPort", "unsigned16", "identifier", 2));
        ent.put(12, new Entity(12, "destinationIPv4Address", "ipv4Address", "default", 4));
        ent.put(16, new Entity(16, "bgpSourceAsNumber", "unsigned32", "identifier", 4));
        ent.put(17, new Entity(17, "bgpDestinationAsNumber", "unsigned32", "identifier", 4));
        ent.put(17, new Entity(17, "bgpDestinationAsNumber", "unsigned32", "identifier", 4));
        ent.put(27, new Entity(27, "sourceIPv6Address", "ipv6Address", "default", 16));
        ent.put(28, new Entity(28, "destinationIPv6Address", "ipv6Address", "default", 16));
        ent.put(32, new Entity(32, "icmpTypeCodeIPv4", "unsigned16", "identifier", 2));
        ent.put(58, new Entity(58, "vlanId", "unsigned16", "identifier", 2));
        ent.put(136, new Entity(136, "flowEndReason", "unsigned8", "identifier", 1));
        ent.put(156, new Entity(156, "flowStartNanoseconds", "dateTimeNanoseconds", "default", 8));
        ent.put(157, new Entity(157, "flowEndNanoseconds", "dateTimeNanoseconds", "default", 8));
        //ent.get(1).getAbstractDataType();
    }


}

class Entity {
    int ElementID;
    String Name;
    String AbstractDataType;
    String DataTypeSemantics;
    String Status;
    String Description;
    String Units;
    String Range;
    String References;
    String Requester;
    String Revision;
    String Date;
    int Length;


    Entity(int ElementID, String Name, String AbstractDataType, String DataTypeSemantics, int Length) {
        this.ElementID = ElementID;
        this.Name = Name;
        this.AbstractDataType = AbstractDataType;
        this.DataTypeSemantics = DataTypeSemantics;
        this.Length = Length;
    }

    public int getElementID() {
        return ElementID;
    }

    public String getName() {
        return Name;
    }

     public String getAbstractDataType() {
        return AbstractDataType;
    }

     public String getDataTypeSemantics() {
        return DataTypeSemantics;
    }
}
