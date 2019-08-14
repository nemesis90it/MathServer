package com.nemesis.mathserver.security;

import com.nemesis.mathserver.security.handler.CustomAccessDeniedHandler;
import com.nemesis.mathserver.security.handler.LoginFailureHandler;
import com.nemesis.mathserver.security.handler.LoginSuccessfulHandler;
import com.nemesis.mathserver.security.handler.LogoutSuccessfulHandler;
import com.nemesis.mathserver.security.service.CustomUserDetailsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;

import javax.annotation.Resource;
import javax.sql.DataSource;

import static com.oracle.tools.packager.StandardBundlerParam.APP_NAME;


@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {


    @Autowired
    private LoginSuccessfulHandler loginSuccessfulHandler;

    @Autowired
    private LoginFailureHandler loginFailureHandler;

    @Autowired
    private LogoutSuccessfulHandler logoutSuccessfulHandler;

    @Autowired
    private CustomAccessDeniedHandler customAccessDeniedHandler;

    @Autowired
    private DataSource jdbcDatasource;

    @Resource(name = "customUserDetailsService")
    private CustomUserDetailsService userDetailsService;


    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userDetailsService)
                .and().jdbcAuthentication().dataSource(jdbcDatasource);

    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {

        // TODO: andando su /app/home senza cookie non ricevo "accesso negato"

        http.authorizeRequests()
                .antMatchers("/" + APP_NAME + "/**").authenticated()
//                .anyRequest().authenticated()
                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and().csrf().disable()
                .formLogin()
                .loginProcessingUrl("/login")
                .permitAll()
                .successHandler(loginSuccessfulHandler)
                .and()
                .logout()
                .permitAll()
                .logoutSuccessHandler(logoutSuccessfulHandler)
        ;

//        http.httpBasic().
//                realmName("calculator-" + APP_NAME)
//                .and().sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and().csrf().disable()
//                .authorizeRequests().antMatchers("" + APP_NAME + "/**").permitAll()
//                .anyRequest().authenticated()
//                .and()
//                .formLogin()
//                .loginProcessingUrl("/login")
//                .defaultSuccessUrl("/" + APP_NAME + "/home", true)
//                .successHandler(loginSuccessfulHandler)
//                .failureHandler(loginFailureHandler);

    }


//    @Override
//    protected void configure(HttpSecurity http) throws Exception {
//
//        //@formatter:off
//        http
//                .csrf().disable()
//                .formLogin()
//                .loginProcessingUrl("/auth/login")
//                .successHandler(loginSuccessfulHandler)
//                .failureHandler(loginFailureHandler)
//                .and()
//                .logout()
//                .logoutUrl("/auth/logout")
//                .logoutSuccessHandler(logoutSuccessfulHandler)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/auth/login").permitAll()
//                .antMatchers("/secure/admin").access("hasRole('ADMIN')")
//                .anyRequest().authenticated()
//                .and()
//                .exceptionHandling().accessDeniedHandler(customAccessDeniedHandler)
//                .authenticationEntryPoint(customAuthenticationEntryPoint)
//                .and()
//                .anonymous()
//                .disable();
//        // @formatter:on
//    }

}