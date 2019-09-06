const scripts = [
    "https://cdnjs.cloudflare.com/ajax/libs/popper.js/1.12.9/umd/popper.min.js",
    "https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js",
    "prism.js"
];

const baseLab = {
    init: function(answerSheet, previousUrl, nextUrl) {
        this.answerSheet = answerSheet;
        fetch('header.html')
            .then(response => response.text())
            .then(function(headerHTML) {
                // Add nav header
                const target = document.querySelector('#header');
                target.innerHTML = headerHTML;

                // Load dependencies
                scripts.forEach(function(src) {
                    const s = document.createElement('script');
                    s.src = src;
                    s.type = 'application/javascript';
                    document.head.appendChild(s);
                });

                // Initialize error counter
                if (localStorage.getItem('errors') == null) {
                    localStorage.setItem('errors', 0);
                }

                // Complete previously completed tasks
                document.querySelectorAll('.lab-task-group').forEach(function(task) {
                    const id = task.getAttribute('id');
                    if (localStorage.getItem(id) == "true") {
                        baseLab.complete(id, baseLab.answerSheet[id]);
                    }
                });

                // Set links in dropdown
                document.querySelectorAll('a.anchor:not([name="Top"])').forEach(function(mainAnchor, i) {
                    function fixLink(prefix, anchor, hx, ix) {
                        const section = anchor.parentElement;
                        const name = anchor.getAttribute('name');
                        const t = document.getElementById('menu-template');
                        const a = t.content.querySelector('a');
                        const heading = prefix + (ix+1) + ". " + section.querySelector(hx).textContent;
                        a.href = '#' + name;
                        a.textContent = heading;
                        if (prefix !== "") {
                            a.style['padding-left'] = '3em';
                        } else {
                            a.style['padding-left'] = '';
                        }
                        section.querySelector(hx).textContent = heading;
                        const clone = document.importNode(t.content, true);
                        const nextLabRef = t.parentElement.querySelector('div.dropdown-divider:last-of-type');
                        t.parentElement.insertBefore(clone, nextLabRef);
                    }
                    fixLink('', mainAnchor, 'h1', i);
                    mainAnchor.parentElement
                        .querySelectorAll('a.sub-anchor').forEach((sub, j) => fixLink((i+1)+".", sub, 'h3', j));
                });
                if (previousUrl) {
                    document.querySelector('#previousLab').href = previousUrl;
                } else {
                    document.querySelector('#previousLab').style.display = 'none';
                }
                if (nextUrl) {
                    document.querySelector('#nextLab').href = nextUrl;
                } else {
                    document.querySelector('#nextLab').style.display = 'none';
                }
            });
    },
    updateProgress: function() {
        const totalTasks = document.querySelectorAll('.lab-task-group').length;
        const completed = document.querySelectorAll('.lab-task-group.done').length;
        const p = Math.round(100 * completed / totalTasks);

        const prog = document.querySelector('#lab-progress .progress-bar');
        prog.textContent = p + '%';
        prog.style.width = p + '%';

        // set current link
        if (totalTasks == completed) {
            document.getElementById('current').textContent = 'All done!';
        }
        const score = document.getElementById('score');
        if (score) {
            score.textContent = localStorage.getItem('errors');
        }
    },
    complete: function(taskId, value) {
        const t = document.getElementById(taskId)
        if (typeof this.answerSheet[taskId] !== "undefined") {
            if (this.answerSheet[taskId] !== value) {
                t.classList.add('error');
                const errors = parseInt(localStorage.getItem('errors'));
                localStorage.setItem('errors', errors+1);
                baseLab.updateProgress();
                return false;
            }
        }
        localStorage.setItem(taskId, true);
        t.classList.add('done');
        t.classList.remove('alert-warning');
        t.classList.add('alert-success');
        t.querySelectorAll('.lab-task-form input').forEach(i => {i.disabled = true});
        t.querySelectorAll('.lab-task-form button').forEach(b => {b.style.display = 'none'});
        baseLab.updateProgress();
        return true;
    },
    clearAll: function() {
        Array.from(document.querySelectorAll('.lab-task-group'))
            .map(function(t) {
                t.classList.remove('done');
                t.classList.remove('error');
                t.classList.add('alert-warning');
                t.classList.remove('alert-success');
                t.querySelectorAll('.lab-task-form input').forEach(i => {i.disabled = false});
                t.querySelectorAll('.lab-task-form button').forEach(b => {b.style.display = ''});
                return t.getAttribute('id');
            });
        localStorage.clear();
        localStorage.setItem('errors', 0);
        baseLab.updateProgress();
        document.getElementById('current').textContent = 'Next task';
    },
    scrollToNext: function() {
        const next = document.querySelector('.lab-task-group:not(.done)');
        if (!next) return;
        const anchor = next.parentElement.querySelector('a.sub-anchor') || next.parentElement.querySelector('a.anchor');
        if (anchor) {
            history.pushState(null, null, '#'+anchor.getAttribute('name'));
        }
        next.scrollIntoView();
        if (Math.abs(window.scrollY - window.scrollMaxY) > 1) {
            window.scrollBy(0, -75); // Adjust for fixed navbar
        }
    }
};
