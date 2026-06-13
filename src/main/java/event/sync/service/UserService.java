package event.sync.service;

import event.sync.dto.auth.RegisterRequest;
import event.sync.dto.user.UserUpdateRequest;
import event.sync.exception.ConflictException;
import event.sync.exception.NotFoundException;
import event.sync.model.User;
import event.sync.repository.UserRepository;
import event.sync.specification.FilterSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public Page<User> getAll(int page, int size, String sortField, String sortOrder, String filterJson) {
        Sort sort = sortOrder.equalsIgnoreCase("DESC") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<User> specification = FilterSpecification.parseSpecificationJson(filterJson);
        return userRepository.findAll(specification, pageable);
    }

    public List<User> getMany(List<UUID> ids) {
        return userRepository.findAllById(ids);
    }

    public User findById(UUID id) throws NotFoundException {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("User not found: " + id));
    }

    @Transactional
    public User create(RegisterRequest request) throws ConflictException {
        String passwordHash = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());

        try {
            return userRepository.save(User.builder()
                    .isAdmin(false)
                    .email(request.getEmail())
                    .passwordHash(passwordHash)
                    .name(request.getName())
                    .joinDate(LocalDateTime.now())
                    .build());
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("A user with that email or name already exists");
        }
    }

    @Transactional
    public User update(UUID id, UserUpdateRequest request) throws NotFoundException, ConflictException {
        User user = findById(id);

        String passwordHash = request.getPassword() != null
                ? BCrypt.hashpw(request.getPassword(), BCrypt.gensalt())
                : user.getPasswordHash();

        try {
            return userRepository.save(User.builder()
                    .id(id)
                    .isAdmin(request.getIsAdmin() != null ? request.getIsAdmin() : user.isAdmin())
                    .email(request.getEmail() != null ? request.getEmail() : user.getEmail())
                    .passwordHash(passwordHash)
                    .name(request.getName() != null ? request.getName() : user.getName())
                    .joinDate(user.getJoinDate())
                    .build());
        } catch (DataIntegrityViolationException e) {
            throw new ConflictException("A user with that email or name already exists");
        }
    }

    @Transactional
    public void delete(UUID id) throws NotFoundException {
        findById(id);
        userRepository.deleteById(id);
    }
}
