package SNMP;

import org.apache.log4j.Logger;
import org.snmp4j.CommunityTarget;
import org.snmp4j.PDU;
import org.snmp4j.Snmp;
import org.snmp4j.TransportMapping;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.event.ResponseListener;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.DefaultPDUFactory;
import org.snmp4j.util.TreeEvent;
import org.snmp4j.util.TreeUtils;

import java.io.IOException;
import java.net.InetAddress;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by mitya on 1/29/18.
 */
public class SNMPClient {
    private Snmp snmp;
    private CommunityTarget target;
    private boolean destroyed = false;
    private static final Logger logger = Logger.getLogger(SNMPClient.class);

    public SNMPClient(Address address, String community, int snmpVersion) throws IOException {
        try {
            target = new CommunityTarget();
            target.setCommunity(new OctetString(community));
            target.setAddress(address);
            switch (snmpVersion) {
                case 1:
                    target.setVersion(SnmpConstants.version1);
                    break;
                case 2:
                    target.setVersion(SnmpConstants.version2c);
                    break;
                default:
                    target.setVersion(SnmpConstants.version1);
            }
            TransportMapping transport = new DefaultUdpTransportMapping();
            this.snmp = new Snmp(transport);
            transport.listen();
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }

    public SNMPClient(String address, String community, int snmpVersion) throws IOException {
        this(new UdpAddress(address), community, snmpVersion);
    }

    public SNMPClient(InetAddress ip, int port, String community, int snmpVersion) throws IOException {
        this(new UdpAddress(ip, port), community, snmpVersion);
    }

    public <W, V> void walk(String oid, Class<V> clazz, Walker<W, V> walker) throws IOException {
        walk(new OID(oid), clazz, walker);
    }

    public <W, V> void walk(OID oid, Class<V> clazz, Walker<W, V> walker) throws IOException {
        try {
            PDU requestPDU = new PDU();
            requestPDU.add(new VariableBinding(oid));
            requestPDU.setType(PDU.GETNEXT);

            boolean finished = false;

            while (!finished) {
                VariableBinding vb = null;

                ResponseEvent re = snmp.send(requestPDU, target);
                PDU responsePDU = re.getResponse();

                if (responsePDU != null) {
                    vb = responsePDU.get(0);
                }
                if (responsePDU == null) {
                    finished = true;
                } else if (responsePDU.getErrorStatus() != 0) {
                    throw new IOException(responsePDU.getErrorStatusText());
                } else if (vb.getOid() == null) {
                    finished = true;
                } else if (vb.getOid().size() < oid.size()) {
                    finished = true;
                } else if (oid.leftMostCompare(oid.size(), vb.getOid()) != 0) {
                    finished = true;
                } else if (Null.isExceptionSyntax(vb.getVariable().getSyntax())) {
                    finished = true;
                } else if (vb.getOid().compareTo(oid) <= 0) {
                    finished = true;
                } else {
                    walker.walk(vb.getOid(), this.castVariable(vb.getVariable(), clazz));
                    // Set up the variable binding for the next entry.
                    requestPDU.setRequestID(new Integer32(0));
                    requestPDU.set(0, vb);
                }
            }
        } catch (IOException ex) {
            throw new IOException(ex);
        }
    }

    public <W, V> Map walk2(OID oid, Class<V> clazz, Walker<W, V> walker) throws IOException {
        //try {
            Map<String, String> result = new TreeMap();
            TreeUtils treeUtils = new TreeUtils(snmp, new DefaultPDUFactory());
            List<TreeEvent> events = treeUtils.getSubtree(target, oid);

             if (events == null || events.size() == 0) {
                 System.out.println("Error: Unable to read table...");
                 return result;
             }

             for (TreeEvent event : events) {
                 if (event == null) {
                    continue;
                 }
                 if (event.isError()) {
                    continue;
                 }
                 VariableBinding[] varBindings = event.getVariableBindings();
                 if (varBindings == null || varBindings.length == 0) {
                     continue;
                 }
                 for (VariableBinding varBinding : varBindings) {
                     if (varBinding == null) {
                         continue;
                     }
                     result.put("." + varBinding.getOid().toString(), varBinding.getVariable().toString());
                 }
             }
                return result;
            /*
        } catch (IOException ex) {
            throw new IOException(ex);
        }
        */
    }




    public <V> V get(OID oid, Class<V> clazz) throws IOException {
        try {
            PDU requestPDU = new PDU();
            requestPDU.add(new VariableBinding(oid));
            requestPDU.setType(PDU.GET);

            ResponseEvent re = snmp.send(requestPDU, target);
            if (re != null) {
                PDU responsePDU = re.getResponse();
                if (responsePDU != null) {
                    VariableBinding vb = responsePDU.get(0);
                    return castVariable(vb.getVariable(), clazz);
                }
            }
        } catch (IOException ex) {
            throw new IOException(ex);
        }
        return null;
    }

    public void destroy() throws IOException {
        this.destroyed = true;
        this.snmp.close();
        this.snmp = null;
    }

    protected void finalize() throws Throwable {
        if (!this.destroyed) {
            logger.warn("SNMPClient was not destroyed before finalize!");
            destroy();
        }
        super.finalize();
    }


    // snmpset
    @SuppressWarnings("rawtypes")
	public void snmpSet(String strOID, int Value) {
		  PDU pdu = new PDU();
		    //Depending on the MIB attribute type, appropriate casting can be done here
		    pdu.add(new VariableBinding(new OID(strOID), new Integer32(Value)));
		    pdu.setType(PDU.SET);
		    ResponseListener listener = new ResponseListener() {
		      public void onResponse(ResponseEvent event) {
		        PDU strResponse;
		        String result;
		        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
		        strResponse = event.getResponse();
		        if (strResponse!= null) {
		          result = strResponse.getErrorStatusText();
		          System.out.println("Set Status is: " + result);
		        }
		      }};
        try {
            snmp.send(pdu, target, null, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

/*
    public <V> void snmpSet(String strOID, Variable Value) {
		  PDU pdu = new PDU();
		    //Depending on the MIB attribute type, appropriate casting can be done here
		    //pdu.add(new VariableBinding(new OID(strOID), new Integer32(Value)));
            pdu.add(new VariableBinding(new OID(strOID), Value));
		    pdu.setType(PDU.SET);
		    ResponseListener listener = new ResponseListener() {
		      public void onResponse(ResponseEvent event) {
		        PDU strResponse;
		        String result;
		        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
		        strResponse = event.getResponse();
		        if (strResponse!= null) {
		          result = strResponse.getErrorStatusText();
		          System.out.println("Set Status is: " + result);
		        }
		      }};
        try {
            snmp.send(pdu, target, null, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
*/
	public void snmpSet(String strOID, String Value) {
		  PDU pdu = new PDU();
		    //Depending on the MIB attribute type, appropriate casting can be done here
		    pdu.add(new VariableBinding(new OID(strOID), new OctetString(Value)));
		    pdu.setType(PDU.SET);
		    ResponseListener listener = new ResponseListener() {
		      public void onResponse(ResponseEvent event) {
		        PDU strResponse;
		        String result;
		        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
		        strResponse = event.getResponse();
		        if (strResponse!= null) {
		          result = strResponse.getErrorStatusText();
		          System.out.println("Set Status is: " + result);
		        }
		      }};
        try {
            snmp.send(pdu, target, null, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


	public void snmpSet(String strOID, byte [] Value) {
		  PDU pdu = new PDU();
		    //Depending on the MIB attribute type, appropriate casting can be done here
		    pdu.add(new VariableBinding(new OID(strOID), new OctetString( Value)));
		    pdu.setType(PDU.SET);
		    ResponseListener listener = new ResponseListener() {
		      public void onResponse(ResponseEvent event) {
		        PDU strResponse;
		        String result;
		        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
		        strResponse = event.getResponse();
		        if (strResponse!= null) {
		          result = strResponse.getErrorStatusText();
		          System.out.println("Set Status is: " + result);
		        }
		      }};
        try {
            snmp.send(pdu, target, null, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void snmpSet(String strOID0, String Value0, String strOID1, String Value1, String strOID2, String Value2, String strOID3, Integer Value3 ) {
		  PDU pdu = new PDU();
		    //Depending on the MIB attribute type, appropriate casting can be done here
		    pdu.add(new VariableBinding(new OID(strOID0), new OctetString(Value0)));
		    //pdu.add(new VariableBinding(new OID(strOID1), new OctetString(Value1)));
		    //pdu.add(new VariableBinding(new OID(strOID2), new OctetString(Value2)));
		    pdu.add(new VariableBinding(new OID(strOID3), new Integer32(Value3)));
		    pdu.setType(PDU.SET);
		    ResponseListener listener = new ResponseListener() {
		      public void onResponse(ResponseEvent event) {
		        PDU strResponse;
		        String result;
		        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
		        strResponse = event.getResponse();
		        if (strResponse!= null) {
		          result = strResponse.getErrorStatusText();
		          System.out.println("Set Status is: " + result);
		        }
		      }};
        try {
            snmp.send(pdu, target, null, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

	public void snmpSet(String strOID0, String Value0, String strOID1, byte [] Value1, String strOID2, Integer Value2, String strOID3, Integer Value3) {
		  PDU pdu = new PDU();
		    //Depending on the MIB attribute type, appropriate casting can be done here
		    pdu.add(new VariableBinding(new OID(strOID0), new IpAddress(Value0)));
		    pdu.add(new VariableBinding(new OID(strOID1), new OctetString(Value1)));
		    pdu.add(new VariableBinding(new OID(strOID2), new Integer32(Value2)));
		    pdu.add(new VariableBinding(new OID(strOID3), new Integer32(Value3)));
		    pdu.setType(PDU.SET);
		    ResponseListener listener = new ResponseListener() {
		      public void onResponse(ResponseEvent event) {
		        PDU strResponse;
		        String result;
		        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
		        strResponse = event.getResponse();
		        if (strResponse!= null) {
		          result = strResponse.getErrorStatusText();
		          System.out.println("Set Status is: " + result);
		        }
		      }};
        try {
            snmp.send(pdu, target, null, listener);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
		// 3526 acl
		public void snmpSet(String strOID0, String Value0, String strOID2, Integer Value2, String strOID1, byte [] Value1,  String strOID3, Integer Value3) {
		  PDU pdu = new PDU();
		    //Depending on the MIB attribute type, appropriate casting can be done here
		    pdu.add(new VariableBinding(new OID(strOID0), new IpAddress(Value0)));
		    pdu.add(new VariableBinding(new OID(strOID2), new Integer32(Value2)));
		    pdu.add(new VariableBinding(new OID(strOID1), new OctetString(Value1)));
		    pdu.add(new VariableBinding(new OID(strOID3), new Integer32(Value3)));
		    pdu.setType(PDU.SET);
		    ResponseListener listener = new ResponseListener() {
		      public void onResponse(ResponseEvent event) {
		        PDU strResponse;
		        String result;
		        ((Snmp)event.getSource()).cancel(event.getRequest(), this);
		        strResponse = event.getResponse();
		        if (strResponse!= null) {
		          result = strResponse.getErrorStatusText();
		          System.out.println("Set Status is: " + result);
		        }
		      }};
            try {
                snmp.send(pdu, target, null, listener);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    /**
     * @param var   variable
     * @param clazz type to cast to
     * @return value
     */
    private <V> V castVariable(Variable var, Class<V> clazz) {
        if (Variable.class.isAssignableFrom(clazz)) {
            return clazz.cast(var);
        }
        if (var instanceof UnsignedInteger32 || var instanceof Counter64) {
            return clazz.cast(var.toLong());
        }
        if (var instanceof Integer32) {
            return clazz.cast(var.toInt());
        }
        if (var instanceof IpAddress) {
            if (clazz == InetAddress.class) {
                return clazz.cast(((IpAddress) var).getInetAddress());
            }
            if (clazz == String.class) {
                return clazz.cast(var.toString());
            }
            return clazz.cast(((IpAddress) var).toByteArray());
        }
        if (var instanceof Null) {
            return null;
        }
        if (var instanceof OctetString) {
            if (clazz == String.class) {
                return clazz.cast(var.toString());
            }
            return clazz.cast(((OctetString) var).toByteArray());
        }
        if (var instanceof OID) {
            if (clazz == String.class) {
                return clazz.cast(var.toString());
            }
            return clazz.cast(((OID) var).toIntArray());
        }
        if (var instanceof VariantVariable) {
            return clazz.cast(((VariantVariable) var).getVariable());
        }
        return clazz.cast(var.toString());
    }

    public static void main(String [] args) {
         Map<String, String> ifaceTitleToIfIndexMap = new LinkedHashMap<String, String>();
        try {
            SNMPClient client = new SNMPClient(InetAddress.getByName("192.168.1.2"), 161, "privatehome", 1);

            // get
            System.out.println(client.get(new OID(".1.3.6.1.2.1.1.1.0"), String.class));

            // walk
            Walker<String, String> ifaceWalk = new Walker<String, String>();
            //client.walk(new OID(".1.3.6.1.2.1.2.2.1.1"), Integer.class, ifaceWalk);
            client.walk(new OID(".1.3.6.1.2.1.2.2.1.6"), String.class, ifaceWalk);
            ifaceTitleToIfIndexMap = ifaceWalk.getResult();
            for(String key : ifaceTitleToIfIndexMap.keySet()) {
                System.out.println(key + " = " + " INTEGER: " + ifaceTitleToIfIndexMap.get(key));
            }

            client.snmpSet(".1.3.6.1.2.1.1.5.0", "http");
            //
            //Class clazz = new String("");
            //Variable v;
            //if(clazz.toString() instanceof String){}

            // walk2
            /*
            Map res = client.walk2(new OID(".1.3.6.1.2.1.2.2.1.6"), String.class, ifaceWalk);
            for(Object key : res.keySet()) {
                System.out.println(key + " : " + res.get(key));
            }
            */

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
