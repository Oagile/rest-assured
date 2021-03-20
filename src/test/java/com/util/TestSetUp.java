package com.util;

import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.filter.log.ErrorLoggingFilter;
import com.jayway.restassured.filter.log.RequestLoggingFilter;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


import static com.interfaces.constants.*;
import static com.interfaces.webElementLocators.*;
import static com.interfaces.modelFields.MF_CONTENT_TYPE;
import static com.ui.SGATELandingPage.getWebDriver;
import static com.util.DataItem.*;
import static com.util.Helper.getCookieID;
import static com.util.Helper.selectOutlet;

public class TestSetUp {

    static  String Cookie;
    static WebDriver webDriver;

    static RequestSpecification requestSpec;
    static RequestSpecBuilder requestSpecBuilder;
    static  String sessionCookie;
    String vehicleDamageId;
    String currentUserId;
    String driverId;
    String vehicleGroupId;
    String testDriveBookingId;
    String vehicleId;
    static JsonPath response;
    public boolean cancelTestDriveAppointment;

    public static RequestSpecification getRequestSpecification() {
        return requestSpec;
    }


    public static ResponseSpecification getCheckStatusCodeSuccess() {
        return checkStatusCodeSuccess;
    }

    public static ResponseSpecification getCheckStatusCodeAccepted() {
        return checkStatusCodeAccepted;
    }

    public String getVehicleAssignmentPeriodId() {
        return vehicleAssignmentPeriodId;
    }

    String vehicleAssignmentPeriodId;

    public String getVehicleDamageId() {
        return vehicleDamageId;
    }

    public void setVehicleDamageId(String vehicleDamageId) {
        this.vehicleDamageId = vehicleDamageId;
    }

    public String getCurrentUserId() {
        return currentUserId;
    }

    public void setCurrentUserId(String currentUserId) {
        this.currentUserId = currentUserId;
    }

    public void setTestDriveBookingId(String testDriveBookingId) {
        this.testDriveBookingId = testDriveBookingId;
    }

    public String getTestDriveBookingId() {
        return testDriveBookingId;
    }


    public void setDriverId(String driverId) {
        this.driverId = driverId;
    }

    public String getDriverId() {
        return driverId;
    }


    public  String[] appointmentTimes  = new String[3];

    public static void setResponse(JsonPath response) {
        TestSetUp.response = response;
    }

    public static JsonPath getResponse() {
        return response;
    }

    public void setVehicleGroupId(String vehicleGroupId) {
        this.vehicleGroupId = vehicleGroupId;
    }

    public String getVehicleGroupId() {
        return vehicleGroupId;
    }

    public void setVehicleId(String vehicleId) {
        this.vehicleId = vehicleId;
    }

    public String getVehicleId() {
        return vehicleId;
    }


    public void setVehicleAssignmentPeriodId(String vehicleAssignmentPeriodId) {
        this.vehicleAssignmentPeriodId = vehicleAssignmentPeriodId;
    }

    static ResponseSpecification checkStatusCodeSuccess = new ResponseSpecBuilder().
                                                                expectStatusCode(200).
                                                                build();

    static ResponseSpecification checkStatusCodeAccepted = new ResponseSpecBuilder().
                                                                expectStatusCode(202).
                                                                build();

    @BeforeClass
    public static void initSpec()
    {
        webDriver = getWebDriver();
        selectOutlet(TD_OUTLET, webDriver);
        Cookie = getCookieID(webDriver);
        String[] headerCookie = Cookie.split(";");
        sessionCookie =  headerCookie[0];

        requestSpecBuilder = new RequestSpecBuilder()
                                .addHeader(COOKIE,sessionCookie)
                                .addHeader(TENANT_ID, TD_TENANT_ID)
                                .addHeader(OUTLET,TD_OUTLET_ID)
                                .addHeader(ACCEPT, MF_CONTENT_TYPE)
                                .setBaseUri(API_BASE_URI)
                                .addFilter(new ErrorLoggingFilter());

        requestSpec = requestSpecBuilder.build();
    }


    @AfterClass
    public static void teardownSpec(){
        webDriver.quit();
    }

}
