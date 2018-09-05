"use strict";

var rootURL = "http://localhost:8080/";


var myObservableArray = ko.observableArray();
var UsersList = ko.observableArray([]);
//var viewModel = ko.mapping.fromJSON(data);


$('#btnGetStudents').click(function() {
	findStudents().val();
	return false;
});

function findStudents() {
	console.log('findStudents');
	$.ajax({
		headers: { 
			"Content-Type": "application/json",
			"Accept": "application/json",
		},
		type: 'GET',
		url: rootURL + 'student',
		crossDomain: true,
		dataType: 'json',
		success: function() { alert("Success"); },
        error: function(jqxhr, status, errorMsg) {
			alert('Failed! ' + errorMsg + status);
		}
	});
}

function UserDetailViewModel(data){
	var self = this;
   self.imie= ko.observable(data.imie);
}


ko.applyBindings(new findStudents());