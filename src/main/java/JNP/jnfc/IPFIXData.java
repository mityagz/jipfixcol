package JNP.jnfc;

import java.util.*;

/**
 * Created by mitya on 10/15/17.
 */
public class IPFIXData {
    // header
    // Set IPFIXSet template
    // Set IPFIXSet data
    private Header header;
    private HashMap<Integer,Template> template;
    private HashSet<Data> data;


    IPFIXData() {
        template = new HashMap<Integer, Template>();
        data = new HashSet<Data>();
    }


    public void setHeader(byte [] head) {
        header = new Header(head);
        //template = new HashSet<Template>();
        //data = new HashSet<Data>();
    }

    public void setTemplate(byte [] temp) {
        Template t = new Template(temp);
        template.put(t.getTemplateID(), t);
        LinkedHashMap m = t.getTemplateRecord();
        /*
        for (Object id : m.keySet()) {
            System.out.println("Template Record val/len: " + id + "/" + m.get(id));
            //System.out.println(ent.ent.get(InfoE).getElementID() + "/" + ent.ent.get(InfoE).getName() + "/" + ent.ent.get(InfoE).getAbstractDataType() + "/" + ent.ent.get(InfoE).getDataTypeSemantics());
        }
        */

        if(!IPFIXCol.templateCache.isExists(t.getTemplateID())) {
            IPFIXCol.templateCache.put(t.getTemplateID(), t);
            for (Object id : m.keySet()) {
                System.out.println("Template Record val/len: " + id + "/" + m.get(id));
            }
        } else {
            System.out.println("This template already exist");
            System.out.println("----------------------------");
            for (Object id : m.keySet()) {
                System.out.println("Template Record val/len: " + id + "/" + m.get(id));
            }
            System.out.println("----------------------------");
        }
    }

    public void setData(byte [] data) {
        Data d = new Data(data);
    }
}
