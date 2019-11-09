package com.github.yannickkirschen.spring.bearer.token;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * The {@link TokenProperties} are the properties for the token as defined in application.yml.
 *
 * @author Yannick Kirschen
 * @implNote Though it is bad practice to use {@link SuppressWarnings}, it is used in that class, so that the IDE won't grumble.
 * @since 1.0.0
 */
@Component
@ConfigurationProperties("token")
@SuppressWarnings("unused")
public final class TokenProperties {
    private String header;
    private String prefix;
    private String type;
    private String issuer;
    private String audience;
    private Integer cache;
    private String authenticationUrl;
    private String publicUrl;
    private String secret;

    private TokenProperties() {}

    public String getHeader() { return header; }

    void setHeader(String header) { this.header = header; }

    public String getPrefix() { return prefix; }

    void setPrefix(String prefix) { this.prefix = prefix; }

    public String getType() { return type; }

    void setType(String type) { this.type = type; }

    public String getIssuer() { return issuer; }

    void setIssuer(String issuer) { this.issuer = issuer; }

    public String getAudience() { return audience; }

    void setAudience(String audience) { this.audience = audience; }

    Integer getCache() { return cache; }

    void setCache(Integer cache) { this.cache = cache; }

    public String getAuthenticationUrl() { return authenticationUrl; }

    public void setAuthenticationUrl(String authenticationUrl) { this.authenticationUrl = authenticationUrl; }

    public String getPublicUrl() { return publicUrl; }

    public void setPublicUrl(String publicUrl) { this.publicUrl = publicUrl; }

    public String getSecret() { return secret; }

    public void setSecret(String secret) { this.secret = secret; }

    @Override
    public String toString() {
        return "TokenProperties{" +
            "header='" + header + '\'' +
            ", prefix='" + prefix + '\'' +
            ", type='" + type + '\'' +
            ", issuer='" + issuer + '\'' +
            ", audience='" + audience + '\'' +
            ", cache=" + cache +
            ", authenticationUrl='" + authenticationUrl + '\'' +
            ", publicUrl='" + publicUrl + '\'' +
            ", secret='" + secret + '\'' +
            '}';
    }
}
