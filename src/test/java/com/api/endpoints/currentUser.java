package com.api.endpoints;

import com.jayway.restassured.specification.RequestSpecification;

import static com.enums.requestEndPoints.CURRENT_USER;
import static com.interfaces.modelFields.MF_TDA_USER_ID;
import static com.requests.restAssuredRequests.getRequest;


public class currentUser {

    public static String get_tda_current_user (RequestSpecification requestSpecification)
    {
              return getRequest(requestSpecification , CURRENT_USER.toString()).then().extract().jsonPath().getString(MF_TDA_USER_ID);
    }
}
