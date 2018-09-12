"use strict";

/* global ko, $, document, self, alert */

var rootURL = "http://localhost:8080/";


var ViewModelMapping = {
      
    create: function(options) {
            return new viewModel(options.data);
        },

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

var viewModel = function(data) {
    ko.mapping.fromJS(data, {}, this);
     
    this.teacherFullname = ko.computed(function() {
        return this.teacherFirstname() + " " + this.teacherLastname();
    }, this);
}

var gradesModel = function(data) {
    ko.mapping.fromJS(data, {}, this);
     
    this.teacherFullname = ko.computed(function() {
        return this.teacherFirstname() + " " + this.teacherLastname();
    }, this);
}




var InitViewModel = function()
{
    self.gradeStudentFilter = ko.observable();
    self.selectedSubject = ko.observable(); 
    
    $( document ).ready(function() {
        $.ajax({
            headers: { 
                //"Content-Type": "application/json",
                "Accept": "application/json",
            },
            type: 'GET',
            url: rootURL + 'student',
            crossDomain: true,
            dataType: 'json',
            success: function(data) { 

            self.students = ko.observableArray(data);     
            },

            error: function(jqxhr, status, errorMsg) {
                alert('Failed! ' + errorMsg);
            }
        });

        $.ajax({
            headers: { 
                //"Content-Type": "application/json",
                "Accept": "application/json",
            },
            type: 'GET',
            url: rootURL + 'subject',
            crossDomain: true,
            dataType: 'json',
            success: function(data) 
            {           
                self.Subjects = ko.mapping.fromJS(data, ViewModelMapping);
                
                ko.applyBindings(self.Subjects); 
            },        
            error: function(jqxhr, status, errorMsg) 
            {
                alert('Failed! ' + errorMsg);
            }
        });
    });
    
     
    
    
    self.filterGrades = ko.computed(function() 
    {        
        if(self.selectedSubject())
        {
            console.log("there is selected subject");
            if(!self.gradeStudentFilter()) 
            {
                console.log("no student filter");
                                
                return self.selectedSubject().gradesList; 
            } 
            else 
            {
                
                console.log("there is student filter");
                return ko.utils.arrayFilter(self.selectedSubject().gradesList(), function(grade) 
                {             
                    console.log(grade);       
                    return grade.referencedStudent.index() == self.gradeStudentFilter();
                });
            }
        }        
    });
    
    self.filter = function (student) {        
        self.gradeStudentFilter(student.index);
    }
    
  /*  self.selectedSubject.subscribe(function(selectedSubject)   
    {      
        self.filteredGrades = ko.toJS(ko.computed(function()
        {
            self.allGrades = ko.observableArray(); 
            if(!self.selectedSubject())   
            {                 
                ko.utils.arrayForEach(self.Subjects(), function(subject) 
                {
                    self.allGrades.push.apply(self.allGrades, subject.gradesList());
                });

                return allGrades;
            }
                       
            return self.selectedSubject().gradesList();
        }));
     });*/
    
    
    
    
    //self.selectedSubjectGrades = ko.observableArray();
    //self.selectedSubject = ko.observable($('#SelectSubjectToGetGrades'));
}




new InitViewModel();