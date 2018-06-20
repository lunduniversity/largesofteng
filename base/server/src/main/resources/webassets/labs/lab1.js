var answerSheet = {
    // Feel free to look at this, at least you learn JavaScript :)
    systemBrowse: true,

    jsFunction: 3,
    jsProperty: 'banana',
    jsDebugging: 'a',

    htmlHeader: true,

    cssSpace: 1,
    cssSelector: 'Select meAnd me',
    cssSelect: 3,

    bootstrapForms: 2,

    e2eTabHtml: true,
    e2eTabRoute: true,

    e2eServer: true,
    e2eTable: true
};

document.addEventListener("DOMContentLoaded", function(event) {
    baseLab.init(answerSheet, null, 'lab2.html');
});

var testJavaScript = function(param1, param2) {alert(param1 + param2);}

var systemBrowseValidate = function() {
    base.rest.getUser()
        .catch(error => baseLab.complete('systemBrowse', false))
        .then(function(user) {
            baseLab.complete('systemBrowse', !user.isNone());
        });
};

var jsPropertyValidate = function(submitEvent) {
    submitEvent.preventDefault();
    var input = document.querySelector('#jsProperty input').value;
    try {
        var ok = eval('(' + input + ')').fruit;
        baseLab.complete('jsProperty', ok);
        return false;
    } catch(error) {
        alert(error);
        baseLab.complete('jsProperty', false);
        throw error;
    }
};

var jsDebuggingValidate = function(event) {
    event.preventDefault();
    var input = document.querySelector('#jsDebugging input');
    try {
        baseLab.complete('jsDebugging', input.value.replace(/['"]+/g, ''));
    } catch (error) {
        alert(error);
        baseLab.complete('jsDebugging', false);
        throw error;
    }
    return false;
};

var htmlDOMDemo = function() {
    var div = document.createElement('div');
    div.innerHTML = '<b>browser will parse this and create HTML elements</b>';
    var target = document.getElementById('putItHere');
    target.appendChild(div);
};

var htmlHeaderValidate = function(submitEvent) {
    submitEvent.preventDefault();
    var d = document.getElementById('htmlHeaderResult');
    var r = document.getElementById('htmlHeaderInput');
    d.innerHTML = r.value;
    if (d.innerHTML.toLowerCase() != r.value.trim().toLowerCase()) {
        baseLab.complete('htmlHeader', false);
        return false;
    }
    var h1 = d.querySelector('h1');
    if (!h1) {
        baseLab.complete('htmlHeader', false);
    } else if (h1.textContent && h1.textContent.length > 0) {
        baseLab.complete('htmlHeader', true);
    }
    return false;
};

var htmlFormTryoutValidate = function(submitEvent) {
    submitEvent.preventDefault();
    if (localStorage.getItem('htmlFormTryout') !== 'true') {
        baseLab.complete('htmlFormTryout');
    }
    return false;
}

var cssSelectorValidate = function(submitEvent) {
    submitEvent.preventDefault();
    var sel = document.querySelector('#cssSelector input').value;
    try {
        baseLab.complete('cssSelector', Array.from(document.querySelectorAll(sel))
            .map(s => s.textContent)
            .reduce((acc,val) => acc+val, ""));
    } catch (error) {
        alert(error);
        baseLab.complete('cssSelector', false);
        throw error;
    }
    return false;
};

var e2eServerValidate = function() {
    base.rest.getFoos().then(function(data) {
        if (data.error) {
            alert('Received error from server: ' + data.message);
            baseLab.complete('e2eServer', false);
        } else if (data.length == 0) {
            baseLab.complete('e2eServer', false);
            alert('There is no data added to the foo table for the user you have logged in with. ' +
                'For this verification to work there needs to be at least one row in the table. '+
                'Please go to http://localhost:8080 and input some in the Foo tab.');
        } else if (typeof data[0].total === "undefined") {
            baseLab.complete('e2eServer', false);
            alert('Could not find not total field in the response from http://localhost:8080/rest/foo.' +
                ' Did you remember to restart the server?');
        } else if (typeof data[0].total !== "number") {
            baseLab.complete('e2eServer', false);
            alert('The "total" field has the wrong type. It should be number but got ' + typeof data[0].total);
        } else {
            baseLab.complete('e2eServer', true);
        }
    }).catch(function(error) {
        baseLab.complete('e2eServer', false);
    });
};

var e2eTabHtmlValidate = function() {
    var p1 = fetch('/hello/hello.html').then(function(response) {
        if (response.ok) {
            return response.text();
        } else {
            throw 'Could not find the HTML file /hello/hello.html';
        }
    }).then(function(html) {
        var d = document.createElement('div');
        d.innerHTML = html;
        var h = d.querySelector('h1');
        if (!h || h.textContent === '') {
            throw 'Could not find a h1 tag with text';
        }
    });
    var p2 = fetch('/hello/hello.js').then(function(response) {
        if (response.ok) {
            return response.text();
        } else {
            throw 'Could not find the JavaScript file /hello/hello.js';
        }
    }).then(function(js) {
        eval(js);
        if (typeof base.helloController !== "function") {
            throw 'The JavaScript in hello.js should define the helloController as a function.';
        }
        if (typeof base.helloController().load !== "function") {
            throw 'The helloController should have a load function.';
        }
    });
    Promise.all([p1,p2]).then(function() {
        baseLab.complete('e2eTabHtml', true);
    }).catch(function(error) {
        if (error) {
            alert(error + '. Did you remember to restart the server?');
        }
        baseLab.complete('e2eTabHtml', false);
    });
};

var e2eTabRouteValidate = function() {
    var p1 = fetch('/index.html').then(response => response.text()).then(function(html) {
        var d = document.createElement('div');
        d.innerHTML = html;
        if (Array.from(d.querySelectorAll('a.nav-link'))
                .map(a => a.href)
                .filter(link => link.indexOf('#/hello') >= 0).length == 0) {
            throw 'Could not find an a-tag with class nav-link and href #/hello in the file index.html';
        }
        if (Array.from(d.querySelectorAll('script'))
                .map(el => el.src)
                .filter(src => src.indexOf('hello/hello.js') >= 0).length == 0) {
            throw 'Could not find a script tag with src hello/hello.js';
        }
    });
    var p2 = fetch('/index.js').then(response => response.text()).then(function(js) {
        eval(js);
        var hello = base.mainController.routingTable.hello;
        if (!hello || hello.partial !== 'hello/hello.html') {
            throw 'Routing information in index.js is not correct.';
        }
    });
    Promise.all([p1,p2]).then(function() {
        baseLab.complete('e2eTabRoute', true);
    }).catch(function(error) {
        if (error) {
            alert(error + '. Did you remember to restart the server?');
        }
        baseLab.complete('e2eTabRoute', false);
    });
};

var e2eTableValidate = function() {
    fetch('/foo/foo.html').then(response => response.text()).then(function(html) {
        var d = document.createElement('div');
        d.innerHTML = html;
        var ths = d.querySelectorAll('table tr th');
        var thsOk = ths.length == 3;
        if (thsOk) {
            var headerOk = ths[2].textContent.trim().toUpperCase() == 'TOTAL';
        } else {
            var headerOk = false;
        }
        var tdsOk = d.querySelector('#foo-template').content.querySelectorAll('td').length == 3;
        baseLab.complete('e2eTable', thsOk && headerOk && tdsOk);
    }).catch(function(error) {
        baseLab.complete('e2eTable', false);
    });
};