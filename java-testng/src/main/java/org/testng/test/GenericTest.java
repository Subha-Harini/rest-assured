package org.testng.test;


import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Operation;
import io.swagger.v3.oas.models.PathItem;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.oas.models.media.MediaType;
import io.swagger.v3.oas.models.media.Schema;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.apache.commons.codec.binary.Base64;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.testng.code.SampleApplication;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class GenericTest {
     String baseUrl;
     Map<String, String> path = new HashMap<>();
     JSONObject payload = new JSONObject();
     OpenAPI openAPI = new OpenAPIV3Parser().read("http://localhost:9010/v3/api-docs/");
     Map<String, String> headers = new HashMap<>();

    @BeforeClass
    public void initiate(){
        baseUrl = openAPI.getServers().get(0).getUrl();
        System.out.println("Base URL : " +baseUrl);
        for (String pathname : openAPI.getPaths().keySet()) {
            PathItem pathItem = openAPI.getPaths().get(pathname);
            resolvePath(pathItem, pathname);
        }
    }

    @Test(description = "Rest Assured with testng post method", groups = { "apiTestMethod"}, priority = 0)
    public void postTest() {
        System.out.println("POST Operation : " + path.get("post"));
         // Given
        Optional response =   given().baseUri(baseUrl)
                .headers(headers)
                // When
                .when().body( payload.toString())
                .post(path.get("post"))
                // Then
                .then()
                .statusCode(200)
                .body("message", equalTo("Success"))
                .extract()
                .as(Optional.class);
        ;
        //LinkedHashMap js = (LinkedHashMap) response.get() ;
        System.out.println(response.get());

    }

    @Test(description = "Rest Assured with testng post method", groups = { "apiTestMethod"}, priority = 0)
    public void postConflictTest() {
        System.out.println("POST Operation : " + path.get("post"));
        // Given
        Optional response =   given().baseUri(baseUrl)
                .headers(headers)
                // When
                .when().body( payload.toString())
                .post(path.get("post"))
                // Then
                .then()
                .statusCode(409)
                .body("errorMessage", equalTo("User with user id "+payload.get("userId")+" already exists"))
                .extract()
                .as(Optional.class);
        ;
        System.out.println(response.get());

    }

    @Test(description = "Rest Assured with testng get method", dependsOnMethods = {"postTest"}, groups = { "apitestMethod"}, priority=0)
    public void getTest()  {
        System.out.println("GET Operation : " + path.get("get"));
        Optional  response = given().baseUri(baseUrl)
                        .headers(headers)
                        .auth().preemptive().basic((String) payload.get("userId"), (String) payload.get("password"))
                        // When
                        .when()
                        .get(path.get("get"))
                        // Then
                        .then()
                        .statusCode(200)
                        .body("message", equalTo("Success"))
                        .extract()
                        .as(Optional.class);

        System.out.println(response.get());

    }

    @Test(description = "Rest Assured with testng get method", dependsOnMethods = {"postTest"}, groups = { "apitestMethod"}, priority=0)
    public void getWithParamTest(){
        System.out.println("GET with param Operation : " + path.get("getWithParam"));
        String getPath = path.get("getWithParam");
        String param = getPath.substring(getPath.indexOf("{")+1,getPath.indexOf("}"));
       Optional response = given().baseUri(baseUrl)
                .headers(headers)
                .pathParam(param,(String) payload.get(param))
                .auth().preemptive().basic((String) payload.get("userId"), (String) payload.get("password"))
                // When
                .when()
                .get(getPath)
                // Then
                .then()
                .statusCode(200)
                .body("message", equalTo("Success"))
                .extract()
                .as(Optional.class);
        System.out.println(response.get());
    }

    @Test(description = "Rest Assured with testng get method", dependsOnMethods = {"postTest"}, groups = { "apitestMethod"}, priority=0)
    public void getUnauthorizedTest(){
        Optional  response = given().baseUri(baseUrl)
                .headers(headers)
                // When
                .when()
                .get(path.get("get"))
                // Then
                .then()
                .statusCode(401)
                .body("errorMessage", equalTo("Unauthorized"))
                .extract()
                .as(Optional.class);
        System.out.println(response.get());
    }


    private void resolvePath(PathItem pathItem, String pathname)  {
        if(pathItem.getPost() != null){
            Operation op = pathItem.getPost();
            path.put("post", pathname);
            //path.put("post", (String) path.get("post") + pathname);
            for (Map.Entry<String, MediaType> entry : op.getRequestBody().getContent().entrySet()) {
                headers.put("Content-Type", entry.getKey());
                MediaType m = entry.getValue();
                System.out.println(m.getSchema().get$ref());
                try {
                    getJsonSchema(m.getSchema().get$ref());
                } catch (JSONException | IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
        }
        if(pathItem.getGet() != null){
            if(!pathname.contains("{")) {
                path.put("get", pathname);
            }else{
                path.put("getWithParam", pathname);
            }
        }
    }

    private void getJsonSchema(String schemaName) throws JSONException, IOException, ParseException {
        String schema = schemaName.substring(schemaName.lastIndexOf("/")+1);
        JSONParser parser = new JSONParser();
        System.out.println(openAPI.getComponents().getSchemas().get(schema).getProperties().keySet());
        Schema s = openAPI.getComponents().getSchemas().get(schema);
        // using iterators
        Iterator<Map.Entry<String, Schema>> itr = s.getProperties().entrySet().iterator();
        while(itr.hasNext())
        {
            Map.Entry<String, Schema> entry = itr.next();
            JSONObject data = (JSONObject) parser.parse(new FileReader("C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\sym\\java-testng\\src\\main\\resources\\post1.json"));
            payload.put(entry.getKey(), data.get(entry.getKey()));
        }
    }


}
