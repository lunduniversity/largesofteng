const answerSheet = {
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

const testJavaScript = function(param1, param2) {alert(param1 + param2);}

const systemBrowseValidate = function() {
    base.rest.getUser()
        .catch(error => baseLab.complete('systemBrowse', false))
        .then(function(user) {
            baseLab.complete('systemBrowse', !user.isNone());
        });
};

const jsPropertyValidate = function(submitEvent) {
    submitEvent.preventDefault();
    const input = document.querySelector('#jsProperty input').value;
    try {
        const ok = eval('(' + input + ')').fruit;
        baseLab.complete('jsProperty', ok);
        return false;
    } catch(error) {
        alert(error);
        baseLab.complete('jsProperty', false);
        throw error;
    }
};

const jsDebuggingValidate = function(event) {
    event.preventDefault();
    const input = document.querySelector('#jsDebugging input');
    try {
        baseLab.complete('jsDebugging', input.value.replace(/['"]+/g, ''));
    } catch (error) {
        alert(error);
        baseLab.complete('jsDebugging', false);
        throw error;
    }
    return false;
};

const htmlDOMDemo = function() {
    const div = document.createElement('div');
    div.innerHTML = '<b>your browser will parse this and create HTML elements</b>';
    const target = document.getElementById('putItHere');
    target.appendChild(div);
};

const htmlHeaderValidate = function(submitEvent) {
    submitEvent.preventDefault();
    const d = document.getElementById('htmlHeaderResult');
    const r = document.getElementById('htmlHeaderInput');
    d.innerHTML = r.value;
    if (d.innerHTML.toLowerCase() != r.value.trim().toLowerCase()) {
        baseLab.complete('htmlHeader', false);
        return false;
    }
    const h1 = d.querySelector('h1');
    if (!h1) {
        baseLab.complete('htmlHeader', false);
    } else if (h1.textContent && h1.textContent.length > 0) {
        baseLab.complete('htmlHeader', true);
    }
    return false;
};

const htmlFormTryoutValidate = function(submitEvent) {
    submitEvent.preventDefault();
    if (localStorage.getItem('htmlFormTryout') !== 'true') {
        baseLab.complete('htmlFormTryout');
    }
    return false;
}

const cssSelectorValidate = function(submitEvent) {
    submitEvent.preventDefault();
    const sel = document.querySelector('#cssSelector input').value;
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

const e2eServerValidate = function() {
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

const e2eTabHtmlValidate = function() {
    const p1 = fetch('/hello/hello.html').then(function(response) {
        if (response.ok) {
            return response.text();
        } else {
            throw 'Could not find the HTML file /hello/hello.html';
        }
    }).then(function(html) {
        const d = document.createElement('div');
        d.innerHTML = html;
        const h = d.querySelector('h1');
        if (!h || h.textContent === '') {
            throw 'Could not find a h1 tag with text';
        }
    });
    const p2 = fetch('/hello/hello.js').then(function(response) {
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

const e2eTabRouteValidate = function() {
    const p1 = fetch('/index.html').then(response => response.text()).then(function(html) {
        const d = document.createElement('div');
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
    const p2 = fetch('/index.js').then(response => response.text()).then(function(js) {
        eval(js);
        const hello = base.mainController.routingTable.hello;
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

const e2eTableValidate = function() {
    fetch('/foo/foo.html').then(response => response.text()).then(function(html) {
        const d = document.createElement('div');
        d.innerHTML = html;
        const ths = d.querySelectorAll('table tr th');
        const thsOk = ths.length == 3;
        let headerOk;
        if (thsOk) {
            headerOk = ths[2].textContent.trim().toUpperCase() == 'TOTAL';
        } else {
            headerOk = false;
        }
        const tdsOk = d.querySelector('#foo-template').content.querySelectorAll('td').length == 3;
        let msg = '';
        if (!headerOk) {
            msg += 'Table header does not contain total. ';
        }
        if (!thsOk) {
            msg += 'Number <th> elements not correct. ';
        }
        if (!tdsOk) {
            msg += 'Number <th> elements not correct.';
        }
        if (msg) throw Error(msg);
        baseLab.complete('e2eTable', true);
    }).catch(function(error) {
        alert(error);
        baseLab.complete('e2eTable', false);
    });
};