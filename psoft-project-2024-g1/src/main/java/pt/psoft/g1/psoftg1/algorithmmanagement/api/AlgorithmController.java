package pt.psoft.g1.psoftg1.algorithmmanagement.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import pt.psoft.g1.psoftg1.algorithmmanagement.service.AlgorithmService;
import pt.psoft.g1.psoftg1.bookmanagement.model.Book;
import java.util.List;

@Tag(name = "Algorithms", description = "Endpoints for managing Algorithms")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/algorithms")
public class AlgorithmController {
    private final AlgorithmService algorithmService;


    @Operation(summary = "Recommends books for a specific reader based on their age and popular genres")
    @GetMapping("/recommendation/2")
    public ResponseEntity<List<Book>> recommendBooks(
            @RequestParam long userId, 
            @RequestParam int x) {
        List<Book> recommendations = algorithmService.recommendBooks(userId, x);
    return ResponseEntity.ok(recommendations);
    }


    @Operation(summary = "Get recommendations for the most lent books")
    @GetMapping("/recommendation/1")
    public ResponseEntity<List<Book>> recommendMostLentBooks(
            @RequestParam(value = "X", defaultValue = "5") int X, // Valor padrão para X
            @RequestParam(value = "Y", defaultValue = "3") int Y  // Valor padrão para Y
    ) {
        List<Book> recommendations = algorithmService.recommendMostLentBooks(X, Y);
        return ResponseEntity.ok(recommendations);
    }


}
