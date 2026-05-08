package event.sync.service;

import event.sync.dto.speaker.SpeakerCreateRequest;
import event.sync.model.Speaker;
import event.sync.repository.SpeakerRepository;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class SpeakerService {
    private final SpeakerRepository speakerRepository;

    public Speaker findById(UUID id) {
        return speakerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Speaker not found"));
    }

    public List<Speaker> getAll() {
        return speakerRepository.getAll();
    }

    public Speaker create(SpeakerCreateRequest speaker) {
        return speakerRepository.create(speaker);
    }

    public Speaker update(UUID id, SpeakerCreateRequest speaker) {
        return speakerRepository.update(id, speaker);
    }

    public void delete(UUID id) {
        speakerRepository.delete(id);
    }
}
