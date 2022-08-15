package org.experimental;

import static org.hamcrest.Matchers.equalTo;

import java.util.Set;

import org.junit.jupiter.api.Test;

import io.quarkus.test.common.QuarkusTestResource;
import io.quarkus.test.junit.QuarkusTest;
import io.quarkus.test.oidc.server.OidcWiremockTestResource;
import io.restassured.RestAssured;
import io.smallrye.jwt.build.Jwt;

@QuarkusTest
@QuarkusTestResource(OidcWiremockTestResource.class)
public class BearerTokenAuthorizationTest {

    @Test
    public void testBearerToken() {
        RestAssured.given().auth().oauth2(getAccessToken("alice", Set.of("user")))
                .when().get("/api/users/me")
                .then()
                .statusCode(200)
                // the test endpoint returns the name extracted from the injected SecurityIdentity Principal
                .body("userName", equalTo("alice"));
    }

    private String getAccessToken(String userName, Set<String> groups) {
        return Jwt.preferredUserName(userName)
                .groups(groups)
                .issuer("https://server.example.com")
                .audience("https://service.example.com")
                .sign();
    }
}