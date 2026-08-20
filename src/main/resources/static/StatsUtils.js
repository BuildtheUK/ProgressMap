export function addStat(containerID, statName, statValue){
    let container = document.getElementById(containerID)
    container.innerHTML = container.innerHTML + `<div class="StatCard">
                        <span class="StatLabel">${statName}</span>
                        <strong class="StatValue" id="buildingsBuilt">${statValue}</strong>
                    </div>`
}