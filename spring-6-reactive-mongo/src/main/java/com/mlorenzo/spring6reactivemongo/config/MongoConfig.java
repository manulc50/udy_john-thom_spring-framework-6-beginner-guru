package com.mlorenzo.spring6reactivemongo.config;

import com.mongodb.reactivestreams.client.MongoClient;
import com.mongodb.reactivestreams.client.MongoClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractReactiveMongoConfiguration;
import org.springframework.data.mongodb.config.EnableReactiveMongoAuditing;

// Para poder usar las anotaciones de auditoría @CreatedDate y @LastModifiedDate en las clases entidad
@EnableReactiveMongoAuditing
@Configuration
public class MongoConfig extends AbstractReactiveMongoConfiguration {

    @Override
    protected String getDatabaseName() {
        return "sfg";
    }

    // Sólo es necesario si necesitamos indicar configuraciones respecto a la conexión con la base de datos, como
    // por ejemplo, las credenciales requeridas, el host y el puerto donde está ejecutándose la base de datos si no
    // se trata del host y puerto por defecto, etc...
    /*@Override
    protected void configureClientSettings(MongoClientSettings.Builder builder) {
        builder.credential(MongoCredential.createCredential("root", "admin", "example".toCharArray()))
                .applyToClusterSettings(settings ->
                        settings.hosts(Collections.singletonList(new ServerAddress("127.0.0.1", 27017))));
    }*/

    @Bean
    public MongoClient mongoClient() {
        return MongoClients.create();
    }
}
