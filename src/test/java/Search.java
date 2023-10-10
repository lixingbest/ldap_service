import org.ldaptive.*;
import org.ldaptive.ad.handler.ObjectGuidHandler;
import org.ldaptive.ad.handler.ObjectSidHandler;
import org.ldaptive.ad.handler.RangeEntryHandler;
import org.ldaptive.handler.DnAttributeEntryHandler;
import org.ldaptive.handler.MergeAttributeEntryHandler;
import org.ldaptive.handler.MergeResultHandler;

import java.util.Scanner;

public class Search {

    public static void main(String[] args) throws Exception {

        String ldap = "ldap://jn.intra.customs.gov.cn";
        String adminDn = "CN=Administrator,CN=Users,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn";
        String passwd = "woshishui.5413LX";
        String baseDN = "OU=济南海关,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn";
        String userExpr = "(objectClass=person)";

        SearchOperation search = new SearchOperation(
                DefaultConnectionFactory.builder()
                        .config(ConnectionConfig.builder()
                                .url(ldap)
                                .connectionInitializers(BindConnectionInitializer.builder()
                                        .dn(adminDn)
                                        .credential(passwd)
                                        .build())
                                .build())
                        .build(),
                baseDN);
        search.setSearchResultHandlers(new MergeResultHandler());
        search.setEntryHandlers(new DnAttributeEntryHandler(), new ObjectGuidHandler(),new ObjectSidHandler());

        SearchResponse response = search.execute(SearchRequest.builder()
                .dn("OU=济南海关,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn")
                .filter("(objectClass=person)")
                .returnAttributes("*")
                .build());
        for (LdapEntry entry : response.getEntries()) {
            String uac = entry.getAttribute("useraccountcontrol").getStringValue();
            System.out.println(entry.getDn() + " -> " + uac);
        }

        System.out.println("------------------");
    }
}
