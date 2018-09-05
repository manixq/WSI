package Main;


import ModelDanych.Ocena;
import ModelDanych.Przedmiot;
import ModelDanych.Student;
import com.mongodb.MongoClient;
import org.glassfish.jersey.server.JSONP;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.query.Query;
import org.mongodb.morphia.query.UpdateOperations;
import org.mongodb.morphia.query.UpdateResults;

import javax.ws.rs.*;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.util.*;

@Path("/")
public class request
{

    //static List<Student> studenty = new ArrayList<Student>();;
    //static List<Ocena> oceny = new ArrayList<Ocena>();;
   // static Map<String, Przedmiot> przedmioty = new HashMap<String, Przedmiot>();;
//users/query?from=100&to=200&orderBy=age&orderBy=name
    public request()
    {



        //studenty.add(new Student(123456789L,"Jan","Sobieski",new Date()));
       // studenty.add(new Student(432156789L,"Jan","Nowak",new Date()));
       // studenty.add(new Student(987656789L,"Zbyszko","Bogdaniec",new Date()));


        //oceny.add(new Ocena(4.5, new Date(), studenty.get(0)));
       // oceny.add(new Ocena(4.0, new Date(), studenty.get(0)));
       // oceny.add(new Ocena(2.0, new Date(), studenty.get(1)));
      //  oceny.add(new Ocena(3.0, new Date(), studenty.get(1)));
       // oceny.add(new Ocena(3.5, new Date(), studenty.get(2)));
       // oceny.add(new Ocena(5.0, new Date(), studenty.get(2)));

      //  przedmioty.put("przyroda", new Przedmiot("Przyroda", 9L, oceny, new Date(), studenty));
       // przedmioty.put("matematyka", new Przedmiot("Matematyka", 9L, oceny, new Date(), studenty));
    }


    //@Path("/{studentId}")
    //@Produces(MediaType.APPLICATION_XML)
    @GET
    @Path("/student")
    @Produces({"application/xml", "application/json"})
    public List<Student> getStudent(@DefaultValue("") @QueryParam("imie") String imie, @DefaultValue("") @QueryParam("nazwisko") String nazwisko)
    {
        final Query<Student> query = Main.datastore.createQuery(Student.class);

        if(imie != "" && nazwisko != "")
        {
           query.and(query.criteria("imie").containsIgnoreCase(imie),
                            query.criteria("nazwisko").containsIgnoreCase(nazwisko));
           return query.asList();

        }
        else if(imie != "") {
            return query.field("imie").equal(imie).asList();
        }

        else if(nazwisko != "")
        {
            return query.field("nazwisko").equal(nazwisko).asList();
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
    public List<Ocena> getStudentFilterByRating(@DefaultValue("") @QueryParam("index") String index, @DefaultValue("") @QueryParam("subject") String subject, @DefaultValue("") @QueryParam("above") String above, @DefaultValue("") @QueryParam("below") String below)
    {
        final Query<Ocena> query = Main.datastore.createQuery(Ocena.class);

        final Query<Student> studentQuery = Main.datastore.createQuery(Student.class);
        if(index != "")
        {
            Student student = studentQuery.field("index").equal(index).get();

            if (subject != "")
            {
                if (above != "")
                {
                    query.and(query.criteria("wartosc").greaterThan(above),
                            query.criteria("przypisanyStudent").equal(student));
                    return query.asList();
                }
                else if (below != "")
                {
                    query.and(query.criteria("wartosc").lessThan(below),
                        query.criteria("przypisanyStudent").equal(student));
                    return query.asList();
                }


                return query.field("przypisanyStudent").equal(student).asList();
            }
        }

        return null;
    }

    @GET
    @Path("/przedmiot")
    @Produces({"application/xml", "application/json"})
    public List<Przedmiot> getPrzedmiotByProwadzacy(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("nazwa") String nazwa)
    {
        final Query<Przedmiot> query = Main.datastore.createQuery(Przedmiot.class);

        if(id != "")
        {
            return query.field("idProwadzacego").equal(id).asList();
        }

        if(nazwa != "")
        {
            return query.field("nazwa").equal(nazwa).asList();
        }


        return query.asList();
    }

    @GET
    @Path("/przedmiot/{przedmiot}/oceny")
    @Produces({"application/xml", "application/json"})
    public List<Ocena> getOcenyZPrzedmiotu(@PathParam("przedmiot") String nazwa)
    {
        final Query<Przedmiot> query = Main.datastore.createQuery(Przedmiot.class);

        Przedmiot query_przedmiot =  query.field("nazwa").equal(nazwa).get();
        return query_przedmiot.getOcena();
    }

    @GET
    @Path("/przedmiot/{przedmiot}/studenci")
    @Produces({"application/xml", "application/json"})
    public List<Student> getPrzedmiotStudenci(@PathParam("przedmiot") String nazwaPrzedmiotu)
    {
        final Query<Przedmiot> query = Main.datastore.createQuery(Przedmiot.class);

        Przedmiot query_przedmiot =  query.field("nazwa").equal(nazwaPrzedmiotu).get();
        return query_przedmiot.getStudent();
    }

    @GET
    @Path("/student/{studentId}/{przedmiot}/Oceny")
    @Produces({"application/xml", "application/json"})
    public List<Ocena> getStudentOcenyZPrzedmiotu(@PathParam("przedmiot") String nazwaPrzedmiotu, @PathParam("studentId") long studentId)
    {
        final Query<Przedmiot> query = Main.datastore.createQuery(Przedmiot.class);
        final Query<Student> query2 = Main.datastore.createQuery(Student.class);

        Przedmiot query_przedmiot =  query.field("nazwa").equal(nazwaPrzedmiotu).get();
        Student query_student = query2.field("index").equal(studentId).get();
        //return Response.status(200).entity("Response studentId").build();
        return query_przedmiot.getOcenaByStudent(query_student.getIndex());
    }

    @GET
    @Path("/student/{studentId}/przedmioty")
    @Produces({"application/xml", "application/json"})
    public List<Przedmiot> getPrzedmiotyByStudent(@PathParam("studentId") long studentId)
    {
        List<Przedmiot> OutPrzedmioty = new ArrayList<Przedmiot>();
        List<Przedmiot> przedmioty = Main.datastore.createQuery(Przedmiot.class).asList();
        final Query<Student> query2 = Main.datastore.createQuery(Student.class);
        Student query_student = query2.field("index").equal(studentId).get();
        for (Przedmiot lprzedmiot :przedmioty)
        {
            if(lprzedmiot.getStudent().contains(query_student))
            {
                OutPrzedmioty.add(lprzedmiot);
            }
        }
        return OutPrzedmioty;
        //return Response.status(200).entity("Response studentId").build();
    }

    @POST
    @Path("/student/dodaj")
    @Consumes({"application/xml", "application/json"})
    //@Produces({"application/xml", "application/json"})
    public Response postStudent() {
        Main.datastore.save(new Student());
        return Response.status(201).entity("dodano studenta").build();
    }


    @POST
    @Path("/Przedmiot/dodaj")
    @Consumes({"application/xml", "application/json"})
    //@Produces({"application/xml", "application/json"})
    public Response postPrzedmiot( @DefaultValue("") @QueryParam("name") String name)
    {
        Main.datastore.save(new Przedmiot(name, 0L, new ArrayList<Ocena>(), new Date(), new ArrayList<Student>()));

        return Response.status(201).entity("Dodano "+name).build();
    }

    @POST
    @Path("/Ocena/dodaj")
    @Consumes({"application/xml", "application/json"})
    //@Produces({"application/xml", "application/json"})
    public Response postOcena() {
        Main.datastore.save(new Ocena(2.0, new Date(), null));

        return Response.status(201).entity("dodano nowa ocene").build();
    }

    @DELETE
    @Path("/usun/student")
    public Response deleteStudenci(@DefaultValue("") @QueryParam("indeks") String indeks) {
        final Query<Student> query = Main.datastore.createQuery(Student.class);

        if(indeks !="")
        {
            query.field("index").equal(Long.parseLong(indeks,10));
            Main.datastore.delete(query);
            return Response.status(200).build();

        }
        return Response.status(404).build();
    }

    @DELETE
    @Path("/usun/przedmiot")
    public Response deletePrzedmiot(@DefaultValue("") @QueryParam("nazwa") String nazwa) {
        final Query<Przedmiot> query = Main.datastore.createQuery(Przedmiot.class);

        if(nazwa != "")
        {
            query.field("nazwa").equal(nazwa);
            Main.datastore.delete(query);
            return Response.status(200).build();

        }
        return Response.status(404).build();
    }

    @DELETE
    @Path("/usun/ocena")
    public Response deleteOcena(@DefaultValue("") @QueryParam("id") String id) {
        final Query<Ocena> query = Main.datastore.createQuery(Ocena.class);

        if(id != "")
        {
            query.field("id").equal(id);
            Main.datastore.delete(query);
            return Response.status(200).build();

        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("aktualizuj/student/indeks")
    public Response putStudentIndex(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("indeks") String indeks)
    {
        if(id != "") {
            final Query<Student> query = Main.datastore.createQuery(Student.class);
            query.field("id").equal(id);
            final UpdateOperations<Student> updateOperations = Main.datastore.createUpdateOperations(Student.class).set("index", Long.parseLong(indeks, 10));
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("aktualizuj/student/imie")
    public Response putStudentImie(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("imie") String imie)
    {
        if(id != "") {
            final Query<Student> query = Main.datastore.createQuery(Student.class);
            query.field("id").equal(id);
            final UpdateOperations<Student> updateOperations = Main.datastore.createUpdateOperations(Student.class).set("imie", imie);
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("aktualizuj/student/nazwisko")
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

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("aktualizuj/przedmiot/nazwa")
    public Response putPrzedmiotNazwa(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("nazwa") String nazwa)
    {
        if(id != "") {
            final Query<Przedmiot> query = Main.datastore.createQuery(Przedmiot.class);
            query.field("id").equal(id);
            final UpdateOperations<Przedmiot> updateOperations = Main.datastore.createUpdateOperations(Przedmiot.class).set("nazwa", nazwa);
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("aktualizuj/przedmiot/prowadzacy")
    public Response putPrzedmiotProwadzacy(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("idProwadzacego") String idProwadzacego)
    {
        if(id != "") {
            final Query<Przedmiot> query = Main.datastore.createQuery(Przedmiot.class);
            query.field("id").equal(id);
            final UpdateOperations<Przedmiot> updateOperations = Main.datastore.createUpdateOperations(Przedmiot.class).set("idProwadzacego", Long.parseLong(idProwadzacego, 10));
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("aktualizuj/ocena")
    public Response putPrzedmiotOcena(@DefaultValue("") @QueryParam("id") String id, @DefaultValue("") @QueryParam("ocena") String ocena)
    {
        if(id != "") {
            final Query<Ocena> query = Main.datastore.createQuery(Ocena.class);
            query.field("id").equal(id);
            final UpdateOperations<Ocena> updateOperations = Main.datastore.createUpdateOperations(Ocena.class).set("ocena", ocena);
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }

    @PUT
    @Consumes({"application/xml", "application/json"})
    @Path("aktualizuj/przedmiot/student")
    public Response putPrzedmiotStudent(@DefaultValue("") @QueryParam("nazwa") String nazwa, @DefaultValue("") @QueryParam("indeks") String indeks)
    {
        if(nazwa != "") {
            final Query<Przedmiot> query = Main.datastore.createQuery(Przedmiot.class);
            query.field("nazwa").equal(nazwa);
            final Query<Student> query2 = Main.datastore.createQuery(Student.class);
            query2.field("index").equal(indeks);
            final UpdateOperations<Przedmiot> updateOperations = Main.datastore.createUpdateOperations(Przedmiot.class).add("student" ,query2.get());
            final UpdateResults results = Main.datastore.update(query, updateOperations);
            return Response.status(200).build();
        }
        return Response.status(404).build();
    }
}

