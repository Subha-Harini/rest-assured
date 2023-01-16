package org.testng.test;


import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;
import java.io.IOException;
import java.util.Map;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GenericTest {

    @Test(description = "Rest Assured with testng post method", groups = { "api"}, priority = 0)
    public void postTest(String baseUrl, String path, Map<String, String> headers, String payload) {
         // Given
        Optional response =   given().baseUri(baseUrl)
                .headers(headers)
                // When
                .when().body( payload)
                .post(path)
                // Then
                .then()
                .statusCode(200)
                .body("message", equalTo("Success"))
                .extract()
                .as(Optional.class);
        ;
        System.out.println(response.get());

    }

    @Test(description = "Rest Assured with testng get method", dependsOnMethods = {"api"}, groups = { "apitestUser"}, priority=0)
    public void getTest(String baseUrl, String path, Map<String, String> headers) throws IOException, ParseException {

       // Given
        Optional response =   given().baseUri(baseUrl)
                .headers(headers)
                // When
                .when()
                .get(path)
                // Then
                .then()
                .statusCode(200)
                .body("message", equalTo("Success"))
                .extract()
                .as(Optional.class)
        ;
        System.out.println(response.get());
    }

}
