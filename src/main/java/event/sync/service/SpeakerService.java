package event.sync.service;

import event.sync.dto.speaker.SpeakerCreateRequest;
import event.sync.dto.speaker.SpeakerLinkRequest;
import event.sync.exception.NotFoundException;
import event.sync.model.Speaker;
import event.sync.model.SpeakerLink;
import event.sync.repository.SpeakerLinkRepository;
import event.sync.repository.SpeakerRepository;
import event.sync.specification.FilterSpecification;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class SpeakerService {
    private final SpeakerRepository speakerRepository;
    private final SpeakerLinkRepository speakerLinkRepository;
    private final ImageService imageService;

    public Speaker findById(UUID id) throws NotFoundException {
        Speaker speaker = speakerRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Speaker not found"));
        setSpeakerPicture(speaker);
        return speaker;
    }

    public Page<Speaker> findAll(int page, int size, String sortField, String sortOrder, String filterJson) {
        Sort sort = sortOrder.equalsIgnoreCase("DESC") ? Sort.by(sortField).descending() : Sort.by(sortField).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);
        Specification<Speaker> specification = FilterSpecification.parseSpecificationJson(filterJson);
        Page<Speaker> pagedResult = speakerRepository.findAll(specification, pageable);
        pagedResult.getContent().forEach(this::setSpeakerPicture);
        return pagedResult;
    }

    public List<Speaker> getMany(List<UUID> ids) {
        List<Speaker> speakers = speakerRepository.findAllById(ids);
        speakers.forEach(this::setSpeakerPicture);
        return speakers;
    }

    @Transactional
    public Speaker create(SpeakerCreateRequest createRequest) {
        File image = null;
        if (createRequest.getBase64Picture() != null && createRequest.getBase64Picture().contains("base64")) {
            image = imageService.saveImage(createRequest.getBase64Picture());
            createRequest.setBase64Picture(image.getName());
        }

        try {
            Speaker newSpeaker = speakerRepository
                    .save(SpeakerCreateRequest.toSpeaker(createRequest));
            newSpeaker.setLinks((speakerLinkRepository.saveAll(newSpeaker.getLinks())));

            if (image != null) {
                newSpeaker.setBase64Picture(imageService.getBase64Image(image.getName()));
            }

            return newSpeaker;
        } catch (Exception e) {
            if (image != null) imageService.deleteImage(image.getName());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public Speaker update(UUID id, SpeakerCreateRequest speakerRequest) throws NotFoundException {
        Speaker speaker = findById(id);

        File image = null;
        if (speakerRequest.getBase64Picture() != null && speakerRequest.getBase64Picture().contains("base64")) {
            image = imageService.saveImage(speakerRequest.getBase64Picture());
        }

        speaker.setFirstName((speakerRequest.getFirstName() == null
                || speakerRequest.getFirstName().isBlank())
                ? speaker.getFirstName() : speakerRequest.getFirstName());
        speaker.setLastName((speakerRequest.getLastName() == null
                || speakerRequest.getLastName().isBlank())
                ? speaker.getLastName() : speakerRequest.getLastName());
        speaker.setPictureFileName((image == null)
                ? speaker.getPictureFileName() : image.getName());
        speaker.setBiography((speakerRequest.getBiography() == null
                || speakerRequest.getBiography().isBlank())
                ? speaker.getBiography() : speakerRequest.getBiography());

        try {
            Speaker savedSpeaker = speakerRepository.save(speaker);

            if (speakerRequest.getLinks() != null) {
                speakerLinkRepository.deleteAllBySpeakerId(savedSpeaker.getId());
                List<SpeakerLink> links = speakerRequest.getLinks().stream()
                        .map(linkRequest -> SpeakerLinkRequest.toSpeakerLink(savedSpeaker, linkRequest))
                        .toList();
                savedSpeaker.setLinks(speakerLinkRepository.saveAll(links));
            }
            setSpeakerPicture(savedSpeaker);

            return savedSpeaker;
        } catch (Exception e) {
            if (image != null) imageService.deleteImage(image.getName());
            throw new RuntimeException(e);
        }
    }

    @Transactional
    public void delete(UUID id) throws NotFoundException {
        imageService.deleteImage(findById(id).getPictureFileName());
        speakerLinkRepository.deleteAllBySpeakerId(id);
        speakerRepository.deleteById(id);
    }

    private void setSpeakerPicture(Speaker speaker) {
        speaker.setBase64Picture(imageService.getBase64Image(speaker.getPictureFileName()));
    }
}
