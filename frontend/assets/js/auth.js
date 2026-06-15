// assets/js/auth.js

function saveAuthData(data) {
    localStorage.setItem("accessToken", data.accessToken);
    localStorage.setItem("tokenType", data.tokenType || "Bearer");
    localStorage.setItem("userId", data.id);
    localStorage.setItem("username", data.username);
    localStorage.setItem("role", data.role);
}

function getAccessToken() {
    return localStorage.getItem("accessToken");
}

function getCurrentUser() {
    return {
        id: localStorage.getItem("userId"),
        username: localStorage.getItem("username"),
        role: localStorage.getItem("role"),
        tokenType: localStorage.getItem("tokenType"),
        accessToken: localStorage.getItem("accessToken")
    };
}

function isLoggedIn() {
    const token = getAccessToken();
    return token !== null && token !== "";
}

function logout() {
    localStorage.removeItem("accessToken");
    localStorage.removeItem("tokenType");
    localStorage.removeItem("userId");
    localStorage.removeItem("username");
    localStorage.removeItem("role");

    window.location.href = "login.html";
}

function getDashboardByRole(role) {
    if (role === "ADMIN") {
        return "admin-dashboard.html";
    }

    if (role === "STAFF") {
        return "staff-dashboard.html";
    }

    if (role === "USER") {
        return "user-dashboard.html";
    }

    return "login.html";
}

function redirectByRole(role) {
    window.location.href = getDashboardByRole(role);
}

function requireLogin() {
    if (!isLoggedIn()) {
        window.location.href = "login.html";
    }
}

function requireRole(allowedRoles) {
    requireLogin();

    const currentUser = getCurrentUser();

    if (!allowedRoles.includes(currentUser.role)) {
        alert("Bạn không có quyền truy cập trang này.");
        redirectByRole(currentUser.role);
    }
}