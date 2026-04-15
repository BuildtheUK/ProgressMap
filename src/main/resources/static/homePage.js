window.addEventListener("load", () => {
    fetch("/user/me", {
        credentials: "include"
    })
        .then ( async (res) => {
            if (!res.ok) {
                throw new Error("Not logged in");
            }
            const username = await res.text();
            document.getElementById("MCSkinLogo").src = "https://mc-heads.net/avatar/" + username
        }).then()
        .catch(err => {
            console.log(err);
            document.getElementById("MCSkinLogo").src = "images/blankProfile.svg"})
})