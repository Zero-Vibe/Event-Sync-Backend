package event.sync.controller;

import event.sync.dto.auth.RegisterRequest;
import event.sync.dto.user.UserUpdateRequest;
import event.sync.exception.ConflictException;
import event.sync.exception.NotFoundException;
import event.sync.model.User;
import event.sync.service.AuthService;
import event.sync.service.JwtService;
import event.sync.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/users")
@RequiredArgsConstructor
@CrossOrigin(exposedHeaders = "X-Total-Count")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;
    private final AuthService authService;

    @GetMapping
    public ResponseEntity<?> getAll(@RequestParam(required = false, value = "_start", defaultValue = "0") Integer start,
                                    @RequestParam(required = false, value = "_end", defaultValue = "10") Integer end,
                                    @RequestParam(required = false, value = "_sort", defaultValue = "name") String sort,
                                    @RequestParam(required = false, value = "_order", defaultValue = "ASC") String order,
                                    @RequestParam(required = false, value = "filter", defaultValue = "{}") String filterJson,
                                    @RequestParam(required = false, value = "id") List<UUID> ids,
                                    @RequestHeader("Authorization") String token) {
        authService.checkIfAdmin(jwtService.decodeToken(token));

        if (ids != null && !ids.isEmpty()) {
            return ResponseEntity.status(HttpStatus.OK)
                    .header("Content-Type", "application/json")
                    .body(userService.getMany(ids));
        }

        int pageSize = end - start;
        int pageNumber = start / pageSize;

        Page<User> pagedResult = userService.getAll(pageNumber, pageSize, sort, order, filterJson);

        return ResponseEntity.status(HttpStatus.OK)
                .header("X-Total-Count", String.valueOf(pagedResult.getTotalElements()))
                .header("Content-Type", "application/json")
                .body(pagedResult.getContent());
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getById(@PathVariable UUID id,
                                     @RequestHeader("Authorization") String token) throws NotFoundException {
        authService.checkIfAdmin(jwtService.decodeToken(token));

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(userService.findById(id));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid RegisterRequest request,
                                    @RequestHeader("Authorization") String token) throws ConflictException {
            authService.checkIfAdmin(jwtService.decodeToken(token));

        return ResponseEntity.status(HttpStatus.CREATED)
                .header("Content-Type", "application/json")
                .body(userService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> update(@PathVariable UUID id,
                                    @RequestBody UserUpdateRequest request,
                                    @RequestHeader("Authorization") String token) throws  NotFoundException, ConflictException {
        authService.checkIfAdmin(jwtService.decodeToken(token));

        return ResponseEntity.status(HttpStatus.OK)
                .header("Content-Type", "application/json")
                .body(userService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable UUID id,
                                    @RequestHeader("Authorization") String token) throws  NotFoundException {
        authService.checkIfAdmin(jwtService.decodeToken(token));

        userService.delete(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
