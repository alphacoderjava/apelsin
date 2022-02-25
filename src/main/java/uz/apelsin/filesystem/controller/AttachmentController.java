package uz.apelsin.filesystem.controller;

import org.apache.tomcat.util.http.fileupload.impl.SizeLimitExceededException;
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import uz.apelsin.filesystem.dto.Result;
import uz.apelsin.filesystem.entity.Attachment;
import uz.apelsin.filesystem.service.AttachmentService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

@RestController
@RequestMapping("/api/v1/file")
public class AttachmentController {
    private final AttachmentService attachmentService;

    public AttachmentController(AttachmentService attachmentService) {
        this.attachmentService = attachmentService;
    }

    @PostMapping
    public ResponseEntity<?> saveAttachment(@RequestParam(name = "file")MultipartFile multipartFile) throws Exception {
        try {
            return ResponseEntity.ok(attachmentService.saveFile(multipartFile));
        }catch (SizeLimitExceededException | MaxUploadSizeExceededException limitExceededException){
            Result result = new Result(limitExceededException.getMessage());
            return ResponseEntity.ok(result);
        }
    }
    @GetMapping("/preview/{hashId}")
    public ResponseEntity<InputStreamResource> previewFile(@PathVariable String hashId,
                                                        HttpServletResponse httpServletResponse) throws IOException {
        return attachmentService.getFile(hashId, httpServletResponse);
    }
    @DeleteMapping("/{hashId}")
    public ResponseEntity<Result> deleteFile(@PathVariable String hashId){
        return ResponseEntity.ok(attachmentService.deleteFile(hashId));
    }
    @GetMapping("/{hashId}")
    public ResponseEntity<Object> findByHashId(@PathVariable String hashId){
        return ResponseEntity.ok(attachmentService.findByHashId(hashId));
    }


    @GetMapping("/download/{hashId}")
    public ResponseEntity<InputStreamResource> downloadFile(@PathVariable String hashId, HttpServletResponse response) throws IOException {
        return attachmentService.downloadFile(hashId, response);
    }
    @GetMapping
    public ResponseEntity<List<Attachment>> findAttach(@RequestParam(value = "name") String fileName){
        System.out.println(fileName);
        return ResponseEntity.ok(attachmentService.findByName(fileName));
    }

    @GetMapping("/filter")
    public ResponseEntity<List<Attachment>> findAllBetween(@RequestParam(value = "size") Long size,
                                                           @RequestParam(value = "size2") Long size2){
        System.out.println(size+" "+size2);
        return ResponseEntity.ok(attachmentService.findAttachSizeBetween(size, size2));
    }

    @GetMapping("/all")
    public ResponseEntity<List<Attachment>> findAll(){
        return ResponseEntity.ok(attachmentService.findAll());
    }

    @GetMapping("/date")
    public ResponseEntity<List<Attachment>> findAllAttachmentsDateBetween(@RequestParam("date1") String date1,
                                                                          @RequestParam("date2")String date2) throws ParseException {
        String pattern = "yyyy-MM-dd";
        Date date = new SimpleDateFormat(pattern).parse(date1);
        Date date3 = new SimpleDateFormat(pattern).parse(date2);
        return ResponseEntity.ok(attachmentService.findAttachmentsDateBetween(date, date3));
    }

}
