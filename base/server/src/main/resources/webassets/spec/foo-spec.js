/*
 * Unit tests for the foo controller
 * Author: Rasmus Ros, rasmus.ros@cs.lth.se
 */
describe('fooController', function() {

    // Some dummy data
    var foos = [
        new base.Foo({id: 1, userId: 1, payload: 's1', created: 0, total: 3}),
        new base.Foo({id: 2, userId: 1, payload: 's2', created: 1, total: 2}),
        new base.Foo({id: 3, userId: 1, payload: 's3', created: 2, total: 1})
    ];

    var node;
    var foo;

    // Creates the controller by loading the foo.html and put it in the node variable
    beforeEach(function(done) {
        controller = base.fooController();
        //specHelper.spyOnRest();
        specHelper.fetchHtml('foo/foo.html', document.body).then(function(n) {
            node = n;
        }).finally(done);
    });
    // Remove the node from the DOM
    afterEach(function() {
        document.body.removeChild(node);
    });

    it('should fetch foos on load', function(done) {
        var foosPromise = Promise.resolve(foos.slice(0));
        spyOn(base.rest, 'getFoos').and.returnValue(foosPromise);
        controller.load();
        foosPromise.then(function() {
            expect(base.rest.getFoos).toHaveBeenCalled();
        }).finally(done);
    });

    it('should populate table on load', function(done) {
        var foosPromise = Promise.resolve(foos.slice(0));
        spyOn(base.rest, 'getFoos').and.returnValue(foosPromise);
        controller.load();
        foosPromise.then(function() {
            // it should have populated the table with three rows
            var rows = node.querySelectorAll('tbody tr');
            expect(rows.length).toBe(foos.length);
        }).finally(done);
    });

    // Change here for lab 1
    xit('should have total in table', function(done) {
        var foosPromise = Promise.resolve([foos[0]]);
        spyOn(base.rest, 'getFoos').and.returnValue(foosPromise);
        controller.load();
        foosPromise.then(function() {
            var tr = node.querySelector('tbody tr');
            var tds = tr.querySelectorAll('td');
            expect(tds.length).toBe(3);
            expect(tds[0].textContent).toBe(foos[0].payload);
            var d = foos[0].createdDate;
            expect(tds[1].textContent).toBe(d.toLocaleDateString() + ' ' + d.toLocaleTimeString());
            expect(tds[2].textContent).toBe(''+foos[0].total);
        }).finally(done);
     });

    describe('submit specs', function() {
        var mockFoo = new base.Foo({id: 4, userId: 1, payload: 'new data', created: 3});
        var fooPromise;

        beforeEach(function(done) {
            var foosPromise = Promise.resolve([]);
            fooPromise = Promise.resolve(mockFoo);
            spyOn(base.rest, 'getFoos').and.returnValue(foosPromise);
            spyOn(base.rest, 'addFoo').and.returnValue(fooPromise);
            controller.load();
            foosPromise.finally(done);
        });

        it('should call submitFoo after button click', function() {
            spyOn(controller, 'submitFoo');
            node.querySelector('input').value = mockFoo.payload;
            node.querySelector('button').click();
            expect(controller.submitFoo).toHaveBeenCalledWith();
        });

        it('should call rest api with foo', function(done) {
            node.querySelector('input').value = mockFoo.payload;
            node.querySelector('button').click();
            fooPromise.then(function() {
                expect(base.rest.addFoo).toHaveBeenCalledWith({payload: 'new data'});
            }).finally(done);
        });

        it('should not submit if there is no text in input', function() {
            spyOn(controller, 'submitFoo');
            node.querySelector('button').click();
            expect(controller.submitFoo).not.toHaveBeenCalled();
        });

        it('should add row after submit', function(done) {
            var input = node.querySelector('input');
            input.value = mockFoo.payload;
            controller.submitFoo(input);
            fooPromise.then(function() {
                expect(node.querySelectorAll('tbody tr').length).toBe(1);
                expect(node.querySelector('tbody td').textContent).toBe('new data');
            }).finally(done);
        });

        it('should clear input after submit', function(done) {
            var input = node.querySelector('input');
            input.value = mockFoo.payload;
            controller.submitFoo(input);
            fooPromise.then(function() {
                expect(input.value).toBe('');
                expect(node.querySelectorAll('tbody tr').length).toBe(1);
                expect(node.querySelector('tbody td').textContent).toBe('new data');
            }).finally(done);
        });
    });
});
