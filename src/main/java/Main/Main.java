package Main;

import ModelDanych.Grade;
import ModelDanych.Subject;
import ModelDanych.Student;
import com.mongodb.MongoClient;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.text.SimpleDateFormat;
import java.util.*;

public class Main
{
    static Datastore datastore;
    public static void main(String[] strs) {


        CustomHeaders customHeaders = new CustomHeaders();
        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        ResourceConfig config = new ResourceConfig(request.class);
        config.register(new DateParamConverterProvider("dd/MM/yyyy"));
        config.register(customHeaders);
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);


        final Morphia morphia = new Morphia();

        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.map(Student.class);
        morphia.map(Subject.class);
        morphia.map(Grade.class);

        MongoClient mongoClient = new MongoClient( "localhost" , 8004 );

        // create the Datastore connecting to the default port on the local host
        datastore = morphia.createDatastore(mongoClient, "morphia_example");
        datastore.ensureIndexes();

        List<Student> studentsArray = new ArrayList<Student>();
        List<Grade> gradesArray = new ArrayList<Grade>();
        List<Grade> gradesArray2 = new ArrayList<Grade>();
        List<Grade> gradesArray3 = new ArrayList<Grade>();

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


        studentsArray.add(new Student(123456789L,"Jan","Sobieski",currentDate));
        studentsArray.add(new Student(432156789L,"Jan","Nowak",currentDate));
        studentsArray.add(new Student(987656789L,"Zbyszko","Bogdaniec",currentDate));


        gradesArray.add(new Grade(4.5, currentDate, studentsArray.get(0)));
        gradesArray.add(new Grade(3.5, currentDate, studentsArray.get(1)));
        gradesArray2.add(new Grade(3.0, currentDate, studentsArray.get(2)));
        gradesArray2.add(new Grade(2.0, currentDate, studentsArray.get(0)));
        gradesArray3.add(new Grade(5.0, currentDate, studentsArray.get(1)));
        gradesArray3.add(new Grade(4.0, currentDate, studentsArray.get(2)));

        List<Subject> subjectsArray = new ArrayList<Subject>();
        subjectsArray.add(new Subject("Fizyka", "Marek", "Mareczko", gradesArray, studentsArray));
        subjectsArray.add(new Subject("Matematyka", "Krystian", "Karczynski", gradesArray2, studentsArray));
        subjectsArray.add(new Subject("Przyroda", "Mike", "Tyson", gradesArray3, studentsArray));

        datastore.save(studentsArray);
        datastore.save(gradesArray);
        datastore.save(gradesArray2);
        datastore.save(gradesArray3);
        datastore.save(subjectsArray);
        //datastore.save(new Subject("Przyroda", "Marek", "Mareczko", gradesArray, studentsArray));
       // datastore.save(new Subject("Matematyka", "Krystian", "Karczynski", gradesArray, studentsArray));

    }
}
