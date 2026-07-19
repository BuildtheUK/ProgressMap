const btnDelete = document.getElementById("btnDeleteAccount")
const btnConfirmDelete = document.getElementById("btnConfirmDeleteAccount")
const btnReturn = document.getElementById("btnSafety")
const popup = document.getElementById("DeleteAccountPopup")
const password = document.getElementById("passwordInput")
const showPassword = document.getElementById('showPassword')

const eyeOffSVG = `<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#e3e3e3"><path d="M792-56 624-222q-35 11-70.5 16.5T480-200q-151 0-269-83.5T40-500q21-53 53-98.5t73-81.5L56-792l56-56 736 736-56 56ZM480-320q11 0 20.5-1t20.5-4L305-541q-3 11-4 20.5t-1 20.5q0 75 52.5 127.5T480-320Zm292 18L645-428q7-17 11-34.5t4-37.5q0-75-52.5-127.5T480-680q-20 0-37.5 4T408-664L306-766q41-17 84-25.5t90-8.5q151 0 269 83.5T920-500q-23 59-60.5 109.5T772-302ZM587-486 467-606q28-5 51.5 4.5T559-574q17 18 24.5 41.5T587-486Z"/></svg>`
const eyeOnSVG = `<svg xmlns="http://www.w3.org/2000/svg" height="24px" viewBox="0 -960 960 960" width="24px" fill="#e3e3e3"><path d="M607.5-372.5Q660-425 660-500t-52.5-127.5Q555-680 480-680t-127.5 52.5Q300-575 300-500t52.5 127.5Q405-320 480-320t127.5-52.5Zm-204-51Q372-455 372-500t31.5-76.5Q435-608 480-608t76.5 31.5Q588-545 588-500t-31.5 76.5Q525-392 480-392t-76.5-31.5ZM214-281.5Q94-363 40-500q54-137 174-218.5T480-800q146 0 266 81.5T920-500q-54 137-174 218.5T480-200q-146 0-266-81.5Z"/></svg>`

if(showPassword != null) {
    showPassword.addEventListener("click", () => {
        if (passwordInput.type === "password") {
            passwordInput.type = "text";
            showPassword.innerHTML = eyeOffSVG
        } else {
            passwordInput.type = "password";
            showPassword.innerHTML = eyeOnSVG
        }
    })
}

btnDelete.addEventListener("click", () => {
    popup.classList.add("open")
})
btnReturn.addEventListener("click", () => {
    popup.classList.remove("open")
})

btnConfirmDelete.addEventListener("click", () => {
    fetch("/user/delete", {
        method: "POST",
        headers: {
            "Content-Type": "application/json"
        },
        body: JSON.stringify({
        password: passwordInput.value
    }),
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
            password.parentElement.classList.add('incorrect')

            //errorMessage.innerText = "Invalid password";
        });
})

password.addEventListener('input', ()=> {
    if(password.parentElement.classList.contains('incorrect')){
        password.parentElement.classList.remove('incorrect')
        errorMessage.innerText= ''
    }
})