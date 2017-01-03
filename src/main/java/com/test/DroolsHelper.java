package com.test;

import com.test.schema.ContactType;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

/**
 * Created by sunilpatil on 1/2/17.
 */
public class DroolsHelper {

    public static void main(String[] argv){

        XSDHelper xsdHelper = new XSDHelper();
        ContactType contactType =xsdHelper.parseXML("/Users/sunilpatil/workspace/SampleProject/src/main/resources/contact.xml");
        kSession.insert(contactType);
        kSession.fireAllRules();
    }
    static KieSession kSession;
    public void initialize(){
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        kSession = kContainer.newKieSession("ksession-rules");

    }

    public void executeRules(ContactType contactType){
        if(kSession == null)
            initialize();
        kSession.insert(contactType);
        kSession.fireAllRules();
    }
}
