package jsc.com.authcodedemo.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.net.MalformedURLException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;

@Configuration
public class JwtDecoderConfig {

    @Value("${spring.security.oauth2.resourceserver.jwt.jwk-set-uri}")
    private String jwkSetUri;

    @Bean
    public JwtDecoder jwtDecoder() throws MalformedURLException {
        // Create a trust manager that doesn't validate certificate chains
        TrustManager[] trustAllCerts = new TrustManager[]{
                new X509TrustManager() {
                    public X509Certificate[] getAcceptedIssuers() {
                        return new X509Certificate[0];
                    }
                    public void checkClientTrusted(X509Certificate[] certs, String authType) {}
                    public void checkServerTrusted(X509Certificate[] certs, String authType) {}
                }
        };

        // Create a custom NimbusJwtDecoder with relaxed SSL validation
        NimbusJwtDecoder.JwkSetUriJwtDecoderBuilder jwkSetUriJwtDecoderBuilder =
                NimbusJwtDecoder.withJwkSetUri(jwkSetUri);

        try {
            // Configure SSL
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            // Apply the SSL context to the decoder
            jwkSetUriJwtDecoderBuilder.restOperations(
                    RestTemplateBuilder
                            .create()
                            .sslContext(sslContext)
                            .hostnameVerifier((hostname, session) -> true)
                            .build()
            );
        } catch (NoSuchAlgorithmException | KeyManagementException e) {
            // Log the error but continue with default SSL configuration
            System.err.println("Error configuring SSL context for JWT decoder: " + e.getMessage());
        }

        return jwkSetUriJwtDecoderBuilder.build();
    }

    // Helper class to build a RestTemplate with custom SSL settings
    private static class RestTemplateBuilder {
        private SSLContext sslContext;
        private javax.net.ssl.HostnameVerifier hostnameVerifier;

        private RestTemplateBuilder() {}

        public static RestTemplateBuilder create() {
            return new RestTemplateBuilder();
        }

        public RestTemplateBuilder sslContext(SSLContext sslContext) {
            this.sslContext = sslContext;
            return this;
        }

        public RestTemplateBuilder hostnameVerifier(javax.net.ssl.HostnameVerifier hostnameVerifier) {
            this.hostnameVerifier = hostnameVerifier;
            return this;
        }

        public org.springframework.web.client.RestTemplate build() {
            org.springframework.http.client.ClientHttpRequestFactory factory =
                    new org.springframework.http.client.SimpleClientHttpRequestFactory() {
                        @Override
                        protected void prepareConnection(java.net.HttpURLConnection connection, String httpMethod)
                                throws java.io.IOException {
                            if (connection instanceof javax.net.ssl.HttpsURLConnection) {
                                javax.net.ssl.HttpsURLConnection httpsConnection =
                                        (javax.net.ssl.HttpsURLConnection) connection;
                                if (sslContext != null) {
                                    httpsConnection.setSSLSocketFactory(sslContext.getSocketFactory());
                                }
                                if (hostnameVerifier != null) {
                                    httpsConnection.setHostnameVerifier(hostnameVerifier);
                                }
                            }
                            super.prepareConnection(connection, httpMethod);
                        }
                    };

            return new org.springframework.web.client.RestTemplate(factory);
        }
    }
}
