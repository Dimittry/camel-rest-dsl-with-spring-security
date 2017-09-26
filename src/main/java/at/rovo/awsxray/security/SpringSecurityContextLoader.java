package at.rovo.awsxray.security;

import at.rovo.awsxray.xray.Trace;
import javax.annotation.Resource;
import org.apache.camel.Exchange;
import org.apache.camel.Handler;
import org.apache.camel.Header;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

/**
 * A camel processor for decoding the HTTP basic authentication header named
 * 'Authorization' and setting up a {@link UsernamePasswordAuthenticationToken}
 * for spring-security authentication support.
 */
@Component
@Trace(metricName = "UserAuthentication")
public class SpringSecurityContextLoader extends AbstractSpringSecurityContextLoader {

  @Resource(name = "userKeyAuthProvider")
  private AuthenticationProvider authProvider;

  @Handler
  public void process(@Header("Authorization") String authHeader, Exchange exchange) throws Exception {
    super.handleRequest(authHeader, exchange, authProvider);
  }

  @Override
  protected UsernamePasswordAuthenticationToken handleAuthentication(String[] usernameAndPassword,
                                                                     Exchange exchange,
                                                                     AuthenticationProvider authProvider) {
    UsernamePasswordAuthenticationToken authToken =
        super.handleAuthentication(usernameAndPassword, exchange, authProvider);
    exchange.getIn().setHeader("userId", authToken.getPrincipal());
    return authToken;
  }
}
