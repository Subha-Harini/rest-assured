package org.testng.test;

import com.google.gson.Gson;
import com.project.springbootexample.entity.User;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Parameters;
import org.testng.annotations.Test;
import org.testng.code.EmpBusinessLogic;
import org.testng.code.EmployeeDetails;
import java.util.List;

import static io.restassured.RestAssured.given;


public class SampleTest {

    EmpBusinessLogic empBusinessLogic = new EmpBusinessLogic();
    EmployeeDetails employee = new EmployeeDetails();

    @DataProvider(name = "provider")
    public static Object[][]  data() {
        return new Object[][] {{1}, {2}};
    }

    @Test(groups = { "functest"}, dataProvider = "provider")
    public void test(int number) {
        System.out.println("test method : " + Thread. currentThread(). getName());
        System.out.println(number<4);
    }

    @Test(groups = { "functest"})
    @Parameters({"val1", "val2", "val3"})
    public void testCalculateAppriasal(String name, int age, double salary) {
        System.out.println("testCalculateAppriasal method : " + Thread. currentThread(). getName());
        employee.setName(name);
        employee.setAge(age);
        employee.setMonthlySalary(salary);

        double appraisal = empBusinessLogic.calculateAppraisal(employee);
        Assert.assertEquals(500, appraisal, 0.0, "500");
    }

    // Test to check yearly salary
    @Test(groups = { "functest"})
    public void testCalculateYearlySalary() {
        System.out.println("testCalculateYearlySalary method : " + Thread. currentThread(). getName());
        employee.setName("Rajeev");
        employee.setAge(25);
        employee.setMonthlySalary(8000);

        double salary = empBusinessLogic.calculateYearlySalary(employee);
        Assert.assertEquals(96000, salary, 0.0, "8000");
    }

    //rest template with testng
    @Test(description = "Rest template with testng ", groups = { "apitest"}, priority = 1)
    public void getUserByAge () {
        String addURI = "http://localhost:9090/getUserFilterByAge/20";
        HttpHeaders headers = new HttpHeaders();
        headers.add("Accept", "application/json");
        headers.add("Content-Type", "application/json");
        RestTemplate restTemplate = new RestTemplate();
        String entity = null;
        ResponseEntity<String> response =restTemplate.getForEntity(addURI, String.class);
        Assert.assertEquals(response.getStatusCode(), HttpStatus.OK);
    }

    @Test(description = "Rest Assured with testng ", groups = { "apitest"}, priority = 0)
    public void getUsersByAgeUsingRestAssured()  {

        // Given
        List response = given().baseUri("http://localhost:9090/")
                // When
                .when()
                .get("getUserFilterByAge/20")
                // Then
                .then()
                .statusCode(200)
                .extract()
                .as(List.class);
               // .body("message", equalTo("Successfully! Record has been fetched."))
        ;
        //System.out.println(response);
        Gson gson = new Gson();
        User user1 = gson.fromJson(response.get(0).toString(), User.class);
        Assert.assertEquals( user1.getName() , "Basant");
    }

}