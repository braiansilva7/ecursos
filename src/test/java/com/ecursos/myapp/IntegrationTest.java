package com.ecursos.myapp;

import com.ecursos.myapp.config.AsyncSyncConfiguration;
import com.ecursos.myapp.config.EmbeddedElasticsearch;
import com.ecursos.myapp.config.EmbeddedSQL;
import com.ecursos.myapp.config.JacksonConfiguration;
import com.ecursos.myapp.config.TestSecurityConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { EcursosApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class, TestSecurityConfiguration.class })
@EmbeddedElasticsearch
@EmbeddedSQL
public @interface IntegrationTest {
}
