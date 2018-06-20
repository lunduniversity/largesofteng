/*
 * Model/view/controller for the foo tab.
 * Author: Rasmus Ros, rasmus.ros@cs.lth.se
 */
var base = base || {};
// Defines the base namespace, if not already declared. Through this pattern it doesn't matter which order
// the scripts are loaded in.
base.fooController = function() {

    // List of all foo data, will be useful to have when update functionality is added.
    var model = [];

    var view = {
        // Creates HTML for each foo in model
        render: function() {
            model.forEach(d => view.renderPart(d));
        },
        // Creates HTML for foo parameter and adds it to the parent of the template
        renderPart: function(foo) {
            // Gets a reference to the template.
            // A template element is a special element used only to add dynamic content multiple times.
            // See: https://developer.mozilla.org/en-US/docs/Web/HTML/Element/template
            var t = view.template();
            view.update(t.content.querySelector('tr'), foo);
            var clone = document.importNode(t.content, true);
            t.parentElement.appendChild(clone);
        },
        // Update a single table row to display a foo
        update: function(trElement, foo) {
            var tds = trElement.children;
            tds[0].textContent = foo.payload;
            var d = foo.createdDate;
            tds[1].textContent = d.toLocaleDateString() + ' ' + d.toLocaleTimeString();
            // TODO: Edit here in lab 1
        },
        template: function() {
            return document.getElementById('foo-template');
        }
    };

    var controller = {
        load: function() {
            // Adds callback to the form.
            document.getElementById('foo-form').onsubmit = function(event) {
                event.preventDefault();
                controller.submitFoo();
                return false;
            };
            // Loads all foos from the server through the REST API, see res.js for definition.
            // It will replace the model with the foos, and then render them through the view.
            base.rest.getFoos().then(function(foos) {
                model = foos;
                view.render();
            });
        },
        // Add a new foo to the table, based on the text content in the input field.
        submitFoo: function() {
            // Fetch an object reference to the input element with id 'foo-input' using the DOM API.
            var input = document.getElementById('foo-input');
            // Call the REST API, see file rest.js for definitions.
            base.rest.addFoo({payload: input.value})
                .then(function(foo) {
                    // Foo is the response from the server, it will have this form:
                    // {id: 123, userId: 1, payload: 'data', created: 1525343407}
                    model.push(foo);         // append the foo to the end of the model array
                    view.renderPart(foo);    // append the foo to the table
                    input.value = '';        // clear the input HTML element
                });
        }
    };

    return controller;
};
