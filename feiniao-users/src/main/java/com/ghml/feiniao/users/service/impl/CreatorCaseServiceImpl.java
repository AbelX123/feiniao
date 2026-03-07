package com.ghml.feiniao.users.service.impl;

import com.ghml.feiniao.common.api.Code;
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
import java.util.Optional;
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
    public CaseVo uploadCase(VideoUploadDto dto) {
        validateUploadDto(dto);

        String creatorId = SecurityUtils.getCurrentUserId();
        String caseId = generateCaseId();

        try {
            int expiryHours = minIOProps.getCaseExpiry();
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime expiryTime = now.plusHours(expiryHours);

            String coverObject = uploadToMinio(creatorId, caseId, Bucket.COVERS.getKey(), dto.getCover(), Bucket.COVERS.getName());
            String videoObject = uploadToMinio(creatorId, caseId, Bucket.VIDEOS.getKey(), dto.getVideo(), Bucket.VIDEOS.getName());

            String coverUrl = MinIOUtils.getObjectUrl(minioClient, Bucket.COVERS.getName(), coverObject, expiryHours);
            String videoUrl = MinIOUtils.getObjectUrl(minioClient, Bucket.VIDEOS.getName(), videoObject, expiryHours);

            CaseEntity entity = buildCaseEntity(caseId, creatorId, dto.getTitle(), coverUrl, videoUrl, expiryTime, now);
            if (caseMapper.insert(entity) <= 0) {
                throw new ServiceException(Code.OPERATION_FAILED);
            }
            return toCaseVo(entity);
        } catch (ServiceException e) {
            throw e;
        } catch (Exception e) {
            log.error("案例上传失败: {}", e.getMessage(), e);
            throw new ServiceException(Code.OSS_ERROR);
        }
    }

    private void validateUploadDto(VideoUploadDto dto) {
        if (dto == null || StringUtils.isBlank(dto.getTitle())
                || dto.getCover() == null || dto.getCover().isEmpty()
                || dto.getVideo() == null || dto.getVideo().isEmpty()) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
    }

    private String generateCaseId() {
        return StringUtils.replace(UUID.randomUUID().toString(), "-", "");
    }

    private CaseEntity buildCaseEntity(String caseId, String creatorId, String title,
                                       String coverUrl, String videoUrl, LocalDateTime expiryTime, LocalDateTime now) {
        CaseEntity entity = new CaseEntity();
        entity.setCaseId(caseId);
        entity.setCreatorId(creatorId);
        entity.setCaseTitle(title);
        entity.setCoverUrl(coverUrl);
        entity.setCoverUrlExpiry(expiryTime);
        entity.setVideoUrl(videoUrl);
        entity.setVideoUrlExpiry(expiryTime);
        entity.setStatus("1");
        entity.setCreateTime(now);
        entity.setUpdateTime(now);
        return entity;
    }

    @Override
    public List<CaseVo> getCases(String creatorId) {
        if (StringUtils.isBlank(creatorId)) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        List<CaseEntity> cases = creatorMapper.getCaseVos(creatorId);
        return cases.stream()
                .map(caseEntity -> toCaseVo(ensureCaseUrls(caseEntity, false)))
                .toList();
    }

    @Override
    public CaseVo getCaseById(String caseId) {
        if (StringUtils.isBlank(caseId)) {
            throw new ServiceException(Code.PARAM_ERROR);
        }
        Optional<CaseEntity> opt = Optional.ofNullable(caseMapper.selectById(caseId));
        if (opt.isEmpty()) {
            throw new ServiceException(Code._404);
        }
        return toCaseVo(ensureCaseUrls(opt.get(), true));
    }

    private String uploadToMinio(String creatorId,
                                 String caseId,
                                 String prefix,
                                 MultipartFile file,
                                 String bucketName) throws Exception {
        // 统一对象路径：{creatorId}/{caseId}/{cover|video}
        String filename = creatorId + "/" + caseId + "/" + prefix;
        return MinIOUtils.uploadFile(minioClient, file, bucketName, filename);
    }

    private CaseVo toCaseVo(CaseEntity entity) {
        return CaseVo.builder()
                .caseId(entity.getCaseId())
                .caseTitle(entity.getCaseTitle())
                .coverUrl(entity.getCoverUrl())
                .videoUrl(entity.getVideoUrl())
                .status(entity.getStatus())
                .createTime(entity.getCreateTime())
                .build();
    }

    private CaseEntity ensureCaseUrls(CaseEntity entity, boolean failOnMissing) {
        if (entity == null || StringUtils.isBlank(entity.getCaseId())) {
            if (failOnMissing) {
                throw new ServiceException(Code.PARAM_ERROR);
            }
            return entity;
        }

        LocalDateTime now = LocalDateTime.now();
        boolean refreshCover = StringUtils.isBlank(entity.getCoverUrl())
                || entity.getCoverUrlExpiry() == null
                || !now.isBefore(entity.getCoverUrlExpiry());
        boolean refreshVideo = StringUtils.isBlank(entity.getVideoUrl())
                || entity.getVideoUrlExpiry() == null
                || !now.isBefore(entity.getVideoUrlExpiry());

        if (!refreshCover && !refreshVideo) {
            return entity;
        }

        try {
            int expiryHours = minIOProps.getCaseExpiry();
            LocalDateTime expiryTime = now.plusHours(expiryHours);
            CaseEntity update = new CaseEntity();
            update.setCaseId(entity.getCaseId());
            update.setUpdateTime(now);

            // 案例对象路径约定：creatorId/caseId/cover、creatorId/caseId/video，直接用实体字段拼接，不解析过期 URL
            String creatorId = entity.getCreatorId();
            String caseId = entity.getCaseId();
            if (StringUtils.isBlank(creatorId) || StringUtils.isBlank(caseId)) {
                if (failOnMissing) {
                    throw new ServiceException(Code.PARAM_ERROR);
                }
                return entity;
            }

            if (refreshCover) {
                String coverObject = creatorId + "/" + caseId + "/" + Bucket.COVERS.getKey();
                if (!MinIOUtils.objectExists(minioClient, Bucket.COVERS.getName(), coverObject)) {
                    if (failOnMissing) {
                        throw new ServiceException(Code.OSS_NOT_EXIST);
                    }
                } else {
                    String coverUrl = MinIOUtils.getObjectUrl(minioClient, Bucket.COVERS.getName(), coverObject, expiryHours);
                    update.setCoverUrl(coverUrl);
                    update.setCoverUrlExpiry(expiryTime);
                    entity.setCoverUrl(coverUrl);
                    entity.setCoverUrlExpiry(expiryTime);
                }
            }

            if (refreshVideo) {
                String videoObject = creatorId + "/" + caseId + "/" + Bucket.VIDEOS.getKey();
                if (!MinIOUtils.objectExists(minioClient, Bucket.VIDEOS.getName(), videoObject)) {
                    if (failOnMissing) {
                        throw new ServiceException(Code.OSS_NOT_EXIST);
                    }
                } else {
                    String videoUrl = MinIOUtils.getObjectUrl(minioClient, Bucket.VIDEOS.getName(), videoObject, expiryHours);
                    update.setVideoUrl(videoUrl);
                    update.setVideoUrlExpiry(expiryTime);
                    entity.setVideoUrl(videoUrl);
                    entity.setVideoUrlExpiry(expiryTime);
                }
            }

            // 至少有一个URL刷新成功时才回写数据库
            if (StringUtils.isNotBlank(update.getCoverUrl()) || StringUtils.isNotBlank(update.getVideoUrl())) {
                if (caseMapper.updateById(update) <= 0 && failOnMissing) {
                    throw new ServiceException(Code.OPERATION_FAILED);
                }
            }
            return entity;
        } catch (ServiceException e) {
            if (failOnMissing) {
                throw e;
            }
            log.warn("刷新案例链接失败, caseId={}, reason={}", entity.getCaseId(), e.getMessage());
            return entity;
        } catch (Exception e) {
            if (failOnMissing) {
                throw new ServiceException(Code.OSS_ERROR);
            }
            log.warn("刷新案例链接异常, caseId={}, reason={}", entity.getCaseId(), e.getMessage());
            return entity;
        }
    }
}
