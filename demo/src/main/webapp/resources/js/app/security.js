$.ajaxSetup({
	error : function(xhr, textStatus, errorThrown) {
		if (xhr.status == 401) {
			window.location = "#login";
		}
	}
});

function performLogin() {
	var username = $("#userName");
	var password = $("#password");

	if (username && password) {
		$.ajax({url: ("rest/login"),
			data:JSON.stringify({ userId: username.val(), password: password.val() }),
			type:"POST",
			dataType:"json",
			contentType:"application/json",
			success: function(user) {
				if (user.id) {
					window.history.back();
				} else {
					$("#error-signin").empty().append("Invalid credentials. Please try again.");
				}
			}}
		);
	}
}

function performLogout() {
	var contextPath = "";

	if (baseUrl) {
		contextPath = baseUrl + "/";
	}

	$.ajax({url: contextPath + "rest/logout",
		type:"POST",
		dataType:"html",
		success: function(context) {
			$("#userSection").hide();
			$("#logoutSection").hide();
			$("#adminSection").hide();
			$("#signInSection").show();
			window.location = "";
		}}
	);

    return false;
}

function performSignUp() {
	var firstName = $("#firstName");
	var lastName = $("#lastName");
	var email = $("#email");
	var password = $("#userPassword");
	var passwordConfirmation = $("#passwordConfirmation");

	if (firstName && lastName && email && password && passwordConfirmation) {
		$.ajax({url: ("rest/registration"),
			data:JSON.stringify({ firstName: firstName.val(), lastName: lastName.val(), email: email.val(), password: password.val(), passwordConfirmation: passwordConfirmation.val() }),
			type:"POST",
			dataType:"json",
			contentType:"application/json",
			success: function(context) {
				if (context.id) {
					window.location = "";
				} else {
					$("#error-signup").empty().append(context.message);
				}
			}}
		);
	}
}

function checkUserInfo() {
	$.ajax({url: ("rest/userinfo"),
		type:"POST",
		dataType:"json",
		contentType:"application/json",
		success: function(context) {
			if (context.user) {
				$("#signInSection").hide();
				$("#userLoggedInName").empty().append(context.user.firstName + " " + context.user.lastName);
				$("#userSection").show();
				$("#logoutSection").show();

				if (context.administrator) {
					$("#adminSection").show();
				}
			}
		}}
	);
}