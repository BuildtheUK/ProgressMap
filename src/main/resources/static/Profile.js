const btnDelete = document.getElementById("btnDeleteAccount")
const btnConfirmDelete = document.getElementById("btnConfirmDeleteAccount")
const btnReturn = document.getElementById("btnSafety")
const popup = document.getElementById("DeleteAccountPopup")

btnDelete.addEventListener("click", () => {
    popup.classList.add("open")
})
btnReturn.addEventListener("click", () => {
    popup.classList.remove("open")
})

btnConfirmDelete.addEventListener("click", () => {
    fetch("/auth/delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
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
            //errorMessage.innerText = "Invalid username or password";
        });
})