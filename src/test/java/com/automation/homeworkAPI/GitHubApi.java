package com.automation.homeworkAPI;

import com.google.common.collect.Ordering;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static io.restassured.RestAssured.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

public class GitHubApi {
    @BeforeAll
    public static void setup() {
        baseURI = "https://api.github.com";
    }

    /*
    Verify organization information
    1.Send a get request to /orgs/:org. Request includes :
    •Path param org with value cucumber
    2.Verify status code 200, content type application/json; charset=utf-8
    3.Verify value of the login field is cucumber
    4.Verify value of the name field is cucumber
    5.Verify value of the id field is 320565
     */
    @Test
    @DisplayName("Verify organization information")
    public void verifyOrganization() {
        Response response =
                given().
                        queryParam("org", "cucumber").
                        when().
                        get("/orgs/:org");
        response.then().
                assertThat().statusCode(200).
                and().
                body("login", is("cucumber")).
                body("name", is("Cucumber")).
                body("id", is(320565)).
                contentType(ContentType.JSON);
    }

    /*
    Verify error message  1.Send a get request to /orgs/:org.
    Request includes :•Header Accept with value application/xml
    •Path param org with value cucumber2.Verify status code 415,
    content type application/json; charset=utf-83.
    Verify response status line include message Unsupported Media Type
     */
    @Test
    @DisplayName("Verify error message")
    public void verifyErrorMsg() {
        Response response =
                given().
                        header("Accept", "application/xml").
                        queryParam("org", "cucumber").
                        when().
                        get("/orgs/:org");
        response.then().
                assertThat().
                statusCode(415).
                contentType(ContentType.JSON).
                statusLine(containsString("Unsupported Media Type"));
    }

    /*Number of repositories
    1.Send a get request to /orgs/:org.
    Request includes :
    •Path param org with value cucumber
    2.Grab the value of the field public_repos
    3.Send a get request to /orgs/:org/repos.
    Request includes :•Path param org with value cucumber
    4.Verify that number of objects in the response  is equal to value from step 2
     */
    @Test
    @DisplayName("Verify number of repositories")
    public void verifyNumberOfRepos() {
        Response response =
                given().
                        queryParam("org", "cucumber").
                        when().
                        get("/orgs/:org");
        int reposNum = response.path("public_repos");
        Response response1 =
                given().
                        queryParam("org", "cucumber").
                        //queryParam("limit", 200).
                                when().
                        get("/orgs/:org/repos");
        List<Map<String, Object>> repos = response1.body().as(List.class);
        assertThat(repos, hasSize(reposNum));
    }

    /*
    Repository id information
    1.Send a get request to /orgs/:org/repos.
    Request includes :•Path param org with value cucumber
    2.Verify that id field is unique in every in every object in the response
    3.Verify that node_id field is unique in every in every object in the response
     */
    @Test
    @DisplayName("Repository id information")
    public void verifyIDinformation() {
        Response response =
                given().
                        queryParam("org", "cucumber").
                        when().
                        get("/orgs/:org/repos");
        List<Object> ids = new ArrayList<>();
        List<Object> nodeIDs = new ArrayList<>();
        List<Map<String, Object>> repos = response.body().as(List.class);
        for (Map<String, Object> each : repos) {
            ids.add(each.get("id"));
            nodeIDs.add(each.get("id"));
        }
        Set<Object> set = new HashSet<>(ids);
        assertThat(set, hasSize(ids.size()));
        Set<Object> nodeSet = new HashSet<>(nodeIDs);
        assertThat(nodeSet, hasSize(nodeIDs.size()));
    }

    /*
    Repository owner information
    1.Send a get request to /orgs/:org.
    Request includes :
    •Path param org with value cucumber
    2.Grab the value of the field id
    3.Send a get request to /orgs/:org/repos.
    Request includes :
    •Path param org with value cucumber
    4.Verify that value of the id inside the owner object
     in every response is equal to value from step 2
     */
    @Test
    @DisplayName("Repository owner information")
    public void verifyOwner() {
        Response response =
                given().
                        queryParam("org", "cucumber").
                        when().
                        get("/orgs/:org");
        int idNum = response.path("id");
        System.out.println(idNum);
        //Verify that value of the id inside the owner object in every response is equal to value from step 2
        Response response1 =
                given().
                        queryParam("org", "cucumber").
                        when().
                        get("/orgs/:org/repos");
        List<Integer> ownerIDList = response1.jsonPath().getList("owner.id");
        boolean equality = true;
        for (Integer each : ownerIDList) {
            if (each != idNum) equality = false;
        }
        assertThat(equality, is(true));
    }
    /*
    Ascending order by full_name sort
    1.Send a get request to /orgs/:org/repos.
    Request includes :
    •Path param org with value cucumber
    •Query param sort with value full_name
    2.Verify that all repositories are listed in
    alphabetical order based on the value of the field name
     */


    @Test
    @DisplayName("Verify that all repositories are listed in alphabetical order based on the value of the field name")
    public void verifyFullNameAlphabetical() {
        Response response =
                given().queryParam("org", "cucumber").
                        queryParam("sort", "full_name").
                        when().get("/orgs/:org/repos").prettyPeek();

        List<String> names = response.jsonPath().getList("full_name");
       boolean result = Ordering.natural().isOrdered(names);
       assertThat(result,is(true));
    }

    /**
     * Descending order by full_name sort
     * 1.Send a get request to /orgs/:org/repos.
     * Request includes :
     * •Path param org with value cucumber
     * •Query param sort with value full_name
     * •Query param direction with value desc2.
     * Verify that all repositories are listed in reverser
     * alphabetical order based on the value of the field name
     */

    @Test
    @DisplayName("Verify that all repositories are listed in reverser alphabetical order based on the value of the field name")
    public void verifyFullNameByDescendingOrder() {
        Response response =
                given().queryParam("org", "cucumber").
                        queryParam("sort", "full_name").
                        queryParam("direction", "desc2").
                        when().get("/orgs/:org/repos");

        List<String> names = response.jsonPath().getList("full_name");
        System.out.println("names = " + names);

        boolean result = Ordering.natural().reverse().isOrdered(names);
        assertThat(result,is(true));
    }

/**
 Default sort
 1.Send a get request to /orgs/:org/repos. 
 Request includes :
 •Path param org with value cucumber
 2.Verify that by default all repositories are listed 
 in descending order based on the value of the field created_at
 */
@Test
@DisplayName("Verify that by default all repositories are listed in descending order based on the value of the field created_at")
    public void verifyDefaultCreatedDateDescendingOrder(){
    Response response= given().queryParam("org", "cucumber").
            when().get("/orgs/:org/repos").prettyPeek();
    List<String> created_at = response.jsonPath().getList("created_at");
    boolean result = Ordering.natural().reverse().isOrdered(created_at);
    assertThat(result,is(true));

}
    private LocalDateTime convertStringToLocalDateTime(String dateTime){
        String date = dateTime.substring(0, dateTime.indexOf("T"));
        String time = dateTime.substring(dateTime.indexOf("T")+1, dateTime.indexOf("Z"));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        LocalDateTime result = LocalDateTime.parse(date+" "+time, formatter);
        return result;
    }
}
