package com.tests;

import com.jayway.restassured.response.Response;
import com.util.TestSetUp;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.io.IOException;

import static com.api.endpoints.assignments.assign_vehicle_to_group;
import static com.api.endpoints.assignments.delete_assignment;
import static com.api.endpoints.currentUser.get_tda_current_user;
import static com.api.endpoints.testDrive.*;
import static com.api.endpoints.vehicle.*;
import static com.appointment.timeSlots.get_available_time_slots;
import static com.interfaces.modelFields.*;
import static com.interfaces.responseMessages.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;

public class vehicleAdministrationTest extends TestSetUp
{

    @Before
    public void testInit() throws IOException, ParseException
    {
        checkIfPreviousRecordsWereCleanedUp();
        setResponse(create_vehicle_group(getRequestSpecification()).then().extract().jsonPath());
        setVehicleGroupId(getResponse().getString(MF_TD_VEHICLE_GROUP_ID));
        setResponse(create_vehicle(getRequestSpecification(), getVehicleGroupId() , false).then().spec(getCheckStatusCodeSuccess()).extract().jsonPath());
        assertThat(getResponse().getString(MF_RSP_CODE_DESCRIPTION) , containsString(THE_VEHICLE_WAS_SUCCESSFULLY_IMPORTED));
        setVehicleId(getResponse().getString(MF_TD_VEHICLE_ID));
        setResponse(get_vehicle(getRequestSpecification(), getVehicleId()).then().spec(getCheckStatusCodeSuccess()).extract().jsonPath());
        setVehicleAssignmentPeriodId(getResponse().get(MF_VEHICLE_ASSIGNMENT_PERIOD));
    }

    private void checkIfPreviousRecordsWereCleanedUp() {
         /*TODO
                Check if the clean up happened successfully
          */
    }

    @After
    public void cleanUp()
    {
        setResponse (get_vehicle(getRequestSpecification(), getVehicleId()).then().spec(getCheckStatusCodeSuccess()).extract().jsonPath());
        setVehicleAssignmentPeriodId(getResponse().get(MF_VEHICLE_ASSIGNMENT_PERIOD));
        delete_assignment(getRequestSpecification(), getVehicleId() ,getVehicleAssignmentPeriodId()).then().spec(getCheckStatusCodeSuccess());
        delete_vehicle(getRequestSpecification(), getVehicleId()).then().spec(getCheckStatusCodeAccepted());
        delete_vehicle_group(getRequestSpecification(), getVehicleGroupId()).then().spec(getCheckStatusCodeAccepted());
    }


    @Test
    public void testThatABlockerCanBeSuccessfullyAdded() throws IOException, ParseException
    {
        Response rs = get_available_time_slots(TestSetUp.getRequestSpecification(), getVehicleId(), getVehicleAssignmentPeriodId() , true);
        appointmentTimes = get_vehicle_appointmentSlots(rs);
        appointmentTimes[2] = getVehicleId();

        setResponse(add_blocker(TestSetUp.getRequestSpecification(), appointmentTimes [0], appointmentTimes [1], appointmentTimes [2])
                    .then().spec(getCheckStatusCodeSuccess()).extract().jsonPath());
        setResponse(create_test_drive_on_blocked_date(TestSetUp.getRequestSpecification(), getVehicleId() , getVehicleAssignmentPeriodId(),appointmentTimes [0], appointmentTimes [1])
                     .then().spec(getCheckStatusCodeSuccess()).extract().jsonPath());
        assertThat(getResponse().getString(MF_RSP_CODE_DESCRIPTION), containsString(VEHICLE_UNAVAILABLE_FOR_BOOKING));
        remove_vehicle_blocks(TestSetUp.getRequestSpecification(), getVehicleId(), get_vehicle(getRequestSpecification(), getVehicleId()) );
    }

    @Test
    public void testThatAStatusCanBeSuccessfullyAdded() throws IOException, ParseException
    {
       add_status(getRequestSpecification(), getVehicleId()).then().spec(getCheckStatusCodeSuccess());
    }

    @Test
    public void testThatADamageCanBeSuccessfullyAddedAndRemoved() throws IOException, ParseException
    {
        setResponse(add_damage(getRequestSpecification(), getVehicleId()).then().spec(getCheckStatusCodeSuccess()).extract().jsonPath());
        assertThat(getResponse().getString(MF_REQUEST_STATUS), equalToIgnoringCase(VEHICLE_DAMAGE_SUCCESSFULLY_DOCUMENTED));

        setVehicleDamageId(get_vehicle_damage(getRequestSpecification(), getVehicleId()).then().spec(getCheckStatusCodeSuccess()).extract().jsonPath().getString(MF_DAMAGE_ID));
        setCurrentUserId(get_tda_current_user(getRequestSpecification()));
        setResponse(delete_damage(getRequestSpecification(), getVehicleDamageId(), getCurrentUserId(), getVehicleId()).then().spec(getCheckStatusCodeSuccess()).extract().jsonPath());
        assertThat(getResponse().getString(MF_REQUEST_STATUS), equalToIgnoringCase(VEHICLE_DAMAGE_SUCCESSFULLY_DELETED));
    }

    @Test
    public void testThatAVehicleCanBeAssignedToVehicleGroup()
    {
        delete_assignment(getRequestSpecification(), getVehicleId() ,getVehicleAssignmentPeriodId()).then().spec(getCheckStatusCodeSuccess());
        assign_vehicle_to_group(getRequestSpecification(), getVehicleId(), getVehicleGroupId()).then().spec(getCheckStatusCodeSuccess());
    }

}
