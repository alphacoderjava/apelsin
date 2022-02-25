package uz.apelsin.filesystem.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import uz.apelsin.filesystem.entity.Attachment;

import java.util.Date;
import java.util.List;

@Repository
public interface AttachmentRepository extends JpaRepository<Attachment,Long> {

    Attachment findByHashId(String hashId);

    @Query("select a from Attachment a where a.name like %:fileName%")
    List<Attachment> findAttachments(@Param("fileName") String fileName);

    @Query("select a from Attachment a where a.created_at between ?1 and ?2")
    List<Attachment> findAttachmentsDateBetween(Date date, Date date2);

    List<Attachment> findAttachmentsBySizeBetween(Long size, Long size2);

}
