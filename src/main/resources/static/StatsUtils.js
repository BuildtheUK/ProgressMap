export function addStat(containerID, statName, statValue, changeIcon,colour){
    let container = document.getElementById(containerID)
    var changeIconHtml = "";
    var statStatusClass = "";
    if( changeIcon === "Decrease")
    {
        statStatusClass = "stat-decrease"
        changeIconHtml = `<span class="StatIcon">▼</span>`
    }
    else if( changeIcon === "Increase")
    {
        statStatusClass = "stat-increase"
        changeIconHtml = `<span class="StatIcon">▲</span>`
    }
    var colourhtml = ""
    if (colour != null){
        colourhtml = `style="color:${colour}"`
    }

    container.innerHTML += `
        <div class="StatCard ${statStatusClass}">
            <span class="StatLabel">${statName}</span>
            <div class="StatValueContainer">
                <strong class="StatValue" ${colourhtml}>${statValue}</strong>
                ${changeIconHtml}
            </div>
        </div>`;
}