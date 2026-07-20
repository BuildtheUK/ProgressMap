import {isPasswordValid,initiatePasswordBoxButtons} from "./PasswordUtils.js";

const resetPasswordState = {
    username: "",
    otc: "",
    newPassword: ""
};
const usernameInput = document.getElementById("usernameInput")
const usernameForm = document.getElementById("enterUsernameForm")
const otcInput = document.getElementById("codeInput")
const otcForm = document.getElementById("enterCodeForm")
const passwordInput = document.getElementById("passwordInput")
const repeatPasswordInput = document.getElementById("repeatPasswordInput")
const passwordForm = document.getElementById("enterNewPasswordForm")
const usernameBottomBar = document.getElementById("enterUsernameBottomBar")
const otcBottomBar = document.getElementById("enterCodeBottomBar")
const errorMessage = document.getElementById("errorMessage")
const successMessage = document.getElementById("successMessage")
const requestNewOtc = document.getElementById("btnResendCode")

initiatePasswordBoxButtons()

otcForm.style.display = "none"
passwordForm.style.display = "none"
otcBottomBar.style.display = "none"

usernameForm.addEventListener("submit" , (e) => {
    e.preventDefault()
    let errors = []
    fetch("/auth/newOTC", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: usernameInput.value,
            purpose: "RESETPASSWORD"
        })
    }).then(res => {
        if (!res.ok){
            throw new Error("Unable to send OTC")
        }
        resetPasswordState.username = usernameInput.value
        usernameForm.style.display = "none"
        usernameBottomBar.style.display = "none"
        otcBottomBar.style.display = "flex"
        otcForm.style.display = "flex"
    }).catch(err => {console.error(err); errorMessage.innerText = "Unable to send OTC"});
} )

otcForm.addEventListener("submit" , (e) => {
    e.preventDefault()
    let errors = []
    if (otcInput.value < 100000 || otcInput.value >= 1000000){
        errorMessage.innerText = "Non-valid one time code value"
        return
    }
    resetPasswordState.otc = otcInput.value
    otcBottomBar.style.display = "none"
    otcForm.style.display = "none"
    passwordForm.style.display = "flex"
    usernameBottomBar.display = "flex"
} )

passwordForm.addEventListener("submit", (e) => {
    e.preventDefault()

    let errors = []

    if(repeatPasswordInput.value !== null){
        let passwordErrors = isPasswordValid(passwordInput.value)
        if(passwordErrors.length > 0){
            errors.push(...passwordErrors)
            passwordInput.parentElement.classList.add('incorrect')
        }
        if(passwordInput.value !== repeatPasswordInput.value){
            errors.push('Passwords must match')
            passwordInput.parentElement.classList.add('incorrect')
            repeatPasswordInput.parentElement.classList.add('incorrect')
        }
        if (errors.length > 0){
            errorMessage.innerText = errors.join(". ")
        }
        else{

            fetch("/auth/reset-password", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: resetPasswordState.username,
                    newPassword: passwordInput.value,
                    otc: resetPasswordState.otc
                })
            }).then(res => {
                if (!res.ok){
                    throw new Error("Unable to reset password")
                }
                //display confirmation popup
                window.location.replace("login.html")
            }).catch(err => {console.error(err); errorMessage.innerText = "Unable to reset password"});


        }

    return errors;
}}
)

requestNewOtc.addEventListener("click", () => {
    fetch("/auth/newOTC", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
            username: resetPasswordState.username,
            purpose: "RESETPASSWORD"
        }),
        credentials: "include"
    })
        .then(res => {
            if (!res.ok) {
                throw new Error("Unable to get new OTC");
            }
            return res.text();
        })
        .then(() => {
            errorMessage.innerText = '';
            successMessage.innerText = "New Code Sent";
        })
        .catch(err => {
            console.error(err);
            successMessage.innerText = '';
            errorMessage.innerText = "Unable to get new OTC. Try again later";
        });
})



