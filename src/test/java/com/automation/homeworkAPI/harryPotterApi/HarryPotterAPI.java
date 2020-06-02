package com.automation.homeworkAPI.harryPotterApi;


import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;

public class HarryPotterAPI {

    @BeforeAll
    public static void setup() {
        baseURI = "https://www.potterapi.com/v1/";
    }

    /**
     * Automate the given test cases.
     * You can use any existing project.
     * You can automate all test cases in same class or different classes.
     * For verifying all of the use pojos.
     * Create pojo classes for Character and House in pojos package based on the provided json files.
     * <p>
     * <p>
     * Verify sorting hat  1.Send a get request to /sortingHat.
     * Request includes :2.Verify status code 200, content type application/json; charset=utf-83.
     * Verify that response body contains one of the following houses: "Gryffindor", "Ravenclaw", "Slytherin", "Hufflepuff"
     */
    @Test
    public void verifyResponseContainsHouses() {
        Response response =
                given().queryParam("apiKey", "$2a$10$8U30d60AJUAvvY407fdewOndCLvGySAgG8GU2UXfzSqkX4tRzPswS").
                        when().get("/sortingHat").prettyPeek();

        response.then().
                statusCode(200).
                contentType(ContentType.JSON);

        String school = response.body().asString().replace("\"", "");
        System.out.println("school = " + school);

        List<String> list = new ArrayList<>(Arrays.asList("Gryffindor", "Ravenclaw", "Slytherin", "Hufflepuff"));
        System.out.println("list = " + list);
        assertTrue(list.contains(school));
    }

    /**
     * Verify bad key
     * 1.Send a get request to /characters. Request includes :
     * •Header Accept with value application/json
     * •Query param key with value invalid
     * 2.Verify status code 401, content type application/json;
     * charset=utf-83.Verify response status line include message Unauthorized4.
     * Verify that response body says"error":"API Key Not Found"
     */


    @Test
    @DisplayName("Verify that response body says error : APIKeyNotFound")
    public void verifyResponseBodyError() {
        Response response =
                given().
                        queryParam("apiKey", "$2a$10$8U30d60AJUAvvY407fdewOndCLvGySAgG8GU2UXfzSqkX4tRzPswS").
                        queryParam("key", "invalid").
                        header("Accept", "application/json").
                        when().
                        get("/characters").prettyPeek();
        response.then().
                statusCode(401).
                contentType(ContentType.JSON);
        assertTrue(response.getStatusLine().contains("Unauthorized"));
        assertTrue(response.body().asString().contains("\"error\":\"API Key Not Found\""));
    }
/**
 * Verify no key
 * 1.Send a get request to /characters.
 * Request includes :•Header Accept with value application/json
 * 2.Verify status code 409, content type application/json;
 * charset=utf-83.
 * Verify response status line include message Conflict
 * 4.Verify that response body says"error":"Must pass API key for request"
 */
@Test
@DisplayName("Verify that response body says error:API Key Not Found")
public void verifyErrorMustPassKey() {
    Response response =
            given().
                    queryParam("apiKey", "$2a$10$8U30d60AJUAvvY407fdewOndCLvGySAgG8GU2UXfzSqkX4tRzPswS").
                    header("Accept", "application/json").
                    when().
                    get("/characters").prettyPeek();
    response.then().
            statusCode(409).
            contentType(ContentType.JSON);
    assertTrue(response.getStatusLine().contains("Conflict"));
    assertTrue(response.body().asString().contains("\"error\":\"Must pass API key for request\""));
}
/**
 * Verify number of characters
 * 1.Send a get request to /characters.
 * Request includes :•Header Accept with value application/json
 * •Query param key with value {{apiKey}}
 * 2.Verify status code 200, content type application/json; charset=utf-83.
 * Verify response contains 194 characters
 */

@Test
    @DisplayName("Verify response contains 194 characters")
    public void verifyResponseContainsChar(){
    Response response=
                    given().queryParam("key", "$2a$10$8U30d60AJUAvvY407fdewOndCLvGySAgG8GU2UXfzSqkX4tRzPswS").
                    header("Accept", "application/json").
                    when().get("/characters").prettyPeek();

   response.then().statusCode(200).contentType(ContentType.JSON);


    }
}
