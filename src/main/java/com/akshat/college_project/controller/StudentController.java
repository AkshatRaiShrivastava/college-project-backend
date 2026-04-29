package com.akshat.college_project.controller;

import com.akshat.college_project.dto.StudentCreateRequest;
import com.akshat.college_project.dto.StudentUpdateRequest;
import com.akshat.college_project.entity.Student;
import com.akshat.college_project.service.StudentService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import java.util.Map;
import java.util.List;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentService studentService;

    public StudentController(StudentService studentService) {
        this.studentService = studentService;
    }

    @PostMapping
    public ResponseEntity<Student> create(@Valid @RequestBody StudentCreateRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(studentService.create(request));
    }

    @GetMapping("/paginated")
    public Page<Student> getPaginated(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(required = false) String search,
            @RequestParam(required = false) String branch,
            @RequestParam(required = false) String batch,
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "name") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir
    ) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending() : Sort.by(sortBy).descending();
        return studentService.getPaginatedStudents(search, branch, batch, status, PageRequest.of(page, size, sort));
    }

    @GetMapping("/metadata")
    public Map<String, List<String>> getMetadata() {
        return studentService.getMetadata();
    }

    @GetMapping
    public List<Student> getAll() {
        return studentService.getAll();
    }
    @GetMapping("/{studentId}")
    public Student get(@PathVariable String studentId) {
        return studentService.get(studentId);
    }

    @PutMapping("/{studentId}")
    public Student update(@PathVariable String studentId, @RequestBody StudentUpdateRequest request) {
        return studentService.update(studentId, request);
    }

    @DeleteMapping("/{studentId}")
    public ResponseEntity<Void> delete(@PathVariable String studentId) {
        studentService.delete(studentId);
        return ResponseEntity.noContent().build();
    }
}
