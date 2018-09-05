package Main;

import ModelDanych.Ocena;
import ModelDanych.Przedmiot;
import ModelDanych.Student;
import com.mongodb.MongoClient;
import org.glassfish.grizzly.http.server.HttpServer;
import org.glassfish.jersey.grizzly2.httpserver.GrizzlyHttpServerFactory;
import org.glassfish.jersey.server.ResourceConfig;
import org.mongodb.morphia.Datastore;
import org.mongodb.morphia.Morphia;

import javax.ws.rs.core.UriBuilder;
import java.net.URI;
import java.util.*;

public class Main
{
    static Datastore datastore;
    public static void main(String[] strs) {



        URI baseUri = UriBuilder.fromUri("http://localhost/").port(8080).build();
        ResourceConfig config = new ResourceConfig(request.class);
        config.register(new DateParamConverterProvider("yyyy-MM-dd"));
        config.register(new CustomHeaders());
        HttpServer server = GrizzlyHttpServerFactory.createHttpServer(baseUri, config);


        final Morphia morphia = new Morphia();

        // tell Morphia where to find your classes
        // can be called multiple times with different packages or classes
        morphia.map(Student.class);
        morphia.map(Przedmiot.class);
        morphia.map(Ocena.class);

        MongoClient mongoClient = new MongoClient( "localhost" , 8004 );

        // create the Datastore connecting to the default port on the local host
        datastore = morphia.createDatastore(mongoClient, "morphia_example");
        datastore.ensureIndexes();

        List<Student> studenty = new ArrayList<Student>();;
        List<Ocena> oceny = new ArrayList<Ocena>();;
        Map<String, Przedmiot> przedmioty = new HashMap<String, Przedmiot>();;

        studenty.add(new Student(123456789L,"Jan","Sobieski",new Date()));
        studenty.add(new Student(432156789L,"Jan","Nowak",new Date()));
        studenty.add(new Student(987656789L,"Zbyszko","Bogdaniec",new Date()));


        oceny.add(new Ocena(4.5, new Date(), studenty.get(0)));
        oceny.add(new Ocena(4.0, new Date(), studenty.get(0)));
        oceny.add(new Ocena(2.0, new Date(), studenty.get(1)));
        oceny.add(new Ocena(3.0, new Date(), studenty.get(1)));
        oceny.add(new Ocena(3.5, new Date(), studenty.get(2)));
        oceny.add(new Ocena(5.0, new Date(), studenty.get(2)));


        datastore.save(studenty);
        datastore.save(oceny);
        datastore.save(new Przedmiot("Przyroda", 9L, oceny, new Date(), studenty));
        datastore.save(new Przedmiot("Matematyka", 9L, oceny, new Date(), studenty));

    }
}
