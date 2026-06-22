package co.com.bancolombia.api.config;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpHeaders;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SecurityHeadersConfigTest {

    @InjectMocks
    private SecurityHeadersConfig config;

    @Mock
    private WebFilterChain chain;

    @Test
    void testSecurityHeadersAreAdded() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.empty());

        StepVerifier.create(config.filter(exchange, chain))
                .verifyComplete();

        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        assertThat(responseHeaders.get("Content-Security-Policy")).isNotNull();
        assertThat(responseHeaders.get("Strict-Transport-Security")).isNotNull();
        assertThat(responseHeaders.getFirst("X-Content-Type-Options")).isEqualTo("nosniff");
        assertThat(responseHeaders.getFirst("Cache-Control")).isEqualTo("no-store");
    }

    @Test
    void testServerHeaderIsEmpty() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.empty());

        config.filter(exchange, chain).block();

        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        assertThat(responseHeaders.getFirst("Server")).isEmpty();
    }

    @Test
    void testPragmaHeaderIsSet() {
        MockServerHttpRequest request = MockServerHttpRequest.get("/api/test").build();
        ServerWebExchange exchange = MockServerWebExchange.from(request);

        when(chain.filter(any())).thenReturn(Mono.empty());

        config.filter(exchange, chain).block();

        HttpHeaders responseHeaders = exchange.getResponse().getHeaders();
        assertThat(responseHeaders.getFirst("Pragma")).isEqualTo("no-cache");
    }
}

