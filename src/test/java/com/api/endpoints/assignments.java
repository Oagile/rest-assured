package com.api.endpoints;

import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.enums.requestEndPoints.*;
import static com.enums.requestEndPoints.API_VEHICLE;

import static com.interfaces.modelFields.*;
import static com.requests.restAssuredRequests.deleteRequest;
import static com.requests.restAssuredRequests.postRequest;

public class assignments {

    public static Response delete_assignment(RequestSpecification requestSpecification, String vehicle_id , String assignmentId)
    {
        String ENDPOINT = API_VEHICLE.toString() +"/"+ vehicle_id + ASSIGNMENTS.toString() + "/" + assignmentId;
        return deleteRequest(requestSpecification , ENDPOINT);
    }

    public static Response assign_vehicle_to_group(RequestSpecification requestSpecification, String vehicle_id, String vehicleGroupId)
    {
        String ENDPOINT = API_VEHICLE.toString() +"/"+ vehicle_id + ASSIGNMENT.toString();
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 12);
        JSONObject vehicleGroup = new JSONObject();

        JSONObject assignment = new JSONObject();

        assignment.put(MF_START_DATE , new SimpleDateFormat("yyyy-MM-dd").format(date));
        assignment.put(MF_END_DATE , sdf.format(c.getTime()));
        vehicleGroup.put(MF_VEHICLE_GROUP_ID, vehicleGroupId);
        assignment.put(MF_VEHICLE_GROUP, vehicleGroup);

        return postRequest(requestSpecification, assignment, ENDPOINT);
    }


}
