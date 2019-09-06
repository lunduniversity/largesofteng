var base = base || {};
base.mainController = (function() {
    'use strict'

    const routingTable = {
        // first in table is the default
        'foo': {
            partial: 'foo/foo.html',
            controller: base.fooController
        },
        'admin': {
            partial: 'admin/user-admin.html',
            controller: base.userAdminController
        }
    };

    const model = {
        route: ''
    };

    const controller = {
        routingTable: routingTable,
        changeRoute: function() {
            const newRoute = location.hash.slice(2);
            if (!controller.routingTable[newRoute]) {
                location.hash = '/'+Object.keys(controller.routingTable)[0];
                return;
            }
            model.route = newRoute;
            fetch(controller.routingTable[newRoute].partial)
                .then(response => response.text())
                .then(function(tabHtml) {
                    document.getElementById('main-tab').innerHTML = tabHtml;
                    controller.routingTable[newRoute].controller().load();
                });
            const nav = document.getElementById('main-nav');
            const activeTabLink = nav.querySelector('li.active');
            if (activeTabLink) activeTabLink.classList.remove('active');
            const newActiveTabLink = nav.querySelector('a[href="#/'+model.route+'"]');
            if (newActiveTabLink) newActiveTabLink.parentElement.classList.add('active');
        },
        load: function() {
            document.getElementById('logout').onclick = controller.logout;
            window.onhashchange = base.mainController.changeRoute;
            base.mainController.changeRoute();
            base.rest.getUser().then(function(user) {
                model.user = user;
                document.getElementById('username').textContent = model.user.username;
                if (user.isNone()) {
                    base.changeLocation('/login/login.html');
                } else if (!user.isAdmin()) {
                    document.querySelectorAll('#main-nav li.admin-only').forEach(li => li.style.display = 'none');
                }
            });
        },
        logout: function() {
            base.rest.logout().then(function(response) {
                base.changeLocation('/login/login.html');
            });
        },
        initOnLoad: function() {
            document.addEventListener("DOMContentLoaded", base.mainController.load);
        }
    };
    return controller;
})();

base.changeLocation = function(url) {
    window.location.replace(url);
};
