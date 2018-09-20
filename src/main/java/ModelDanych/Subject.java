package ModelDanych;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.xml.bind.annotation.*;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@XmlRootElement(name = "subject")
@XmlAccessorType(XmlAccessType.FIELD)
public class Subject implements Serializable
{
    @XmlElement(name = "subjectName")
    private String subjectName;
    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;
    @XmlElement(name = "teacherFirstname")
    private String teacherFirstname;
    @XmlElement(name = "teacherLastname")
    private String teacherLastname;
    @XmlElement(name = "gradesList")
    @Reference
    private List<Grade> gradesList;
    @XmlElement(name = "studentsList")
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
        if(inOceny != null)
        {
            this.gradesList = inOceny;
        }
        else
        {
            this.gradesList = new ArrayList<Grade>();
        }
        this.studentsList = studentsList;
    }

    @XmlTransient
    public String getSubjectName() {

        return subjectName;
    }

    @XmlTransient
    public String getTeacherFirstname() {
        return teacherFirstname;
    }
    @XmlTransient
    public String getTeacherLastname() {
        return teacherLastname;
    }
    @XmlTransient
    public List<Student> getStudentsList() {
        return studentsList;
    }
    @XmlTransient
    public List<Grade> getGradesList() {
        return gradesList;
    }

    public void setTeacherLastname(String teacherLastname) {
        this.teacherLastname = teacherLastname;
    }
    public void setTeacherFirstname(String teacherFirstname) {
        this.teacherFirstname = teacherFirstname;
    }
    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }


    public List<Grade> getGradeByStudent(Long index) {
        List<Grade> lGradesList = new ArrayList<Grade>();
        for(Grade lGrade: gradesList)
        {
            if(lGrade.getReferencedStudent().getIndex() == index) {
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
    public void setStudentsList(List<Student> studentsList) {
        this.studentsList = studentsList;
    }
    public void addStudent(Student inStudent) {
        this.studentsList.add(inStudent);
    }
}
