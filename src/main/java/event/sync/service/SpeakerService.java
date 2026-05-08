package event.sync.service;

import event.sync.model.Speaker;
import event.sync.repository.SpeakerRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@Service
@AllArgsConstructor
public class SpeakerService {
    private final SpeakerRepository speakerRepository;

    public Speaker findById(UUID id) {
        return speakerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Speaker not found"));
    }
}
