import {isPasswordValid,initiatePasswordBoxButtons} from "./PasswordUtils.js";

const form = document.getElementById('form')
const usernameInput = document.getElementById('usernameInput')
const passwordInput = document.getElementById('passwordInput')
const repeatPasswordInput = document.getElementById('repeatPasswordInput')
const errorMessage = document.getElementById('errorMessage')
const otcInput = document.getElementById('codeInput')

const resendOTC = document.getElementById('btnResendCode')
const successMessage = document.getElementById('successMessage')

initiatePasswordBoxButtons()


form.addEventListener('submit', (e) => {

    e.preventDefault()
    let errors = []

    if(repeatPasswordInput !== null){
        errors = getSignUpFormErrors(usernameInput.value,passwordInput.value,repeatPasswordInput.value)
        if (errors.length > 0){
            errorMessage.innerText = errors.join(". ")
        }
        else{

            fetch("/auth/register", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: usernameInput.value,
                    password: passwordInput.value
                })
            }).then(res => {
                if (!res.ok){
                    throw new Error("Unable to register")
                }
                sessionStorage.setItem("pendingUsername", usernameInput.value)
                window.location.replace("verification.html")
            }).catch(err => {console.error(err); errorMessage.innerText = "Network error"});


        }
    }
    else if( otcInput !== null){
        fetch("/auth/verify", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: sessionStorage.getItem("pendingUsername"),
                otc: otcInput.value
            })
        }).then (async (res) => {
            if (!res.ok) throw new Error("Verify failed");

            const data = await res.json();

            if (data.success) {
                window.location.replace("/index.html");
            }
        }).catch(err => {console.error(err); errorMessage.innerText = "Network Error"})
    }
    else
    {
        errors = getLoginFormErrors(usernameInput.value,passwordInput.value)
        console.log("Logging in")
        if (errors.length > 0){

            errorMessage.innerText = errors.join(". ")
            console.log(errors)
        }
        else{

            fetch("/auth/login", {
                method: "POST",
                headers: {
                    "Content-Type": "application/json"
                },
                body: JSON.stringify({
                    username: usernameInput.value,
                    password: passwordInput.value
                }),
                credentials: "include"
            })
                .then(res => {
                    if (!res.ok) {
                        throw new Error("Invalid credentials");
                    }
                    return res.text();
                })
                .then(() => {
                    window.location.replace("index.html");
                })
                .catch(err => {
                    console.error(err);
                    errorMessage.innerText = "Invalid username or password";
                });

        }
    }





})

function getLoginFormErrors(username, password){
    let errors = []
    if(username === '' || username == null){
        errors.push('Username required')
        usernameInput.parentElement.classList.add('incorrect')
    }
    if(password === '' || password == null){
        errors.push('Password required')
        passwordInput.parentElement.classList.add('incorrect')
    }
    return errors;
}

function getSignUpFormErrors(username, password, repeatPassword){

    let errors = []

    if(username === '' || username == null){
        errors.push('Username required')
        usernameInput.parentElement.classList.add('incorrect')
    }
    let passwordErrors = isPasswordValid(password)
    if(passwordErrors.length > 0){
        errors.push(...passwordErrors)
        passwordInput.parentElement.classList.add('incorrect')
    }
    if(password !== repeatPassword){
        errors.push('Passwords must match')
        passwordInput.parentElement.classList.add('incorrect')
        repeatPasswordInput.parentElement.classList.add('incorrect')
    }

    return errors;
}

const allInputs = [usernameInput, passwordInput, repeatPasswordInput,otcInput].filter( input => input != null)
allInputs.forEach(input => {
    input.addEventListener('input', ()=> {
        if(input.parentElement.classList.contains('incorrect')){
            input.parentElement.classList.remove('incorrect')
            errorMessage.innerText= ''
            if (successMessage != null) {
                successMessage.innerText = ''
            }
        }
    })
})

if (resendOTC != null) {
    resendOTC.addEventListener('click', () => {
        fetch("/auth/newOTC", {
            method: "POST",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: sessionStorage.getItem("pendingUsername"),
                purpose: "REGISTER"
            }),
            credentials: "include"
        })
            .then(res => {
                if (!res.ok) {
                    throw new Error("Unable to get new OTC");
                }
                return res.text();
            })
            .then(() => {
                errorMessage.innerText = '';
                successMessage.innerText = "New Code Sent";
            })
            .catch(err => {
                console.error(err);
                successMessage.innerText = '';
                errorMessage.innerText = "Unable to get new OTC. Try again later";
            });
    })
}
const btnForgotPassword = document.getElementById("btnForgotPassword")
if (btnForgotPassword != null){
    btnForgotPassword.addEventListener("click", () => {
        window.location.replace("ForgotPassword.html")
    })
}
