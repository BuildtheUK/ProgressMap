import {initiatePasswordBoxButtons,isPasswordValid} from "./PasswordUtils.js";

const popup = document.getElementById("confirmPasswordChange")
popup.style.display = "none"
const confirmChange = document.getElementById("btnConfirmPasswordChange")
confirmChange.addEventListener("click", () => {
    window.location.assign("Profile.html")
    }
)
initiatePasswordBoxButtons();
const form = document.getElementById('form')
const passwordInput = document.getElementById('passwordInput')
const repeatPasswordInput = document.getElementById('repeatPasswordInput')
const errorMessage = document.getElementById('errorMessage')
const previousPasswordInput = document.getElementById("oldPasswordInput")

form.addEventListener("submit", (e) => {
    e.preventDefault()
    if (previousPasswordInput.value === ""){
        errorMessage.innerText = "Enter your old password"
        previousPasswordInput.parentElement.classList.add('incorrect')
        return
    }


    let errors = isPasswordValid(passwordInput.value)
    if (passwordInput.value !== repeatPasswordInput.value)
    {
        errors.push('Passwords must match')

    }
    if (errors.length > 0){
        errorMessage.innerText = errors.join(". ")
        passwordInput.parentElement.classList.add('incorrect')
        repeatPasswordInput.parentElement.classList.add('incorrect')
    }
    else{

        fetch("/user/changePassword", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                newPassword: passwordInput.value,
                previousPassword: previousPasswordInput.value
            })
        }).then(res => {
            if (!res.ok){
                throw new Error("Unable to reset password")
            }
            popup.style.display = "flex"
        }).catch(err => {console.error(err); errorMessage.innerText = "Unable to reset password"});


    }
})

const allInputs = [ passwordInput, repeatPasswordInput,previousPasswordInput]
allInputs.forEach(input => {
    input.addEventListener('input', ()=> {
        if(input.parentElement.classList.contains('incorrect')){
            input.parentElement.classList.remove('incorrect')
            errorMessage.innerText= ''
        }
    })
})