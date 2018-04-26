var baseRest = {
    getUser: function() {
        return baseFetch('/rest/user');
    },
    login: function(username, password, rememberMe) {
        var loginObj = {username: username, password: password};
        return baseFetch('/rest/user/login?remember=' + rememberMe, {
            method: 'POST',
            body: JSON.stringify(loginObj),
            headers: jsonHeader()});
    },
    logout: function() {
        return baseFetch('/rest/user/logout', {method: 'POST'});
    },
    getUsers: function() {
        return baseFetch('/rest/user/all');
    },
    getRoles: function() {
        return baseFetch('/rest/user/roles');
    },
    putUser: function(user) {
        return baseFetch('/rest/user', {
            credentials: 'same-origin',
            method: 'POST',
            body: JSON.stringify(user),
            headers: jsonHeader()});
    },
    deleteUser: function(username) {
        return baseFetch('/rest/user/'+username, {method: 'DELETE'});
    },
    getSimple: function(userId) {
        var postfix = "";
        if (typeof userId !== "undefined") postfix = "/" + userId;
        return baseFetch('/rest/simple' + postfix);
    },
    putSimple: function(simpleData) {
        return baseFetch('/rest/simple', {
            method: 'POST',
            body: JSON.stringify(simpleData),
            headers: jsonHeader()});
    }
};

function baseFetch(url, config) {
    config = config || {};
    config.credentials = 'same-origin';
    return fetch(url, config).catch(function(error) {
        alert(error);
        throw error;
    });
}

function jsonHeader() {
    return {'Content-Type': 'application/json;charset=utf-8'};
}
