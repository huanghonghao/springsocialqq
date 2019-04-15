package com.suoju.social.qq.api;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class IdInfo {

    @JsonProperty("client_id")
    private String clientId;

    @JsonProperty("openid")
    private String openId;
}
