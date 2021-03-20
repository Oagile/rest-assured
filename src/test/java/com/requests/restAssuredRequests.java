package com.requests;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;

import static com.jayway.restassured.RestAssured.given;

public class restAssuredRequests {


    public static Response postRequest(RequestSpecification requestSpecification,
                                       JSONObject object, String ENDPOINT )
    {
        return   given()
                .spec(requestSpecification)
                .body(object)
                .contentType(ContentType.JSON)
                .when()
                .post(ENDPOINT);
    }

    public static Response putRequest(RequestSpecification requestSpecification, JSONObject jsonObject ,String ENDPOINT )
    {
        return   given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .when()
                .body(jsonObject)
                .put(ENDPOINT);
    }


    public static Response getRequest(RequestSpecification requestSpecification, String ENDPOINT )
    {
        return   given()
                .spec(requestSpecification)
                .get(ENDPOINT);
    }

    public static Response deleteRequest(RequestSpecification requestSpecification, String ENDPOINT )
    {
        return  given()
                .spec(requestSpecification)
                .delete(ENDPOINT);
    }

}
