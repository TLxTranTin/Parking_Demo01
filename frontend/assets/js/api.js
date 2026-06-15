// assets/js/api.js

const API_BASE_URL = "http://localhost:8080";

async function apiFetch(url, options = {}) {
    const accessToken = getAccessToken();

    const headers = {
        "Content-Type": "application/json",
        ...(options.headers || {})
    };

    if (accessToken) {
        headers["Authorization"] = "Bearer " + accessToken;
    }

    const fullUrl = url.startsWith("http")
        ? url
        : API_BASE_URL + url;

    const response = await fetch(fullUrl, {
        ...options,
        headers: headers
    });

    if (response.status === 401) {
        alert("Phiên đăng nhập đã hết hạn hoặc token không hợp lệ.");
        logout();
        return;
    }

    const data = await response.json();

    if (!response.ok) {
        throw new Error(data.message || "Có lỗi xảy ra khi gọi API.");
    }

    return data;
}