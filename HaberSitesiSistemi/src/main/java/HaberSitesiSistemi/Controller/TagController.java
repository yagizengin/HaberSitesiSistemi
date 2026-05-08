package HaberSitesiSistemi.Controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import HaberSitesiSistemi.DTO.Request.TagCreateRequest;
import HaberSitesiSistemi.DTO.Response.ApiResponse;
import HaberSitesiSistemi.DTO.Response.TagResponseDTO;
import HaberSitesiSistemi.Mapper.EntityDtoMapper;
import HaberSitesiSistemi.Model.Tag;
import HaberSitesiSistemi.Service.TagService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tags")
@RequiredArgsConstructor
public class TagController {

    private final TagService tagService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<TagResponseDTO>>> getAllTags() {
        List<Tag> tags = tagService.getAllTags();
        List<TagResponseDTO> data = tags.stream()
                .map(EntityDtoMapper::toTagResponseDTO).toList();
        return ResponseEntity.ok(ApiResponse.<List<TagResponseDTO>>builder()
                .success(true).message("Tags retrieved successfully")
                .data(data).timestamp(System.currentTimeMillis()).build());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<TagResponseDTO>> getTagById(@PathVariable Long id) {
        // Tags don't have a dedicated getById in service, use findAll and filter
        Tag tag = tagService.getAllTags().stream()
                .filter(t -> t.getTag_id().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Tag not found"));
        return ResponseEntity.ok(ApiResponse.<TagResponseDTO>builder()
                .success(true).message("Tag retrieved successfully")
                .data(EntityDtoMapper.toTagResponseDTO(tag))
                .timestamp(System.currentTimeMillis()).build());
    }

    @PostMapping
    public ResponseEntity<ApiResponse<TagResponseDTO>> createTag(
            @Valid @RequestBody TagCreateRequest request) {
        Tag tag = tagService.createTag(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(ApiResponse.<TagResponseDTO>builder()
                .success(true).message("Tag created successfully")
                .data(EntityDtoMapper.toTagResponseDTO(tag))
                .timestamp(System.currentTimeMillis()).build());
    }
}
