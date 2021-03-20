package com.enums;

public enum requestEndPoints {

    TESTDRIVE {
        public String toString() {
            return "/api/test-drive";
        }},
    D2D_BOOKABLE_VEHICLES {
        public String toString() {
            return "api-d2d/vehicle";
        }},
    BOOKABLE_VEHICLES {
        public String toString() {
            return "/api/test-drive/bookable-vehicles";
        }},
    AVAILABLE_TIME_SLOTS {
        public String toString() {
            return "/api/test-drive/time-slot";
        }},
    D2D_AVAILABLE_TIME_SLOTS {
        public String toString() {
            return "/api-d2d/test-drive/time-slot";
        }},
    CANCEL_BOOKING {
        public String toString() {
            return "/cancel-booking";
        }},
    CREATE_CONTRACT {
        public String toString() {
            return "/usage-agreement";
        }},
    DRIVER {
        public String toString() {
            return "/driver";
        }},
    PRICE {
        public String toString() {
            return "/price";
        }},
    RETURN_VEHICLE {
        public String toString() {
            return "/vehicle-returned";
        }},
    CAPTURE_FEEDBACK {
        public String toString() {
            return "/capture-feedback";
        }},
    API_VEHICLE {
        public String toString() {
            return "/api/vehicle";
        }},
    VEHICLE_OPTIONS {
        public String toString() {
            return "/vehicle-options";
        }},
    D2D_TESTDRIVE {
        public String toString() {
            return "/api-d2d/test-drive";
        }},
    RETURN_PROTOCOL {
        public String toString() {
            return "/return-protocol";
        }},
    DAMAGE {
        public String toString() {
            return "/api/damage";
        }},
    RESERVATION {
        public String toString() {
            return "/reservation";
        }},
    INVITATION {
        public String toString() {
            return "/invitation";
        }},
    VEHICLE_GROUP {
        public String toString() {
            return "/api/vehicle-group";
        }},
    VEHICLE_GROUP_LIST {
        public String toString() {
            return "/api/vehicle-group/list";
        }},
    ASSIGNMENTS {
        public String toString() {
            return "/assignments";
        }},
    ASSIGNMENT {
        public String toString() {
            return "/assignment";
        }},
    APPOINTMENT  {
        public String toString() {
            return "/appointment";
        }},
    VEHICLE {
        public String toString() {
            return "/vehicle";
        }},
    IMPORT_VEHICLE {
        public String toString() {
            return "/api/vehicle/import";
        }},
    UNAVAILABILITY {
        public String toString() {
            return "/unavailability";
        }},
    STATUS {
        public String toString() {
            return "/status";
        }},
    CALENDAR {
        public String toString() {
            return "/calendar";
        }},
    CURRENT_USER {
        public String toString() {
            return "/api/current-user";
        }},
    VEHICLE_DAMAGE {
        public String toString() {
            return "   /api/damage/vehicle";
        }},
    VEHICLE_BASIC_INFO {
        public String toString() {
            return "/basic";
        }},
    ADD_COMMENT {
        public String toString() {
            return "/add-comment";
        }},




}


