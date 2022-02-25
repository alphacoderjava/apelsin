package uz.apelsin.filesystem.service;

import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ResponseEntity;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartFile;
import uz.apelsin.filesystem.dto.Result;
import uz.apelsin.filesystem.entity.Attachment;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Date;
import java.util.List;

public interface AttachmentService {
    Attachment saveFile(MultipartFile multipartFile) throws Exception;
    Object findByHashId(String hashId);
    Result deleteFile(String hashId);
    Result deleteFile(Long id);
    ResponseEntity<InputStreamResource> downloadFile(String id, HttpServletResponse response) throws IOException;
    ResponseEntity<InputStreamResource> getFile(String hashCode, HttpServletResponse response) throws IOException;
    List<Attachment> findByName(String fileName);
    List<Attachment> findAll();
    List<Attachment> findAttachSizeBetween(Long size, Long size2);
    List<Attachment> findAttachmentsDateBetween(Date date, Date date2);

}
