package com.tests;
import com.enums.requestActionStatus;
import com.enums.testDriveAppointmentStatus;
import com.util.TestSetUp;
import org.json.simple.parser.ParseException;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static com.api.endpoints.testDrive.*;
import static com.enums.testDriveAppointmentStatus.*;
import static com.interfaces.responseMessages.*;
import static com.interfaces.modelFields.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.hamcrest.text.IsEqualIgnoringCase.equalToIgnoringCase;


public class testDriveManagementTest extends TestSetUp
{


    @Before
    public void init()
    {
        cancelTestDriveAppointment = false;
        setResponse(create_test_drive( getRequestSpecification(),true, false, false).then().extract().jsonPath());
        setTestDriveBookingId(getResponse().getString(MF_TEST_DRIVE_BOOKING_ID));
        setDriverId(getResponse().getString(MF_TD_DRIVER_ID));

        assertThat(get_test_drive(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_TEST_DRIVE_STATUS) ,
                                  equalToIgnoringCase(testDriveAppointmentStatus.BOOKED.toString()));
    }

    @After
    public void after()
    {
        if(cancelTestDriveAppointment)
        {
           setResponse(cancel_test_drive(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath());
            assertThat(
                        getResponse().getString(MF_TEST_DRIVE_STATUS),
                        equalToIgnoringCase(CANCELLED.toString())
                      );
        }
    }


    @Test
    public void testThatTestDriveAppointmentDriverCanBeChanged()
    {
        cancelTestDriveAppointment = true;
        setResponse(change_test_drive_driver(getRequestSpecification(), getTestDriveBookingId(),  getDriverId()).then().spec(getCheckStatusCodeSuccess()).extract().jsonPath());
        assertThat(
                    getResponse().getString(MF_REQUEST_STATUS),
                    equalToIgnoringCase(requestActionStatus.SUCCESS.toString())
                  );

        assertThat(
                    getResponse().getString(MF_COMMENTS),
                    containsString (THE_FOLLOWING_ATTRIBUTES_CHANGED)
                   );
    }

    @Test
    public void testThatACommentCanBeAddedToTestDriveAppointment()
    {
        cancelTestDriveAppointment = true;
       setResponse(add_comments(getRequestSpecification(), getTestDriveBookingId()).then().spec(getCheckStatusCodeSuccess()).extract().jsonPath());
        assertThat(
                    getResponse().getString(MF_REQUEST_STATUS),
                    equalToIgnoringCase(requestActionStatus.SUCCESS.toString())
                   );

        assertThat(
                    getResponse().getString(MF_TEST_DRIVE_COMMENTS),
                     notNullValue()
                   );

    }

    @Test
    public void testThaACommentCanNotBeAddedToACancelledTestDriveAppointment()
    {
        assertThat(
                    cancel_test_drive(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_TEST_DRIVE_STATUS),
                    equalToIgnoringCase(CANCELLED.toString())
                   );

        assertThat(
                    add_comments(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_RSP_CODE_DESCRIPTION),
                    equalToIgnoringCase(INVALID_TEST_DRIVE_STATUS)
                  );
    }

    @Test
    public void testThatTestDrivePriceCanNotBeChangedByFleetManager()
    {
        cancelTestDriveAppointment = true;
        assertThat(
                    change_test_drive_price(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_REQUEST_STATUS),
                    equalToIgnoringCase(requestActionStatus.SUCCESS.toString())
                   );
    }

    @Test
    public void testThatTestDriveAppointmentVehicleCanBeChanged()
    {
        cancelTestDriveAppointment = true;
        assertThat(
                    change_test_drive_vehicle(getRequestSpecification(),  getTestDriveBookingId()).then().extract().jsonPath().getString(MF_REQUEST_STATUS),
                    equalToIgnoringCase(requestActionStatus.SUCCESS.toString())
                   );
    }

    @Test
    public void testThatATestDriveContractCanBeCreated() throws IOException, ParseException
    {
        cancelTestDriveAppointment = true;
        assertThat(
                    create_test_drive_contract(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_DOCUMENT_URL),
                    notNullValue()
                   );
    }

    @Test
    public void createTestDriveAppointmentContractReturnProtocol() throws IOException , ParseException
    {
        assertThat(
                    create_test_drive_contract(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_DOCUMENT_URL),
                    notNullValue()
                   );

       assertThat(
                    get_test_drive(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_TEST_DRIVE_STATUS),
                    equalToIgnoringCase(CONTRACT_CREATED.toString())
                  );

        setResponse(return_vehicle(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath());

        assertThat(
                getResponse().getString(MF_REQUEST_STATUS),
                     equalToIgnoringCase(VEHICLE_RETURNED_SUCCESSFULLY_RETURNED)
                   );

        assertThat(
                    getResponse().getString(MF_RETURN_VEHICLE_STATUS),
                    equalToIgnoringCase(VEHICLE_RETURNED.toString())
                   );

        String bookingId = getResponse().getString(MF_ENTITY_BOOKING_ID);
        String vehicleId = getResponse().getString(MF_ENTITY_VEHICLE_ID);

        assertThat(
                      capture_damage(getRequestSpecification(), vehicleId , bookingId).then().extract().jsonPath().getString(MF_REQUEST_STATUS),
                      equalToIgnoringCase(VEHICLE_DAMAGE_SUCCESSFULLY_DOCUMENTED)
                   );

        assertThat(
                    get_test_drive(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_TEST_DRIVE_STATUS) ,
                    equalToIgnoringCase(VEHICLE_RETURNED.toString())
                  );

        assertThat(
                    capture_feedback(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_TEST_DRIVE_STATUS),
                    equalToIgnoringCase(FEEDBACK_CAPTURED.toString())
                   );
    }

    @Test
    public void testThatCommentsCanBeAddedAfterReturningVehicle() throws IOException , ParseException
    {
        assertThat(
                    create_test_drive_contract(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_DOCUMENT_URL),
                    notNullValue()
                   );

        assertThat(
                    get_test_drive(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_TEST_DRIVE_STATUS) ,
                    equalToIgnoringCase(CONTRACT_CREATED.toString())
                    );

        setResponse(return_vehicle(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath());
        assertThat(
                    getResponse().getString(MF_REQUEST_STATUS),
                    equalToIgnoringCase(VEHICLE_RETURNED_SUCCESSFULLY_RETURNED)
                  );

        assertThat(
                    getResponse().getString(MF_RETURN_VEHICLE_STATUS),
                    equalToIgnoringCase(VEHICLE_RETURNED.toString())
                   );

        assertThat(
                    add_comments(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_REQUEST_STATUS),
                    equalToIgnoringCase(requestActionStatus.SUCCESS.toString())
                  );
    }

    @Test
    public void testThatVehicleCanNotBeChangedOnReturningVehicleStatus() throws IOException , ParseException
    {
        assertThat(
                    create_test_drive_contract(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath().getString(MF_DOCUMENT_URL),
                    notNullValue()
                   );
        setResponse(return_vehicle(getRequestSpecification(), getTestDriveBookingId()).then().extract().jsonPath());

        assertThat(
                    getResponse().getString(MF_REQUEST_STATUS),
                    equalToIgnoringCase(VEHICLE_RETURNED_SUCCESSFULLY_RETURNED)
                   );

        assertThat(
                    getResponse().getString(MF_RETURN_VEHICLE_STATUS),
                    equalToIgnoringCase(VEHICLE_RETURNED.toString())
                  );

        assertThat(
                    change_test_drive_vehicle(getRequestSpecification(),  getTestDriveBookingId()).then().extract().jsonPath().getString(MF_RSP_CODE_DESCRIPTION),
                    equalToIgnoringCase(TEST_DRIVE_STATUS_SHOULD_BE_BOOKED)
                  );
    }
}