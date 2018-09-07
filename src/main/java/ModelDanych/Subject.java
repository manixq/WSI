package ModelDanych;

import org.bson.types.ObjectId;
import org.mongodb.morphia.annotations.Entity;
import org.mongodb.morphia.annotations.Id;
import org.mongodb.morphia.annotations.Reference;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@XmlRootElement(name = "subject")
public class Subject implements Serializable
{
    private String subjectName;
    @Id
    @XmlJavaTypeAdapter(ObjectIdJaxbAdapter.class)
    private ObjectId id;
    private String teacherFirstname;
    private String teacherLastname;
    @Reference
    private List<Grade> gradesList;
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

    @XmlElement
    public String getSubjectName() {

        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    @XmlElement
    public String getTeacherFirstname() {
        return teacherFirstname;
    }
    @XmlElement
    public String getTeacherLastname() {
        return teacherLastname;
    }

    public void setTeacherLastname(String teacherLastname) {
        this.teacherLastname = teacherLastname;
    }
    public void setTeacherFirstname(String teacherFirstname) {
        this.teacherFirstname = teacherFirstname;
    }

    @XmlElement
    public List<Grade> getGradesList() {
        return gradesList;
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


    @XmlElement
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
