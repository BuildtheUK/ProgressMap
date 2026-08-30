import {initiatePasswordBoxButtons} from "./PasswordUtils.js";
import {addStat} from "./StatsUtils.js";

const btnDelete = document.getElementById("btnDeleteAccount")
const btnConfirmDelete = document.getElementById("btnConfirmDeleteAccount")
const btnReturn = document.getElementById("btnSafety")
const popup = document.getElementById("DeleteAccountPopup")
const passwordInput = document.getElementById("passwordInput")
const errorMessage = document.getElementById("errorMessage")

const standardColourGradient = ["#07b804","#5bb804","#b2b804","#b88e04","#b85e04","#b80404"]

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

    fetch("/stats/profile", {
        method: "GET",
        credentials: "include"
    })
        .then(res => {
            if (!res.ok) throw new Error("Failed to fetch personal stats");
            return res.json();
        })
        .then(data => {
            if (data) {
                updateStatsBox(data)
            }
        })
        .catch(err => {
            console.error("Error loading personal building count:", err);
            buildingCount.innerText = "0";
        });
});

function updateStatsBox(data){
    let statsBoxName = "ProfileStatsBox"
        addStat(statsBoxName,"Buildings",data.buildings.toString(),"","")
        addStat(statsBoxName, "Tplls", data.tplls.toString(),"","")
    addStat(statsBoxName,"Time Non-AFK", data.timeNonAFK.toFixed(2) + " days","","")
    let colour = getProductivityColour(data.productivity)
    addStat(statsBoxName,"Productivity grade", data.productivity,"", colour)
    if (data.reviewsCompleted > 0){
        addStat(statsBoxName, "Reviews", data.reviewsCompleted.toString(),"","")
    }
}

function getProductivityColour(productivity){
    switch (productivity){
        case "A":
            return standardColourGradient[0]
        case "B":
            return standardColourGradient[1]
        case "C":
            return standardColourGradient[2]
        case "D":
            return standardColourGradient[3]
        case "E":
            return standardColourGradient[4]
        case "F":
            return standardColourGradient[5]
        default:
            return "#000000"
    }
}