var base = base || {};
base.rest = (function() {

    var Foo = function(json) {
        Object.assign(this, json);
        this.createdDate = new Date(this.created);
    };

    var Role = function(role) {
        this.name = role;
        this.label = this.name[0] + this.name.toLowerCase().slice(1);
    };

    var User = function(json) {
        Object.assign(this, json);
        this.role = new Role(json.role);
        this.json = json;

        this.isAdmin = function() {
            return this.role.name === 'ADMIN';
        };
        this.isNone = function() {
            return this.role.name === 'NONE';
        };
    };


    var objOrError = function(json, cons) {
        if (json.error) {
            return json;
        } else {
            return new cons(json);
        }
    };

    base.Foo = Foo;
    base.User = User;
    base.Role = Role;

    var baseFetch = function(url, config) {
        config = config || {};
        config.credentials = 'same-origin';
        var bf = fetch(url, config).catch(function(error) {
            alert(error);
            throw error;
        });
        return bf;
    };

    var jsonHeader = {
        'Content-Type': 'application/json;charset=utf-8'
    };

    return {
        getUser: function() {
            return baseFetch('/rest/user')
                .then(response => response.json())
                .then(u => new User(u));
        },
        login: function(username, password, rememberMe) {
            var loginObj = {username: username, password: password};
            return baseFetch('/rest/user/login?remember=' + rememberMe, {
                    method: 'POST',
                    body: JSON.stringify(loginObj),
                    headers: jsonHeader});
        },
        logout: function() {
            return baseFetch('/rest/user/logout', {method: 'POST'});
        },
        getUsers: function() {
            return baseFetch('/rest/user/all')
                .then(response => response.json())
                .then(users => users.map(u => new User(u)));
        },
        getRoles: function() {
            return baseFetch('/rest/user/roles')
                .then(response => response.json())
                .then(roles => roles.map(r => new Role(r)));
        },
        addUser: function(credentials) {
            return baseFetch('/rest/user', {
                    method: 'POST',
                    body: JSON.stringify(credentials),
                    headers: jsonHeader})
                .then(response => response.json())
                .then(u => objOrError(u, User));
        },
        putUser: function(id, credentials) {
            return baseFetch('/rest/user/'+id, {
                    method: 'PUT',
                    body: JSON.stringify(credentials),
                    headers: jsonHeader})
                .then(response => response.json())
                .then(u => objOrError(u, User));
        },
        deleteUser: function(username) {
            return baseFetch('/rest/user/'+username, {method: 'DELETE'});
        },
        getFoos: function(userId) {
            var postfix = "";
            if (typeof userId !== "undefined") postfix = "/user/" + userId;
            return baseFetch('/rest/foo' + postfix)
                .then(response => response.json())
                .then(foos => foos.map(f => new Foo(f)));
        },
        addFoo: function(foo) {
            return baseFetch('/rest/foo', {
                    method: 'POST',
                    body: JSON.stringify(foo),
                    headers: jsonHeader})
                .then(response => response.json())
                .then(f => new Foo(f));
        },
        deleteFoo: function(fooId) {
            return baseFetch('/rest/foo/'+fooId, {method: 'DELETE'});
        },
        updateFoo: function(fooId, total) {
            return baseFetch('/rest/foo/'+fooId+'/total/'+total, {method: 'POST'})
                .then(function() {
                    return total;
                });
        }
    };
})();

