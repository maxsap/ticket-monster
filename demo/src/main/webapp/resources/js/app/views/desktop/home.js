/**
 * The About view
 */
define([
    'utilities',
    'configuration',
    'text!../../../../templates/desktop/home.html'
], function (utilities, config, HomeTemplate) {

    var HomeView = Backbone.View.extend({
        render:function () {
            utilities.applyTemplate($(this.el),HomeTemplate,{});
            return this;
        }
    });

    return HomeView;
});