"use strict";

/* global ko, $, document, self, alert */

var rootURL = "http://localhost:8080/";

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





var InitViewModel = function()
{
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
            console.log(self.students());   
            //ko.applyBindings(self.students);
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
                console.log(self.Subjects());     

                ko.applyBindings(self.Subjects); 
            },        
            error: function(jqxhr, status, errorMsg) 
            {
                alert('Failed! ' + errorMsg);
            }
        });
    });
    
    self.selectedSubject = ko.observable();
    self.selectedSubject.subscribe(function(selectedSubjectName)    
    {      
        self.filteredGrades = ko.toJS(ko.computed(function()
        {
            if(self.Subjects)//!self.selectedSubject())   
            {                
                self.allGrades = ko.observableArray();  
                ko.utils.arrayForEach(self.Subjects(), function(subject) 
                {
                    self.allGrades.push.apply(self.allGrades, subject.gradesList());
                });

                return allGrades;
            }
        }));
        
        console.log(self.filteredGrades);
        
     });
    
    
    
    
    self.selectedSubjectGrades = ko.observableArray();
    //self.selectedSubject = ko.observable($('#SelectSubjectToGetGrades'));
}
/*$(document).ready(function(){
    $('#SelectSubjectToGetGrades').change(function()
    {
        console.log(ko.toJS(self.selectedSubject.subjectName));
        $.ajax({
                headers: { 
                    "Content-Type": "application/json",
                    "Accept": "application/json",
                },
                type: 'GET',
                url: rootURL + 'subject/'+ko.toJS(self.selectedSubject).subjectName+'/grades',
                crossDomain: true,
                dataType: 'json',
                success: function(data) 
                {           
                    ko.observableArray(data, self.selectedSubjectGrades);
                    console.log(self.selectedSubjectGrades());    

                },        
                error: function(jqxhr, status, errorMsg) 
                {
                    alert('Failed! ' + errorMsg);
                }
            });

        console.log("clicked");   
    });
});*/


new InitViewModel();