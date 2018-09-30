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
import javax.ws.rs.core.Context;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriBuilder;
import javax.ws.rs.core.UriInfo;
import java.text.SimpleDateFormat;
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
    @Path("/students/FilterByDate")
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
    @Path("/students/FilterByRating")
    @Produces({"application/xml", "application/json"})
    public List<Grade> getStudentFilterByRating(@DefaultValue("0") @QueryParam("index") long index, @DefaultValue("") @QueryParam("subject") String subject, @DefaultValue("0") @QueryParam("above") double above, @DefaultValue("0") @QueryParam("below") double below)
    {
        final Query<Grade> gradeQuery = Main.datastore.createQuery(Grade.class);

        final Query<Student> studentQuery = Main.datastore.createQuery(Student.class);
        if(index != 0)
        {
            Student student = studentQuery.field("index").equal(index).get();


            if (above != 0)
            {
                gradeQuery.and(gradeQuery.criteria("gradeValue").greaterThan(above),
                        gradeQuery.criteria("referencedStudent").equal(student));
                return gradeQuery.asList();
            }
            else if (below != 0)
            {
                gradeQuery.and(gradeQuery.criteria("gradeValue").lessThan(below),
                    gradeQuery.criteria("referencedStudent").equal(student));
                return gradeQuery.asList();
            }


            return gradeQuery.field("referencedStudent").equal(student).asList();

        }

        return null;
    }

    @GET
    @Path("/students/{studentId}")
    @Produces({"application/xml", "application/json"})
    public Student getStudent(@PathParam("subject") String subjectName, @PathParam("studentId") long studentId)
    {
        final Query<Student> query2 = Main.datastore.createQuery(Student.class);

        Student query_student = query2.field("index").equal(studentId).get();
        //return Response.status(200).entity("Response studentId").build();
        return query_student;
    }

    @GET
    @Path("/students/{studentId}/grades")
    @Produces({"application/xml", "application/json"})
    public List<Grade> getStudentGrades(@PathParam("subject") String subjectName, @PathParam("studentId") long studentId)
    {
        final Query<Subject> query1 = Main.datastore.createQuery(Subject.class);

        List<Grade> lGrades = new ArrayList<>();
        List<Subject> lSubjects = query1.asList();
        for(Subject s : lSubjects)
        {   for(Grade g : s.getGradesByStudent(studentId))
                lGrades.add(g);
        }
        //return Response.status(200).entity("Response studentId").build();
        return lGrades;
    }

    @GET
    @Path("/students/{studentId}/{subject}/grades")
    @Produces({"application/xml", "application/json"})
    public List<Grade> getStudentGradesFromSubject(@PathParam("subject") String subjectName, @PathParam("studentId") long studentId)
    {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);
        final Query<Student> query2 = Main.datastore.createQuery(Student.class);

        Subject query_subject =  query.field("subjectName").equal(subjectName).get();
        Student query_student = query2.field("index").equal(studentId).get();
        //return Response.status(200).entity("Response studentId").build();
        return query_subject.getGradesByStudent(query_student.getIndex());
    }

    @GET
    @Path("/students/{studentId}/subjects")
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
    @Path("/subjects/{subject}/grades")
    @Produces({"application/xml", "application/json"})
    public List<Grade> getSubjectGrades(@PathParam("subject") String subjectName)
    {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);

        Subject query_subject =  query.field("subjectName").equal(subjectName).get();
        return query_subject.getGradesList();
    }

    @GET
    @Path("/subjects/{subject}/students")
    @Produces({"application/xml", "application/json"})
    public List<Student> getSubjectStudents(@PathParam("subject") String subjectName)
    {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);

        Subject query_subject =  query.field("subjectName").equal(subjectName).get();
        return query_subject.getStudentsList();
    }

    @GET
    @Path("/subjects/{subject}/students/{student_index}")
    @Produces({"application/xml", "application/json"})
    public Student getSubjectStudent(@PathParam("subject") String subjectName, @PathParam("student_index") long student_index)
    {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);

        Subject query_subject =  query.field("subjectName").equal(subjectName).get();
        return query_subject.getStudent(student_index);
    }

    @GET
    @Path("/subjects/{subject}/students/{student_index}/grades")
    @Produces({"application/xml", "application/json"})
    public List<Grade> getSubjectStudentGrades(@PathParam("subject") String subjectName, @PathParam("student_index") long student_index)
    {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);

        Subject query_subject =  query.field("subjectName").equal(subjectName).get();
        return query_subject.getGradesByStudent(student_index);
    }



    @GET
    @Path("/grades")
    @Produces({"application/xml", "application/json"})
    public List<Grade> getGrades()
    {
        final Query<Grade> query = Main.datastore.createQuery(Grade.class);

        return query.asList();
    }


    @POST
    @Path("/students/add")
    @Consumes({"application/xml", "application/json"})
    //@Produces({"application/xml", "application/json"})
    public Response postStudent(@Context UriInfo uriInfo, @DefaultValue("") @QueryParam("subjectName") String subjectName, @DefaultValue("0") @QueryParam("index") long index, @DefaultValue("") @QueryParam("firstName") String firstName, @DefaultValue("") @QueryParam("lastName") String lastName, @DefaultValue("") @QueryParam("bornDate") String bornDate) {
        if(index != 0)
        {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            try
            {
                Student lStudent = new Student(index, firstName, lastName,formatter.parse(bornDate));
                Main.datastore.save(lStudent);
                if(!subjectName.equals(""))
                {
                    final Query<Subject> query = Main.datastore.createQuery(Subject.class);

                    Subject query_subject =  query.field("subjectName").equal(subjectName).get();
                    query_subject.addStudent(lStudent);
                    Main.datastore.save(query_subject);
                }
            }
            catch (Exception ex)
            {
                throw new WebApplicationException("Bad formatted date", 400);
            }

            UriBuilder builder = uriInfo.getBaseUriBuilder();
            builder.path("students/" + Long.toString(index));



            return Response.created(builder.build()).status(201).build();

        }
        //if no index
        return Response.status(400).entity("no index").build();
    }


    @POST
    @Path("/subjects/add")
    @Consumes({"application/xml", "application/json"})
    //@Produces({"application/xml", "application/json"})
    public Response postSubject(@Context UriInfo uriInfo, @DefaultValue("") @QueryParam("subjectName") String subjectName, @DefaultValue("") @QueryParam("teacherFirstName") String teacherFirstName, @DefaultValue("") @QueryParam("teacherLastName") String teacherLastName)
    {
        if(!subjectName.equals(""))
        {
            Main.datastore.save(new Subject(subjectName, teacherFirstName, teacherLastName, new ArrayList<Grade>(), new ArrayList<Student>()));

            UriBuilder builder = uriInfo.getBaseUriBuilder();
            builder.path("subjects");
            return Response.created(builder.build()).status(201).build();

        }
        //if no index
        return Response.status(400).entity("no subject name").build();
    }

    @POST
    @Path("/grade/add")
    @Consumes({"application/xml", "application/json"})
    //@Produces({"application/xml", "application/json"})
    public Response postGrade(@Context UriInfo uriInfo, @DefaultValue("") @QueryParam("subjectName") String subjectName, @DefaultValue("0") @QueryParam("gradeValue") double gradeValue, @QueryParam("index") long index) {
        if(gradeValue != 0)
        {
            Date currentDate = new Date();
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try
            {
                currentDate = formatter.parse(formatter.format(currentDate));
            }
            catch (Exception ex)
            {
                throw new WebApplicationException("Bad formatted date", 400);
            }


            final Query<Student> query2 = Main.datastore.createQuery(Student.class);
            Student query_student = query2.field("index").equal(index).get();
            Grade lGrade = new Grade(gradeValue, currentDate, query_student);
            Main.datastore.save(lGrade);

            if(!subjectName.equals("")) {
                final Query<Subject> query = Main.datastore.createQuery(Subject.class);

                Subject query_subject = query.field("subjectName").equal(subjectName).get();
                query_subject.addGrade(lGrade);
                Main.datastore.save(query_subject);
            }

            UriBuilder builder = uriInfo.getBaseUriBuilder();
            builder.path("students/" + Long.toString(index) + "/grades");

            return Response.created(builder.build()).status(201).build();

        }
        //if no index
        return Response.status(400).entity("no subject name").build();
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
    public Response deleteSubject(@DefaultValue("") @QueryParam("subjectName") String subjectName) {
        final Query<Subject> query = Main.datastore.createQuery(Subject.class);

        if(!subjectName.isEmpty())
        {
            query.field("subjectName").equal(subjectName);
            Main.datastore.delete(query);
            return Response.status(200).build();

        }
        return Response.status(404).build();
    }

    @DELETE
    @Path("/delete/grade")
    public Response deleteGrade(@DefaultValue("") @QueryParam("subjectName") String subjectName, @DefaultValue("0") @QueryParam("gradeValue") double gradeValue, @DefaultValue("") @QueryParam("gradeDate") String gradeDate, @QueryParam("index") long index) {
        final Query<Grade> query = Main.datastore.createQuery(Grade.class);
        final Query<Subject> queryRef;

        if(!gradeDate.isEmpty())
        {

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            try
            {
                query.field("gradeDate").equal(formatter.parse(gradeDate));
            }
            catch (Exception ex)
            {
                throw new WebApplicationException("Bad formatted date", 400);
            }
            Grade deleteGrade = null;
            query.field("gradeValue").equal(gradeValue);
            for(Grade lGrade : query.asList())
            {
                if(lGrade.getReferencedStudent().getIndex().equals(index)) {
                    deleteGrade = lGrade;
                }
            }
            if(!deleteGrade.equals(null)) {
                queryRef = Main.datastore.createQuery(Subject.class).field("gradesList").hasThisElement(deleteGrade);
                for (Subject s : queryRef.asList()) {
                    for (int i = s.getGradesList().size() - 1; i >= 0; i--) {
                        if (s.getGradesList().get(i).getId().equals(query.get().getId())) {
                            s.getGradesList().remove(i);
                        }
                    }
                }

                Main.datastore.save(queryRef.asList());
                Main.datastore.delete(deleteGrade);
                return Response.status(200).build();
            }

        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/student")
    public Response putStudent(@DefaultValue("0") @QueryParam("index") long index, @DefaultValue("0") @QueryParam("newindex") long new_index, @DefaultValue("") @QueryParam("firstName") String firstName, @DefaultValue("") @QueryParam("lastName") String lastName, @DefaultValue("") @QueryParam("bornDate") String bornDate)
    {
        if(index != 0) {
            final Query<Student> query = Main.datastore.createQuery(Student.class);
            query.field("index").equal(index);


            if(!firstName.isEmpty())
            {
                final UpdateOperations<Student> updateOperationsFirstname = Main.datastore.createUpdateOperations(Student.class).set("firstName", firstName);
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

            if(new_index != 0)
            {
                Student lStudent = query.get();
                lStudent.setIndex(new_index);
                Main.datastore.delete(query);
                Main.datastore.save(lStudent);
            }
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/subject")
    public Response putSubject(@DefaultValue("") @QueryParam("subjectName") String subjectName, @DefaultValue("") @QueryParam("teacherFirstname") String teacherFirstname, @DefaultValue("") @QueryParam("teacherLastname") String teacherLastname)
    {
        if(!subjectName.isEmpty()) {
            final Query<Subject> query = Main.datastore.createQuery(Subject.class);
            query.field("subjectName").equal(subjectName);


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

            if(!subjectName.isEmpty())
            {
                Subject lSubject = query.get();
                lSubject.setSubjectName(subjectName);
                Main.datastore.delete(query);
                Main.datastore.save(lSubject);
            }

            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/grade")
    public Response putSubjectGrade(@DefaultValue("") @QueryParam("gradeDate") String gradeDate, @DefaultValue("0") @QueryParam("gradeValue") double gradeValue, @DefaultValue("0") @QueryParam("gradeNewValue") double gradeNewValue)
    {
        if(!gradeDate.isEmpty()) {
            if(gradeNewValue > 0) {
                final Query<Grade> query = Main.datastore.createQuery(Grade.class);


                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                try
                {
                    query.field("gradeDate").equal(formatter.parse(gradeDate));
                }
                catch (Exception ex)
                {
                    throw new WebApplicationException("Bad formatted date", 400);
                }




                List<Grade> lGradeList = query.field("gradeValue").equal(gradeValue).asList();
                if(lGradeList.size() > 0) {
                    final UpdateOperations<Grade> updateOperations = Main.datastore.createUpdateOperations(Grade.class).set("gradeValue", gradeNewValue);
                    final UpdateResults results = Main.datastore.update(lGradeList.get(0), updateOperations);
                    return Response.status(200).build();
                }
            }
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("update/subject/student")
    public Response putSubjectStudent(@DefaultValue("") @QueryParam("subjectName") String subjectName, @DefaultValue("0") @QueryParam("index") long index)
    {
        if(!subjectName.isEmpty()) {
            final Query<Subject> query = Main.datastore.createQuery(Subject.class);
            Subject lSubject = query.field("subjectName").equal(subjectName).get();
            final Query<Student> query2 = Main.datastore.createQuery(Student.class);
            Student lStudent = query2.field("index").equal(index).get();

            lSubject.addStudent(lStudent);

            Main.datastore.save(lSubject);

            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}

