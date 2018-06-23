package JNP.jnfc;

import java.util.HashMap;

/**
 * Created by mitya on 10/14/17.
 */
public class TemplateCache {
    private HashMap<Integer, Template> templateHashMap;


    TemplateCache() {
        templateHashMap = new HashMap<Integer, Template>();
    }

    public void put(Integer id, Template template) {
        templateHashMap.put(id, template);
    }

    public void remove(Template template) {

    }

    public int getTemplateCache() { return  0;}

    public Template getTemplate(int setId) {
        for (Integer id : templateHashMap.keySet()) {
            if(id == setId) return templateHashMap.get(id);
        }
        return null;
    }

    public boolean isExists(int templateId) {
        if(templateHashMap == null) return false;
        for (Integer id : templateHashMap.keySet()) {
            if(id == templateId) return true;
        }
        return false;
    }
}
