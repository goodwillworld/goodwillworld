package com.jboth.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.*;
import org.redisson.Redisson;
import org.redisson.config.Config;
import org.redisson.jcache.configuration.RedissonConfiguration;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.hibernate.cache.jcache.ConfigSettings;

import java.util.concurrent.TimeUnit;

import javax.cache.configuration.MutableConfiguration;
import javax.cache.expiry.CreatedExpiryPolicy;
import javax.cache.expiry.Duration;

import io.github.jhipster.config.JHipsterProperties;

@Configuration
@EnableCaching
public class CacheConfiguration {

    @Bean
    public javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration(JHipsterProperties jHipsterProperties) {
        MutableConfiguration<Object, Object> jcacheConfig = new MutableConfiguration<>();
        Config config = new Config();
        config.useSingleServer().setAddress(jHipsterProperties.getCache().getRedis().getServer());
        jcacheConfig.setStatisticsEnabled(true);
        jcacheConfig.setExpiryPolicyFactory(CreatedExpiryPolicy.factoryOf(new Duration(TimeUnit.SECONDS, jHipsterProperties.getCache().getRedis().getExpiration())));
        return RedissonConfiguration.fromInstance(Redisson.create(config), jcacheConfig);
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cm) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cm);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer(javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        return cm -> {
            createCache(cm, com.jboth.repository.UserRepository.USERS_BY_LOGIN_CACHE, jcacheConfiguration);
            createCache(cm, com.jboth.repository.UserRepository.USERS_BY_EMAIL_CACHE, jcacheConfiguration);
            createCache(cm, com.jboth.domain.User.class.getName(), jcacheConfiguration);
            createCache(cm, com.jboth.domain.Authority.class.getName(), jcacheConfiguration);
            createCache(cm, com.jboth.domain.User.class.getName() + ".authorities", jcacheConfiguration);
            createCache(cm, com.jboth.domain.PersistentToken.class.getName(), jcacheConfiguration);
            createCache(cm, com.jboth.domain.User.class.getName() + ".persistentTokens", jcacheConfiguration);
            createCache(cm, com.jboth.domain.Product.class.getName(), jcacheConfiguration);
            createCache(cm, com.jboth.domain.ProductCategory.class.getName(), jcacheConfiguration);
            createCache(cm, com.jboth.domain.ProductCategory.class.getName() + ".products", jcacheConfiguration);
            createCache(cm, com.jboth.domain.Customer.class.getName(), jcacheConfiguration);
            createCache(cm, com.jboth.domain.Customer.class.getName() + ".orders", jcacheConfiguration);
            createCache(cm, com.jboth.domain.ProductOrder.class.getName(), jcacheConfiguration);
            createCache(cm, com.jboth.domain.ProductOrder.class.getName() + ".orderItems", jcacheConfiguration);
            createCache(cm, com.jboth.domain.ProductOrder.class.getName() + ".invoices", jcacheConfiguration);
            createCache(cm, com.jboth.domain.OrderItem.class.getName(), jcacheConfiguration);
            createCache(cm, com.jboth.domain.Invoice.class.getName(), jcacheConfiguration);
            createCache(cm, com.jboth.domain.Invoice.class.getName() + ".shipments", jcacheConfiguration);
            createCache(cm, com.jboth.domain.Shipment.class.getName(), jcacheConfiguration);
            // jhipster-needle-redis-add-entry
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName, javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache == null) {
            cm.createCache(cacheName, jcacheConfiguration);
        }
    }

}
