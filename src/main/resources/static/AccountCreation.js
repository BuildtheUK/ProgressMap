const form = document.getElementById('form')
const usernameInput = document.getElementById('usernameInput')
const passwordInput = document.getElementById('passwordInput')
const repeatPasswordInput = document.getElementById('repeatPasswordInput')
const errorMessage = document.getElementById('errorMessage')
const otcInput = document.getElementById('codeInput')

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
        fetch("auth/verify", {
            method: "POST",
            credentials: "include",
            headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: sessionStorage.getItem("pendingUsername"),
                otc: otcInput.value
            })
        }).then (res => {
            if (!res.ok){
                throw new Error("Unable to verify")
            }
            window.location.replace("index.html")

        }).catch(err => {console.error(err); errorMessage.innerText = "Network Error"})
    }
    else{
        errors = getLoginFormErrors(usernameInput.value,passwordInput.value)
        if (errors.length > 0){

            errorMessage.innerText = errors.join(". ")
            console.log(errors)
        }
        else{
            fetch("auth/login", {method: "POST",
                credentials: "include",
                headers: {
                "Content-Type": "application/json"
            },
            body: JSON.stringify({
                username: usernameInput.value,
                password: passwordInput.value
            })}).then(
                res => {
                    if (!res.ok){
                        throw new Error("Network Error")
                    }
                    window.location.replace("index.html")
                }
            ).catch(err => {console.log(err); errorMessage.innerText = "Network Error"})

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

function isPasswordValid(password){
    let errors = []
    if (password == null || password.length === 0)
    {
        errors.push('Password is required')
        return errors
    }
    if (password.length < 10)
    {
        errors.push('Password length must be longer than 9')
    }
    if (!/[A-Z]/.test(password)){
        errors.push('Password must contain an Uppercase letter')
    }
    if(!/[a-z]/.test(password)){
        errors.push('Password must contain a Lowercase letter')
    }
    if(!/[0-9]/.test(password)){
        errors.push('Password must contain a number')
    }
    if(!/[$!@#%^&*]/.test(password)){
        errors.push('Password must contain one of $!@#%^&*')
    }
    if(!/^([A-Z]|[a-z]|[0-9]|[$!@#%^&*])+$/.test(password)){
        errors.push("Password contains an illegal character")
    }

    return  errors

}

const allInputs = [usernameInput, passwordInput, repeatPasswordInput].filter( input => input != null)
allInputs.forEach(input => {
    input.addEventListener('input', ()=> {
        if(input.parentElement.classList.contains('incorrect')){
            input.parentElement.classList.remove('incorrect')
            errorMessage.innerText= ''
        }
    })
})