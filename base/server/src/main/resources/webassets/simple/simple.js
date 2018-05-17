/*
 * Model/view/controller for the simple tab.
 * Author: Rasmus Ros, rasmus.ros@cs.lth.se
 */
var base = base || {};
// Defines the base namespace, if not already declared. Through this pattern it doesn't matter which order
// the scripts are loaded in.
base.simpleController = function() {

    // List of all simple data, will be useful to have when update functionality is added.
    var model = [];

    var view = {
        // Creates HTML for each simple in model
        render: function() {
            model.forEach(d => view.renderPart(d));
        },
        // Creates HTML for simple parameter and adds it to the parent of the template
        renderPart: function(simple) {
            // Gets a reference to the template.
            // A template element is a special element used only to add dynamic content multiple times.
            // See: https://developer.mozilla.org/en-US/docs/Web/HTML/Element/template
            var t = view.template();
            view.update(t.content.querySelector('tr'), simple);
            var clone = document.importNode(t.content, true);
            t.parentElement.appendChild(clone);
        },
        // Update a single table row to display a simple
        update: function(trElement, simple) {
            var tds = trElement.children;
            tds[0].textContent = simple.payload;
            var d = simple.createdDate;
            tds[1].textContent = d.toLocaleDateString() + ' ' + d.toLocaleTimeString();
            // Edit here in lab 1
        },
        template: function() {
            return document.getElementById('simple-template');
        }
    };

    var controller = {
        load: function() {
            // Adds callback to the form.
            document.getElementById('simple-form').onsubmit = function(event) {
                event.preventDefault();
                controller.submitSimple();
                return false;
            };
            // Loads all simples from the server through the REST API, see res.js for definition.
            // It will replace the model with the simples, and then render them through the view.
            base.rest.getSimples().then(function(simples) {
                model = simples;
                view.render();
            });
        },
        // Add a new simple to the table, based on the text content in the input field.
        submitSimple: function() {
            // Fetch an object reference to the input element with id 'simple-input' using the DOM API.
            var input = document.getElementById('simple-input');
            // Call the REST API, see file rest.js for definitions.
            base.rest.addSimple({payload: input.value})
                .then(function(simple) {
                    // Simple is the response from the server, it will have this form:
                    // {id: 123, userId: 1, payload: 'data', created: 1525343407}
                    model.push(simple);         // append the simple to the end of the model array
                    view.renderPart(simple);    // append the simple to the table
                    input.value = '';           // clear the input HTML element
                });
        }
    };

    return controller;
};
