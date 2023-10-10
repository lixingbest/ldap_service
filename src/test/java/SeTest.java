import com.plzy.ldap.framework.utils.SecurityUtil;
import io.netty.handler.codec.base64.Base64Encoder;

import java.io.FileInputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.util.Base64;

public class SeTest {

    public static void main(String[] args) {

        try {
            //读取文件内容
            FileInputStream is = new FileInputStream("/Users/lixingbest/Downloads/ldap.keystore");
            KeyStore ks = KeyStore.getInstance("JKS");
            ks.load(is, "woshishui.5413LX".toCharArray());
            PrivateKey key = (PrivateKey) ks.getKey("ldap", "woshishui.5413LX".toCharArray());
            String encoded = new String(Base64.getEncoder().encode(key.getEncoded()));
            System.out.println(encoded);
            is.close();
        } catch (Exception e){
            e.printStackTrace();
        }




    }
}
