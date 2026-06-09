package event.sync.service;

import event.sync.exception.NotFoundException;
import event.sync.model.Speaker;
import event.sync.model.SpeakerLink;
import event.sync.repository.SpeakerLinkRepository;
import event.sync.repository.SpeakerRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpeakerService {
    private final SpeakerRepository speakerRepository;
    private final SpeakerLinkRepository speakerLinkRepository;

    public Speaker findById(UUID id) throws NotFoundException {
        return speakerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Speaker not found"));
    }

    public List<Speaker> getAll() {
        return speakerRepository.findAll();
    }

    @Transactional
    public Speaker create(Speaker speaker) {
        Speaker newSpeaker = speakerRepository.save(speaker);
        speaker.setLinks(createSpeakerLinks(newSpeaker));
        return newSpeaker;
    }

    @Transactional
    public Speaker update(UUID id, Speaker newSpeaker) throws NotFoundException {
        Speaker speaker = findById(id);
        return speakerRepository.save(Speaker.builder()
                .id(speaker.getId())
                .firstName((newSpeaker.getFirstName() == null || newSpeaker.getFirstName().isBlank()) ? speaker.getFirstName() : newSpeaker.getFirstName())
                .lastName((newSpeaker.getLastName() == null || newSpeaker.getLastName().isBlank()) ? speaker.getLastName() : newSpeaker.getLastName())
                .pictureUrl(speaker.getPictureUrl())
                .biography((newSpeaker.getBiography() == null || newSpeaker.getBiography().isBlank()) ? speaker.getBiography() : newSpeaker.getBiography())
                .links((newSpeaker.getLinks() == null) ? createSpeakerLinks(speaker) : createSpeakerLinks(newSpeaker))
                .build());
    }

    @Transactional
    public void delete(UUID id) throws NotFoundException {
        findById(id);
        speakerRepository.deleteById(id);
    }

    private List<SpeakerLink> createSpeakerLinks(Speaker speaker) {
        speakerLinkRepository.deleteBySpeakerId(speaker.getId());
        speaker.getLinks().forEach(speakerLink -> speakerLink.setSpeaker(speaker));
        return speakerLinkRepository.saveAll(speaker.getLinks());
    }
}
