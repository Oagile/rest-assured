package com.tests;

import static com.api.endpoints.assignments.delete_assignment;
import static com.api.endpoints.testDrive.cancel_test_drive;
import static com.api.endpoints.testDrive.create_test_drive;
import static com.api.endpoints.vehicle.*;
import com.util.TestSetUp;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import java.io.IOException;

import static com.interfaces.responseMessages.*;
import static com.interfaces.modelFields.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;

public class importVehicleAndCreateTestDriveAppointmentsTest extends TestSetUp
{
    @Before
    public void init() throws IOException, ParseException
    {
        setVehicleGroupId(create_vehicle_group(getRequestSpecification()).then().spec(getCheckStatusCodeSuccess())
                                              .extract().jsonPath().getString(MF_TD_VEHICLE_GROUP_ID));

        setResponse(create_vehicle(getRequestSpecification(), getVehicleGroupId(), true).then().spec(getCheckStatusCodeSuccess())
                                  .extract().jsonPath());
        assertThat(
                    getResponse().getString(MF_RSP_CODE_DESCRIPTION) ,
                    containsString(THE_VEHICLE_WAS_SUCCESSFULLY_CREATED)
                   );

        setVehicleId(getResponse().getString(MF_TD_VEHICLE_ID));
    }


    @After
    public void cleanUp()
    {
        setVehicleAssignmentPeriodId(get_vehicle(getRequestSpecification(), getVehicleId()).then().extract().jsonPath().get(MF_VEHICLE_ASSIGNMENT_PERIOD));
        delete_assignment(getRequestSpecification(), getVehicleId() , getVehicleAssignmentPeriodId());
        delete_vehicle(getRequestSpecification(), getVehicleId()).then().spec(getCheckStatusCodeAccepted());
        delete_vehicle_group(getRequestSpecification(), getVehicleGroupId()).then().spec(getCheckStatusCodeAccepted());
    }


    @Test
    public void testThatVehicleGroupCanBeEdited()
    {
        assertThat(
                    update_vehicle_group(getRequestSpecification(), getVehicleGroupId()).then().extract().jsonPath().get(MF_RSP_CODE_DESCRIPTION),
                    containsString(THE_VEHICLE_GROUP_WAS_SUCCESSFULLY_UPDATED)
                   );
    }

    @Test
    public void testThatVehicleCanNotBeDuplicated() throws IOException, ParseException
    {
        assertThat(
                    create_vehicle(getRequestSpecification(), getVehicleGroupId(), true).then().extract().jsonPath().getString(MF_RSP_CODE_DESCRIPTION) ,
                    containsString(VEHICLE_ALREADY_EXISTS)
                  );
    }


    @Test
    public void testThatVehicleGroupCanNotBeDuplicated() throws IOException, ParseException
    {
        assertThat(
                    create_vehicle_group(getRequestSpecification()).then().extract().jsonPath().getString(MF_RSP_CODE_DESCRIPTION),
                    containsString(VEHICLE_GROUP_ALREADY_EXISTS)
                  );
    }


    @Test
    public void testThatVehicleGroupCanNotBeDeletedWhenAssignmentExist()
    {
        assertThat(
                    delete_vehicle_group(getRequestSpecification(), getVehicleGroupId()).then().extract().jsonPath().getString(MF_RSP_CODE_DESCRIPTION),
                    containsString(VEHICLE_GROUP_CANNOT_BE_DELETED)
                   );
    }

    @Test
    public void testThatVehicleCanNotBeDeletedWhenAssignmentExist()
    {
        assertThat(
                    delete_vehicle(getRequestSpecification(), getVehicleId()).then().extract().jsonPath().get(MF_RSP_CODE_DESCRIPTION),
                    containsString(VEHICLE_CANNOT_BE_DELETED)
                   );
    }


   @Test
    public void testThatAVehicleGroupCanNotBeDeletedWhenCalendarEntriesExist()
   {
       setTestDriveBookingId(create_test_drive(getRequestSpecification(), true, true , false)
                                              .then().extract().jsonPath().getString(MF_TEST_DRIVE_BOOKING_ID));
        setResponse(delete_vehicle_group(getRequestSpecification(), getVehicleGroupId()).then().extract().jsonPath());
        assertThat(
                    getResponse().getString(MF_RSP_CODE_DESCRIPTION),
                    containsString(VEHICLE_GROUP_CANNOT_BE_DELETED)
                   );

        cancel_test_drive(getRequestSpecification(), getTestDriveBookingId()).then().spec(getCheckStatusCodeSuccess());
    }

    @Test
    public void testThatVehicleCanNotBeDeletedWhenCalendarEntriesExist()
    {
        setTestDriveBookingId(create_test_drive(getRequestSpecification(), true, true, false)
                                               .then().extract().jsonPath().getString(MF_TEST_DRIVE_BOOKING_ID));
        assertThat(
                    delete_vehicle(getRequestSpecification(), getVehicleId()).then().extract().jsonPath().getString(MF_RSP_CODE_DESCRIPTION),
                    containsString(VEHICLE_CANNOT_BE_DELETED)
                   );
        cancel_test_drive(getRequestSpecification(), getTestDriveBookingId()).then().spec(getCheckStatusCodeSuccess());
    }

}
