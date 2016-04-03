package org.openvisu.security;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.csrf.CsrfFilter;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.security.web.csrf.CsrfTokenRepository;
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

public class SecurityConfiguration extends WebSecurityConfigurerAdapter
{
  @Override
  protected void configure(HttpSecurity http) throws Exception
  {
    http.formLogin().loginPage("/login").permitAll().and().logout().and().authorizeRequests()
        .antMatchers("/index.html", "/home.html", "/login.html", "/").permitAll().anyRequest().authenticated().and().csrf()
        .csrfTokenRepository(csrfTokenRepository()).and().addFilterAfter(csrfHeaderFilter(), CsrfFilter.class);
  }

  @Autowired
  public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception
  {
    auth.authenticationProvider(new AuthenticationProvider() {

      @Override
      public Authentication authenticate(Authentication authentication) throws AuthenticationException
      {
        String name = authentication.getName();
        String password = authentication.getCredentials().toString();

        // use the credentials to try to authenticate against the third party system
        if ("user".equals(name) && "pw".equals(password)) {
          List<GrantedAuthority> grantedAuths = new ArrayList<>();
          return new UsernamePasswordAuthenticationToken(name, password, grantedAuths);
        } else {
          throw new BadCredentialsException("Unable to auth against third party systems");
        }
      }

      @Override
      public boolean supports(Class< ? > authentication)
      {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
      }
    });
  }

  private Filter csrfHeaderFilter()
  {
    return new OncePerRequestFilter() {
      @Override
      protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
          throws ServletException, IOException
      {
        CsrfToken csrf = (CsrfToken) request.getAttribute(CsrfToken.class.getName());
        if (csrf != null) {
          Cookie cookie = WebUtils.getCookie(request, "XSRF-TOKEN");
          String token = csrf.getToken();
          if (cookie == null || token != null && !token.equals(cookie.getValue())) {
            cookie = new Cookie("XSRF-TOKEN", token);
            cookie.setPath("/");
            response.addCookie(cookie);
          }
        }
        filterChain.doFilter(request, response);
      }
    };
  }

  private CsrfTokenRepository csrfTokenRepository()
  {
    HttpSessionCsrfTokenRepository repository = new HttpSessionCsrfTokenRepository();
    repository.setHeaderName("X-XSRF-TOKEN");
    return repository;
  }
}
