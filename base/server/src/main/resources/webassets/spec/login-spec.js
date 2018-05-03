/*
 * Spec for loginController.
 * Author: Rasmus Ros, rasmus.ros@cs.lth.se
 */
describe('loginController', function() {

    var none = new base.User({username: '-', role: 'NONE'});
    var test = new base.User({username: 'Test', role: 'USER'});
    var admin = new base.User({username: 'Admin', role: 'ADMIN'});

    var node;
    // Creates the controller by loading the index.html and put it in the node variable
    beforeEach(function(done) {
        specHelper.fetchHtml('login/login.html', document.body).then(function(n) {
            node = n;
        }).finally(done);
    });
    // Remove the node from the DOM
    afterEach(function() {
        document.body.removeChild(node);
    });

    it('should redirect user to "/" if already logged in', function(done) {
        var userPromise = Promise.resolve(test);
        spyOn(base.rest, 'getUser').and.returnValue(Promise.resolve(test));
        spyOn(base, 'changeLocation');
        base.loginController.load();
        userPromise.then(function() {
            expect(base.changeLocation).toHaveBeenCalledWith('/');
        }).finally(done);
    });

    describe('login submit', function() {
        beforeEach(function(done) {
            var userPromise = Promise.resolve(none);
            spyOn(base.rest, 'getUser').and.returnValue(userPromise);
            base.loginController.load();
            userPromise.finally(done);
        });

        it('should call loginUser on form submit', function() {
            document.getElementById('username').value = 'test';
            document.getElementById('password').value = 'password1';
            spyOn(base.loginController, 'loginUser');
            document.querySelector('#login-form button').click();
            expect(base.loginController.loginUser).toHaveBeenCalled();
        });

        it('should do post to rest/login', function(done) {
            document.getElementById('username').value = 'test';
            document.getElementById('password').value = 'password2';
            spyOn(base, 'changeLocation');
            var loginPromise = Promise.resolve({ok: true});
            spyOn(base.rest, 'login').and.returnValue(loginPromise);
            document.querySelector('button').click();
            loginPromise.then(function() {
                expect(base.rest.login).toHaveBeenCalledWith('test', 'password2', false);
                expect(base.changeLocation).toHaveBeenCalledWith('/');
            }).finally(done);
        });

        it('should show error on failed login', function(done) {
            document.getElementById('username').value = 'test';
            document.getElementById('password').value = 'password3';
            var errorPromise = Promise.resolve({message: 'mock error'});
            var loginPromise = Promise.resolve({
                ok: false,
                json: () => errorPromise});
            spyOn(base.rest, 'login').and.returnValue(loginPromise);
            spyOn(base.loginController.view, 'showFailure');
            base.loginController.loginUser()
            Promise.all([loginPromise, errorPromise]).then(function() {
                expect(base.loginController.view.showFailure).toHaveBeenCalledWith('mock error');
            }).finally(done);
        })
    });
});