package event.sync.service;

import event.sync.dto.speaker.SpeakerCreateRequest;
import event.sync.dto.speaker.SpeakerLinkRequest;
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
    public Speaker create(SpeakerCreateRequest createRequest) {
        Speaker newSpeaker = speakerRepository
                .save(SpeakerCreateRequest.toSpeaker(createRequest));
        newSpeaker.setLinks((speakerLinkRepository.saveAll(newSpeaker.getLinks())));
        return newSpeaker;
    }

    @Transactional
    public Speaker update(UUID id, SpeakerCreateRequest speakerRequest) throws NotFoundException {
        Speaker speaker = findById(id);

        speaker.setFirstName((speakerRequest.getFirstName() == null
                || speakerRequest.getFirstName().isBlank())
                ? speaker.getFirstName() : speakerRequest.getFirstName());
        speaker.setLastName((speakerRequest.getLastName() == null
                || speakerRequest.getLastName().isBlank())
                ? speaker.getLastName() : speakerRequest.getLastName());
        speaker.setPictureUrl((speakerRequest.getPictureUrl() == null)
                ? speaker.getPictureUrl() : speakerRequest.getPictureUrl());
        speaker.setBiography((speakerRequest.getBiography() == null
                || speakerRequest.getBiography().isBlank())
                ? speaker.getBiography() : speakerRequest.getBiography());

        Speaker savedSpeaker = speakerRepository.save(speaker);

        if (speakerRequest.getLinks() != null) {
            speakerLinkRepository.deleteAllBySpeakerId(savedSpeaker.getId());
            List<SpeakerLink> links = speakerRequest.getLinks().stream()
                    .map(linkRequest -> SpeakerLinkRequest.toSpeakerLink(savedSpeaker, linkRequest))
                    .toList();
            savedSpeaker.setLinks(speakerLinkRepository.saveAll(links));
        }

        return savedSpeaker;
    }

    @Transactional
    public void delete(UUID id) throws NotFoundException {
        findById(id);
        speakerLinkRepository.deleteAllBySpeakerId(id);
        speakerRepository.deleteById(id);
    }
}
