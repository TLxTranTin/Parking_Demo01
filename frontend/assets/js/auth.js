const AUTH_STORAGE_KEYS = {
    accessToken: "accessToken",
    tokenType: "tokenType",
    userId: "userId",
    username: "username",
    role: "role"
};

function saveAuthData(authData) {
    if (!authData) {
        throw new Error("Login response không có data.");
    }

    if (!authData.accessToken || !authData.tokenType || !authData.role) {
        throw new Error("Login response thiếu accessToken, tokenType hoặc role.");
    }

    localStorage.setItem(AUTH_STORAGE_KEYS.accessToken, authData.accessToken);
    localStorage.setItem(AUTH_STORAGE_KEYS.tokenType, authData.tokenType);
    localStorage.setItem(AUTH_STORAGE_KEYS.userId, String(authData.id));
    localStorage.setItem(AUTH_STORAGE_KEYS.username, authData.username);
    localStorage.setItem(AUTH_STORAGE_KEYS.role, authData.role);
}

function getCurrentUser() {
    return {
        accessToken: localStorage.getItem(AUTH_STORAGE_KEYS.accessToken),
        tokenType: localStorage.getItem(AUTH_STORAGE_KEYS.tokenType),
        userId: localStorage.getItem(AUTH_STORAGE_KEYS.userId),
        username: localStorage.getItem(AUTH_STORAGE_KEYS.username),
        role: localStorage.getItem(AUTH_STORAGE_KEYS.role)
    };
}

function isLoggedIn() {
    const user = getCurrentUser();
    return Boolean(user.accessToken && user.tokenType && user.role);
}

function getAuthHeader() {
    const user = getCurrentUser();

    if (!user.accessToken || !user.tokenType) {
        return null;
    }

    return `${user.tokenType} ${user.accessToken}`;
}

function clearAuthData() {
    Object.values(AUTH_STORAGE_KEYS).forEach(function (key) {
        localStorage.removeItem(key);
    });
}

function logout() {
    clearAuthData();
    window.location.href = "login.html";
}

function redirectByRole(role) {
    if (role === "ADMIN") {
        window.location.href = "admin-dashboard.html";
        return;
    }

    if (role === "STAFF") {
        window.location.href = "staff-dashboard.html";
        return;
    }

    if (role === "USER") {
        window.location.href = "user-dashboard.html";
        return;
    }

    clearAuthData();
    window.location.href = "login.html";
}

function requireLogin() {
    if (!isLoggedIn()) {
        window.location.href = "login.html";
        return null;
    }

    return getCurrentUser();
}

function requireRole(allowedRoles) {
    const user = requireLogin();

    if (!user) {
        return null;
    }

    if (!allowedRoles.includes(user.role)) {
        redirectByRole(user.role);
        return null;
    }

    return user;
}

function bindLogoutButton() {
    const logoutButton = document.getElementById("logoutButton");

    if (logoutButton) {
        logoutButton.addEventListener("click", logout);
    }
}