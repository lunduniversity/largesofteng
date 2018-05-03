/*
 * Unit tests for the user admin controller and user classes.
 * Author: Rasmus Ros, rasmus.ros@cs.lth.se
 */
describe('user specs', function() {

    var none = new base.User({username: '-', role: 'NONE', id: 0});
    var admin = new base.User({username: 'Admin', role: 'ADMIN', id: 1});
    var test = new base.User({username: 'Test', role: 'USER', id: 2});

    describe('User class', function() {
        it('isAdmin should return true for ADMIN', function() {
            expect(admin.isAdmin()).toBe(true);
            expect(test.isAdmin()).toBe(false);
            expect(none.isAdmin()).toBe(false);
        });
        it('isNone should return true for NONE', function() {
            expect(admin.isNone()).toBe(false);
            expect(test.isNone()).toBe(false);
            expect(none.isNone()).toBe(true);
        });
    });

    describe('Role class', function() {
        it('label should be capitalized', function() {
            expect(none.role.label).toBe('None');
            expect(admin.role.label).toBe('Admin');
            expect(test.role.label).toBe('User');
        });
    });

    describe('userAdminController', function() {

        var node;
        var controller;

        var startUsers = [admin, test];
        var roles = [admin.role, test.role];

        // Creates the controller by loading the admin.html and put it in the node variable
        beforeEach(function(done) {
            controller = base.userAdminController();
            var nodePromise = specHelper.fetchHtml('admin/user-admin.html', document.body);
            nodePromise.then(function(n) {
                node = n;
                return node;
            }).then(function() {
                var usersPromise = Promise.resolve(startUsers.slice(0));
                spyOn(base.rest, 'getUsers').and.returnValue(usersPromise);
                var rolesPromise = Promise.resolve(roles);
                spyOn(base.rest, 'getRoles').and.returnValue(rolesPromise);
                controller.load();
                return Promise.all([usersPromise, rolesPromise]);
            }).finally(done);
        });
        // Remove the node from the DOM
        afterEach(function() {
            document.body.removeChild(node);
        });

        it('should have roles in drop down', function() {
            var options = node.querySelectorAll('option');
            expect(options.length).toBe(2);
            expect(options[0].textContent).toBe(roles[0].label);
            expect(options[1].textContent).toBe(roles[1].label);
        });

        it('should select user on click', function() {
            var userBtn = node.querySelectorAll('#user-list button')[1];
            expect(userBtn.textContent).toBe(startUsers[1].username);
            expect(userBtn.classList.contains('active')).toBe(false);
            userBtn.click();
            var selected = node.querySelector('#user-list .active');
            expect(selected).toBe(userBtn);
            expect(selected.classList.contains('active')).toBe(true);
        });

        describe('add user', function() {
            var credentials = {username: 'new user', password: 'qwerty123', role: roles[1].name};
            var newUser = new base.User({username: credentials.username, role: credentials.role, id: 3});
            var userPromise = Promise.resolve(newUser);

            beforeEach(function(){
                document.getElementById('new-user').click();
                document.getElementById('set-username').value = credentials.username;
                document.getElementById('set-password').value = credentials.password;
                document.getElementById('set-role').selectedIndex = 1;
                spyOn(base.rest, 'addUser').and.returnValue(userPromise);
            });

            it('should post username, password, and role', function(done) {
                document.getElementById('submit-user').click();
                userPromise.then(function() {
                    expect(base.rest.addUser).toHaveBeenCalledWith(credentials);
                }).finally(done);
            });

            it('should add and select user in user list', function(done) {
                document.getElementById('submit-user').click();
                userPromise.then(function() {
                    var newUserBtn = document.querySelectorAll('#user-list button')[2];
                    expect(newUserBtn.classList.contains('active')).toBe(true);
                }).finally(done);
            });

            it('should be possible to delete the added user', function(done) {
                document.getElementById('submit-user').click();
                userPromise.then(function() {
                    var deleteUserPromise = Promise.resolve({});
                    spyOn(base.rest, 'deleteUser').and.returnValue(deleteUserPromise);
                    document.getElementById('delete-user').click();
                    return deleteUserPromise;
                }).then(function() {
                    var items = document.querySelectorAll('#user-list button');
                    expect(items.length).toBe(3);
                    expect(items[0].textContent).toBe(startUsers[0].username);
                    expect(items[1].textContent).toBe(startUsers[1].username);
                    expect(items[2].textContent).not.toBe(newUser.username); // should be add user button
                }).finally(done);
            });
        });

        describe('delete user', function() {
            it('should be possible to delete a selected user', function(done) {
                var deleteUserPromise = Promise.resolve({});
                spyOn(base.rest, 'deleteUser').and.returnValue(deleteUserPromise);
                document.getElementById('delete-user').click();
                deleteUserPromise.then(function() {
                    var items = document.querySelectorAll('#user-list button');
                    expect(items.length).toBe(2);
                }).finally(done);
            });
        });

        describe('edit user', function() {

            it('should allow editing of password after change button is pressed', function() {
                var input = document.getElementById('set-password');
                expect(input.disabled).toBe(true);
                document.getElementById('change-password').click();
                expect(input.disabled).toBe(false);
            });

            it('should allow skipping update of password', function(done) {
                var userCredentials = {username: 'New name', role: startUsers[0].role.name};
                var userPromise = Promise.resolve(new base.User({
                        username: userCredentials.username,
                        role: userCredentials.role,
                        id: startUsers[0].id}));
                spyOn(base.rest, 'putUser').and.returnValue(userPromise);
                document.getElementById('set-username').value = userCredentials.username;
                document.getElementById('submit-user').click();
                userPromise.then(function() {
                    expect(base.rest.putUser).toHaveBeenCalledWith(''+startUsers[0].id, userCredentials);
                }).finally(done);
            });

            it('should allow password to be changed', function(done) {
                var userCredentials = {username: 'New name', password: 'new password', role: startUsers[0].role.name};
                var userPromise = Promise.resolve(new base.User({
                        username: userCredentials.username,
                        role: userCredentials.role,
                        id: startUsers[0].id}));
                spyOn(base.rest, 'putUser').and.returnValue(userPromise);
                document.getElementById('set-username').value = userCredentials.username;
                document.getElementById('set-password').disabled = false;
                document.getElementById('set-password').value = userCredentials.password;
                document.getElementById('submit-user').click();
                userPromise.then(function() {
                    expect(base.rest.putUser).toHaveBeenCalledWith(''+startUsers[0].id, userCredentials);
                }).finally(done);
            });

            it('should update the left user list menu with new new username', function(done) {
                var userCredentials = {username: 'New name', password: 'new password', role: startUsers[0].role.name};
                var userPromise = Promise.resolve(new base.User({
                        username: userCredentials.username,
                        role: userCredentials.role,
                        id: startUsers[0].id}));
                spyOn(base.rest, 'putUser').and.returnValue(userPromise);
                document.getElementById('set-username').value = userCredentials.username;
                document.getElementById('set-password').disabled = false;
                document.getElementById('set-password').value = userCredentials.password;
                document.getElementById('submit-user').click();
                userPromise.then(function() {
                    var userBtns = document.querySelectorAll('#user-list button');
                    expect(userBtns.length).toBe(3);
                    expect(userBtns[0].textContent).toBe(userCredentials.username);
                }).finally(done);
            });
        });
    });
});