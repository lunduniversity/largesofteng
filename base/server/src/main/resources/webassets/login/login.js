var loginController = (function() {
    var loginView = {
        show: function() {
            document.querySelector('form').style.visibility = 'visible';
        },
        showFailure: function(msg) {
            alert(msg);
        }
    };
    var controller = {
        load: function() {
            baseRest.getUser().then(response => response.json()).then(function(user) {
                if (user.role == 'None') {
                    loginView.show();
                } else {
                    window.location.replace('/');
                }
            });
        },
        loginUser: function(submitEvent) {
            submitEvent.preventDefault();
            var username = document.getElementById('username').value;
            var password = document.getElementById('password').value;
            var remember = document.getElementById('remember').checked;
            baseRest.login(username, password, remember)
                .then(function(response) {
                    if (response.ok) {
                        window.location.replace('/');
                    } else {
                        document.getElementById('password').value = "";
                        response.json().then(error => loginView.showFailure(error.message));
                    }
                });
            return false;
        }
    };
    return controller;
})();
document.addEventListener("DOMContentLoaded", loginController.load);
