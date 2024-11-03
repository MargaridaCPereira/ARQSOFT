package pt.psoft.g1.psoftg1.idgeneratormanagement;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class IdGeneratorConfig {

    @Value("${id.generator.class}")
    private String idGeneratorClassName;

    @Bean
    IdGeneratorType idGenerator() throws Exception {
        Class<?> clazz = Class.forName(idGeneratorClassName);
        return (IdGeneratorType) clazz.getDeclaredConstructor().newInstance();
    }
}
