# Spring-Bearer

![](https://github.com/yannickkirschen/spring-bearer/workflows/Maven%20clean%20install/badge.svg)
[![](https://jitpack.io/v/yannickkirschen/spring-bearer.svg)](https://jitpack.io/#yannickkirschen/spring-bearer)
[![](https://api.dependabot.com/badges/status?host=github&repo=yannickkirschen/spring-bearer)](https://dependabot.com)

*Spring-Bearer* allows you to enable authentication/authorization in a spring application by using JWT tokens.

## Installation

pom.xml:
```xml
<project>
    <dependencies>
        <dependency>
            <groupId>com.github.yannickkirschen</groupId>
            <artifatcId>spring-bearer</artifatcId>
            <version>1.0.0</version>
        </dependency>
    </dependencies>
    
    <repositories>
        <repository>
            <id>jitpack.io</id>
            <url>https://jitpack.io</url>
        </repository>
    </repositories>
</project>
```

## Usage

Your main class:
```java
@SpringBootApplication
@ComponentScan({ "your.package", "com.github.yannickkirschen.spring.bearer" })
@EnableConfigurationProperties(TokenProperties.class)
public class Main {
    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
        JwtUserDetailsService.setDatabase(applicationContext.getBean(UserService.class)); // UserService is the implementation of JwtUserDatabase
    }
}
```

Your application.yml:
```yaml
token:
    header: "Authorization"
    prefix: "Bearer "
    type: "JWT"
    issuer: "secure-api"
    audience: "secure-app"
    authenticationUrl: "/authenticate" # The url to authenticate
    publicUrl: "/your/public/url" # Only accessible with valid token
    secret: "YOUR SECRET" # The secret to use for the generation of the token
    cache: 15000 # The number of tokens that gets cached
```

You can now access the endpoint with the headers:

```
username: USERNAME
password: PASSWORD
```

and you'll get a response of the following:

```json
{
    "token": "TOKEN"
}
```

## Contributing & Coding guidelines

There are many ways in which you can participate in the project, for example:

* Submit [bugs and feature requests](https://github.com/yannickkirschen/spring-bearer/issues) to help me improve Spring Bearer.
* Review [source code changes](https://github.com/yannickkirschen/spring-bearer/pulls).

Please respect the [Code of Conduct](https://www.contributor-covenant.org/version/1/4/code-of-conduct.html).

For the coding style, please refer to the Oracle style guide.

## License

Copyright (c) 2019, Yannick Kirschen, All rights reserved.
Licensed under the [MIT License](https://github.com/yannickkirschen/spring-bearer/blob/master/LICENSE).
Happy forking :fork_and_knife:
