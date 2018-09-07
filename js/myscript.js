"use strict";

var rootURL = "http://localhost:8080/";
function StudentList(data)
{
    var self = this;
    
}

//var students = [];

var Studenci = [];

var Student = function(data) {
    this.index = ko.observable(data.index);
    this.firstName = ko.observable(data.firstName);
    this.lastName = ko.observable(data.lastName);
    this.bornDate = ko.observable(data.bornDate);
 }

var Subject = function(data) {
    this.subjectName = ko.observable(data.subjectName);
 }

$('#btnGetStudents').click(function() {
	findStudents().val();
	return false;
});

var ViewModelMapping = {
   'gradesList':{
       key: function(data){
           return ko.utils.unwrapObservable(data.id);
       }
   },
    
   'studentsList':{
       key: function(data){
           return ko.utils.unwrapObservable(data.id);
       }
   }
}
var Subjects = ko.observableArray([]);
function LoadViewModel()
{
	console.log('LoadViewModel');
	$.ajax({
		headers: { 
			"Content-Type": "application/json",
			"Accept": "application/json",
		},
		type: 'GET',
		url: rootURL + 'subject',
		crossDomain: true,
		dataType: 'json',
		success: function(data) 
        {           
            self.Subjects = ko.mapping.fromJS(data, ViewModelMapping);
            console.log(data);
            console.log(self.Subjects());     
            
            ko.applyBindings(self.Subjects);  
		},        
        error: function(jqxhr, status, errorMsg) 
        {
			alert('Failed! ' + errorMsg);
		}
	});
}

function findStudents() 
{
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
            
            console.log(data);
            console.log(self.students);
            console.log(self.students());
            //var x = new StudentList(data);          
		},
        
        error: function(jqxhr, status, errorMsg) {
			alert('Failed! ' + errorMsg);
		}
	});
}
new LoadViewModel();
(new findStudents());