package uz.apelsin.filesystem.service.imp;

import org.apache.commons.io.FilenameUtils;
import org.hashids.Hashids;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import uz.apelsin.filesystem.dto.Result;
import uz.apelsin.filesystem.entity.Attachment;
import uz.apelsin.filesystem.repository.AttachmentRepository;
import uz.apelsin.filesystem.service.AttachmentService;

import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
public class AttachmentServiceImp implements AttachmentService {
    private final AttachmentRepository repository;
    private final Hashids hashids;

    @Value("${upload.path}")
    private String filePath;

    public AttachmentServiceImp(AttachmentRepository repository, Hashids hashids) {
        this.repository = repository;
        this.hashids = hashids;
    }

    @Override
    public Attachment saveFile(MultipartFile multipartFile) {
        try {
            Attachment attachment = new Attachment();
            attachment.setContentType(multipartFile.getContentType());
            attachment.setExtension(FilenameUtils.getExtension(multipartFile.getOriginalFilename()));
            attachment.setName(multipartFile.getOriginalFilename());

            attachment.setSize(multipartFile.getSize());
            Date date = new Date();
            LocalDate localDate = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            Path path = Paths.get(this.filePath+"/"+localDate.getYear()+"/"+localDate.getMonthValue()+
                    "/"+localDate.getDayOfMonth());
            path = check(path);

            attachment.setUploadPath(path.toFile().getAbsolutePath());

            attachment = repository.save(attachment);

            attachment.setHashId(hashids.encode(attachment.getId()));
            File file = new File(path.toFile().getAbsolutePath()+"/"+attachment.getHashId()+"."+attachment.getExtension());


            repository.save(attachment);
            multipartFile.transferTo(file);
            return attachment;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    public Path check(Path path){
        if (!path.toFile().exists()) {
           path = path.normalize();
           path.toFile().mkdirs();
        }
        return path;
    }

    @Override
    public Object findByHashId(String hashId) {
        try {
            return repository.findByHashId(hashId);
        }catch (Exception e){
            return new Result(e.getMessage());
        }
    }

    @Override
    public Result deleteFile(String hashId) {
        try {
            Attachment attachment = repository.findByHashId(hashId);
            Path path =Paths.get(attachment.getUploadPath());
            System.out.println(path.toFile().getAbsolutePath());
            if (path.toFile().exists()) {
                File file = new File(attachment.getUploadPath()
                        + "/" + attachment.getHashId() + "." + attachment.getExtension());
                if (file.delete()) {
                    repository.deleteById(attachment.getId());
                    return new Result(Boolean.TRUE, "File deleted");
                } else {
                    return new Result(false, "Failed");
                }
            }else {
                return new Result(false, "Failed");
            }
        }catch (Exception e){
            System.out.println(e);
            return new Result(false, e.getMessage());
        }
    }

    @Override
    public Result deleteFile(Long id) {
        try {
            Optional<Attachment> attachment = repository.findById(id);
            if (attachment.isPresent()){
                Path path =Paths.get(attachment.get().getUploadPath());
                if (path.toFile().exists()){
                    File file = new File(attachment.get().getUploadPath()
                            + "/" + attachment.get().getHashId() + "." + attachment.get().getExtension());
                    if (file.delete()) {
                        repository.deleteById(attachment.get().getId());
                        return new Result(Boolean.TRUE, "File deleted");
                    }
                }
            }
        }catch (Exception e){
            return new Result(e.getMessage());
        }
        return new Result(false, "Failed");
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ResponseEntity<InputStreamResource> downloadFile(String id, HttpServletResponse response) throws IOException {
        try {
            Attachment attachment = repository.findByHashId(id);
            Path path = Paths.get(attachment.getUploadPath());
            path = check(path);
            File file = new File(attachment.getUploadPath()+"/"+attachment.getHashId()+"."+attachment.getExtension());

            InputStreamResource resource =new InputStreamResource(new FileInputStream(file));

            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename="+attachment.getName());
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");
            return ResponseEntity.ok()
                    .headers(header)
                    .contentLength(file.length())
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .body(resource);
        }catch (Exception e){
            System.out.println(e);
            return ResponseEntity.ok(new InputStreamResource(new FileInputStream(id)));
        }
    }

    @Override
    @Transactional(readOnly = true, propagation = Propagation.SUPPORTS)
    public ResponseEntity<InputStreamResource> getFile(String hashCode, HttpServletResponse response) throws IOException {
        Attachment attachment= repository.findByHashId(hashCode);
        if (attachment!=null) {
            Path path = Paths.get(this.filePath);
//            path = checkPackage(path);
            File file = new File(attachment.getUploadPath() + "/" + attachment.getHashId()+"."+attachment.getExtension());
            InputStreamResource resource = new InputStreamResource(new FileInputStream(file));
            HttpHeaders header = new HttpHeaders();
            header.add(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + attachment.getName());
            header.add("Cache-Control", "no-cache, no-store, must-revalidate");
            header.add("Pragma", "no-cache");
            header.add("Expires", "0");
            return ResponseEntity.ok()
                    .headers(header)
                    .contentLength(file.length())
//                    .contentType(MediaType.APPLICATION_OCTET_STREAM)
                    .contentType(MediaType.parseMediaType(attachment.getContentType()))
                    .body(resource);
        }else
            return null;
    }

    @Override
    public List<Attachment> findByName(String fileName) {
        return repository.findAttachments(fileName);
    }

    @Override
    public List<Attachment> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Attachment> findAttachSizeBetween(Long size, Long size2) {
        return repository.findAttachmentsBySizeBetween((long) (size/(1.2*Math.pow(10,-7))), (long) (size2/(1.2*Math.pow(10,-7))));
    }

    @Override
    public List<Attachment> findAttachmentsDateBetween(Date date, Date date2) {
        return repository.findAttachmentsDateBetween(date, date2);
    }


}
