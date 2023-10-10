import org.ldaptive.*;
import org.ldaptive.ad.control.util.NotificationClient;
import org.ldaptive.ad.handler.ObjectGuidHandler;
import org.ldaptive.handler.DnAttributeEntryHandler;
import org.ldaptive.handler.MergeResultHandler;

import java.util.concurrent.BlockingQueue;

public class DirectoryNotification {

    public static void main(String[] args) throws Exception {

        SearchOperation search = new SearchOperation(
                DefaultConnectionFactory.builder()
                        .config(ConnectionConfig.builder()
                                .url("ldap://jn.intra.customs.gov.cn")
                                .connectionInitializers(BindConnectionInitializer.builder()
                                        .dn("CN=Administrator,CN=Users,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn")
                                        .credential("tswcbyy5413LX")
                                        .build())
                                .build())
                        .build(),
                "ou=济南海关,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn");

        SearchResponse response = search.execute(SearchRequest.builder()
                .dn("OU=济南海关,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn")
                .filter("(objectClass=organizationalUnit)")
                .build());
        for (LdapEntry entry : response.getEntries()) {
            System.out.println(entry.getDn());
            String currDN = entry.getDn();
            new Thread(() -> {

                try {
                    SingleConnectionFactory factory = new SingleConnectionFactory(ConnectionConfig.builder()
                            .url("ldap://jn.intra.customs.gov.cn")
                            .connectionInitializers(new BindConnectionInitializer("CN=Administrator,CN=Users,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn", new Credential("tswcbyy5413LX")))
                            .build());
                    factory.initialize();
                    NotificationClient client = new NotificationClient(factory);
                    SearchRequest request = SearchRequest.builder()
                            .dn(currDN)
                            .filter("(objectClass=*)")
                            .scope(SearchScope.ONELEVEL)
                            .build();
                    BlockingQueue<NotificationClient.NotificationItem> results = client.execute(request);
                    while (true) {
                        NotificationClient.NotificationItem item = results.take(); // blocks until result is received
                        if (item.isEntry()) {
                            LdapEntry entry1 = item.getEntry();
                            System.out.println(entry1);
                        } else if (item.isException()) {
                            break;
                        }
                    }
                    factory.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }).start();
        }
    }
}
