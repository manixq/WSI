package ModelDanych;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlRootElement(name = "ocena")
public class Ocena implements Serializable
{
    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;
    private double wartosc;
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="CET")
    private Date dataWystawienia;
    @Reference
    private Student przypisanyStudent;

    public Ocena()
    {

    }

    public Ocena(double wartosc, Date dataWystawienia, Student przypisanyStudent) {
        this.wartosc = wartosc;
        this.dataWystawienia = dataWystawienia;
        this.przypisanyStudent = przypisanyStudent;
    }

    @XmlElement
    public double getWartosc() {
        return wartosc;
    }

    public void setWartosc(float wartosc) {
        this.wartosc = Math.round(wartosc * 2) / 2.0;
    }

    public Date getDataWystawienia() {
        return dataWystawienia;
    }

    public void setDataWystawienia(Date dataWystawienia) {
        this.dataWystawienia = dataWystawienia;
    }

    public Student getPrzypisanyStudent() {
        return przypisanyStudent;
    }

    public void setPrzypisanyStudent(Student przypisanyStudent) {
        this.przypisanyStudent = przypisanyStudent;
    }
}
