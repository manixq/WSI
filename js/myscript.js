"use strict";

/* global ko, $, document, self, alert */

var rootURL = "http://localhost:8080/";

    
 function preventNumberInput(e){
    e.preventDefault();
}


//-----------------REWORK--------------------
var StudentViewModel = function () {
    self.index = ko.observable();
    self.firstName = ko.observable();
    self.lastName = ko.observable();
    self.bornDate = ko.observable();
};

var ManageStudentsViewModel = ko.observableArray();

 self.filterGrades = ko.computed(function() 
{        

});




var SubjectsViewModelMapping = {
      
    create: function(options) {
            return new SubjectViewModel(options.data);
        },
/*
   'gradesList':{
       key: function(data){
           return ko.utils.unwrapObservable(data.id);
       }
   },
    
   'studentsList':{
       key: function(data){
           return ko.utils.unwrapObservable(data.index);
       }
   }*/
    
   
}




var SubjectViewModel = function(data) {
    ko.mapping.fromJS(data, {}, this);
     
    this.teacherFullname = ko.computed(function() {
        return this.teacherFirstname() + " " + this.teacherLastname();
    }, this);
}




function getFormData($form){
    var unindexed_array = $form.serializeArray();
    var indexed_array = {};

    $.map(unindexed_array, function(n, i){
        indexed_array[n['name']] = n['value'];
    });

    return indexed_array;
}

$( document ).ready(function() 
{
    self.studentsList = ko.observableArray();  
    self.subjectsList = ko.observableArray();  
    self.gradesList = ko.observableArray();     
    //self.filterArray = ko.observableArray();
    //self.selectedSubject = ko.observable(),
    
    self.GradesViewModel ={
        student : ko.observable(),
        selectedSubject : ko.observable(),
        gradesList : ko.observableArray(),
        subjectsList: ko.observableArray()
    }; 
    
    self.SubjectsViewModel ={
        subjectsList : self.subjectsList,
        activeSubject : ko.observable()
    }; 
     
    
    ko.applyBindings(self.studentsList, document.getElementById("ManageStudents"));
    ko.applyBindings(self.SubjectsViewModel, document.getElementById("ManageCourses"));
    //ko.applyBindings(self.gradesList, document.getElementById("ManageSeeAllGrades"));
    ko.applyBindings(self.GradesViewModel, document.getElementById("ManageSeeGrades"));
    
    document.getElementById("ManageStudentsButton").onclick =  function()
    {    
        console.log("Loading students model...");
        location.href = '#ManageStudents';
        
        SubjectsViewModel.activeSubject([]);
        self.AjaxGet('students', {}, self.studentsList);     
        
    }
    
    document.getElementById("ManageCoursesButton").onclick = function()
    {
        console.log("Loading courses model..");
        location.href = '#ManageCourses';
        
        self.AjaxGet('subjects', SubjectsViewModelMapping, self.subjectsList);
    }    
        
     document.getElementById("AddStudentButton").onclick = function()
     {
        var f = document.getElementById('AddStudentForm');
        if(f.checkValidity()) 
        {
            console.log("Adding student..");
            if(SubjectsViewModel.activeSubject().subjectName())
            { 
                console.log("Adding student to subject! ..");
                console.log(SubjectsViewModel.activeSubject().subjectName());
                self.AjaxPost('students/add?', $('#AddStudentForm').serialize()+"&subjectName="+SubjectsViewModel.activeSubject().subjectName());
            }
            else
            {                
               self.AjaxPost('students/add?', $('#AddStudentForm').serialize());
            }
            var lData = getFormData($('#AddStudentForm'));
            var lDate = new Date(lData.bornDate);
            self.studentsList.push(
            {
                index: ko.observable(lData.index),
                firstName: ko.observable(lData.firstName),
                lastName: ko.observable(lData.lastName),
                bornDate: ko.observable(lDate.toLocaleDateString("en-US"))
            });  
        }
    }  
     
     document.getElementById("AddSubjectButton").onclick = function()
    {
        var f = document.getElementById('AddSubjectForm');
        if(f.checkValidity()) 
        {
            console.log("Adding subject..");

            self.AjaxPost('subjects/add?', $('#AddSubjectForm').serialize());

            var data = getFormData($('#AddSubjectForm'));
            self.subjectsList.push(
            {
                subjectName: ko.observable(data.subjectName),
                teacherFirstName: ko.observable(data.teacherFirstName),
                teacherLastName: ko.observable(data.teacherLastName),
                teacherFullname: ko.observable(data.teacherFirstName + " " + data.teacherLastName)
            });  
        }
    }  
     
    $("#AddStudentButton").click(function() 
    {
        $(this).closest('form').find("input[type=text], textarea").val("");
        $(this).closest('form').find("input[type=number], textarea").val("");
        $(this).closest('form').find("input[type=date], textarea").val("");
    });
    
    $("#AddSubjectButton").click(function() 
    {
        $(this).closest('form').find("input[type=text], textarea").val("");
        $(this).closest('form').find("input[type=number], textarea").val("");
        $(this).closest('form').find("input[type=date], textarea").val("");
    });
    
    
     
    document.getElementById("AddGradeButton").onclick = function()
    {
        var f = document.getElementById('AddGradeForm');
        if(f.checkValidity() && self.GradesViewModel.selectedSubject()) 
        {
            console.log("Adding grade...");
            
            self.AjaxPost('grade/add?', $('#AddGradeForm').serialize()+"&index="+self.GradesViewModel.student().index()+"&subjectName="+self.GradesViewModel.selectedSubject().subjectName());
                        
            var data = getFormData($('#AddGradeForm'));
            data.gradeDate = new Date(Date.now()).toLocaleDateString();
            self.GradesViewModel.gradesList.push(
            {
                gradeDate: ko.observable(data.gradeDate),
                gradeValue: ko.observable(data.gradeValue)
            });  
        } 
    }    
     
    document.getElementById("SelectSubjectToGetGrades").onchange = function()
    {
        console.log(self.GradesViewModel.selectedSubject());
        if(self.GradesViewModel.selectedSubject())
        {
            console.log("Grade list updated..");      
            self.AjaxGet('subjects/' + self.GradesViewModel.selectedSubject().subjectName() + '/students/'+GradesViewModel.student().index() + '/grades', {}, GradesViewModel.gradesList, function()
            {
                GradesViewModel.gradesList().forEach(function(grade)
                {
                    console.log("binded");
                    //grade = ko.observable(grade);
                    grade.gradeValue.subscribe(function(oldValue) 
                    {
                        if(typeof grade.gradeValue["oldValue"] == 'undefined')
                            grade.gradeValue["oldValue"] = oldValue;
                        
                    }, null, "beforeChange");  
                });
                
            });              
            
        }
    }    
     
    
    
    self.filterSubjectGradesByStudent = function (student) {   
        console.log("Loading student grades..");
        GradesViewModel.student(student);
        self.AjaxGet('subjects', {}, GradesViewModel.subjectsList);
       
    }
     
    self.StudentsBySubject = function (subject) { 
        console.log("Loading students model..");
        location.href = '#ManageStudents';
        
        SubjectsViewModel.activeSubject(subject);
        self.AjaxGet('subjects/'+subject.subjectName()+'/students', {}, self.studentsList);
        
    }      
      
    self.EditGrade  = function(grade)
    {        
        console.log("Editing grade..");
        if(grade.gradeValue["oldValue"])
            {                
            console.log(grade.gradeValue["oldValue"]);
            self.AjaxPut("update/grade?", "gradeDate=" + grade.gradeDate() + "&gradeValue=" + grade.gradeValue["oldValue"] + "&gradeNewValue=" + grade.gradeValue());
            grade.gradeValue["oldValue"] = undefined;
            }
    }
    
    self.RemoveSubject =  function(subject)
    {    
        console.log("Deleting " + subject.subjectName());
        self.AjaxDelete('delete/subject?subjectName=', subject.subjectName());
        self.subjectsList.remove(subject);
    }
    
    self.RemoveStudent = function (student) 
    { 
        console.log("Deleting " + student.index());
        self.AjaxDelete('delete/student?index=', student.index());
        self.studentsList.remove(student);
    }
    
    self.RemoveGrade = function (grade) 
    { 
        console.log("Deleting grade " + grade.gradeValue());
        self.AjaxDelete('delete/grade?subjectName=' + self.GradesViewModel.selectedSubject().subjectName() + "&index=" +
        GradesViewModel.student.index()+"&gradeDate="+grade.gradeDate()+"&gradeValue=",grade.gradeValue());
        GradesViewModel.gradesList.remove(grade);
        
    }
    
    self.AjaxGet = function(path, viewModel, container, callbackF)
    {
        $.ajax({
            headers: { 
                //"Content-Type": "application/json",
                "Accept": "application/json",
            },
            type: 'GET',
            url: rootURL + path,
            crossDomain: true,
            dataType: 'json',
            success: function(data) 
            {         
                ko.mapping.fromJS(data, viewModel, container);
                //console.log(ko.toJS(data));
                if(callbackF)
                    callbackF();
            },        
            error: function(jqxhr, status, errorMsg) 
            {
                console.log('Cant get subjects! ' + errorMsg);
            }
        });
    }
    
    self.AjaxPut = function (path, data) 
    { 
                $.ajax({
                    headers: { 
                        "Content-Type": "application/json",
                        //"Accept": "application/json",
                    },
                    url: rootURL + path + data,
                    method: 'PUT',
                    datatype: "json",
                    success: function (data, status, xhr) {
                        //console.log(xhr.getResponseHeader('Location'));

                    },
                    error: function (data) {

                        console.log("failed");
                        console.log(data);

                    }
                });
    }
    
    
     self.AjaxPost = function (path, data) { 
                $.ajax({
                    headers: { 
                        "Content-Type": "application/json",
                        //"Accept": "application/json",
                    },
                    url: rootURL + path + data,
                    method: 'POST',
                    datatype: "json",
                    success: function (data, status, xhr) {
                        //console.log(xhr.getResponseHeader('Location'));
                        
                    },
                    error: function (data) {

                        console.log("failed");
                        console.log(data);

                    }
                });
    }
    
    self.AjaxDelete = function(path, element)
    {
        $.ajax(
        {
            headers: {                   
                "Content-Type": "application/json",
                //"Accept": "application/json",
            },
            url: rootURL + path + element,
            type: 'DELETE',
            success: function() 
            {
                console.log("Deleted " + element);
            },
            error: function(jqxhr, status, errorMsg) 
            {
                alert('Failed! ' + errorMsg);
            }
        });    
    }

});