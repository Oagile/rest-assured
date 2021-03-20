package com.tests;


import com.jayway.restassured.builder.RequestSpecBuilder;
import com.jayway.restassured.builder.ResponseSpecBuilder;
import com.jayway.restassured.filter.log.ErrorLoggingFilter;
import com.jayway.restassured.filter.log.RequestLoggingFilter;
import com.jayway.restassured.filter.log.ResponseLoggingFilter;
import com.jayway.restassured.path.json.JsonPath;
import com.jayway.restassured.specification.RequestSpecification;
import com.jayway.restassured.specification.ResponseSpecification;
import org.hamcrest.text.IsEqualIgnoringCase;
import org.json.simple.parser.ParseException;
import org.junit.*;
import org.openqa.selenium.WebDriver;


import java.io.IOException;

import static com.api.endpoints.testDrive.*;
import static com.enums.requestActionStatus.SUCCESS;
import static com.enums.testDriveAppointmentStatus.CANCELLED;
import static com.interfaces.constants.*;
import static com.interfaces.modelFields.*;
import static com.ui.SGATELandingPage.getWebDriver;
import static com.util.DataItem.*;
import static com.util.Helper.getCookieID;
import static com.util.Helper.selectOutlet;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalToIgnoringCase;
import static org.hamcrest.core.IsNull.notNullValue;

public class brandExperienceCentreTestDriveTest {

    static String Cookie, testDriveBookingId, securityToken ;
    static WebDriver webDriver;
    static RequestSpecification requestSpec;
    static RequestSpecBuilder requestSpecBuilder;
    static String sessionCookie;
    static JsonPath response;
    static ResponseSpecification checkStatusCode = new ResponseSpecBuilder().
                                                       expectStatusCode(200).
                                                       build();

    @BeforeClass
    public static void initSpec(){
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
                                 .addFilter(new ErrorLoggingFilter())
                .addFilter(new RequestLoggingFilter())
                .addFilter(new ResponseLoggingFilter());

        requestSpec = requestSpecBuilder.build();

        response = create_test_drive(requestSpec,false, false, false).then().extract().jsonPath();
        assertThat(response.getString(MF_D2D_TEST_DRIVE_BOOKING_ID), notNullValue());
        assertThat(response.getString(MF_SECURITY_TOKEN), notNullValue());
        testDriveBookingId = response.getString(MF_D2D_TEST_DRIVE_BOOKING_ID);
        securityToken = response.getString(MF_SECURITY_TOKEN);
    }


    @AfterClass
    public static void teardown(){
        response = cancel_test_drive(reqSpec(securityToken), testDriveBookingId).then().spec(checkStatusCode).extract().jsonPath();
        assertThat(response.getString(MF_TEST_DRIVE_STATUS), IsEqualIgnoringCase.equalToIgnoringCase(CANCELLED.toString()));
    }


    @Test
    public void testThatTestBECDriveAppointmentCanBeSuccessfullyBooked() {
        assertThat(response.getString(MF_REQUEST_STATUS), equalToIgnoringCase(SUCCESS.toString()));
    }

    @Test
    public void testThatTestBECDriveInvitationCanBeSent() throws IOException, ParseException{
        response = create_test_drive_invitation(reqSpec(securityToken), testDriveBookingId).then().spec(checkStatusCode).extract().jsonPath();
        assertThat(response.get(MF_DOCUMENT_URL), notNullValue()) ;
    }

    @Test
    public void testThatTestBECDriveReservationCanBeMade() throws IOException, ParseException{
        response = create_test_drive_reservation(reqSpec(securityToken), testDriveBookingId).then().spec(checkStatusCode).extract().jsonPath();
        assertThat(response.get(MF_DOCUMENT_URL), notNullValue());
    }

    private static RequestSpecification reqSpec(String secToken){
        RequestSpecBuilder requestSpecBuilder = new RequestSpecBuilder()
                                                    .addHeader(COOKIE,  sessionCookie)
                                                    .addHeader(TENANT_ID, TD_TENANT_ID)
                                                    .addHeader(OUTLET, TD_OUTLET_ID)
                                                    .addHeader(ACCEPT, MF_CONTENT_TYPE)
                                                    .addHeader(MF_SECURITY_TOKEN, secToken)
                                                    .setBaseUri(API_BASE_URI)
                                                    .addFilter(new ErrorLoggingFilter())
                                                    .addFilter(new RequestLoggingFilter())
                                                    .addFilter(new ResponseLoggingFilter());
        return requestSpecBuilder.build();
    }
}
