(function() {
    /*
        Variables
     */
    var user_id = '1111';
    var user_fullname = 'Dingle Zhang';
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
        // set welcome message
        var welcomeMsg = $('welcome-msg');
        welcomeMsg.innerHTML = 'Welcome, <strong>' + user_fullname + '</strong>.';

        // innit geolocation
        initGeolocation();
        $('nearby-btn').onclick = function(){
            loadNearbyItems();
        };
        $('fav-btn').onclick = function(){
            loadFavItems();
        }
        $('rec-btn').onclick = function(){
            loadRecItems();
        }
        // load nearby items

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
