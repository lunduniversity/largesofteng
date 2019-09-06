/*
 * Model/view/controller for the foo tab.
 * Author: Rasmus Ros, rasmus.ros@cs.lth.se
 */
var base = base || {};
// Defines the base namespace, if not already declared. Through this pattern it doesn't matter which order
// the scripts are loaded in.
base.fooController = function() {
    'use strict' // add this to avoid some potential bugs

    // List of all foo data, will be useful to have when update functionality is added in lab 2.
    let model = [];

    const FooViewModel = function(_foo) {
        // We call the parameter _foo to avoid accidentally using the old version, we might otherwise end up in a
        // scenario where foo and this.foo are different things.
        this.foo = _foo;
        // This assignment is used below where 'this' is not available
        const viewModel = this;

        this.render = function(template) {
            this.update(template.content.querySelector('tr'));
            const clone = document.importNode(template.content, true);
            // TODO: Add stuff from lab 2 end-2-end task here
            template.parentElement.appendChild(clone);
        };
        // Update a single table row to display a foo
        this.update = function(trElement) {
            const tds = trElement.children;
            tds[0].textContent = viewModel.foo.payload;
            const d = viewModel.foo.createdDate;
            tds[1].textContent = d.toLocaleDateString() + ' ' + d.toLocaleTimeString();
            // TODO: Add stuff from lab 1 here
        };
    };

    const view = {
        // Creates HTML for each foo in model
        render: function() {
            // A template element is a special element used only to add dynamic content multiple times.
            // See: https://developer.mozilla.org/en-US/docs/Web/HTML/Element/template
            const t = this.template();
            model.forEach(d => d.render(t));
        },

        template: function() {
            return document.getElementById('foo-template');
        }
    };

    const controller = {
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
                model = foos.map(f => new FooViewModel(f));
                view.render();
            });
        },
        // Add a new foo to the table, based on the text content in the input field.
        submitFoo: function() {
            // Fetch an object reference to the input element with id 'foo-input' using the DOM API.
            const input = document.getElementById('foo-input');
            // Call the REST API, see file rest.js for definitions.
            base.rest.addFoo({payload: input.value})
                .then(function(foo) {
                    // Foo is the response from the server, it will have this form:
                    // {id: 123, userId: 1, payload: 'data', created: 1525343407}
                    const vm = new FooViewModel(foo);
                    model.push(vm);          // append the foo to the end of the model array
                    vm.render(view.template());             // append the foo to the table
                    input.value = '';        // clear the input HTML element
                });
        }
    };

    return controller;
};
