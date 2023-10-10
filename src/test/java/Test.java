import javax.naming.*;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;
import java.util.ArrayList;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;

public class Test {

    public static void main(String[] args) throws Exception{

        getAllAdUserNames();
    }

    public static void getAllAdUserNames() throws Exception{

        Hashtable env = new Hashtable();
        env.put(Context.SECURITY_AUTHENTICATION, "simple");//"none","simple","strong"
        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldap://jn.intra.customs.gov.cn");
        env.put(Context.SECURITY_PRINCIPAL, "CN=Administrator,CN=Users,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn");
        env.put(Context.SECURITY_CREDENTIALS, "tswcbyy5413LX");

        LdapContext ctx = new InitialLdapContext(env,null);
        SearchControls searchControls = new SearchControls();
        searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);

        String searchFilter = "(objectClass=person)";
        String searchBase = "OU=济南海关,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn";
        String returnedAtts[] = {"*"};
        searchControls.setReturningAttributes(returnedAtts);

        NamingEnumeration<SearchResult> result = ctx.search(searchBase,searchFilter,searchControls);
        while (result.hasMoreElements()) {
            SearchResult searchResult = (SearchResult) result.next();
            String uac = searchResult.getAttributes().get("useraccountcontrol").get() + "";
            System.out.println("[" + searchResult.getName() + "] -> "+ uac);
        }
        ctx.close();
    }
}
