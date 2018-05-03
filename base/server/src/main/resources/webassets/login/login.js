var base = base || {};
base.changeLocation = function(url) {
    window.location.replace(url);
};
base.loginController = (function() {
    var view = {
        showFailure: function(msg) {
            alert(msg);
        }
    };
    var controller = {
        view,
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
            var username = document.getElementById('username').value;
            var password = document.getElementById('password').value;
            var remember = document.getElementById('remember').checked;
            base.rest.login(username, password, remember)
                .then(function(response) {
                    if (response.ok) {
                        base.changeLocation('/');
                    } else {
                        document.getElementById('password').value = '';
                        response.json().then(error => view.showFailure(error.message));
                    }
                });
        },
        initOnLoad: function() {
            document.addEventListener('DOMContentLoaded', base.loginController.load);
        }
    };
    return controller;
})();
