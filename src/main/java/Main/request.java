package Main;


import ModelDanych.Grade;
import ModelDanych.Subject;
import ModelDanych.Student;
import com.mongodb.BasicDBObject;
import org.bson.types.ObjectId;
import org.mongodb.morphia.Key;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.util.*;

@Path("/")
public class request
{
    public request()
    {

    }

    @GET
    @Path("/students")
    @Produces({"application/xml", "application/json"})
    public List<Student> getStudent(@DefaultValue("") @QueryParam("firstName") String firstName, @DefaultValue("") @QueryParam("lastName") String lastName)
    {
        final Query<Student> query = Main.datastore.createQuery(Student.class);

        if(firstName != "" && lastName != "")
        {
           query.and(query.criteria("firstName").containsIgnoreCase(firstName),
                            query.criteria("lastName").containsIgnoreCase(lastName));
           return query.asList();

        }
        else if(firstName != "") {
            return query.field("firstName").equal(firstName).asList();
        }

        else if(lastName != "")
        {
            return query.field("lastName").equal(lastName).asList();
        }


        return query.asList();
    }

    @GET
    @Path("/student/FilterByDate")
    @Produces({"application/xml", "application/json"})
    public List<Student> getStudentFilterByDate(@DefaultValue("") @QueryParam("in") String in, @DefaultValue("") @QueryParam("after") String after, @DefaultValue("") @QueryParam("before") String before)
    {
        final Query<Student> query = Main.datastore.createQuery(Student.class);

        if(in != "")
        {
            return query.field("Data").equal(in).asList();
        }
        else if(after != "") {
            return query.field("Data").greaterThan(after).asList();
        }

        else if(before != "")
        {
            return query.field("Data").lessThan(before).asList();
        }


        return query.asList();
    }

    @GET
    @Path("/student/FilterByRating")
    @Produces({"application/xml", "application/json"})
    public List<Grade> getStudentFilterByRating(@DefaultValue("") @QueryParam("index") String index, @DefaultValue("") @QueryParam("subject") String subject, @DefaultValue("") @QueryParam("above") String above, @DefaultValue("") @QueryParam("below") String below)
    {
        final Query<Grade> gradeQuery = Main.datastore.createQuery(Grade.class);

        final Query<Student> studentQuery = Main.datastore.createQuery(Student.class);
        if(index != "")
        {
            Student student = studentQuery.field("index").equal(index).get();

            if (subject != "")
            {
                if (above != "")
                {
                    gradeQuery.and(gradeQuery.criteria("gradeValue").greaterThan(above),
                            gradeQuery.criteria("referencedStudent").equal(student));
                    return gradeQuery.asList();
                }
                else if (below != "")
                {
                    gradeQuery.and(gradeQuery.criteria("gradeValue").lessThan(below),
                        gradeQuery.criteria("referencedStudent").equal(student));
                    return gradeQuery.asList();
                }


                return gradeQuery.field("referencedStudent").equal(student).asList();
            }
        }

        return null;
    }

    @GET
    @Path("/subjects")
    @Produces({"application/xml", "application/json"})
    public List<Subject> getSubject(@DefaultValue("") @QueryParam("teacherFirstname") String teacherFirstname, @DefaultValue("") @QueryParam("teacherLastname") String teacherLastname, @DefaultValue("") @QueryParam("subjectName") String subjectName)
    {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);
        if(!teacherFirstname.isEmpty())
        {
            query.field("teacherFirstname").equal(teacherFirstname);

            if(!teacherLastname.isEmpty())
            {
                return query.field("teacherLastname").equal(teacherLastname).asList();
            }
            else
            {
                return query.asList();
            }
        }
        else if(!teacherLastname.isEmpty())
        {
            return query.field("teacherLastname").equal(teacherLastname).asList();
        }

        if(!subjectName.isEmpty())
        {
            return query.field("subjectName").equal(subjectName).asList();
        }

        return query.asList();
    }

    @GET
    @Path("/subject/{subject}/grades")
    @Produces({"application/xml", "application/json"})
    public List<Grade> getSubjectGrades(@PathParam("subject") String subjectName)
    {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);

        Subject query_subject =  query.field("subjectName").equal(subjectName).get();
        return query_subject.getGradesList();
    }

    @GET
    @Path("/subject/{subject}/students")
    @Produces({"application/xml", "application/json"})
    public List<Student> getSubjectStudents(@PathParam("subject") String subjectName)
    {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);

        Subject query_subject =  query.field("subjectName").equal(subjectName).get();
        return query_subject.getStudentsList();
    }

    @GET
    @Path("/student/{studentId}/{subject}/grades")
    @Produces({"application/xml", "application/json"})
    public List<Grade> getStudentGradesFromSubject(@PathParam("subject") String subjectName, @PathParam("studentId") long studentId)
    {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);
        final Query<Student> query2 = Main.datastore.createQuery(Student.class);

        Subject query_subject =  query.field("subjectName").equal(subjectName).get();
        Student query_student = query2.field("index").equal(studentId).get();
        //return Response.status(200).entity("Response studentId").build();
        return query_subject.getGradeByStudent(query_student.getIndex());
    }

    @GET
    @Path("/student/{studentId}/subjects")
    @Produces({"application/xml", "application/json"})
    public List<Subject> getSubjectsByStudent(@PathParam("studentId") long studentId)
    {
        List<Subject> outSubjects = new ArrayList<Subject>();
        List<Subject> subjectsList = Main.datastore.createQuery(Subject.class).asList();
        final Query<Student> query2 = Main.datastore.createQuery(Student.class);
        Student query_student = query2.field("index").equal(studentId).get();
        for (Subject lSubject : subjectsList)
        {
            if(lSubject.getStudentsList().contains(query_student))
            {
                outSubjects.add(lSubject);
            }
        }
        return outSubjects;
        //return Response.status(200).entity("Response studentId").build();
    }

    @POST
    @Path("/student/add")
    @Consumes({"application/xml", "application/json"})
    //@Produces({"application/xml", "application/json"})
    public Response postStudent(Student student) {
        Main.datastore.save(student);
        return Response.status(201).entity("Added").build();
    }


    @POST
    @Path("/Subject/add")
    @Consumes({"application/xml", "application/json"})
    //@Produces({"application/xml", "application/json"})
    public Response postSubject(Subject subject)
    {
        Main.datastore.save(subject);

        return Response.status(201).entity("Added " + subject.getSubjectName()).build();
    }

    @POST
    @Path("/Grade/add")
    @Consumes({"application/xml", "application/json"})
    //@Produces({"application/xml", "application/json"})
    public Response postGrade() {
        Main.datastore.save(new Grade(2.0, new Date(), null));

        return Response.status(201).entity("Added new grade").build();
    }

    @DELETE
    @Path("/delete/student")
    public Response deleteStudenci(@DefaultValue("") @QueryParam("index") String index) {
        final Query<Student> query = Main.datastore.createQuery(Student.class);
        final Query<Subject> queryRef;
        if(!index.isEmpty())
        {
            query.disableValidation().field("index").equal(Long.parseLong(index,10));
            queryRef = Main.datastore.createQuery(Subject.class).field("studentsList").hasThisElement(query.get());
            for (Subject s:queryRef.asList()) {
                for(int i = s.getStudentsList().size()-1; i >= 0; i--)
                {
                    if(s.getStudentsList().get(i).getIndex().equals(query.get().getIndex()))
                    {
                        s.getStudentsList().remove(i);
                    }
                }
                for(int i = s.getGradesList().size()-1; i >= 0; i--)
                {
                    if(s.getGradesList().get(i).getReferencedStudent().getIndex().equals(query.get().getIndex()))
                    {
                        s.getGradesList().remove(i);
                    }
                }
            }

            Main.datastore.save(queryRef.asList());
            Main.datastore.delete(query.get());

            return Response.status(200).build();

        }
        return Response.status(404).build();
    }

    @DELETE
    @Path("/delete/subject")
    public Response deleteSubject(@DefaultValue("") @QueryParam("id") String id) {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);

        if(id != "")
        {
            query.field("id").equal(new ObjectId(id));
            Main.datastore.delete(query);
            return Response.status(200).build();

        }
        return Response.status(404).build();
    }

    @DELETE
    @Path("/delete/grade")
    public Response deleteGrade(@DefaultValue("") @QueryParam("id") String id) {
        final Query<Grade> query = Main.datastore.createQuery(Grade.class);
        final Query<Subject> queryRef;

        if(!id.isEmpty())
        {
            query.field("id").equal(new ObjectId(id));
            queryRef = Main.datastore.createQuery(Subject.class).field("gradesList").hasThisElement(query.get());
            for (Subject s:queryRef.asList()) {
                for(int i = s.getGradesList().size()-1; i >= 0; i--)
                {
                    if(s.getGradesList().get(i).getId().equals(query.get().getId()))
                    {
                        s.getGradesList().remove(i);
                    }
                }
            }

            Main.datastore.save(queryRef.asList());
            Main.datastore.delete(query);
            return Response.status(200).build();

        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/student/index")
    public Response putStudentIndex(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("index") String index)
    {
        if(!id.isEmpty()) {
            final Query<Student> query = Main.datastore.createQuery(Student.class);
            query.field("id").equal(id);
            final UpdateOperations<Student> updateOperations = Main.datastore.createUpdateOperations(Student.class).set("index", Long.parseLong(index, 10));
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/student")
    public Response putStudent(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("firstName") String firstName, @DefaultValue("") @QueryParam("lastName") String lastName, @DefaultValue("") @QueryParam("bornDate") String bornDate)
    {
        if(!id.isEmpty()) {
            final Query<Student> query = Main.datastore.createQuery(Student.class);
            query.field("id").equal(id);
            if(!firstName.isEmpty())
            {
                final UpdateOperations<Student> updateOperationsFirstname = Main.datastore.createUpdateOperations(Student.class).set("imie", firstName);
                final UpdateResults resultsFirstname = Main.datastore.update(query, updateOperationsFirstname);
            }

            if(!lastName.isEmpty())
            {
                final UpdateOperations<Student> updateOperationsLastname = Main.datastore.createUpdateOperations(Student.class).set("lastName", lastName);
                final UpdateResults resultsLastname = Main.datastore.update(query, updateOperationsLastname);
            }
            if(!bornDate.isEmpty())
            {
                final UpdateOperations<Student> updateOperationsBornDate = Main.datastore.createUpdateOperations(Student.class).set("bornDate", lastName);
                final UpdateResults resultsBornDate = Main.datastore.update(query, updateOperationsBornDate);
            }

            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
/*
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/student/nazwisko")
    public Response putStudentNazwisko(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("nazwisko") String nazwisko)
    {
        if(id != "") {
            final Query<Student> query = Main.datastore.createQuery(Student.class);
            query.field("id").equal(id);
            final UpdateOperations<Student> updateOperations = Main.datastore.createUpdateOperations(Student.class).set("nazwisko", nazwisko);
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("aktualizuj/student/data")
    public Response putStudentData(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("data") String data)
    {
        if(id != "") {
            final Query<Student> query = Main.datastore.createQuery(Student.class);
            query.field("id").equal(id);
            final UpdateOperations<Student> updateOperations = Main.datastore.createUpdateOperations(Student.class).set("Data", data);
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
*/

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/subject")
    public Response putSubject(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("subjectName") String subjectName, @DefaultValue("") @QueryParam("teacherFirstname") String teacherFirstname, @DefaultValue("") @QueryParam("teacherLastname") String teacherLastname)
    {
        if(!id.isEmpty()) {
            final Query<Subject> query = Main.datastore.createQuery(Subject.class);
            query.field("id").equal(id);
            if(subjectName != "")
            {
                final UpdateOperations<Subject> updateOperations = Main.datastore.createUpdateOperations(Subject.class).set("subjectName", subjectName);
                final UpdateResults results = Main.datastore.update(query, updateOperations);
            }

            if(!teacherFirstname.isEmpty())
            {
                final UpdateOperations<Subject> updateOperationsTeacherFirstname = Main.datastore.createUpdateOperations(Subject.class).set("teacherFirstname", teacherFirstname);
                final UpdateResults resultsTeacherFirstname = Main.datastore.update(query, updateOperationsTeacherFirstname);
            }

            if(!teacherLastname.isEmpty())
            {
                final UpdateOperations<Subject> updateOperationsTeacherLastname = Main.datastore.createUpdateOperations(Subject.class).set("teacherLastname", teacherLastname);
                final UpdateResults resultsTeacherLastname = Main.datastore.update(query, updateOperationsTeacherLastname);
            }

            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
/*
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("aktualizuj/przedmiot/prowadzacy")
    public Response putPrzedmiotProwadzacy(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("idProwadzacego") String idProwadzacego)
    {
        if(id != "") {
            final Query<Subject> query = Main.datastore.createQuery(Subject.class);
            query.field("id").equal(id);
            final UpdateOperations<Subject> updateOperations = Main.datastore.createUpdateOperations(Subject.class).set("idProwadzacego", Long.parseLong(idProwadzacego, 10));
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
*/
    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/grade")
    public Response putSubjectGrade(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("gradeValue") String gradeValue)
    {
        if(!id.isEmpty()) {
            final Query<Grade> query = Main.datastore.createQuery(Grade.class);
            query.field("id").equal(id);
            final UpdateOperations<Grade> updateOperations = Main.datastore.createUpdateOperations(Grade.class).set("gradeValue", gradeValue);
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/subject/student")
    public Response putSubjectStudent(@DefaultValue("") @QueryParam("subjectName") String subjectName, @DefaultValue("") @QueryParam("index") String index)
    {
        if(!subjectName.isEmpty()) {
            final Query<Subject> query = Main.datastore.createQuery(Subject.class);
            query.field("subjectName").equal(subjectName);
            final Query<Student> query2 = Main.datastore.createQuery(Student.class);
            query2.field("index").equal(index);
            final UpdateOperations<Subject> updateOperations = Main.datastore.createUpdateOperations(Subject.class).add("student" ,query2.get());
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}

