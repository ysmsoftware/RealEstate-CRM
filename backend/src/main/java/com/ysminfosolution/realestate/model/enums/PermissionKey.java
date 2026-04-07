package com.ysminfosolution.realestate.model.enums;

public enum PermissionKey {
    // ── Projects ─────────────────────────────────────────────────────────
    VIEW_PROJECT, CREATE_PROJECT, UPDATE_PROJECT, DELETE_PROJECT,
    // ── Wings / Floors / Flats ────────────────────────────────────────────
    CREATE_WING, UPDATE_WING, DELETE_WING,
    CREATE_FLOOR, UPDATE_FLOOR, DELETE_FLOOR,
    // ── Enquiries ─────────────────────────────────────────────────────────
    VIEW_ENQUIRY, CREATE_ENQUIRY, UPDATE_ENQUIRY,
    CANCEL_ENQUIRY, CHANGE_ENQUIRY_STATUS,
    // ── Bookings ──────────────────────────────────────────────────────────
    VIEW_BOOKING, CREATE_BOOKING,
    // ── Clients ───────────────────────────────────────────────────────────
    VIEW_CLIENT, UPDATE_CLIENT,
    // ── Users ─────────────────────────────────────────────────────────────
    VIEW_USER, CREATE_USER, UPDATE_USER, DELETE_USER,
    // ── Follow-ups ────────────────────────────────────────────────────────
    VIEW_FOLLOWUP, CREATE_FOLLOWUP, UPDATE_FOLLOWUP,
    // ── Documents ────────────────────────────────────────────────────────
    VIEW_DOCUMENT, UPLOAD_DOCUMENT, DELETE_DOCUMENT,
    // ── Exports (also subscription-gated) ────────────────────────────────
    EXPORT_PDF, EXPORT_LEADS,
    // ── Dashboard ────────────────────────────────────────────────────────
    VIEW_DASHBOARD,
    // ── Disbursements ─────────────────────────────────────────────────────
    VIEW_DISBURSEMENT, CREATE_DISBURSEMENT, UPDATE_DISBURSEMENT,
}
