package HaberSitesiSistemi.Controller.PageController;

import java.util.HashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import HaberSitesiSistemi.Model.Media;
import HaberSitesiSistemi.Service.MediaService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/editor/media")
@RequiredArgsConstructor
public class EditorMediaController {

    private final MediaService mediaService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadStandaloneMedia(@RequestParam("file") MultipartFile file) {
        Media media = mediaService.uploadMedia(file, null);

        Map<String, Object> response = new HashMap<>();
        response.put("mediaId", media.getMediaId());
        response.put("fileUrl", media.getFileUrl());

        return ResponseEntity.ok(response);
    }
}
