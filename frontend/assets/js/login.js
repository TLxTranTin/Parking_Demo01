document.addEventListener("DOMContentLoaded", function () {
    if (isLoggedIn()) {
        redirectByRole(getCurrentUser().role);
        return;
    }

    const loginForm = document.getElementById("loginForm");
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");
    const loginButton = document.getElementById("loginButton");
    const loginButtonText = document.getElementById("loginButtonText");
    const loginSpinner = document.getElementById("loginSpinner");
    const loginMessage = document.getElementById("loginMessage");
    const passwordToggle = document.getElementById("passwordToggle");
    const goRegisterButton = document.getElementById("goRegisterButton");
    
    if (goRegisterButton) {
        goRegisterButton.addEventListener("click", function () {
            window.location.href = "register.html";
        });
    }

    passwordToggle.addEventListener("click", function () {
        const isPassword = passwordInput.type === "password";
        passwordInput.type = isPassword ? "text" : "password";
        passwordToggle.textContent = isPassword ? "Ẩn" : "Hiện";
    });

    loginForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();

        if (!username || !password) {
            showLoginMessage("Vui lòng nhập username và password.", "error");
            return;
        }

        setLoginLoading(true);
        showLoginMessage("", "");

        try {
            const response = await apiFetch(API_ENDPOINTS.login, {
                method: "POST",
                body: JSON.stringify({
                    username,
                    password
                })
            });

            saveAuthData(response.data);
            redirectByRole(response.data.role);
        } catch (error) {
            showLoginMessage(error.message, "error");
        } finally {
            setLoginLoading(false);
        }
    });

    function setLoginLoading(isLoading) {
        loginButton.disabled = isLoading;
        loginButtonText.textContent = isLoading ? "Đang đăng nhập..." : "Đăng nhập";
        loginSpinner.classList.toggle("hidden", !isLoading);
    }

    function showLoginMessage(message, type) {
        loginMessage.textContent = message;
        loginMessage.className = "message";

        if (type) {
            loginMessage.classList.add(type);
        }
    }
});