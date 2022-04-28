package com.mitocode;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.config.annotation.configurers.ClientDetailsServiceConfigurer;
import org.springframework.security.oauth2.config.annotation.web.configuration.AuthorizationServerConfigurerAdapter;
import org.springframework.security.oauth2.config.annotation.web.configuration.EnableAuthorizationServer;
import org.springframework.security.oauth2.config.annotation.web.configurers.AuthorizationServerEndpointsConfigurer;
import org.springframework.security.oauth2.provider.token.TokenEnhancerChain;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

//Tercera Clase
//En esta clase se crea el token
@Configuration
@EnableAuthorizationServer
public class AuthorizationServer extends AuthorizationServerConfigurerAdapter{
	
	@Value("${security.jwt.client-id}")
	private String clientId;
	
	@Value("${security.jwt.client-secret}")
	private String clientSecret;
	
	@Value("${security.jwt.grant-type}")
	private String grantType;
	
	@Value("${security.jwt.scope-read}")
	private String scopeRead;
	
	@Value("${security.jwt.scope-write}")
	private String scopeWrite = "write";
	
	@Value("${security.jwt.resource-ids}")
	private String resourceIds;
	
	@Autowired
	private TokenStore tokenStore;
	
	@Autowired
	private JwtAccessTokenConverter accessTokenConverter;
	
	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private BCryptPasswordEncoder bcrypt;

	
	//Creando token en memoria
	//El clientId debe estar englobado con bcrypt
	//Ámbito de lectura y escritura, la configuración
	//Tiempo de vida de los tokens
	@Override
	public void configure(ClientDetailsServiceConfigurer configurer) throws Exception {
		configurer.inMemory().withClient(clientId).secret(bcrypt.encode(clientSecret)).authorizedGrantTypes(grantType)//refresh_Token
		.scopes(scopeRead, scopeWrite).resourceIds(resourceIds).accessTokenValiditySeconds(20000)
		.refreshTokenValiditySeconds(0);
	}
	
	//Enlazar todas las configuraciones previas para generar el token
	@Override
	public void configure(AuthorizationServerEndpointsConfigurer endpoints) throws Exception{
		TokenEnhancerChain enhancerChain = new TokenEnhancerChain();
		enhancerChain.setTokenEnhancers(Arrays.asList(accessTokenConverter));
		endpoints.tokenStore(tokenStore).accessTokenConverter(accessTokenConverter).tokenEnhancer(enhancerChain).authenticationManager(authenticationManager);
		//.pathMapping("/oauth/token", "login");
	}
	

}
