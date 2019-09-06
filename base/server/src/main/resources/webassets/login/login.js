var base = base || {};
base.changeLocation = function(url) {
    window.location.replace(url);
};
base.loginController = (function() {
    'use strict'
    const controller = {
        load: function() {
            document.getElementById('login-form').onsubmit = function(event) {
                event.preventDefault;
                controller.loginUser();
                return false;
            };
            base.rest.getUser().then(function(user) {
                if (!user.isNone()) {
                    base.changeLocation('/');
                }
            });
        },
        loginUser: function() {
            const username = document.getElementById('username').value;
            const password = document.getElementById('password').value;
            const remember = document.getElementById('remember').checked;
            base.rest.login(username, password, remember)
                .then(() => base.changeLocation('/'))
                .catch(() => document.getElementById('password').value = '');
        },
        initOnLoad: function() {
            document.addEventListener('DOMContentLoaded', base.loginController.load);
        }
    };
    return controller;
})();
