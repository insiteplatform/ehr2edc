(function () {
    if (Element.prototype.closest) {
        return;
    }

    if (!Element.prototype.matches) {
        Element.prototype.matches = Element.prototype.msMatchesSelector ||
            Element.prototype.webkitMatchesSelector;
    }

    const closest = function (s) {
        console.log("closest-polyfill");
        var el = this;

        do {
            if (el.matches(s)) return el;
            el = el.parentElement || el.parentNode;
        } while (el !== null && el.nodeType === 1);
        return null;
    };


    if (!Element.prototype.closest) {
        console.log("add closest-support");
        Element.prototype.closest = closest;
    }
})(this);