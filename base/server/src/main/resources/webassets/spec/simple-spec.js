/*
 * Unit tests for the simple controller
 * Author: Rasmus Ros, rasmus.ros@cs.lth.se
 */
describe('simpleController', function() {

    // Some dummy data
    var simples = [
        new base.Simple({id: 1, userId: 1, payload: 's1', created: 0, count: 3}),
        new base.Simple({id: 2, userId: 1, payload: 's2', created: 1, count: 2}),
        new base.Simple({id: 3, userId: 1, payload: 's3', created: 2, count: 1})
    ];

    var node;
    var simple;

    // Creates the controller by loading the simple.html and put it in the node variable
    beforeEach(function(done) {
        controller = base.simpleController();
        //specHelper.spyOnRest();
        specHelper.fetchHtml('simple/simple.html', document.body).then(function(n) {
            node = n;
        }).finally(done);
    });
    // Remove the node from the DOM
    afterEach(function() {
        document.body.removeChild(node);
    });

    it('should fetch simples on load', function(done) {
        var simplesPromise = Promise.resolve(simples.slice(0));
        spyOn(base.rest, 'getSimples').and.returnValue(simplesPromise);
        controller.load();
        simplesPromise.then(function() {
            expect(base.rest.getSimples).toHaveBeenCalled();
        }).finally(done);
    });

    it('should populate table on load', function(done) {
        var simplesPromise = Promise.resolve(simples.slice(0));
        spyOn(base.rest, 'getSimples').and.returnValue(simplesPromise);
        controller.load();
        simplesPromise.then(function() {
            // it should have populated the table with three rows
            var rows = node.querySelectorAll('tbody tr');
            expect(rows.length).toBe(simples.length);
        }).finally(done);
    });

    // Change here for lab 1
    xit('should have count in table', function(done) {
        var simplesPromise = Promise.resolve([simples[0]]);
        spyOn(base.rest, 'getSimples').and.returnValue(simplesPromise);
        controller.load();
        simplesPromise.then(function() {
            var tr = node.querySelector('tbody tr');
            var tds = tr.querySelectorAll('td');
            expect(tds.length).toBe(3);
            expect(tds[0].textContent).toBe(simples[0].payload);
            var d = simples[0].createdDate;
            expect(tds[1].textContent).toBe(d.toLocaleDateString() + ' ' + d.toLocaleTimeString());
            expect(tds[2].textContent).toBe(''+simples[0].count);
        }).finally(done);
     });

    describe('submit specs', function() {
        var mockSimple = new base.Simple({id: 4, userId: 1, payload: 'new data', created: 3});
        var simplePromise;

        beforeEach(function(done) {
            var simplesPromise = Promise.resolve([]);
            simplePromise = Promise.resolve(mockSimple);
            spyOn(base.rest, 'getSimples').and.returnValue(simplesPromise);
            spyOn(base.rest, 'addSimple').and.returnValue(simplePromise);
            controller.load();
            simplesPromise.finally(done);
        });

        it('should call submitSimple after button click', function() {
            spyOn(controller, 'submitSimple');
            node.querySelector('input').value = mockSimple.payload;
            node.querySelector('button').click();
            expect(controller.submitSimple).toHaveBeenCalledWith();
        });

        it('should call rest api with simple', function(done) {
            node.querySelector('input').value = mockSimple.payload;
            node.querySelector('button').click();
            simplePromise.then(function() {
                expect(base.rest.addSimple).toHaveBeenCalledWith({payload: 'new data'});
            }).finally(done);
        });

        it('should not submit if there is no text in input', function() {
            spyOn(controller, 'submitSimple');
            node.querySelector('button').click();
            expect(controller.submitSimple).not.toHaveBeenCalled();
        });

        it('should add row after submit', function(done) {
            var input = node.querySelector('input');
            input.value = mockSimple.payload;
            controller.submitSimple(input);
            simplePromise.then(function() {
                expect(node.querySelectorAll('tbody tr').length).toBe(1);
                expect(node.querySelector('tbody td').textContent).toBe('new data');
            }).finally(done);
        });

        it('should clear input after submit', function(done) {
            var input = node.querySelector('input');
            input.value = mockSimple.payload;
            controller.submitSimple(input);
            simplePromise.then(function() {
                expect(input.value).toBe('');
                expect(node.querySelectorAll('tbody tr').length).toBe(1);
                expect(node.querySelector('tbody td').textContent).toBe('new data');
            }).finally(done);
        });
    });
});
