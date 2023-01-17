package org.testng.code;


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
import org.testng.test.GenericTest;

import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class SampleApplication {
    static String path, baseUrl;
    static JSONObject payload = new JSONObject();
    static OpenAPI openAPI = new OpenAPIV3Parser().read("http://localhost:9010/v3/api-docs/");
    static GenericTest gt = new GenericTest();

    public static void main(String[] args) {
        baseUrl = openAPI.getServers().get(0).getUrl();
        System.out.println("Base URL : " +baseUrl);
        Paths paths = openAPI.getPaths();
        for (String pathname : openAPI.getPaths().keySet()) {
            path = pathname;
            PathItem pathItem = openAPI.getPaths().get(pathname);
            resolvePath(pathItem);
        }
    }

    private static void resolvePath(PathItem pathItem)  {
        Map<String, String> inputHeaders = new HashMap<>();
        if(pathItem.getPost() != null){
            Operation op = pathItem.getPost();
           System.out.println("POST Operation : "+path);
            for (Map.Entry<String, MediaType> entry : op.getRequestBody().getContent().entrySet()) {
                inputHeaders.put("Content-Type", entry.getKey());
                MediaType m = entry.getValue();
                System.out.println(m.getSchema().get$ref());
                try {
                    getJsonSchema(m.getSchema().get$ref());
                } catch (JSONException | IOException | ParseException e) {
                    e.printStackTrace();
                }
            }
           // gt.postTest(baseUrl,path,inputHeaders,payload.toString());
        }
        if(pathItem.getGet() != null){
            System.out.println("GET Operation : " + path);
            if(!path.contains("{")) {
                String passwordInput = (String) payload.get("userId") + ":" + (String) payload.get("password");
                inputHeaders.put("Authorization", "Basic " + Base64.encodeBase64String(passwordInput.getBytes()));
                try {
                    //gt.getTest(baseUrl, path, inputHeaders);
                } //catch (IOException | ParseException e) {
                    catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private static void getJsonSchema(String schemaName) throws JSONException, IOException, ParseException {

        JSONParser parser = new JSONParser();
        if(schemaName.contentEquals("#/components/schemas/User")){
            System.out.println(openAPI.getComponents().getSchemas().get("User").getProperties().keySet());
           Schema s = openAPI.getComponents().getSchemas().get("User");
            // using iterators
            Iterator<Map.Entry<String, Schema>> itr = s.getProperties().entrySet().iterator();
            while(itr.hasNext())
            {
                Map.Entry<String, Schema> entry = itr.next();
                System.out.println("Key = " + entry.getKey() +
                        ", Value = " +  entry.getValue().getType());
               JSONObject data = (JSONObject) parser.parse(new FileReader("C:\\Users\\hkannan\\Desktop\\Harini\\Brodcom\\sym\\java-testng\\src\\main\\resources\\post1.json"));
               System.out.println(data.get(entry.getKey()));
               payload.put(entry.getKey(), data.get(entry.getKey()));
            }
        }

    }

}
