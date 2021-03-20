package com.interfaces;

public interface responseMessages {
    String THE_VEHICLE_WAS_SUCCESSFULLY_CREATED = "The Vehicle was successfully created.";
    String THE_VEHICLE_WAS_SUCCESSFULLY_IMPORTED ="Vehicle successfully imported.";
    String VEHICLE_ALREADY_EXISTS = "Vehicle already exists.";
    String VEHICLE_GROUP_ALREADY_EXISTS = "Vehicle group with name \"Rest Assured Group\" already exists.";
    String VEHICLE_GROUP_CANNOT_BE_DELETED  = "The vehicle group cannot be deleted";
    String VEHICLE_CANNOT_BE_DELETED  = "Vehicle cannot be deleted";
    String VEHICLE_RETURNED_SUCCESSFULLY_RETURNED = "Vehicle Successfully Returned";
    String VEHICLE_DAMAGE_SUCCESSFULLY_DOCUMENTED = "Damage successfully documented.";
    String VEHICLE_DAMAGE_SUCCESSFULLY_DELETED= "Vehicle damage has been successfully deleted.";
    String THE_FOLLOWING_ATTRIBUTES_CHANGED = "The following driver attributes are changed";
    String VEHICLE_UNAVAILABLE_FOR_BOOKING = "Either the vehicle is not available or there is no capacity available for your chosen appointment. Please select another start and end time.";
    String INVALID_TEST_DRIVE_STATUS = "Invalid test drive status, status should be \"BOOKED or CONTRACT_CREATED or VEHICLE_RETURNED or FEEDBACK_CAPTURED\"";
    String TEST_DRIVE_STATUS_SHOULD_BE_BOOKED = "Invalid test drive status, status should be \"BOOKED\"";
    String THE_VEHICLE_GROUP_WAS_SUCCESSFULLY_UPDATED = "The vehicle group was successfully updated.";
}
