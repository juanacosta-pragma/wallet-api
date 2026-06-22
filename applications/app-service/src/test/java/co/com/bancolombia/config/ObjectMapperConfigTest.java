package co.com.bancolombia.config;

import org.junit.jupiter.api.Test;
import org.reactivecommons.utils.ObjectMapper;
import org.reactivecommons.utils.ObjectMapperImp;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ObjectMapperConfigTest {

    @Test
    void testObjectMapperBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ObjectMapperConfig.class);
        ObjectMapper objectMapper = context.getBean(ObjectMapper.class);
        assertNotNull(objectMapper);
        assertTrue(objectMapper instanceof ObjectMapperImp);
        context.close();
    }

    @Test
    void testObjectMapperBeanExists() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ObjectMapperConfig.class)) {
            assertThat(context.containsBean("objectMapper")).isTrue();
        }
    }

    @Test
    void testObjectMapperBeanCreation() {
        try (AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext(ObjectMapperConfig.class)) {
            Object mapper = context.getBean("objectMapper");
            assertThat(mapper).isNotNull();
        }
    }
}