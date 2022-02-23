package uz.pdp.appjparelationships.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.*;
import uz.pdp.appjparelationships.entity.Address;
import uz.pdp.appjparelationships.entity.Group;
import uz.pdp.appjparelationships.entity.Student;
import uz.pdp.appjparelationships.entity.Subject;
import uz.pdp.appjparelationships.payload.StudentDto;
import uz.pdp.appjparelationships.repository.AddressRepository;
import uz.pdp.appjparelationships.repository.GroupRepository;
import uz.pdp.appjparelationships.repository.StudentRepository;
import uz.pdp.appjparelationships.repository.SubjectRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("/student")
public class StudentController {
    @Autowired
    StudentRepository studentRepository;

    @Autowired
    GroupRepository groupRepository;

    @Autowired
    SubjectRepository subjectRepository;

    @Autowired
    AddressRepository addressRepository;

    //1. VAZIRLIK
    @GetMapping("/forMinistry")
    public Page<Student> getStudentListForMinistry(@RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAll(pageable);
    }

    //2. UNIVERSITY
    @GetMapping("/forUniversity/{universityId}")
    public Page<Student> getStudentListForUniversity(@PathVariable Integer universityId,
                                                     @RequestParam int page) {

        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findAllByGroup_Faculty_UniversityId(universityId, pageable);
    }

    //3. FACULTY DEKANAT

    @GetMapping("/forFaculty/{facultyId}")
    public Page<Student> getStudent(@PathVariable Integer facultyId,
                                    @RequestParam int page) {
        Pageable pageable = PageRequest.of(page, 10);
        return studentRepository.findByGroup_FacultyId(facultyId, pageable);
    }

    //4. GROUP OWNER  // yani id orqali

    @GetMapping("/getOne/{id}")
    public Student getStudent(@PathVariable Integer id) {
        final Optional<Student> optionalStudent = studentRepository.findById(id);
        return optionalStudent.orElseGet(Student::new);
    }


    @PostMapping("/saveOrEdit")
    public String saveOrEdit(@RequestBody StudentDto studentDto) {
        Student student = new Student();
        Optional<Group> optionalGroup = groupRepository.findById(studentDto.getGroupId());
        Optional<Address> optionalAddress = addressRepository.findById(studentDto.getAddressId());
        if (!optionalGroup.isPresent()) {
            return "gruppa topilmadi";
        }
        if (!optionalAddress.isPresent()) {
            return "Adress topilmadi";
        }

        student.setId(studentDto.getId());
        student.setLastName(studentDto.getLastName());
        student.setFirstName(studentDto.getFirstName());
        student.setAddress(optionalAddress.get());
        student.setGroup(optionalGroup.get());

        List<Subject> subjects = new ArrayList<>();
        try {

            for (Subject subject : studentDto.getSubjects()) {
                Subject one = subjectRepository.getOne(subject.getId());
                subjects.add(one);
            }
        } catch (Exception e) {
            return "subject error";
        }
        student.setSubjects(subjects);
        return studentDto.getId()!=null?"qoshildi":"o'zgartirildi";
    }


    @DeleteMapping("/delete/{id}")
    public String delete(@PathVariable Integer id){
        try {
            studentRepository.deleteById(id);
        }catch (Exception e){
            return "O'chirilmadi";
        }
        return "O'chirildi";

    }

}
