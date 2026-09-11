export function addStat(container, statName, statValue, changeIcon="",colour=""){
    const statCard = document.createElement("div");
    statCard.classList.add("StatCard");

    if (changeIcon === "Decrease") {
        statCard.classList.add("stat-decrease");
    } else if (changeIcon === "Increase") {
        statCard.classList.add("stat-increase");
    }

    const statLabel = document.createElement("span");
    statLabel.classList.add("StatLabel");
    statLabel.textContent = statName;

    const statValueContainer = document.createElement("div");
    statValueContainer.classList.add("StatValueContainer");

    const statValueElement = document.createElement("strong");
    statValueElement.classList.add("StatValue");
    statValueElement.textContent = statValue;

    if (colour != null) {
        statValueElement.style.color = colour;
    }
    statValueContainer.appendChild(statValueElement);

    if (changeIcon === "Decrease") {
        const icon = document.createElement("span");
        icon.classList.add("StatIcon");
        icon.textContent = "▼";
        statValueContainer.appendChild(icon);
    } else if (changeIcon === "Increase") {
        const icon = document.createElement("span");
        icon.classList.add("StatIcon");
        icon.textContent = "▲";
        statValueContainer.appendChild(icon);
    }

    statCard.appendChild(statLabel);
    statCard.appendChild(statValueContainer);

    container.appendChild(statCard);
}