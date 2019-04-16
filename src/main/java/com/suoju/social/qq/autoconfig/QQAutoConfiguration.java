package com.suoju.social.qq.autoconfig;

import com.suoju.social.qq.api.QQ;
import com.suoju.social.qq.content.QQConnectionFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.core.env.Environment;
import org.springframework.security.crypto.encrypt.Encryptors;
import org.springframework.social.UserIdSource;
import org.springframework.social.config.annotation.ConnectionFactoryConfigurer;
import org.springframework.social.config.annotation.EnableSocial;
import org.springframework.social.config.annotation.SocialConfiguration;
import org.springframework.social.config.annotation.SocialConfigurerAdapter;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionFactoryLocator;
import org.springframework.social.connect.ConnectionRepository;
import org.springframework.social.connect.UsersConnectionRepository;
import org.springframework.social.connect.jdbc.JdbcUsersConnectionRepository;
import org.springframework.social.security.AuthenticationNameUserIdSource;
import org.springframework.social.security.SpringSocialConfigurer;

import javax.sql.DataSource;

@Slf4j
@EnableSocial
@Configuration
@ConditionalOnClass(value = {SocialConfigurerAdapter.class, QQConnectionFactory.class})
@ConditionalOnProperty(prefix = "spring.social.qq.", value = "app-id")
@AutoConfigureBefore(SocialConfiguration.class)
@AutoConfigureAfter(WebMvcAutoConfiguration.class)
public class QQAutoConfiguration extends SocialConfigurerAdapter {

    @Value("${spring.social.qq.app-id}")
    private String qqAppId;

    @Value("${spring.social.qq.app-secret}")
    private String qqAppSecret;

    @Value("${spring.social.qq.provider-id}")
    private String qqProviderId;

    @Autowired
    private DataSource dataSource;

    @Override
    public UserIdSource getUserIdSource() {
        return new AuthenticationNameUserIdSource();
    }

    @Override
    public void addConnectionFactories(ConnectionFactoryConfigurer configurer, Environment environment) {
        configurer.addConnectionFactory(new QQConnectionFactory("qq", qqAppId, qqAppSecret));
    }

    @Override
    public UsersConnectionRepository getUsersConnectionRepository(ConnectionFactoryLocator connectionFactoryLocator) {
        JdbcUsersConnectionRepository repository = new JdbcUsersConnectionRepository(dataSource,
                connectionFactoryLocator, Encryptors.noOpText());
//        if (myConnectionSignUp != null) {
//            repository.setConnectionSignUp(myConnectionSignUp);
//        }
        return repository;
    }

    /**
     * 社交登录配类
     *
     * @return
     */
    @Bean
    public SpringSocialConfigurer socialSecurityConfig() {
        return new QQSpringSocialConfigurer("/login");
    }

/*    @Bean
    public SpringSocialConfigurer merryyouSocialSecurityConfig() {
        MerryyouSpringSocialConfigurer configurer = new MerryyouSpringSocialConfigurer("/login");
        return configurer;
    }*/

    /**
     * 处理注册流程的工具类
     *
     * @param factoryLocator
     * @return
     */
/*    @Bean
    public ProviderSignInUtils providerSignInUtils(ConnectionFactoryLocator factoryLocator) {
        return new ProviderSignInUtils(factoryLocator, getUsersConnectionRepository(factoryLocator));
    }*/
    @Bean
    @ConditionalOnMissingBean(QQ.class)
    @Scope(value = "request", proxyMode = ScopedProxyMode.INTERFACES)
    public QQ QQ(ConnectionRepository repository) {
        try {
            Connection<QQ> connection = repository.findPrimaryConnection(QQ.class);
            return connection != null ? connection.getApi() : null;
        } catch (Exception e) {
            log.error("Error when create qq api", e);
            return null;
        }
    }

    /*@Bean(name = {"connect/weiboConnect", "connect/weiboConnected"})
    @ConditionalOnProperty(prefix = "spring.social.", value = "auto-connection-views")
    public View weiboConnectView() {
        return new GenericConnectionStatusView("weibo", "Weibo");
    }*/
}
