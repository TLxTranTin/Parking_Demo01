async function apiFetch(endpoint, options = {}) {
    const url = endpoint.startsWith("http")
        ? endpoint
        : `${API_BASE_URL}${endpoint}`;

    const headers = new Headers(options.headers || {});

    if (options.body && !(options.body instanceof FormData) && !headers.has("Content-Type")) {
        headers.set("Content-Type", "application/json");
    }

    const authHeader = getAuthHeader();

    if (authHeader) {
        headers.set("Authorization", authHeader);
    }

    let response;

    try {
        response = await fetch(url, {
            ...options,
            headers
        });
    } catch (error) {
        throw new Error("Không thể kết nối backend. Kiểm tra backend đã chạy chưa hoặc CORS.");
    }

    if (response.status === 401) {
        clearAuthData();
        window.location.href = "login.html";
        throw new Error("Phiên đăng nhập hết hạn. Vui lòng đăng nhập lại.");
    }

    let payload = null;
    const responseText = await response.text();

    if (responseText) {
        try {
            payload = JSON.parse(responseText);
        } catch (error) {
            throw new Error("Backend trả về dữ liệu không phải JSON.");
        }
    }

    if (!response.ok) {
        throw new Error(payload?.message || `Request thất bại với HTTP ${response.status}.`);
    }

    if (payload && payload.success === false) {
        throw new Error(payload.message || "Yêu cầu thất bại.");
    }

    return payload;
}