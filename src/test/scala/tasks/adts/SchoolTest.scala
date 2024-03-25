package tasks.adts

import org.junit.*
import org.junit.Assert.*
import tasks.adts.SchoolModel.*

/* Tests should be clear, but note they are expressed independently of the 
   specific implementation
*/

class Schooltest:

    val school: SchoolModule = BasicSchoolModule

    @Test def testAddTeacher() =
        school.addTeacher("Prova")
        assertEquals("Prova", school.teacherByName("Prova"))
