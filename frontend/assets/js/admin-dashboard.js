"use strict";

let allVehicles = [];
let currentStatusFilter = "ALL";

let allAccounts = [];

let allSlots = [];
let currentSlotStatusFilter = "ALL";

let allFloors = [];
let allZones = [];

const ADMIN_ACCOUNT_ENDPOINTS = (() => {
    const fallbackEndpoints = {
        accounts: "/api/admin/accounts",
        lockAccount: function (id) {
            return `/api/admin/accounts/${id}/lock`;
        },
        unlockAccount: function (id) {
            return `/api/admin/accounts/${id}/unlock`;
        }
    };

    if (typeof ADMIN_ENDPOINTS !== "undefined") {
        return {
            accounts: ADMIN_ENDPOINTS.accounts || fallbackEndpoints.accounts,
            lockAccount: ADMIN_ENDPOINTS.lockAccount || fallbackEndpoints.lockAccount,
            unlockAccount: ADMIN_ENDPOINTS.unlockAccount || fallbackEndpoints.unlockAccount
        };
    }

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.adminAccounts
    ) {
        return {
            accounts: API_ENDPOINTS.adminAccounts.accounts || fallbackEndpoints.accounts,
            lockAccount: API_ENDPOINTS.adminAccounts.lockAccount || fallbackEndpoints.lockAccount,
            unlockAccount: API_ENDPOINTS.adminAccounts.unlockAccount || fallbackEndpoints.unlockAccount
        };
    }

    return fallbackEndpoints;
})();

const ADMIN_VEHICLE_ENDPOINTS = (() => {
    const fallbackEndpoints = {
        list: "/api/vehicles",
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
    };

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.vehicles
    ) {
        return {
            list: API_ENDPOINTS.vehicles.list || fallbackEndpoints.list,
            approve: API_ENDPOINTS.vehicles.approve || fallbackEndpoints.approve,
            reject: API_ENDPOINTS.vehicles.reject || fallbackEndpoints.reject,
            block: API_ENDPOINTS.vehicles.block || fallbackEndpoints.block,
            unblock: API_ENDPOINTS.vehicles.unblock || fallbackEndpoints.unblock
        };
    }

    return fallbackEndpoints;
})();

const ADMIN_SLOT_ENDPOINTS = (() => {
    const fallbackEndpoints = {
        list: "/api/parking-slots",
        available: "/api/parking-slots/available",
        create: "/api/parking-slots",
        updateStatus: function (id) {
            return `/api/parking-slots/${id}/status`;
        },
        delete: function (id) {
            return `/api/parking-slots/${id}`;
        }
    };

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.parkingSlotManagement
    ) {
        return {
            list: API_ENDPOINTS.parkingSlotManagement.list || fallbackEndpoints.list,
            available: API_ENDPOINTS.parkingSlotManagement.available || fallbackEndpoints.available,
            create: API_ENDPOINTS.parkingSlotManagement.create || fallbackEndpoints.create,
            updateStatus: API_ENDPOINTS.parkingSlotManagement.updateStatus || fallbackEndpoints.updateStatus,
            delete: API_ENDPOINTS.parkingSlotManagement.delete || fallbackEndpoints.delete
        };
    }

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        typeof API_ENDPOINTS.parkingSlots === "string"
    ) {
        return {
            list: API_ENDPOINTS.parkingSlots,
            available: `${API_ENDPOINTS.parkingSlots}/available`,
            create: API_ENDPOINTS.parkingSlots,
            updateStatus: fallbackEndpoints.updateStatus,
            delete: fallbackEndpoints.delete
        };
    }

    return fallbackEndpoints;
})();

const ADMIN_BUILDING_ENDPOINTS = (() => {
    const fallbackEndpoints = {
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
    };

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.building
    ) {
        return {
            floors: API_ENDPOINTS.building.floors || fallbackEndpoints.floors,
            floorActive: API_ENDPOINTS.building.floorActive || fallbackEndpoints.floorActive,
            floorInactive: API_ENDPOINTS.building.floorInactive || fallbackEndpoints.floorInactive,
            zones: API_ENDPOINTS.building.zones || fallbackEndpoints.zones,
            zoneActive: API_ENDPOINTS.building.zoneActive || fallbackEndpoints.zoneActive,
            zoneInactive: API_ENDPOINTS.building.zoneInactive || fallbackEndpoints.zoneInactive
        };
    }

    return fallbackEndpoints;
})();

const ADMIN_INCIDENT_ENDPOINTS = (() => {
    const fallbackEndpoints = {
        list: "/api/incidents",
        updateStatus: function (id) {
            return `/api/incidents/${id}/status`;
        }
    };

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.incidents
    ) {
        return {
            list: API_ENDPOINTS.incidents.list || fallbackEndpoints.list,
            updateStatus: API_ENDPOINTS.incidents.updateStatus || fallbackEndpoints.updateStatus
        };
    }

    return fallbackEndpoints;
})();

document.addEventListener("DOMContentLoaded", function () {
    try {
        if (typeof requireLogin === "function") {
            requireLogin();
        }

        if (typeof requireRole === "function") {
            requireRole(["ADMIN"]);
        } else {
            requireAdminRoleFallback();
        }

        renderCurrentUser();
        bindEvents();

        loadVehicles();
        loadParkingSlots();
        loadAccounts();
        loadAdminIncidentReports();
        loadFloors();
        loadZones();

    } catch (error) {
        console.error(error);
        showVehicleMessage("Cannot load admin dashboard.", "error");
    }
});

function requireAdminRoleFallback() {
    const token = localStorage.getItem("accessToken");
    const role = localStorage.getItem("role");

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    if (role !== "ADMIN") {
        window.location.href = "login.html";
    }
}

function renderCurrentUser() {
    let currentUser = null;

    if (typeof getCurrentUser === "function") {
        currentUser = getCurrentUser();
    }

    const username = currentUser?.username || localStorage.getItem("username") || "-";
    const role = currentUser?.role || localStorage.getItem("role") || "-";

    setText("usernameText", username);
    setText("roleText", role);
}

function bindEvents() {
    const logoutButton = document.getElementById("logoutButton");

    const refreshVehiclesButton = document.getElementById("refreshVehiclesButton");
    const statusFilter = document.getElementById("statusFilter");
    const vehiclesTableBody = document.getElementById("vehiclesTableBody");

    const createSlotForm = document.getElementById("createSlotForm");
    const refreshSlotsButton = document.getElementById("refreshSlotsButton");
    const slotStatusFilter = document.getElementById("slotStatusFilter");
    const slotsTableBody = document.getElementById("slotsTableBody");
    const slotZoneSelect = document.getElementById("slotZoneSelect");

    const createAccountForm = document.getElementById("createAccountForm");
    const refreshAccountsButton = document.getElementById("refreshAccountsButton");
    const accountsTableBody = document.getElementById("accountsTableBody");

    const adminIncidentFilterForm = document.getElementById("adminIncidentFilterForm");
    const resetAdminIncidentsButton = document.getElementById("resetAdminIncidentsButton");
    const adminIncidentTableBody = document.getElementById("adminIncidentTableBody");

    const createFloorForm = document.getElementById("createFloorForm");
    const refreshFloorsButton = document.getElementById("refreshFloorsButton");
    const floorActiveFilter = document.getElementById("floorActiveFilter");
    const floorsTableBody = document.getElementById("floorsTableBody");

    const createZoneForm = document.getElementById("createZoneForm");
    const zoneFilterForm = document.getElementById("zoneFilterForm");
    const resetZoneFilterButton = document.getElementById("resetZoneFilterButton");
    const zonesTableBody = document.getElementById("zonesTableBody");

    if (slotZoneSelect) {
        slotZoneSelect.addEventListener("change", handleSlotZoneChange);
    }

    if (createFloorForm) {
        createFloorForm.addEventListener("submit", handleCreateFloor);
    }

    if (refreshFloorsButton) {
        refreshFloorsButton.addEventListener("click", function () {
            loadFloors();
        });
    }

    if (floorActiveFilter) {
        floorActiveFilter.addEventListener("change", function () {
            loadFloors();
        });
    }

    if (floorsTableBody) {
        floorsTableBody.addEventListener("click", handleFloorActionClick);
    }

    if (createZoneForm) {
        createZoneForm.addEventListener("submit", handleCreateZone);
    }

    if (zoneFilterForm) {
        zoneFilterForm.addEventListener("submit", function (event) {
            event.preventDefault();
            loadZones();
        });
    }

    if (resetZoneFilterButton) {
        resetZoneFilterButton.addEventListener("click", handleZoneFilterReset);
    }

    if (zonesTableBody) {
        zonesTableBody.addEventListener("click", handleZoneActionClick);
    }

    if (adminIncidentFilterForm) {
        adminIncidentFilterForm.addEventListener("submit", handleAdminIncidentSearch);
    }

    if (resetAdminIncidentsButton) {
        resetAdminIncidentsButton.addEventListener("click", handleAdminIncidentReset);
    }

    if (adminIncidentTableBody) {
        adminIncidentTableBody.addEventListener("click", function (event) {
            const button = event.target.closest("button[data-admin-incident-action='update']");

            if (!button) {
                return;
            }

            const incidentId = button.dataset.incidentId;

            if (!incidentId) {
                showAdminIncidentMessage("Incident id is missing.", "error");
                return;
            }

            handleAdminUpdateIncidentStatus(incidentId);
        });
    }

    if (createAccountForm) {
        createAccountForm.addEventListener("submit", handleCreateAccount);
    }

    if (refreshAccountsButton) {
        refreshAccountsButton.addEventListener("click", function () {
            loadAccounts();
        });
    }

    if (accountsTableBody) {
        accountsTableBody.addEventListener("click", handleAccountActionClick);
    }

    if (logoutButton) {
        logoutButton.addEventListener("click", handleLogout);
    }

    if (refreshVehiclesButton) {
        refreshVehiclesButton.addEventListener("click", function () {
            loadVehicles();
        });
    }

    if (statusFilter) {
        statusFilter.addEventListener("change", function () {
            currentStatusFilter = statusFilter.value || "ALL";
            renderVehicles();
        });
    }

    if (vehiclesTableBody) {
        vehiclesTableBody.addEventListener("click", handleVehicleActionClick);
    }

    if (createSlotForm) {
        createSlotForm.addEventListener("submit", handleCreateSlot);
    }

    if (refreshSlotsButton) {
        refreshSlotsButton.addEventListener("click", function () {
            loadParkingSlots();
        });
    }

    if (slotStatusFilter) {
        slotStatusFilter.addEventListener("change", function () {
            currentSlotStatusFilter = slotStatusFilter.value || "ALL";
            renderParkingSlots();
        });
    }

    if (slotsTableBody) {
        slotsTableBody.addEventListener("click", handleSlotActionClick);
    }
}

function handleLogout() {
    if (typeof logout === "function") {
        logout();
        return;
    }

    localStorage.clear();
    window.location.href = "login.html";
}

/* =========================
   VEHICLE MANAGEMENT
========================= */

async function loadVehicles(options = {}) {
    const refreshVehiclesButton = document.getElementById("refreshVehiclesButton");
    const vehiclesTableBody = document.getElementById("vehiclesTableBody");

    if (!options.keepMessage) {
        clearVehicleMessage();
    }

    if (refreshVehiclesButton) {
        refreshVehiclesButton.disabled = true;
    }

    if (vehiclesTableBody) {
        vehiclesTableBody.innerHTML = `
            <tr>
                <td colspan="10" class="empty-cell">Loading vehicles...</td>
            </tr>
        `;
    }

    try {
        const response = await apiFetch(ADMIN_VEHICLE_ENDPOINTS.list, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load vehicles.");
        }

        allVehicles = Array.isArray(response.data) ? response.data : [];
        renderVehicles();
    } catch (error) {
        console.error(error);
        allVehicles = [];
        renderVehicles();
        showVehicleMessage(error.message || "Cannot load vehicles.", "error");
    } finally {
        if (refreshVehiclesButton) {
            refreshVehiclesButton.disabled = false;
        }
    }
}

/* =========================
   ADMIN INCIDENT MANAGEMENT
========================= */

async function loadAdminIncidentReports(options = {}) {
    const adminIncidentTableBody = document.getElementById("adminIncidentTableBody");

    if (!options.keepMessage) {
        clearAdminIncidentMessage();
    }

    if (adminIncidentTableBody) {
        adminIncidentTableBody.innerHTML = `
            <tr>
                <td colspan="12" class="empty-cell">Loading incident reports...</td>
            </tr>
        `;
    }

    const queryString = buildAdminIncidentQueryParams();
    const endpoint = queryString
        ? `${ADMIN_INCIDENT_ENDPOINTS.list}?${queryString}`
        : ADMIN_INCIDENT_ENDPOINTS.list;

    try {
        const response = await apiFetch(endpoint, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load incident reports.");
        }

        const reports = Array.isArray(response.data) ? response.data : [];
        renderAdminIncidentReports(reports);
    } catch (error) {
        console.error(error);
        renderAdminIncidentReports([]);
        showAdminIncidentMessage(error.message || "Cannot load incident reports.", "error");
    }
}

/* =========================
   BUILDING FLOOR MANAGEMENT
========================= */

async function loadFloors(options = {}) {
    const floorsTableBody = document.getElementById("floorsTableBody");
    const floorActiveFilter = document.getElementById("floorActiveFilter");
    const active = floorActiveFilter?.value || "";

    if (!options.keepMessage) {
        clearFloorMessage();
    }

    if (floorsTableBody) {
        floorsTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-cell">Loading floors...</td>
            </tr>
        `;
    }

    const endpoint = active
        ? `${ADMIN_BUILDING_ENDPOINTS.floors}?active=${encodeURIComponent(active)}`
        : ADMIN_BUILDING_ENDPOINTS.floors;

    try {
        const response = await apiFetch(endpoint, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load floors.");
        }

        allFloors = Array.isArray(response.data) ? response.data : [];

        renderFloors(allFloors);
        populateFloorSelects(allFloors);
    } catch (error) {
        console.error(error);
        allFloors = [];
        renderFloors([]);
        populateFloorSelects([]);
        showFloorMessage(error.message || "Cannot load floors.", "error");
    }
}

function renderFloors(floors) {
    const floorsTableBody = document.getElementById("floorsTableBody");

    if (!floorsTableBody) {
        return;
    }

    floorsTableBody.innerHTML = "";

    if (!floors || floors.length === 0) {
        floorsTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-cell">No floors found.</td>
            </tr>
        `;
        return;
    }

    floors.forEach(function (floor) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(floor.id));
        row.appendChild(createTextCell(floor.floorCode || "-"));
        row.appendChild(createTextCell(floor.floorName || "-"));
        row.appendChild(createTextCell(floor.description || "-"));

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderActiveBadge(floor.active));
        row.appendChild(statusCell);

        row.appendChild(createTextCell(formatDateTime(floor.createdAt)));

        const actionsCell = document.createElement("td");
        actionsCell.appendChild(renderFloorActions(floor));
        row.appendChild(actionsCell);

        floorsTableBody.appendChild(row);
    });
}

function renderFloorActions(floor) {
    const actionGroup = document.createElement("div");
    actionGroup.className = "action-group";

    if (floor.active === true) {
        actionGroup.appendChild(
            createActionButton(
                "Inactive",
                "floor-inactive",
                floor.id,
                "inactive-button floor-action-button"
            )
        );

        return actionGroup;
    }

    actionGroup.appendChild(
        createActionButton(
            "Active",
            "floor-active",
            floor.id,
            "active-button floor-action-button"
        )
    );

    return actionGroup;
}

function handleFloorActionClick(event) {
    const button = event.target.closest("button[data-action][data-id]");

    if (!button) {
        return;
    }

    const action = button.dataset.action;
    const id = button.dataset.id;

    if (!id) {
        showFloorMessage("Floor id is missing.", "error");
        return;
    }

    if (action === "floor-active") {
        handleSetFloorActive(id, true);
        return;
    }

    if (action === "floor-inactive") {
        handleSetFloorActive(id, false);
    }
}

async function handleCreateFloor(event) {
    event.preventDefault();

    clearFloorMessage();

    const createFloorButton = document.getElementById("createFloorButton");

    let requestBody;

    try {
        requestBody = buildCreateFloorRequest();
    } catch (error) {
        showFloorMessage(error.message, "error");
        return;
    }

    setButtonLoading(createFloorButton, true, "Creating...");

    try {
        const response = await apiFetch(ADMIN_BUILDING_ENDPOINTS.floors, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Create floor failed.");
        }

        resetCreateFloorForm();

        await loadFloors({
            keepMessage: true
        });

        showFloorMessage(response.message || "Create floor successfully.", "success");
    } catch (error) {
        console.error(error);
        showFloorMessage(error.message || "Create floor failed.", "error");
    } finally {
        setButtonLoading(createFloorButton, false, "Create Floor");
    }
}

function buildCreateFloorRequest() {
    const floorCodeInput = document.getElementById("floorCodeInput");
    const floorNameInput = document.getElementById("floorNameInput");
    const floorDescriptionInput = document.getElementById("floorDescriptionInput");

    const floorCode = floorCodeInput?.value.trim() || "";
    const floorName = floorNameInput?.value.trim() || "";
    const description = floorDescriptionInput?.value.trim() || "";

    if (!floorCode) {
        floorCodeInput?.focus();
        throw new Error("Floor code is required.");
    }

    if (!floorName) {
        floorNameInput?.focus();
        throw new Error("Floor name is required.");
    }

    return {
        floorCode,
        floorName,
        description: description || null
    };
}

async function handleSetFloorActive(id, active) {
    clearFloorMessage();
    setButtonsDisabled(".floor-action-button", true);

    const endpoint = active
        ? ADMIN_BUILDING_ENDPOINTS.floorActive(id)
        : ADMIN_BUILDING_ENDPOINTS.floorInactive(id);

    try {
        const response = await apiFetch(endpoint, {
            method: "PATCH"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Update floor status failed.");
        }

        await loadFloors({
            keepMessage: true
        });

        await loadZones({
            keepMessage: true
        });

        showFloorMessage(response.message || "Update floor status successfully.", "success");
    } catch (error) {
        console.error(error);
        showFloorMessage(error.message || "Update floor status failed.", "error");
    } finally {
        setButtonsDisabled(".floor-action-button", false);
    }
}

function resetCreateFloorForm() {
    const createFloorForm = document.getElementById("createFloorForm");

    if (createFloorForm) {
        createFloorForm.reset();
    }
}

function showFloorMessage(message, type = "info") {
    const floorMessage = document.getElementById("floorMessage");

    if (!floorMessage) {
        return;
    }

    floorMessage.textContent = message;
    floorMessage.className = `page-message ${type}`;
}

function clearFloorMessage() {
    const floorMessage = document.getElementById("floorMessage");

    if (!floorMessage) {
        return;
    }

    floorMessage.textContent = "";
    floorMessage.className = "page-message hidden";
}

/* =========================
   BUILDING ZONE MANAGEMENT
========================= */

async function loadZones(options = {}) {
    const zonesTableBody = document.getElementById("zonesTableBody");

    if (!options.keepMessage) {
        clearZoneMessage();
    }

    if (zonesTableBody) {
        zonesTableBody.innerHTML = `
            <tr>
                <td colspan="9" class="empty-cell">Loading zones...</td>
            </tr>
        `;
    }

    const queryString = buildZoneQueryParams();
    const endpoint = queryString
        ? `${ADMIN_BUILDING_ENDPOINTS.zones}?${queryString}`
        : ADMIN_BUILDING_ENDPOINTS.zones;

    try {
        const response = await apiFetch(endpoint, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load zones.");
        }

        allZones = Array.isArray(response.data) ? response.data : [];

        renderZones(allZones);
        populateZoneSelects(allZones);
    } catch (error) {
        console.error(error);
        allZones = [];
        renderZones([]);
        populateZoneSelects([]);
        showZoneMessage(error.message || "Cannot load zones.", "error");
    }
}

function buildZoneQueryParams() {
    const zoneFloorFilter = document.getElementById("zoneFloorFilter");
    const zoneActiveFilter = document.getElementById("zoneActiveFilter");
    const zoneVehicleTypeFilter = document.getElementById("zoneVehicleTypeFilter");

    const floorId = zoneFloorFilter?.value.trim() || "";
    const active = zoneActiveFilter?.value.trim() || "";
    const vehicleType = zoneVehicleTypeFilter?.value.trim() || "";

    const params = new URLSearchParams();

    if (floorId) {
        params.set("floorId", floorId);
    }

    if (active) {
        params.set("active", active);
    }

    if (vehicleType) {
        params.set("vehicleType", vehicleType);
    }

    return params.toString();
}

function renderZones(zones) {
    const zonesTableBody = document.getElementById("zonesTableBody");

    if (!zonesTableBody) {
        return;
    }

    zonesTableBody.innerHTML = "";

    if (!zones || zones.length === 0) {
        zonesTableBody.innerHTML = `
            <tr>
                <td colspan="9" class="empty-cell">No zones found.</td>
            </tr>
        `;
        return;
    }

    zones.forEach(function (zone) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(zone.id));
        row.appendChild(createTextCell(formatZoneFloorLabel(zone)));
        row.appendChild(createTextCell(zone.zoneCode || "-"));
        row.appendChild(createTextCell(zone.zoneName || "-"));
        row.appendChild(createTextCell(zone.vehicleType || "-"));
        row.appendChild(createTextCell(zone.description || "-"));

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderActiveBadge(zone.active));
        row.appendChild(statusCell);

        row.appendChild(createTextCell(formatDateTime(zone.createdAt)));

        const actionsCell = document.createElement("td");
        actionsCell.appendChild(renderZoneActions(zone));
        row.appendChild(actionsCell);

        zonesTableBody.appendChild(row);
    });
}

function renderActiveBadge(active) {
    const badge = document.createElement("span");

    if (active === true) {
        badge.className = "status-badge status-active";
        badge.textContent = "Active";
        return badge;
    }

    badge.className = "status-badge status-inactive";
    badge.textContent = "Inactive";
    return badge;
}

function renderZoneActions(zone) {
    const actionGroup = document.createElement("div");
    actionGroup.className = "action-group";

    if (zone.active === true) {
        actionGroup.appendChild(
            createActionButton(
                "Inactive",
                "zone-inactive",
                zone.id,
                "inactive-button zone-action-button"
            )
        );

        return actionGroup;
    }

    actionGroup.appendChild(
        createActionButton(
            "Active",
            "zone-active",
            zone.id,
            "active-button zone-action-button"
        )
    );

    return actionGroup;
}

function handleZoneActionClick(event) {
    const button = event.target.closest("button[data-action][data-id]");

    if (!button) {
        return;
    }

    const action = button.dataset.action;
    const id = button.dataset.id;

    if (!id) {
        showZoneMessage("Zone id is missing.", "error");
        return;
    }

    if (action === "zone-active") {
        handleSetZoneActive(id, true);
        return;
    }

    if (action === "zone-inactive") {
        handleSetZoneActive(id, false);
    }
}

async function handleCreateZone(event) {
    event.preventDefault();

    clearZoneMessage();

    const createZoneButton = document.getElementById("createZoneButton");

    let requestBody;

    try {
        requestBody = buildCreateZoneRequest();
    } catch (error) {
        showZoneMessage(error.message, "error");
        return;
    }

    setButtonLoading(createZoneButton, true, "Creating...");

    try {
        const response = await apiFetch(ADMIN_BUILDING_ENDPOINTS.zones, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Create zone failed.");
        }

        resetCreateZoneForm();

        await loadZones({
            keepMessage: true
        });

        showZoneMessage(response.message || "Create zone successfully.", "success");
    } catch (error) {
        console.error(error);
        showZoneMessage(error.message || "Create zone failed.", "error");
    } finally {
        setButtonLoading(createZoneButton, false, "Create Zone");
    }
}

function buildCreateZoneRequest() {
    const zoneFloorSelect = document.getElementById("zoneFloorSelect");
    const zoneCodeInput = document.getElementById("zoneCodeInput");
    const zoneNameInput = document.getElementById("zoneNameInput");
    const zoneVehicleTypeSelect = document.getElementById("zoneVehicleTypeSelect");
    const zoneDescriptionInput = document.getElementById("zoneDescriptionInput");

    const floorId = zoneFloorSelect?.value.trim() || "";
    const zoneCode = zoneCodeInput?.value.trim() || "";
    const zoneName = zoneNameInput?.value.trim() || "";
    const vehicleType = zoneVehicleTypeSelect?.value.trim() || "";
    const description = zoneDescriptionInput?.value.trim() || "";

    if (!floorId) {
        zoneFloorSelect?.focus();
        throw new Error("Floor is required.");
    }

    if (!zoneCode) {
        zoneCodeInput?.focus();
        throw new Error("Zone code is required.");
    }

    if (!zoneName) {
        zoneNameInput?.focus();
        throw new Error("Zone name is required.");
    }

    const requestBody = {
        floorId: Number(floorId),
        zoneCode,
        zoneName,
        description: description || null
    };

    if (vehicleType) {
        requestBody.vehicleType = vehicleType;
    }

    return requestBody;
}

async function handleSetZoneActive(id, active) {
    clearZoneMessage();
    setButtonsDisabled(".zone-action-button", true);

    const endpoint = active
        ? ADMIN_BUILDING_ENDPOINTS.zoneActive(id)
        : ADMIN_BUILDING_ENDPOINTS.zoneInactive(id);

    try {
        const response = await apiFetch(endpoint, {
            method: "PATCH"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Update zone status failed.");
        }

        await loadZones({
            keepMessage: true
        });

        showZoneMessage(response.message || "Update zone status successfully.", "success");
    } catch (error) {
        console.error(error);
        showZoneMessage(error.message || "Update zone status failed.", "error");
    } finally {
        setButtonsDisabled(".zone-action-button", false);
    }
}

function handleZoneFilterReset() {
    const zoneFilterForm = document.getElementById("zoneFilterForm");

    if (zoneFilterForm) {
        zoneFilterForm.reset();
    }

    clearZoneMessage();
    loadZones();
}

function resetCreateZoneForm() {
    const createZoneForm = document.getElementById("createZoneForm");

    if (createZoneForm) {
        createZoneForm.reset();
    }
}

function populateFloorSelects(floors) {
    const zoneFloorSelect = document.getElementById("zoneFloorSelect");
    const zoneFloorFilter = document.getElementById("zoneFloorFilter");

    populateFloorSelect(zoneFloorSelect, floors, "-- Select floor --", true);
    populateFloorSelect(zoneFloorFilter, floors, "All floors", false);
}

function populateFloorSelect(selectElement, floors, placeholder, activeOnly) {
    if (!selectElement) {
        return;
    }

    const currentValue = selectElement.value;

    selectElement.innerHTML = "";

    const placeholderOption = document.createElement("option");
    placeholderOption.value = "";
    placeholderOption.textContent = placeholder;
    selectElement.appendChild(placeholderOption);

    const sourceFloors = activeOnly
        ? floors.filter(function (floor) {
            return floor.active === true;
        })
        : floors;

    sourceFloors.forEach(function (floor) {
        const option = document.createElement("option");
        option.value = floor.id;
        option.textContent = `${floor.floorCode || "-"} - ${floor.floorName || "-"}`;
        selectElement.appendChild(option);
    });

    selectElement.value = currentValue;
}

function populateZoneSelects(zones) {
    const slotZoneSelect = document.getElementById("slotZoneSelect");

    if (!slotZoneSelect) {
        return;
    }

    const currentValue = slotZoneSelect.value;

    slotZoneSelect.innerHTML = "";

    const emptyOption = document.createElement("option");
    emptyOption.value = "";
    emptyOption.textContent = "No zone / Legacy floor only";
    slotZoneSelect.appendChild(emptyOption);

    zones
        .filter(function (zone) {
            return zone.active === true;
        })
        .forEach(function (zone) {
            const option = document.createElement("option");
            option.value = zone.id;
            option.textContent = formatZoneSelectLabel(zone);
            option.dataset.floorCode = zone.floorCode || "";
            option.dataset.vehicleType = zone.vehicleType || "";

            slotZoneSelect.appendChild(option);
        });

    slotZoneSelect.value = currentValue;
}

function formatZoneFloorLabel(zone) {
    const floorCode = zone.floorCode || "-";
    const floorName = zone.floorName || "-";

    return `${floorCode} - ${floorName}`;
}

function formatZoneSelectLabel(zone) {
    const floorCode = zone.floorCode || "-";
    const zoneCode = zone.zoneCode || "-";
    const zoneName = zone.zoneName || "-";
    const vehicleType = zone.vehicleType || "COMMON";

    return `${floorCode} / ${zoneCode} - ${zoneName} (${vehicleType})`;
}

function showZoneMessage(message, type = "info") {
    const zoneMessage = document.getElementById("zoneMessage");

    if (!zoneMessage) {
        return;
    }

    zoneMessage.textContent = message;
    zoneMessage.className = `page-message ${type}`;
}

function clearZoneMessage() {
    const zoneMessage = document.getElementById("zoneMessage");

    if (!zoneMessage) {
        return;
    }

    zoneMessage.textContent = "";
    zoneMessage.className = "page-message hidden";
}

function buildAdminIncidentQueryParams() {
    const statusFilter = document.getElementById("adminIncidentStatusFilter");
    const typeFilter = document.getElementById("adminIncidentTypeFilter");
    const priorityFilter = document.getElementById("adminIncidentPriorityFilter");
    const userIdFilter = document.getElementById("adminIncidentUserIdFilter");
    const plateNumberFilter = document.getElementById("adminIncidentPlateNumberFilter");
    const fromDateFilter = document.getElementById("adminIncidentFromDateFilter");
    const toDateFilter = document.getElementById("adminIncidentToDateFilter");

    const params = new URLSearchParams();

    const status = statusFilter?.value.trim() || "";
    const type = typeFilter?.value.trim() || "";
    const priority = priorityFilter?.value.trim() || "";
    const userId = userIdFilter?.value.trim() || "";
    const plateNumber = plateNumberFilter?.value.trim() || "";
    const fromDate = fromDateFilter?.value || "";
    const toDate = toDateFilter?.value || "";

    if (status) params.set("status", status);
    if (type) params.set("type", type);
    if (priority) params.set("priority", priority);
    if (userId) params.set("userId", userId);
    if (plateNumber) params.set("plateNumber", plateNumber);
    if (fromDate) params.set("fromDate", fromDate);
    if (toDate) params.set("toDate", toDate);

    return params.toString();
}

function renderAdminIncidentReports(reports) {
    const adminIncidentTableBody = document.getElementById("adminIncidentTableBody");

    if (!adminIncidentTableBody) {
        return;
    }

    adminIncidentTableBody.innerHTML = "";

    if (!reports || reports.length === 0) {
        adminIncidentTableBody.innerHTML = `
            <tr>
                <td colspan="12" class="empty-cell">No incident reports found.</td>
            </tr>
        `;
        return;
    }

    reports.forEach(function (report) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(report.id));
        row.appendChild(createTextCell(report.userId ?? "-"));
        row.appendChild(createTextCell(report.title || "-"));
        row.appendChild(createTextCell(report.type || "-"));

        const priorityCell = document.createElement("td");
        priorityCell.appendChild(renderAdminIncidentPriorityBadge(report.priority));
        row.appendChild(priorityCell);

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderAdminIncidentStatusBadge(report.status));
        row.appendChild(statusCell);

        row.appendChild(createTextCell(report.plateNumber || "-"));
        row.appendChild(createLongTextCell(report.description || "-"));
        row.appendChild(createLongTextCell(report.staffNote || "-"));
        row.appendChild(createTextCell(formatDateTime(report.createdAt)));
        row.appendChild(createTextCell(formatDateTime(report.resolvedAt)));

        const actionsCell = document.createElement("td");
        actionsCell.appendChild(renderAdminIncidentActions(report));
        row.appendChild(actionsCell);

        adminIncidentTableBody.appendChild(row);
    });
}

function renderAdminIncidentActions(report) {
    const wrapper = document.createElement("div");
    wrapper.className = "incident-action-box";

    const statusSelect = document.createElement("select");
    statusSelect.className = "admin-incident-row-status";
    statusSelect.dataset.incidentId = report.id;

    ["OPEN", "IN_PROGRESS", "RESOLVED", "REJECTED"].forEach(function (status) {
        const option = document.createElement("option");
        option.value = status;
        option.textContent = status;

        if (String(report.status || "").toUpperCase() === status) {
            option.selected = true;
        }

        statusSelect.appendChild(option);
    });

    const noteTextarea = document.createElement("textarea");
    noteTextarea.className = "admin-incident-row-note";
    noteTextarea.dataset.incidentId = report.id;
    noteTextarea.rows = 2;
    noteTextarea.placeholder = "Staff note...";
    noteTextarea.value = report.staffNote || "";

    const updateButton = document.createElement("button");
    updateButton.type = "button";
    updateButton.className = "primary-button incident-update-button";
    updateButton.textContent = "Update";
    updateButton.dataset.adminIncidentAction = "update";
    updateButton.dataset.incidentId = report.id;

    wrapper.appendChild(statusSelect);
    wrapper.appendChild(noteTextarea);
    wrapper.appendChild(updateButton);

    return wrapper;
}

function handleAdminIncidentSearch(event) {
    event.preventDefault();
    loadAdminIncidentReports();
}

function handleAdminIncidentReset() {
    const adminIncidentFilterForm = document.getElementById("adminIncidentFilterForm");

    if (adminIncidentFilterForm) {
        adminIncidentFilterForm.reset();
    }

    clearAdminIncidentMessage();
    loadAdminIncidentReports();
}

async function handleAdminUpdateIncidentStatus(id) {
    clearAdminIncidentMessage();

    const statusSelect = document.querySelector(`.admin-incident-row-status[data-incident-id="${id}"]`);
    const noteTextarea = document.querySelector(`.admin-incident-row-note[data-incident-id="${id}"]`);
    const updateButton = document.querySelector(`button[data-admin-incident-action="update"][data-incident-id="${id}"]`);

    const status = statusSelect?.value.trim() || "";
    const staffNote = noteTextarea?.value.trim() || "";

    if (!status) {
        showAdminIncidentMessage("Status is required.", "error");
        return;
    }

    setButtonLoading(updateButton, true, "Updating...");

    try {
        const response = await apiFetch(ADMIN_INCIDENT_ENDPOINTS.updateStatus(id), {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                status,
                staffNote
            })
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Update incident failed.");
        }

        await loadAdminIncidentReports({
            keepMessage: true
        });

        showAdminIncidentMessage(response.message || "Update incident successfully.", "success");
    } catch (error) {
        console.error(error);
        showAdminIncidentMessage(error.message || "Update incident failed.", "error");
    } finally {
        setButtonLoading(updateButton, false, "Update");
    }
}

function renderAdminIncidentStatusBadge(status) {
    const badge = document.createElement("span");
    const normalizedStatus = String(status || "UNKNOWN").toUpperCase();

    let statusClass = "unknown";

    if (normalizedStatus === "OPEN") statusClass = "open";
    if (normalizedStatus === "IN_PROGRESS") statusClass = "in-progress";
    if (normalizedStatus === "RESOLVED") statusClass = "resolved";
    if (normalizedStatus === "REJECTED") statusClass = "rejected";

    badge.className = `status-badge incident-status-${statusClass}`;
    badge.textContent = normalizedStatus;

    return badge;
}

function renderAdminIncidentPriorityBadge(priority) {
    const badge = document.createElement("span");
    const normalizedPriority = String(priority || "UNKNOWN").toUpperCase();

    let priorityClass = "unknown";

    if (normalizedPriority === "LOW") priorityClass = "low";
    if (normalizedPriority === "MEDIUM") priorityClass = "medium";
    if (normalizedPriority === "HIGH") priorityClass = "high";

    badge.className = `priority-badge priority-${priorityClass}`;
    badge.textContent = normalizedPriority;

    return badge;
}

function createLongTextCell(value) {
    const cell = document.createElement("td");
    cell.className = "long-text-cell";
    cell.textContent = value ?? "-";
    return cell;
}

function showAdminIncidentMessage(message, type = "info") {
    const adminIncidentMessage = document.getElementById("adminIncidentMessage");

    if (!adminIncidentMessage) {
        return;
    }

    adminIncidentMessage.textContent = message;
    adminIncidentMessage.className = `page-message ${type}`;
}

function clearAdminIncidentMessage() {
    const adminIncidentMessage = document.getElementById("adminIncidentMessage");

    if (!adminIncidentMessage) {
        return;
    }

    adminIncidentMessage.textContent = "";
    adminIncidentMessage.className = "page-message hidden";
}

function formatDateTime(value) {
    if (!value) {
        return "-";
    }

    const date = new Date(value);

    if (Number.isNaN(date.getTime())) {
        return value;
    }

    return date.toLocaleString("vi-VN");
}

function renderVehicles() {
    const vehiclesTableBody = document.getElementById("vehiclesTableBody");

    if (!vehiclesTableBody) {
        return;
    }

    const vehicles = getFilteredVehicles();

    vehiclesTableBody.innerHTML = "";

    if (!vehicles || vehicles.length === 0) {
        vehiclesTableBody.innerHTML = `
            <tr>
                <td colspan="10" class="empty-cell">No vehicles found.</td>
            </tr>
        `;
        return;
    }

    vehicles.forEach(function (vehicle) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(vehicle.id));
        row.appendChild(createTextCell(vehicle.plateNumber));
        row.appendChild(createTextCell(vehicle.type));
        row.appendChild(createTextCell(vehicle.ownerName || "-"));
        row.appendChild(createTextCell(vehicle.ownerUserId ?? "-"));
        row.appendChild(createTextCell(vehicle.brand || "-"));
        row.appendChild(createTextCell(vehicle.color || "-"));

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderVehicleStatusBadge(vehicle.status));
        row.appendChild(statusCell);

        row.appendChild(createTextCell(vehicle.description || "-"));

        const actionsCell = document.createElement("td");
        actionsCell.appendChild(renderVehicleActions(vehicle));
        row.appendChild(actionsCell);

        vehiclesTableBody.appendChild(row);
    });
}

function getFilteredVehicles() {
    if (currentStatusFilter === "ALL") {
        return allVehicles;
    }

    return allVehicles.filter(function (vehicle) {
        return String(vehicle.status || "").toUpperCase() === currentStatusFilter;
    });
}

function renderVehicleStatusBadge(status) {
    return renderStatusBadge(status, ["PENDING", "APPROVED", "REJECTED", "BLOCKED"]);
}

function renderVehicleActions(vehicle) {
    const actionGroup = document.createElement("div");
    actionGroup.className = "action-group";

    const vehicleId = vehicle.id;
    const status = String(vehicle.status || "").toUpperCase();

    if (status === "PENDING") {
        actionGroup.appendChild(createActionButton("Approve", "vehicle-approve", vehicleId, "approve-button vehicle-action-button"));
        actionGroup.appendChild(createActionButton("Reject", "vehicle-reject", vehicleId, "reject-button vehicle-action-button"));
        return actionGroup;
    }

    if (status === "APPROVED") {
        actionGroup.appendChild(createActionButton("Block", "vehicle-block", vehicleId, "block-button vehicle-action-button"));
        return actionGroup;
    }

    if (status === "BLOCKED") {
        actionGroup.appendChild(createActionButton("Unblock", "vehicle-unblock", vehicleId, "unblock-button vehicle-action-button"));
        return actionGroup;
    }

    const noAction = document.createElement("span");
    noAction.className = "no-action";
    noAction.textContent = "No action";
    actionGroup.appendChild(noAction);

    return actionGroup;
}

function handleVehicleActionClick(event) {
    const button = event.target.closest("button[data-action][data-id]");

    if (!button) {
        return;
    }

    const action = button.dataset.action;
    const id = button.dataset.id;

    if (!id) {
        showVehicleMessage("Vehicle id is missing.", "error");
        return;
    }

    if (action === "vehicle-approve") {
        handleApproveVehicle(id);
        return;
    }

    if (action === "vehicle-reject") {
        handleRejectVehicle(id);
        return;
    }

    if (action === "vehicle-block") {
        handleBlockVehicle(id);
        return;
    }

    if (action === "vehicle-unblock") {
        handleUnblockVehicle(id);
    }
}

async function handleApproveVehicle(id) {
    await callVehicleAction(
        ADMIN_VEHICLE_ENDPOINTS.approve(id),
        "Approve vehicle successfully."
    );
}

async function handleRejectVehicle(id) {
    const confirmed = window.confirm("Reject this vehicle?");

    if (!confirmed) {
        return;
    }

    await callVehicleAction(
        ADMIN_VEHICLE_ENDPOINTS.reject(id),
        "Reject vehicle successfully."
    );
}

async function handleBlockVehicle(id) {
    const confirmed = window.confirm("Block this vehicle?");

    if (!confirmed) {
        return;
    }

    await callVehicleAction(
        ADMIN_VEHICLE_ENDPOINTS.block(id),
        "Block vehicle successfully."
    );
}

async function handleUnblockVehicle(id) {
    await callVehicleAction(
        ADMIN_VEHICLE_ENDPOINTS.unblock(id),
        "Unblock vehicle successfully."
    );
}

async function callVehicleAction(url, successMessage) {
    clearVehicleMessage();
    setButtonsDisabled(".vehicle-action-button", true);

    try {
        const response = await apiFetch(url, {
            method: "PATCH"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Action failed.");
        }

        await loadVehicles({
            keepMessage: true
        });

        showVehicleMessage(response.message || successMessage, "success");
    } catch (error) {
        console.error(error);
        showVehicleMessage(error.message || "Action failed.", "error");
    } finally {
        setButtonsDisabled(".vehicle-action-button", false);
    }
}

/* =========================
   PARKING SLOT MANAGEMENT
========================= */

async function loadParkingSlots(options = {}) {
    const refreshSlotsButton = document.getElementById("refreshSlotsButton");
    const slotsTableBody = document.getElementById("slotsTableBody");

    if (!options.keepMessage) {
        clearSlotMessage();
    }

    if (refreshSlotsButton) {
        refreshSlotsButton.disabled = true;
    }

    if (slotsTableBody) {
        slotsTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-cell">Loading parking slots...</td>
            </tr>
        `;
    }

    try {
        const response = await apiFetch(ADMIN_SLOT_ENDPOINTS.list, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load parking slots.");
        }

        allSlots = Array.isArray(response.data) ? response.data : [];
        renderParkingSlots();
    } catch (error) {
        console.error(error);
        allSlots = [];
        renderParkingSlots();
        showSlotMessage(error.message || "Cannot load parking slots.", "error");
    } finally {
        if (refreshSlotsButton) {
            refreshSlotsButton.disabled = false;
        }
    }
}

function renderParkingSlots() {
    const slotsTableBody = document.getElementById("slotsTableBody");

    if (!slotsTableBody) {
        return;
    }

    const slots = getFilteredSlots();

    slotsTableBody.innerHTML = "";

    if (!slots || slots.length === 0) {
        slotsTableBody.innerHTML = `
            <tr>
                <td colspan="6" class="empty-cell">No parking slots found.</td>
            </tr>
        `;
        return;
    }

    slots.forEach(function (slot) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(slot.id));
        row.appendChild(createTextCell(slot.slotCode));
        row.appendChild(createTextCell(slot.floorCode || slot.floorName || slot.floor || "-"));
        row.appendChild(createTextCell(slot.vehicleType));

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderSlotStatusBadge(slot.status));
        row.appendChild(statusCell);

        const actionsCell = document.createElement("td");
        actionsCell.appendChild(renderSlotActions(slot));
        row.appendChild(actionsCell);

        slotsTableBody.appendChild(row);
    });
}

function getFilteredSlots() {
    if (currentSlotStatusFilter === "ALL") {
        return allSlots;
    }

    return allSlots.filter(function (slot) {
        return String(slot.status || "").toUpperCase() === currentSlotStatusFilter;
    });
}

async function handleCreateSlot(event) {
    event.preventDefault();

    clearSlotMessage();

    const createSlotButton = document.getElementById("createSlotButton");

    let requestBody;

    try {
        requestBody = buildCreateSlotRequest();
    } catch (error) {
        showSlotMessage(error.message, "error");
        return;
    }

    setButtonLoading(createSlotButton, true, "Adding...");

    try {
        const response = await apiFetch(ADMIN_SLOT_ENDPOINTS.create, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Create parking slot failed.");
        }

        resetCreateSlotForm();

        await loadParkingSlots({
            keepMessage: true
        });

        showSlotMessage(response.message || "Create parking slot successfully.", "success");
    } catch (error) {
        console.error(error);
        showSlotMessage(error.message || "Create parking slot failed.", "error");
    } finally {
        setButtonLoading(createSlotButton, false, "Add Slot");
    }
}

function buildCreateSlotRequest() {
    const slotCodeInput = document.getElementById("slotCodeInput");
    const slotFloorInput = document.getElementById("slotFloorInput");
    const slotVehicleTypeSelect = document.getElementById("slotVehicleTypeSelect");
    const slotZoneSelect = document.getElementById("slotZoneSelect");

    const slotCode = slotCodeInput?.value.trim() || "";
    const floor = slotFloorInput?.value.trim() || "";
    const vehicleType = slotVehicleTypeSelect?.value.trim() || "";
    const zoneId = slotZoneSelect?.value.trim() || "";

    if (!slotCode) {
        slotCodeInput?.focus();
        throw new Error("Slot code is required.");
    }

    if (!floor) {
        slotFloorInput?.focus();
        throw new Error("Floor is required.");
    }

    if (!vehicleType) {
        slotVehicleTypeSelect?.focus();
        throw new Error("Vehicle type is required.");
    }

    const requestBody = {
        slotCode,
        floor,
        vehicleType
    };

    if (zoneId) {
        requestBody.zoneId = Number(zoneId);
    }

    return requestBody;
}

function renderSlotStatusBadge(status) {
    return renderStatusBadge(status, ["AVAILABLE", "OCCUPIED", "MAINTENANCE"]);
}

function renderSlotActions(slot) {
    const actionGroup = document.createElement("div");
    actionGroup.className = "action-group";

    const slotId = slot.id;
    const status = String(slot.status || "").toUpperCase();

    if (status === "AVAILABLE") {
        actionGroup.appendChild(createActionButton("Set Maintenance", "slot-set-maintenance", slotId, "maintenance-button slot-action-button"));
        actionGroup.appendChild(createActionButton("Delete", "slot-delete", slotId, "delete-button slot-action-button"));
        return actionGroup;
    }

    if (status === "MAINTENANCE") {
        actionGroup.appendChild(createActionButton("Set Available", "slot-set-available", slotId, "available-button slot-action-button"));
        actionGroup.appendChild(createActionButton("Delete", "slot-delete", slotId, "delete-button slot-action-button"));
        return actionGroup;
    }

    if (status === "OCCUPIED") {
        const occupiedText = document.createElement("span");
        occupiedText.className = "occupied-text";
        occupiedText.textContent = "Occupied";
        actionGroup.appendChild(occupiedText);
        return actionGroup;
    }

    const noAction = document.createElement("span");
    noAction.className = "no-action";
    noAction.textContent = "No action";
    actionGroup.appendChild(noAction);

    return actionGroup;
}

function handleSlotActionClick(event) {
    const button = event.target.closest("button[data-action][data-id]");

    if (!button) {
        return;
    }

    const action = button.dataset.action;
    const id = button.dataset.id;

    if (!id) {
        showSlotMessage("Parking slot id is missing.", "error");
        return;
    }

    if (action === "slot-set-maintenance") {
        handleSetSlotStatus(id, "MAINTENANCE");
        return;
    }

    if (action === "slot-set-available") {
        handleSetSlotStatus(id, "AVAILABLE");
        return;
    }

    if (action === "slot-delete") {
        handleDeleteSlot(id);
    }
}

async function handleSetSlotStatus(id, status) {
    clearSlotMessage();
    setButtonsDisabled(".slot-action-button", true);

    try {
        const response = await apiFetch(ADMIN_SLOT_ENDPOINTS.updateStatus(id), {
            method: "PATCH",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                status
            })
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Update parking slot status failed.");
        }

        await loadParkingSlots({
            keepMessage: true
        });

        showSlotMessage(response.message || "Update parking slot status successfully.", "success");
    } catch (error) {
        console.error(error);
        showSlotMessage(error.message || "Update parking slot status failed.", "error");
    } finally {
        setButtonsDisabled(".slot-action-button", false);
    }
}

async function handleDeleteSlot(id) {
    const confirmed = window.confirm("Delete this parking slot?");

    if (!confirmed) {
        return;
    }

    clearSlotMessage();
    setButtonsDisabled(".slot-action-button", true);

    try {
        const response = await apiFetch(ADMIN_SLOT_ENDPOINTS.delete(id), {
            method: "DELETE"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Delete parking slot failed.");
        }

        await loadParkingSlots({
            keepMessage: true
        });

        showSlotMessage(response.message || "Delete parking slot successfully.", "success");
    } catch (error) {
        console.error(error);
        showSlotMessage(error.message || "Delete parking slot failed.", "error");
    } finally {
        setButtonsDisabled(".slot-action-button", false);
    }
}

function resetCreateSlotForm() {
    const createSlotForm = document.getElementById("createSlotForm");

    if (createSlotForm) {
        createSlotForm.reset();
    }
}
/* =========================
   ACCOUNT MANAGEMENT
========================= */

async function loadAccounts(options = {}) {
    const refreshAccountsButton = document.getElementById("refreshAccountsButton");
    const accountsTableBody = document.getElementById("accountsTableBody");

    if (!options.keepMessage) {
        clearAccountMessage();
    }

    if (refreshAccountsButton) {
        refreshAccountsButton.disabled = true;
    }

    if (accountsTableBody) {
        accountsTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-cell">Loading accounts...</td>
            </tr>
        `;
    }

    try {
        const response = await apiFetch(ADMIN_ACCOUNT_ENDPOINTS.accounts, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load accounts.");
        }

        allAccounts = Array.isArray(response.data) ? response.data : [];
        renderAccounts(allAccounts);
    } catch (error) {
        console.error(error);
        allAccounts = [];
        renderAccounts([]);
        showAccountMessage(error.message || "Cannot load accounts.", "error");
    } finally {
        if (refreshAccountsButton) {
            refreshAccountsButton.disabled = false;
        }
    }
}

function renderAccounts(accounts) {
    const accountsTableBody = document.getElementById("accountsTableBody");

    if (!accountsTableBody) {
        return;
    }

    accountsTableBody.innerHTML = "";

    if (!accounts || accounts.length === 0) {
        accountsTableBody.innerHTML = `
            <tr>
                <td colspan="5" class="empty-cell">No accounts found.</td>
            </tr>
        `;
        return;
    }

    accounts.forEach(function (account) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(account.id));
        row.appendChild(createTextCell(account.username));
        row.appendChild(createTextCell(account.role));

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderAccountStatusBadge(account.enabled));
        row.appendChild(statusCell);

        const actionsCell = document.createElement("td");
        actionsCell.appendChild(renderAccountActions(account));
        row.appendChild(actionsCell);

        accountsTableBody.appendChild(row);
    });
}

async function handleCreateAccount(event) {
    event.preventDefault();

    clearAccountMessage();

    const createAccountButton = document.getElementById("createAccountButton");

    let requestBody;

    try {
        requestBody = buildCreateAccountRequest();
    } catch (error) {
        showAccountMessage(error.message, "error");
        return;
    }

    setButtonLoading(createAccountButton, true, "Creating...");

    try {
        const response = await apiFetch(ADMIN_ACCOUNT_ENDPOINTS.accounts, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Create account failed.");
        }

        resetCreateAccountForm();

        await loadAccounts({
            keepMessage: true
        });

        showAccountMessage(response.message || "Create account successfully.", "success");
    } catch (error) {
        console.error(error);
        clearAccountPasswordOnly();
        showAccountMessage(error.message || "Create account failed.", "error");
    } finally {
        setButtonLoading(createAccountButton, false, "Create Account");
    }
}

function buildCreateAccountRequest() {
    const usernameInput = document.getElementById("accountUsernameInput");
    const passwordInput = document.getElementById("accountPasswordInput");
    const roleSelect = document.getElementById("accountRoleSelect");

    const username = usernameInput?.value.trim() || "";
    const password = passwordInput?.value.trim() || "";
    const role = roleSelect?.value.trim() || "";

    if (!username) {
        usernameInput?.focus();
        throw new Error("Username is required.");
    }

    if (!password) {
        passwordInput?.focus();
        throw new Error("Password is required.");
    }

    if (!role) {
        roleSelect?.focus();
        throw new Error("Role is required.");
    }

    if (role !== "STAFF" && role !== "ADMIN") {
        roleSelect?.focus();
        throw new Error("Role must be STAFF or ADMIN.");
    }

    return {
        username,
        password,
        role
    };
}

function renderAccountStatusBadge(enabled) {
    const badge = document.createElement("span");

    if (enabled === true) {
        badge.className = "status-badge status-active";
        badge.textContent = "Active";
        return badge;
    }

    badge.className = "status-badge status-locked";
    badge.textContent = "Locked";
    return badge;
}

function renderAccountActions(account) {
    const actionGroup = document.createElement("div");
    actionGroup.className = "action-group";

    if (account.enabled === true) {
        actionGroup.appendChild(
            createActionButton(
                "Lock",
                "account-lock",
                account.id,
                "lock-button account-action-button"
            )
        );

        return actionGroup;
    }

    actionGroup.appendChild(
        createActionButton(
            "Unlock",
            "account-unlock",
            account.id,
            "unlock-button account-action-button"
        )
    );

    return actionGroup;
}

function handleAccountActionClick(event) {
    const button = event.target.closest("button[data-action][data-id]");

    if (!button) {
        return;
    }

    const action = button.dataset.action;
    const id = button.dataset.id;

    if (!id) {
        showAccountMessage("Account id is missing.", "error");
        return;
    }

    if (action === "account-lock") {
        handleLockAccount(id);
        return;
    }

    if (action === "account-unlock") {
        handleUnlockAccount(id);
    }
}

async function handleLockAccount(id) {
    const confirmed = window.confirm("Lock this account?");

    if (!confirmed) {
        return;
    }

    clearAccountMessage();
    setButtonsDisabled(".account-action-button", true);

    try {
        const response = await apiFetch(ADMIN_ACCOUNT_ENDPOINTS.lockAccount(id), {
            method: "PATCH"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Lock account failed.");
        }

        await loadAccounts({
            keepMessage: true
        });

        showAccountMessage(response.message || "Lock account successfully.", "success");
    } catch (error) {
        console.error(error);
        showAccountMessage(error.message || "Lock account failed.", "error");
    } finally {
        setButtonsDisabled(".account-action-button", false);
    }
}

async function handleUnlockAccount(id) {
    const confirmed = window.confirm("Unlock this account?");

    if (!confirmed) {
        return;
    }

    clearAccountMessage();
    setButtonsDisabled(".account-action-button", true);

    try {
        const response = await apiFetch(ADMIN_ACCOUNT_ENDPOINTS.unlockAccount(id), {
            method: "PATCH"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Unlock account failed.");
        }

        await loadAccounts({
            keepMessage: true
        });

        showAccountMessage(response.message || "Unlock account successfully.", "success");
    } catch (error) {
        console.error(error);
        showAccountMessage(error.message || "Unlock account failed.", "error");
    } finally {
        setButtonsDisabled(".account-action-button", false);
    }
}

function resetCreateAccountForm() {
    const createAccountForm = document.getElementById("createAccountForm");

    if (createAccountForm) {
        createAccountForm.reset();
    }
}

function clearAccountPasswordOnly() {
    const passwordInput = document.getElementById("accountPasswordInput");

    if (passwordInput) {
        passwordInput.value = "";
    }
}

function showAccountMessage(message, type = "info") {
    const accountMessage = document.getElementById("accountMessage");

    if (!accountMessage) {
        return;
    }

    accountMessage.textContent = message;
    accountMessage.className = `page-message ${type}`;
}

function clearAccountMessage() {
    const accountMessage = document.getElementById("accountMessage");

    if (!accountMessage) {
        return;
    }

    accountMessage.textContent = "";
    accountMessage.className = "page-message hidden";
}
/* =========================
   SHARED HELPERS
========================= */

function renderStatusBadge(status, allowedStatuses) {
    const badge = document.createElement("span");
    const normalizedStatus = String(status || "UNKNOWN").toUpperCase();

    const statusClass = allowedStatuses.includes(normalizedStatus)
        ? normalizedStatus.toLowerCase()
        : "unknown";

    badge.className = `status-badge status-${statusClass}`;
    badge.textContent = normalizedStatus;

    return badge;
}

function createActionButton(label, action, id, className) {
    const button = document.createElement("button");

    button.type = "button";
    button.className = `action-button ${className}`;
    button.textContent = label;
    button.dataset.action = action;
    button.dataset.id = id;

    return button;
}

function createTextCell(value) {
    const cell = document.createElement("td");
    cell.textContent = value ?? "-";
    return cell;
}

function setButtonsDisabled(selector, disabled) {
    const buttons = document.querySelectorAll(selector);

    buttons.forEach(function (button) {
        button.disabled = disabled;
    });
}

function setButtonLoading(button, isLoading, loadingText) {
    if (!button) {
        return;
    }

    if (isLoading) {
        button.dataset.originalText = button.textContent;
        button.textContent = loadingText;
        button.disabled = true;
        return;
    }

    button.textContent = button.dataset.originalText || loadingText;
    button.disabled = false;
}

function showVehicleMessage(message, type = "info") {
    const pageMessage = document.getElementById("pageMessage");

    if (!pageMessage) {
        return;
    }

    pageMessage.textContent = message;
    pageMessage.className = `page-message ${type}`;
}

function clearVehicleMessage() {
    const pageMessage = document.getElementById("pageMessage");

    if (!pageMessage) {
        return;
    }

    pageMessage.textContent = "";
    pageMessage.className = "page-message hidden";
}

function showSlotMessage(message, type = "info") {
    const slotMessage = document.getElementById("slotMessage");

    if (!slotMessage) {
        return;
    }

    slotMessage.textContent = message;
    slotMessage.className = `page-message ${type}`;
}

function clearSlotMessage() {
    const slotMessage = document.getElementById("slotMessage");

    if (!slotMessage) {
        return;
    }

    slotMessage.textContent = "";
    slotMessage.className = "page-message hidden";
}

function setText(elementId, value) {
    const element = document.getElementById(elementId);

    if (element) {
        element.textContent = value;
    }
}

function handleSlotZoneChange() {
    const slotZoneSelect = document.getElementById("slotZoneSelect");
    const slotFloorInput = document.getElementById("slotFloorInput");
    const slotVehicleTypeSelect = document.getElementById("slotVehicleTypeSelect");

    if (!slotZoneSelect) {
        return;
    }

    const selectedOption = slotZoneSelect.options[slotZoneSelect.selectedIndex];

    if (!selectedOption || !selectedOption.value) {
        return;
    }

    const floorCode = selectedOption.dataset.floorCode || "";
    const vehicleType = selectedOption.dataset.vehicleType || "";

    if (floorCode && slotFloorInput) {
        slotFloorInput.value = floorCode;
    }

    if (vehicleType && slotVehicleTypeSelect) {
        slotVehicleTypeSelect.value = vehicleType;
    }
}