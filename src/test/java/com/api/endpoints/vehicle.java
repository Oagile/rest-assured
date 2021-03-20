package com.api.endpoints;

import com.enums.requestEndPoints;
import com.interfaces.constants;
import com.jayway.restassured.RestAssured;
import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.api.endpoints.testDrive.get_test_drive_json;
import static com.enums.modelsLocation.*;
import static com.enums.requestEndPoints.*;
import static com.interfaces.constants.*;
import static com.interfaces.modelFields.*;
import static com.jayway.restassured.RestAssured.given;
import static com.requests.restAssuredRequests.*;
import static com.util.DataItem.*;

public class vehicle {

    static JSONParser parser = new JSONParser();

    public static Response getVehicleOptions (RequestSpecification requestSpecification , String vehicleId)
    {
        String ENDPOINT = API_VEHICLE.toString() +"/"+ vehicleId + VEHICLE_OPTIONS.toString();
        return getRequest(requestSpecification, ENDPOINT);
    }


    public static Response create_vehicle(RequestSpecification requestSpecification , String vehicleGroupId,
                                          boolean manuallyCreateVehicle) throws IOException, ParseException
    {
        String ENDPOINT = manuallyCreateVehicle ? API_VEHICLE.toString()
                                                : IMPORT_VEHICLE.toString();

        String VIN  = manuallyCreateVehicle ? TD_NEW_VEHICLE_VIN
                                            : VEHICLE_IMPORT_VIN;

        JSONObject createNewVehicleObject;
        if(manuallyCreateVehicle) {
            createNewVehicleObject = (JSONObject) parser.parse(new FileReader(MANUALLY_CREATE_VEHICLE_JSON.toString()));
            createNewVehicleObject.put("vin17", VIN);
        }else{
            createNewVehicleObject = new JSONObject();
            createNewVehicleObject.put("vin7", VIN);
        }

        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, 6);

        createNewVehicleObject.put("assignmentStartDate", new SimpleDateFormat("yyyy-MM-dd").format(date) );
        createNewVehicleObject.put("assignmentEndDate" , sdf.format(c.getTime()));

        createNewVehicleObject.put("vehicleGroupId",  vehicleGroupId);

        return postRequest(requestSpecification, createNewVehicleObject,  ENDPOINT);
    }


    public static Response create_vehicle_group(RequestSpecification requestSpecification) throws IOException, ParseException
    {
        String ENDPOINT = requestEndPoints.VEHICLE_GROUP.toString();
        JSONObject createNewVehicleGroupObject = (JSONObject) parser.parse(new FileReader(CREATE_VEHICLE_GROUP_JSON.toString()));
        createNewVehicleGroupObject.put("managingOutletId" , TD_OUTLET_ID);
        return postRequest(requestSpecification, createNewVehicleGroupObject,  ENDPOINT);
    }

    public static Response update_vehicle_group(RequestSpecification requestSpecification, String vehicleGroupId)
    {
        String ENDPOINT = requestEndPoints.VEHICLE_GROUP.toString() +"/"+ vehicleGroupId;

        JSONObject updateVehicleGroupObject = new JSONObject();
        updateVehicleGroupObject.put("name" , "Rest Assured");
        updateVehicleGroupObject.put("description" , "Automated Testing Is Awesome");
        return putRequest(requestSpecification, updateVehicleGroupObject,  ENDPOINT);
    }

    public static Response get_vehicle_groups(RequestSpecification requestSpecification) {
        return  getRequest(requestSpecification, VEHICLE_GROUP_LIST.toString());
    }

    public void test_cleanUp(RequestSpecification requestSpecification){


    }


    public static Response delete_vehicle_group(RequestSpecification requestSpecification, String vehicle_group_id)
    {
        String ENDPOINT = VEHICLE_GROUP.toString() + "/" + vehicle_group_id;

        return   given()
                .spec(requestSpecification)
                .queryParam("priceDeletionConfirmed", false)
                .delete(ENDPOINT);
    }


    public static Response delete_vehicle(RequestSpecification requestSpecification, String vehicle_id)
    {
        String ENDPOINT = API_VEHICLE.toString() + "/" + vehicle_id;
        return given()
                .spec(requestSpecification)
                .delete(ENDPOINT);
    }

    public static Response get_vehicle(RequestSpecification requestSpecification, String vehicle_id)
    {
        String ENDPOINT = API_VEHICLE.toString() + "/" + vehicle_id;
        return getRequest(requestSpecification, ENDPOINT);
    }

    public static Response get_vehicle_damage(RequestSpecification requestSpecification, String vehicle_id)
    {
        String ENDPOINT = VEHICLE_DAMAGE.toString();
        return  given()
                .spec(requestSpecification)
                .queryParam("vehicleId", vehicle_id)
                .get(ENDPOINT);
    }

    public static Response getBookableVehicles(RequestSpecification requestSpecification, boolean newVehicle,
                                                                                          boolean importVehicle) {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 12);

        String searchText = newVehicle ? TD_NEW_VEHICLE_VIN : "";

        if ( importVehicle && newVehicle){
            searchText = VEHICLE_IMPORT_VIN;
        }

        return given()
                .spec(requestSpecification)
                .queryParam(SEARCH_TEXT, searchText)
                .queryParam(LOCALE, "en_GB")
                .queryParam(USAGE_TYPE_CODE, TD_USAGE_TYPE)
                .queryParam(AVAILABLE_FROM , new SimpleDateFormat("yyyy-MM-dd").format(date))
                .queryParam(AVAILABLE_TO,  sdf.format(c.getTime()))
                .queryParam(OUTLETS, TD_OUTLETS)
                .get(BOOKABLE_VEHICLES.toString());
    }



    public static Response getBookableVehiclesFromTestDriveCenter(RequestSpecification requestSpecification)
    {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        c.add(Calendar.DAY_OF_MONTH, 12);

        return  given()
                .spec(requestSpecification)
                .queryParam(TEXT_SEARCH , "")
                .queryParam(TEXT_SEARCH_LOCALE , "en_GB")
                .queryParam(USAGE_TYPE_CODE, TD_USAGE_TYPE)
                .queryParam(START_DATE, new SimpleDateFormat("yyyy-MM-dd").format(date))
                .queryParam(END_DATE,  sdf.format(c.getTime()))
                .queryParam(OUTLETS,  TD_OUTLETS)
                .queryParam(OUTLET_TYPE , "TD")
                .queryParam(SERVICE_TYPES, "CATALOG")
                .queryParam(SERVICE_TYPES, "TEST_DRIVE")
                .queryParam(LIMIT , "20")
                .queryParam(OFFSET, "0")
                .get(D2D_BOOKABLE_VEHICLES.toString());
    }

    public static Response add_blocker(RequestSpecification requestSpecification , String startTime ,
                                                                    String endTime, String vehicleId) throws IOException, ParseException {

        String ENDPOINT = API_VEHICLE.toString() + "/" + vehicleId+  UNAVAILABILITY.toString();

        JSONObject addBlockerObject = (JSONObject) parser.parse(new FileReader(ADD_BLOCKER_JSON.toString()));
        addBlockerObject.put(START_DATE, startTime);
        addBlockerObject.put(END_DATE, endTime);
        addBlockerObject.put(REPEAT_UNTIL, endTime);

        return postRequest(requestSpecification, addBlockerObject, ENDPOINT);
    }

    public static Response add_status (RequestSpecification requestSpecification , String vehicleId)
                                                                                                    throws IOException, ParseException {
        String ENDPOINT = API_VEHICLE.toString() + "/" + vehicleId + STATUS.toString();
        Timestamp timestamp = new Timestamp(System.currentTimeMillis());

        JSONObject addStatusObject = (JSONObject) parser.parse(new FileReader(ADD_VEHICLE_STATUS_JSON.toString()));

        addStatusObject.put(TIME_STAMP, timestamp.getTime());

        return putRequest(requestSpecification , addStatusObject, ENDPOINT);
    }

    public static Response add_damage (RequestSpecification requestSpecification , String vehicleId)
                                                                                                throws IOException, ParseException {
        Date date = new Date();
        JSONObject addDamageObject = (JSONObject) parser.parse(new FileReader(ADD_VEHICLE_DAMAGE_JSON.toString()));
        addDamageObject.put(OCCURRENCE_DATE,   new SimpleDateFormat("yyyy-MM-dd").format(date));
        addDamageObject.put(VEHICLE_ID, vehicleId);

        return postRequest(requestSpecification , addDamageObject , DAMAGE.toString());
    }


    public static Response delete_damage (RequestSpecification requestSpecification , String damageId ,
                                                                                      String currentUserId,
                                                                                      String vehicleId
                                                                                    ) {

        String ENDPOINT = DAMAGE.toString() + "/" + damageId;

        return  given()
                .spec(requestSpecification)
                .queryParam(CURRENT_USER_ID, currentUserId)
                .queryParam(constants.VEHICLE_ID, vehicleId)
                .delete(ENDPOINT);
    }



    public static void remove_vehicle_blocks(RequestSpecification requestSpecification, String vehicleId, Response response ){

        String ENDPOINT = API_VEHICLE.toString() + "/" + vehicleId + CALENDAR.toString();

        String calendarEntryTimeBlocker = response.jsonPath().get(FIRST_CALENDAR_ENTRY);
        String calendarEntryUnAssigned = response.jsonPath().get(SECOND_CALENDAR_ENTRY);
        String calendarEntry = response.jsonPath().get(THIRD_CALENDAR_ENTRY);

        given().spec(requestSpecification).queryParam(CALENDAR_ENTRY_ID,calendarEntry ).delete(ENDPOINT).then().statusCode(200);
        given().spec(requestSpecification).queryParam(CALENDAR_ENTRY_ID, calendarEntryTimeBlocker).delete(ENDPOINT).then().statusCode(200);
        given().spec(requestSpecification).queryParam(CALENDAR_ENTRY_ID, calendarEntryUnAssigned).delete(ENDPOINT).then().statusCode(200);
    }

    public static Response create_test_drive_on_blocked_date(RequestSpecification requestSpecification,
                                                             String vehicleId,
                                                             String vehicleAssignmentId,
                                                             String startTime,
                                                             String endTime)
    {
        JSONObject test_drive_dto = get_test_drive_json(true);
        String ENDPOINT =  TESTDRIVE.toString();
        Map<String, String> bookingOutletMap = new HashMap<>();

        test_drive_dto.put(VEHICLE_ID , vehicleId);
        test_drive_dto.put(MF_VEHICLE_ASSIGNMENT_ID, vehicleAssignmentId);

        Map<String, String> appointment = new HashMap<>();

        appointment.put(APP_CALENDAR_ENTRY_TYPE, "TEST_DRIVE");
        appointment.put(START_TIME , startTime.trim());
        appointment.put(END_TIME, endTime.trim());

        test_drive_dto.put(MF_APPOINTMENT_OBJ, appointment );
        bookingOutletMap.put(MF_OUTLET_ID, TD_OUTLET_ID);
        bookingOutletMap.put(MF_OUTLET_DESCRIPTION, TD_OUTLET_DESCRIPTION);
        test_drive_dto.put(MF_BOOKING_OUTLET, bookingOutletMap);

        return postRequest(requestSpecification, test_drive_dto ,ENDPOINT);
    }


    public static Response update_vehicle_basic_info(RequestSpecification requestSpecification, Response response){

        String ENDPOINT = VEHICLE.toString() + VEHICLE_BASIC_INFO;
        Map vehicleResponseRoot = response.jsonPath().get(".");

        return RestAssured.given()
                .spec(requestSpecification)
                .contentType(ContentType.JSON)
                .when()
                .body(vehicleResponseRoot)
                .put(ENDPOINT);

    }

}
