package uz.apelsin.filesystem.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class AttachmentDto {
    @JsonIgnore
    private Long id;
    private String name;
    @JsonIgnore
    private String hashId;
    private String extension;
    private String contentType;

    private String uploadPath;

    private Long size;
    private Date created_at;
    private Date updated_at;
}
