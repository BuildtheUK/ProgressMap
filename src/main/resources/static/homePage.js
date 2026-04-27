const loginButton = document.getElementById("btnLogin")

window.addEventListener("load", () => {
    fetch("/user/me", {
        method: "GET",
        credentials: "include"
    })
        .then(async (res) => {

            if (!res.ok) {
                loggedOutDisplay();
                return;
            }

            const data = await res.json();


            if (!data.loggedIn) {
                loggedOutDisplay();
                return;
            }

            loggedInDisplay(data.username);


        })
        .catch(err => {
            console.log(err);
            loggedOutDisplay();
        });
});

function loggedInDisplay(username){
    document.getElementById("MCSkinLogo").src = "https://mc-heads.net/avatar/" + username
    loginButton.innerText = "Logout"
    loginButton.onclick = ( () => {
        fetch("/logout", {method: "POST", credentials: "include"}).then(() => window.location.reload()).catch(err => console.log(err))
    })
}

function loggedOutDisplay(){
    document.getElementById("MCSkinLogo").src = "images/blankProfile.svg"
    loginButton.innerText = "Login"
    loginButton.onclick = ( () => {
        window.location.assign("login.html")
    })
}