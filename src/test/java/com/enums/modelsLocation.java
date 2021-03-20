package com.enums;

public enum modelsLocation {
    D2D_TEST_DRIVE_JSON {
        public String toString() {
            return "src/test/models/d2dTestDrive.json";
        }
    },
    BOOK_TEST_DRIVE_JSON {
        public String toString() {
            return    "src/test/models/BookTestDrive.json";
        }
    },
    ADD_BLOCKER_JSON {
        public String toString() {
            return    "src/test/models/addBlockerOnAVehicle.json";
        }
    },
    INVITATION_JSON {
        public String toString() {
            return "src/test/models/invitation.json";
        }
    },
    RESERVATION_JSON {
        public String toString() {
            return "src/test/models/reservation.json";
        }
    },
    CAPTURE_FEEDBACK_JSON {
        public String toString() {
            return "src/test/models/captureFeedback.json";
        }
    },
    RETURN_VEHICLE_JSON {
        public String toString() {
            return  "src/test/models/returnVehicle.json";
        }
    },
    TEST_DRIVE_CONTRACT_JSON {
        public String toString() {
            return     "src/test/models/createTestDriveContract.json";
        }
    },
    MANUALLY_CREATE_VEHICLE_JSON {
        public String toString() {
            return     "src/test/models/manuallyCreateVehicle.json";
        }
    },
    CREATE_VEHICLE_GROUP_JSON {
        public String toString() {
            return     "src/test/models/createVehicleGroup.json";
        }
    },
    ADD_VEHICLE_DAMAGE_JSON{
        public String toString() {
            return     "src/test/models/vehicleDamage.json";
        }
    },
    ADD_VEHICLE_STATUS_JSON{
        public String toString() {
            return     "src/test/models/addStatus.json";
        }
    },



}
