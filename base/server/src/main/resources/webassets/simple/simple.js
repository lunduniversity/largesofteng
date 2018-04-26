var simpleController = (function () {
    var model = [];

    var view = {
        renderPart: function(simpleData) {
            var t = this.template();
            this.update(t.content.querySelector('tr'), simpleData);
            var clone = document.importNode(t.content, true);
            t.parentElement.appendChild(clone);
        },
        update: function(trElement, simpleData) {
            var tds = trElement.children;
            tds[0].textContent = simpleData.payload;
            tds[1].textContent = simpleData.createdDate.toISOString();
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
            baseRest.getSimple().then(response => response.json()).then(function(data) {
                model = data.map(d => new SimpleData(d));
                view.render();
            });
        },
        submitSimpleData: function(submitEvent) {
            submitEvent.preventDefault();
            var form = submitEvent.target;
            var input = form.querySelector('input');
            baseRest.putSimple({payload: input.value})
                .then(response => response.json())
                .then(function(s) {
                    var simpleData = new SimpleData(s);
                    model.push(simpleData);
                    view.renderPart(simpleData);
                    input.value = '';
                });
            return false;
        },
        deleteSimple: function(clickEvent) {
            var index = model.data.indexOf(d);
            model.splice(index, 1);
        },
        putSimple: function(clickEvent) {
            var trElement;
        }
    };

    var SimpleData = function(json) {
        Object.assign(this, json);
        this.createdDate = new Date(this.created);
    };

    return controller;
})();
