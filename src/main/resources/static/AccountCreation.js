const form = document.getElementById('form')
const usernameInput = document.getElementById('usernameInput')
const passwordInput = document.getElementById('passwordInput')
const repeatPasswordInput = document.getElementById('repeatPasswordInput')
const errorMessage = document.getElementById('errorMessage')
const otcInput = document.getElementById('codeInput')
const showPassword = document.getElementById('showPassword')
const showRepeatPassword = document.getElementById('showRepeatPassword')

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

if(showRepeatPassword != null) {
    showRepeatPassword.addEventListener("click", () => {
        if (repeatPasswordInput.type === "password") {
            repeatPasswordInput.type = "text";
            showRepeatPassword.innerHTML = eyeOffSVG
        } else {
            repeatPasswordInput.type = "password";
            showRepeatPassword.innerHTML = eyeOnSVG
        }
    })
}

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