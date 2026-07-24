const map = L.map('map').setView([51.505, -0.09], 10);

const tiles = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

const loginButton = document.getElementById("btnLogin")
const regionInfoBox = document.getElementById("RegionInfoBox")
const buildingInfoBox = document.getElementById("BuildingInfoBox")
const noItemSelectedInfoBox = document.getElementById("NoItemSelected")
const profileIcon = document.getElementById("MCSkinLogo")
var loggedIn = false;
var username = ""
var buildingSelected = false;


const gridMarkerGroup = L.layerGroup().addTo(map);
const buildingMarkerGroup = L.layerGroup().addTo(map);
var buildings = []

const stepIcon = L.icon({
    iconUrl: 'images/BlueCircle.png',
    iconSize: [40, 40],
    iconAnchor: [20, 20] // Sets the anchor to the center (half of 40x40)
});

const redBuilding = L.icon({
    iconUrl: 'images/redBuilding.png',

    iconSize:     [30, 30], // size of the icon
    iconAnchor:   [15, 15], // point of the icon which will correspond to marker's location
    popupAnchor:  [15, 30]
});

const greenBuilding = L.icon({
    iconUrl: 'images/greenBuilding.png',

    iconSize:     [30, 30], // size of the icon
    iconAnchor:   [15, 15], // point of the icon which will correspond to marker's location
    popupAnchor:  [15, 30]
});

const orangeBuilding = L.icon({
    iconUrl: 'images/orangeBuilding.png',

    iconSize:     [30, 30], // size of the icon
    iconAnchor:   [15, 15], // point of the icon which will correspond to marker's location
    popupAnchor:  [15, 30]
});

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



    // Initial load when map opens

    updateLayers()
    loadServerStats()

    // var marker = L.marker([55, 0], {buildingId: 1}).addTo(map);
    // marker.on('click',(e) => displayBuildingBox(e.target.options.buildingId))
    // map.on('click', displayWelcomeBox)
});

// Single event listener for pan and zoom ('moveend' handles both)
map.on("moveend", () => {

    updateLayers()
});

map.on("click",() => {if (buildingSelected){displayWelcomeBox(); buildingSelected = false}})

function updateLayers(){
    if (map.getZoom() >= 17){
        gridMarkerGroup.clearLayers()
        updateBuildingsInView()
    }else
    {
        buildingMarkerGroup.clearLayers()
        updateMarkerGroupCounts();
    }
}



async function updateBuildingsInView(){
    const bounds = map.getBounds();

    const minLat = bounds.getSouth();
    const maxLat = bounds.getNorth();
    const minLon = bounds.getWest();
    const maxLon = bounds.getEast();

    const requestData = {
        minLat: minLat,
        minLon: minLon,
        maxLat: maxLat,
        maxLon: maxLon,
    };
    try {
        const response = await fetch("/building/allBuildings", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) throw new Error("Failed to fetch grid counts");

        const data = await response.json();

        buildings = data;
        buildingMarkerGroup.clearLayers();

        buildings.forEach(building => {
            // Create a standard or custom marker at building coordinates
            var buildingIcon
            if (building.colour === "green"){
                buildingIcon = greenBuilding;
            }
            else if (building.colour === "orange"){
                buildingIcon = orangeBuilding
            }
            else
            {
                buildingIcon = redBuilding;
            }
            const marker = L.marker([building.lat, building.lon], { icon: buildingIcon });

            // Store buildingId in marker options for reference
            marker.options.buildingId = building.buildingId;

            // 3. Attach click event listener to show the building ID
            marker.on('click', (e) => {
                onBuildingClick(building);
            });

            // Add marker to layer group
            buildingMarkerGroup.addLayer(marker);
        });


    } catch (error) {
        console.error("Error fetching buildings", error);
    }


    }

function onBuildingClick(buildingData) {
    buildingSelected = true
    displayBuildingBox(buildingData)
}

async function updateMarkerGroupCounts() {
    const bounds = map.getBounds();
    const zoom = map.getZoom();

    const minLat = bounds.getSouth();
    const maxLat = bounds.getNorth();
    const minLon = bounds.getWest();
    const maxLon = bounds.getEast();

    // Calculate fixed world grid steps based on current zoom level
    const { latStep, lonStep } = getGridStepSizes(zoom, map.getCenter().lat);

    const requestData = {
        minLat: minLat,
        minLon: minLon,
        maxLat: maxLat,
        maxLon: maxLon,
        stepLat: latStep,
        stepLon: lonStep
    };

    try {
        const response = await fetch("/building/gridCount", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) throw new Error("Failed to fetch grid counts");

        const data = await response.json();

        // 1. Clear previous markers from the map
        gridMarkerGroup.clearLayers();

        // 2. Render new markers
        data.forEach(item => {
            if (item.count > 0) {
                const markerMessage = getGroupIconMessage(item.count);
                const marker = L.marker([item.lat, item.lon], { icon: stepIcon });

                addGroupMarkerToMap(marker, markerMessage);
            }
        });

    } catch (error) {
        console.error("Error loading grid counts:", error);
    }
}

function getGridStepSizes(zoom, centerLat) {
    // Base step size at zoom level 10 (approx ~0.05 degrees)
    const baseStep = 0.05;

    // Halve the step size for every zoom level in
    const latStep = baseStep / Math.pow(2, zoom - 10);

    // Adjust longitude step to maintain visually square cells at current latitude
    const lonStep = latStep / Math.cos(centerLat * Math.PI / 180);

    return { latStep, lonStep };
}

function addGroupMarkerToMap(marker, markerMessage) {
    marker.bindTooltip(
        `<span style="font-size: ${getGroupIconFontSize(markerMessage)};">${markerMessage}</span>`,
        {
            permanent: true,
            direction: 'center',
            className: "my-labels"
        }
    );
    // Add to our managed layer group instead of directly to map
    gridMarkerGroup.addLayer(marker);
}

function getGroupIconMessage(number) {
    if (number < 1000) return number.toString();
    return ((Math.round((number / 1000) * 10) / 10).toString() + "K");
}

function getGroupIconFontSize(message) {
    const length = message.length;

    if (length >= 4) return '10px'; // For numbers like 1000+ or 1.5K
    if (length === 3) return '15px'; // For numbers like 200
    if (length === 2) return '18px'; // For numbers like 50
    return '20px';                   // For single digits like 1
}


function loggedInDisplay(username){
    document.getElementById("MCSkinLogo").src = "https://mc-heads.net/avatar/" + username
    noItemSelectedInfoBox.style.display = "flex";
    displayWelcomeBox()
    loginButton.innerText = "Logout"
    loginButton.onclick = ( () => {
        fetch("/auth/logout", {method: "POST", credentials: "include"}).then(() => window.location.reload()).catch(err => console.log(err))
    })
    profileIcon.style.cursor = "pointer";
    profileIcon.onclick = (() => {window.location.assign("Profile.html")})

}

function loggedOutDisplay(){
    document.getElementById("MCSkinLogo").src = "images/blankProfile.svg"
    noItemSelectedInfoBox.style.display = "flex";
    displayWelcomeBox()
    loginButton.innerText = "Login"
    loginButton.onclick = ( () => {
        window.location.assign("login.html")
    })
    profileIcon.style.cursor = "default";
    profileIcon.onclick = null
}

const serverStatsBox = document.getElementById("ServerStatsBox");
const closeBuildingBtn = document.getElementById("btnCloseBuilding");
const closeRegionBtn = document.getElementById("closeRegionBtn");
const sidebarDivider = document.getElementById("sidebarDivider");

closeBuildingBtn.addEventListener("click", () => {displayWelcomeBox(); buildingSelected = false});
closeRegionBtn.addEventListener("click", displayWelcomeBox);
const welcomeMessage = document.getElementById("welcomeMessage")
const welcomeTitle = document.getElementById("welcomeTitle")

function displayWelcomeBox() {
    buildingInfoBox.style.display = "none";
    regionInfoBox.style.display = "none";
    serverStatsBox.style.display = "flex";
    noItemSelectedInfoBox.style.display = "flex";
    sidebarDivider.style.display = "block";
    if (loggedIn) {
        welcomeTitle.innerHTML = `Hello, ${username}! Welcome back to BTUK Progress Map.`
        welcomeMessage.innerHTML = "Explore our current progress or create and edit your own claims! (eventually)"
    } else {
        welcomeTitle.innerHTML = `Welcome to BTUK Progress Map!`
        welcomeMessage.innerHTML = "Have a look around or create an account to link to your in-game progress."
    }
}

const buildingId = document.getElementById("buildingId")
const buildingBuilder = document.getElementById("buildingBuilder")
const buildingDate = document.getElementById("buildingCreatedDate")
const buildingIsClaimed = document.getElementById("isClaimed")

function displayBuildingBox(building) {
    // Hide default views and tabs
    noItemSelectedInfoBox.style.display = "none";
    serverStatsBox.style.display = "none";
    regionInfoBox.style.display = "none";
    sidebarDivider.style.display = "none";

    buildingInfoBox.style.display = "flex";
    buildingId.innerText = building.buildingId
    buildingBuilder.innerText = building.username
    buildingDate.innerText = formatDate(building.timeAdded)
    buildingIsClaimed.innerText = building.playerBuilt
}

function formatDate(dateStr) {
    if (!dateStr) return "Unknown";
    const date = new Date(dateStr);

    // If the input is not a valid date parseable by JS, return original string
    if (isNaN(date.getTime())) return dateStr;

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');
    const year = date.getFullYear();

    return `${day}-${month}-${year}`;
}

const statBuildings = document.getElementById("statBuildings");
const statProgress = document.getElementById("statProgress");

async function loadServerStats() {
    try {
        const response = await fetch("/building/buildingCountTotal", {
            method: "POST"
        });

        if (!response.ok) throw new Error("Failed to fetch server stats");

        const data = await response.json();
        const total = data.count || 0;

        // Display total formatted with commas (e.g., 12,345)
        statBuildings.innerText = total.toLocaleString();

        // Calculate percentage complete based on 30,000,000 target
        const percentage = (total / 30000000) * 100;

        // Show higher precision if percentage is tiny, otherwise 2 decimals
        if (percentage > 0 && percentage < 0.01) {
            statProgress.innerText = percentage.toFixed(4) + "%";
        } else {
            statProgress.innerText = percentage.toFixed(2) + "%";
        }

    } catch (err) {
        console.error("Error loading server statistics:", err);
        statBuildings.innerText = "--";
        statProgress.innerText = "--%";
    }
}