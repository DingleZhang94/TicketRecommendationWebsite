(function() {
    /*
        Variables
     */
    var user_id = null;
    var user_fullname = null;
    var lon = -122.08;
    var lat = 37.38;
    // innitialize index page
    init();

    //Helper function: $(), ajax() ----------------------------------

    /**
     *DOM Helper function:
     * select and create element
     * @param id - String, element id to be created or selected
     * @param props - JsonObject, create element with property
     * @return DOM element
     */
    function $(id, props) {
        // select element without props.
        if (!props) {
            return document.getElementById(id);
        } else {
            // create new element if has props.
            var newEle = document.createElement(id);
            for (var prop in props) { 
                // when the property is valid, add it.
                if (props.hasOwnProperty(prop)) {
                    newEle[prop] = props[prop];
                }
            }
            return newEle;
        }
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
        xhr.onload = function() {
            if (xhr.status === 200) {
                success(xhr.responseText);
            } else if (xhr.status === 403) {
                onSessionInvalid();
            } else {
                fail();
            }
        }
        // request error
        xhr.onerror = function() {
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

    function showWarningMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-triangle"></i>' +
            msg + '</p>';
    }

    function showLoadingMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-spinner fa-spin"></i>' +
            msg + '</p>';
    }

    function showErrorMessage(msg) {
        var itemList = $('item-list');
        itemList.innerHTML = '<p class="notice"><i class="fa fa-exclamation-circle"></i> ' + msg + '</p>';
    }

    // make navi button active
    function activeBtn(btnId) {
        var btns = document.getElementsByClassName('content-nav-btn');

        //deactivate all button
        for (var i = 0; i < btns.length; i++) {
            btns[i].className = btns[i].className.replace(/\bactive\b/, '');
        }

        // active the one with btnId
        var btn = $(btnId);
        btn.className += ' active';
    }
    //--------------------------------------------------------------------------

    // innitial the web page
    function init() {

        $('login-submit').onclick = submitLog;
        
        $('wel-logout').onclick = logOut;
        
        $('wel-login').onclick = function(){
            showLoggin();
        }
        $('register-link').onclick = function(){
            showRegister();
        }
        
        $('reg-submit').onclick = submitReg;

        $('nearby-btn').onclick = function(){
            loadNearbyItems();
        };
        $('fav-btn').onclick = function(){
            loadFavItems();
        }
        $('rec-btn').onclick = function(){
            loadRecItems();
        }
        // show loggin button at header.
        showLogginBtn();

        initUserInfo();

        toggleCloseBtn();
        // load nearby items
    }

    function submitReg(){
        var user_id = $('reg-username').value;
        var password = $('reg-password').value;
        var cmfPsw = $('reg-confirm-psw').value;
        var firstname = $('reg-firstname').value;
        var lastname = $('reg-lastname').value;
        var url = './register';
        var req = JSON.stringify({
            username: user_id,
            password: password,
            confirm_psw : cmfPsw,
            firstname: firstname,
            lastname: lastname,
        });
        console.log("submit register: "+ req );
        ajax("POST", url, req, function(res){
            var response = JSON.parse(res);
            var result = response['result'];
            if(result === 'success!'){
                hideRegister();
                showLoggin();
            }else{
                showRegWarning('Duplicate username!');
            }
        },function(){
            showRegWarning('Cannot connect to server!');
        })
    }

    function submitLog(){
        hideLogginWarning();
        var user_id = $('login-username').value;
        var pasword = $('login-password').value;
        var url = './login'
        var req = {
            username: user_id,
            password: pasword,
        }
        req = JSON.stringify(req);
        ajax('POST', url, req, function(res){
            console.log(res);
            var response = JSON.parse(res);
            var result = response['result'];
            if(result === 'success'){
                user_id = response['userId'];
                user_fullname = response['fullname'];
                setFullname();
                hideLogginBtn();
                hideLoggin();
                showlogoutBtn();
                initGeolocation();
            }else{
                showLogginWarning('Can not log in!');
            }
        },function(){
            showLogginWarning('Can not log in!');
        });
    }

    /**
     * add close function to close button in modal
     */
    function toggleCloseBtn(){ 
        var closes = document.getElementsByClassName('close');
        for(var i = 0; i < closes.length; i++){
            closes[i].addEventListener('click',function(){
                hideLoggin();
                hideRegister();
            })
        }
    }
    
    function showRegWarning(msg){
        $('reg-warning').innerHTML = msg;
    }

    function hideRegWarning(){
        $('reg-warning').innerHTML = null;
    }

    function hidelogoutBtn(){
        $('wel-logout').innerHTML = null;
    }

    function showlogoutBtn(){
        $('wel-logout').innerHTML = 'log out';
    }

    function showLogginWarning(msg){
        $('login-warning').innerHTML = msg;
    }

    function hideLogginWarning(msg){
        $('login-warning').innerHTML = null;
    }

    function showLogginBtn(){
        $('wel-login').innerHTML = "please log in";
    }

    function hideLogginBtn(){
        $('wel-login').innerHTML = null;
    }

    function showLoggin(){
        $('login-modal').style.display = "block";
    }

    function hideLoggin(){
        $('login-modal').style.display = "none";
    }

    function showRegister(){
        $('register-modal').style.display = "block";
    }

    function hideRegister(){
        $('register-modal').style.display = "none";
    }

    function setFullname(){
        $("wel-username").innerHTML = user_fullname;
    }

    function logOut(){
        var url = './logout';
        var req = JSON.stringify({});
        ajax("POST", url, req,function(res){
            var response = JSON.parse(res);
            var result = response['result'];
            if(result === '1'){
                // reset full name to stranger
                user_fullname="stranger";
                user_id = null;
                setFullname();
                // enbale login button
                showLogginBtn();
                hidelogoutBtn();
                initUserInfo();
            }else{
                showErrorMessage('fail to log out');
            }
        },function(){
            console.log("can't log out with server");
        })
    }

    /**
     * innit Userid, user_fullname.
     */
    function initUserInfo(){
        if(document.cookie.includes('username') && document.cookie.includes('token')){
            var url = './login';
            var req = JSON.stringify({});
            showLoadingMessage("Waiting for log in!");
            
            hidelogoutBtn();
            ajax('GET',url, req, function(res){
                console.log("init" + res);
                var result = JSON.parse(res);
                if(result['result'] === 'success'){
                    user_fullname = result['fullname'];
                    user_id = result['userId'];
                    setFullname();
                    initGeolocation();
                    hideLogginBtn();
                    showlogoutBtn();
                }else{
                    showLoggin();
                }
            }, function(){
                showLoggin();
            });
        }else{
            showLoggin();
        }
    }

    // initial the geolocation
    function initGeolocation() {
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(onPositionUpdated, onLoadPositionFailed, {
                maximumAge: 60000
            });
            showLoadingMessage('Retrieving your loaction...');
        } else {
            onLoadPositionFailed();
        }
    }

    // When navigator has position
    function onPositionUpdated(position) {
        lat = position.coords.latitude;
        lon = position.coords.longitude;
        loadNearbyItems();
    }

    function onLoadPositionFailed(){
        console.warn('navigator.geolocation is not available');
        geoLoactionFromIP();
    }

    //Get geolocation from inspect
    function geoLoactionFromIP() {
        var url = 'http://ipinfo.io/json';
        var req = null;
        ajax('GET', url, req, function(res) {
            var result = JSON.parse(res);
            if (result.loc) {
                //success update loaction by ip
                var loc = result.loc.split(",");
                lat = loc[0];
                lon = loc[1];
            } else {
                // fail to get location, use default instead.
                console.warn('Getting location by IP failed');
            }
            loadNearbyItems();
        });
    }


    // load favorite items
    function loadFavItems(){
        activeBtn('fav-btn');
        var url = './history?user_id=' + user_id;
        console.log("loadnearbyitems:"+ url);
        var req = JSON.stringify({});
        showLoadingMessage('Loading favorite items...');

        ajax("GET", url, req,
            // success
            function(res){
                var items = JSON.parse(res);
                if(!items && items.length===0){
                    showWarningMessage('No favorite tickets.');
                }else{
                    listitems(items);
                }
            },
            // fail
            function(){
                showErrorMessage('Cannot load nearby tickets.');
            });
    }


    // load nearby items
    function loadNearbyItems() {
        activeBtn('nearby-btn');
        var url = './search?user_id=' + user_id + "&lat=" + lat + "&lon=" + lon;
        console.log("loadnearbyitems:"+ url);
        var req = JSON.stringify({});

        showLoadingMessage('Loading Nearby tickets...');

        ajax("GET", url, req,
            // success
            function(res){
                var items = JSON.parse(res);
                if(!items && items.length===0){
                    showWarningMessage('No nearby item.');
                }else{
                    listitems(items);
                }
            },
            // fail
            function(){
                showErrorMessage('Cannot load nearby tickets.');
            });
    }

    // load recmendated items
    function loadRecItems(){
        activeBtn('rec-btn');
        var url = './recommendation?user_id=' + user_id+ "&lat=" + lat + "&lon=" + lon;
        var req = JSON.stringify({});
        showLoadingMessage('Loading Recommend tickets...');

        ajax("GET", url, req, function(res){
            var items = JSON.parse(res);
            if (!items && items.length === 0) {
                showWarningMessage("No Recommend tickets.")
            }else{
                listitems(items);
            }
        },
        function(){
            showErrorMessage('cannot load recommend tickets.')
        });
    }


    /**
     * list items into item list
     * @param {[JSONArray]} items [items that need to be added into list]
     */
    function listitems(items){
        var itemList = $('item-list');
        // clear the list
        itemList.innerHTML= "";

        for(var item of items){
            addItem(itemList, item);
        }
    }

    /**
     * add a single item to itemlist
     * @param {[DOM element]} itemList [list that item need to add in ]
     * @param {[JSONObject]} item     [single item]
     */
    function addItem(itemList, item){
        var item_id = item.item_id;

        var li = $("li",{
            id:"item-"+ item_id,
            className: "item"
        });
        li.dataset.item_id = item_id;
        li.dataset.favorite = item.favorite;

        // image
        var img = $('img',{
            src: item.image_url
        });

        // first block
        var div1 = $('div',{});

        var item_name =$('a',{
            className: "item-name",
            href:item.url,
            target:'_blank'
        });
        item_name.innerHTML = item.name;

        var category = $('p',{
            className:"item-category"
        });
        category.innerHTML=item.categories.join(', ');
        div1.appendChild(item_name);
        div1.appendChild(category);


        // star
        var stars = $('div',{
            className:"stars"
        });

        // for(let i = 0; i< item.rating; i++){
        //     var star = $('i',{className: 'fa fa-star'});
        //     stars.appendChild(star);
        // }

        // address
        var address = $('p',{
            className:'item-addr'
        });
        address.innerHTML = item.address.replace("\n\n",'<br/>');


        // like
        var like = $('div', {
            className: 'like'
        });

        like.onclick = function(){
            changeFavoriteItem(item_id);
        }

        like.appendChild(
            $('i',{
                id : 'fav-icon-' + item_id,
                className:item.favorite? 'fas fa-heart':'far fa-heart'
            })
        );

        var itemList = $('item-list');
        li.appendChild(img);
        li.appendChild(div1);
        li.appendChild(address);
        li.appendChild(like);
        itemList.appendChild(li);
    }

    function changeFavoriteItem(item_id){
        console.log(changeFavoriteItem);
        var li = $('item-' + item_id);
        var favIcon = $('fav-icon-' + item_id);
        // The request parameters
        var url = './history';
        var req = JSON.stringify({user_id: user_id, favorite: item_id});
        var fav = li.dataset.favorite;
        var method = (fav === "true") ? 'DELETE':'POST';

        ajax(method, url, req,
        // successful callback
        function(res) {
            var result = JSON.parse(res);
            if (result.result === 'Success') {
                li.dataset.favorite = !(fav === "true");
                favIcon.className = !(fav === "true")
                    ? 'fas fa-heart'
                    : 'far fa-heart';
            }
        });
    }


})();
