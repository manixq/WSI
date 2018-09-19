package ModelDanych;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlRootElement(name = "student")
@XmlAccessorType(XmlAccessType.FIELD)
public class Student implements Serializable
{
    @XmlTransient
    @JsonIgnore
    private ObjectId id;
    @Id
    @XmlID
    @XmlJavaTypeAdapter(LongIdJaxbAdapter.class)
    @XmlElement(type=Long.class, name = "index")
    private Long index;
    @XmlElement(name = "firstName")
    private String firstName;
    @XmlElement(name = "lastName")
    private String lastName;
    @XmlElement(name = "bornDate")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="CET")
    private Date bornDate;

    public Student()
    {

    }

    public Student(Long index, String firstName, String lastName, Date bornDate) {
        this.index = index;
        this.firstName = firstName;
        this.lastName = lastName;
        this.bornDate = bornDate;
    }

    @XmlTransient
    public ObjectId getId() {
        return id;
    }
    @XmlTransient
    public Long getIndex() {
        return index;
    }
    @XmlTransient
    public String getFirstName() {
        return firstName;
    }
    @XmlTransient
    public String getLastName() {
        return lastName;
    }
    @XmlTransient
    public Date getBornDate() {
        return bornDate;
    }

    public void setId(ObjectId id) {
        this.id = id;
    }
    public void setIndex(Long index) {
        this.index = index;
    }
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
    public void setBornDate(Date bornDate) {
        this.bornDate = bornDate;
    }
}
