package com.test;

import com.test.schema.ContactType;
import com.test.schema.ErrorType;
import com.test.schema.ErrorTypeList;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;

import java.util.ArrayList;

/**
 * Created by sunilpatil on 1/2/17.
 */
public class DroolsHelper {

    public static void main(String[] argv){

        XSDHelper xsdHelper = new XSDHelper();
        ContactType contactType =xsdHelper.parseXML("/Users/sunilpatil/workspace/SampleProject/src/main/resources/contact.xml");
        DroolsHelper droolsHelper = new DroolsHelper();
        droolsHelper.executeRules(contactType);
        System.out.println(xsdHelper.convertToXML(contactType));
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

    public static void addError(ContactType contactType , String error){
        ErrorTypeList errorTypeList1 = new ErrorTypeList();
        contactType.setErrorList(errorTypeList1);
        ErrorType errorType = new ErrorType();
        errorType.setError(error);
        errorTypeList1.getError().add(errorType);
    }
}
