// TODO module?
var User = function(jsonObj) {
    Object.assign(this, jsonObj);

    this.isAdmin = function() {
        this.role === 'Admin';
    }
};

var userNone = new User({username: '-', role: 'None'});