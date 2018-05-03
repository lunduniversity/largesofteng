var base = base || {};
base.simpleController = function() {
    var model = [];

    var view = {
        renderPart: function(simple) {
            var t = view.template();
            view.update(t.content.querySelector('tr'), simple);
            var clone = document.importNode(t.content, true);
            t.parentElement.appendChild(clone);
        },
        update: function(trElement, simple) {
            var tds = trElement.children;
            tds[0].textContent = simple.payload;
            tds[1].textContent = simple.createdDate.toISOString();
        },
        render: function() {
            model.forEach(d => view.renderPart(d));
        },
        remove: function(nd) {
            var d = nd.data;
            view.template().parentElement.removeChild(nd);
        },
        template: function() {
            return document.getElementById('simple-template');
        }
    };

    var controller = {
        load: function() {
            document.getElementById('simple-form').onsubmit = function(event) {
                event.preventDefault();
                controller.submitSimple();
                return false;
            };
            base.rest.getSimples().then(function(simples) {
                model = simples;
                view.render();
            });
        },
        submitSimple: function() {
            var input = document.getElementById('simple-input');
            base.rest.addSimple({payload: input.value})
                .then(function(simple) {
                    model.push(simple);
                    view.renderPart(simple);
                    input.value = '';
                });
        },
        deleteSimple: function(clickEvent) {
            var index = model.data.indexOf(d);
            model.splice(index, 1);
            // view.remove or view.render?
            // TODO finish me
        }
    };

    return controller;
};
