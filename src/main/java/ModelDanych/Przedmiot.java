package ModelDanych;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexes;
import org.mongodb.morphia.annotations.Reference;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

@Entity
@XmlRootElement(name = "przedmiot")
public class Przedmiot implements Serializable
{
    private String nazwa;
    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;
    private Long idProwadzacego;
    @Reference
    private List<Ocena> oceny;
    @Reference
    private List<Student> student;

    public Przedmiot()
    {
        this.nazwa = "podaj nazwe";
        this.idProwadzacego = 0L;
        this.oceny = new ArrayList<Ocena>();
        this.student = new ArrayList<Student>();
    }

    public Przedmiot(String nazwa, Long idProwadzacego, List<Ocena> inOceny, Date data, List<Student> student) {
        this.nazwa = nazwa;
        this.idProwadzacego = idProwadzacego;
        this.oceny = inOceny;
        this.student = student;
    }

    @XmlElement
    public String getNazwa() {

        return nazwa;
    }

    public void setNazwa(String nazwa) {
        this.nazwa = nazwa;
    }

    @XmlElement
    public Long getIdProwadzacego() {
        return idProwadzacego;
    }

    public void setIdProwadzacego(Long idProwadzacego) {
        this.idProwadzacego = idProwadzacego;
    }

    @XmlElement
    public List<Ocena> getOcena() {
        return oceny;
    }

    public List<Ocena> getOcenaByStudent(Long index) {
        List<Ocena> ocenystudenta = new ArrayList<Ocena>();;
        for(Ocena locena:oceny)
        {
            if(locena.getPrzypisanyStudent().getIndex() == index) {
                ocenystudenta.add(locena);
            }
        }

        return ocenystudenta;
    }

    public void setOcena(List<Ocena> oceny) {
        this.oceny = oceny;
    }
    public void addOcena(Ocena inOcena) {
        this.oceny.add(inOcena);
    }


    @XmlElement
    public List<Student> getStudent() {
        return student;
    }

    public void setStudent(List<Student> student) {
        this.student = student;
    }
    public void addStudent(Student inStudent) {
        this.student.add(inStudent);
    }
}
