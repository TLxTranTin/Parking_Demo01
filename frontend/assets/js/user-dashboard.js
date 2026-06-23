"use strict";

const VEHICLE_ENDPOINTS = (() => {
    const fallbackEndpoints = {
        list: "/api/vehicles",
        register: "/api/vehicles/register"
    };

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.vehicles
    ) {
        return {
            list: API_ENDPOINTS.vehicles.list || fallbackEndpoints.list,
            register: API_ENDPOINTS.vehicles.register || fallbackEndpoints.register
        };
    }

    return fallbackEndpoints;
})();

const USER_INVOICE_ENDPOINTS = (() => {
    const fallbackEndpoints = {
        my: "/api/invoices/my"
    };

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.invoices
    ) {
        return {
            my: API_ENDPOINTS.invoices.my || fallbackEndpoints.my
        };
    }

    return fallbackEndpoints;
})();

const USER_PAYMENT_ENDPOINTS = (() => {
    const fallbackEndpoints = {
        payMyInvoices: "/api/payments/my-invoices"
    };

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.payments
    ) {
        return {
            payMyInvoices:
                API_ENDPOINTS.payments.payMyInvoices ||
                fallbackEndpoints.payMyInvoices
        };
    }

    return fallbackEndpoints;
})();

const USER_INCIDENT_ENDPOINTS = (() => {
    const fallbackEndpoints = {
        my: "/api/incidents/my"
    };

    if (
        typeof API_ENDPOINTS !== "undefined" &&
        API_ENDPOINTS.incidents
    ) {
        return {
            my: API_ENDPOINTS.incidents.my || fallbackEndpoints.my
        };
    }

    return fallbackEndpoints;
})();

document.addEventListener("DOMContentLoaded", () => {
    try {
        if (typeof requireLogin === "function") {
            requireLogin();
        }

        if (typeof requireRole === "function") {
            requireRole(["USER"]);
        } else {
            requireUserRoleFallback();
        }

        renderCurrentUser();
        bindEvents();
        loadMyVehicles();
        loadMyInvoices();
        loadMyIncidents();

    } catch (error) {
        console.error(error);
        showMessage("Cannot load user dashboard.", "error");
    }
});

function requireUserRoleFallback() {
    const token = localStorage.getItem("accessToken");
    const role = localStorage.getItem("role");

    if (!token) {
        window.location.href = "login.html";
        return;
    }

    if (role !== "USER") {
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
    const registerVehicleForm = document.getElementById("registerVehicleForm");
    const refreshVehiclesButton = document.getElementById("refreshVehiclesButton");
    const refreshMyInvoicesButton = document.getElementById("refreshMyInvoicesButton");
    const paySelectedInvoicesButton = document.getElementById("paySelectedInvoicesButton");
    const myInvoiceTableBody = document.getElementById("myInvoiceTableBody");
    const incidentForm = document.getElementById("incidentForm");
    const refreshIncidentsButton = document.getElementById("refreshIncidentsButton");

    if (incidentForm) {
        incidentForm.addEventListener("submit", handleCreateIncident);
    }

    if (refreshIncidentsButton) {
        refreshIncidentsButton.addEventListener("click", function () {
            loadMyIncidents();
        });
    }

    if (paySelectedInvoicesButton) {
        paySelectedInvoicesButton.addEventListener("click", handlePaySelectedInvoices);
    }

    if (myInvoiceTableBody) {
        myInvoiceTableBody.addEventListener("change", handleInvoiceSelectionChange);
    }

    if (refreshMyInvoicesButton) {
        refreshMyInvoicesButton.addEventListener("click", function () {
            loadMyInvoices();
        });
    }

    if (logoutButton) {
        logoutButton.addEventListener("click", handleLogout);
    }

    if (registerVehicleForm) {
        registerVehicleForm.addEventListener("submit", handleRegisterVehicle);
    }

    if (refreshVehiclesButton) {
        refreshVehiclesButton.addEventListener("click", () => {
            loadMyVehicles();
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

async function loadMyVehicles(options = {}) {
    const vehiclesTableBody = document.getElementById("vehiclesTableBody");
    const refreshVehiclesButton = document.getElementById("refreshVehiclesButton");

    if (!options.keepMessage) {
        clearMessage();
    }

    if (refreshVehiclesButton) {
        refreshVehiclesButton.disabled = true;
    }

    if (vehiclesTableBody) {
        vehiclesTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-cell">Loading vehicles...</td>
            </tr>
        `;
    }

    try {
        const response = await apiFetch(VEHICLE_ENDPOINTS.list, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load vehicles.");
        }

        const vehicles = Array.isArray(response.data) ? response.data : [];
        renderVehicles(vehicles);
    } catch (error) {
        console.error(error);
        renderVehicles([]);
        showMessage(error.message || "Cannot load vehicles.", "error");
    } finally {
        if (refreshVehiclesButton) {
            refreshVehiclesButton.disabled = false;
        }
    }
}

async function handleRegisterVehicle(event) {
    event.preventDefault();

    clearMessage();

    const plateNumberInput = document.getElementById("plateNumberInput");
    const vehicleTypeSelect = document.getElementById("vehicleTypeSelect");
    const brandInput = document.getElementById("brandInput");
    const colorInput = document.getElementById("colorInput");
    const descriptionInput = document.getElementById("descriptionInput");

    const plateNumber = plateNumberInput?.value.trim() || "";
    const type = vehicleTypeSelect?.value.trim() || "";
    const brand = brandInput?.value.trim() || "";
    const color = colorInput?.value.trim() || "";
    const description = descriptionInput?.value.trim() || "";

    if (!plateNumber) {
        showMessage("Plate number is required.", "error");
        plateNumberInput?.focus();
        return;
    }

    if (!type) {
        showMessage("Vehicle type is required.", "error");
        vehicleTypeSelect?.focus();
        return;
    }

    const submitButton = event.target.querySelector("button[type='submit']");
    if (submitButton) {
        submitButton.disabled = true;
        submitButton.textContent = "Registering...";
    }

    const requestBody = {
        plateNumber,
        type,
        brand,
        color,
        description
    };

    try {
        const response = await apiFetch(VEHICLE_ENDPOINTS.register, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Register vehicle failed.");
        }

        resetForm();

        await loadMyVehicles({
            keepMessage: true
        });

        showMessage(response.message || "Register vehicle successfully.", "success");
    } catch (error) {
        console.error(error);
        showMessage(error.message || "Register vehicle failed.", "error");
    } finally {
        if (submitButton) {
            submitButton.disabled = false;
            submitButton.textContent = "Register Vehicle";
        }
    }
}

async function loadMyInvoices(options = {}) {
    const myInvoiceTableBody = document.getElementById("myInvoiceTableBody");
    const refreshMyInvoicesButton = document.getElementById("refreshMyInvoicesButton");

    if (!options.keepMessage) {
        clearMyInvoiceMessage();
    }

    if (refreshMyInvoicesButton) {
        refreshMyInvoicesButton.disabled = true;
    }

    if (myInvoiceTableBody) {
        myInvoiceTableBody.innerHTML = `
            <tr>
                <td colspan="10" class="empty-cell">Loading invoices...</td>
            </tr>
        `;
    }

    try {
        const response = await apiFetch(USER_INVOICE_ENDPOINTS.my, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load invoices.");
        }

        const invoices = Array.isArray(response.data) ? response.data : [];

        renderMyInvoices(invoices);
        renderInvoiceSummary(invoices);
        handleInvoiceSelectionChange();
        clearPaymentMessage();

    } catch (error) {
        console.error(error);

        renderMyInvoices([]);
        renderInvoiceSummary([]);
        handleInvoiceSelectionChange();

        showMyInvoiceMessage(error.message || "Cannot load invoices.", "error");
    } finally {
        if (refreshMyInvoicesButton) {
            refreshMyInvoicesButton.disabled = false;
        }
    }
}

async function loadMyIncidents(options = {}) {
    const myIncidentTableBody = document.getElementById("myIncidentTableBody");
    const refreshIncidentsButton = document.getElementById("refreshIncidentsButton");

    if (!options.keepMessage) {
        clearIncidentMessage();
    }

    if (refreshIncidentsButton) {
        refreshIncidentsButton.disabled = true;
    }

    if (myIncidentTableBody) {
        myIncidentTableBody.innerHTML = `
            <tr>
                <td colspan="9" class="empty-cell">Loading incident reports...</td>
            </tr>
        `;
    }

    try {
        const response = await apiFetch(USER_INCIDENT_ENDPOINTS.my, {
            method: "GET"
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Cannot load incident reports.");
        }

        const incidents = Array.isArray(response.data) ? response.data : [];
        renderMyIncidents(incidents);
    } catch (error) {
        console.error(error);
        renderMyIncidents([]);
        showIncidentMessage(error.message || "Cannot load incident reports.", "error");
    } finally {
        if (refreshIncidentsButton) {
            refreshIncidentsButton.disabled = false;
        }
    }
}

function renderMyIncidents(incidents) {
    const myIncidentTableBody = document.getElementById("myIncidentTableBody");

    if (!myIncidentTableBody) {
        return;
    }

    myIncidentTableBody.innerHTML = "";

    if (!incidents || incidents.length === 0) {
        myIncidentTableBody.innerHTML = `
            <tr>
                <td colspan="9" class="empty-cell">No incident reports found.</td>
            </tr>
        `;
        return;
    }

    incidents.forEach(function (incident) {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(incident.id));
        row.appendChild(createTextCell(incident.title || "-"));
        row.appendChild(createTextCell(incident.type || "-"));

        const priorityCell = document.createElement("td");
        priorityCell.appendChild(renderIncidentPriorityBadge(incident.priority));
        row.appendChild(priorityCell);

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderIncidentStatusBadge(incident.status));
        row.appendChild(statusCell);

        row.appendChild(createTextCell(incident.plateNumber || "-"));
        row.appendChild(createTextCell(incident.staffNote || "-"));
        row.appendChild(createTextCell(formatDateTime(incident.createdAt)));
        row.appendChild(createTextCell(formatDateTime(incident.resolvedAt)));

        myIncidentTableBody.appendChild(row);
    });
}

async function handleCreateIncident(event) {
    event.preventDefault();

    clearIncidentMessage();

    const submitIncidentButton = document.getElementById("submitIncidentButton");

    let requestBody;

    try {
        requestBody = buildIncidentRequest();
    } catch (error) {
        showIncidentMessage(error.message, "error");
        return;
    }

    setButtonLoading(submitIncidentButton, true, "Submitting...");

    try {
        const response = await apiFetch(USER_INCIDENT_ENDPOINTS.my, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestBody)
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Submit report failed.");
        }

        resetIncidentForm();

        await loadMyIncidents({
            keepMessage: true
        });

        showIncidentMessage(response.message || "Submit report successfully.", "success");
    } catch (error) {
        console.error(error);
        showIncidentMessage(error.message || "Submit report failed.", "error");
    } finally {
        setButtonLoading(submitIncidentButton, false, "Submit Report");
    }
}

function buildIncidentRequest() {
    const titleInput = document.getElementById("incidentTitle");
    const descriptionInput = document.getElementById("incidentDescription");
    const typeSelect = document.getElementById("incidentType");
    const prioritySelect = document.getElementById("incidentPriority");
    const plateNumberInput = document.getElementById("incidentPlateNumber");

    const title = titleInput?.value.trim() || "";
    const description = descriptionInput?.value.trim() || "";
    const type = typeSelect?.value.trim() || "";
    const priority = prioritySelect?.value.trim() || "";
    const plateNumber = plateNumberInput?.value.trim() || "";

    if (!title) {
        titleInput?.focus();
        throw new Error("Title is required.");
    }

    if (!type) {
        typeSelect?.focus();
        throw new Error("Type is required.");
    }

    if (!priority) {
        prioritySelect?.focus();
        throw new Error("Priority is required.");
    }

    if (!description) {
        descriptionInput?.focus();
        throw new Error("Description is required.");
    }

    const requestBody = {
        title,
        description,
        type,
        priority
    };

    if (plateNumber) {
        requestBody.plateNumber = plateNumber;
    }

    return requestBody;
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

function resetIncidentForm() {
    const incidentForm = document.getElementById("incidentForm");

    if (incidentForm) {
        incidentForm.reset();
    }
}

function showIncidentMessage(message, type = "info") {
    const incidentMessage = document.getElementById("incidentMessage");

    if (!incidentMessage) {
        return;
    }

    incidentMessage.textContent = message;
    incidentMessage.className = `page-message ${type}`;
}

function clearIncidentMessage() {
    const incidentMessage = document.getElementById("incidentMessage");

    if (!incidentMessage) {
        return;
    }

    incidentMessage.textContent = "";
    incidentMessage.className = "page-message hidden";
}

function renderMyInvoices(invoices) {
    const myInvoiceTableBody = document.getElementById("myInvoiceTableBody");

    if (!myInvoiceTableBody) {
        return;
    }

    myInvoiceTableBody.innerHTML = "";

    if (!invoices || invoices.length === 0) {
        myInvoiceTableBody.innerHTML = `
            <tr>
                <td colspan="10" class="empty-cell">No invoices found.</td>
            </tr>
        `;
        return;
    }

    invoices.forEach(function (invoice) {
        const row = document.createElement("tr");

        row.appendChild(createInvoiceSelectCell(invoice));
        row.appendChild(createTextCell(invoice.id));
        row.appendChild(createTextCell(invoice.parkingSessionId ?? "-"));
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

        myInvoiceTableBody.appendChild(row);
    });
}

function renderInvoiceSummary(invoices) {
    const unpaidInvoiceTotal = document.getElementById("unpaidInvoiceTotal");
    const unpaidInvoiceCount = document.getElementById("unpaidInvoiceCount");

    const unpaidInvoices = Array.isArray(invoices)
        ? invoices.filter(function (invoice) {
            return String(invoice.status || "").toUpperCase() === "UNPAID";
        })
        : [];

    const totalAmount = unpaidInvoices.reduce(function (sum, invoice) {
        const amount = Number(invoice.amount || 0);
        return sum + amount;
    }, 0);

    if (unpaidInvoiceTotal) {
        unpaidInvoiceTotal.textContent = formatMoney(totalAmount);
    }

    if (unpaidInvoiceCount) {
        unpaidInvoiceCount.textContent = unpaidInvoices.length;
    }
}

function renderInvoiceStatusBadge(status) {
    const badge = document.createElement("span");
    const normalizedStatus = String(status || "UNKNOWN").toUpperCase();

    let statusClass = "unknown";

    if (normalizedStatus === "UNPAID") {
        statusClass = "unpaid";
    }

    if (normalizedStatus === "PAID") {
        statusClass = "paid";
    }

    if (normalizedStatus === "CANCELLED") {
        statusClass = "cancelled";
    }

    badge.className = `status-badge invoice-status-${statusClass}`;
    badge.textContent = normalizedStatus;

    return badge;
}

function createInvoiceSelectCell(invoice) {
    const cell = document.createElement("td");
    const status = String(invoice.status || "").toUpperCase();

    if (status !== "UNPAID") {
        cell.textContent = "-";
        return cell;
    }

    const checkbox = document.createElement("input");
    checkbox.type = "checkbox";
    checkbox.className = "invoice-select-checkbox";
    checkbox.dataset.invoiceId = invoice.id;
    checkbox.dataset.amount = invoice.amount ?? 0;

    cell.appendChild(checkbox);
    return cell;
}

function renderInvoiceTypeBadge(type) {
    const badge = document.createElement("span");
    const normalizedType = String(type || "UNKNOWN").toUpperCase();

    let typeClass = "unknown";

    if (normalizedType === "REGISTERED_USER") {
        typeClass = "registered-user";
    }

    if (normalizedType === "GUEST") {
        typeClass = "guest";
    }

    badge.className = `type-badge invoice-type-${typeClass}`;
    badge.textContent = normalizedType;

    return badge;
}

function showMyInvoiceMessage(message, type = "info") {
    const myInvoiceMessage = document.getElementById("myInvoiceMessage");

    if (!myInvoiceMessage) {
        return;
    }

    myInvoiceMessage.textContent = message;
    myInvoiceMessage.className = `page-message ${type}`;
}

function clearMyInvoiceMessage() {
    const myInvoiceMessage = document.getElementById("myInvoiceMessage");

    if (!myInvoiceMessage) {
        return;
    }

    myInvoiceMessage.textContent = "";
    myInvoiceMessage.className = "page-message hidden";
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

function renderVehicles(vehicles) {
    const vehiclesTableBody = document.getElementById("vehiclesTableBody");

    if (!vehiclesTableBody) {
        return;
    }

    vehiclesTableBody.innerHTML = "";

    if (!vehicles || vehicles.length === 0) {
        vehiclesTableBody.innerHTML = `
            <tr>
                <td colspan="7" class="empty-cell">No vehicles found.</td>
            </tr>
        `;
        return;
    }

    vehicles.forEach((vehicle) => {
        const row = document.createElement("tr");

        row.appendChild(createTextCell(vehicle.id));
        row.appendChild(createTextCell(vehicle.plateNumber));
        row.appendChild(createTextCell(vehicle.type));
        row.appendChild(createTextCell(vehicle.brand || "-"));
        row.appendChild(createTextCell(vehicle.color || "-"));

        const statusCell = document.createElement("td");
        statusCell.appendChild(renderStatusBadge(vehicle.status));
        row.appendChild(statusCell);

        row.appendChild(createTextCell(vehicle.description || "-"));

        vehiclesTableBody.appendChild(row);
    });
}

function renderStatusBadge(status) {
    const badge = document.createElement("span");
    const normalizedStatus = String(status || "UNKNOWN").toUpperCase();

    const allowedStatuses = ["PENDING", "APPROVED", "REJECTED", "BLOCKED"];
    const statusClass = allowedStatuses.includes(normalizedStatus)
        ? normalizedStatus.toLowerCase()
        : "unknown";

    badge.className = `status-badge status-${statusClass}`;
    badge.textContent = normalizedStatus;

    return badge;
}

function createTextCell(value) {
    const cell = document.createElement("td");
    cell.textContent = value ?? "-";
    return cell;
}

function showMessage(message, type = "info") {
    const pageMessage = document.getElementById("pageMessage");

    if (!pageMessage) {
        return;
    }

    pageMessage.textContent = message;
    pageMessage.className = `page-message ${type}`;
}

function clearMessage() {
    const pageMessage = document.getElementById("pageMessage");

    if (!pageMessage) {
        return;
    }

    pageMessage.textContent = "";
    pageMessage.className = "page-message hidden";
}

function resetForm() {
    const registerVehicleForm = document.getElementById("registerVehicleForm");

    if (registerVehicleForm) {
        registerVehicleForm.reset();
    }
}

function setText(elementId, value) {
    const element = document.getElementById(elementId);

    if (element) {
        element.textContent = value;
    }
}
function handleInvoiceSelectionChange() {
    const selectedTotalElement = document.getElementById("selectedPaymentTotal");
    const selectedTotal = calculateSelectedPaymentTotal();

    if (selectedTotalElement) {
        selectedTotalElement.textContent = formatMoney(selectedTotal);
    }
}

function getSelectedInvoiceIds() {
    const checkboxes = document.querySelectorAll(".invoice-select-checkbox:checked");

    return Array.from(checkboxes)
        .map(function (checkbox) {
            return Number(checkbox.dataset.invoiceId);
        })
        .filter(function (id) {
            return !Number.isNaN(id);
        });
}

function calculateSelectedPaymentTotal() {
    const checkboxes = document.querySelectorAll(".invoice-select-checkbox:checked");

    return Array.from(checkboxes).reduce(function (sum, checkbox) {
        const amount = Number(checkbox.dataset.amount || 0);
        return sum + amount;
    }, 0);
}

async function handlePaySelectedInvoices() {
    clearPaymentMessage();

    const selectedInvoiceIds = getSelectedInvoiceIds();
    const payButton = document.getElementById("paySelectedInvoicesButton");

    if (selectedInvoiceIds.length === 0) {
        showPaymentMessage("Please select at least one unpaid invoice.", "error");
        return;
    }

    const confirmed = window.confirm("Pay selected invoices?");

    if (!confirmed) {
        return;
    }

    setButtonLoading(payButton, true, "Paying...");

    try {
        const response = await apiFetch(USER_PAYMENT_ENDPOINTS.payMyInvoices, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                invoiceIds: selectedInvoiceIds,
                method: "MOCK"
            })
        });

        if (!response || response.success !== true) {
            throw new Error(response?.message || "Payment failed.");
        }

        await loadMyInvoices({
            keepMessage: true
        });

        handleInvoiceSelectionChange();

        showPaymentMessage(response.message || "Payment successfully.", "success");
    } catch (error) {
        console.error(error);
        showPaymentMessage(error.message || "Payment failed.", "error");
    } finally {
        setButtonLoading(payButton, false, "Pay selected invoices");
    }
}

function showPaymentMessage(message, type = "info") {
    const paymentMessage = document.getElementById("paymentMessage");

    if (!paymentMessage) {
        return;
    }

    paymentMessage.textContent = message;
    paymentMessage.className = `page-message ${type}`;
}

function clearPaymentMessage() {
    const paymentMessage = document.getElementById("paymentMessage");

    if (!paymentMessage) {
        return;
    }

    paymentMessage.textContent = "";
    paymentMessage.className = "page-message hidden";
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