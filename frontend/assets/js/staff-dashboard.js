"use strict";

let checkInMode = "REGISTERED";

const STAFF_ENDPOINTS = (() => {
    const fallbackEndpoints = {
        activeSessions: "/api/parking-sessions/active",
        checkIn: "/api/parking-sessions/check-in",
        checkOut: "/api/parking-sessions/check-out",
        history: "/api/parking-sessions/history",
        invoices: "/api/invoices",
        incidents: "/api/incidents",
        updateIncidentStatus: function (id) {
            return `/api/incidents/${id}/status`;
        }
    };

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.parkingSessions
    ) {
        return {
            activeSessions:
                API_ENDPOINTS.parkingSessions.active ||
                API_ENDPOINTS.parkingSessions.activeSessions ||
                fallbackEndpoints.activeSessions,

            checkIn:
                API_ENDPOINTS.parkingSessions.checkIn ||
                fallbackEndpoints.checkIn,

            checkOut:
                API_ENDPOINTS.parkingSessions.checkOut ||
                fallbackEndpoints.checkOut,
            history:
                API_ENDPOINTS.parkingSessions.history ||
                fallbackEndpoints.history,
            invoices:
                API_ENDPOINTS.invoices?.list ||
                fallbackEndpoints.invoices,
            incidents:
                API_ENDPOINTS.incidents?.list ||
                fallbackEndpoints.incidents,

            updateIncidentStatus:
                API_ENDPOINTS.incidents?.updateStatus ||
                fallbackEndpoints.updateIncidentStatus
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
            requireRole(["STAFF"]);
        } else {
            requireStaffRoleFallback();
        }

        renderCurrentUser();
        bindEvents();
        updateCheckInModeUI();
        loadActiveSessions();
        loadSessionHistory();
        loadInvoices();
        loadIncidentReports();

    } catch (error) {
        console.error(error);
        showCheckInMessage("Cannot load staff dashboard.", "error");
    }
});

function requireStaffRoleFallback() {
    const token = localStorage.getItem("accessToken");
    const role = localStorage.getItem("role");

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    if (role !== "STAFF") {
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
    const checkInForm = document.getElementById("checkInForm");
    const checkOutForm = document.getElementById("checkOutForm");
    const checkInModeRegistered = document.getElementById("checkInModeRegistered");
    const checkInModeVisitor = document.getElementById("checkInModeVisitor");
    const refreshActiveSessionsButton = document.getElementById("refreshActiveSessionsButton");
    const sessionHistoryFilterForm = document.getElementById("sessionHistoryFilterForm");
    const resetHistoryButton = document.getElementById("resetHistoryButton");
    const invoiceFilterForm = document.getElementById("invoiceFilterForm");
    const resetInvoiceButton = document.getElementById("resetInvoiceButton");
    const incidentManagementFilterForm = document.getElementById("incidentManagementFilterForm");
    const resetIncidentsButton = document.getElementById("resetIncidentsButton");
    const incidentManagementTableBody = document.getElementById("incidentManagementTableBody");

    if (incidentManagementFilterForm) {
        incidentManagementFilterForm.addEventListener("submit", handleIncidentSearch);
    }

    if (resetIncidentsButton) {
        resetIncidentsButton.addEventListener("click", handleIncidentReset);
    }

    if (incidentManagementTableBody) {
        incidentManagementTableBody.addEventListener("click", function (event) {
            const button = event.target.closest("button[data-incident-action='update']");

            if (!button) {
                return;
            }

            const incidentId = button.dataset.incidentId;

            if (!incidentId) {
                showIncidentManagementMessage("Incident id is missing.", "error");
                return;
            }

            handleUpdateIncidentStatus(incidentId);
        });
    }

    if (invoiceFilterForm) {
        invoiceFilterForm.addEventListener("submit", handleInvoiceSearch);
    }

    if (resetInvoiceButton) {
        resetInvoiceButton.addEventListener("click", handleInvoiceReset);
    }

    if (sessionHistoryFilterForm) {
    sessionHistoryFilterForm.addEventListener("submit", handleHistorySearch);
    }

    if (resetHistoryButton) {
        resetHistoryButton.addEventListener("click", handleHistoryReset);
    }
    if (logoutButton) {
        logoutButton.addEventListener("click", handleLogout);
    }

    if (checkInForm) {
        checkInForm.addEventListener("submit", handleCheckInSubmit);
    }

    if (checkOutForm) {
        checkOutForm.addEventListener("submit", handleCheckOutSubmit);
    }

    if (checkInModeRegistered) {
        checkInModeRegistered.addEventListener("change", function () {
            if (checkInModeRegistered.checked) {
                checkInMode = "REGISTERED";
                updateCheckInModeUI();
            }
        });
    }

    if (checkInModeVisitor) {
        checkInModeVisitor.addEventListener("change", function () {
            if (checkInModeVisitor.checked) {
                checkInMode = "VISITOR";
                updateCheckInModeUI();
            }
        });
    }

    if (refreshActiveSessionsButton) {
        refreshActiveSessionsButton.addEventListener("click", function () {
            loadActiveSessions();
        });
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

function updateCheckInModeUI() {
    const checkInVehicleTypeGroup = document.getElementById("checkInVehicleTypeGroup");
    const checkInVehicleTypeSelect = document.getElementById("checkInVehicleTypeSelect");
    const checkInModeHint = document.getElementById("checkInModeHint");

    const isVisitor = checkInMode === "VISITOR";

    if (checkInVehicleTypeGroup) {
        checkInVehicleTypeGroup.style.display = isVisitor ? "flex" : "none";
    }

    if (checkInVehicleTypeSelect) {
        checkInVehicleTypeSelect.disabled = !isVisitor;

        if (!isVisitor) {
            checkInVehicleTypeSelect.value = "";
        }
    }

    if (checkInModeHint) {
        checkInModeHint.textContent = isVisitor
            ? "Visitor vehicle requires vehicle type."
            : "Registered vehicle only needs plate number.";
    }

    clearCheckInMessage();
}

async function handleCheckInSubmit(event) {
    event.preventDefault();

    clearCheckInMessage();

    const checkInButton = document.getElementById("checkInButton");

    let requestBody;

    try {
        requestBody = buildCheckInRequest();
    } catch (error) {
        showCheckInMessage(error.message, "error");
        return;
    }

    setButtonLoading(checkInButton, true, "Checking in...");

    try {
        const response = await apiFetch(STAFF_ENDPOINTS.checkIn, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Check-in failed.");
        }

        resetCheckInForm();

        await loadActiveSessions({
            keepMessage: true
        });

        showCheckInMessage(response.message || "Check-in successfully.", "success");
    } catch (error) {
        console.error(error);
        showCheckInMessage(error.message || "Check-in failed.", "error");
    } finally {
        setButtonLoading(checkInButton, false, "Check-in");
    }
}

async function loadIncidentReports(options = {}) {
    const incidentManagementTableBody = document.getElementById("incidentManagementTableBody");

    if (!options.keepMessage) {
        clearIncidentManagementMessage();
    }

    if (incidentManagementTableBody) {
        incidentManagementTableBody.innerHTML = `
            <tr>
                <td colspan="12" class="empty-cell">Loading incident reports...</td>
            </tr>
        `;
    }

    const queryString = buildIncidentQueryParams();
    const endpoint = queryString
        ? `${STAFF_ENDPOINTS.incidents}?${queryString}`
        : STAFF_ENDPOINTS.incidents;

    try {
        const response = await apiFetch(endpoint, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load incident reports.");
        }

        const reports = Array.isArray(response.data) ? response.data : [];
        renderIncidentReports(reports);
    } catch (error) {
        console.error(error);
        renderIncidentReports([]);
        showIncidentManagementMessage(error.message || "Cannot load incident reports.", "error");
    }
}

function buildIncidentQueryParams() {
    const statusFilter = document.getElementById("incidentStatusFilter");
    const typeFilter = document.getElementById("incidentTypeFilter");
    const priorityFilter = document.getElementById("incidentPriorityFilter");
    const userIdFilter = document.getElementById("incidentUserIdFilter");
    const plateNumberFilter = document.getElementById("incidentPlateNumberFilter");
    const fromDateFilter = document.getElementById("incidentFromDateFilter");
    const toDateFilter = document.getElementById("incidentToDateFilter");

    const status = statusFilter?.value.trim() || "";
    const type = typeFilter?.value.trim() || "";
    const priority = priorityFilter?.value.trim() || "";
    const userId = userIdFilter?.value.trim() || "";
    const plateNumber = plateNumberFilter?.value.trim() || "";
    const fromDate = fromDateFilter?.value || "";
    const toDate = toDateFilter?.value || "";

    const params = new URLSearchParams();

    if (status) {
        params.set("status", status);
    }

    if (type) {
        params.set("type", type);
    }

    if (priority) {
        params.set("priority", priority);
    }

    if (userId) {
        params.set("userId", userId);
    }

    if (plateNumber) {
        params.set("plateNumber", plateNumber);
    }

    if (fromDate) {
        params.set("fromDate", fromDate);
    }

    if (toDate) {
        params.set("toDate", toDate);
    }

    return params.toString();
}

function renderIncidentReports(reports) {
    const incidentManagementTableBody = document.getElementById("incidentManagementTableBody");

    if (!incidentManagementTableBody) {
        return;
    }

    incidentManagementTableBody.innerHTML = "";

    if (!reports || reports.length === 0) {
        incidentManagementTableBody.innerHTML = `
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
        priorityCell.appendChild(renderIncidentPriorityBadge(report.priority));
        row.appendChild(priorityCell);

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderIncidentStatusBadge(report.status));
        row.appendChild(statusCell);

        row.appendChild(createTextCell(report.plateNumber || "-"));
        row.appendChild(createLongTextCell(report.description || "-"));
        row.appendChild(createLongTextCell(report.staffNote || "-"));
        row.appendChild(createTextCell(formatDateTime(report.createdAt)));
        row.appendChild(createTextCell(formatDateTime(report.resolvedAt)));

        const actionsCell = document.createElement("td");
        actionsCell.appendChild(renderIncidentActions(report));
        row.appendChild(actionsCell);

        incidentManagementTableBody.appendChild(row);
    });
}

function renderIncidentActions(report) {
    const wrapper = document.createElement("div");
    wrapper.className = "incident-action-box";

    const statusSelect = document.createElement("select");
    statusSelect.className = "incident-row-status";
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
    noteTextarea.className = "incident-row-note";
    noteTextarea.dataset.incidentId = report.id;
    noteTextarea.rows = 2;
    noteTextarea.placeholder = "Staff note...";
    noteTextarea.value = report.staffNote || "";

    const updateButton = document.createElement("button");
    updateButton.type = "button";
    updateButton.className = "primary-button incident-update-button";
    updateButton.textContent = "Update";
    updateButton.dataset.incidentAction = "update";
    updateButton.dataset.incidentId = report.id;

    wrapper.appendChild(statusSelect);
    wrapper.appendChild(noteTextarea);
    wrapper.appendChild(updateButton);

    return wrapper;
}

function handleIncidentSearch(event) {
    event.preventDefault();
    loadIncidentReports();
}

function handleIncidentReset() {
    const incidentManagementFilterForm = document.getElementById("incidentManagementFilterForm");

    if (incidentManagementFilterForm) {
        incidentManagementFilterForm.reset();
    }

    clearIncidentManagementMessage();
    loadIncidentReports();
}

async function handleUpdateIncidentStatus(id) {
    clearIncidentManagementMessage();

    const statusSelect = document.querySelector(`.incident-row-status[data-incident-id="${id}"]`);
    const noteTextarea = document.querySelector(`.incident-row-note[data-incident-id="${id}"]`);
    const updateButton = document.querySelector(`button[data-incident-action="update"][data-incident-id="${id}"]`);

    const status = statusSelect?.value.trim() || "";
    const staffNote = noteTextarea?.value.trim() || "";

    if (!status) {
        showIncidentManagementMessage("Status is required.", "error");
        return;
    }

    setButtonLoading(updateButton, true, "Updating...");

    try {
        const response = await apiFetch(STAFF_ENDPOINTS.updateIncidentStatus(id), {
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

        await loadIncidentReports({
            keepMessage: true
        });

        showIncidentManagementMessage(response.message || "Update incident successfully.", "success");
    } catch (error) {
        console.error(error);
        showIncidentManagementMessage(error.message || "Update incident failed.", "error");
    } finally {
        setButtonLoading(updateButton, false, "Update");
    }
}

function renderIncidentStatusBadge(status) {
    const badge = document.createElement("span");
    const normalizedStatus = String(status || "UNKNOWN").toUpperCase();

    let statusClass = "unknown";

    if (normalizedStatus === "OPEN") {
        statusClass = "open";
    }

    if (normalizedStatus === "IN_PROGRESS") {
        statusClass = "in-progress";
    }

    if (normalizedStatus === "RESOLVED") {
        statusClass = "resolved";
    }

    if (normalizedStatus === "REJECTED") {
        statusClass = "rejected";
    }

    badge.className = `status-badge incident-status-${statusClass}`;
    badge.textContent = normalizedStatus;

    return badge;
}

function renderIncidentPriorityBadge(priority) {
    const badge = document.createElement("span");
    const normalizedPriority = String(priority || "UNKNOWN").toUpperCase();

    let priorityClass = "unknown";

    if (normalizedPriority === "LOW") {
        priorityClass = "low";
    }

    if (normalizedPriority === "MEDIUM") {
        priorityClass = "medium";
    }

    if (normalizedPriority === "HIGH") {
        priorityClass = "high";
    }

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

function showIncidentManagementMessage(message, type = "info") {
    const incidentManagementMessage = document.getElementById("incidentManagementMessage");

    if (!incidentManagementMessage) {
        return;
    }

    incidentManagementMessage.textContent = message;
    incidentManagementMessage.className = `form-message ${type}`;
}

function clearIncidentManagementMessage() {
    const incidentManagementMessage = document.getElementById("incidentManagementMessage");

    if (!incidentManagementMessage) {
        return;
    }

    incidentManagementMessage.textContent = "";
    incidentManagementMessage.className = "form-message hidden";
}

function buildCheckInRequest() {
    const plateNumberInput = document.getElementById("checkInPlateNumberInput");
    const vehicleTypeSelect = document.getElementById("checkInVehicleTypeSelect");

    const plateNumber = plateNumberInput?.value.trim() || "";

    if (!plateNumber) {
        plateNumberInput?.focus();
        throw new Error("Plate number is required");
    }

    if (checkInMode === "REGISTERED") {
        return {
            plateNumber
        };
    }

    const vehicleType = vehicleTypeSelect?.value.trim() || "";

    if (!vehicleType) {
        vehicleTypeSelect?.focus();
        throw new Error("Vehicle type is required for visitor vehicle");
    }

    return {
        plateNumber,
        vehicleType
    };
}

async function handleCheckOutSubmit(event) {
    event.preventDefault();

    clearCheckOutMessage();
    clearCheckOutResult();

    const checkOutButton = document.getElementById("checkOutButton");
    const checkOutPlateNumberInput = document.getElementById("checkOutPlateNumberInput");

    const plateNumber = checkOutPlateNumberInput?.value.trim() || "";

    if (!plateNumber) {
        showCheckOutMessage("Plate number is required", "error");
        checkOutPlateNumberInput?.focus();
        return;
    }

    setButtonLoading(checkOutButton, true, "Checking out...");

    try {
        const response = await apiFetch(STAFF_ENDPOINTS.checkOut, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                plateNumber
            })
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Check-out failed.");
        }

        if (checkOutPlateNumberInput) {
            checkOutPlateNumberInput.value = "";
        }

        renderCheckOutResult(response.data);

        await loadActiveSessions({
            keepMessage: true
        });

        await loadSessionHistory({
            keepMessage: true
        });

        await loadInvoices({
            keepMessage: true
        });

        showCheckOutMessage(response.message || "Check-out successfully.", "success");
    } catch (error) {
        console.error(error);
        showCheckOutMessage(error.message || "Check-out failed.", "error");
    } finally {
        setButtonLoading(checkOutButton, false, "Check-out");
    }
}

async function loadActiveSessions(options = {}) {
    const activeSessionsTableBody = document.getElementById("activeSessionsTableBody");
    const refreshActiveSessionsButton = document.getElementById("refreshActiveSessionsButton");

    if (!options.keepMessage) {
        clearCheckInMessage();
        clearCheckOutMessage();
    }

    if (refreshActiveSessionsButton) {
        refreshActiveSessionsButton.disabled = true;
    }

    if (activeSessionsTableBody) {
        activeSessionsTableBody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-cell">Loading active sessions...</td>
            </tr>
        `;
    }

    try {
        const response = await apiFetch(STAFF_ENDPOINTS.activeSessions, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load active sessions.");
        }

        const sessions = Array.isArray(response.data) ? response.data : [];
        renderActiveSessions(sessions);
    } catch (error) {
        console.error(error);
        renderActiveSessions([]);
        showCheckInMessage(error.message || "Cannot load active sessions.", "error");
    } finally {
        if (refreshActiveSessionsButton) {
            refreshActiveSessionsButton.disabled = false;
        }
    }
}
async function loadInvoices(options = {}) {
    const invoiceTableBody = document.getElementById("invoiceTableBody");

    if (!options.keepMessage) {
        clearInvoiceMessage();
    }

    if (invoiceTableBody) {
        invoiceTableBody.innerHTML = `
            <tr>
                <td colspan="10" class="empty-cell">Loading invoices...</td>
            </tr>
        `;
    }

    const queryString = buildInvoiceQueryParams();
    const endpoint = queryString
        ? `${STAFF_ENDPOINTS.invoices}?${queryString}`
        : STAFF_ENDPOINTS.invoices;

    try {
        const response = await apiFetch(endpoint, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load invoices.");
        }

        const invoices = Array.isArray(response.data) ? response.data : [];
        renderInvoices(invoices);
    } catch (error) {
        console.error(error);
        renderInvoices([]);
        showInvoiceMessage(error.message || "Cannot load invoices.", "error");
    }
}

function buildInvoiceQueryParams() {
    const plateNumberInput = document.getElementById("invoicePlateNumberInput");
    const statusSelect = document.getElementById("invoiceStatusSelect");
    const typeSelect = document.getElementById("invoiceTypeSelect");
    const fromDateInput = document.getElementById("invoiceFromDateInput");
    const toDateInput = document.getElementById("invoiceToDateInput");

    const plateNumber = plateNumberInput?.value.trim() || "";
    const status = statusSelect?.value.trim() || "";
    const type = typeSelect?.value.trim() || "";
    const fromDate = fromDateInput?.value || "";
    const toDate = toDateInput?.value || "";

    const params = new URLSearchParams();

    if (plateNumber) {
        params.set("plateNumber", plateNumber);
    }

    if (status) {
        params.set("status", status);
    }

    if (type) {
        params.set("type", type);
    }

    if (fromDate) {
        params.set("fromDate", fromDate);
    }

    if (toDate) {
        params.set("toDate", toDate);
    }

    return params.toString();
}

function renderInvoices(invoices) {
    const invoiceTableBody = document.getElementById("invoiceTableBody");

    if (!invoiceTableBody) {
        return;
    }

    invoiceTableBody.innerHTML = "";

    if (!invoices || invoices.length === 0) {
        invoiceTableBody.innerHTML = `
            <tr>
                <td colspan="10" class="empty-cell">No invoices found.</td>
            </tr>
        `;
        return;
    }

    invoices.forEach(function (invoice) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(invoice.id));
        row.appendChild(createTextCell(invoice.parkingSessionId ?? "-"));
        row.appendChild(createTextCell(invoice.userId ?? "-"));
        row.appendChild(createTextCell(invoice.plateNumber || "-"));
        row.appendChild(createTextCell(invoice.vehicleType || "-"));
        row.appendChild(createTextCell(formatMoney(invoice.amount)));

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderInvoiceStatusBadge(invoice.status));
        row.appendChild(statusCell);

        const typeCell = document.createElement("td");
        typeCell.appendChild(renderInvoiceTypeBadge(invoice.type));
        row.appendChild(typeCell);

        row.appendChild(createTextCell(formatDateTime(invoice.issuedAt)));
        row.appendChild(createTextCell(formatDateTime(invoice.paidAt)));

        invoiceTableBody.appendChild(row);
    });
}

function handleInvoiceSearch(event) {
    event.preventDefault();
    loadInvoices();
}

function handleInvoiceReset() {
    const invoiceFilterForm = document.getElementById("invoiceFilterForm");

    if (invoiceFilterForm) {
        invoiceFilterForm.reset();
    }

    clearInvoiceMessage();
    loadInvoices();
}

function renderInvoiceStatusBadge(status) {
    const badge = document.createElement("span");
    const normalizedStatus = String(status || "UNKNOWN").toUpperCase();

    let statusClass = "unknown";

    if (normalizedStatus === "PAID") {
        statusClass = "paid";
    }

    if (normalizedStatus === "UNPAID") {
        statusClass = "unpaid";
    }

    if (normalizedStatus === "CANCELLED") {
        statusClass = "cancelled";
    }

    badge.className = `status-badge status-${statusClass}`;
    badge.textContent = normalizedStatus;

    return badge;
}

function renderInvoiceTypeBadge(type) {
    const badge = document.createElement("span");
    const normalizedType = String(type || "UNKNOWN").toUpperCase();

    let typeClass = "unknown";

    if (normalizedType === "GUEST") {
        typeClass = "guest";
    }

    if (normalizedType === "REGISTERED_USER") {
        typeClass = "registered-user";
    }

    badge.className = `type-badge type-${typeClass}`;
    badge.textContent = normalizedType;

    return badge;
}

function showInvoiceMessage(message, type = "info") {
    const invoiceMessage = document.getElementById("invoiceMessage");

    if (!invoiceMessage) {
        return;
    }

    invoiceMessage.textContent = message;
    invoiceMessage.className = `form-message ${type}`;
}

function clearInvoiceMessage() {
    const invoiceMessage = document.getElementById("invoiceMessage");

    if (!invoiceMessage) {
        return;
    }

    invoiceMessage.textContent = "";
    invoiceMessage.className = "form-message hidden";
}

function renderActiveSessions(sessions) {
    const activeSessionsTableBody = document.getElementById("activeSessionsTableBody");

    if (!activeSessionsTableBody) {
        return;
    }

    activeSessionsTableBody.innerHTML = "";

    if (!sessions || sessions.length === 0) {
        activeSessionsTableBody.innerHTML = `
            <tr>
                <td colspan="8" class="empty-cell">No active sessions found.</td>
            </tr>
        `;
        return;
    }

    sessions.forEach(function (session) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(session.id));
        row.appendChild(createTextCell(session.vehicleId ?? "-"));
        row.appendChild(createTextCell(session.plateNumber || session.vehiclePlateNumber || "-"));
        row.appendChild(createTextCell(session.vehicleType || session.type || "-"));
        row.appendChild(createTextCell(session.parkingSlotId ?? "-"));
        row.appendChild(createTextCell(session.slotCode || session.parkingSlotCode || "-"));
        row.appendChild(createTextCell(formatDateTime(session.checkInTime)));

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderSessionStatusBadge(session.status));
        row.appendChild(statusCell);

        activeSessionsTableBody.appendChild(row);
    });
}

function renderSessionStatusBadge(status) {
    const badge = document.createElement("span");
    const normalizedStatus = String(status || "UNKNOWN").toUpperCase();

    let statusClass = "unknown";

    if (normalizedStatus === "ACTIVE") {
        statusClass = "active";
    }

    if (normalizedStatus === "COMPLETED") {
        statusClass = "completed";
    }

    badge.className = `status-badge status-${statusClass}`;
    badge.textContent = normalizedStatus;

    return badge;
}

function renderCheckOutResult(data) {
    const checkOutResult = document.getElementById("checkOutResult");

    if (!checkOutResult || !data) {
        return;
    }

    const totalAmount = data.totalAmount ?? data.amount ?? "-";
    const durationHours = data.durationHours ?? "-";

    checkOutResult.innerHTML = `
        <div><strong>Session ID:</strong> ${escapeHtml(data.sessionId ?? data.id ?? "-")}</div>
        <div><strong>Plate number:</strong> ${escapeHtml(data.plateNumber ?? "-")}</div>
        <div><strong>Vehicle type:</strong> ${escapeHtml(data.vehicleType ?? data.type ?? "-")}</div>
        <div><strong>Slot:</strong> ${escapeHtml(data.slotCode ?? "-")}</div>
        <div><strong>Check-in time:</strong> ${escapeHtml(formatDateTime(data.checkInTime))}</div>
        <div><strong>Check-out time:</strong> ${escapeHtml(formatDateTime(data.checkOutTime))}</div>
        <div><strong>Duration hours:</strong> ${escapeHtml(durationHours)}</div>
        <div><strong>Total amount:</strong> ${escapeHtml(totalAmount)}</div>
    `;

    checkOutResult.classList.remove("hidden");
}

function resetCheckInForm() {
    const checkInForm = document.getElementById("checkInForm");
    const checkInModeRegistered = document.getElementById("checkInModeRegistered");

    if (checkInForm) {
        checkInForm.reset();
    }

    checkInMode = "REGISTERED";

    if (checkInModeRegistered) {
        checkInModeRegistered.checked = true;
    }

    updateCheckInModeUI();
}

function showCheckInMessage(message, type = "info") {
    showFormMessage("checkInMessage", message, type);
}

function clearCheckInMessage() {
    clearFormMessage("checkInMessage");
}

function showCheckOutMessage(message, type = "info") {
    showFormMessage("checkOutMessage", message, type);
}

function clearCheckOutMessage() {
    clearFormMessage("checkOutMessage");
}

function clearCheckOutResult() {
    const checkOutResult = document.getElementById("checkOutResult");

    if (!checkOutResult) {
        return;
    }

    checkOutResult.innerHTML = "";
    checkOutResult.classList.add("hidden");
}

function showFormMessage(elementId, message, type = "info") {
    const element = document.getElementById(elementId);

    if (!element) {
        return;
    }

    element.textContent = message;
    element.className = `form-message ${type}`;
}

function clearFormMessage(elementId) {
    const element = document.getElementById(elementId);

    if (!element) {
        return;
    }

    element.textContent = "";
    element.className = "form-message hidden";
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
async function loadSessionHistory(options = {}) {
    const sessionHistoryTableBody = document.getElementById("sessionHistoryTableBody");

    if (!options.keepMessage) {
        clearSessionHistoryMessage();
    }

    if (sessionHistoryTableBody) {
        sessionHistoryTableBody.innerHTML = `
            <tr>
                <td colspan="9" class="empty-cell">Loading session history...</td>
            </tr>
        `;
    }

    const queryString = buildHistoryQueryParams();
    const endpoint = queryString
        ? `${STAFF_ENDPOINTS.history}?${queryString}`
        : STAFF_ENDPOINTS.history;

    try {
        const response = await apiFetch(endpoint, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load session history.");
        }

        const history = Array.isArray(response.data) ? response.data : [];
        renderSessionHistory(history);
    } catch (error) {
        console.error(error);
        renderSessionHistory([]);
        showSessionHistoryMessage(error.message || "Cannot load session history.", "error");
    }
}

function buildHistoryQueryParams() {
    const plateNumberInput = document.getElementById("historyPlateNumberInput");
    const statusSelect = document.getElementById("historyStatusSelect");
    const fromDateInput = document.getElementById("historyFromDateInput");
    const toDateInput = document.getElementById("historyToDateInput");

    const plateNumber = plateNumberInput?.value.trim() || "";
    const status = statusSelect?.value.trim() || "";
    const fromDate = fromDateInput?.value || "";
    const toDate = toDateInput?.value || "";

    const params = new URLSearchParams();

    if (plateNumber) {
        params.set("plateNumber", plateNumber);
    }

    if (status) {
        params.set("status", status);
    }

    if (fromDate) {
        params.set("fromDate", fromDate);
    }

    if (toDate) {
        params.set("toDate", toDate);
    }

    return params.toString();
}

function renderSessionHistory(history) {
    const sessionHistoryTableBody = document.getElementById("sessionHistoryTableBody");

    if (!sessionHistoryTableBody) {
        return;
    }

    sessionHistoryTableBody.innerHTML = "";

    if (!history || history.length === 0) {
        sessionHistoryTableBody.innerHTML = `
            <tr>
                <td colspan="9" class="empty-cell">No session history found.</td>
            </tr>
        `;
        return;
    }

    history.forEach(function (item) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(item.id));
        row.appendChild(createTextCell(item.plateNumber || "-"));
        row.appendChild(createTextCell(item.vehicleType || "-"));
        row.appendChild(createTextCell(item.slotCode || "-"));
        row.appendChild(createTextCell(formatDateTime(item.checkInTime)));
        row.appendChild(createTextCell(formatDateTime(item.checkOutTime)));
        row.appendChild(createTextCell(item.durationHours ?? "-"));
        row.appendChild(createTextCell(formatMoney(item.totalAmount)));

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderSessionStatusBadge(item.status));
        row.appendChild(statusCell);

        sessionHistoryTableBody.appendChild(row);
    });
}

function handleHistorySearch(event) {
    event.preventDefault();
    loadSessionHistory();
}

function handleHistoryReset() {
    const sessionHistoryFilterForm = document.getElementById("sessionHistoryFilterForm");

    if (sessionHistoryFilterForm) {
        sessionHistoryFilterForm.reset();
    }

    clearSessionHistoryMessage();
    loadSessionHistory();
}

function showSessionHistoryMessage(message, type = "info") {
    const sessionHistoryMessage = document.getElementById("sessionHistoryMessage");

    if (!sessionHistoryMessage) {
        return;
    }

    sessionHistoryMessage.textContent = message;
    sessionHistoryMessage.className = `form-message ${type}`;
}

function clearSessionHistoryMessage() {
    const sessionHistoryMessage = document.getElementById("sessionHistoryMessage");

    if (!sessionHistoryMessage) {
        return;
    }

    sessionHistoryMessage.textContent = "";
    sessionHistoryMessage.className = "form-message hidden";
}

function formatMoney(value) {
    if (value === null || value === undefined || value === "") {
        return "-";
    }

    const numberValue = Number(value);

    if (Number.isNaN(numberValue)) {
        return value;
    }

    return `${numberValue.toLocaleString("vi-VN")} VND`;
}

function createTextCell(value) {
    const cell = document.createElement("td");
    cell.textContent = value ?? "-";
    return cell;
}

function setText(elementId, value) {
    const element = document.getElementById(elementId);

    if (element) {
        element.textContent = value;
    }
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

function escapeHtml(value) {
    return String(value ?? "-")
        .replaceAll("&", "&amp;")
        .replaceAll("<", "&lt;")
        .replaceAll(">", "&gt;")
        .replaceAll('"', "&quot;")
        .replaceAll("'", "&#039;");
}