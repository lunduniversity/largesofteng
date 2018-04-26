
var mainModel = {};

var routing = {
    'simple': {
        partial: 'simple/simple.html',
        controller: simpleController
    },
    'admin': {
        partial: 'admin/admin.html',
        controller: adminController
    }
};

var mainView = {
    render: function() {
        var activeLink = document.querySelector('ul.navbar-nav>li.active');
        if (activeLink) activeLink.classList.remove('active');
        var link = document.querySelector('a[href="#/'+mainModel.selected+'"]');
        link.parentElement.classList.add('active');

        //var activeDiv = document.querySelector('.main-tab');
        //if (activeDiv) activeDiv.classList.remove('active');
        //document.getElementById(mainModel.selected).classList.add('active');

        document.getElementById('username').textContent = mainModel.user.username;
        document.querySelector('body').style.visibility = 'visible';
    },
    hideAdminLinks() {
        document.querySelectorAll('ul.navbar-nav>li.admin-only').forEach(li => li.style.display = 'none');
    }
};

var mainController = {
    tabTo: function() {
        var tabId = location.hash.slice(2);
        mainModel.selected = tabId;
        mainView.render();
        fetch(routing[tabId].partial)
            .then(response => response.text())
            .then(function(tabHtml) {
                document.getElementById('main-tab').innerHTML = tabHtml;
                routing[tabId].controller.load();
            });
    },
    load: function() {
        baseRest.getUser().then(response => response.json()).then(function(user) {
            mainModel.user = user;
            if (user.role == 'None') {
                window.location.replace('/login/login.html');
            }
            if (user.role !== 'Admin') {
                mainView.hideAdminLinks();
            }
            if (!location.hash) {
                location.hash = '/simple';
            } else {
                mainController.tabTo();
            }
        });
    },
    logout: function() {
        mainModel.user = userNone;
        baseRest.logout().then(function(response) {
            window.location.replace('/login/login.html');
        });
    }
};

document.addEventListener("DOMContentLoaded", mainController.load);
window.addEventListener("hashchange", mainController.tabTo);
