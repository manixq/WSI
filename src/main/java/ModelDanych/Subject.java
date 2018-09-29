package ModelDanych;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Indexed;
import org.mongodb.morphia.annotations.Reference;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.beans.Transient;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@XmlRootElement(name = "subject")
@XmlAccessorType(XmlAccessType.FIELD)
public class Subject implements Serializable
{
    @XmlTransient
    @JsonIgnore
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;
    @Id
    @XmlElement(type=String.class)
    private String subjectName;
    private String teacherFirstname;
    private String teacherLastname;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Reference
    private List<Grade> gradesList;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @Reference
    private List<Student> studentsList;

    public Subject()
    {
        this.subjectName = "podaj nazwe";
        this.teacherFirstname = "Jan";
        this.teacherLastname = "Janonowicz";
        this.gradesList = new ArrayList<Grade>();
        this.studentsList = new ArrayList<Student>();
    }

    public Subject(String subjectName, String teacherFirstname, String teacherLastname, List<Grade> inOceny, List<Student> studentsList) {
        this.subjectName = subjectName;
        this.teacherFirstname = teacherFirstname;
        this.teacherLastname = teacherLastname;
        this.gradesList = inOceny;
        this.studentsList = studentsList;
    }

    @XmlTransient
    public String getSubjectName() {

        return subjectName;
    }

    public ObjectId getSubjectId()
    {
        return id;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @XmlTransient
    public String getTeacherFirstname() {
        return teacherFirstname;
    }
    @XmlTransient
    public String getTeacherLastname() {
        return teacherLastname;
    }

    public void setTeacherLastname(String teacherLastname) {
        this.teacherLastname = teacherLastname;
    }
    public void setTeacherFirstname(String teacherFirstname) {
        this.teacherFirstname = teacherFirstname;
    }

    @XmlTransient
    public List<Grade> getGradesList() {
        return gradesList;
    }

    public Student getStudent(Long index) {
        for(Student lStudent: studentsList)
        {
            if(lStudent.getIndex().equals(index)) {
                return lStudent;
            }
        }

        return null;
    }



    public List<Grade> getGradesByStudent(Long index) {
        List<Grade> lGradesList = new ArrayList<Grade>();
        for(Grade lGrade: gradesList)
        {
            if(lGrade.getReferencedStudent().getIndex().equals(index)) {
                lGradesList.add(lGrade);
            }
        }

        return lGradesList;
    }

    public void setGrade(List<Grade> gradesList) {
        this.gradesList = gradesList;
    }
    public void addGrade(Grade inGrade) {
        this.gradesList.add(inGrade);
    }

    @XmlTransient
    public List<Student> getStudentsList() {
        return studentsList;
    }

    public void setStudentsList(List<Student> studentsList) {
        this.studentsList = studentsList;
    }
    public void addStudent(Student inStudent) {
        this.studentsList.add(inStudent);
    }
}
