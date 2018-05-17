var base = base || {};
base.userAdminController = function() {

    var model = {
        users: [],
        roles: [],
        roleNames: []
    };

    var view = {
        renderPart: function(user) {
            var t = document.getElementById('user-template');
            view.update(t.content.querySelector('button'), user);
            var clone = document.importNode(t.content, true);
            t.parentElement.appendChild(clone);
        },
        update: function(liElement, user) {
            liElement.textContent = user.username;
        },
        render: function() {
            model.users.forEach(user => view.renderPart(user));
            var roleTemplate = document.getElementById('role-template');
            model.roles.forEach(function(role) {
                var o = roleTemplate.content.querySelector('option');
                o.textContent = role.label;
                o.value = role.name;
                var clone = document.importNode(roleTemplate.content, true);
                roleTemplate.parentElement.appendChild(clone);
            });
        },
        removeUser: function(user, ix) {
            var el = document.getElementById('user-list').querySelectorAll('button')[ix];
            el.parentElement.removeChild(el);
        },
        resetEdit: function() {
            var userView = document.getElementById('user-view');
            var isEdit = userView.classList.contains('edit');
            view.editPassword(isEdit);
        },
        editPassword: function(disabled) {
            var pi = document.getElementById('set-password');
            pi.disabled = disabled;
        },
        selectUser: function(user, clickedEl) {
            // Set appropriate user-view class to either add or edit.
            var userView = document.getElementById('user-view');
            if (user.username === '') {
                userView.classList.remove('edit');
                userView.classList.add('add');
                view.editPassword(false);
            } else {
                userView.classList.add('edit');
                userView.classList.remove('add');
                view.editPassword(true);
            }

            // Set active link the left-hand side menu.
            document.getElementById('user-list')
                .querySelectorAll('.active')
                .forEach(activeEl => activeEl.classList.remove('active'));
            clickedEl.classList.add('active');

            document.getElementById('user-data').querySelector('a').href = '/rest/foo/user/'+user.id;

            // Set defaults of form values. This will allow the HTML reset button to work by default HTML behaviour.
            document.getElementById('user-id').defaultValue = user.id;
            document.getElementById('set-username').defaultValue = user.username;
            document.getElementById('set-password').defaultValue = '';
            var roleIx = model.roleNames.indexOf(user.role.name);
            var options = document.getElementById('set-role').querySelectorAll('option');
            options.forEach(o => o.defaultSelected = false);
            options[roleIx].defaultSelected = true;
            document.getElementById('user-form').reset();
        }
    };

    var controller = {
        submitUser: function(submitEvent) {
            submitEvent.preventDefault;
            var password = document.getElementById('set-password').value;
            var username = document.getElementById('set-username').value;
            var role = document.getElementById('set-role').value;
            var id = document.getElementById('user-id').value;
            var credentials = {username, password, role};
            if (password === '') {
                delete credentials.password;
            }
            if (id !== '') {
                base.rest.putUser(id, credentials).then(function(user) {
                    if (user.error) {
                        alert(user.message);
                    } else {
                        var el = document.querySelector('#user-list .active');
                        el.onclick = () => view.selectUser(user, el);
                        view.update(el, user);
                        view.selectUser(user, el);
                    }
                });
            } else {
                base.rest.addUser(credentials).then(function(user) {
                    if (user.error) {
                        alert(user.message);
                    } else {
                        model.users.push(user);
                        view.renderPart(user);
                        var el = document.querySelector('#user-list .list-group button:last-of-type');
                        el.onclick = () => view.selectUser(user, el);
                        view.selectUser(user, el);
                    }
                });
            }
            return false;
        },
        deleteUser: function() {
            var userId = document.getElementById('user-id').value;
            var ix = model.users.map(user => user.id).indexOf(parseInt(userId));
            var user = model.users[ix];
            base.rest.deleteUser(userId).then(function() {
                view.removeUser(user, ix);
                model.users.splice(ix, 1);
                document.querySelector('#user-list button').click();
            });
        },
        load: function() {
            document.getElementById('change-password').onclick = (event) => view.editPassword(false);
            document.getElementById('user-form').onsubmit = controller.submitUser;
            document.querySelector('#reset-user').onclick = view.resetEdit;
            document.querySelector('#delete-user').onclick = controller.deleteUser;
            Promise.all([
                base.rest.getUsers().then(function(users) {
                    model.users = users;
                    return users;
                }),
                base.rest.getRoles().then(function(roles) {
                    model.roles = roles;
                    model.roleNames = roles.map(r => r.name);
                    return roles;
                })
            ]).then(function(values) {
                view.render();
                var userEls = document.querySelectorAll('#user-list button');
                userEls.forEach(function(el, i) {
                    if (i == userEls.length-1) {
                        el.onclick = () => view.selectUser({username: '', role: model.roles[0], id: ''}, el);
                    } else {
                        var user = model.users[i]; // Closure on user, not on i
                        el.onclick = () => view.selectUser(user, el);
                    }
                });
                userEls[0].click();
            });
        }
    };

    return controller;
};
