package com.appointment;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.enums.requestEndPoints.*;
import static com.interfaces.constants.*;
import static com.jayway.restassured.RestAssured.given;

public class timeSlots {

    public static Response get_available_time_slots(RequestSpecification requestSpecification , String vehicleId , String assignmentId , boolean isLocalBooking)
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 2);

        String ENDPOINT = isLocalBooking ? AVAILABLE_TIME_SLOTS.toString() : D2D_AVAILABLE_TIME_SLOTS.toString();

        return  given()
                .spec(requestSpecification)
                .queryParam(VEHICLE_ID_PARAM, vehicleId)
                .queryParam(ASSIGNMENT_ID_PARAM, assignmentId)
                .queryParam(USAGE_TYPE_CODE, "DEMO")
                .queryParam(START_DATE, new SimpleDateFormat("yyyy-MM-dd").format(date))
                .queryParam(END_DATE, sdf.format(c.getTime()))
                .queryParam(GROUP_LOCALE, "true")
                .get(ENDPOINT);

    }

}
