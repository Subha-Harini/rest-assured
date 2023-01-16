package org.testng.test;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.Test;
import java.io.FileReader;
import java.io.IOException;
import org.apache.commons.codec.binary.Base64;
import java.util.Optional;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class UserTest {

    @Test(description = "Rest Assured with testng post method", groups = { "apitestUser"}, priority = 0)
    public void postTest() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(new FileReader("C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\sym\\java-testng\\src\\main\\resources\\post.json"));
        // Given
        Optional response =  given().baseUri((String)  data.get("baseUrl"))
                .headers("Content-Type","application/json")
                // When
                .when().body( data.get("payload"))
                .post((String) data.get("path"))
                // Then
                .then()
                .statusCode(200)
                .body("message", equalTo("Success")) .extract()
                .as(Optional.class);
        ;
        System.out.println(response.get());
    }

    @Test(description = "Rest Assured with testng get method", dependsOnMethods = {"postUser"}, groups = { "apitestUser"}, priority=0)
    public void getUser() throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        JSONObject data = (JSONObject) parser.parse(
                new FileReader("C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\sym\\java-testng\\src\\main\\resources\\get.json"));

        String passwordInput = (String) data.get("username") + ":" +(String) data.get("password");
        System.out.println(passwordInput +" "+ Base64.encodeBase64String(passwordInput.getBytes()));
        // Given
        Optional response =   given().baseUri((String)  data.get("baseUrl"))
                .headers("Authorization","Basic "+ Base64.encodeBase64String(passwordInput.getBytes()))
                // When
                .when().body( data.get("payload"))
                .get((String) data.get("path"))
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
