/*
 * Utility functions for specs.
 * Author: Rasmus Ros, rasmus.ros@cs.lth.se
 */
var specHelper = (function() {
    // Fetch a HTML file and add it to a new div, calling 'done' when finished. Done is obtained from jasmine to
    // block until completion.
    var fetchHtml = function(url, el) {
        var node = document.createElement('div');
        el.appendChild(node);
        return fetch(url).then(response => response.text())
            .then(function(text) {
                node.innerHTML = text;
                return node;
            });
    };

    return {fetchHtml};
})();