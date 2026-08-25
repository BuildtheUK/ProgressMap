export function addStat(containerID, statName, statValue, changeIcon,colour){
    let container = document.getElementById(containerID)
    var changeIconHtml = "";
    var statStatusClass = "";
    if( changeIcon === "Decrease")
    {
        statStatusClass = "stat-decrease"
        changeIconHtml = `<svg class="StatIcon" xmlns="http://www.w3.org/2000/svg"  viewBox="0 -960 960 960"><path d="M480-360 280-560h400L480-360Z"/></svg>`
    }
    else if( changeIcon === "Increase")
    {
        statStatusClass = "stat-increase"
        changeIconHtml = `<svg class="StatIcon" xmlns="http://www.w3.org/2000/svg"  viewBox="0 -960 960 960" ><path d="m280-400 200-200 200 200H280Z"/></svg>`
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