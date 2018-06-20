document.addEventListener("DOMContentLoaded", function(event) {
    baseLab.init({
        h2Gui: "Admin",
        sqlSelect: 2,
        sqlQuery: 4,
        sqlBase: true,
        sqlSchema: true,
        restDefinition: 2,
        restVerb: 2,
        jerseyResource: 3,
        jerseyPath: '/rest/foo/all',
        jerseyExample: 3,
        jerseyPathParam: 1,
        e2eBackEnd: true
    }, 'lab1.html', 'android.pdf');
});

var h2GuiValidate = function(submitEvent) {
    submitEvent.preventDefault;
    var value = document.querySelector('#h2Gui input').value;
    baseLab.complete('h2Gui', value);
    return false;
}

var sqlBaseValidate = function(submitEvent) {
    submitEvent.preventDefault();
    var checked = Array.from(document.querySelectorAll('#sqlBase input')).map(i => i.checked);
    baseLab.complete('sqlBase', checked[0] && !checked[1] && checked[2] && checked[3])
    return false;
}

var sqlSchemaValidate = function(submitEvent) {
    submitEvent.preventDefault();
    var checked = Array.from(document.querySelectorAll('#sqlSchema input')).map(i => i.checked);
    baseLab.complete('sqlSchema', checked[0] && !checked[1] && checked[2] && !checked[3])
    return false;
}

var jerseyPathValidate = function(submitEvent) {
    submitEvent.preventDefault();
    var value = document.querySelector('#jerseyPath input').value;
    value = value.replace(/^\//, '').replace(/\/$/, '');
    var parser = document.createElement('a');
    parser.href = '/'+value;
    baseLab.complete('jerseyPath', parser.pathname);
    return false;
}

var e2eBackEndValidate = function() {
    base.rest.addFoo({payload:'test'}).then(function(foo) {
        fetch('/rest/foo/'+foo.id+'/total', {
                credentials: 'same-origin',
                headers: {'Content-Type': 'application/json;charset=utf-8'},
                body: JSON.stringify(1),
                method: 'POST'})
        .then(response => response.json()).then(function(total) {
            if (total.error) {
                throw total.message;
            }
            if (total !== 2) {
                throw 'Got wrong total when testing implementation: expected 2 but received ' + total;
            }
        }).then(function() {
            fetch('/rest/foo/'+foo.id, {
                credentials: 'same-origin',
                method: 'DELETE'
            }).then(function(response) {
                if (!response.ok) {
                    throw 'Failed to call delete on foo';
                }
                baseLab.complete('e2eBackEnd', true);
            });
        }).catch(function(error) {
            alert(error);
            baseLab.complete('e2eBackEnd', false)
        });
    });
}
