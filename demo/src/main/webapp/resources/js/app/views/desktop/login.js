define([
    'utilities',
    'configuration',
    'text!../../../../templates/desktop/login.html'
], function (
    utilities,
    config,
    securityTemplate) {

    var LoginView = Backbone.View.extend({
        events:{
            "click input[name='signIn']":"signIn",
            "click input[name='signUp']":"signUp",
        },
    	render:function () {
    		var self = this;
    		utilities.applyTemplate($(this.el), securityTemplate, {});
            return this;
        },
        signIn:function (event) {
        	performLogin();
        },
        signUp:function (event) {
        	performSignUp();
        }
    });

    return LoginView;
});