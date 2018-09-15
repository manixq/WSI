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
    self.gradeSubjectFilter = ko.observable();
    
    
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
                console.log(self.students());
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
    
     
    
    
    self.filterGrades = ko.computed(function() 
    {        
        if(self.selectedSubject())
        {
            if(!self.gradeStudentFilter()) 
            {                                
                return self.selectedSubject().gradesList; 
            } 
            else 
            {
                return ko.utils.arrayFilter(self.selectedSubject().gradesList(), function(grade) 
                {             
                    console.log(grade);       
                    return grade.referencedStudent.index() == self.gradeStudentFilter();
                });
            }
        }        
    });
    
    self.filterByStudent = function (student) {        
        self.gradeStudentFilter(student.index);
    }
    
     self.filterBySubject = function (subject) { 
         
         console.log(subject);
         console.log(subject.gradesList());
        self.gradeSubjectFilter(subject.gradesList());
    }
     
      self.RemoveSubject = function (subject) 
      { 
          $.ajax(
          {
              headers: {                   
                "Content-Type": "application/json",
                //"Accept": "application/json",
                },
                url: rootURL + 'delete/subject?id=' + subject.id(),
                type: 'DELETE',
                success: function() 
                {
                    self.Subjects.remove(subject);
                },
                error: function(jqxhr, status, errorMsg) 
                {
                    alert('Failed! ' + errorMsg);
                }
            });          
    }
      
      self.RemoveStudent = function (student) 
      { 
          $.ajax(
          {
              headers: {                   
                "Content-Type": "application/json",
                //"Accept": "application/json",
                },
                url: rootURL + 'delete/student?index=' + student.index,
                type: 'DELETE',
                success: function() 
                {                    
                    self.students.remove(student);
                },
                error: function(jqxhr, status, errorMsg) 
                {
                    alert('Failed! ' + errorMsg);
                }
            });
          
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




$( document ).ready(function() { new InitViewModel()});