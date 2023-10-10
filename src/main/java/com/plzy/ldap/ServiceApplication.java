package com.plzy.ldap;

import lombok.extern.slf4j.Slf4j;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustStrategy;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.ldaptive.*;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import springfox.documentation.oas.annotations.EnableOpenApi;

import javax.net.ssl.SSLContext;
import java.util.Scanner;

@SpringBootApplication
@MapperScan("com.plzy.ldap.**.mapper")
@ServletComponentScan
@EnableScheduling
@EnableWebMvc
@Slf4j
@EnableOpenApi
@EnableAsync
public class ServiceApplication {

	/**
	 * ladp 测试
	 *
	 * ldap://jn.intra.customs.gov.cn
	 * CN=Administrator,CN=Users,DC=jn,DC=intra,DC=customs,DC=gov,DC=cn
	 * tswcbyy5413LX
	 * DC=jn,DC=intra,DC=customs,DC=gov,DC=cn
	 * (objectClass=person)
	 * (objectClass=organizationalUnit)
	 *
	 * @throws Exception
	 */
	public static void search() throws Exception {

		Scanner scr = new Scanner(System.in);

		System.out.print("ldap地址：");
		String ldap = scr.next();

		System.out.print("管理员名称DN：");
		String adminDn = scr.next();

		System.out.print("管理员密码：");
		String passwd = scr.next();

		System.out.print("baseDN：");
		String baseDN = scr.next();

		System.out.print("userExpr：");
		String userExpr = scr.next();

		SearchOperation search = new SearchOperation(
				DefaultConnectionFactory.builder()
						.config(ConnectionConfig.builder()
								.url(ldap)
//                                .useStartTLS(true)
								.connectionInitializers(BindConnectionInitializer.builder()
										.dn(adminDn)
										.credential(passwd)
										.build())
								.build())
						.build(),
				baseDN);

		System.out.print("准备查询用户");
		SearchResponse response = search.execute(userExpr);
		for (LdapEntry entry : response.getEntries()) {
			System.out.println(entry.getDn());
		}

		System.out.println("------------------");

		System.out.print("ouExpr:");
		String ouExpr = scr.next();

		SearchResponse response2 = search.execute(ouExpr);
		for (LdapEntry entry : response2.getEntries()) {
			System.out.println(entry.getDn());
		}
	}

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate(generateHttpsRequestFactory());
	}

	public HttpComponentsClientHttpRequestFactory generateHttpsRequestFactory() {
		try {
			TrustStrategy acceptingTrustStrategy = (x509Certificates, authType) -> true;
			SSLContext sslContext = SSLContexts.custom().loadTrustMaterial(null, acceptingTrustStrategy).build();
			SSLConnectionSocketFactory connectionSocketFactory =
					new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

			HttpClientBuilder httpClientBuilder = HttpClients.custom();
			httpClientBuilder.setSSLSocketFactory(connectionSocketFactory);
			CloseableHttpClient httpClient = httpClientBuilder.build();
			HttpComponentsClientHttpRequestFactory factory = new HttpComponentsClientHttpRequestFactory();
			factory.setHttpClient(httpClient);
			factory.setConnectTimeout(10 * 1000);
			factory.setReadTimeout(30 * 1000);
			return factory;
		} catch (Exception e) {
			log.error("创建HttpsRestTemplate失败", e);
			throw new RuntimeException("创建HttpsRestTemplate失败", e);
		}

	}

	public static void main(String[] args) throws Exception{
		SpringApplication.run(ServiceApplication.class, args);
//		search();
	}
}
