const API_BASE_URL = "http://localhost:8080";

const API_ENDPOINTS = {
    login: "/api/auth/login",
    register: "/api/auth/register",

    activeSessions: "/api/parking-sessions/active",
    checkIn: "/api/parking-sessions/check-in",
    checkOut: "/api/parking-sessions/check-out",

    availableSlots: "/api/parking-slots/available",
    parkingSlots: "/api/parking-slots",

    vehicles: {
        list: "/api/vehicles",
        register: "/api/vehicles/register",
        approve: function (id) {
            return `/api/vehicles/${id}/approve`;
        },
        reject: function (id) {
            return `/api/vehicles/${id}/reject`;
        },
        block: function (id) {
            return `/api/vehicles/${id}/block`;
        },
        unblock: function (id) {
            return `/api/vehicles/${id}/unblock`;
        }
    },

    parkingSessions: {
        active: "/api/parking-sessions/active",
        checkIn: "/api/parking-sessions/check-in",
        checkOut: "/api/parking-sessions/check-out",
        history: "/api/parking-sessions/history"
    },

    parkingSlotManagement: {
        list: "/api/parking-slots",
        available: "/api/parking-slots/available",
        create: "/api/parking-slots",
        updateStatus: function (id) {
            return `/api/parking-slots/${id}/status`;
        },
        delete: function (id) {
            return `/api/parking-slots/${id}`;
        }
    },

    adminAccounts: {
        accounts: "/api/admin/accounts",
        lockAccount: function (id) {
            return `/api/admin/accounts/${id}/lock`;
        },
        unlockAccount: function (id) {
            return `/api/admin/accounts/${id}/unlock`;
        }
    },
    invoices: {
        list: "/api/invoices",
        my: "/api/invoices/my"
    },
    payments: {
        payMyInvoices: "/api/payments/my-invoices"
    },
    incidents:{
        my: "/api/incidents/my",
        list: "/api/incidents",
        updateStatus: function (id) {
            return `/api/incidents/${id}/status`;
        }
    },
    building: {
        floors: "/api/building/floors",

        floorActive: function (id) {
            return `/api/building/floors/${id}/active`;
        },

        floorInactive: function (id) {
            return `/api/building/floors/${id}/inactive`;
        },

        zones: "/api/building/zones",

        zoneActive: function (id) {
            return `/api/building/zones/${id}/active`;
        },

        zoneInactive: function (id) {
            return `/api/building/zones/${id}/inactive`;
        }
    }
};