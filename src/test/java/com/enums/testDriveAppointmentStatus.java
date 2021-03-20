package com.enums;

public enum testDriveAppointmentStatus {
    BOOKED {
        public String toString() { return "BOOKED"; }},
    CANCELLED {
        public String toString() { return "CANCELED"; }},
    CONTRACT_CREATED {
        public String toString() {
            return "CONTRACT_CREATED";
        }},
    VEHICLE_RETURNED {
        public String toString() {
            return "VEHICLE_RETURNED";
        }},
    FEEDBACK_CAPTURED{
        public String toString() {
            return "FEEDBACK_CAPTURED";
        }},
}


