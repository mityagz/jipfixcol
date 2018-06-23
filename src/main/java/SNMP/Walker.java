package SNMP;

import org.snmp4j.smi.OID;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Created by mitya on 1/29/18.
 */


public class Walker<W, V> {
    final Map<W, V> result = new LinkedHashMap<W, V>();

    public void walk(OID oid, V value) {
        result.put((W) oid.toDottedString(), value);
    }

    Map<W, V> getResult() {
        return result;
    }
}
