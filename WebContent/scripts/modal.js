(function () {
    var closes = document.getElementsByClassName('close');
    var login = document.getElementById("login");
    var register = document.getElementById("register-link");
    //var modals = document.getElementsByClassName('modal');
    var loginModal = document.getElementById('login-modal');
    var registerModal = document.getElementById('register-modal');

    // add function to close button
    for (var i = 0; i < closes.length; i++) {
        closes[i].addEventListener("click", closeModal);
    }

    // add function to open button
    login.addEventListener("click", openLogin);
    register.addEventListener("click", openRegister);


    function closeModal() {  
         loginModal.style.display = "none";
         registerModal.style.display ="none";  
    }

    function openLogin() {  
        loginModal.style.display = "block";
        registerModal.style.display ="none";
    }

    function openRegister() {
        loginModal.style.display = "none";
        registerModal.style.display ="block";
    }



    /**
     * Ajax helper function
     * @param method - GET/POST/DELETE/PUT
     * @param url - API end point
     * @param success - callback when success
     * @param fail - callback when fail
     */
    function ajax(method, url, data, success, fail) {
        var xhr = new XMLHttpRequest();

        xhr.open(method, url, true);

        // request is loading
        xhr.onload = function () {
            if (xhr.status === 200) {
                success(xhr.responseText);
            } else if (xhr.status === 403) {
                onSessionInvalid();
            } else {
                fail();
            }
        }
        // request error
        xhr.onerror = function () {
            console.error('The request couldn\'t be completed');
            fail();
        }

        // send XMLHttpRequest
        if (data === null) {
            xhr.send();
        } else {
            xhr.setRequestHeader('Content-Type', 'application/json; charset=utf-8');
            xhr.send(data);
        }

    }

})()