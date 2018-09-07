"use strict";

var rootURL = "http://localhost:8080/";
function StudentList(data)
{
    var self = this;
    
}

//var students = [];

var Studenci = [];

var Student = function(data) {
    this.imie = ko.observable(data.imie);
    this.nazwisko = ko.observable(data.nazwisko);
 }

$('#btnGetStudents').click(function() {
	findStudents().val();
	return false;
});

function findStudents() {

    self.customerList = ko.observableArray();
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
		success: function(data) { 
            
        self.students = ko.observableArray(data);
           //ko.mapping.fromJS(data, self.students);
            //ko.utils.arrayForEach(data, function(item) {
            //students.push(new Student(item));
        //});
           // self.neuelist = new StudentList(data);
            //console.log(students);
            console.log(data);
            console.log(self.students());
            //var x = new StudentList(data);
            ko.applyBindings(self.students);            
		},
        
        error: function(jqxhr, status, errorMsg) {
			alert('Failed! ' + errorMsg);
		}
	});
}

(new findStudents());