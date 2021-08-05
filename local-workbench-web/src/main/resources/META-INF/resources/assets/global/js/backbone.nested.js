//
//  Backbone.nested.js 1.0.0
//
//  (c) 2013 Eric Renault
//

// Initial Setup
// --------------
(function() {
    "use strict";

    var root = this;

    var _, Backbone;

    if (typeof exports !== 'undefined') {
        _ = require('underscore');
        Backbone = require('backbone');
        if (typeof module !== 'undefined' && module.exports) {
            module.exports = Backbone;
        }
        exports = Backbone;
    } else {
        _ = root._;
        Backbone = root.Backbone;
    }

    var backboneModel_set = Backbone.Model.prototype.set;

    // Model overriding
    Backbone.Model = Backbone.Model.extend({
        set: function(key, val, options) {
            // formatting parameters to the attributes + options parameters only case
            var attributes, attrKey, attrValue;
            if (_.isObject(key) || key === null) {
                attributes = key;
                options = val;
            } else {
                attributes = {};
                attributes[key] = val;
            }
            // checks if a nested attribute was added to the model definition
            if (this.nested) {
                // checks if the nested items have been initialized
                if (this.nested_init) {
                    // Already been initialized, looping through the nested items
                    for (attrKey in this.nested) {
                        // Checks if there are parameters passed regarding the current nested item
                        if (attributes.hasOwnProperty(attrKey)) {
                            // Uses the set method directly with the attributes
                            this.get(attrKey).set(attributes[attrKey], _.clone(options));
                            // Remove the attributes from the parameters tree, so that Backbone doesn't override the item with a simple json object.
                            delete attributes[attrKey];
                        }
                    }
                } else {
                    // It's the initial set called from object creation. The attributes are the merging of defaults and custom data -if any passed-
                    this.nested_init = true;
                    // Loop through the nested items and initialize them
                    for (attrKey in this.nested) {
                        // Retrieves the nested object prototype
                        attrValue = this.nested[attrKey];
                        // And create a new instance of the nested object prototype, with the parameters assigned to it. 
                        // If none, it will be initialized with a null paremeter, therefore using the defaults values.
                        attributes[attrKey] = new attrValue(attributes[attrKey], _.clone(options));
                    }
                }
            }
            // Execute the normal Backbone setting method, with the remaining parameters
            return backboneModel_set.call(this, attributes, options);
        }
    });

}).call(this);