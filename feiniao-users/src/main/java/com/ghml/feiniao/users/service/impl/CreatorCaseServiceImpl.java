package com.ghml.feiniao.users.service.impl;

import com.ghml.feiniao.common.api.Code;
import com.ghml.feiniao.common.api.R;
import com.ghml.feiniao.common.constants.Bucket;
import com.ghml.feiniao.common.dto.VideoUploadDto;
import com.ghml.feiniao.common.entity.CaseEntity;
import com.ghml.feiniao.common.exception.ServiceException;
import com.ghml.feiniao.common.mapper.CaseMapper;
import com.ghml.feiniao.common.mapper.CreatorMapper;
import com.ghml.feiniao.common.vo.CaseVo;
import com.ghml.feiniao.security.utils.SecurityUtils;
import com.ghml.feiniao.users.config.MinIOProps;
import com.ghml.feiniao.users.service.CreatorCaseService;
import com.ghml.feiniao.users.utils.MinIOUtils;
import io.minio.MinioClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CreatorCaseServiceImpl implements CreatorCaseService {
    private final CaseMapper caseMapper;
    private final CreatorMapper creatorMapper;
    private final MinioClient minioClient;
    private final MinIOProps minIOProps;

    @Override
    public R<String> uploadCase(VideoUploadDto dto) {
        if (dto == null || StringUtils.isBlank(dto.getTitle())
                || dto.getCover() == null || dto.getCover().isEmpty()
                || dto.getVideo() == null || dto.getVideo().isEmpty()) {
            throw new ServiceException(Code.PARAM_ERROR);
        }

        String creatorId = SecurityUtils.getCurrentUserId();
        String caseId = StringUtils.replace(UUID.randomUUID().toString(), "-", "");

        try {
            String coverObject = uploadToMinio(creatorId, caseId, "cover", dto.getCover(), Bucket.COVERS.getName());
            String videoObject = uploadToMinio(creatorId, caseId, "video", dto.getVideo(), Bucket.VIDEOS.getName());

            int expiryHours = minIOProps.getCaseExpiry();
            String coverUrl = MinIOUtils.getObjectUrl(minioClient, Bucket.COVERS.getName(), coverObject, expiryHours);
            String videoUrl = MinIOUtils.getObjectUrl(minioClient, Bucket.VIDEOS.getName(), videoObject, expiryHours);

            LocalDateTime now = LocalDateTime.now();
            CaseEntity entity = new CaseEntity();
            entity.setCaseId(caseId);
            entity.setCreatorId(creatorId);
            entity.setCaseTitle(dto.getTitle());
            entity.setCoverUrl(coverUrl);
            entity.setVideoUrl(videoUrl);
            entity.setStatus("1");
            entity.setCreateTime(now);
            entity.setUpdateTime(now);

            int inserted = caseMapper.insert(entity);
            if (inserted <= 0) {
                throw new ServiceException(Code.OPERATION_FAILED);
            }

            return R.ok(caseId);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("案例上传失败: {}", e.getMessage(), e);
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    @Override
    public R<List<CaseVo>> getCase(String creatorId) {
        if (StringUtils.isBlank(creatorId)) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        List<CaseEntity> cases = creatorMapper.getCaseVos(creatorId);
        List<CaseVo> vos = cases.stream()
                .map(caseEntity -> CaseVo.builder()
                        .caseId(caseEntity.getCaseId())
                        .caseTitle(caseEntity.getCaseTitle())
                        .coverUrl(caseEntity.getCoverUrl())
                        .videoUrl(caseEntity.getVideoUrl())
                        .status(caseEntity.getStatus())
                        .createTime(caseEntity.getCreateTime())
                        .build())
                .toList();
        return R.ok(vos);
    }

    private String uploadToMinio(String creatorId,
                                 String caseId,
                                 String prefix,
                                 MultipartFile file,
                                 String bucketName) throws Exception {
        String ext = StringUtils.substringAfterLast(file.getOriginalFilename(), ".");
        if (StringUtils.isBlank(ext)) {
            ext = "dat";
        }
        String filename = creatorId + "/" + caseId + "/" + prefix + "." + ext;
        return MinIOUtils.uploadFile(minioClient, file, bucketName, filename);
    }
}
