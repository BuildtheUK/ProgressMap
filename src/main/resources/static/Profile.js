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

const buildingCount = document.getElementById("buildingsBuilt")
const nameBox = document.getElementById("profileName")

function updateProfilePhoto(username){
    document.getElementById("ProfilePhoto").src = "https://mc-heads.net/avatar/" + username
}


window.addEventListener("load", () => {
    fetch("/user/me", {
        method: "GET",
        credentials: "include"
    })
        .then(res => {
            if (!res.ok) throw new Error("Not logged in");
            return res.json();
        })
        .then(data => {
                nameBox.innerText = data.username;
                updateProfilePhoto(data.username);
        })
        .catch(err => {
            console.error("Failed to load user profile:", err);
            window.location.assign("login.html");
        });

    fetch("/building/playerCount", {
        method: "GET",
        credentials: "include"
    })
        .then(res => {
            if (!res.ok) throw new Error("Failed to fetch personal stats");
            return res.json();
        })
        .then(data => {
            if (data && data.count !== undefined) {
                buildingCount.innerText = data.count;
            }
        })
        .catch(err => {
            console.error("Error loading personal building count:", err);
            buildingCount.innerText = "0";
        });
});