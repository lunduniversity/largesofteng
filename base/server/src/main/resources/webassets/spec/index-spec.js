/*
 * Spec for mainController, for the portion that can be easily unit tested.
 * Author: Rasmus Ros, rasmus.ros@cs.lth.se
 */
describe('mainController', function() {

    var none = new base.User({username: '-', role: 'NONE'});
    var test = new base.User({username: 'Test', role: 'USER'});
    var admin = new base.User({username: 'Admin', role: 'ADMIN'});

    var r1 = jasmine.createSpyObj('r1', ['load']);
    var r2 = jasmine.createSpyObj('r2', ['load']);
    var fakeRoute = {
        'r1': {partial: 'r1.html', controller: function() {return r1;}},
        'r2': {partial: 'r2.html', controller: function() {return r2;}}
    };

    var node;
    // Creates the controller by loading the index.html and put it in the node variable
    beforeEach(function(done) {
        specHelper.fetchHtml('index.html', document.body).then(function(n) {
            node = n;
        }).finally(done);

        // This disables the loading of all controllers and their partial HTML
        base.mainController.routingTable = fakeRoute;
    });
    // Remove the node from the DOM
    afterEach(function() {
        document.body.removeChild(node);
        window.onhashchange = null;
        window.location.hash = '';
    });

    it('should set route to default if none is specified', function(done) {
        var userPromise = Promise.resolve(test);
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        base.mainController.load();
        userPromise.then(function() {
            expect(window.location.hash).toBe('#/r1');
        }).finally(done);
    });

    it('should redirect bad route to default', function(done) {
        var userPromise = Promise.resolve(test);
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        window.location.hash = '/missing';
        base.mainController.load();
        userPromise.then(function() {
            expect(window.location.hash).toBe('#/r1');
        }).finally(done);
    });

    it('should load controller of r1', function(done) {
        var userPromise = Promise.resolve(admin);
        var fetchPromise = Promise.resolve({text: () => 'html'});
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        spyOn(window, 'fetch').and.returnValue(fetchPromise);

        window.location.hash = '/r1';
        base.mainController.load();
        Promise.all([userPromise, fetchPromise])
            .then(function() {
                expect(r1.load).toHaveBeenCalled();
            }).finally(done);
    });

    it('should fetch partial of r2', function(done) {
        var userPromise = Promise.resolve(admin);
        var fetchPromise = Promise.resolve({text: () => 'html'});
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        spyOn(window, 'fetch').and.returnValue(fetchPromise);

        window.location.hash = '/r2';
        base.mainController.load();
        Promise.all([userPromise, fetchPromise])
            .then(function() {
                expect(window.location.hash).toBe('#/r2');
                expect(window.fetch).toHaveBeenCalledWith('r2.html');
            }).finally(done);
    });

    it('should render username', function(done) {
        var userPromise = Promise.resolve(test);
        spyOn(base.mainController, 'changeRoute');
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        base.mainController.load();
        userPromise.then(function() {
            expect(document.getElementById('username').textContent).toBe(test.username);
        }).finally(done);
    });

    it('should redirect to login if user is none', function(done) {
        var userPromise = Promise.resolve(none);
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        spyOn(base, 'changeLocation');
        spyOn(base.mainController, 'changeRoute');
        base.mainController.load();
        userPromise.then(function() {
            expect(base.changeLocation).toHaveBeenCalledWith('/login/login.html');
        }).finally(done);
    });

    it('should redirect to login after logout', function(done) {
        var userPromise = Promise.resolve(admin);
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        spyOn(base, 'changeLocation');
        spyOn(base.mainController, 'changeRoute');
        base.mainController.load();
        userPromise.then(function() {
            var logoutPromise = Promise.resolve({});
            spyOn(base.rest, 'logout').and.returnValue(logoutPromise);
            document.getElementById('logout').click();
            logoutPromise.then(function() {
                expect(base.changeLocation).toHaveBeenCalledWith('/login/login.html');
            }).finally(done);
        });
    });

    it('should mark an element active in nav', function(done) {
        var userPromise = Promise.resolve(admin);
        spyOn(base.mainController, 'changeRoute');
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        base.mainController.load();
        userPromise.then(function() {
            expect(document.querySelector('#main-nav .active')).toBeDefined();
        }).finally(done);
    });

    it('should hide admin tabs from user', function(done) {
        var userPromise = Promise.resolve(test);
        spyOn(base.mainController, 'changeRoute');
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        base.mainController.load();
        userPromise.then(function() {
            var list = document.querySelectorAll('#main-nav .admin-only');
            list.forEach(function(ao) {
                expect(ao.style.display).toBe('none');
            })
        }).finally(done);
    });

    it('should show admin tabs to admin', function(done) {
        var userPromise = Promise.resolve(admin);
        spyOn(base.mainController, 'changeRoute');
        spyOn(base.rest, 'getUser').and.returnValue(userPromise);
        base.mainController.load();
        userPromise.then(function() {
            var list = document.querySelectorAll('#main-nav .admin-only');
            list.forEach(function(ao) {
                expect(ao.style.display).not.toBe('none');
            })
        }).finally(done);
    });
});