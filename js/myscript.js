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
    self.gradeStudentFilter = ko.observableArray();
    self.selectedSubject = ko.observable(); 
    self.gradeSubjectFilter = ko.observableArray();
    
    //subject
    self.inputSubjectName = ko.observable(); 
    self.inputTeacherFirstName = ko.observable(); 
    self.inputTeacherLastName = ko.observable(); 
    
    //student
    self.inputIndex = ko.observable();
    self.inputStudentFirstName = ko.observable();
    self.inputStudentLastName = ko.observable();
    self.inputBornDate = ko.observable();
    
    
        $.ajax({
            headers: { 
                //"Content-Type": "application/json",
                "Accept": "application/json",
            },
            type: 'GET',
            url: rootURL + 'students',
            crossDomain: true,
            dataType: 'json',
            success: function(data) { 

            self.students = ko.observableArray(data);  
            console.log(self.students());
            },

            error: function(jqxhr, status, errorMsg) {
                console.log('Cant get students! ' + errorMsg);
            }
        });

        $.ajax({
            headers: { 
                //"Content-Type": "application/json",
                "Accept": "application/json",
            },
            type: 'GET',
            url: rootURL + 'subjects',
            crossDomain: true,
            dataType: 'json',
            success: function(data) 
            {           
                self.Subjects = ko.mapping.fromJS(data, ViewModelMapping);
                
                ko.applyBindings(self.Subjects); 
            },        
            error: function(jqxhr, status, errorMsg) 
            {
                console.log('Cant get subjects! ' + errorMsg);
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
                    if(grade.referencedStudent.index)
                    {
                        console.log(grade.referencedStudent.index());     
                        return grade.referencedStudent.index() == self.gradeStudentFilter().index;
                    }
                });
            }
        }        
    });
    
    self.AddStudent = function () { 
        if(self.inputIndex && self.inputStudentFirstName && self.inputStudentLastName && self.inputBornDate)
            {
                $.ajax({
                    headers: { 
                        "Content-Type": "application/json",
                        //"Accept": "application/json",
                    },
                    url: rootURL + 'student/add',
                    method: 'POST',
                    data: JSON.stringify({
                        index: self.inputIndex(),                   
                        firstName: self.inputStudentFirstName(),        
                        lastName: self.inputStudentLastName(),   
                        bornDate: self.inputBornDate()
                    }),
                    datatype: "json",
                    success: function (data) {
                        console.log(data);
                        self.students.push({
                            index: self.inputSubjectName(),                   
                            firstName: self.inputTeacherFirstName(),        
                            lastName: self.inputTeacherLastName(),    
                            bornDate: self.inputBornDate()                            
                        });


                    },
                    error: function (data) {

                        console.log("failed");
                        console.log(data);

                    }
                });
            }
    }
    
    self.AddSubject = function () { 
        if(self.inputSubjectName() && self.inputTeacherFirstName() && self.inputTeacherLastName())
            {
                $.ajax({
                    headers: { 
                        "Content-Type": "application/json",
                        //"Accept": "application/json",
                    },
                    url: rootURL + 'Subject/add',
                    method: 'POST',
                    data: JSON.stringify({
                        subjectName: self.inputSubjectName(),                   
                        teacherFirstname: self.inputTeacherFirstName(),        
                        teacherLastname: self.inputTeacherLastName(),   
                        gradesList: [],
                        studentsList: []
                    }),
                    datatype: "json",
                    success: function (data) {
                        console.log(data);
                        self.Subjects.push({
                            subjectName: self.inputSubjectName(),                   
                            teacherFirstname: self.inputTeacherFirstName(),        
                            teacherLastname: self.inputTeacherLastName(),    
                            teacherFullname: inputTeacherFirstName() + " " + inputTeacherLastName(),
                            gradesList: [],
                            studentsList: []
                            
                        });


                    },
                    error: function (data) {

                        console.log("failed");
                        console.log(data);

                    }
                });
            }
    }
    
    self.filterByStudent = function (student) {        
        self.gradeStudentFilter(student);
    }
    
     self.filterBySubject = function (subject) {          
        self.gradeSubjectFilter(subject);
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
          
      self.RemoveGrade = function (grade, subject) 
      { 
          console.log(grade);
          $.ajax(
          {
              headers: {                   
                "Content-Type": "application/json",
                //"Accept": "application/json",
                },
                url: rootURL + 'delete/grade?id=' + grade.id(),
                type: 'DELETE',
                success: function() 
                {                       
                    subject.gradesList.remove(grade);
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