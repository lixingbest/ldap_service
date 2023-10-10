import org.ldaptive.*;
import org.ldaptive.ad.control.DirSyncControl;
import org.ldaptive.ad.control.util.DirSyncClient;
import org.ldaptive.control.util.DefaultCookieManager;

public class DirSync {

    public static void main(String[] args) throws Exception{

        SingleConnectionFactory factory = new SingleConnectionFactory(ConnectionConfig.builder()
                .url("ldap://jn.intra.customs.gov.cn")
                .connectionInitializers(
                        new BindConnectionInitializer("CN=Administrator,CN=Users,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn", new Credential("tswcbyy5413LX")))
                .build());
        factory.initialize();
        DirSyncClient client = new DirSyncClient(
                factory, new DirSyncControl.Flag[] {DirSyncControl.Flag.ANCESTORS_FIRST_ORDER, });
        SearchRequest request = new SearchRequest("OU=济南海关,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn", "(uid=*)");
        SearchResponse res = client.executeToCompletion(request, new DefaultCookieManager());
        for (LdapEntry entry : res.getEntries()) {
            System.out.print(entry);
        }
        factory.close();
    }
}
