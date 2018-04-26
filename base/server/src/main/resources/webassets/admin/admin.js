var adminController = {load: function(){} };

var adminTab = {
    showInMenu() {
        document.querySelectorAll('ul.navbar-nav>li.admin-only')
            .forEach(li => li.style.display = 'block');
    },
    getUsers: function() {
        baseRest.getUsers().then(response => response.json()).then(function(users) {
            users.forEach(user => adminTab.appendUser(user));
            adminTab.getSimple(document.querySelector("#userslist li"));
         });
    },
    getRoles: function() {
        baseRest.getRoles().then(response => response.json()).then(function(roles) {
            var t = document.getElementById('rolerow');
            var select = t.parentElement;
            roles.forEach(function(role) {
                option = t.content.querySelector('option');
                option.textContent = role;
                option.value = role;
                var clone = document.importNode(t.content, true);
                select.appendChild(clone);
            });
         })
    },
    addUser(submitEvent) {
        submitEvent.preventDefault();
        var username = document.getElementById('newusername').value;
        var password = document.getElementById('newpassword').value;
        var e = document.getElementById('newrole')
        var role = e.options[e.selectedIndex].value;
        baseRest.putUser({'username': username, 'password': password, 'role': role}).then(function(response) {
            if (response.ok) {
                document.getElementById('newusername').value = '';
                document.getElementById('newpassword').value = '';
                document.getElementById('newrole').selectedIndex = 0;
                response.json().then(newUser => adminTab.appendUser(newUser));
            } else {
               response.json().then(error => alert(error.message));
            }
        });
        return false;
    },
    appendUser: function(user) {
        var t = document.getElementById('userrow');
        li = t.content.querySelector('li');
        li.querySelector('b').textContent = user.role;
        li.querySelector('span').textContent = user.username;
        var clone = document.importNode(t.content, true);
        var target = t.parentElement;
        target.appendChild(clone);
        target.children[target.childElementCount-1].user = user;
    },
    deleteUser: function() {
        var d = adminTab.getSelectedUserDom();
        baseRest.deleteUser(d.user.id).then(function(response) {
            if (response.ok) {
                d.parentElement.removeChild(d);
                adminTab.getSimple(document.querySelector("#userslist li"));
            } else {
                response.json().then(error => alert(error.message));
            }
        });
    },
    getSimple: function(eventSource) {
        var active = adminTab.getSelectedUserDom();
        if (active) {
            active.classList.remove('active');
        }
        eventSource.classList.add('active');
        baseRest.getSimple(eventSource.user.id).then(response => response.json()).then(function(userData) {
            document.getElementById('usersdata').querySelector('code').textContent = JSON.stringify(userData, null, 2);
        });
    },
    getSelectedUserDom: function() {
       return document.querySelector('#userslist li.active');
    }
};
