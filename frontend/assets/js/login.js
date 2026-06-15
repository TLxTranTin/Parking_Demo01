// assets/js/login.js

const LOGIN_API_URL = "http://localhost:8080/api/auth/login";

const loginForm = document.getElementById("loginForm");
const usernameInput = document.getElementById("username");
const passwordInput = document.getElementById("password");
const formMessage = document.getElementById("formMessage");
const loginButton = document.getElementById("loginButton");

loginForm.addEventListener("submit", async function (event) {
    event.preventDefault();

    const username = usernameInput.value.trim();
    const password = passwordInput.value.trim();

    formMessage.textContent = "";

    if (username === "") {
        formMessage.textContent = "Vui lòng nhập username.";
        return;
    }

    if (password === "") {
        formMessage.textContent = "Vui lòng nhập password.";
        return;
    }

    try {
        loginButton.disabled = true;
        loginButton.textContent = "Đang đăng nhập...";

        const response = await fetch(LOGIN_API_URL, {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: username,
                password: password
            })
        });

        const result = await response.json();

        if (!response.ok || result.success === false) {
            throw new Error(result.message || "Đăng nhập thất bại.");
        }

        const authData = result.data;

        saveAuthData(authData);

        if (authData.role === "ADMIN") {
            window.location.href = "admin-dashboard.html";
        } else if (authData.role === "STAFF") {
            window.location.href = "staff-dashboard.html";
        } else if (authData.role === "USER") {
            window.location.href = "user-dashboard.html";
        } else {
            formMessage.textContent = "Role không hợp lệ.";
        }

    } catch (error) {
        formMessage.textContent = error.message;
    } finally {
        loginButton.disabled = false;
        loginButton.textContent = "Đăng nhập";
    }
});