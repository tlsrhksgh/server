package com.example.server.file;

import com.example.server.member.repository.Member;
import com.example.server.member.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.transaction.Transactional;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Transactional
public class FileService {
    private final FileApplication fileApplication;
    private final AwsS3Uploader s3Uploader;
    private final MemberRepository memberRepository;

    @Value("${cloud.aws.s3.url}")
    private String imageUrl;

    public String memberImgFileUpload(MultipartFile file) {
        if(Objects.isNull(file) || !StringUtils.hasText(file.getOriginalFilename())) {
            return null;
        }

        fileApplication.fileMimeTypeCheck(file);

        String translatedImgName = s3Uploader.uploadFile(file);

        return imageUrl + translatedImgName;
    }

    public String updateMemberImgFile(MultipartFile file, Member member) {
        String currentImg = member.getImg();
        s3Uploader.delete(fileApplication.splitImageUrl(currentImg));

        if(Objects.isNull(file) || file.isEmpty()) {
            memberRepository.updateMemberImg(null, member.getAccount());
            return null;
        }

        fileApplication.fileMimeTypeCheck(file);
        String translatedFileUrl = this.memberImgFileUpload(file);

        memberRepository.updateMemberImg(translatedFileUrl, member.getAccount());
        return translatedFileUrl;
    }

    public void deleteMemberImgFile(String imageUrl) {
        s3Uploader.delete(fileApplication.splitImageUrl(imageUrl));
    }
}
