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

    e2eServer: true,
    e2eTable: true
};

document.addEventListener("DOMContentLoaded", function() {
    baseLab.init(answerSheet, null, 'lab2.html');
});

var testJavaScript = function(param1, param2) {alert(param1 + "" + param2);}

var systemBrowseValidate = function() {
    base.rest.getUser()
        .catch(error => baseLab.complete('systemBrowse', false))
        .then(function(user) {
            if (user.role !== 'None') {
                baseLab.complete('systemBrowse', true);
            } else {
                baseLab.complete('systemBrowse', false);
            }
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
        baseLab.complete('jsProperty', false);
        throw error;
    }
};

var jsDebuggingValidate = function(event) {
    event.preventDefault();
    var input = document.querySelector('#jsDebugging input');
    try {
        baseLab.complete('jsDebugging', input.value);
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
    baseLab.complete('htmlFormTryout');
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
};

var e2eServerValidate = function() {
    base.rest.getSimples().then(function(data) {
        if (data.error) {
            alert('Received error from server: ' + data.message);
            baseLab.complete('e2eServer', false);
        } else if (data.length == 0) {
            baseLab.complete('e2eServer', false);
            alert('No data on the user, please add some so the implementation can be verified.');
        } else if (typeof data[0].count === "undefined") {
            baseLab.complete('e2eServer', false);
            alert('Count not added, did you restart the server after completing the task?');
        } else if (typeof data[0].count !== "number") {
            baseLab.complete('e2eServer', false);
            alert('Wrong type of "count"? expected number but got ' + typeof data[0].count);
        } else {
            baseLab.complete('e2eServer', true);
        }
    }).catch(function(error) {
        baseLab.complete('e2eServer', false);
    });
};

var e2eTableValidate = function() {
    fetch('/simple/simple.html').then(response => response.text()).then(function(html) {
        var d = document.createElement('div');
        d.innerHTML = html;
        var ths = d.querySelectorAll('table tr th');
        var thsOk = ths.length == 3;
        if (thsOk) {
            var headerOk = ths[2].textContent.trim().toUpperCase() == 'COUNT';
        } else {
            var headerOk = false;
        }
        var tdsOk = d.querySelector('#simple-template').content.querySelectorAll('td').length == 3;
        baseLab.complete('e2eTable', thsOk && headerOk && tdsOk);
    }).catch(function(error) {
        baseLab.complete('e2eTable', false);
    });
};