import {addStat} from "./StatsUtils.js";

//set map centre to cover whole UK
const map = L.map('map').setView([54.06801502799949, -3.8921490108560857], 5);

const tiles = L.tileLayer('https://tile.openstreetmap.org/{z}/{x}/{y}.png', {
    maxZoom: 19,
    attribution: '&copy; <a href="http://www.openstreetmap.org/copyright">OpenStreetMap</a>'
}).addTo(map);

const loginButton = document.getElementById("btnLogin")
const regionInfoBox = document.getElementById("RegionInfoBox")
const buildingInfoBox = document.getElementById("BuildingInfoBox")
const noItemSelectedInfoBox = document.getElementById("NoItemSelected")
const profileIcon = document.getElementById("MCSkinLogo")
const btnCloseWelcome = document.getElementById("btnCloseWelcome")

var loggedIn = false;
var username = ""
var buildingSelected = false;

const redHeatmapColours = ['#fff5f0','#fee0d2','#fcbba1','#fc9272','#fb6a4a','#ef3b2c','#cb181d','#a50f15','#67000d']
var heatmapColours = redHeatmapColours
const gridMarkerGroup = L.layerGroup().addTo(map);
const buildingMarkerGroup = L.layerGroup().addTo(map);
const heatMapGroup = L.layerGroup().addTo(map);
const regionMarkerGroup = L.layerGroup().addTo(map);
const progressAreaMarkerGroup = L.layerGroup().addTo(map);
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

async function updateLayers() {
    clearLayers()
    if (map.getZoom() >= 17) {
        const dto = await getCloseUp();
        if (dto && dto.buildings) {
            updateBuildingsInView(dto.buildings);
        }
        if (dto && dto.polygons){
            updateProgressAreasInView(dto.polygons);
        }
    } else {
        const dto = await getOverview();
        if (dto) {
            if (dto.buildings) {
                updateMarkerGroupCounts(dto.buildings);
            }
            if (dto.heatmap) {
                updateHeatmapView(dto.heatmap);
            }
        }
    }
}
async function getOverview() {
    const bounds = map.getBounds();
    const zoom = map.getZoom();
    const requestData = {
        minLat: bounds.getSouth(),
        minLon: bounds.getWest(),
        maxLat: bounds.getNorth(),
        maxLon: bounds.getEast(),
        zoom: zoom,
        centreLat: map.getCenter().lat
    };

    try {
        const response = await fetch("/map/overview", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) throw new Error("Failed to fetch overview data");
        return await response.json();
    } catch (error) {
        console.error("Error fetching overview:", error);
        return null;
    }
}

async function getCloseUp() {
    const bounds = map.getBounds();

    const requestData = {
        minLat: bounds.getSouth(),
        minLon: bounds.getWest(),
        maxLat: bounds.getNorth(),
        maxLon: bounds.getEast()
    };

    try {
        const response = await fetch("/map/closeUp", {
            method: "POST",
            headers: { "Content-Type": "application/json" },
            body: JSON.stringify(requestData)
        });

        if (!response.ok) throw new Error("Failed to fetch close-up data");
        return await response.json();
    } catch (error) {
        console.error("Error fetching closeUp:", error);
        return null;
    }
}

function updateHeatmapView(heatmapItems) {

    heatmapItems.forEach(item => {
        // Construct bounding rectangle coordinates: [[south, west], [north, east]]
        const bounds = [
            [item.minLat, item.minLon],
            [item.maxLat, item.maxLon]
        ];

        const color = heatmapColours[item.magnitude];

        const rectangle = L.rectangle(bounds, {
            color: color,
            weight: 0,          // No border
            fillColor: color,
            fillOpacity: 0.7    // Semi-transparent overlay
        });

        heatMapGroup.addLayer(rectangle);
    });
}

function updateBuildingsInView(buildings){

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
    }

function updateProgressAreasInView (polygonData){
        const geoJsonLayer = L.geoJSON(polygonData, {
            style: function(feature) {
                return {
                    color: feature.properties?.stroke || '#00FF00',
                    fillColor: feature.properties?.fill || '#00FF00',
                    weight: feature.properties?.['stroke-width'] || 2,
                    opacity: feature.properties?.['stroke-opacity'] ?? 0.5,
                    fillOpacity: feature.properties?.['fill-opacity'] ?? 0.5
                };
            },
            // Optional: Attach events or popups to individual features
            onEachFeature: function(feature, layer) {
                if (feature.properties) {
                    // Example: Open a popup with extra metadata properties on click
                    const name = feature.properties.name || 'Unnamed';
                    const builder = feature.properties.builders || 'Unknown';
                    const date = feature.properties.date || 'Unknown';

                    layer.bindPopup(`
                    <div>
                        <strong>Name:</strong> ${name}<br/>
                        <strong>Builder:</strong> ${builder}<br/>
                        <strong>Date:</strong> ${date}
                    </div>
                `);
                }
            }
        });

        // 2. Add the created GeoJSON layer to your progressAreaMarkerGroup
        progressAreaMarkerGroup.addLayer(geoJsonLayer);
    }

function onBuildingClick(buildingData) {
    buildingSelected = true
    displayBuildingBox(buildingData)
}

function clearLayers(){
    gridMarkerGroup.clearLayers();
    regionMarkerGroup.clearLayers();
    progressAreaMarkerGroup.clearLayers();
    buildingMarkerGroup.clearLayers();
    heatMapGroup.clearLayers();
}

function updateMarkerGroupCounts(buildingGridItems) {

    buildingGridItems.forEach(item => {
        if (item.count > 0) {
            const markerMessage = getGroupIconMessage(item.count);
            const marker = L.marker([item.lat, item.lon], { icon: stepIcon });
            addGroupMarkerToMap(marker, markerMessage);
        }
    });
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

btnCloseWelcome.addEventListener("click", () => {noItemSelectedInfoBox.style.display = "none"; serverStatsBox.style.display ="flex"; welcomeMessageRemoved = true;});

function displayWelcomeBox() {
    buildingInfoBox.style.display = "none";
    regionInfoBox.style.display = "none";

    if (isMobile()) {
        if (!welcomeMessageRemoved) {
            noItemSelectedInfoBox.style.display = "flex";
            serverStatsBox.style.display = "none";
        }
        else{
            noItemSelectedInfoBox.style.display = "none";
            serverStatsBox.style.display = "flex";
        }
        sidebarDivider.style.display = "none";

    }
    else{
        noItemSelectedInfoBox.style.display = "flex";
        sidebarDivider.style.display = "block";
        serverStatsBox.style.display = "flex";
    }

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
let welcomeMessageRemoved = false

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
}

function isMobile() {
    return window.matchMedia("(max-width: 800px)").matches;
}

window.addEventListener("resize", () => {
    // if there is not item info selected update page to have the correct stylings
    if (noItemSelectedInfoBox.style.display === "flex" || serverStatsBox.style.display === "flex") {
        displayWelcomeBox();
    }
});


function formatDate(dateStr) {
    if (!dateStr) return "Unknown";

    // Handle dates beyond JavaScript Date's representable range
    const yearMatch = dateStr.match(/^\+?(\d+)-/);

    if (yearMatch) {
        const year = Number(yearMatch[1]);

        if (year > 999999){
            return `~${((year - 2026) / 1_000_000).toFixed(1)} million years`;
        }
        if (year > 9999) {
            return `~${((year - 2026) / 1_000).toFixed(1)} thousand years`;
        }
    }

    const date = new Date(dateStr);

    if (isNaN(date.getTime())) return dateStr;
    console.log("date is valid")

    const cutoffDate = new Date("2026-03-15T00:00:00");
    if (date < cutoffDate) return "Unknown";

    const year = date.getFullYear();

    const day = String(date.getDate()).padStart(2, '0');
    const month = String(date.getMonth() + 1).padStart(2, '0');

    return `${day}-${month}-${year}`;
}

async function loadServerStats() {
    try {
        const response = await fetch("/stats/homePage", {
            method: "GET",
            credentials: "include"
        });

        if (!response.ok) throw new Error("Failed to fetch server stats");
        const data = await response.json();
        if (data){
            let statBoxName = "ServerStatsBox"
            addStat(statBoxName,"Buildings",data.buildings.toString())
            let percentageText = ""
            if (data.percentage > 0 && data.percentage < 0.01) {
                percentageText = data.percentage.toFixed(4) + "%";
            } else {
                percentageText = data.percentage.toFixed(2) + "%";
            }
            let changeIcon = ""
            if (data.previousRecentBuildings > data.buildingsRecent){
                changeIcon = "Decrease"
            }
            else if (data.previousRecentBuildings < data.buildingsRecent){
                changeIcon = "Increase"
            }
            addStat(statBoxName,"Buildings last month",data.buildingsRecent,changeIcon,"")
            addStat(statBoxName,"Percentage Complete",percentageText,"","")
            addStat(statBoxName,"Estimated Completion", formatDate(data.estimatedFinishDate),"","")

        }

    } catch (err) {
        console.error("Error loading server statistics:", err);
    }
}