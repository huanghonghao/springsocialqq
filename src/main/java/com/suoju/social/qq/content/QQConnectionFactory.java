package com.suoju.social.qq.content;

import com.suoju.social.qq.api.QQ;
import org.springframework.social.connect.support.OAuth2ConnectionFactory;

/**
 * @author huanghonghao
 * @version 1.0
 * @description 链接工厂
 * @createdate 2019/4/15 13:09
 */
public class QQConnectionFactory extends OAuth2ConnectionFactory<QQ> {

    public QQConnectionFactory(String providerId, String appId, String appSecret) {
        super(providerId, new QQServiceProvider(appId, appSecret), new QQAdapter());
    }
}
