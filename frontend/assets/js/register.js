document.addEventListener("DOMContentLoaded", function () {
    if (isLoggedIn()) {
        redirectByRole(getCurrentUser().role);
        return;
    }

    const allowedRoles = ["USER", "STAFF", "ADMIN"];

    const registerForm = document.getElementById("registerForm");
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");
    const confirmPasswordInput = document.getElementById("confirmPassword");
    const roleSelect = document.getElementById("role");
    const registerButton = document.getElementById("registerButton");
    const registerButtonText = document.getElementById("registerButtonText");
    const registerSpinner = document.getElementById("registerSpinner");
    const registerMessage = document.getElementById("registerMessage");
    const passwordToggle = document.getElementById("passwordToggle");
    const confirmPasswordToggle = document.getElementById("confirmPasswordToggle");

    bindPasswordToggle(passwordToggle, passwordInput);
    bindPasswordToggle(confirmPasswordToggle, confirmPasswordInput);

    registerForm.addEventListener("submit", async function (event) {
        event.preventDefault();

        const username = usernameInput.value.trim();
        const password = passwordInput.value.trim();
        const confirmPassword = confirmPasswordInput.value.trim();
        const role = roleSelect.value;

        const validationMessage = validateRegisterForm(username, password, confirmPassword, role, allowedRoles);

        if (validationMessage) {
            showRegisterMessage(validationMessage, "error");
            return;
        }

        setRegisterLoading(true);
        showRegisterMessage("", "");

        try {
            const response = await apiFetch(API_ENDPOINTS.register, {
                method: "POST",
                body: JSON.stringify({
                    username,
                    password,
                    role
                })
            });

            showRegisterMessage(response.message || "Đăng ký tài khoản thành công.", "success");
            registerForm.reset();

            setTimeout(function () {
                window.location.href = "login.html";
            }, 1000);
        } catch (error) {
            showRegisterMessage(error.message, "error");
        } finally {
            setRegisterLoading(false);
        }
    });

    function bindPasswordToggle(toggleButton, inputElement) {
        if (!toggleButton || !inputElement) {
            return;
        }

        toggleButton.addEventListener("click", function () {
            const isPassword = inputElement.type === "password";
            inputElement.type = isPassword ? "text" : "password";
            toggleButton.textContent = isPassword ? "Ẩn" : "Hiện";
        });
    }

    function validateRegisterForm(username, password, confirmPassword, role, allowedRoles) {
        if (!username) {
            return "Vui lòng nhập username.";
        }

        if (!password) {
            return "Vui lòng nhập password.";
        }

        if (!confirmPassword) {
            return "Vui lòng nhập confirm password.";
        }

        if (password !== confirmPassword) {
            return "Password và confirm password không giống nhau.";
        }

        if (!allowedRoles.includes(role)) {
            return "Role không hợp lệ.";
        }

        return "";
    }

    function setRegisterLoading(isLoading) {
        registerButton.disabled = isLoading;
        registerButtonText.textContent = isLoading ? "Đang đăng ký..." : "Register";
        registerSpinner.classList.toggle("hidden", !isLoading);
    }

    function showRegisterMessage(message, type) {
        registerMessage.textContent = message;
        registerMessage.className = "message login-message";

        if (type) {
            registerMessage.classList.add(type);
        }
    }
});