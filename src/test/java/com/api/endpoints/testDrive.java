package com.api.endpoints;

import com.jayway.restassured.http.ContentType;
import com.jayway.restassured.response.Response;
import com.jayway.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


import static com.api.endpoints.currentUser.get_tda_current_user;
import static com.api.endpoints.vehicle.getBookableVehicles;
import static com.api.endpoints.vehicle.getBookableVehiclesFromTestDriveCenter;
import static com.appointment.timeSlots.get_available_time_slots;
import static com.enums.requestEndPoints.*;
import static com.enums.modelsLocation.*;
import static com.interfaces.constants.*;
import static com.interfaces.modelFields.*;
import static com.jayway.restassured.RestAssured.given;
import static com.jayway.restassured.path.json.JsonPath.from;
import static com.requests.restAssuredRequests.getRequest;
import static com.requests.restAssuredRequests.postRequest;
import static com.requests.restAssuredRequests.putRequest;
import static com.util.DataItem.*;

public class testDrive
{

    static Response availableTimeSlotsResponse, getTestDriveBookingResponse;
    public static String startTime, endTime, vehicleId, vehicleAssignmentId;
    static JSONParser parser = new JSONParser();


    public static Response create_test_drive(RequestSpecification requestSpecification, boolean isLocalBooking,
                                                                                        boolean newVehicle,
                                                                                        boolean importVehicle){
        JSONObject test_drive_dto = get_test_drive_json(isLocalBooking);
        String ENDPOINT = isLocalBooking ?  TESTDRIVE.toString() : D2D_TESTDRIVE.toString();

        Map<String, String> bookingOutletMap = new HashMap<>();

        /* select vehicle to book*/ 
        set_test_drive_vehicle(requestSpecification, isLocalBooking, newVehicle, false, importVehicle );
        test_drive_dto.put(VEHICLE_ID , vehicleId);
        test_drive_dto.put(MF_VEHICLE_ASSIGNMENT_ID, vehicleAssignmentId);
        /* get available timeslots */                                                                                        
        availableTimeSlotsResponse = get_available_time_slots(requestSpecification, vehicleId, vehicleAssignmentId , isLocalBooking);
        /* add timeslot to the map*/                                                                                        
        Map<String, String> appointment = set_appointment_details(availableTimeSlotsResponse.asString());

        if(isLocalBooking) {
            test_drive_dto.put(MF_APPOINTMENT_OBJ, appointment );
            bookingOutletMap.put(MF_OUTLET_ID, TD_OUTLET_ID);
            bookingOutletMap.put(MF_OUTLET_DESCRIPTION, TD_OUTLET_DESCRIPTION);
            test_drive_dto.put(MF_BOOKING_OUTLET, bookingOutletMap);

        }
        else{
            test_drive_dto.put(MF_START_TIME, startTime);
            test_drive_dto.put(MF_END_TIME, endTime);
        }
		/* send the request to book the test drive and return the response */
        return postRequest(requestSpecification, test_drive_dto ,ENDPOINT);
    }


    private static void set_test_drive_vehicle(RequestSpecification requestSpecification, boolean isLocalBooking
                                                                                        , boolean newVehicle
                                                                                        , boolean changeVehicle
                                                                                        , boolean importVehicle) {

        Response bookable_vehicle = isLocalBooking  ?  getBookableVehicles(requestSpecification, newVehicle, importVehicle)
                                                    :  getBookableVehiclesFromTestDriveCenter(requestSpecification);

        if (isLocalBooking){
            vehicleId = bookable_vehicle.jsonPath().get(MF_BOOKABLE_VEHICLE);
            vehicleAssignmentId = bookable_vehicle.jsonPath().get(MF_BOOKABLE_VEHICLE_ASSIGNMENT_PERIOD);

            if (changeVehicle){
                vehicleId = bookable_vehicle.jsonPath().get(MF_CHANGE_VEHICLE);
            }
        }
        else {
                vehicleId = bookable_vehicle.jsonPath().get(MF_D2D_VEHICLE_ID);
                vehicleAssignmentId = bookable_vehicle.jsonPath().get(MF_D2D_ASSIGNMENT_ID);
        }

    }


    public static JSONObject get_test_drive_json(boolean isLocalBooking) {
        JSONObject createTestDriveObject = null;
        {
            try {
                if (isLocalBooking) {
                    createTestDriveObject = (JSONObject) parser.parse(new FileReader(BOOK_TEST_DRIVE_JSON.toString()));
                }
                else {
                    createTestDriveObject = (JSONObject) parser.parse(new FileReader(D2D_TEST_DRIVE_JSON.toString()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        return createTestDriveObject;
    }


    public static String[] get_vehicle_appointmentSlots(Response response) {
        String[] appointmentStartTimes = new String[3];
        set_appointment_details(response.asString());
        appointmentStartTimes[0] = startTime;
        appointmentStartTimes[1] = endTime;
        return appointmentStartTimes;
    }


    private static Map<String, String> set_appointment_details(String response) {
        Object o = null;
        ArrayList<Map<String,?>> start = from(response).param("start", o).get("$..");

        for (Map item : start){
            if(item.toString().contains("start=")){
                String[] startDates = item.toString().split("start=");
                String[] appStartTime = startDates[1].split(",");
                startTime =  appStartTime[0];
                endTime =  appStartTime[1].substring(5,25);
                break;
            }
        }

        Map<String, String> appointmentMap = new HashMap<>();

        appointmentMap.put(APP_CALENDAR_ENTRY_TYPE, "TEST_DRIVE");
        appointmentMap.put(START_TIME , startTime.trim());
        appointmentMap.put(END_TIME, endTime.trim());

        return  appointmentMap;

    }

    public static Response create_test_drive_contract(RequestSpecification requestSpecification , String bookingId) throws IOException, ParseException
    {
        String ENDPOINT = TESTDRIVE.toString() + "/" + bookingId + CREATE_CONTRACT.toString();
        JSONObject createTestDriveContractObject = (JSONObject) parser.parse(new FileReader(TEST_DRIVE_CONTRACT_JSON.toString()));

        getTestDriveBookingResponse = get_test_drive(requestSpecification , bookingId);

        createTestDriveContractObject.put(TEST_DRIVE_START_TIME , getTestDriveBookingResponse.jsonPath().get(MF_APPOINTMENT_START_TIME));
        createTestDriveContractObject.put(TEST_DRIVE_APPOINTMENT , getTestDriveBookingResponse.jsonPath().get(MF_TEST_DRIVE_APPOINTMENT));

        return postRequest( requestSpecification, createTestDriveContractObject ,ENDPOINT );
    }

    public static Response return_vehicle(RequestSpecification requestSpecification , String bookingId) throws IOException, ParseException
    {
        String ENDPOINT = TESTDRIVE.toString() + "/" + bookingId + RETURN_VEHICLE.toString();
        JSONObject returnVehicleObject = (JSONObject) parser.parse(new FileReader(RETURN_VEHICLE_JSON.toString()));

        getTestDriveBookingResponse = get_test_drive(requestSpecification , bookingId);

        returnVehicleObject.put(TEST_DRIVE_END_TIME, getTestDriveBookingResponse.jsonPath().get(MF_APPOINTMENT_END_TIME));

        return  putRequest( requestSpecification , returnVehicleObject ,ENDPOINT );
    }


    public static Response capture_feedback(RequestSpecification requestSpecification, String bookingId) throws IOException, ParseException
    {
        String ENDPOINT = TESTDRIVE.toString() + "/" + bookingId + CAPTURE_FEEDBACK.toString();
        JSONObject captureFeedbackObject = (JSONObject) parser.parse(new FileReader(CAPTURE_FEEDBACK_JSON.toString()));
        return  putRequest(requestSpecification , captureFeedbackObject ,ENDPOINT);
    }


    public static Response change_test_drive_price(RequestSpecification requestSpecification , String bookingId)
    {
        String ENDPOINT = TESTDRIVE.toString() + "/" + bookingId + PRICE.toString();
        JSONObject priceDetails = new JSONObject();
        priceDetails.put (MF_CHANGE_DRIVER_COMMENTS , "Rest Assured");
        priceDetails.put (MF_CHANGE_DRIVER_PRICE , "50000");
        priceDetails.put ( MF_CHANGE_DRIVER_SHARED, false);

        return  putRequest(requestSpecification ,priceDetails ,ENDPOINT );
    }

    public static Response change_test_drive_vehicle(RequestSpecification requestSpecification , String bookingId )
    {
        String ENDPOINT = TESTDRIVE.toString() + "/" + bookingId + VEHICLE.toString();
        set_test_drive_vehicle(requestSpecification, true, false, true, false);
        return   given()
                .spec(requestSpecification)
                .queryParam(VEHICLE_ID,vehicleId)
                .put(ENDPOINT);
    }

    public static Response cancel_test_drive(RequestSpecification requestSpecification, String bookingId)
    {
        String ENDPOINT = TESTDRIVE.toString() + "/" + bookingId + CANCEL_BOOKING.toString();
        JSONObject json = new JSONObject();
        json.put(MF_COMMENTS_TEXT_BLOCK, "I have changed my mind");

        return   given()
                 .spec(requestSpecification)
                 .contentType(ContentType.JSON)
                 .body(json)
                 .queryParam(MF_CURRENT_USER_ID, get_tda_current_user(requestSpecification))
                 .put(ENDPOINT);
    }

    public static Response get_test_drive(RequestSpecification requestSpecification, String bookingId)
    {
        String ENDPOINT = TESTDRIVE.toString() + "/" + bookingId;
        return getRequest(requestSpecification, ENDPOINT);
    }


    public static Response add_comments (RequestSpecification requestSpecification , String bookingID)
    {
        String ENDPOINT = TESTDRIVE.toString() + "/" + bookingID + ADD_COMMENT.toString();
        JSONObject json = new JSONObject();
        json.put(MF_COMMENTS_TEXT_BLOCK, "Testing Comments");

        return given()
               .spec(requestSpecification)
               .contentType(ContentType.JSON)
               .queryParam(MF_CHANGE_DRIVER_SHARED, true)
                .body(json)
               .put(ENDPOINT);
    }

    public static Response create_test_drive_invitation(RequestSpecification requestSpecification , String bookingId) throws IOException, ParseException
    {
        String ENDPOINT = D2D_TESTDRIVE.toString() + "/" + bookingId + INVITATION.toString();
        JSONObject createTestDriveInvitation = (JSONObject) parser.parse(new FileReader(INVITATION_JSON.toString()));

        Map<String, String> appointmentMap = new HashMap<>();
        appointmentMap.put(MF_APP_START_TIME, startTime);
        appointmentMap.put(MF_APPOINTMENT_RESPONSIBLE_PERSONID , null);
        appointmentMap.put(MF_APP_END_TIME, endTime);

        createTestDriveInvitation.put(MF_APPOINTMENT_OBJ , appointmentMap);

        return  given()
                .spec(requestSpecification)
                .queryParam(MF_TRIGGER, "AUTO")
                .contentType(ContentType.JSON)
                .body(createTestDriveInvitation)
                .post(ENDPOINT);
    }

    public static Response create_test_drive_reservation(RequestSpecification requestSpecification,  String bookingId) throws IOException, ParseException
    {
        String ENDPOINT = D2D_TESTDRIVE.toString() + "/" + bookingId + RESERVATION.toString();
        JSONObject createTestDriveReservation = (JSONObject) parser.parse(new FileReader(RESERVATION_JSON.toString()));

        Map<String, String> appointmentMap = new HashMap<>();
        appointmentMap.put(MF_APP_START_TIME, startTime);
        appointmentMap.put(MF_APPOINTMENT_RESPONSIBLE_PERSONID , null);
        appointmentMap.put(MF_APP_END_TIME, endTime);

        createTestDriveReservation.put(MF_APPOINTMENT_OBJ , appointmentMap);

        return   given()
                .spec(requestSpecification)
                .queryParam("trigger", "AUTO")
                .contentType(ContentType.JSON)
                .body(createTestDriveReservation)
                .post(ENDPOINT);
    }


    public static Response capture_damage(RequestSpecification request_spec, String vehicleId, String bookingId) throws IOException, ParseException
    {
        Date date = new Date();
        JSONObject addDamageObject = (JSONObject) parser.parse(new FileReader(ADD_VEHICLE_DAMAGE_JSON.toString()));

        addDamageObject.put(MF_CAPTURE_DAMAGE_ID , bookingId);
        addDamageObject.put(OCCURRENCE_DATE,   new SimpleDateFormat("yyyy-MM-dd").format(date));
        addDamageObject.put(MF_TD_VEHICLE_ID, vehicleId);
        return postRequest(request_spec , addDamageObject , DAMAGE.toString());
    }


    public static Response change_test_drive_driver(RequestSpecification requestSpecification , String bookingId , String driverId)
    {
        String ENDPOINT = TESTDRIVE.toString() +  "/" + bookingId + DRIVER.toString();
        JSONObject changeDriverObject = new JSONObject();
        JSONObject driverDetails , driverAddressMap;
        driverAddressMap = new JSONObject();
        driverDetails = new JSONObject();

        driverDetails.put("driverId", driverId);
        driverDetails.put("salutationCode", "MS");
        driverDetails.put("firstName", "Ben");
        driverDetails.put("lastName", "Dover");
        driverDetails.put("titleCode", "DR");
        driverDetails.put("phone", "+1 (800) 555 0000");
        driverDetails.put("cellphone", "+1 (800) 555 1533");
        driverDetails.put("email", "rest@assured.com");
        driverDetails.put("correspondenceLanguage", "en_GB");
        driverAddressMap.put("street", "742 Evergreen Terrace");
        driverAddressMap.put("additionalAddressField", "");
        driverAddressMap.put("zipCode", "50000");
        driverAddressMap.put("city", "Springfield");
        driverAddressMap.put("state", "Ohio");
        driverAddressMap.put("country", "DE");

        driverDetails.put("address", driverAddressMap);

        changeDriverObject.put("driver" , driverDetails);

        return putRequest(requestSpecification ,changeDriverObject , ENDPOINT );
    }



}