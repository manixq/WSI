package ModelDanych;

import com.fasterxml.jackson.annotation.JsonFormat;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.Date;

@Entity
@XmlRootElement(name = "grade")
@XmlAccessorType(XmlAccessType.FIELD)
public class Grade implements Serializable
{
    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;
    @XmlElement(name = "gradeValue")
    private double gradeValue;
    @XmlElement(name = "gradeDate")
    @JsonFormat(shape=JsonFormat.Shape.STRING, pattern="yyyy-MM-dd", timezone="CET")
    private Date gradeDate;
    @Reference
    @XmlElement(name = "referencedStudent")
    private Student referencedStudent;

    public Grade()
    {

    }

    public Grade(double gradeValue, Date gradeDate, Student referencedStudent) {
        this.gradeValue = gradeValue;
        this.gradeDate = gradeDate;
        this.referencedStudent = referencedStudent;
    }

    @XmlTransient
    public double getGradeValue() {
        return gradeValue;
    }
    @XmlTransient
    public Date getGradeDate() {
        return gradeDate;
    }
    @XmlTransient
    public Student getReferencedStudent() {
        return referencedStudent;
    }

    public void setGradeDate(Date gradeDate) {
        this.gradeDate = gradeDate;
    }
    public void setGradeValue(float gradeValue) {
        this.gradeValue = Math.round(gradeValue * 2) / 2.0;
    }
    public void setReferencedStudent(Student referencedStudent) {
        this.referencedStudent = referencedStudent;
    }
}
