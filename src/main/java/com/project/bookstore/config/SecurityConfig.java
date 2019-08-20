package com.project.bookstore.config;

import com.project.bookstore.service.impl.UserSecurityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserSecurityService userSecurityService;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    private static final String[] PUBLIC_MATCHERS = {
            "/css/**",
            "/js/**",
            "/images/**",
            "/login",
            "/signup",
            "/books/**",
            "/",
            "/browse/**",
            "/registration/**",
            "/search/**",
            "/searchByCategory/**",
            "/deliveryInfo/"
    };

    private static final String [] USER_MATCHERS = {
            "/account/**",
            "/basket/**",
            "/profile/**",
            "/addNewAddress/**",
            "/removeAddress/**",
            "/setAsDefaultAddress/**",
            "/setAsDefaultAddressFromCheckout/**",
            "/addressFromCheckout/**",
            "/changePassword/**",
            "/checkout/**",
            "/createOrder/**",
            "/addNewCreditCard/**",
            "/setAsDefaultPayment/**",
            "/removeCreditCard/**",
            "/paymentFromCheckout/**",
            "/wishlist/**"
    };

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers(PUBLIC_MATCHERS)
                .permitAll();
        http
                .csrf().disable().cors().disable()
                .formLogin().failureUrl("/login?error")
                .defaultSuccessUrl("/")
                .loginPage("/login").permitAll()
                .and()
                .logout().logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/").deleteCookies("remember-me").permitAll()
                .and()
                .rememberMe();
        http
                .authorizeRequests()
                .antMatchers("/admin/**")
                .hasRole("ADMIN");
        http
                .authorizeRequests()
                .antMatchers(USER_MATCHERS)
                .hasRole("USER");
        http
                .authorizeRequests()
                .antMatchers("/saveNewPassword*")
                .hasAuthority("CHANGE_PASSWORD_PRIVILEGE");

    }

    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userSecurityService).passwordEncoder(passwordEncoder());
    }
}
