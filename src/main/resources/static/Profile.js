import {initiatePasswordBoxButtons} from "./PasswordUtils.js";

const btnDelete = document.getElementById("btnDeleteAccount")
const btnConfirmDelete = document.getElementById("btnConfirmDeleteAccount")
const btnReturn = document.getElementById("btnSafety")
const popup = document.getElementById("DeleteAccountPopup")
const passwordInput = document.getElementById("passwordInput")
const errorMessage = document.getElementById("errorMessage")

initiatePasswordBoxButtons()

btnDelete.addEventListener("click", () => {
    popup.classList.add("open")
})
btnReturn.addEventListener("click", () => {
    popup.classList.remove("open")
})

btnConfirmDelete.addEventListener("click", () => {
    fetch("/user/delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
        password: passwordInput.value
    }),
        credentials: "include"
    })
        .then(res => {
            if (!res.ok) {
                throw new Error("Unable to delete account");
            }
            return res.text();
        })
        .then(() => {
            window.location.replace("index.html");
        })
        .catch(err => {
            console.error(err);
            passwordInput.parentElement.classList.add('incorrect')

            errorMessage.innerText = "Incorrect password";
        });
})

passwordInput.addEventListener('input', ()=> {
    if(passwordInput.parentElement.classList.contains('incorrect')){
        passwordInput.parentElement.classList.remove('incorrect')
        errorMessage.innerText= ''
    }
})

const btnChangePassword = document.getElementById("btnChangePassword")
btnChangePassword.addEventListener("click", () => {
    window.location.assign("ChangePassword.html")
})