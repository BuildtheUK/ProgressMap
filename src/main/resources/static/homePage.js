const map = L.map('map').setView([51.505, -0.09], 13);

const tiles = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

const loginButton = document.getElementById("btnLogin")
const regionInfoBox = document.getElementById("RegionInfoBox")
const buildingInfoBox = document.getElementById("BuildingInfoBox")
const noItemSelectedInfoBox = document.getElementById("NoItemSelected")
const buildingName = document.getElementById("BuildingName")
const buildingBuilder = document.getElementById("BuildingBuilder")
const buildingCreationDate = document.getElementById("BuildingCreationDate")
var loggedIn = false;
var username = ""
window.addEventListener("load", () => {
    fetch("/user/me", {
        method: "GET",
        credentials: "include"
    })
        .then(async (res) => {

            if (!res.ok) {
                loggedIn = false
                loggedOutDisplay();
                return;
            }

            const data = await res.json();


            if (!data.loggedIn) {
                loggedIn = false

                loggedOutDisplay();
                return;
            }
            username = data.username
            loggedIn = true

            loggedInDisplay(data.username);


        })
        .catch(err => {
            console.log(err);
            loggedIn = false

            loggedOutDisplay();
        });
    var marker = L.marker([55, 0], {buildingId: 1}).addTo(map);
    marker.on('click',(e) => displayBuildingBox(e.target.options.id))
    map.on('click', displayWelcomeBox)
});

function displayWelcomeBox(){
    buildingInfoBox.style.display = "none"
    regionInfoBox.style.display = "none"

    if(loggedIn){
        noItemSelectedInfoBox.children.item(0).innerHTML = "Hello, " + username + "! Welcome back to BTUK Progress Map. <br> Explore our current progress or create and edit your own claims!"
    }
    else{
        noItemSelectedInfoBox.children.item(0).innerHTML = "Welcome to BTUK Progress Map! <br> Have a look around or create an account to link to your in-game progress"
    }
    noItemSelectedInfoBox.style.display = "flex"
}


function displayBuildingBox(buildingId){
    const builder = "Poole"
    const dateAdded = "07/06/2026"
    const name = "test Building"
    noItemSelectedInfoBox.style.display = "none"
    buildingInfoBox.style.display = "flex"
    buildingName.value = name
    buildingBuilder.innerText = "Builder: " + builder
    buildingCreationDate.innerText = "Created on: " + dateAdded
}

function loggedInDisplay(username){
    document.getElementById("MCSkinLogo").src = "https://mc-heads.net/avatar/" + username
    noItemSelectedInfoBox.style.display = "flex";
    displayWelcomeBox()
    loginButton.innerText = "Logout"
    loginButton.onclick = ( () => {
        fetch("/logout", {method: "POST", credentials: "include"}).then(() => window.location.reload()).catch(err => console.log(err))
    })
}

function loggedOutDisplay(){
    document.getElementById("MCSkinLogo").src = "images/blankProfile.svg"
    noItemSelectedInfoBox.style.display = "flex";
    displayWelcomeBox()
    loginButton.innerText = "Login"
    loginButton.onclick = ( () => {
        window.location.assign("login.html")
    })
}