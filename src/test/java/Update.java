import org.ldaptive.*;

public class Update {

    public static void main(String[] args) throws Exception{

        SingleConnectionFactory factory = new SingleConnectionFactory(ConnectionConfig.builder()
                .url("ldap://jn.intra.customs.gov.cn")
                .connectionInitializers(
                        new BindConnectionInitializer("CN=Administrator,CN=Users,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn", new Credential("tswcbyy5413LX")))
                .build());
        factory.initialize();

        AddOperation add = new AddOperation(factory);
        AddResponse res = add.execute(AddRequest.builder()
                .dn("CN=okok,OU=济南海关,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn")
                .attributes(new LdapAttribute("uid", "okok"),
                        new LdapAttribute("cn", "okok")
                )
                .build());
        if (res.isSuccess()) {
            System.out.println("ok");
            // add succeeded
        } else {
            System.out.println("error:" + res.getDiagnosticMessage());
            // add failed
        }
    }
}
